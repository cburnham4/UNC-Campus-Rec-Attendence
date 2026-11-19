# Google Play Data Safety Responses

**App:** Recreation Attendence for UNC (`letshangllc.uncrecattendence`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

This document provides responses for the Google Play Console Data Safety section.

---

## Overview

This app shows UNC campus recreation facility hours and historical average occupancy charts. It does not create user accounts. Network calls go to Google Calendar (facility hours) and Google AdMob (ads). Local preferences store which facilities the user selected.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| App interactions | Yes | Yes (AdMob) | Advertising |
| Advertising ID | Yes | Yes (AdMob) | Advertising |
| Crash logs | No | No | — |
| Personal info (name/email) | No | No | — |
| Location | No | No | — |

---

## Detailed Data Types

### App Activity — App interactions
- **Collected:** Yes (via Google AdMob / analytics associated with ads)
- **Shared:** Yes (Google advertising partners)
- **Purpose:** Advertising
- **Optional:** Subject to UMP consent in applicable regions

### Device or Other IDs — Advertising ID
- **Collected:** Yes (via Google AdMob)
- **Shared:** Yes (with Google for advertising)
- **Purpose:** Advertising
- **User Control:** Device ad settings + in-app UMP consent where required

### Local app data (not a Play “collected” cloud type)
- Selected facility list stored in SharedPreferences on device only

---

## Third-Party SDKs

### Google Calendar API
- **Data:** App requests public calendar events for facility hours (no user credentials)
- **Purpose:** App functionality
- **Transport:** HTTPS

### Google AdMob
- **Data:** Advertising ID, app interactions, device info
- **Purpose:** Display advertisements
- **Consent:** Google User Messaging Platform (UMP)
- **Privacy Policy:** https://policies.google.com/privacy

### Google User Messaging Platform (UMP)
- **Purpose:** GDPR/consent management before requesting ads
- **Privacy Policy:** https://policies.google.com/privacy

---

## Permissions (Play declarations)

| Permission | Why |
|------------|-----|
| `INTERNET` | Fetch facility hours; load ads |
| `ACCESS_NETWORK_STATE` | AdMob / connectivity checks |

No location, camera, microphone, contacts, SMS, or notification permissions are requested.

---

## Data Security

- **In transit:** HTTPS only (`android:usesCleartextTraffic="false"`)
- **Deletion:** No cloud user account; uninstall clears local preferences. Ad identifiers are controlled via device settings.

---

## Children's Privacy

This app is **not** directed at children under 13 (COPPA) or under 16 (GDPR).

- UMP uses `setTagForUnderAgeOfConsent(false)`
- No child-directed ad serving is enabled

---

## Play Console Form Responses (quick fill)

1. **Does your app collect or share any of the required user data types?** Yes  
2. **Is all of the user data collected by your app encrypted in transit?** Yes  
3. **Do you provide a way for users to request that their data is deleted?** Yes (uninstall / device ad settings; no account)  
4. Declare **Device or other IDs** (Advertising ID) — Collected + Shared — Advertising  
5. Declare **App interactions** if prompted for AdMob — Collected + Shared — Advertising  

---

## Contact

For privacy inquiries, contact the developer via the email address listed on the Google Play Store listing.
