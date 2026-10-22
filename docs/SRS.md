# Software Requirements Specification (SRS)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 22, 2026  
**Version:** 1.7.9  

---

### 1. Functional Requirements (FR)

#### 1.1 Home Screen (FR-HOME)
- **FR-HOME-01:** System shall render current time in bold minimal sans-serif typography.
- **FR-HOME-02:** System shall display lowercase localized date beneath the clock.
- **FR-HOME-03:** System shall host a persistent command bar anchored to the bottom.
- **FR-HOME-04:** System shall support swipe-up gesture to transition to the App Drawer.
- **FR-HOME-05:** System shall support swipe-right gesture to transition to the Notification Hub.
- **FR-HOME-06:** System shall provide quick header shortcuts to Minimal Ask, Clock, Memo, and Settings.
- **FR-HOME-07:** System shall provide configurable Home Screen Live Widgets (Live Pomodoro Countdown Widget and Live Alarm Countdown Widget).
- **FR-HOME-08:** Live Alarm Countdown Widget shall query the next scheduled system alarm via `AlarmManager.getNextAlarmClock()` and compute real-time remaining countdown.
- **FR-HOME-09:** Live Pomodoro Countdown Widget shall feature single-instance deduplication (suppressing redundant floating live cards when pinned to home widgets) and provide an in-card custom duration selector.

#### 1.2 Command Bar & Execution Engine (FR-CMD)
- **FR-CMD-01 (Direct SMS):** Symbol `@` or keyword `msg`/`sms` + text -> Dispatch SMS composition intent to recipient or body.
- **FR-CMD-02 (Quick Call):** Symbol `#` or keyword `call`/`dial` + number/contact -> Dispatch phone dialer intent immediately.
- **FR-CMD-03 (To-Do Item):** Symbol `-` or keyword `todo`/`task` + text -> Save to-do item into persistent local storage.
- **FR-CMD-04 (Memo / Note):** Symbol `!` or keyword `note`/`memo` + text -> Save scratch note into persistent local storage.
- **FR-CMD-05 (Calendar Event):** Symbol `*` or keyword `event`/`cal` + text -> Dispatch calendar event creation intent.
- **FR-CMD-06 (Countdown Timer):** Symbol `+` or keyword `timer` + duration -> Start system countdown timer via `AlarmClock.ACTION_SET_TIMER`.
- **FR-CMD-07 (Clock Alarm):** Symbol `:` or keyword `alarm` + time -> Configure alarm via `AlarmClock.ACTION_SET_ALARM`.
- **FR-CMD-08 (AI Assistant Query):** Symbol `?` or keyword `assistant`/`ai`/`ask` + query -> Transmit prompt directly to configured AI assistant (Gemini, ChatGPT, Claude, Copilot, Perplexity, or OS default).
- **FR-CMD-09 (YouTube Direct Search):** Symbol `$` or keyword `yt`/`youtube` + query -> Search directly in YouTube Android app with web fallback.
- **FR-CMD-10 (Live Pomodoro Timer):** Symbol `%` or keyword `pomo`/`pomodoro` + duration -> Parse duration naturally (e.g. `pomo 50m`, `pomodoro 50 minute`) and start live Pomodoro countdown widget.
- **FR-CMD-11 (App Uninstallation):** Symbol `/` or keyword `uninstall`/`remove`/`rmapp` + app name -> Match installed application and trigger native Android `Intent.ACTION_DELETE` dialog.
- **FR-CMD-12 (Custom Aliases):** `alias <name> = <target>`, `unalias <name>`, and `alias list` -> Create, delete, and list custom user-defined shortcuts. Comprehensive reference directory available in [ALIASES_AND_SHORTCUTS.md](../ALIASES_AND_SHORTCUTS.md).
- **FR-CMD-13 (Terminal History Stream):** Interactive card stream showing past commands as numbered squared cards. Tapping any card reloads it into the input bar.
- **FR-CMD-14 (Terminal History Storage & Export):** Continuously persist command history to `minimal_command_history.md`. Provide a download button in Settings via Storage Access Framework (`CreateDocument("text/markdown")`).
- **FR-CMD-15 (Terminal History Toggle):** Allow users to enable or disable command history recording in Settings.
- **FR-CMD-16 (Terminal Up/Down Cycling):** Tactile Up and Down arrows beside input box step through command history with live badge indicator (`HIST 1/10`).
- **FR-CMD-17 (Inline Syntax Highlighting):** Highlight command triggers and aliases in bold theme accent color while parameters remain crisp white.
- **FR-CMD-18 (Interactive App & Uninstall Suggestions):** Filter installed apps in real-time. In uninstall mode, show dedicated `UNINSTALL` badge on matching apps.
- **FR-CMD-19 (Fuzzy App Launch & Web Search):** Plain app name launches the app; non-matching text defaults to web search in default browser.

