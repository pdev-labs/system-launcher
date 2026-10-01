# System Launcher — Last Launcher inspired, with icon logos

Minimal text-first Android launcher inspired by Last Launcher (speed, instant search,
keyboard-first) **plus real app icon logos** next to each label.

- Single-page home: pinned `[icon + name]` rows, then all apps
- Instant search, auto-launch single match, keyboard-first
- Long-press: pin / rename alias / hide / app-info / uninstall
- Settings: icons ON/OFF (pure text-only Last mode), icon size, Light/Dark
- Fresh codebase (Kotlin + Compose + Material3 + DataStore), Apache-2.0

## APK — GitHub Actions ONLY
Push to `main` → Actions → Build APK → download `system-launcher-debug` (`app-debug.apk`).
`adb install`, set as Default Home.

Tech: `minSdk 26`, `targetSdk 34`. Icons via `PackageManager` + `LruCache(100)`.
