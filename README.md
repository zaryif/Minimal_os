# 📱 Minimal OS Launcher

[![Build Minimal OS Launcher APK](https://github.com/zaryif/Minimal_os/actions/workflows/build.yml/badge.svg)](https://github.com/zaryif/Minimal_os/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-black.svg?logo=android)](https://www.android.com)

> A distraction-free, text-first Android home launcher inspired by the **Minimal Phone** design paradigm with full terminal-like symbol capability (T-UI style).
>
> **Type to do things. One hub for everything. Zero visual noise.**

---

## 📸 Screenshots

| 1. Your Apps | 2. Minimal Ask | 3. Type to do things | 4. Hub | 5. Restrictions |
|:---:|:---:|:---:|:---:|:---:|
| <img src="screenshot/03F4F184-35F3-430D-B353-1B753FA0892A.PNG" width="160"/> | <img src="screenshot/07A9A71C-F7E4-40D7-B216-357554EE58A8.PNG" width="160"/> | <img src="screenshot/16F82F2C-C4CD-488A-9C0D-A311AE745880.PNG" width="160"/> | <img src="screenshot/227BA325-9E2F-48AD-A9B7-CA84D707FA01.PNG" width="160"/> | <img src="screenshot/443F9313-5212-4D83-AC25-90ECE083B0E2.PNG" width="160"/> |

---

## ✨ Core Features

### ⌨️ 1. Type to Do Things (Command Bar)
From your home screen, a symbol and a line is the whole job. Nothing to open, nowhere to navigate:

| Symbol | Command | Example | What Happens |
|:---:|:---|:---|:---|
| `@` | **message** | `@Mom dinner at 7` | Opens SMS composer pre-filled |
| `#` | **call** | `#+1234567890` | Dials number or contact |
| `-` | **to-do** | `-buy groceries` | Adds item to Minimal Memo todo list |
| `!` | **note** | `!project ideas...` | Adds note into Minimal Memo |
| `*` | **event** | `*team sync tomorrow` | Opens calendar event creation |
| `+` | **timer** | `+10m` | Sets a 10-minute countdown timer |
| `:` | **alarm** | `:7:00am` | Sets alarm for 7:00 AM |
| `?` | **ask** | `?weather forecast` | Fast web query without navigating |
| _(none)_ | **search apps** | `spotify` | Real-time app search & instant launch |

---

### 📬 2. Unified Hub (One Swipe Right)
Messages, calls, mail, and app alerts in one list, so there is one place to look rather than nine.
- **Tabs:** `ALL` · `MESSAGE` · `CALLS` · `EMAIL` · `APPS` · `FLAGGED`
- Red accent indicator, relative timestamps (`now`, `5m`, `1h`), and tap to open.
- Swipe left to return to Home.

---

### 🛡️ 3. Restriction Modes
Set up your digital diet to eliminate mindless phone habits:
- **Monk Mode:** Essentials only, grayscale always.
- **Focus Mode:** Everything available, distracting apps on a clock.
- **Unrestricted:** Everything, no guardrails.

---

### 🧰 4. Built-in Minimal Mini Apps
- **Minimal Settings:** Configure restrictions, remap symbols, set default launcher.
- **Minimal Ask:** Distraction-free query assistant.
- **Minimal Clock:** Clean typography clock, date, and quick timer/alarm shortcuts.
- **Minimal Memo:** Fast scratchpad with to-do checkmarks and notes.

---

## 📥 Download APK

**⭐ [Download Latest Stable APK (v1.6.0)](https://github.com/zaryif/Minimal_os/releases/download/v1.6.0/MinimalOS-v1.6.0.apk)**

You can also browse all builds directly from:
- **[GitHub Latest Release](https://github.com/zaryif/Minimal_os/releases/latest)** (Recommended)
- **[GitHub Actions Artifacts](https://github.com/zaryif/Minimal_os/actions)** (Automated latest build)

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 2.0+
- **UI:** Jetpack Compose (Material3)
- **Architecture:** MVVM + Clean Architecture
- **Storage:** Android DataStore Preferences + SharedPreferences JSON
- **System Services:** `NotificationListenerService`, `PackageManager`, `AlarmClock`, `CalendarContract`
- **Target SDK:** 35 (Android 15)
- **Minimum SDK:** 26 (Android 8.0 Oreo)

---

## 📖 Specifications & Architecture Documents

Full documentation suite available in the [`docs/`](docs/) directory:
- [Business Requirements Document (BRD)](docs/BRD.md)
- [Software Requirements Specification (SRS)](docs/SRS.md)
- [Software Architecture Document (SAD)](docs/SAD.md)
- [Management Information System (MIS)](docs/MIS.md)
- [Master Plan](docs/Minimal_OS_Launcher_Master_Plan.md)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
Created with ❤️ by **MD. Zarif Azfar** ([@zaryif](https://github.com/zaryif)).
