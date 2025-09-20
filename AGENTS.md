# Repository Guidelines

## Project Structure & Module Organization
- `app/` contains the Android client; core logic sits under `app/src/main/java/org/autojs/autojs/` split into `core`, `engine`, `ui`, `script`, `runtime`, and related feature packages.
- `app/src/main/assets-app/` stores built-in scripts, docs, and Rhino resources; update assets here rather than embedding large blobs in code.
- `modules/` houses reusable Gradle modules (`apk-parser`, `color-picker`, `jieba-analysis`, etc.) that are published into the main app via composite builds; follow the existing layout when adding a new component.
- Native and third-party libraries live in `libs/` (e.g., `org.opencv-4.8.0`, `paddleocr`); keep binary updates versioned and documented in `version.properties`.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` builds a debug APK with accessibility features enabled.
- `./gradlew :app:assembleInrtRelease` refreshes the script-packaging template APK required for standalone script builds.
- `./gradlew installDebug` deploys the debug build to a connected device or running emulator for quick iteration.
- `./gradlew clean` removes previous outputs; run before switching Gradle toolchains or SDK versions.

## Coding Style & Naming Conventions
- Kotlin and Java coexist; prefer Kotlin for new code while keeping interop signatures stable. Indent with 4 spaces and avoid wildcard imports.
- Run `./gradlew ktlintCheck` and `./gradlew lint` before opening a PR; configure Android Studio to auto-format with official Kotlin/Android style.
- Package names stay under `org.autojs.autojs.*`; Android resource files use lower_snake_case for names and follow existing prefix patterns (`activity_`, `fragment_`, `item_`).

## Testing Guidelines
- Place JVM unit tests under `app/src/test/java` (create the path if missing) and instrumentation tests under `app/src/androidTest/java` mirroring the production package.
- Execute `./gradlew test` for JVM tests and `./gradlew connectedAndroidTest` against a USB device or API 24+ emulator; both run in CI.
- Name tests with the feature + behaviour pattern (e.g., `TimedTaskManagerResetsOnCancelTest`) and assert accessibility flows with Espresso where possible.

## Commit & Pull Request Guidelines
- Follow the existing imperative style (`Add …`, `Fix …`); release commits use the `<version> - <type> - <summary>` pattern seen in history (`6.6.4 - Fix - …`).
- Reference issues with `#123` in commit bodies or PR descriptions and include short bilingual notes when touching user-facing strings.
- PRs should describe motivation, test coverage, and attach relevant screenshots/logs for UI or accessibility changes; link to builds when sharing APK artifacts.

## Security & Configuration Tips
- Keep signing credentials local; `sign.properties` is git-ignored—use the documented keys without committing secrets.
- Align SDK/NDK versions with `version.properties` before building native OCR bundles; mismatches surface as missing `.so` files at runtime.
- Accessibility and OCR features require real-device validation; scrub personal data from captured logs before attaching them to issues.
