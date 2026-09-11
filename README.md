# AutoBrowser 🌐⚡

[![Android Build](https://github.com/fourgeailabs/AutoBrowser/actions/workflows/build.yml/badge.svg)](https://github.com/fourgeailabs/AutoBrowser/actions)
[![Current Version](https://img.shields.io/badge/version-2.03.00-blue.svg)](https://github.com/fourgeailabs/AutoBrowser/releases)
[![Android Automotive](https://img.shields.io/badge/Automotive%20OS-Ready-brightgreen.svg)](https://developer.android.com/training/cars)
[![Creator](https://img.shields.io/badge/Creator-FourgeAI%20LABS-purple.svg)](https://github.com/fourgeailabs)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

**AutoBrowser** is an intelligent, versatile automated web browser for Android and **Android Automotive OS (AAOS)** crafted with modern Kotlin and Jetpack Compose. Built specifically for automated web monitoring, multi-tab queue cycling, hands-free smooth auto-scrolling, in-car parked browsing, in-drive developer testing, and custom script execution, AutoBrowser empowers power users, vehicle developers, testers, and kiosk operators to run routine web actions without tedious manual intervention.

---

## 🚀 Key Features

### 🚗 1. Android Automotive OS (AAOS) & In-Drive Testing Mode
- **Native Automotive Compatibility**: Declared `android.hardware.type.automotive` support and `@xml/automotive_app_desc` app descriptor.
- **Drive Testing Mode (In-Motion Testing)**: Bypasses parked restriction to enable uninterrupted web browsing, live dashboard monitoring, auto-scrolling, and playlist cycles while in Drive for developer testing and road trials.
- **Distraction Optimized Manifest Flag**: Includes `<meta-data android:name="distractionOptimized" android:value="true" />` so Android Automotive OS does not force-kill or hide the UI when shifting into gear.
- **Standard Driver Distraction Safety Lockout**: Production mode complies strictly with Google Play Automotive OS driver distraction standards with parked detection and lockout screen.
- **Dashboard Center-Console UI**: Oversized touch targets (56dp+), high-contrast readability, and rotary/D-pad controller navigation.
- **In-Car Quick Presets Bar**: Instant one-tap access to EV Charging (PlugShare), Live Radar (Windy), Traffic (Google Maps), In-Car Radio (Radio Garden), and GitHub.
- **Multi-Device Testing**: Toggle between Automotive Dashboard Mode, Drive Testing Mode, and Mobile/Tablet Mode directly in Settings.

### ⏱️ 2. Intelligent Auto-Refresh Engine
- **Customizable Interval Timers**: Select preset reload frequencies (5s, 10s, 30s, 60s, 2m, 5m) or define a custom countdown.
- **Visual Countdown Ring**: Real-time circular progress indicator displays the exact time remaining before the next automatic page refresh.
- **Instant Pause/Resume**: Quickly halt or trigger the auto-reload loop with one tap.

### 📜 3. Hands-Free Auto-Scroller
- **Continuous Reading Mode**: Effortlessly scroll articles, documents, code repositories, or social feeds.
- **Granular Speed Tuning**: 10 speed increments from gentle slow reading to high-speed scanning.
- **Bidirectional Control**: Auto-scroll downwards or upwards seamlessly.
- **Touch-Aware Safety**: Automatically pauses when the screen is touched so you never lose your spot.

### 🔄 4. Multi-URL Playlist Cycling (Kiosk & Dashboard Mode)
- **Automated Queue Looping**: Add multiple URLs into a rotation playlist.
- **Custom Per-Site Dwell Time**: Specify exactly how many seconds each site remains on screen before automatically advancing.
- **Continuous Loop or Single-Pass**: Perfect for digital signage, in-car dashboards, monitoring servers, and live analytics.

### ⚡ 5. Web Action & Script Automation
- **Quick Automation Presets**:
  - **Force Dark Mode**: Injects smart CSS styling to eliminate eye fatigue on any webpage.
  - **Sticky & Overlay Dismissal**: Automatically sweeps away sticky headers, cookie banners, and modal popups.
  - **Auto-Clicker**: Target elements by CSS selector or button text and automatically trigger clicks at specified intervals.
  - **Reader Clean View**: Cleans page layout for distraction-free reading.
- **Custom JavaScript Console**: Inject and execute custom JavaScript directly into the live DOM with immediate feedback and console logging.

### 📑 6. Complete Modern Web Experience
- **Multi-Tab Management**: Open, switch, and close tabs with smooth animations.
- **Desktop & Mobile Viewport Toggle**: Switch between responsive mobile and full desktop mode on the fly.
- **Bookmarks & History Tracking**: Stored locally in a persistent SQLite Room database.
- **Search Engine Flexibility**: Choose between Google, DuckDuckGo, Bing, and Ecosia.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material 3 Design
- **State Management**: Android Jetpack ViewModel + Kotlin StateFlow
- **Automotive**: Android Automotive OS (AAOS) compatibility with `automotive_app_desc.xml`, driver safety compliance, and rotary/touch navigation
- **Web Engine**: Android WebKit WebView with custom `WebViewClient`, `WebChromeClient`, and JavaScript interface bridges
- **Local Persistence**: Android Room Database + KSP
- **Target SDK**: Android 16 (API 36) | **Min SDK**: Android 7.0 (API 24)
- **CI/CD Pipeline**: GitHub Actions for automated APK building and release publishing

---

## 📦 What's New & Release Notes

### Version 2.03.00 (Build 5) - Current
- **Chevrolet Equinox EV Notification Bar Clearance & System Top Inset Support**:
  - Positioned the browser header, URL bar, navigation buttons, and webpage cleanly below the Equinox EV top notification and system bar.
  - Implemented dynamic top inset processing combining `WindowInsets.safeDrawing` with a dedicated automotive display offset.
  - Set default 64dp clearance tailored for the Equinox EV 17.7-inch infotainment touchscreen with full zero-obscuration layout.
  - Added dedicated **Notification Bar Clearance (Equinox EV)** configuration under Settings (Auto, 48dp, 56dp, 64dp, 72dp, 80dp, 96dp).
  - Added an interactive one-tap top clearance cycle badge directly in the In-Car Quick Bar for immediate in-vehicle adjustment.

### Version 2.02.00 (Build 4)
- **In-Drive Testing Mode & Distraction Optimization**:
  - Added Drive Testing Mode toggle allowing uninterrupted web browsing, live dashboard monitoring, and auto-scrolling while in Drive.
  - Added `distractionOptimized` manifest meta-data flag allowing Android Automotive OS to run the activity continuously in motion without OS suspension.
  - Auto-scroller and URL playlist cycles remain active without being auto-paused when in Drive testing mode.
  - Added quick Drive Testing toggle badge directly inside the In-Car Presets toolbar for rapid status switching.
  - Added Drive Testing Mode controls inside Settings dialog under Android Automotive & In-Car Mode.
  - Safety lockout overlay now includes one-tap 'Enable Drive Testing Mode' bypass action for streamlined road trials.

### Version 2.00.00 (Build 2)
- **Android Automotive OS Compatibility & In-Car Parked Mode**:
  - Full Android Automotive OS (AAOS) platform compatibility with hardware descriptors and automotive manifest declarations.
  - In-Car Parked Browsing Mode adhering strictly to Google Play Automotive driver distraction guidelines.
  - Driver Distraction Safety Lockout that automatically halts web browsing and video playback while vehicle is in motion.
  - In-Car Quick Presets bar providing instant one-tap access to EV Charging (PlugShare), Live Radar (Windy), Traffic, Radio, and GitHub.
  - Dashboard-optimized controls with oversized touch targets (56dp+) and high contrast designed for center console displays.
  - Rotary controller, D-pad, and touch input support for vehicle head units.
  - In-app Automotive simulation switch in Settings for multi-device testing on phones, tablets, and emulators.

### Version 1.00.00 (Build 1)
- **Initial Official Release**:
  - Full-featured automated web browser engine with hardware acceleration.
  - Auto-Refresh feature with customizable countdown timers and live visual ring.
  - Hands-free Auto-Scroller with 10-level variable speed and bidirectional movement.
  - Multi-URL Playlist Cycling for continuous dashboard and kiosk loops.
  - Preloaded script automation tools (Force Dark Mode, Overlay Cleaner, Auto-Clicker, Reader Mode).
  - Custom JavaScript console executor for power users.
  - Full tab switching, bookmarking, and browsing history management.
  - Settings screen with search engine configuration and desktop mode options.
  - Interactive "What's New" accordion release history log.
  - "About" screen with verified links to FourgeAI LABS and official GitHub repository.

---

## 👨‍💻 Creator & Community

- **Creator**: **FourgeAI LABS**
- **Creator GitHub**: [https://github.com/fourgeailabs](https://github.com/fourgeailabs)
- **Application Repository**: [https://github.com/fourgeailabs/AutoBrowser](https://github.com/fourgeailabs/AutoBrowser)

---

## 📄 License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
