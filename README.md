# System Launcher — Nova-style power, Last Launcher speed

Minimal text-first launcher **with icon logos**, now with **Nova Launcher-style** home:
Nova grid + dock + folders + drawer grid, forked from Last Launcher ideas (fast search, text-only mode).

## Home (Nova grid, default)
- Grid of pinned apps (3–6 columns setting) + folders
- Dock row at bottom (up to 8 apps, toggleable) — Nova signature
- Long-press any icon: Pin / Dock / Rename / Hide / Folders / Info / Uninstall
- Folders: create from long-press menu, open from home, add/remove members

## Drawer (Nova-style)
- Searchable grid (3–6 columns) with instant filter
- Auto-launch single match (Last Launcher behavior, keyboard-first)
- Long-press: pin to home, add to dock, folders

## Last Launcher mode (preserved)
- Settings → Home style → **Last list**: pure text list, no grid
- Settings → Text-only: hides all icons everywhere
- Fast search, hidden apps, aliases, AMOLED dark

## Settings
- Home style Nova grid / Last list, home+drawer columns, dock, labels
- Icons ON/OFF, icon size 32–72dp, theme System/Light/Dark
- Unhide, refresh

## APK — GitHub Actions ONLY
Push to `main` → Actions → Build APK → download `system-launcher-debug` (`app-debug.apk`).
`adb install`, set as Default Home.

Tech: Kotlin + Compose + Material3 + DataStore, `minSdk 26`, `targetSdk 34`.
Icons via `PackageManager` + `LruCache(100)`, adaptive-icon safe.
