# Business Requirements Document (BRD)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 22, 2026  
**Status:** Approved  
**Version:** 1.8.0  

---

### 1. Executive Summary & Purpose
Modern smartphones subject users to endless dopamine feedback loops driven by bright icons, notification badges, and algorithmically optimized feed grids. Minimal OS Launcher replaces this paradigm with a **pure text-driven, distraction-free home experience** inspired by the Minimal Phone and T-UI terminal launchers.

With version 1.8.0, Minimal OS introduces the official retro-futuristic brand logo and full adaptive launcher icon suite across all screen densities, a comprehensive directory of 148+ tested custom aliases and system shortcuts with Android documentation references, an alarm clock countdown widget disabled by default for distraction-free onboarding, and automatic exact alarm / clock permission handling across all Android versions (Android 8.0 to Android 15+) and vendor skins.

### 2. Business Objectives
- **BO-1 (Digital Wellbeing):** Decrease compulsive phone checking by eliminating icon-based dopamine triggers and visual clutter.
- **BO-2 (High Efficiency):** Reduce task completion time (< 2 seconds to initiate calls, messages, notes, timers, alarms, uninstalls, or web searches).
- **BO-3 (Open-Source Community):** Provide an accessible, MIT-licensed launcher with zero tracking, zero telemetry, and complete privacy.
- **BO-4 (Lightweight Footprint):** Keep the APK under 10 MB with negligible idle battery consumption.
- **BO-5 (Zero Emoji Policy):** Maintain a clean, professional, distraction-free typographic aesthetic across all screens and system messages.

### 3. Target Audience & Stakeholders
- **Digital Minimalists & Deep Workers:** Seeking to curb doomscrolling and reclaim daily focus.
- **Terminal & Power Users:** Users who favor keyboard efficiency, natural-language commands, and custom aliases over navigating graphical menus.
- **Hardware Phone Enthusiasts:** Users of physical QWERTY devices (e.g. Unihertz, Minimal Phone, Symphony Q100) and standard touchscreens alike.
- **Privacy Advocates:** Demanding local-only data persistence with zero cloud dependencies or analytics.

### 4. Scope of the System
#### 4.1 In-Scope (v1.8.0)
- Official Minimal OS branding, app launcher icons, and adaptive icon layers (432px centered within 72dp safe zone).
- Directory of 148+ custom aliases, Android settings intents, URL search templates, and command macros (`ALIASES_AND_SHORTCUTS.md`).
- Alarm Clock Countdown widget disabled by default on setup/onboarding for a cleaner home layout.
- Automatic clock and exact alarm permission detection and user grant prompts (`AlarmClockPermissionHelper`) on Android 12 through Android 15+.
- Minimalist text-first home launcher with live clock, localized date, and live widget countdowns.
- Home Screen Pomodoro Deduplication: Guaranteed single instance rendering of Pomodoro timers.
- Home Screen Quick Custom Duration: Dedicated custom duration button on home Pomodoro card.
- Unified Command Bar with prefix symbols (`-`, `!`, `#`, `@`, `:`, `+`, `*`, `?`, `$`, `%`, `/`) and natural keywords.
- App Uninstallation Command: `uninstall <app_name>` with fuzzy matching and native confirmation dialog.
- Digital Assistant Routing: `assistant <prompt>` dispatching queries directly to installed AI assistants.
- YouTube Direct Search: `yt <query>` with native app dispatch and browser fallback.
- Natural Pomodoro Live Timer: `pomo <duration>` parsing minutes/hours naturally.
- Terminal History Stream: Interactive buffer cards with Up/Down arrow controls and persistent markdown storage (`minimal_command_history.md`).
- Dedicated Widgets Deck: Live Pomodoro Card, Custom Alarm Card, Minimal Memo, Aliases & Shortcuts Deck, Countdown Timers, and Native AppWidgets.
- Handcrafted Themes: 7 bespoke palettes with `MinimalOnAccent` adaptive contrast engine.
- Unified Notification Hub: Grouping notifications into filter categories with zero telemetry.
- Interactive Setup Wizard: Personalizing theme, home live widgets, terminal history, and default AI assistant.

#### 4.2 Out-of-Scope (v1.8.0)
- Cloud-based account synchronization or telemetry tracking (data remains strictly local).
- Proprietary closed-source libraries or advertising SDKs.

### 5. Constraints & Compliance
- **Platform:** Android 8.0 Oreo (API 26) through Android 15 (API 35).
- **Design:** Strict typography-first monochrome and high-contrast dark themes with WCAG AAA compliance.
- **Zero Emojis:** Strictly no emoji characters in code, resources, user interface, or documentation.
- **License:** MIT Open Source License.
