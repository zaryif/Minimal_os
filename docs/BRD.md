# Business Requirements Document (BRD)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 20, 2026  
**Status:** Approved  
**Version:** 1.0.0  

---

### 1. Executive Summary & Purpose
Modern smartphones subject users to endless dopamine feedback loops driven by bright icons, notification badges, and algorithmically optimized feed grids. The Minimal OS Launcher replaces this paradigm with a **pure text-driven, distraction-free home experience** inspired by the Minimal Phone and T-UI terminal launchers.

### 2. Business Objectives
- **BO-1 (Digital Wellbeing):** Decrease user compulsive phone checking by at least 30%.
- **BO-2 (High Efficiency):** Reduce task completion time (< 3 seconds to initiate a call, SMS, note, or timer).
- **BO-3 (Open-Source Community):** Provide an accessible, MIT-licensed launcher with zero tracking or telemetry.
- **BO-4 (Lightweight Footprint):** Keep the APK under 10 MB with negligible idle battery drain (< 2% per hour).

### 3. Target Audience & Stakeholders
- **Digital Minimalists & Deep Workers:** Seeking to curb doomscrolling and reclaim focus.
- **Terminal & Keyboard Enthusiasts:** Users of physical QWERTY phones (e.g. Unihertz, Minimal Phone, Symphony Q100) and power users who favor keyboard shortcuts over multi-screen navigation.
- **Privacy Advocates:** Demanding zero ads, zero analytics, and local-only data persistence.

### 4. Scope of the System
#### 4.1 In-Scope
- Minimalist text-first home launcher with live clock and date.
- Command bar with symbol prefix mapping (`@`, `#`, `-`, `!`, `*`, `+`, `:`, `?`).
- Full app list drawer with instant search filtering.
- Unified notification Hub grouping SMS, calls, emails, and alerts.
- Digital detox restriction modes (Monk Mode, Focus Mode, Unrestricted).
- Mini utility sheets: Minimal Memo (notes + to-dos), Minimal Clock, Minimal Ask.
- Onboarding walkthrough introducing gestures and command syntax.

#### 4.2 Out-of-Scope (v1.0)
- Third-party launcher widget embedding.
- Cloud account synchronization.
- Complex graphical theme engines.

### 5. Constraints & Compliance
- **Platform:** Android 8.0 Oreo (API 26) through Android 15 (API 35).
- **Design:** Strict monochrome dark palette (#000000 background, #FFFFFF text, #FF0000 accent).
- **License:** MIT Open Source License.
