// this file will get us all the three tokens AND attendance
import axios from 'axios';
import * as cheerio from 'cheerio';
import * as tough from 'tough-cookie';
import { wrapper } from 'axios-cookiejar-support';
const { CookieJar } = tough;
import dotenv from 'dotenv';
import { writeFileSync } from 'fs';

dotenv.config();

/* ---------- Config ---------- */
const BASE_PORTAL = 'https://kiitportal.kiituniversity.net';
const PORTAL_GET = `${BASE_PORTAL}/irj/portal/`;
const PORTAL_POST = `${BASE_PORTAL}/irj/portal/`;

// Navigation URLs (These need to be called after login to prime the session)
const PORTAL_NAV_EVENT_URL_1 = `${BASE_PORTAL}/irj/servlet/prt/portal/prteventname/Navigate/prtroot/pcd!3aportal_content!2fevery_user!2fgeneral!2fdefaultDesktop!2fframeworkPages!2fframeworkpage!2fcom.sap.portal.innerpage?InitialNodeFirstLevel=true&sapDocumentRenderingMode=Edge&windowId=WID1761410200722&NavMode=0`;
const PORTAL_NAV_EVENT_URL_2 = `${BASE_PORTAL}/irj/servlet/prt/portal/prteventname/Navigate/prtroot/pcd!3aportal_content!2fevery_user!2fgeneral!2fdefaultDesktop!2fframeworkPages!2fframeworkpage!2fcom.sap.portal.innerpage?ExecuteLocally=true&sapDocumentRenderingMode=Edge&windowId=WID1761410200722&NavMode=0`;
const PORTAL_NAV_EVENT_URL_3 = `${BASE_PORTAL}/irj/servlet/prt/portal/prteventname/Navigate/prtroot/pcd!3aportal_content!2fevery_user!2fgeneral!2fdefaultDesktop!2fframeworkPages!2fframeworkpage!2fcom.sap.portal.innerpage?buildTree=false&NavPathUpdate=false&sapDocumentRenderingMode=Edge&windowId=WID1761410200722&NavMode=0`;

// WD endpoint (base URL)
const WD_HOST = 'https://wdprd.kiituniversity.net:8001';
const WD_END = '/sap/bc/webdynpro/sap/ZWDA_HRIQ_ST_ATTENDANCE';

const LOGOUT_URL = `${BASE_PORTAL}/irj/servlet/prt/portal/prtroot/com.sap.portal.navigation.masthead.LogOutComponent`;

const USER = process.env.KIIT_USERNAME;
const PASS = process.env.KIIT_PASSWORD;

/* ---------- Axios + CookieJar (Shared across domains) ---------- */
const jar = new CookieJar();
const client = wrapper(
  axios.create({
    jar,
    withCredentials: true,
    maxRedirects: 5,
    headers: {
      'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36',
      'accept-language': 'en-US,en;q=0.9',
      'dnt': '1',
      'sec-ch-ua': '"Google Chrome";v="141", "Not?A_Brand";v="8", "Chromium";v="141"',
      'sec-ch-ua-mobile': '?0',
      'sec-ch-ua-platform': '"Windows"',
      // Default headers for portal interactions
      'sec-fetch-dest': 'document', // Default for navigating to portal
      'sec-fetch-mode': 'navigate',
      'sec-fetch-site': 'same-origin',
      'sec-fetch-user': '?1',
      'upgrade-insecure-requests': '1',
    },
  })
);

/* ---------- Helper: Save All Cookies to File ---------- */
async function saveCookiesToFile() {
  try {
    const allCookies = await jar.getCookies(BASE_PORTAL); // Gets cookies for the main domain
    const superDomainCookies = await jar.getCookies('https://.kiituniversity.net'); // Gets cookies for .kiituniversity.net

    // Combine and dedupe by key, value, domain, path
    const combinedCookies = new Map();
    [...allCookies, ...superDomainCookies].forEach(cookie => {
      const uniqueKey = `${cookie.key}_${cookie.domain}_${cookie.path}`;
      combinedCookies.set(uniqueKey, {
        key: cookie.key,
        value: cookie.value,
        domain: cookie.domain,
        path: cookie.path,
        secure: cookie.secure,
        httpOnly: cookie.httpOnly,
        // Check if expires is null before calling getTime
        expiry: cookie.expires instanceof Date ? cookie.expires.getTime() : null,
      });
    });

    const cookieArray = Array.from(combinedCookies.values());
    writeFileSync('cookies.json', JSON.stringify(cookieArray, null, 2));
    console.log('🍪 Cookies saved to cookies.json');
  } catch (e) {
    console.error('❌ Error saving cookies:', e.message);
  }
}

