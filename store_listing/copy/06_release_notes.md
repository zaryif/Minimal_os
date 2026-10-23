# Minimal OS Release Notes - v1.9.0 (Build 21)

## Google Play Console Release Notes (en-US)

Paste this exact snippet into the Release Notes field in Google Play Console (en-US):

```text
<en-US>
Minimal OS v1.9.0:
- Interactive Terminal Buffer: Past commands render as numbered chat cards ([1], [2], [3]); tap any card to instantly re-execute or edit.
- Apple-grade interactive onboarding with animated command demonstrations.
- Custom alias system for instant app launching and web navigation.
- Refined Notification Hub with streamlined categories and expanded touch targets.
- Modernized widget task deck with instant quick-add.
- 100% offline, zero-telemetry architecture with zero ads and zero data tracking.
</en-US>
```

---

## Detailed GitHub Release Notes (v1.9.0)

### What's New in v1.9.0

#### 1. Interactive Terminal Buffer & Command History Chat Stream
- When typing into the Command Bar, previous commands are displayed in a scrollable, ChatGPT/Gemini-style chat card stream.
- Each past execution is rendered as an interactive numbered card (`[1]`, `[2]`, `[3]...`).
- Tapping any card immediately reloads the command into the input bar ready to execute or tweak.
- Includes a live message counter and a one-tap clear history action.

#### 2. Apple & Google-Grade Interactive Onboarding
- Re-architected the initial onboarding flow into a single-viewport, no-scroll presentation.
- Features live simulated command typing animations demonstrating natural language triggers in real time:
  - `- Texting Zaryif` -> Instant task creation
  - `! Life is always good with Minimal OS` -> Instant scratchpad memo
  - `+ 25m Focus sprint` -> Instant Pomodoro session
- Interactive alias setup allowing users to assign one-letter shortcuts (`z`, `n`, `m`, `c`, `calc`, `cam`, `set`) before reaching the home screen.

#### 3. Streamlined Notification Hub
- Removed redundant flagged category tab to eliminate visual noise.
- Resized and expanded category filter tabs (ALL, MESSAGE, CALLS, EMAIL, APPS) with 13sp bold typography and larger touch targets.
- Retained full notification actions and one-tap dismissals.

#### 4. Unified Task Nomenclature
- Renamed all "to-do" references to "tasks" across home screen widgets, widgets deck, and settings for modern, consistent terminology.

#### 5. Top-Brand Store Presentation Kit
- High-fidelity 1080x2400 phone showcase screenshots with dark titanium device mockups, feature pills, and bold typography.
- Refined 1024x500 Feature Graphic for Google Play Store.
- 7-inch (1200x1920) and 10-inch (1600x2560) tablet showcase graphics.

#### 6. Privacy & Performance
- Built on Android 15 / 16 (API 36).
- 100% local, zero-telemetry, zero-ad, open-source architecture.
