package com.kito.sap

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.kito.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kito.security.StringEncryption
import com.kito.sap.sensitive.*

class SapPortalClient {

    private val client = OkHttpClient.Builder()
        .cookieJar(PersistentCookieJar())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "document")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .header("upgrade-insecure-requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .header("DNT", "1")

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    suspend fun fetchAttendance(username: String, password: String, academicYear: String = "", termCode: String = ""): AttendanceResult = withContext(Dispatchers.IO) {
        val cookieJar = client.cookieJar as PersistentCookieJar
        cookieJar.clear()

        return@withContext try {
            // STEP 1: GET Login Page
            println("🌐 Logging in to Portal...")
            val loginPageResponse = client.newCall(
                Request.Builder()
                    .url(SapPortalUrls.getLoginPageUrl())
                    .build()
            ).execute()

            if (!loginPageResponse.isSuccessful) {
                return@withContext AttendanceResult.Error("Failed to load login page. Status: ${loginPageResponse.code}")
            }

            val loginPageHtml = loginPageResponse.body?.string()
            loginPageResponse.close()
            val salt = SapPortalHtmlParser.extractSaltFromLoginPage(loginPageHtml)

            if (salt.isNullOrEmpty()) {
                return@withContext AttendanceResult.Error("Could not extract j_salt from login page")
            }

            // STEP 2: POST Credentials (WIPE IMMEDIATELY AFTER)
            val loginParams = SapPortalParams.getLoginParams(salt!!, username, password)

            val loginResponse = client.newCall(
                Request.Builder()
                    .url(SapPortalUrls.getLoginPageUrl())
                    .post(
                        createFormBody(loginParams)
                    )
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build()
            ).execute()

            if (!loginResponse.isSuccessful) {
                val responseContent = loginResponse.body?.string()
                return@withContext AttendanceResult.Error("Login failed with status: ${loginResponse.code}. Response: ${responseContent?.take(200)}")
            }

            println("✅ Logged in successfully.")

            // WIPE CREDENTIALS IMMEDIATELY AFTER USE
            secureWipe(username.toCharArray())
            secureWipe(password.toCharArray())

            // Check if login was successful
            val loginResultHtml = loginResponse.body?.string()
            loginResponse.close()
            if (loginResultHtml?.contains("authentication failed", true) == true) {
                return@withContext AttendanceResult.Error("Invalid credentials")
            }

            // STEPS 3-5: Navigation Events (Prime session for sap-ext-sid)
            println("🔄 Performing Navigation Event 1 (Priming Session)...")

            // Navigation Event 1
            val navEvent1Response = client.newCall(
                Request.Builder()
                    .url(SapPortalUrls.getNavEvent1Url())
                    .post(createFormBody(SapPortalParams.getNavEvent1Params()))
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("referer", SapPortalUrls.getLoginPageUrl())
                    .addHeader("sec-fetch-dest", "iframe")
                    .addHeader("sec-fetch-mode", "navigate")
                    .addHeader("sec-fetch-site", "same-origin")
                    .build()
            ).execute()

            if (!navEvent1Response.isSuccessful) {
                return@withContext AttendanceResult.Error("Navigation Event 1 failed with status: ${navEvent1Response.code}")
            }

            println("✅ Navigation Event 1 successful.")

            // Navigation Event 2
            println("🔄 Performing Navigation Event 2 (Priming Session)...")
            val navEvent2Response = client.newCall(
                Request.Builder()
                    .url(SapPortalUrls.getNavEvent2Url())
                    .post(createFormBody(SapPortalParams.getNavEvent2Params()))
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("referer", SapPortalUrls.getLoginPageUrl())
                    .addHeader("sec-fetch-dest", "iframe")
                    .addHeader("sec-fetch-mode", "navigate")
                    .addHeader("sec-fetch-site", "same-origin")
                    .build()
            ).execute()

            if (!navEvent2Response.isSuccessful) {
                return@withContext AttendanceResult.Error("Navigation Event 2 failed with status: ${navEvent2Response.code}")
            }

            println("✅ Navigation Event 2 successful.")

            // Navigation Event 3 (Final Prime - Gets sap-ext-sid)
            println("🔄 Performing Navigation Event 3 (Final Prime - Gets sap-ext-sid)...")
            val navEvent3Response = client.newCall(
                Request.Builder()
                    .url(SapPortalUrls.getNavEvent3Url())
                    .post(createFormBody(SapPortalParams.getNavEvent3Params()))
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("referer", SapPortalUrls.getLoginPageUrl())
                    .addHeader("sec-fetch-dest", "iframe")
                    .addHeader("sec-fetch-mode", "navigate")
                    .addHeader("sec-fetch-site", "same-origin")
                    .build()
            ).execute()

            if (!navEvent3Response.isSuccessful) {
                return@withContext AttendanceResult.Error("Navigation Event 3 failed with status: ${navEvent3Response.code}")
            }

            // === AFTER NAVIGATION EVENT 3 ===
            println("✅ Navigation Event 3 successful.")

            // Get the HTML from Navigation Event 3 response and look for the Web Dynpro form in the navigation response
            val nav3Html = navEvent3Response.body?.string()
            navEvent3Response.close()

            val wdFormAction = SapPortalHtmlParser.extractWebDynproFormAction(nav3Html)

            if (wdFormAction.isNullOrEmpty()) {
                return@withContext AttendanceResult.Error("Could not find Web Dynpro form (named 'isolatedWorkAreaForm') in navigation response. Response snippet: ${(nav3Html ?: "").take(500)}")
            }

            // Extract sap-ext-sid from the form action URL
            val sapExtSid = SapPortalTokenExtractor.extractSapExtSid(wdFormAction)

            if (sapExtSid.isNullOrEmpty()) {
                return@withContext AttendanceResult.Error("Could not extract sap-ext-sid from form action. Action: $wdFormAction")
            }

            println("🔑 Extracted sap-ext-sid: $sapExtSid")

            // Extract all form fields as per fetch_attendance.js
            val formData = SapPortalHtmlParser.extractFormFields(nav3Html ?: "")

            // Submit Web Dynpro form (as per fetch_attendance.js)
            println("🌐 Submitting Web Dynpro form to initialize session...")
            val wdInitialResponse = client.newCall(
                Request.Builder()
                    .url(wdFormAction)  // Use the actual form action URL from the isolatedWorkAreaForm
                    .post(createFormBody(formData))
                    .apply {
                        SapPortalHeaders.webDynproHeaders.forEach { (key, value) ->
                            addHeader(key, value)
                        }
                    }
                    .build()
            ).execute()

            if (!wdInitialResponse.isSuccessful) {
                return@withContext AttendanceResult.Error("Web Dynpro form submission failed with status: ${wdInitialResponse.code}")
            }

            println("✅ Web Dynpro form submitted. Status: ${wdInitialResponse.code}")

            // Extract contextId and secureId from the response
            val wdResponseHtml = wdInitialResponse.body?.string()
            wdInitialResponse.close()

            val wdContextId = SapPortalTokenExtractor.extractContextId(wdResponseHtml, wdInitialResponse)
            println("🔑 Found sap-contextid: $wdContextId")

            val secureId = SapPortalTokenExtractor.extractSecureId(wdResponseHtml)
            println("🔑 Found secure ID: $secureId")

            // Extract additional tokens from the form action if present (as per fetch_attendance.js)
            val sapClientForm = Jsoup.parse(wdResponseHtml ?: "").selectFirst(SapPortalHeaders.sapClientFormSelector)
            val formAction = sapClientForm?.attr("action")

            val (extSidFromForm, contextIdFromForm) = SapPortalTokenExtractor.extractTokensFromFormAction(formAction)

            // Determine the final values to use - prioritize tokens found in the WD form action if present
            val finalExtSid = extSidFromForm ?: sapExtSid
            var finalContextId = contextIdFromForm ?: wdContextId

            // ALSO extract from the response URL if the form action extraction failed
            if (finalContextId == wdContextId) { // If we didn't get it from form action
                val responseUrl = wdInitialResponse.request.url.toString()
                val urlContextIdMatch = Regex("""[?&]sap-contextid=([^&]+)""", RegexOption.IGNORE_CASE).find(responseUrl)
                if (urlContextIdMatch != null && urlContextIdMatch.groupValues.size > 1) {
                    val urlContextId = java.net.URLDecoder.decode(urlContextIdMatch.groupValues[1], "UTF-8")
                    if (urlContextId.isNotEmpty()) {
                        finalContextId = urlContextId
                        println("🔑 Found sap-contextid in response URL: $urlContextId")
                    }
                }
            }

            println("--- Final Token Extraction ---")
            println("sap-ext-sid (from Nav step or WD form): $finalExtSid")
            println("sap-contextid (from WD form): $finalContextId")
            println("sap-wd-secure-id (from WD input): $secureId")
            println("")

            // Validate we have all required tokens
            if (finalExtSid.isEmpty() || finalContextId.isEmpty() || secureId.isEmpty()) {
                return@withContext AttendanceResult.Error("Missing required tokens: ext-sid=${finalExtSid.isNotEmpty()}, context-id=${finalContextId.isNotEmpty()}, secure-id=${secureId.isNotEmpty()}")
            }

            println("✅ All tokens found: ext-sid=$finalExtSid, ctx-id=$finalContextId, secure-id=$secureId")

            // FIRST: Send initial request to load default attendance data (as per fetch_attendance.js)
            println("📡 Sending initial attendance load request (loading default data)...")
            val initialUrl = SapPortalUrls.getInitialAttendanceUrl(finalExtSid, finalContextId)
            val initialBody = SapPortalParams.getInitialAttendanceBody(secureId, finalContextId)

            val initialResponse = client.newCall(
                Request.Builder()
                    .url(initialUrl)
                    .post(createFormBody(initialBody))
                    .apply {
                        SapPortalHeaders.getInitialHeaders().forEach { (key, value) ->
                            addHeader(key, value)
                        }
                    }
                    .build()
            ).execute()

            if (!initialResponse.isSuccessful) {
                val responseContent = initialResponse.body?.string()
                initialResponse.close()
                return@withContext AttendanceResult.Error("Initial attendance load failed with status: ${initialResponse.code}. Response: ${responseContent?.take(200)}")
            }

            println("✅ Initial attendance load successful. Status: ${initialResponse.code}")

            // NOW: Send the full request with year/session selection (as per fetch_attendance.js)
            println("📅 Selecting Academic Year and Session from WD response...")

            // Determine which values to use: user-provided or auto-detected
            val (academicYearValue, termCodeValue) = SapPortalHtmlParser.detectAcademicYearAndTerm(wdResponseHtml, academicYear, termCode)

            // Build SAPEVENTQUEUE with year/session selection as per fetch_attendance.js
            val attendanceBody = SapPortalParams.getAttendanceBodyWithSelection(secureId, academicYearValue, termCodeValue).toMutableMap()

            // Replace the placeholder with actual ext sid
            val encodedExtSid = java.net.URLEncoder.encode(finalExtSid.replace("*", "*").replace("-", "--"), "UTF-8")
            val sapeventQueue = attendanceBody["SAPEVENTQUEUE"]?.replace("PLACEHOLDER_EXT_SID", encodedExtSid) ?: ""
            attendanceBody["SAPEVENTQUEUE"] = sapeventQueue

            val attendanceResponse = client.newCall(
                Request.Builder()
                    .url(initialUrl) // Use same URL as initial request
                    .post(createFormBody(attendanceBody))
                    .apply {
                        SapPortalHeaders.getInitialHeaders().forEach { (key, value) ->
                            addHeader(key, value)
                        }
                    }
                    .build()
            ).execute()

            if (!attendanceResponse.isSuccessful) {
                val responseContent = attendanceResponse.body?.string()
                attendanceResponse.close()
                return@withContext AttendanceResult.Error("Attendance request failed with status: ${attendanceResponse.code}. Response: ${responseContent?.take(200)}")
            }

            println("✅ Attendance request successful. Status: ${attendanceResponse.code}")

            val attendanceHtml = attendanceResponse.body?.string()
            attendanceResponse.close()

            // Add debugging logs as suggested
            println("🔗 Attendance URL: $initialUrl")
            println("📄 Raw attendance response (first 1000 chars):\n${attendanceHtml?.take(1000)}")
            println("📄 Full response length: ${attendanceHtml?.length}")

            // Parse attendance data from the response
            val parsedAttendance = AttendanceData(SapPortalHtmlParser.parseAttendanceData(attendanceHtml ?: ""))

            // Logout after successful attendance fetch
            performLogout()

            AttendanceResult.Success(parsedAttendance)

        } catch (e: IOException) {
            performLogout() // Attempt logout even on error
            AttendanceResult.Error("Network error: ${e.message ?: e.javaClass.simpleName}")
        } catch (e: Exception) {
            performLogout() // Attempt logout even on error
            println("❌ General error during attendance fetch: ${e.message}")
            e.printStackTrace() // Print stack trace for debugging
            // Provide more user-friendly error message for specific cases
            val errorMessage = if (e.message?.contains("No attendance rows parsed") == true) {
                "No attendance data available for the selected year and session. Please check your year/session selection and try again."
            } else {
                "Error: ${e.message ?: e.javaClass.simpleName}"
            }
            AttendanceResult.Error(errorMessage)
        }
    }

    private fun createFormBody(params: Map<String, String>): RequestBody {
        val formBuilder = FormBody.Builder()
        for ((key, value) in params) {
            formBuilder.add(key, value)
        }
        return formBuilder.build()
    }

    private fun secureWipe(charArray: CharArray) {
        for (i in charArray.indices) {
            charArray[i] = '0'
        }
    }

    private fun performLogout() {
        try {
            val logoutResponse = client.newCall(
                Request.Builder()
                    .url(SapPortalUrls.getLogoutUrl())
                    .post(createFormBody(mapOf("logout_submit" to "true")))
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("origin", BuildConfig.PORTAL_BASE)
                    .addHeader("referer", SapPortalUrls.getLoginPageUrl())
                    .build()
            ).execute()

            if (logoutResponse.isSuccessful) {
                println("✅ Logout completed successfully.")
            } else {
                println("⚠️ Logout completed with status: ${logoutResponse.code}")
            }
            logoutResponse.close()
        } catch (e: Exception) {
            println("⚠️ Error during logout: ${e.message}")
        }
    }
}

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

