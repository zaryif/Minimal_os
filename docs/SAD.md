# Software Architecture Document (SAD)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 22, 2026  
**Version:** 1.7.9  

---

### 1. Architectural Style & Principles
Minimal OS Launcher is architected around **Clean Architecture + Unidirectional Data Flow (UDF)** using **Jetpack Compose** and **Kotlin Coroutines / Flow**.

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Presentation Layer                            │
│  HomeScreen (Live Pomodoro / Live Alarm Widgets) · AppDrawerScreen     │
│  HubScreen · SettingsScreen · WidgetsDeckScreen · OnboardingScreen    │
│  CommandBar (Syntax Highlighting, History Stream) · MinimalMemoSheet  │
└───────────────────────────────────▲────────────────────────────────────┘
                                    │ (StateFlow / Events)
┌───────────────────────────────────┴────────────────────────────────────┐
│                             Domain Layer                               │
│  CommandParser · ExecuteCommandUseCase · PomodoroTimerManager          │
│  Models: AppInfo, Command, CommandType, NotificationItem, Note, Todo   │
└───────────────────────────────────▲────────────────────────────────────┘
                                    │ (Repositories & Services)
┌───────────────────────────────────┴────────────────────────────────────┐
│                              Data Layer                                │
│  AppRepository · StorageRepository · ContactRepository                 │
│  UserPreferences (DataStore & minimal_command_history.md logging)      │
│  NotificationListenerService · AppWidgetHost                           │
└────────────────────────────────────────────────────────────────────────┘
```

---

### 2. Module & Package Decomposition

- `com.zaryif.minimalos`:
  - `ui/`:
    - `theme/`: Theme tokens (Colors, Typography, Material3 ColorScheme, `MinimalOnAccent` adaptive luminance calculator).
    - `components/`: Atomic widgets (`MinimalButton`, `MinimalCard`, `CommandBar`, `CommandHelpDialog`, `MinimalClockSheet`, `MinimalMemoSheet`, `WidgetsDialogs`).
    - `home/`: Home screen with clock, live countdown widgets (Pomodoro and Alarm), and bottom command bar.
    - `apps/`: App drawer with alphabetical listing, search filtering, favorites, and app hiding.
    - `hub/`: Unified notification hub screen with category filters (`ALL`, `MESSAGE`, `CALLS`, `EMAIL`, `APPS`, `FLAGGED`).
    - `settings/`: Launcher configuration, theme selector, widget toggles, command history `.md` download, and custom alias manager.
    - `widgets/`: Widgets deck screen containing Pomodoro, Custom Alarms, Memo, Aliases, Timers, and Native AppWidget host.
    - `onboarding/`: 5-step interactive introduction flow.
  - `domain/`:
    - `model/`: Immutable data models (`AppInfo`, `Command`, `CommandType`, `NotificationItem`, `Note`, `Todo`, `Contact`, `ThemeModel`).
    - `parser/`: `CommandParser` supporting symbol prefixes (`-`, `!`, `#`, `@`, `:`, `+`, `*`, `?`, `$`, `%`, `/`) and natural language keywords (`uninstall`, `assistant`, `yt`, `pomo`, etc.).
    - `timer/`: `PomodoroTimerManager` orchestrating background countdown loops and state broadcasts.
    - `usecase/`: `ExecuteCommandUseCase` routing parsed commands to system intents, local storage, or application launches.
  - `data/`:
    - `repository/`: `AppRepository` (PackageManager queries), `StorageRepository` (Notes & To-dos), `ContactRepository` (Contacts content provider).
    - `preferences/`: `UserPreferences` managing Jetpack DataStore preferences and continuous markdown file persistence (`minimal_command_history.md`).
    - `service/`: `NotificationListenerService` capturing incoming system notifications locally.

---

### 3. Data Flow & Command Dispatch Pipeline

1. **Keystroke Input:** User enters text into `CommandBar`.
2. **Grammar & Lexical Analysis:** `CommandParser.parse(input)` extracts the trigger symbol or natural prefix and isolates the payload argument.
3. **Execution Dispatch:** `ExecuteCommandUseCase.execute(input)` routes to the appropriate handler:
   - **App Uninstallation (`CommandType.UNINSTALL`):** Matches installed app by label or package name and dispatches `Intent(Intent.ACTION_DELETE, Uri.parse("package:<pkg>"))`. For system apps, falls back to `ACTION_APPLICATION_DETAILS_SETTINGS`.
   - **AI Assistant Routing (`CommandType.ASK`):** Transmits query via `ACTION_SEND` (`text/plain`, `EXTRA_TEXT`) or `ACTION_ASSIST` directly to installed assistants (Gemini, ChatGPT, Claude, Copilot, Perplexity, or OS default).
   - **YouTube Search (`CommandType.YOUTUBE`):** Targets `com.google.android.youtube` via `ACTION_SEARCH`/`ACTION_VIEW` with fallback to `https://www.youtube.com`.
   - **Natural Pomodoro (`CommandType.POMODORO`):** Parses duration strings (`50 minute`, `50m`, `25`), invokes `PomodoroTimerManager.start(seconds, mode)`, and activates home screen countdown widget.
   - **Telephony & Messaging (`CommandType.CALL`, `CommandType.MESSAGE`, `CommandType.WHATSAPP`):** Resolves contacts and dispatches `ACTION_DIAL`, `ACTION_SENDTO`, or WhatsApp deep links.
   - **Clock & Calendar (`CommandType.ALARM`, `CommandType.TIMER`, `CommandType.EVENT`):** Dispatches to `AlarmClock.ACTION_SET_ALARM`, `AlarmClock.ACTION_SET_TIMER`, or `CalendarContract.ACTION_INSERT`.
   - **Notes & To-Dos (`CommandType.NOTE`, `CommandType.TODO`):** Persisted directly into `StorageRepository` local JSON storage.
   - **Custom Aliases:** Resolves user-defined aliases stored in `UserPreferences`.
   - **App Launch & Web Search:** Plain text matching installed apps triggers launch intent; non-matches default to web search.
4. **History Logging:** When history is enabled, input is added to DataStore buffer and asynchronously saved to `minimal_command_history.md`.
5. **UI Notification Banner:** Result returned as `CommandResult` sealed class, rendered as a top snackbar notification on the home screen.

---

### 4. Security, Permissions & Storage Architecture

- **Launcher Intents:** Declares `CATEGORY_HOME` and `CATEGORY_DEFAULT` in `AndroidManifest.xml`.
- **Uninstallation Intent:** Standard `Intent.ACTION_DELETE` prompts the native Android OS confirmation dialog, requiring zero privileged permissions or device admin rights.
- **Package Visibility:** Standard `QUERY_ALL_PACKAGES` permission allows querying launchable applications on Android 11+ (API 30+).
- **Storage Access Framework (SAF):** Exporting terminal history (`minimal_command_history.md`) utilizes `ActivityResultContracts.CreateDocument("text/markdown")`, granting scoped write access to the user's chosen directory without requiring broad external storage permissions.
- **Privacy Guarantees:** 100% on-device local storage. No network telemetry, no analytics beacons, and no tracking libraries.
