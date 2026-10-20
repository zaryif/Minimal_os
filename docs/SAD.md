# Software Architecture Document (SAD)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 20, 2026  
**Version:** 1.0.0  

---

### 1. Architectural Style & Principles
Minimal OS Launcher is architected around **Clean Architecture + Unidirectional Data Flow (UDF)** using **Jetpack Compose** and **Kotlin Coroutines / Flow**.

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│   HomeScreen · AppDrawer · HubScreen · SettingsScreen  │
│   OnboardingScreen · CommandBar · MiniAppsSheets       │
└───────────────────────────▲────────────────────────────┘
                            │ (StateFlow / Events)
┌───────────────────────────┴────────────────────────────┐
│                      Domain Layer                      │
│   CommandParser · ExecuteCommandUseCase                │
│   Models: AppInfo, Command, NotificationItem, Note/Todo │
└───────────────────────────▲────────────────────────────┘
                            │ (Repositories)
┌───────────────────────────┴────────────────────────────┐
│                       Data Layer                       │
│   AppRepository · StorageRepository · UserPreferences  │
│   MinimalNotificationService (Android NLS)             │
└────────────────────────────────────────────────────────┘
```

### 2. Module & Package Decomposition
- `com.zaryif.minimalos`:
  - `ui/`:
    - `theme/`: Theme tokens (Colors, Typography, Material3 ColorScheme)
    - `components/`: Atomic widgets (MinimalButton, MinimalCard, CommandBar, MiniAppsSheets)
    - `home/`: Home screen with clock, gestures, and command bar
    - `apps/`: App drawer with alphabetical listing and filter
    - `hub/`: Unified notification hub screen
    - `settings/`: Launcher configuration and symbol reference
    - `onboarding/`: 5-step introduction flow
  - `domain/`:
    - `model/`: Immutable data models
    - `parser/`: Symbol-based token parsing logic
    - `usecase/`: Pure business logic for command execution
  - `data/`:
    - `repository/`: PackageManager querying, persistent JSON storage
    - `preferences/`: Jetpack DataStore Preferences
  - `service/`:
    - `MinimalNotificationService`: System notification capture service

### 3. Data Flow & Command Dispatch Pipeline
1. **User Keystroke:** User enters text into `CommandBar`.
2. **Grammar Parsing:** `CommandParser.parse(input)` extracts prefix symbol and argument token.
3. **Execution Dispatch:** `ExecuteCommandUseCase.execute(input)` routes to the appropriate handler:
   - SMS / Call / Calendar / Alarm / Timer → Android System Intent
   - Todo / Note → `StorageRepository` (persisted locally)
   - App Search → `PackageManager` launch intent
4. **UI Feedback:** Result returned as `CommandResult` sealed class, displayed via Snackbar or app launch.

### 4. Security & Permissions
- Launcher category intents: `CATEGORY_HOME`, `CATEGORY_DEFAULT`.
- Telephony intents: `ACTION_DIAL`, `ACTION_SENDTO` (minimizing dangerous runtime permission requests).
- Package visibility: `QUERY_ALL_PACKAGES` to resolve installed launchable applications.
- Notification access: Handled via standard Android `NotificationListenerService` user authorization.