// Enhanced cookie jar for cross-domain SAP session sharing
class PersistentCookieJar : CookieJar {
    private val cookieStore = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            // Remove cookies that match name/domain/path first
            cookieStore.removeAll {
                it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path
            }
            // Add new cookie
            cookieStore.add(cookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val currentTime = System.currentTimeMillis()
        return cookieStore.filter { cookie ->
            // Check if cookie is valid for this request
            isCookieValidForRequest(cookie, url, currentTime)
        }
    }

    private fun isCookieValidForRequest(cookie: Cookie, requestUrl: HttpUrl, currentTime: Long): Boolean {
        // Check expiration
        if (cookie.expiresAt <= currentTime) return false

        // Check domain - allow subdomain matching for SAP portals
        val requestHost = requestUrl.host.lowercase()
        var cookieDomain = cookie.domain.lowercase()
        if (!cookieDomain.startsWith(".")) {
            cookieDomain = ".$cookieDomain"
        }

        // Domain matching (allows subdomain access for SAP environments)
        if (!requestHost.endsWith(cookieDomain) && requestHost != cookieDomain.removePrefix(".")) {
            return false
        }

        // Path matching
        if (!requestUrl.encodedPath.startsWith(cookie.path)) {
            return false
        }

        // HTTPS requirement if secure
        if (cookie.secure && requestUrl.scheme != "https") {
            return false
        }

        return true
    }

    fun clear() {
        cookieStore.clear()
    }
}