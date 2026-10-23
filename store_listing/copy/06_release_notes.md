# Minimal OS Release Notes - v1.9.2 (Build 23)

## Google Play Console Release Notes (en-US)

Paste this exact snippet into the Release Notes field in Google Play Console (en-US):

```text
<en-US>
Minimal OS v1.9.2:
- Intelligent Contact Resolution: Added greedy multi-word contact matching and relevance scoring for SMS (@), WhatsApp (wa), and phone calls (#).
- Primary Number Preference: Prioritizes mobile and primary default numbers for messaging contacts.
- Streamlined Notification Hub: Removed redundant Apps section to focus strictly on high-signal tabs (ALL, MESSAGE, CALLS, EMAIL).
- Onboarding Default Launcher: One-tap default home and notification permission setup directly during initial walkthrough.
- 100% offline, zero-telemetry architecture with zero ads and zero data tracking.
</en-US>
```

---

## Detailed GitHub Release Notes (v1.9.2)

### What's New in v1.9.2

#### 1. Intelligent Contact Matching & Messaging Engine
- **Greedy Multi-Word Token Matching**: The terminal command bar now greedily parses multi-word recipient names from longest candidate down to single words. Typing `@John Doe on my way` now correctly targets "John Doe" instead of splitting on the first space and texting "John Adams".
- **Dynamic Relevance Scoring Engine**: Replaced raw alphabetical sorting with a 1,000-point relevance scoring hierarchy:
  - Exact full name match: +1000 points
  - Prefix name match: +600 points
  - Word-level name match: +400 points
  - Suffix / phone number match: +450 to +800 points
  - Primary default number bonus: +60 points
  - Mobile number bonus: +30 points
- **Deterministic Disambiguation**: Typing `@Mom` now reliably matches "Mom" rather than picking "Grandma Mom" just because it was alphabetically earlier.
- **Unified Engine**: Applied across SMS (`@`), WhatsApp (`wa`), and direct calls (`#`).

#### 2. Streamlined Notification Hub
- Removed the separate `APPS` tab from the Notification Hub.
- Refined category filters to four essential, high-signal tabs: `ALL`, `MESSAGE`, `CALLS`, and `EMAIL`.
- Non-communication notifications remain accessible in the unified `ALL` view without visual clutter.

#### 3. Default Launcher & System Perfection in Onboarding
- Preserves the v1.9.1 setup wizard with live status badges (`DEFAULT` / `SYSTEM PICKER`, `GRANTED` / `ALLOW ACCESS`).
- Automatic default home launcher prompt upon completing onboarding.

#### 4. Architecture & Quality
- Target SDK: Android 15/16 (API 36).
- Minimum SDK: Android 8.0 (API 26).
- 100% private, local-first, zero telemetry, zero trackers, and zero ads.
