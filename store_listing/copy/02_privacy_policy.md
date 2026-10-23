# Privacy Policy for Minimal OS Launcher

Last Updated: October 23, 2026

Minimal OS ("we", "our", or "the application") is committed to protecting your privacy. This Privacy Policy explains our practices regarding data collection, usage, and disclosure when you use the Minimal OS Android application.

## 1. Zero Data Collection Principle
Minimal OS is designed with a strict offline-first, zero-telemetry architecture. We do NOT collect, transmit, store, sell, or share any personal data, usage metrics, diagnostic reports, or behavioral analytics.

## 2. Information Handled Locally on Your Device
All information created or handled by the user remains entirely on the local device storage:
- Tasks and Notes: Stored locally in private application sandbox storage using encrypted Android DataStore and SQLite databases.
- Command History: Stored locally if enabled in settings and never transmitted to external servers.
- Pinned Apps and Theme Settings: Stored strictly on device.

## 3. Permissions Requested and Purpose
Minimal OS requests only standard Android permissions necessary to perform its core functions as a home screen launcher:
- Query All Packages (QUERY_ALL_PACKAGES): Required for launchers to index and launch installed applications from the text-based app drawer.
- Notification Listener Service (Optional): Used solely to display unified alerts inside the on-device Notification Hub. No notification contents are stored remotely or transmitted outside your device.
- Calendar Read / Write (Optional): Used only when you schedule events via the calendar widget or the "*" event command.
- Set Alarms / Timers (Optional): Used to dispatch local system alarms or timers via the ":" and "+" commands.
- Call / Contacts (Optional): Used solely to resolve contact names when using "#" or "@" shortcuts. Contacts are never uploaded to any server.

## 4. Third-Party Services and SDKs
Minimal OS contains:
- ZERO advertising SDKs (No AdMob, Unity Ads, etc.)
- ZERO analytics SDKs (No Firebase Analytics, Mixpanel, Segment, Facebook SDK, etc.)
- ZERO crash logging SDKs that collect identifiable data (No Crashlytics, Sentry, Bugsnag, etc.)

## 5. Network Access
Minimal OS does not require an active internet connection to function. If you use the command line to trigger a web search, the search query is passed directly to your system default web browser (such as Google Chrome) via a standard Android Intent.

## 6. Children's Privacy
Because Minimal OS collects no data whatsoever, it complies with the Children's Online Privacy Protection Act (COPPA) and GDPR regulations. It is safe for users of all ages.

## 7. Changes to This Privacy Policy
We may periodically update this policy to reflect application enhancements. Any revisions will be published with an updated effective date in our official repository.

## 8. Contact Us
For any questions regarding this Privacy Policy or the security of Minimal OS, you can contact the developer via GitHub:
https://github.com/zaryif/Minimal_os
