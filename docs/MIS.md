# Management Information System (MIS)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 22, 2026  
**Version:** 1.8.0  

---

### 1. Project Management & Delivery Timeline
The project delivery follows an agile, sprint-based release progression:

| Milestone | Scope | Deliverables | Status |
|:---|:---|:---|:---:|
| **M1: Foundation** | Project setup, Gradle configuration, Theme & Design System | `settings.gradle.kts`, `build.gradle.kts`, Color/Type/Theme | Complete |
| **M2: Core Features** | Command bar grammar, Execution engine, Home screen | `HomeScreen`, `CommandBar`, `ExecuteCommandUseCase` | Complete |
| **M3: App Drawer & Hub** | PackageManager query, Notification service, Hub UI | `AppDrawerScreen`, `HubScreen`, `MinimalNotificationService` | Complete |
| **M4: Settings & Mini Apps** | Settings Screen, Memo/Clock/Ask sheets, Storage | `SettingsScreen`, `MiniAppsSheets`, `StorageRepository` | Complete |
| **M5: Onboarding & Specs** | 5-step wizard, BRD, SRS, SAD, MIS documentation | `OnboardingScreen`, Complete `docs/` suite | Complete |
| **M6: Widgets Deck & Live Countdowns** | Modular Widgets Deck, Home Live Pomodoro & Alarm widgets | `WidgetsDeckScreen`, `PomodoroTimerManager`, Live widgets | Complete |
| **M7: Theme Engine & Adaptive Contrast** | 7 bespoke themes, `MinimalOnAccent` luminance engine | Adaptive WCAG AAA contrast, Nordic Snow contrast engine | Complete |
| **M8: Power Commands & Terminal Logging (v1.7.8)** | `uninstall <app>`, `assistant <prompt>`, `yt <query>`, `pomo <duration>`, markdown history download, zero-emoji policy | Terminal history SAF exporter, zero-emoji verification, v1.7.8 release | Complete |
| **M9: Widget Deduplication & Quick Duration (v1.7.9)** | Home Screen Pomodoro deduplication, custom duration button on home card, distinct widget storage | Single-instance Pomodoro enforcement, DataStore distinct filter, v1.7.9 release | Complete |
| **M10: Branding, Aliases Directory & Alarm Permissions (v1.8.0)** | Official Minimal OS logo & adaptive icon suite across all densities, 148+ aliases reference directory, default off alarm countdown, and automatic exact alarm permission handling | Adaptive icons, ALIASES_AND_SHORTCUTS.md, AlarmClockPermissionHelper, v1.8.0 release | Complete |

---

### 2. Quality Assurance & Testing Metrics

- **Static Code Analysis:** Android Lint + Kotlin compiler strict check.
- **Zero-Emoji Compliance:** Automated Python unicode scanner enforcing 0 emoji characters across all `.kt`, `.xml`, `.json`, and `.md` project files.
- **Device Compatibility:** Tested across Android API 26 (8.0 Oreo) through API 35 (Android 15).
- **Crash Rate Target:** < 0.1% of user sessions.
- **ANR (Application Not Responding):** Zero ANR threshold — all I/O, package manager queries, and file persistence executed asynchronously via Kotlin Coroutines.
- **Build Verification:** Successful compilation for both `assembleDebug` and `assembleRelease`.

---

### 3. Risk Management & Mitigations

| Identified Risk | Severity | Likelihood | Mitigation Strategy |
|:---|:---:|:---:|:---|
| User declines Notification Listener permission | Medium | Medium | Graceful fallback showing guidance card in Hub with direct link to system settings |
| App uninstallation on system/pre-installed packages | Low | High | Detect `isSystemApp` flag and route user to App Info settings instead of failing silently |
| AI assistant application not installed | Low | Low | Hierarchical fallback: targeted assistant -> generic ACTION_ASSIST -> system web search |
| Storage permission restrictions for history export | Low | Low | Uses Storage Access Framework (`CreateDocument("text/markdown")`) requiring no broad storage permissions |
| White-on-white contrast failures in light/monochrome themes | Medium | Medium | Implemented `MinimalOnAccent` calculating relative luminance and dynamically inverting text/buttons |

---

### 4. Release & Deployment Pipeline

- **Continuous Integration:** GitHub Actions (`ubuntu-latest`, JDK 17, Gradle 8.11).
- **Automated Artifacts:** Debug and unsigned Release APKs generated automatically on every tag and main push.
- **Direct Distribution:** GitHub Releases with direct APK download links and release notes.
