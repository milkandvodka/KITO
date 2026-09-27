package com.kito.sap

import com.fleeksoft.ksoup.Ksoup
import com.kito.core.platform.AppConfig
import com.kito.core.platform.ErrorSanitizer
import com.kito.core.platform.createHttpEngine
import com.kito.sap.sensitive.SapError
import com.kito.sap.sensitive.SapPortalHeaders
import com.kito.sap.sensitive.SapPortalHtmlParser
import com.kito.sap.sensitive.SapPortalParams
import com.kito.sap.sensitive.SapPortalTokenExtractor
import com.kito.sap.sensitive.SapPortalUrls
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.decodeURLPart
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/** Logs only in debug builds; the message lambda is not evaluated in release. */
private inline fun debugLog(block: () -> String) {
    if (AppConfig.isDebug) println(block())
}

class SapPortalClient {

    private val cookieStorage = ClearableCookiesStorage()

    private val client: HttpClient by lazy {
        HttpClient(createHttpEngine()) {
            install(HttpCookies) {
                storage = cookieStorage
            }
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }
        }
    }

    /**
     * The (year, term) the live Web Dynpro instance was last driven to.
     *
     * The Web Dynpro app is server-side state keyed to the session, so
     * re-entering it on the same cookies resumes the results screen the previous
     * fetch submitted — and a ComboBox_Select sent to that screen is silently
     * ignored. Tracking the last selection lets us drop the cookies only when the
     * session actually changes, keeping the reuse that avoids the HTTP 500s.
     */
    private var lastSelection: Pair<String, String>? = null

    suspend fun fetchAttendance(
        username: String,
        password: String,
        academicYear: String = "",
        termCode: String = ""
    ): AttendanceResult = withContext(Dispatchers.Default) {
        val totalStart = TimeSource.Monotonic.markNow()
        debugLog { "🚀 Starting fetchAttendance..." }
        // Year key e.g. "2025" (=2025-2026); term key "010"=Autumn, "020"=Spring.
        val academicYearValue = academicYear.ifEmpty { "2025" }
        val termCodeValue = termCode.ifEmpty { "020" }

        // Note: we intentionally do NOT clear cookies or log out between fetches
        // of the same year/term. The session is reused (see Step 1/2): clearing/
        // logging out mid-session makes the portal return HTTP 500 or an
        // authenticated home page with no j_salt, which caused the intermittent
        // failures. Switching year/term is the exception: it needs a brand-new
        // Web Dynpro instance, so drop the cookies and log in again.
        val last = lastSelection
        if (last != null && last != (academicYearValue to termCodeValue)) {
            debugLog { "DEBUG: year/term changed ${last} -> $academicYearValue/$termCodeValue — dropping the session" }
            cookieStorage.clear()
            lastSelection = null
        }

        try {
            // ─── Step 1: Load login page ──────────────────────────────────────────────
            val step1Start = TimeSource.Monotonic.markNow()
            val loginPageResponse = client.get(SapPortalUrls.getLoginPageUrl()) {
                setupDefaultHeaders()
            }

            if (!loginPageResponse.status.isSuccess()) {
                val error = SapError.LoginPageFailed(loginPageResponse.status.value)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val loginPageHtml = loginPageResponse.bodyAsText()
            val salt = SapPortalHtmlParser.extractSaltFromLoginPage(loginPageHtml)
            debugLog { "⏱️ Step 1 (Load Login) took: ${step1Start.elapsedNow()}" }

            // No salt + no login form => we're already authenticated (the portal
            // returns its home page). Reuse the live session and skip login.
            // Re-logging in / clearing cookies mid-session is what triggered the
            // intermittent HTTP 500 and missing-j_salt failures.
            val alreadyAuthenticated = salt.isNullOrEmpty() && !loginPageHtml.contains("j_username")

            if (salt.isNullOrEmpty() && !alreadyAuthenticated) {
                val error = SapError.SaltExtractionFailed()
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            // ─── Step 2: Submit login (only when not already authenticated) ───────────
            if (alreadyAuthenticated) {
                debugLog { "DEBUG: already authenticated — reusing session, skipping login" }
            } else {
                val step2Start = TimeSource.Monotonic.markNow()
                val loginParams = SapPortalParams.getLoginParams(salt!!, username, password)
                val loginResponse = client.submitForm(
                    url = SapPortalUrls.getLoginPageUrl(),
                    formParameters = Parameters.build {
                        loginParams.forEach { (key, value) -> append(key, value) }
                    }
                ) {
                    setupDefaultHeaders(site = "same-origin")
                    headers.set("content-type", "application/x-www-form-urlencoded")
                }

                if (!loginResponse.status.isSuccess()) {
                    val error = SapError.LoginFailed(
                        status = loginResponse.status.value,
                        preview = loginResponse.bodyAsText().take(200)
                    )
                    ErrorSanitizer.log(error)
                    return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
                }

                secureWipe(username.toCharArray())
                secureWipe(password.toCharArray())

                val loginResultHtml = loginResponse.bodyAsText()
                debugLog { "⏱️ Step 2 (Submit Login) took: ${step2Start.elapsedNow()}" }
                debugLog { "DEBUG: loginResponse status = ${loginResponse.status}" }
                debugLog { "DEBUG: loginResponse url = ${loginResponse.call.request.url}" }

                if (loginResultHtml.contains("authentication failed", true) || loginResultHtml.contains("login failed", true)) {
                    val error = SapError.InvalidCredentials()
                    ErrorSanitizer.log(error)
                    return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
                }
            }

            // ─── Step 3: Navigation events ────────────────────────────────────────────
            val step3Start = TimeSource.Monotonic.markNow()

            val navEvent1Response = client.submitForm(
                url = SapPortalUrls.getNavEvent1Url(),
                formParameters = Parameters.build {
                    SapPortalParams.getNavEvent1Params().forEach { (key, value) -> append(key, value) }
                }
            ) {
                setupDefaultHeaders(dest = "iframe", site = "same-origin")
                headers.set("content-type", "application/x-www-form-urlencoded")
                headers.set("referer", SapPortalUrls.getLoginPageUrl())
            }

            if (!navEvent1Response.status.isSuccess()) {
                val error = SapError.NavigationFailed(step = 1, status = navEvent1Response.status.value)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val navEvent2Response = client.submitForm(
                url = SapPortalUrls.getNavEvent2Url(),
                formParameters = Parameters.build {
                    SapPortalParams.getNavEvent2Params().forEach { (key, value) -> append(key, value) }
                }
            ) {
                setupDefaultHeaders(dest = "iframe", site = "same-origin")
                headers.set("content-type", "application/x-www-form-urlencoded")
                headers.set("referer", SapPortalUrls.getLoginPageUrl())
            }

            if (!navEvent2Response.status.isSuccess()) {
                val error = SapError.NavigationFailed(step = 2, status = navEvent2Response.status.value)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val navEvent3Response = client.submitForm(
                url = SapPortalUrls.getNavEvent3Url(),
                formParameters = Parameters.build {
                    SapPortalParams.getNavEvent3Params().forEach { (key, value) -> append(key, value) }
                }
            ) {
                setupDefaultHeaders(dest = "iframe", site = "same-origin")
                headers.set("content-type", "application/x-www-form-urlencoded")
                headers.set("referer", SapPortalUrls.getLoginPageUrl())
            }

            if (!navEvent3Response.status.isSuccess()) {
                val error = SapError.NavigationFailed(step = 3, status = navEvent3Response.status.value)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val nav3Html = navEvent3Response.bodyAsText()
            debugLog { "⏱️ Step 3 (Nav Events) took: ${step3Start.elapsedNow()}" }

            // ─── Step 4: Extract Web Dynpro form action ───────────────────────────────
            val wdFormAction = SapPortalHtmlParser.extractWebDynproFormAction(nav3Html)

            if (wdFormAction.isNullOrEmpty()) {
                val error = SapError.WebDynproFormActionFailed()
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val sapExtSid = SapPortalTokenExtractor.extractSapExtSid(wdFormAction)

            if (sapExtSid.isNullOrEmpty()) {
                val error = SapError.ExtSidExtractionFailed(wdFormAction)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            // ─── Step 5: Submit Web Dynpro form ───────────────────────────────────────
            val step5Start = TimeSource.Monotonic.markNow()
            val formData = SapPortalHtmlParser.extractFormFields(nav3Html)

            val wdInitialResponse = client.submitForm(
                url = wdFormAction,
                formParameters = Parameters.build {
                    formData.forEach { (key, value) -> append(key, value) }
                }
            ) {
                setupDefaultHeaders(dest = "iframe", site = "cross-site")
                SapPortalHeaders.webDynproHeaders.forEach { (key, value) ->
                    headers.set(key, value)
                }
            }

            if (!wdInitialResponse.status.isSuccess()) {
                val error = SapError.WebDynproSubmitFailed(wdInitialResponse.status.value)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val wdResponseHtml = wdInitialResponse.bodyAsText()
            val responseUrl = wdInitialResponse.call.request.url.toString()
            debugLog { "⏱️ Step 5 (WebDynpro Init) took: ${step5Start.elapsedNow()}" }

            // ─── Step 6: Extract session tokens ───────────────────────────────────────
            val wdContextId = SapPortalTokenExtractor.extractContextId(wdResponseHtml, responseUrl)
            val secureId = SapPortalTokenExtractor.extractSecureId(wdResponseHtml)

            val sapClientForm = Ksoup.parse(wdResponseHtml).selectFirst(SapPortalHeaders.sapClientFormSelector)
            debugLog {
                if (sapClientForm != null) "DEBUG: sapClientForm HTML: ${sapClientForm.outerHtml()}"
                else "DEBUG: sapClientForm is NULL!"
            }
            val formAction = sapClientForm?.attr("action")

            val (extSidFromForm, contextIdFromForm) = SapPortalTokenExtractor.extractTokensFromFormAction(formAction)

            val finalExtSid = extSidFromForm ?: sapExtSid
            var finalContextId = contextIdFromForm ?: wdContextId

            if (finalContextId == wdContextId) {
                val urlContextIdMatch = Regex("""[?&]sap-contextid=([^&]+)""", RegexOption.IGNORE_CASE).find(responseUrl)
                if (urlContextIdMatch != null && urlContextIdMatch.groupValues.size > 1) {
                    val urlContextId = urlContextIdMatch.groupValues[1].decodeURLPart()
                    if (urlContextId.isNotEmpty()) {
                        finalContextId = urlContextId
                    }
                }
            }

            debugLog { "DEBUG: responseUrl = $responseUrl" }
            debugLog { "DEBUG: wdFormAction = $wdFormAction" }
            debugLog { "DEBUG: sapExtSid = $sapExtSid" }
            debugLog { "DEBUG: wdContextId = $wdContextId" }
            debugLog { "DEBUG: secureId = $secureId" }
            debugLog { "DEBUG: formAction = $formAction" }
            debugLog { "DEBUG: extSidFromForm = $extSidFromForm" }
            debugLog { "DEBUG: contextIdFromForm = $contextIdFromForm" }
            debugLog { "DEBUG: finalExtSid = $finalExtSid" }
            debugLog { "DEBUG: finalContextId = $finalContextId" }
            debugLog { "DEBUG: wdResponseHtml snippet = ${wdResponseHtml.take(500)}" }

            if (finalExtSid.isEmpty() || finalContextId.isEmpty() || secureId.isEmpty()) {
                val missing = buildList {
                    if (finalExtSid.isEmpty()) add("ext-sid")
                    if (finalContextId.isEmpty()) add("context-id")
                    if (secureId.isEmpty()) add("secure-id")
                }
                val error = SapError.TokenExtractionFailed(missing)
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            // ─── Step 7: Initial render (load-on-demand bootstrap) ────────────────────
            val step7Start = TimeSource.Monotonic.markNow()
            val initialUrl = if (!formAction.isNullOrEmpty()) {
                val resolvedAction = formAction
                    .replace("&amp;", "&")
                    .replace("&#x3b;", ";")
                    .replace("&#x3f;", "?")
                    .replace("&#x26;", "&")
                    .replace("&#x3d;", "=")
                if (resolvedAction.startsWith("http")) resolvedAction else "${SapPortalUrls.WD_HOST}$resolvedAction"
            } else {
                SapPortalUrls.getInitialAttendanceUrl(finalExtSid, finalContextId)
            }
            val placeholderId = Regex("""id="(_loadingPlaceholder_[^"]*)"""")
                .find(wdResponseHtml)?.groupValues?.get(1) ?: "_loadingPlaceholder_"
            val initialBody = SapPortalParams
                .wdBody(secureId, SapPortalParams.getInitQueue(finalExtSid, placeholderId))
                .toMutableMap()

            debugLog { "DEBUG: raw formAction = $formAction" }
            debugLog { "DEBUG: initialUrl = $initialUrl" }
            debugLog { "DEBUG: initialBody keys = ${initialBody.keys}" }

            val initialResponse = client.submitForm(
                url = initialUrl,
                formParameters = Parameters.build {
                    initialBody.forEach { (key, value) -> append(key, value) }
                }
            ) {
                setupDefaultHeaders(
                    accept = "*/*",
                    dest = "empty",
                    mode = "cors",
                    site = "same-origin",
                    user = ""
                )
                SapPortalHeaders.getInitialHeaders().forEach { (key, value) ->
                    headers.set(key, value)
                }
                headers.set("referer", responseUrl)
                // XHR requests don't send upgrade-insecure-requests
                headers.remove("upgrade-insecure-requests")
            }

            debugLog { "DEBUG: Step 7 response status = ${initialResponse.status}" }

            if (!initialResponse.status.isSuccess()) {
                val fullBody = initialResponse.bodyAsText()
                debugLog { "DEBUG: Step 7 full response body:\n$fullBody" }
                val error = SapError.AttendanceInitFailed(
                    status = initialResponse.status.value,
                    preview = fullBody.take(1000)
                )
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val initialResponseBody = initialResponse.bodyAsText()
            debugLog { "⏱️ Step 7 (Init Render) took: ${step7Start.elapsedNow()}" }

            // ─── Step 8: Detect element ids + resolve year/term keys ──────────────────
            var htmlToParse = wdResponseHtml
            val cdataMatch = SapPortalHeaders.contentUpdateRegex.find(initialResponseBody)
            if (cdataMatch != null) {
                htmlToParse = cdataMatch.groupValues[1]
            } else if (initialResponseBody.length > wdResponseHtml.length) {
                htmlToParse = initialResponseBody
            }

            val (yearId, termId, buttonId) = SapPortalHtmlParser.detectComboboxIds(htmlToParse)
            debugLog { "DEBUG: ids year=$yearId term=$termId btn=$buttonId | select year=$academicYearValue term=$termCodeValue" }

            // ─── Step 9: Fetch attendance with selection ──────────────────────────────
            val step9Start = TimeSource.Monotonic.markNow()
            val attendanceBody = SapPortalParams
                .wdBody(
                    secureId,
                    SapPortalParams.getAttendanceQueue(
                        finalExtSid, academicYearValue, termCodeValue, yearId, termId, buttonId
                    )
                )
                .toMutableMap()

            val attendanceResponse = client.submitForm(
                url = initialUrl,
                formParameters = Parameters.build {
                    attendanceBody.forEach { (key, value) -> append(key, value) }
                }
            ) {
                setupDefaultHeaders(
                    accept = "*/*",
                    dest = "empty",
                    mode = "cors",
                    site = "same-origin",
                    user = ""
                )
                SapPortalHeaders.getInitialHeaders().forEach { (key, value) ->
                    headers.set(key, value)
                }
                headers.set("referer", responseUrl)
                // XHR requests don't send upgrade-insecure-requests
                headers.remove("upgrade-insecure-requests")
            }

            if (!attendanceResponse.status.isSuccess()) {
                val error = SapError.AttendanceFetchFailed(
                    status = attendanceResponse.status.value,
                    preview = attendanceResponse.bodyAsText().take(200)
                )
                ErrorSanitizer.log(error)
                return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
            }

            val attendanceHtml = attendanceResponse.bodyAsText()
            debugLog { "⏱️ Step 9 (Fetch Attendance) took: ${step9Start.elapsedNow()}" }

            // ─── Step 10: Confirm the selection landed, then parse ────────────────────
            // A Web Dynpro app that ignored ComboBox_Select still answers 200 with a
            // valid table — just for the session it was already showing. Storing that
            // under the requested year/term makes every session look identical, so
            // check the echoed year before trusting the rows.
            val step10Start = TimeSource.Monotonic.markNow()
            val renderedHtml = SapPortalHeaders.contentUpdateRegex.find(attendanceHtml)
                ?.groupValues?.get(1) ?: attendanceHtml
            val shownYear = Ksoup.parse(renderedHtml).getElementById(yearId)
                ?.attr("value")?.trim()?.takeIf { it.isNotEmpty() }
            when {
                // `contains` rather than `==`: the input renders "2026" on some
                // themes and "2026/2027" on others.
                shownYear != null && !shownYear.contains(academicYearValue) -> {
                    val error = SapError.AttendanceFetchFailed(
                        status = attendanceResponse.status.value,
                        preview = "ComboBox_Select was ignored: asked for year=$academicYearValue " +
                            "term=$termCodeValue via ids year=$yearId term=$termId btn=$buttonId, " +
                            "but the year input came back as \"$shownYear\""
                    )
                    ErrorSanitizer.log(error)
                    // Don't reuse a session that is stuck on another year/term.
                    cookieStorage.clear()
                    lastSelection = null
                    return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
                }
                shownYear != null -> debugLog { "DEBUG: portal confirmed year=$shownYear" }
                else -> debugLog { "DEBUG: year input absent from the delta — selection not verifiable" }
            }

            val parsedAttendance = AttendanceData(SapPortalHtmlParser.parseAttendanceData(attendanceHtml))
            lastSelection = academicYearValue to termCodeValue
            debugLog { "⏱️ Step 10 (Parse Data) took: ${step10Start.elapsedNow()}" }

            debugLog { "✅ Total fetch time: ${totalStart.elapsedNow()}" }
            return@withContext AttendanceResult.Success(parsedAttendance)

        } catch (e: Exception) {
            val error = when {
                e.message?.contains("Unable to resolve host") == true ->
                    SapError.NetworkError(e.message ?: "Unable to resolve host")
                else ->
                    SapError.UnknownError(e.message ?: e::class.simpleName ?: "unknown")
            }
            ErrorSanitizer.log(error)
            // Always print full stack trace in debug for crash diagnostics
            if (AppConfig.isDebug) e.printStackTrace()
            return@withContext AttendanceResult.Error(ErrorSanitizer.sanitize(error))
        }
    }

    private fun secureWipe(charArray: CharArray) {
        for (i in charArray.indices) {
            charArray[i] = '0'
        }
    }

    /**
     * Explicit logout. Not called automatically — the session is kept alive so
     * subsequent fetches can reuse it. Call this only when the user explicitly
     * signs out; it ends the server session and clears local cookies.
     */
    suspend fun logout() {
        performLogout(client)
        cookieStorage.clear()
        lastSelection = null
    }

    private suspend fun performLogout(client: HttpClient) {
        try {
            withTimeout(2000.milliseconds) {
                try {
                    client.submitForm(
                        url = SapPortalUrls.getLogoutUrl(),
                        formParameters = Parameters.build {
                            append("logout_submit", "true")
                        }
                    ) {
                        setupDefaultHeaders(site = "same-origin")
                        headers.set("content-type", "application/x-www-form-urlencoded")
                        headers.set("origin", AppConfig.portalBase)
                        headers.set("referer", SapPortalUrls.getLoginPageUrl())
                    }
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Timeout during logout — safe to swallow
        }
    }
}

// ─── Result types ─────────────────────────────────────────────────────────────

sealed class AttendanceResult {
    data class Success(val data: AttendanceData) : AttendanceResult()
    data class Error(val message: String) : AttendanceResult()
}

data class AttendanceData(
    val subjects: List<SubjectAttendance> = emptyList()
)

data class SubjectAttendance(
    val subjectCode: String,
    val subjectName: String,
    val attendedClasses: Int,
    val totalClasses: Int,
    val percentage: Double,
    val facultyName: String = ""
)

// ─── Cookie storage ───────────────────────────────────────────────────────────

/**
 * Simple in-memory cookie storage with clear support.
 */
class ClearableCookiesStorage : io.ktor.client.plugins.cookies.CookiesStorage {
    private val cookies = mutableListOf<io.ktor.http.Cookie>()
    private val mutex = kotlinx.coroutines.sync.Mutex()

    override suspend fun get(requestUrl: io.ktor.http.Url): List<io.ktor.http.Cookie> =
        mutex.withLock { cookies.filter { it.matches(requestUrl) } }

    override suspend fun addCookie(requestUrl: io.ktor.http.Url, cookie: io.ktor.http.Cookie): Unit =
        mutex.withLock {
            cookies.removeAll { it.name == cookie.name && it.matches(requestUrl) }
            cookies.add(cookie)
        }

    override fun close() {}

    suspend fun clear() = mutex.withLock { cookies.clear() }

    private fun io.ktor.http.Cookie.matches(requestUrl: io.ktor.http.Url): Boolean {
        val domain = this.domain ?: return true
        if (!requestUrl.host.endsWith(domain) && requestUrl.host != domain) return false
        val path = this.path ?: "/"
        if (!requestUrl.encodedPath.startsWith(path)) return false
        if (this.secure && requestUrl.protocol.name != "https") return false
        return true
    }
}

private fun HttpRequestBuilder.setupDefaultHeaders(
    accept: String = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
    dest: String = "document",
    mode: String = "navigate",
    site: String = "same-origin",
    user: String = "?1"
) {
    headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
    headers.set("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"")
    headers.set("sec-ch-ua-mobile", "?0")
    headers.set("sec-ch-ua-platform", "\"Windows\"")
    headers.set("upgrade-insecure-requests", "1")
    headers.set("Accept-Language", "en-US,en;q=0.9")
    headers.set("Cache-Control", "no-cache")
    headers.set("Pragma", "no-cache")
    headers.set("DNT", "1")
    headers.set("Accept", accept)
    headers.set("sec-fetch-dest", dest)
    headers.set("sec-fetch-mode", mode)
    headers.set("sec-fetch-site", site)
    if (user.isNotEmpty()) {
        headers.set("sec-fetch-user", user)
    } else {
        headers.remove("sec-fetch-user")
    }
}
