# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AutoJs6 is a JavaScript automation tool for Android that supports accessibility services. It's a continuation of the Auto.js project, providing powerful automation capabilities including UI automation, image recognition, script packaging, and more.

## Development Environment

This is an Android project built with:
- **Build System**: Gradle with Kotlin DSL (`.gradle.kts` files)
- **Language**: Mixed Kotlin/Java codebase
- **Platform**: Android (Min SDK 24, Target SDK 35)
- **IDE**: Android Studio or IntelliJ IDEA recommended

## Common Commands

### Build Commands
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK  
./gradlew assembleRelease

# Build template APK for script packaging feature
./gradlew :app:assembleInrtRelease
```

### Testing
```bash
# Run tests
./gradlew test

# Run Android instrumentation tests
./gradlew connectedAndroidTest
```

### Lint and Code Quality
```bash
# Run lint checks
./gradlew lint

# Check for Kotlin code style issues
./gradlew ktlintCheck
```

## Architecture Overview

### Main Application Structure
- `app/` - Main Android application module
  - `src/main/java/org/autojs/autojs/` - Core application code organized by feature:
    - `core/` - Core automation functionality (accessibility, gestures, image processing)
    - `engine/` - JavaScript engine integration (Rhino-based)
    - `ui/` - Application UI components and activities
    - `script/` - Script execution and management
    - `project/` - Project and build system for scripts
    - `runtime/` - Runtime APIs exposed to scripts
    - `service/` - Background services and accessibility service

### Modules
- `modules/` - Modular components:
  - `apk-parser/` - APK parsing utilities
  - `apk-signer/` - APK signing functionality  
  - `color-picker/` - Color selection UI
  - `jieba-analysis/` - Chinese text segmentation
  - `material-dialogs/` - Enhanced dialog components
  - `material-date-time-picker/` - Date/time selection widgets

### Libraries  
- `libs/` - Third-party and custom libraries:
  - `org.opencv-4.8.0/` - OpenCV for computer vision
  - `paddleocr/` - OCR capabilities using PaddleOCR
  - `rapidocr/` - Alternative OCR implementation
  - Various Android compatibility and utility libraries

## Key Development Notes

### JavaScript Engine
- Uses Rhino JavaScript engine (v1.8.1-SNAPSHOT)
- Scripts run in isolated contexts with Android API access
- Supports modern JavaScript features including template literals, arrow functions, async/await

### Script Packaging
- Scripts can be packaged into standalone APK files
- Template APK (`template.apk`) must be built using `inrt:assemble` task before packaging functionality works
- If you see `java.io.FileNotFoundException: template.apk`, run the inrt assembly task

### Version Management
- Version information stored in `version.properties`
- Current version: 6.6.4 (build 3274)
- Supports Android API 24+ (Android 7.0+)

### Build Configuration
- Complex Gradle setup with automatic version management in `settings.gradle.kts`
- Supports multiple build variants (app/inrt) and architectures
- OCR libraries (PaddleOCR, RapidOCR) require specific NDK/CMake versions

## Development Workflow

1. **Setup**: Ensure Android SDK, NDK, and CMake versions match `version.properties`
2. **Build Template**: Run `./gradlew :app:assembleInrtRelease` if using script packaging
3. **Development**: Use standard Android development practices
4. **Testing**: Test on real devices for accessibility service functionality
5. **Packaging**: Use release builds for distribution

## Important Files

- `version.properties` - Version and SDK configuration
- `settings.gradle.kts` - Complex build configuration with platform detection
- `build.gradle.kts` - Root project configuration
- `app/build.gradle.kts` - Main app build configuration

## Special Considerations

- Accessibility service testing requires real devices or properly configured emulators
- OCR functionality depends on native libraries that may need device-specific builds
- Script packaging requires the inrt variant to be built first
- The project uses a sophisticated Gradle setup that auto-detects IDE versions and adjusts build configurations accordingly