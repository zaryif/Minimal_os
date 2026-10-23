# Minimal OS Release Notes - v1.9.1 (Build 22)

## Google Play Console Release Notes (en-US)

Paste this exact snippet into the Release Notes field in Google Play Console (en-US):

```text
<en-US>
Minimal OS v1.9.1:
- Default Launcher & Permissions Integration: Set Minimal OS as your default launcher and grant Notification Hub permissions directly during onboarding.
- Standardized task nomenclature: Replaced all to-do references with tasks across home widgets, deck, and memo sheet.
- Interactive Terminal Buffer: Numbered execution cards ([1], [2], [3]...) ready to re-run or edit.
- Streamlined Notification Hub with enlarged 13sp tabs and direct permission management.
- 100% offline, zero-telemetry architecture with zero ads and zero data tracking.
</en-US>
```

---

## Detailed GitHub Release Notes (v1.9.1)

### What's New in v1.9.1

#### 1. Onboarding Default Launcher & Permission Setup
- Added direct **Default Launcher** configuration cards right inside the final setup wizard with live status indicators (`[active default]` vs `[tap to set default]`).
- Automatically triggers system default home selection upon completing onboarding if not yet set.
- Added direct **Notification Hub Access** grant buttons in both the Notification Hub preview step and the final setup wizard.

#### 2. Comprehensive Task Terminology Standardization
- Eliminated all legacy "to-do" / "to-dos" labels across the entire app.
- Onboarding demo animations now feature `- TASK` and `Task Added to Home`.
- Onboarding widget preview displays `ACTIVE TASK`.
- Slide-up Minimal Memo sheet now renders `TASKS (N)` as the primary tab, with `Search tasks…` and `No tasks yet`.
- Terminal command line returns `Saved task: <query>` on execution.

#### 3. Top-Brand Store Presentation Graphics (v1.9.1)
- Re-captured 100% authentic in-app screenshots directly from the running Android 15/16 emulator.
- Regenerated 8 flagship 1080x2400 phone showcase slides, 1024x500 feature graphic, and 7-inch & 10-inch tablet graphics.

#### 4. Architecture & Privacy
- Built on Android 15 / 16 (API 36).
- 100% offline, zero-telemetry, zero-ad, open-source architecture.
