# Google Play Console - Data Safety Questionnaire Answers

Use these exact answers when completing the "Data safety" section in Google Play Console (Policy and programs > Data safety).

---

## Page 1: Data Collection and Sharing

1. Does your app collect or share any of the required user data types?
   -> Select: "No"
   (Minimal OS operates entirely offline with zero third-party telemetry, ads, or remote databases.)

2. Is all of the user data collected by your app encrypted in transit?
   -> Automatically answered as Not Applicable (since No data is collected).

3. Do you provide a way for users to request that their data is deleted?
   -> Select: "Yes"
   (Explain in deletion mechanism: Users can wipe all local application data, notes, and preferences at any time by clearing storage in Android System Settings > Apps > Minimal OS > Storage > Clear Storage, or by uninstalling the application.)

---

## Page 2: Data Types Breakdown (Confirmation)

Verify that all data categories are unchecked:
- Location: No
- Personal info: No
- Financial info: No
- Health and fitness: No
- Messages: No (Notification Hub reads locally via NotificationListenerService without transmitting)
- Photos and videos: No
- Audio files: No
- Files and docs: No
- Calendar: No (Calendar events are created locally on device calendar provider)
- Contacts: No (Resolved locally on device for dialer shortcuts)
- App activity: No
- Web browsing: No
- App info and performance: No
- Device or other IDs: No

---

## Page 3: Preview and Submit

Review the summary screen:
- "No data shared with third parties"
- "No data collected"
- "Data is not collected, so security practices do not apply"
Click "Save" and submit.
