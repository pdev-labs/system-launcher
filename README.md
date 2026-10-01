# System Launcher

Minimal text-first Android launcher **with icon logos** — inspired by Last Launcher (text-only speed) but shows real app icons next to each label.

- Home: pinned `[icon + name]` rows, tap to launch, long-press for pin / rename / hide / info / uninstall
- Drawer + instant search, auto-launch single match, keyboard-first
- Settings: Show icons ON/OFF (pure text-only Last-Launcher mode), icon size, Light/Dark/AMOLED
- Fresh codebase (Kotlin + Compose + Material3 + DataStore), Apache-2.0

## APK — GitHub Actions ONLY

APKs are **never built locally**. Every push to `main` builds in CI:

1. Push code
2. GitHub > Actions > Build APK > download `system-launcher-debug` artifact (`app-debug.apk`)
3. `adb install app-debug.apk`, set as Default Home to test

Release signing (optional V2): add `KEYSTORE_BASE64 / KEYSTORE_PASSWORD / KEY_ALIAS / KEY_PASSWORD` secrets + `release.yml` on `v*` tags.

## Project

- `applicationId: com.pdevlabs.systemlauncher`, `minSdk 26`, `targetSdk 34`
- Icons loaded via `PackageManager` + `LruCache(100)`, off main thread, adaptive-icon safe
- Launcher icon: `res/mipmap-anydpi-v26` adaptive + monochrome for themed icons