#### 1.3 App Drawer (FR-APPS)
- **FR-APPS-01:** Query `PackageManager` for all activities matching `ACTION_MAIN` & `CATEGORY_LAUNCHER`.
- **FR-APPS-02:** Sort apps alphabetically in ascending order.
- **FR-APPS-03:** Provide real-time top search bar with instant substring filtering.
- **FR-APPS-04:** Tap on an app item immediately triggers launch intent.
- **FR-APPS-05:** Long-press provides actions: pin to favorites, hide app, or open app info.

#### 1.4 Notification Hub (FR-HUB)
- **FR-HUB-01:** Intercept status bar notifications locally via `NotificationListenerService`.
- **FR-HUB-02:** Group notifications into filter tabs: `ALL`, `MESSAGE`, `CALLS`, `EMAIL`, `APPS`, `FLAGGED`.
- **FR-HUB-03:** Format relative timestamps (`now`, `5m`, `1h`, `2d`).
- **FR-HUB-04:** Tap notification to open source application; swipe-to-dismiss support.
- **FR-HUB-05:** Swipe-left returns to Home screen.

#### 1.5 Widgets Deck Screen (FR-WIDGETS)
- **FR-WIDGETS-01:** Dedicated utility deck containing modular cards.
- **FR-WIDGETS-02 (Pomodoro Card):** Built-in 15m, 25m, 45m, 60m focus chips, custom duration picker dialog, work/break cycle tracker, and tactile controls.
- **FR-WIDGETS-03 (Alarm Card):** Custom time picker with Hour, Minute, AM/PM, Date selection, and quick relative presets (`+15m`, `+30m`, `+1h`, `+8h`).
- **FR-WIDGETS-04 (Minimal Memo):** Real-time search box to filter notes and tasks, plus context-aware inline creation bar.
- **FR-WIDGETS-05 (Aliases Deck):** Quick template creator and 1-tap launcher for custom shortcuts.
- **FR-WIDGETS-06 (Countdown Timers):** Quick presets (`1m`, `5m`, `10m`, `15m`, `25m`) dispatching to system clock.
- **FR-WIDGETS-07 (App Widget Host):** Embed standard Android third-party app widgets via `AppWidgetHost`.

#### 1.6 Handcrafted Themes & Adaptive Contrast (FR-THEME)
- **FR-THEME-01:** 7 curated themes: AMOLED Crimson, Apple Space Gray, Nordic Snow, Rabbit Amber, Matrix Forest, Midnight Obsidian, Titanium Purple.
- **FR-THEME-02 (Adaptive Luminance):** `MinimalOnAccent` calculates background luminance. On pure white accents (Nordic Snow), text renders in pure black (`#000000`), guaranteeing WCAG AAA legibility.

#### 1.7 Interactive Setup Wizard & Onboarding (FR-ONBOARD)
- **FR-ONBOARD-01:** 5-step introductory wizard on initial launch:
  1. Welcome & Digital Minimalism Philosophy.
  2. The Unified Command Box (uninstallation, AI routing, YouTube search, Pomodoro, symbol shortcuts).
  3. Widgets Deck & Live Countdowns (Alarm countdown, Pomodoro widget, Minimal Memo, AppWidgetHost).
  4. Unified Notification Hub (One swipe right, category filters).
  5. Setup Wizard (Live theme selector, home live widgets toggles, terminal history switch, digital assistant selector).
- **FR-ONBOARD-02:** Live theme switching in Setup Wizard immediately updates active theme palette.
- **FR-ONBOARD-03:** Store completion state and all customized preferences in `UserPreferences`.
- **FR-ONBOARD-04:** Replay onboarding option accessible at any time from Settings.

---

### 2. Non-Functional Requirements (NFR)
- **NFR-01 (Performance):** Cold start to interactive home screen under 600ms.
- **NFR-02 (Keystroke Latency):** Command bar input keystroke response under 16ms (60fps).
- **NFR-03 (Resource Efficiency):** Memory footprint under 45 MB RSS with negligible idle battery drain.
- **NFR-04 (Storage Footprint):** Release APK size under 8 MB.
- **NFR-05 (Accessibility):** High-contrast scheme meeting and exceeding WCAG AAA standards.
- **NFR-06 (Zero Emojis):** Strictly no emoji characters anywhere in code, UI, notifications, or documentation.
- **NFR-07 (Privacy):** 100% local operation with zero telemetry, zero analytics, and zero cloud tracking.
