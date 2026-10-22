# Minimal OS Launcher — Master Plan & Documentation

> **Project:** Minimal OS Launcher (Minimal OS)  
> **Version:** 1.7.8  
> **Author:** MD. Zarif Azfar  
> **Date:** October 22, 2026  
> **Repository:** `github.com/zaryif/Minimal_os`  

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Business Requirements Document (BRD)](#2-business-requirements-document-brd)
3. [Software Requirements Specification (SRS)](#3-software-requirements-specification-srs)
4. [Software Architecture Document (SAD)](#4-software-architecture-document-sad)
5. [Management Information System (MIS)](#5-management-information-system-mis)
6. [Feature Catalog](#6-feature-catalog)
7. [UI/UX Design Specification](#7-uiux-design-specification)
8. [Build & Release Plan](#8-build--release-plan)

---

## 1. Executive Summary

Minimal OS Launcher is an **intentional, text-first, terminal-inspired Android home launcher** designed to reclaim user attention. It replaces the conventional icon grid with a clean, dark, typography-driven interface where **typing is the primary interaction model**. Users type symbol-prefixed commands or natural-language phrases to send messages, set alarms, create notes, start live Pomodoro timers, search YouTube, dispatch AI queries, uninstall applications, and make calls — all without opening a single app.

### Core Philosophy
- **Type to do things:** A single unified line of text replaces hunting through nested graphical app menus.
- **One hub for everything:** Messages, calls, emails, and notifications unified with zero tracking.
- **Intentional by design:** Reclaim attention through text-first interaction, anti-dopamine layouts, and customizable focus.
- **Zero visual noise:** High-contrast monochromatic typography, adaptive luminance, and zero emojis.

---

## 2. Business Requirements Document (BRD)

### 2.1 Business Objectives

| ID | Objective | Success Metric |
|----|-----------|----------------|
| BO-1 | Provide a distraction-free Android launcher | < 2 seconds to initiate any task |
| BO-2 | Reduce screen time for users | 30%+ screen time reduction reported |
| BO-3 | Open-source community-driven development | Clean, extensible, MIT-licensed architecture |
| BO-4 | Support all Android 8.0+ devices | 95%+ Android device compatibility |
| BO-5 | Strictly zero emoji visual clutter | 100% clean typography across all screens |

### 2.2 Stakeholders

| Stakeholder | Role | Interest |
|-------------|------|----------|
| End Users | Primary users | Distraction-free, intentional phone experience |
| Developer (Zarif) | Creator & maintainer | Portfolio project, community building |
| Digital Minimalism Community | Advocates | Alternative to mainstream dopamine-heavy launchers |

### 2.3 Business Rules

| ID | Rule |
|----|------|
| BR-1 | The launcher must be 100% free and open-source (MIT License) |
| BR-2 | No ads, no tracking, no telemetry, no cloud dependencies |
| BR-3 | APK size must remain under 10 MB |
| BR-4 | Must function as a complete home screen replacement |
| BR-5 | Must work completely offline for all core functions |

### 2.4 Scope

#### In Scope (v1.7.8)
- Complete Home screen replacement with live clock and localized date
- Unified Command Bar with 11 symbol prefixes and natural-language keyword recognition
- App Uninstallation via command (`uninstall <app>`, `/<app>`) with interactive suggestion badges
- AI Assistant dispatch (`assistant <prompt>`, `?<prompt>`) to Gemini, ChatGPT, Claude, Copilot
- YouTube direct search (`yt <query>`, `$<query>`) with app and browser fallback
- Natural Pomodoro countdown (`pomo <duration>`, `%<duration>`) with live home screen widget
- Home Screen Live Alarm Countdown Widget tracking the exact time until the next system alarm
- Dedicated Widgets Deck (Pomodoro, Custom Alarms, Minimal Memo, Shortcuts, Timers, AppWidgetHost)
- Terminal History card stream with persistent markdown export (`minimal_command_history.md`)
- Handcrafted Theme Engine (7 themes) with `MinimalOnAccent` adaptive contrast engine
- App Drawer with instant search, favorites pinning, and app hiding
- Unified Notification Hub with category filtering
- Interactive Setup Wizard with live theme selection and preference customization
- Replayable onboarding wizard accessible from Settings

#### Out of Scope (v1.7.8)
- Cloud-based account synchronization (data remains strictly local)
- Closed-source analytics SDKs or advertising networks

### 2.5 Constraints

| Type | Constraint |
|------|-----------|
| Technical | Android SDK 26+ (Android 8.0 Oreo) through SDK 35 (Android 15) |
| Technical | Kotlin 2.0+ and Jetpack Compose UI framework |
| Design | Strict typography-first monochrome and high-contrast dark themes |
| Legal | MIT License, no proprietary dependencies |

---

## 3. Software Requirements Specification (SRS)

### 3.1 Functional Requirements

#### 3.1.1 Home Screen (FR-HOME)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-HOME-01 | Display current time in large typography | P0 |
| FR-HOME-02 | Display current date below time | P0 |
| FR-HOME-03 | Show command input bar at bottom of screen | P0 |
| FR-HOME-04 | Command bar activates on any key press | P0 |
| FR-HOME-05 | Swipe up from home reveals full app list | P0 |
| FR-HOME-06 | Swipe right from home opens Hub | P1 |
| FR-HOME-07 | Live Pomodoro Countdown Widget on home screen | P0 |
| FR-HOME-08 | Live Alarm Countdown Widget on home screen | P0 |

#### 3.1.2 Command Bar & Execution Engine (FR-CMD)

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-CMD-01 | `uninstall` or `/` prefix -> uninstalls application via native system dialog | P0 |
| FR-CMD-02 | `assistant` or `?` prefix -> dispatches prompt to AI assistant | P0 |
| FR-CMD-03 | `yt` or `$` prefix -> searches YouTube app or web | P0 |
| FR-CMD-04 | `pomo` or `%` prefix -> parses duration naturally and starts live Pomodoro | P0 |
| FR-CMD-05 | `@` or `msg` prefix -> composes SMS message | P0 |
| FR-CMD-06 | `#` or `call` prefix -> initiates phone call | P0 |
| FR-CMD-07 | `-` or `todo` prefix -> creates to-do item in Minimal Memo | P0 |
| FR-CMD-08 | `!` or `note` prefix -> creates note in Minimal Memo | P0 |
| FR-CMD-09 | `*` or `event` prefix -> creates calendar event | P0 |
| FR-CMD-10 | `+` or `timer` prefix -> sets countdown timer | P0 |
| FR-CMD-11 | `:` or `alarm` prefix -> sets clock alarm | P0 |
| FR-CMD-12 | `alias <name> = <target>` -> creates user-defined shortcut | P0 |
| FR-CMD-13 | Interactive command history card stream with Up/Down navigation | P0 |
| FR-CMD-14 | Continuous markdown history logging (`minimal_command_history.md`) with SAF export | P0 |
| FR-CMD-15 | Real-time app search suggestions with dedicated UNINSTALL badge in uninstall mode | P0 |

---

## 4. Software Architecture Document (SAD)

### 4.1 Architecture Principles
Minimal OS Launcher is architected around **Clean Architecture + Unidirectional Data Flow (UDF)** using **Jetpack Compose** and **Kotlin Coroutines / Flow**.

```
com.zaryif.minimalos
├── data
│   ├── model          # Note, Todo, Notification, App, Theme models
│   ├── preferences    # Jetpack DataStore preferences (Theme, Home Widgets, Aliases, History)
│   ├── repository     # StorageRepository, ContactRepository, NotificationRepository
│   └── service        # NotificationListenerService
├── domain
│   ├── parser         # CommandParser (Symbol & natural-language parsing)
│   ├── timer          # PomodoroTimerManager
│   └── usecase        # ExecuteCommandUseCase
└── ui
    ├── apps           # AppDrawerScreen
    ├── components     # CommandBar, MinimalCard, MinimalMemoSheet, Dialogs
    ├── home           # HomeScreen & Home Widgets (Live Pomodoro, Live Alarm)
    ├── onboarding     # OnboardingScreen
    ├── settings       # SettingsScreen (organized by top-brand guidelines)
    ├── theme          # Adaptive Theme, Color, Type
    └── widgets        # WidgetsDeckScreen (Pomodoro, Alarms, Aliases, Timers)
```

---

## 5. Management Information System (MIS)

### 5.1 Project Management & Milestones

| Milestone | Scope | Deliverables | Status |
|:---|:---|:---|:---:|
| **M1: Foundation** | Project setup, Gradle configuration, Theme System | `build.gradle.kts`, Color/Type/Theme | Complete |
| **M2: Core Features** | Command bar grammar, Execution engine, Home screen | `HomeScreen`, `CommandBar`, `ExecuteCommandUseCase` | Complete |
| **M3: App Drawer & Hub** | PackageManager query, Notification service, Hub UI | `AppDrawerScreen`, `HubScreen`, `MinimalNotificationService` | Complete |
| **M4: Settings & Mini Apps** | Settings Screen, Memo/Clock/Ask sheets, Storage | `SettingsScreen`, `MiniAppsSheets`, `StorageRepository` | Complete |
| **M5: Onboarding & Specs** | 5-step wizard, BRD, SRS, SAD, MIS documentation | `OnboardingScreen`, Complete `docs/` suite | Complete |
| **M6: Widgets Deck & Live Countdowns** | Modular Widgets Deck, Home Live Pomodoro & Alarm widgets | `WidgetsDeckScreen`, `PomodoroTimerManager`, Live widgets | Complete |
| **M7: Theme Engine & Adaptive Contrast** | 7 bespoke themes, `MinimalOnAccent` luminance engine | Adaptive WCAG AAA contrast, Nordic Snow contrast engine | Complete |
| **M8: Power Commands & Terminal Logging (v1.7.8)** | `uninstall <app>`, `assistant <prompt>`, `yt <query>`, `pomo <duration>`, markdown history download, zero-emoji policy | Terminal history SAF exporter, zero-emoji verification, v1.7.8 release | Complete |

---

## 6. Feature Catalog

### 6.1 Command Symbol Reference

| Symbol / Keyword | Action | Example | Result |
|:---|:---|:---|:---|
| `uninstall` or `/` | Uninstall App | `uninstall chatgpt` | Triggers native system deletion confirmation dialog |
| `assistant` or `?` | AI Assistant | `assistant what is gravity` | Dispatches prompt directly to Gemini, ChatGPT, Claude |
| `yt` or `$` | YouTube Search | `yt lofi beats` | Opens search query in YouTube Android app |
| `pomo` or `%` | Live Pomodoro | `pomo 50m` | Naturally parses time and starts live Pomodoro widget |
| `@` or `msg` | Send Message | `@Mom dinner at 7` | Opens system SMS composer to Mom |
| `#` or `call` | Make Call | `#Mom` | Initiates phone call to Mom |
| `-` or `todo` | Create To-Do | `-Buy groceries` | Saves item to Minimal Memo to-do list |
| `!` or `note` | Create Note | `!Meeting notes...` | Saves note to Minimal Memo |
| `*` or `event` | Create Event | `*Lunch 12pm Friday` | Opens calendar with pre-filled event |
| `+` or `timer` | Set Timer | `+5m` | Starts system countdown timer |
| `:` or `alarm` | Set Alarm | `:7:30am` | Configures clock alarm for 7:30 AM |
| `alias` | Custom Shortcuts | `alias wifi = ...` | Creates custom user-defined shortcut |
| _(app name)_ | Search Apps | `spotify` | Finds and launches Spotify |
| _(query)_ | Web Search | `weather today` | Searches query in default browser |

### 6.2 Interactive Setup Wizard & Onboarding

| Step | Focus Area | User Value |
|------|------------|------------|
| 1. Welcome & Philosophy | Digital minimalism, text-first paradigm | Understands why Minimal OS exists and how it reclaims time |
| 2. The Command Box | Unified terminal shortcuts (`uninstall`, `assistant`, `yt`, `pomo`, symbols) | Learns to execute daily actions in under 2 seconds |
| 3. Widgets Deck | Live Alarm Countdown, Live Pomodoro, Minimal Memo, AppWidgetHost | Discovers modular productivity utilities without icon clutter |
| 4. Notification Hub | Category-filtered notification stream | Single calm place to review alerts one swipe right |
| 5. Setup Wizard | Live theme picker, home widget toggles, history switch, assistant | Personalizes launcher preferences immediately before home screen |

### 6.3 Hub Notification Categories

| Tab | Content | Source |
|-----|---------|--------|
| ALL | Every notification | All apps |
| MESSAGE | SMS, WhatsApp, Telegram, etc. | Messaging apps |
| CALLS | Missed calls, voicemail | Phone app |
| EMAIL | Gmail, Outlook, etc. | Email apps |
| APPS | App-specific notifications | All other apps |
| FLAGGED | User-pinned notifications | Manual flagging |

---

## 7. UI/UX Design Specification

### 7.1 Design Tokens

```
Background:       #000000 (Pure Black)
Text Primary:     #FFFFFF (Pure White)
Text Secondary:   #888888 (Gray)
Accent:           #FF3B30 (Crimson Red - configurable across 7 themes)
Card Border:      #262626 (Dark Gray)
Card Fill:        #121212 (Near Black)
Adaptive Text:    MinimalOnAccent (#000000 on light accents, #FFFFFF on dark accents)
```

### 7.2 Component Patterns
- **Buttons:** Crisp borders, transparent or accent fills, high-contrast typography, rounded corners (8dp).
- **Cards:** Dark fill, subtle borders, rounded corners (12dp), zero decorative dropshadows.
- **Syntax Highlighting:** Only trigger keywords and symbols are highlighted in bold accent color; payload text remains crisp white.
- **Zero Emojis:** Completely clean typography across all UI components and system notices.

---

## 8. Build & Release Plan

### 8.1 Repository Structure

```
Minimal_os/
├── .github/
│   └── workflows/
│       └── build.yml          # GitHub Actions CI/CD
├── app/
│   └── ... (Android source)
├── docs/
│   ├── BRD.md
│   ├── SRS.md
│   ├── SAD.md
│   ├── MIS.md
│   └── Minimal_OS_Launcher_Master_Plan.md
├── screenshots/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
├── LICENSE
└── .gitignore
```

### 8.2 Release Strategy
1. **Build:** GitHub Actions auto-builds debug and release APKs on every tag push.
2. **Tag:** Release tagged with semantic versioning (`v1.7.8`).
3. **Distribution:** Published directly on GitHub Releases page.

---

*Minimal OS Launcher — zaryif/Minimal_os*
