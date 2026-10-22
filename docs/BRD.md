# Business Requirements Document (BRD)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 22, 2026  
**Status:** Approved  
**Version:** 1.7.8  

---

### 1. Executive Summary & Purpose
Modern smartphones subject users to endless dopamine feedback loops driven by bright icons, notification badges, and algorithmically optimized feed grids. Minimal OS Launcher replaces this paradigm with a **pure text-driven, distraction-free home experience** inspired by the Minimal Phone and T-UI terminal launchers.

With version 1.7.8, Minimal OS expands its unified command-line interface to support native application uninstallation (`uninstall <app>`), direct AI assistant dispatch (`assistant <prompt>`), instant YouTube searches (`yt <query>`), natural-language Pomodoro timers (`pomo 50m`), persistent terminal markdown history logging with in-app export, and live home screen countdown widgets.

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
#### 4.1 In-Scope (v1.7.8)
- Minimalist text-first home launcher with live clock, localized date, and live widget countdowns.
- Unified Command Bar with prefix symbols (`-`, `!`, `#`, `@`, `:`, `+`, `*`, `?`, `$`, `%`, `/`) and natural keywords.
- App Uninstallation Command: `uninstall <app_name>` with fuzzy matching, interactive app suggestions, and native confirmation dialog.
- Digital Assistant Routing: `assistant <prompt>` dispatching queries directly to installed AI assistants (Gemini, ChatGPT, Claude, Copilot, Perplexity, or OS default).
- YouTube Direct Search: `yt <query>` with native app dispatch and browser fallback.
- Natural Pomodoro Live Timer: `pomo <duration>` parsing minutes/hours naturally and launching live home countdowns.
- Terminal History Stream: Interactive buffer cards with Up/Down arrow controls and persistent markdown storage (`minimal_command_history.md`) downloadable via Storage Access Framework.
- Dedicated Widgets Deck: Live Pomodoro Card, Custom Alarm & Countdown Card, Minimal Memo (notes and to-dos with search), Aliases & Shortcuts Deck, Countdown Timers, and Native Android App Widget hosting.
- Home Screen Live Widgets: Live Alarm Countdown Widget and Live Pomodoro Widget.
- Handcrafted Themes: 7 bespoke palettes with `MinimalOnAccent` adaptive contrast engine.
- Unified Notification Hub: Grouping notifications into filter categories with zero telemetry.
- Restriction Modes: Monk Mode, Focus Mode, and Unrestricted Mode.
- Re-openable Onboarding Wizard: Accessible during initial setup and replayable anytime from Settings.

#### 4.2 Out-of-Scope (v1.7.8)
- Cloud-based account synchronization or telemetry tracking (data remains strictly local).
- Proprietary closed-source libraries or advertising SDKs.

### 5. Constraints & Compliance
- **Platform:** Android 8.0 Oreo (API 26) through Android 15 (API 35).
- **Design:** Strict typography-first monochrome and high-contrast dark themes with WCAG AAA compliance.
- **Zero Emojis:** Strictly no emoji characters in code, resources, user interface, or documentation.
- **License:** MIT Open Source License.