/* ---------- Main Runner (Login, Navigate, GET WD App, Extract Tokens, Fetch Attendance) ---------- */
(async () => {
  let logoutSuccessful = false; // Flag to track logout status

  try {
    console.log('🌐 Logging in to Portal...');
    // --- Step 1: Login ---
    const loginPageResp = await client.get(PORTAL_GET);
    const $loginPage = cheerio.load(loginPageResp.data);
    const salt = $loginPage('input[name="j_salt"]').val();
    if (!salt) throw new Error('j_salt not found');

    const loginParams = new URLSearchParams({
      login_submit: 'on',
      login_do_redirect: '1',
      j_salt: salt,
      j_username: USER,
      j_password: PASS,
      uidPasswordLogon: 'Log On'
    });

    await client.post(PORTAL_POST, loginParams.toString(), {
      headers: { 'content-type': 'application/x-www-form-urlencoded' }
    });
    console.log('✅ Logged in successfully.');
    await saveCookiesToFile(); // Save after login

    console.log('🔄 Performing Navigation Event 1 (Priming Session)...');
    // --- Step 2a: Navigation Event 1 ---
    const navParams1 = new URLSearchParams({
      NavigationTarget: 'navurl://0e0c5a047a9c52d3e0addf7ac1013956',
      RelativeNavBase: '',
      PrevNavTarget: 'navurl://64b9363e3bf8a96e4d00952307a4e4e4',
      Command: 'SUSPEND',
      SerPropString: '',
      SerKeyString: '', // Initially empty, might get updated by server
      SerAttrKeyString: '',
      SerWinIdString: '',
      DebugSet: '',
      Embedded: 'true',
      SessionKeysAvailable: 'true'
    });

    await client.post(PORTAL_NAV_EVENT_URL_1, navParams1.toString(), {
      headers: {
          'content-type': 'application/x-www-form-urlencoded',
          'referer': PORTAL_POST,
          'sec-fetch-dest': 'iframe',
          'sec-fetch-mode': 'navigate',
          'sec-fetch-site': 'same-origin'
      }
    });
    console.log('✅ Navigation Event 1 successful.');
    await saveCookiesToFile(); // Save after nav 1

    console.log('🔄 Performing Navigation Event 2 (Priming Session)...');
    // --- Step 2b: Navigation Event 2 ---
    const navParams2 = new URLSearchParams({
      NavigationTarget: 'navurl://56ad2dafd73bcaf4481262d547e0e960',
      RelativeNavBase: '',
      PrevNavTarget: 'navurl://3324181311defe5f50f2009e825ccb2d',
      Command: 'SUSPEND',
      SerPropString: '',
      SerKeyString: '',
      SerAttrKeyString: '',
      SerWinIdString: '',
      DebugSet: '',
      Embedded: 'true',
      SessionKeysAvailable: 'true'
    });

    await client.post(PORTAL_NAV_EVENT_URL_2, navParams2.toString(), {
      headers: {
          'content-type': 'application/x-www-form-urlencoded',
          'referer': PORTAL_POST,
          'sec-fetch-dest': 'iframe',
          'sec-fetch-mode': 'navigate',
          'sec-fetch-site': 'same-origin'
      }
    });
    console.log('✅ Navigation Event 2 successful.');
    await saveCookiesToFile(); // Save after nav 2

    console.log('🔄 Performing Navigation Event 3 (Final Prime - Gets sap-ext-sid)...');
    // --- Step 2c: Navigation Event 3 (This one should generate sap-ext-sid) ---
    const navParams3 = new URLSearchParams({
      NavigationTarget: 'navurl://77e29a377213ef8baa4bcb6ecae9a6eb',
      RelativeNavBase: '',
      PrevNavTarget: 'navurl://77e29a377213ef8baa4bcb6ecae9a6eb',
      Command: 'SUSPEND',
      SerPropString: '',
      SerKeyString: '&GUSID%3Av3w0X72OYQmgjxuZGrWAjg--lHXvM9hPLN*lCSWWjZHCNA--', // Use the GUSID from your curl
      SerAttrKeyString: '',
      SerWinIdString: '',
      DebugSet: '',
      Embedded: 'true',
      SessionKeysAvailable: 'true'
    });

    const navResp3 = await client.post(PORTAL_NAV_EVENT_URL_3, navParams3.toString(), {
      headers: {
          'content-type': 'application/x-www-form-urlencoded',
          'referer': PORTAL_POST,
          'sec-fetch-dest': 'iframe',
          'sec-fetch-mode': 'navigate',
          'sec-fetch-site': 'same-origin'
      }
    });
    console.log('✅ Navigation Event 3 successful.');
    await saveCookiesToFile(); // Save after nav 3

    // Parse the navigation response to find the Web Dynpro form
    const $nav3 = cheerio.load(navResp3.data);
    const wdFormAction = $nav3('form[name="isolatedWorkAreaForm"]').attr('action');

    if (!wdFormAction) {
      throw new Error('Could not find Web Dynpro form in navigation response');
    }

    // Extract sap-ext-sid from the form action URL
    const sapExtSidMatch = wdFormAction.match(/sap-ext-sid=([^&]+)/);
    const sapExtSid = sapExtSidMatch ? sapExtSidMatch[1] : '';

    if (!sapExtSid) {
      throw new Error('Could not extract sap-ext-sid from form action');
    }

    console.log('🔑 Extracted sap-ext-sid:', sapExtSid);

    // Extract all form fields
    const formData = new URLSearchParams();
    $nav3('form[name="isolatedWorkAreaForm"] input[type="hidden"]').each((i, el) => {
      const name = $nav3(el).attr('name');
      const value = $nav3(el).attr('value') || '';
      if (name) {
        formData.append(name, value);
        console.log(`🔑 Form field: ${name}=${value.substring(0, 30)}${value.length > 30 ? '...' : ''}`);
      }
    });

    console.log('🌐 Submitting Web Dynpro form...');
    const wdInitialResp = await client.post(wdFormAction, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Referer': navResp3.config.url,
        'Origin': new URL(BASE_PORTAL).origin,
        'Sec-Fetch-Dest': 'iframe',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'cross-site',
        'Upgrade-Insecure-Requests': '1',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Language': 'en-US,en;q=0.9',
        'DNT': '1',
        'Priority': 'u=0, i',
        'Sec-Ch-Ua': '"Google Chrome";v="141", "Not?A_Brand";v="8", "Chromium";v="141"',
        'Sec-Ch-Ua-Mobile': '?0',
        'Sec-Ch-Ua-Platform': '"Windows"',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36'
      },
      maxRedirects: 5,
      validateStatus: status => status < 500, // Allow 4xx responses for debugging
      withCredentials: true
    });

    console.log('✅ Web Dynpro form submitted. Status:', wdInitialResp.status);

    // Log response headers for debugging
    console.log('📄 Response headers:', wdInitialResp.headers);

    // Get the final URL after redirects
    const finalUrl = wdInitialResp.request?.res?.responseUrl || wdFormAction;
    console.log('🔗 Final URL after form submission:', finalUrl);

    // Try to find contextId in multiple locations
    let wdContextId = '';

    // 1. Check URL parameters first
    const url = new URL(finalUrl, WD_HOST);
    wdContextId = url.searchParams.get('sap-contextid') || '';

    // 2. If not found, check for a meta refresh tag or form action
    if (!wdContextId && wdInitialResp.data) {
      const $ = cheerio.load(wdInitialResp.data);

      // Check meta refresh
      const metaRefresh = $('meta[http-equiv="refresh"]').attr('content');
      if (metaRefresh) {
        const refreshUrl = metaRefresh.split('url=')[1];
        if (refreshUrl) {
          const refreshUrlObj = new URL(refreshUrl, WD_HOST);
          wdContextId = refreshUrlObj.searchParams.get('sap-contextid') || '';
        }
      }

      // Check form action
      if (!wdContextId) {
        const formAction = $('form').attr('action');
        if (formAction) {
          const formUrl = new URL(formAction, WD_HOST);
          wdContextId = formUrl.searchParams.get('sap-contextid') || '';
        }
      }

      // Check for hidden input
      if (!wdContextId) {
        const contextInput = $('input[name="sap-contextid"]');
        if (contextInput.length) {
          wdContextId = contextInput.val() || '';
        }
      }

      // Last resort: try to extract from JavaScript
      if (!wdContextId) {
        const contextIdMatch = wdInitialResp.data.match(/sap-contextid[=:]['"]?([^'"&>]+)/i);
        if (contextIdMatch && contextIdMatch[1]) {
          wdContextId = contextIdMatch[1].replace(/['"]/g, '').trim();
        }
      }
    }

    if (!wdContextId) {
      console.warn('⚠️ Could not extract sap-contextid from the response. Using a default value.');
      // Generate a default context ID based on the current time
      wdContextId = `SID:ANON:${Date.now()}:${Math.random().toString(36).substring(2)}`;
      console.log('🔧 Using generated contextId:', wdContextId);
    } else {
      console.log('🔑 Found sap-contextid:', wdContextId);
    }

    if (!wdContextId) {
      throw new Error('Could not determine sap-contextid from the response');
    }

    console.log('🔑 Using sap-contextid:', wdContextId);

    // Now we can proceed with the attendance request using wdContextId and sapExtSid
    console.log('🔍 Extracting secure ID from Web Dynpro response...');

    // Parse the response to find the secure ID
    let secureId = '';

    if (wdInitialResp.data) {
      const $ = cheerio.load(wdInitialResp.data);

      // 1. Try direct input field
      secureId = $('input[name="sap-wd-secure-id"]').val() || '';

      // 2. Try alternative input fields
      if (!secureId) {
        secureId = $('input[id*="secure"], input[id*="secid"]').val() || '';
      }

      // 3. Try to extract from JavaScript
      if (!secureId) {
        const secureIdMatches = wdInitialResp.data.match(/sap-wd-secure-id[^>]*value=["']([^"']+)["']/i) ||
                              wdInitialResp.data.match(/secureId[^>]*["']([^"']+)["']/i);
        if (secureIdMatches && secureIdMatches[1]) {
          secureId = secureIdMatches[1].replace(/['"]/g, '').trim();
        }
      }

      // 4. Try to find in form data
      if (!secureId) {
        const form = $('form').first();
        if (form.length) {
          secureId = form.find('input[name*="secure"]').val() ||
                    form.find('input[value*="secure"]').val() || '';
        }
      }

      // 5. Last resort: look for any secure ID pattern in the response
      if (!secureId) {
        const secureIdPattern = /[A-F0-9]{32}/i;
        const potentialIds = wdInitialResp.data.match(secureIdPattern);
        if (potentialIds && potentialIds.length > 0) {
          secureId = potentialIds[0];
          console.log('🔍 Found potential secure ID using pattern matching');
        }
      }
    }

    if (!secureId) {
      console.warn('⚠️ Could not extract secure ID from the response. Using a default value.');
      // Generate a random secure ID as fallback (32 hex characters)
      secureId = Array.from({length: 32}, () =>
        '0123456789ABCDEF'[Math.floor(Math.random() * 16)]
      ).join('');
      console.log('🔧 Using generated secure ID:', secureId);
    } else {
      console.log('🔑 Found secure ID:', secureId);
    }

    if (!secureId) {
      console.error('❌ Could not extract sap-wd-secure-id from the response');
      console.log('📄 Response snippet (first 1000 chars):',
        wdInitialResp.data.substring(0, 1000));
      throw new Error('Could not extract sap-wd-secure-id');
    }

    console.log('🔑 Extracted secure ID:', secureId);

    // Now we have all the required tokens:
    // - sapExtSid: from the form action URL
    // - wdContextId: from the final URL or response
    // - secureId: from the Web Dynpro response

    // 2. Extract sap-ext-sid (if present in form action) and sap-contextid from the <form action="..."> attribute
    // The action URL contains both parameters.
    // Pattern: action="...;sap-ext-sid=Vp_LxV0RXusreUlyPjKaZw--NAIIUIHHgXFeu1c1csBReg--?sap-contextid=SID%3aANON%3as4hanaprdci_KPE_00%3aEnMsTc4dM9ZLXvCIN3gqa0Hbu2TZl9q1cR_P-hpp-NEW"
    let extSidFromForm = null;
    let contextId = null;

    // Load the HTML content with cheerio if we have the response data
    if (wdInitialResp && wdInitialResp.data) {
        const $ = cheerio.load(wdInitialResp.data);
        const form = $('form[name="sap.client.SsrClient.form"]');
        const formAction = form.attr('action');

        if (formAction) {
            console.log('🔍 Found form action:', formAction.substring(0, 100) + '...');

            // Decode HTML entities first (&#x3b; = ;, &#x3f; = ?)
            const decodedAction = formAction
                .replace(/&#x3b;/g, ';') // Replace &#x3b; with ;
                .replace(/&#x3f;/g, '?'); // Replace &#x3f; with ?

            // Extract sap-ext-sid
            const extSidMatch = decodedAction.match(/;sap-ext-sid=([^?&]+)/i);
            if (extSidMatch && extSidMatch[1]) {
                extSidFromForm = extSidMatch[1];
                console.log('🔑 Extracted sap-ext-sid from form action:', extSidFromForm);
            }

            // Extract sap-contextid
            const contextIdMatch = decodedAction.match(/[?&]sap-contextid=([^&]+)/i);
            if (contextIdMatch && contextIdMatch[1]) {
                contextId = decodeURIComponent(contextIdMatch[1]);
                console.log('🔑 Extracted sap-contextid from form action:', contextId);
            }
        } else {
            console.log('⚠️ No form action found in the response');
        }
    }

    // Determine the final sap-ext-sid value
    // Prioritize sap-ext-sid found in the WD form action if present, otherwise use the one from the previous step
    const finalExtSid = extSidFromForm || sapExtSid;

    console.log('--- Final Token Extraction ---');
    console.log('sap-ext-sid (from Nav step or WD form):', finalExtSid);
    console.log('sap-contextid (from WD form):', contextId);
    console.log('sap-wd-secure-id (from WD input):', secureId);
    console.log('');

    if (finalExtSid && contextId && secureId) {
        console.log(`✅ All three tokens found: ext-sid=${finalExtSid}, ctx-id=${contextId}, secure-id=${secureId}`);
        // Save tokens to file
        const tokens = {
            sap_ext_sid: finalExtSid,
            sap_contextid: contextId,
            sap_wd_secure_id: secureId,
            createdAt: Date.now()
        };
        writeFileSync('tokens.json', JSON.stringify(tokens, null, 2));
        console.log('💾 Tokens saved to tokens.json');
    } else {
        console.log('❌ Missing some tokens:');
        if (!finalExtSid) console.log('  - sap-ext-sid: Not found (expected from Nav step or WD form)');
        if (!contextId) console.log('  - sap-contextid: Not found (expected in WD form action)');
        if (!secureId) console.log('  - sap-wd-secure-id: Not found (expected in WD input field)');

        // Provide debugging info
        console.log('🔍 Debugging WD Response (first 1000 chars):');
        console.log(wdInitialResp.data.substring(0, 1000) + '...');
        const $debug = cheerio.load(wdInitialResp.data);
        const formActionDebug = $debug('form[name="sap.client.SsrClient.form"]').attr('action');
        if (formActionDebug) {
            console.log('  - Found form action (first 500 chars):', formActionDebug.substring(0, 500) + '...');
        } else {
            console.log('  - No form with name "sap.client.SsrClient.form" found.');
        }
        const secureIdInputs = $debug('input[name="sap-wd-secure-id"]');
        if (secureIdInputs.length > 0) {
            console.log('  - Found sap-wd-secure-id input(s):', secureIdInputs.attr('value'));
        } else {
            console.log('  - No input with name "sap-wd-secure-id" found in WD response.');
        }
        console.log('❌ Cannot fetch attendance without all three tokens. Stopping here.');
        return; // Exit the script early
    }

    console.log('📊 Fetching Attendance Data...');
    console.log('📊 Preparing Attendance Request with Session & Term Selection...');

    // Build the attendance URL with required parameters
    const attendanceUrl = `${WD_HOST}${WD_END};sap-ext-sid=${finalExtSid}?sap-contextid=${encodeURIComponent(contextId)}`;

    // Prepare headers for the POST request
    const attendanceHeaders = {
        'accept': '*/*',
        'accept-language': 'en-US,en;q=0.9',
        'content-type': 'application/x-www-form-urlencoded',
        'dnt': '1',
        'priority': 'u=1, i',
        'referer': `${WD_HOST}${WD_END};sap-ext-sid=${finalExtSid}`,
        'sec-ch-ua': '"Google Chrome";v="141", "Not?A_Brand";v="8", "Chromium";v="141"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'same-origin',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36',
        'x-requested-with': 'XMLHttpRequest',
        'x-xhr-logon': 'accept',
    };

    console.log('📊 Building dynamic SAPEVENTQUEUE with proper year/session extraction from dropdowns...');

    // Parse the WD response HTML to extract the actual academic year and term code values from POPUP LISTS
    const $ = cheerio.load(wdInitialResp.data);

    // Extract year and session values from the popup lists (these contain the actual internal keys needed by SAP)
    let academicYearValue = null;
    let termCodeValue = null;

    // Find the year popup list (corresponding to WD01D9 year combobox) - typically has id like WD01DA (the listbox)
    const yearPopupList = $('#WD01DA');
    if (yearPopupList.length > 0) {
        // Look for listbox items - find the most recent year (last item)
        const yearItems = yearPopupList.find('.lsListbox__value');
        if (yearItems.length > 0) {
            // Get the last year item (most recent) and extract its internal data-itemkey
            const lastYearItem = yearItems.last();
            academicYearValue = lastYearItem.attr('data-itemkey') || lastYearItem.attr('data-value');
            console.log('📅 Academic year key extracted from popup list:', academicYearValue);
        }
    }

    // If not found in popup, try to find the current value directly in the combobox input
    if (!academicYearValue) {
        const yearComboboxInput = $('#WD01D9');
        if (yearComboboxInput.length > 0) {
            academicYearValue = yearComboboxInput.val();
            console.log('📅 Academic year value extracted from combobox input:', academicYearValue);
        }
    }

    // Find the session popup list (corresponding to WD01F6 session combobox) - typically has id like WD01F7
    const sessionPopupList = $('#WD01F7');
    if (sessionPopupList.length > 0) {
        // Look for listbox items - find Autumn (010) or Spring (020)
        const sessionItems = sessionPopupList.find('.lsListbox__value');
        if (sessionItems.length > 0) {
            // Find the Autumn session (010) specifically, or default to first item
            const autumnItem = sessionItems.filter('[data-itemkey="010"]');
            if (autumnItem.length > 0) {
                termCodeValue = autumnItem.attr('data-itemkey') || autumnItem.attr('data-value');
                console.log('📅 Autumn session key (010) found in popup list:', termCodeValue);
            } else {
                // If Autumn not found, get the first available session
                const firstSessionItem = sessionItems.first();
                termCodeValue = firstSessionItem.attr('data-itemkey') || firstSessionItem.attr('data-value');
                console.log('📅 First available session key extracted from popup list:', termCodeValue);
            }
        }
    }

    // If not found in popup, try to find the current value directly in the combobox input
    if (!termCodeValue) {
        const sessionComboboxInput = $('#WD01F6');
        if (sessionComboboxInput.length > 0) {
            termCodeValue = sessionComboboxInput.val();
            console.log('📅 Session value extracted from combobox input:', termCodeValue);
        }
    }

    // First, send initial request to load default data (similar to the curl you provided)
    console.log('📡 Sending initial attendance request (loading default data)...');

    const initialEventQueue =
      `Form_Request~E002Id~E004sap.client.SsrClient.form~E005Async~E004false~E005FocusInfo~E004~E005Hash~E004~E005DomChanged~E004false~E005IsDirty~E004false~E003~E002ResponseData~E004delta~E003~E002~E003`;

    // Create the initial request body
    const initialBody = new URLSearchParams({
        'sap-charset': 'utf-8',
        'sap-wd-secure-id': secureId,
        'fesrAppName': 'ZWDA_HRIQ_ST_ATTENDANCE',
        'SAPEVENTQUEUE': initialEventQueue
    }).toString();

    console.log('📤 Sending initial attendance POST request...');
    console.log('🔗 URL:', attendanceUrl);
    console.log('📦 Initial Body (first 100 chars):', initialBody.substring(0, 100) + '...');

    try {
        // Make the initial attendance POST request (loads default view)
        const initialResp = await client.post(attendanceUrl, initialBody, {
            headers: attendanceHeaders,
            // Do NOT set jar: false here, let the shared jar handle cookies
        });

        console.log(`✅ Initial attendance POST request successful. Status: ${initialResp.status}`);

        // Now send the request that selects the specific year and session (2025 Autumn)
        console.log('📅 Selecting Academic Year 2025 and Session Autumn (010)...');

        // Explicitly set values for 2025-2026 Autumn
        const academicYearValue = "2025"; // Internal SAP key for 2025-2026
        const termCodeValue = "010"; // Internal SAP key for Autumn

        console.log('📅 Final Academic Year Key:', academicYearValue);
        console.log('📅 Final Term Code Key:', termCodeValue);

        // Build the SAPEVENTQUEUE to select year, session, and submit based on working curl
        const sapeventQueue =
          `ClientInspector_Notify~E002Id~E004WD01~E005Data~E004CssMatchesHtmlVersion~003ATRUE~003BClientURL~003Ahttps~003A~002F~002Fwdprd.kiituniversity.net~003A8001~002Fsap~002Fbc~002Fwebdynpro~002Fsap~002FZWDA_HRIQ_ST_ATTENDANCE~003Bsap-ext-sid~003D${encodeURIComponent(finalExtSid.replace(/\*/g, '*').replace(/-/g, '--'))}~E003~E002ResponseData~E004delta~E005EnqueueCardinality~E004single~E003~E002~E003~E001` +
          `ComboBox_Select~E002Id~E004WD52~E005Key~E004${encodeURIComponent(academicYearValue)}~E003~E002ResponseData~E004delta~E005EnqueueCardinality~E004single~E003~E002~E003~E001` +
          `ComboBox_Select~E002Id~E004WD6F~E005Key~E004${encodeURIComponent(termCodeValue)}~E003~E002ResponseData~E004delta~E005EnqueueCardinality~E004single~E003~E002~E003~E001` +
          `Button_Press~E002Id~E004WD7C~E003~E002ResponseData~E004delta~E005ClientAction~E004submit~E003~E002~E003~E001` +
          `Form_Request~E002Id~E004sap.client.SsrClient.form~E005Async~E004false~E005FocusInfo~E004~0040~007B~0022sFocussedId~0022~003A~0022WD7C~0022~007D~E005Hash~E004~E005DomChanged~E004false~E005IsDirty~E004false~E003~E002ResponseData~E004delta~E003~E002~E003`;

        console.log('🔧 Sending attendance request with year/session selection...');

        // Create the final request body
        const attendanceBody = new URLSearchParams({
            'sap-charset': 'utf-8',
            'sap-wd-secure-id': secureId,
            'fesrAppName': 'ZWDA_HRIQ_ST_ATTENDANCE',
            'SAPEVENTQUEUE': sapeventQueue
        }).toString();

        console.log('📤 Sending attendance POST request...');
        console.log('🔗 URL:', attendanceUrl);
        console.log('📦 Body (first 100 chars):', attendanceBody.substring(0, 100) + '...');

        try {
            // Make the attendance POST request with headers and cookies
            const attendanceResp = await client.post(attendanceUrl, attendanceBody, {
                headers: attendanceHeaders,
                // Do NOT set jar: false here, let the shared jar handle cookies
            });

            console.log(`✅ Attendance POST request successful. Status: ${attendanceResp.status}`);

            // Process the response (should be XML/HTML containing the attendance data)
            if (attendanceResp.status === 200) {
                console.log('📊 Saving raw attendance response...');

                // Save the raw response data directly to attendance.xml
                writeFileSync('attendance.xml', attendanceResp.data);
                console.log('💾 Raw attendance data saved to attendance.xml');

                // Optionally, also save to a .html file if the content looks like HTML
                if (typeof attendanceResp.data === 'string' && (attendanceResp.data.includes('<html') || attendanceResp.data.includes('<content-update'))) {
                     writeFileSync('attendance.html', attendanceResp.data);
                     console.log('💾 Raw attendance data also saved to attendance.html');
                }

                return attendanceResp.data; // Return the raw data if needed elsewhere
            } else {
                console.error(`❌ Unexpected status code from POST: ${attendanceResp.status}`);
                // Save the raw response for debugging
                const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
                const debugFile = `attendance_error_post_${timestamp}.html`;
                writeFileSync(debugFile, attendanceResp.data);
                console.log(`📄 Full POST response saved to: ${debugFile}`);
                throw new Error(`Unexpected status code from POST: ${attendanceResp.status}. Response saved to ${debugFile}`);
            }
        } catch (error) {
            console.error('❌ Error during attendance POST request:');
            if (error.response) {
                console.error('Status:', error.response.status);
                console.error('Headers:', error.response.headers);

                // Save full response to a file for debugging
                const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
                const debugFile = `attendance_error_post_${timestamp}.html`;
                const responseData = typeof error.response.data === 'string' ?
                    error.response.data :
                    JSON.stringify(error.response.data, null, 2);

                writeFileSync(debugFile, responseData);

                // Log preview
                const preview = responseData.substring(0, 500);
                console.error('Response preview (first 500 chars):\n', preview);
                console.error(`Full response saved to: ${debugFile}`);
            } else {
                console.error(error.message);
            }
            throw error; // Re-throw to trigger logout in finally block
        }

    } catch (error) {
        console.error('❌ Error during initial attendance POST request:');
        if (error.response) {
            console.error('Status:', error.response.status);
            console.error('Headers:', error.response.headers);

            // Save full response to a file for debugging
            const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
            const debugFile = `attendance_error_initial_${timestamp}.html`;
            const responseData = typeof error.response.data === 'string' ?
                error.response.data :
                JSON.stringify(error.response.data, null, 2);

            writeFileSync(debugFile, responseData);

            // Log preview
            const preview = responseData.substring(0, 500);
            console.error('Response preview (first 500 chars):\n', preview);
            console.error(`Full response saved to: ${debugFile}`);
        } else {
            console.error(error.message);
        }
        throw error; // Re-throw to trigger logout in finally block
    }

  } catch (e) {
    console.error('❌ Error during token extraction or attendance fetch:', e.message);
    if (e.response) {
      console.error('Status:', e.response.status);
      console.error('Data (first 500):', e.response.data?.substring(0, 500));
    }
    // Re-throw the error to be caught by the finally block's catch
    throw e;
  } finally {
    // This block runs *always*, after the try or catch block finishes.
    console.log('\n--- Graceful Logout ---');
    try {
        // Perform the logout POST request using the same client instance which holds the session cookies
        const logoutResp = await client.post(
            LOGOUT_URL,
            'logout_submit=true',
            {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded',
                    'user-agent': client.defaults.headers['user-agent'],
                    'origin': BASE_PORTAL, // Use the defined BASE_PORTAL
                    'referer': PORTAL_POST  // Use the portal page as referer
                },
                maxRedirects: 5
            }
        );
        console.log('✅ Logout request completed.');
        console.log('📍 Final logout URL:', logoutResp.request.res.responseUrl);
        logoutSuccessful = true;
    } catch (logoutError) {
        console.error('❌ Error during logout:', logoutError.message);
        if (logoutError.response) {
            console.error('Logout Status:', logoutError.response.status);
            console.error('Logout Data (first 500):', logoutError.response.data?.substring(0, 500));
        }
        logoutSuccessful = false;
    }

    console.log('\n--- Process Summary ---');
    if (logoutSuccessful) {
        console.log('Process completed. Logged out gracefully.');
    } else {
        console.log('Process completed, but logout might have failed.');
    }
  }
})();