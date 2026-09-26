# Calorie Tracker Android starter

This is the initial native Android app in Kotlin, Jetpack Compose and Room. Its starter Indian-food catalog is bundled in the app and copied into Room on first launch. Food searches, macro edits and diary logs work offline; the manifest intentionally has no internet permission.

## Features in this starter

- Browse/search 30 generic Indian and everyday food entries stored locally.
- Add a custom food or edit calories, protein, carbohydrates and fat per 100 g.
- Log a gram amount and save the calculated calories/macros in today's diary.
- See daily totals and delete log rows.
- View a rough weight-loss energy-gap estimate with conservative pace checks and explicit limitations.
- Food edits do not rewrite already saved diary snapshots.

Starter dish nutrition figures are estimates for demonstration and vary by recipe. Edit them to match the actual ingredients, cooking oil, portion and product label. This starter is not an exhaustive or clinically validated nutrition database. The goal screen uses a static energy-per-weight approximation for informational energy-gap arithmetic only. It is not a personalized weight-loss forecast, maintenance-calorie estimate, or exercise-calorie prescription.

## Build in GitHub Codespaces with VS Code

Android Studio does not have to be installed on the personal computer. The Codespace must have a JDK, Gradle, Android SDK command-line tools, Android platform/build tools and accepted SDK licenses. Java and Gradle are present in the current Codespace, but the Android SDK is not configured yet; install/configure it in the remote Codespace container before building.

This starter currently targets AGP 9.4.0 (Gradle 9.6+), Kotlin 2.4.20, Compose BOM 2026.09.00, Room 2.8.5, compile SDK 37.2, target SDK 36 and build tools 36.0.0. In the Codespace, install the [official Android CLI](https://developer.android.com/tools/agents/android-cli) or [SDK command-line tools](https://developer.android.com/studio#command-line-tools-only), set `ANDROID_HOME` to the remote SDK directory, and add its `platform-tools` to `PATH`. With Android CLI available, install the needed packages in the Codespace using:

```sh
android --sdk="$ANDROID_HOME" sdk install platforms/android-37.2 build-tools/36.0.0 platform-tools
```

Accept the Android SDK licenses there when prompted. These changes stay in the remote Codespace; they do not install Android Studio or SDK components on the personal computer. Then from this directory run:

```sh
gradle assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. Download it from the Codespace and install it on an Android phone, or run the build on a machine with an Android emulator. Codespaces generally does not provide a hardware-accelerated Android emulator in the browser. The project has no Gradle wrapper yet; this initial setup uses Gradle installed in the Codespace.

## Current implementation boundary

The initial code includes the offline food catalog, local macro editor, calorie diary and limited goal arithmetic. Database export/import, automated tests, accessibility pass, clinically reviewed goal calculations, and release signing still need implementation before calling this a complete v1 or distributing it publicly. The initial starter data's generic values should be reviewed and sourced before release.
