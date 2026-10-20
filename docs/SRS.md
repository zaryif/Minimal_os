# Software Requirements Specification (SRS)
## Minimal OS Launcher

**Author:** MD. Zarif Azfar  
**Date:** October 20, 2026  
**Version:** 1.0.0  

---

### 1. Functional Requirements (FR)

#### 1.1 Home Screen (FR-HOME)
- **FR-HOME-01:** System shall render current time in bold 76sp minimal sans-serif typography.
- **FR-HOME-02:** System shall display lowercase localized date beneath the clock.
- **FR-HOME-03:** System shall host a persistent command bar anchored to the bottom.
- **FR-HOME-04:** System shall support swipe-up gesture to transition to the App Drawer.
- **FR-HOME-05:** System shall support swipe-right gesture to transition to the Hub.
- **FR-HOME-06:** System shall provide quick header shortcuts to Minimal Ask, Clock, Memo, and Settings.

#### 1.2 Command Bar & Execution Engine (FR-CMD)
- **FR-CMD-01:** Symbol `@` + text → Dispatch SMS composition intent to recipient or body.
- **FR-CMD-02:** Symbol `#` + number → Dispatch phone dialer intent.
- **FR-CMD-03:** Symbol `-` + text → Save to-do item into persistent storage.
- **FR-CMD-04:** Symbol `!` + text → Save quick note into persistent storage.
- **FR-CMD-05:** Symbol `*` + text → Dispatch calendar event creation intent.
- **FR-CMD-06:** Symbol `+` + duration → Set countdown timer via `AlarmClock.ACTION_SET_TIMER`.
- **FR-CMD-07:** Symbol `:` + time → Set alarm via `AlarmClock.ACTION_SET_ALARM`.
- **FR-CMD-08:** Symbol `?` + query → Dispatch web search query.
- **FR-CMD-09:** Text without prefix → Filter installed apps in real-time and launch on Enter.
- **FR-CMD-10:** Clear button `(X)` clears current input; Arrow button `(→)` executes command.

#### 1.3 App Drawer (FR-APPS)
- **FR-APPS-01:** Query `PackageManager` for all activities matching `ACTION_MAIN` & `CATEGORY_LAUNCHER`.
- **FR-APPS-02:** Sort apps alphabetically in ascending order.
- **FR-APPS-03:** Provide real-time top search bar with instant substring filtering.
- **FR-APPS-04:** Tap on an app item immediately triggers launch intent.

#### 1.4 Notification Hub (FR-HUB)
- **FR-HUB-01:** Implement `NotificationListenerService` to intercept incoming status bar notifications.
- **FR-HUB-02:** Group notifications into filter tabs: `ALL`, `MESSAGE`, `CALLS`, `EMAIL`, `APPS`, `FLAGGED`.
- **FR-HUB-03:** Format relative timestamps (`now`, `5m`, `1h`, `2d`).
- **FR-HUB-04:** Tap notification to open source application.
- **FR-HUB-05:** Gesture: Swipe-left returns to Home screen.

#### 1.5 Restriction Modes (FR-RESTRICT)
- **FR-RESTRICT-01:** Monk Mode: Essentials only, grayscale always.
- **FR-RESTRICT-02:** Focus Mode: Distracting apps on a strict usage clock.
- **FR-RESTRICT-03:** Unrestricted: Full access without guardrails.

#### 1.6 Onboarding Wizard (FR-ONBOARD)
- **FR-ONBOARD-01:** 5-step interactive wizard on initial launch:
  1. Your Apps (Minimal built-in apps)
  2. Minimal Ask (AI/Search helper)
  3. Type to do things (Symbol command table)
  4. Hub (Unified notification preview)
  5. Restrictions (Mode selection)
- **FR-ONBOARD-02:** Store completion flag in `UserPreferences`.
- **FR-ONBOARD-03:** Settings screen must provide option to replay onboarding.

---

### 2. Non-Functional Requirements (NFR)
- **NFR-01 (Performance):** Cold start to interactive home under 600ms.
- **NFR-02 (Responsiveness):** Command bar input keystroke latency under 16ms (60fps).
- **NFR-03 (Resource Efficiency):** Memory footprint under 45 MB RSS.
- **NFR-04 (Storage Footprint):** Release APK size under 8 MB.
- **NFR-05 (Accessibility):** High-contrast monochrome scheme exceeding WCAG AAA standards.
