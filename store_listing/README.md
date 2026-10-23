# Minimal OS - Google Play Store Listing & Publishing Kit

This directory contains all official graphic assets, real device screenshots, and text copy required to publish Minimal OS to Google Play Console (Closed Testing and Production).

---

## Directory Structure

```text
store_listing/
├── README.md                                  # This directory index and upload guide
├── copy/                                      # Ready-to-paste store listing and policy text
│   ├── 01_store_listing_copy.md               # Title, short description (<80 chars), full description
│   ├── 02_privacy_policy.md                   # Zero-telemetry privacy policy
│   ├── 03_data_safety_answers.md              # Exact Play Console Data Safety questionnaire answers
│   ├── 04_target_audience_and_content_rating.md # Content rating and app declarations walkthrough
│   ├── 05_closed_testing_and_production_guide.md # 14-day / 20-tester playbook to unlock production
│   └── 06_release_notes.md                    # Release notes for v1.8.0 (Build 20)
├── graphics/                                  # Formatted store assets
│   ├── icon_512x512.png                       # 512x512 PNG app icon (Main store listing)
│   ├── feature_graphic_1024x500.png           # 1024x500 PNG banner (Main store listing)
│   └── screenshots/                           # 10 Curated Real-Device In-App Screenshots (1080x2400)
│       ├── 01_live_home_screen.png            # Live minimalist home screen with clock, agenda & to-dos
│       ├── 02_command_terminal_buffer.png     # Live terminal command buffer with active cards
│       ├── 03_command_guide.png               # Interactive command guide dialog
│       ├── 04_app_drawer.png                  # Text-based alphabetical app drawer
│       ├── 05_instant_search.png              # Live typing app search filter
│       ├── 06_quick_memo_todos.png            # Slide-up Minimal Memo sheet with to-dos & notes
│       ├── 07_live_focus_timer.png            # Live Pomodoro focus countdown on home screen
│       ├── 08_widgets_deck.png                # Unified widgets deck dashboard
│       ├── 09_settings_and_themes.png         # Deep settings and customization options
│       └── 10_notification_hub.png            # Unified notification hub
├── video/                                     # High-definition promotional and showcase videos
│   ├── minimal_os_marketing_vertical_9x16.mp4 # 9:16 Vertical app overview (Shorts, Reels, Play Store)
│   ├── minimal_os_marketing_landscape_16x9.mp4 # 16:9 Landscape app overview (YouTube, Play Store)
│   ├── minimal_os_command_showcase_vertical_9x16.mp4 # 9:16 Vertical Command Bar & Alias deep dive
│   ├── minimal_os_command_showcase_landscape_16x9.mp4 # 16:9 Landscape Command Bar & Alias deep dive
│   ├── minimal_os_virtual_phone_code_vertical_9x16.mp4 # 9:16 Vertical real app & code architecture showcase
│   └── minimal_os_virtual_phone_code_landscape_16x9.mp4 # 16:9 Landscape real app & code architecture showcase
├── generate_marketing_video.py                # Automated marketing overview video generator script
├── generate_command_showcase_video.py         # Command Bar typing & execution showcase video generator script
├── generate_virtual_phone_code_showcase.py    # Real virtual phone & code architecture video generator script
├── capture_real_emulator_states.py            # Automated ADB script capturing live emulator states
└── screenshots_real/                          # Complete raw captures archive from running device
```

---

## Why the App Icon Was Missing in Play Console

Google Play Console does **NOT** extract the store listing icon from inside the `.apk` or `.aab` file.

To display your icon on Google Play and inside the Console header:
1. Open Google Play Console: https://play.google.com/console
2. Navigate to: **Grow > Store presence > Main store listing**
3. Scroll down to **Graphics > App icon**.
4. Upload `store_listing/graphics/icon_512x512.png`.
5. Upload `store_listing/graphics/feature_graphic_1024x500.png` under **Feature graphic**.
6. Upload the screenshots from `store_listing/graphics/screenshots/` under **Phone screenshots**.
7. Click **Save** at the bottom of the page.

Once saved, the app icon replaces the generic Android placeholder throughout the Play Console and Play Store.

---

## Shareable Links Reference

- **Internal Testing Link**:
  `https://play.google.com/apps/internaltest/4976361961120095801`
  *(Works immediately for users in the internal testers list)*

- **Closed Testing Web Opt-In Link**:
  `https://play.google.com/apps/testing/com.minimal.os`
  *(Testers click here to opt in and unlock their 14-day countdown for Production)*

- **Play Store Web Link (After Production Approval)**:
  `https://play.google.com/store/apps/details?id=com.minimal.os`
