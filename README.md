# StreakTracker — Android portfolio demo

A newly generated, AI-assisted Android sample for tracking everyday habits. This is a demo project, not a previously shipped application. No production adoption, app-store publication, or user metrics are claimed. Review and customize the implementation before presenting it as your work.

## Features
- Independent reading, study and walking calendars with completion toggles.
- Current streak calculation and local persistence using Preferences DataStore.
- Reactive UI using Jetpack Compose, Material 3, ViewModel, coroutines and StateFlow.
- Optional daily reminder via WorkManager, with Android 13+ notification permission. Delivery is inexact.

## Architecture
Compose UI → TrackerViewModel → HabitRepository → Preferences DataStore. The repository exposes a Flow; the ViewModel converts it to StateFlow; UI observes it with lifecycle-aware collection. ReminderWorker is separate from UI and persistence. All habit data remains local.

## Run locally
Requires JDK 17, Android SDK 35 and Gradle 8.9. Open the project in Android Studio and sync. No Gradle wrapper is bundled; run gradle wrapper --gradle-version 8.9 using an installed Gradle. Then run ./gradlew :app:testDebugUnitTest :app:assembleDebug or launch on an Android 8+ emulator/device.

## Verification status
The native build, tests, emulator UI, persistence and notifications have not yet been executed. Add authentic screenshots after running the app. This is not a claim of prior employment or release experience.

## Portfolio
[Interactive browser prototype](docs/index.html) — a separate web demo, not an Android screenshot. For GitHub Pages, use Settings → Pages → Deploy from a branch → main → /docs.
