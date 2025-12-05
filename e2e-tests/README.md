# PetSpot E2E Tests - Java/Maven/Cucumber Stack

**Status**: JAVA ONLY - TypeScript E2E tests have been removed (Spec 025)

This directory contains the **unified E2E testing infrastructure** for PetSpot using Java/Maven/Cucumber.

---

## Directory Structure

```
/e2e-tests/
├── java/                           # Java/Maven/Selenium/Appium/Cucumber stack
│   ├── pom.xml                     # Maven project configuration
│   ├── apps/                       # Mobile app files for testing (.apk, .app)
│   │   ├── petspot-android.apk     # Android APK (copy from composeApp/build/outputs/apk/debug/)
│   │   └── petspot-ios.app         # iOS App (copy from iosApp/build/)
│   └── src/test/
│       ├── java/                   # Java test code
│       │   └── com/intive/aifirst/petspot/e2e/
│       │       ├── pages/          # Web Page Objects (Selenium)
│       │       ├── screens/        # Mobile Screen Objects (Appium)
│       │       ├── steps/          # Cucumber Step Definitions
│       │       │   ├── web/        # Web-specific steps
│       │       │   └── mobile/     # Mobile-specific steps
│       │       ├── runners/        # JUnit Test Runners
│       │       └── utils/          # Utilities (drivers, helpers)
│       └── resources/
│           └── features/           # Gherkin feature files (.feature)
│               ├── animal-list.feature      # @web @ios @android
│               ├── pet-details.feature      # @web @ios @android
│               ├── report-missing.feature   # @web @ios @android
│               └── legacy/                  # Old tests (to be removed)
│
└── README.md                       # This file
```

---

## Infrastructure Setup (COMPLETE GUIDE)

### 1. Java 21 (REQUIRED)

E2E tests require **Java 21 LTS**.

```bash
# macOS (Homebrew)
brew install openjdk@21

# Verify installation
java -version
# Expected: openjdk version "21.x.x"

# If Maven uses wrong Java version, check:
mvn -version
# Look for "Java version: 21.x.x"
```

**Note**: Maven may use a different Java than your shell. If `mvn -version` shows wrong Java:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### 2. Maven 3.9+ (REQUIRED)

```bash
# macOS (Homebrew)
brew install maven

# Verify
mvn -version
```

### 3. Web Tests Setup (Selenium)

Web tests use **Selenium WebDriver** with Chrome. ChromeDriver is auto-managed by WebDriverManager.

**Requirements:**
- Google Chrome browser installed
- Backend server running: `cd server && npm run dev` (port 3000)
- Web app running: `cd webApp && npm run start` (port 8080)

**Run Web Tests:**
```bash
cd e2e-tests/java
mvn test -Dtest=WebTestRunner
```

**Troubleshooting Chrome Issues:**
- If Chrome crashes on startup, update dependencies in `pom.xml` to latest Selenium version
- Current working versions (Dec 2025): Selenium 4.27.0, WebDriverManager 5.9.2

### 4. Android Tests Setup (Appium + UiAutomator2)

#### 4.1 Install Android SDK

```bash
# macOS - Android Studio installs SDK automatically
# Default location: ~/Library/Android/sdk

# Set environment variables (add to ~/.zshrc or ~/.bash_profile)
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH
```

#### 4.2 Install Appium 2.x

```bash
# Install Appium globally
npm install -g appium

# Install required drivers
appium driver install uiautomator2  # Android
appium driver install xcuitest      # iOS

# Verify installation
appium driver list
# Should show: uiautomator2 [installed], xcuitest [installed]
```

#### 4.3 Prepare Android APK

```bash
# Build Android APK
cd /path/to/project
./gradlew :composeApp:assembleDebug

# Copy APK to e2e-tests
cp composeApp/build/outputs/apk/debug/composeApp-debug.apk \
   e2e-tests/java/apps/petspot-android.apk
```

#### 4.4 Start Android Emulator

```bash
# List available emulators
emulator -list-avds

# Start emulator (replace with your AVD name)
emulator -avd Pixel_7_API_34

# Verify device is connected
adb devices
# Should show: emulator-5554  device
```

#### 4.5 Start Appium Server

**IMPORTANT**: Appium MUST have `ANDROID_HOME` set!

```bash
# Start Appium with ANDROID_HOME
export ANDROID_HOME=~/Library/Android/sdk
appium

# Verify Appium is running
curl http://localhost:4723/status
# Should return: {"value":{"ready":true,...}}
```

#### 4.6 Run Android Tests

```bash
cd e2e-tests/java
mvn test -Dtest=AndroidTestRunner
```

### 5. iOS Tests Setup (Appium + XCUITest)

#### 5.1 Requirements (macOS only)
- Xcode installed with Command Line Tools
- iOS Simulator available

#### 5.2 Prepare iOS App

```bash
# Build iOS app in Xcode or via command line
# Copy .app bundle to e2e-tests
cp -r iosApp/build/Debug-iphonesimulator/iosApp.app \
   e2e-tests/java/apps/petspot-ios.app
```

#### 5.3 Start iOS Simulator

```bash
open -a Simulator
# Or use specific device:
xcrun simctl boot "iPhone 15"
```

#### 5.4 Run iOS Tests

```bash
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
```

---

## Quick Start (After Setup)

### Build Project

```bash
cd e2e-tests/java
mvn clean compile test-compile
```

### Run Web Tests

```bash
# Ensure backend (port 3000) and webApp (port 8080) are running!
mvn test -Dtest=WebTestRunner
```

### Run Android Tests

```bash
# 1. Start Appium with ANDROID_HOME
export ANDROID_HOME=~/Library/Android/sdk && appium &

# 2. Ensure Android emulator is running
adb devices

# 3. Run tests
mvn test -Dtest=AndroidTestRunner
```

### Run iOS Tests

```bash
# 1. Start Appium
appium &

# 2. Ensure iOS Simulator is running
open -a Simulator

# 3. Run tests
mvn test -Dtest=IosTestRunner
```

### Run Smoke Tests (Fast)

```bash
# All platforms, smoke only
mvn test -Dcucumber.filter.tags="@smoke"

# Web smoke tests
mvn test -Dcucumber.filter.tags="@web and @smoke"

# Mobile smoke tests
mvn test -Dcucumber.filter.tags="@mobile and @smoke"
```

---

## Test Reports

Reports are generated automatically after test execution:

| Platform | Report Location |
|----------|-----------------|
| Web | `target/cucumber-reports/web/cucumber.html` |
| Android | `target/cucumber-reports/android/cucumber.html` |
| iOS | `target/cucumber-reports/ios/cucumber.html` |

**Screenshots** (on failure): `target/screenshots/`

---

## E2E Testing Principles

### 1. API-Driven Test Data
Tests create their own data via backend API:
- No dependency on seed data
- Each test is self-contained
- Cleanup after test (pass or fail)

### 2. Test Flows, Not Atomic Features
```gherkin
# ❌ BAD - Atomic
Scenario: Button is visible
  Then I should see the button

# ✅ GOOD - Flow
Scenario: User reports missing pet
  Given I create test data via API
  When I complete the report flow
  Then the announcement appears in the list
  And I delete test data via API
```

### 3. Cross-Platform Scenarios
Same Gherkin scenarios run on all platforms using tags:
```gherkin
@web @ios @android
Scenario: View animal list
  Given I open the animal list
  Then I should see announcements
```

---

## Cucumber Tags

### Platform Tags
| Tag | Description |
|-----|-------------|
| `@web` | Web platform tests |
| `@ios` | iOS platform tests |
| `@android` | Android platform tests |
| `@mobile` | All mobile tests (iOS + Android) |

### Feature Tags
| Tag | Description |
|-----|-------------|
| `@animalList` | Animal list feature tests |
| `@petDetails` | Pet details feature tests |
| `@reportMissing` | Report missing flow tests |

### Status Tags
| Tag | Description |
|-----|-------------|
| `@smoke` | Smoke tests (fast, critical paths) |
| `@pending` | Tests waiting for infrastructure (e.g., geolocation mocking) |
| `@legacy` | Old tests to be removed after migration |

### Tag Filtering
```bash
# Run only active tests (exclude pending and legacy)
mvn test -Dtest=WebTestRunner
# Default filter: @web and not @pending and not @legacy

# Run specific feature
mvn test -Dcucumber.filter.tags="@animalList"

# Run smoke tests only
mvn test -Dcucumber.filter.tags="@smoke and not @legacy"

# Run pending tests (for development)
mvn test -Dcucumber.filter.tags="@pending"
```

---

## Feature Specs

E2E test coverage is organized by feature:

| Spec | Feature | Status |
|------|---------|--------|
| [050-e2e-animal-list](../specs/050-e2e-animal-list/) | Animal List + Location | Draft |
| [051-e2e-pet-details](../specs/051-e2e-pet-details/) | Pet Details | Draft |
| [052-e2e-report-missing](../specs/052-e2e-report-missing/) | Report Missing Flow | Draft |

---

## Troubleshooting

### Web Tests: "Chrome instance exited"

**Cause**: Chrome/ChromeDriver version mismatch or outdated Selenium.

**Solution**: Update dependencies in `pom.xml`:
```xml
<selenium.version>4.27.0</selenium.version>
<webdrivermanager.version>5.9.2</webdrivermanager.version>
```

Then rebuild: `mvn clean compile test-compile`

### Web Tests: Element Not Found

**Cause**: Hardcoded test IDs don't exist in database, or web app not running.

**Solution**:
1. Verify web app is running: `curl http://localhost:8080`
2. Verify backend is running: `curl http://localhost:3000/api/announcements`
3. Check test data - IDs like `11111111-1111-1111-1111-111111111111` must exist

### Mobile Tests: "Response code 404"

**Cause**: Appium 2.x URL changed - no longer uses `/wd/hub` suffix.

**Solution**: In `AppiumDriverManager.java`, URL should be:
```java
// ✅ Correct (Appium 2.x)
"http://127.0.0.1:4723"

// ❌ Wrong (Appium 1.x style)
"http://127.0.0.1:4723/wd/hub"
```

### Mobile Tests: "ANDROID_HOME not set"

**Cause**: Appium server doesn't see `ANDROID_HOME` environment variable.

**Solution**: Start Appium with environment variable:
```bash
export ANDROID_HOME=~/Library/Android/sdk && appium
```

Or add to your shell profile (`~/.zshrc`):
```bash
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH
```

### Mobile Tests: "App launch failed"

**Cause**: APK/App file missing or wrong path.

**Solution**:
1. Build the app:
   ```bash
   # Android
   ./gradlew :composeApp:assembleDebug
   
   # iOS (in Xcode or)
   xcodebuild -scheme iosApp -sdk iphonesimulator
   ```

2. Copy to correct location:
   ```bash
   # Android
   cp composeApp/build/outputs/apk/debug/composeApp-debug.apk \
      e2e-tests/java/apps/petspot-android.apk
   
   # iOS
   cp -r iosApp/build/Debug-iphonesimulator/iosApp.app \
      e2e-tests/java/apps/petspot-ios.app
   ```

### Mobile Tests: "ConnectException"

**Cause**: Appium server not running.

**Solution**:
```bash
# Check if Appium is running
curl http://localhost:4723/status

# If not, start it
appium
```

### Mobile Tests: No devices found

**Cause**: Emulator/Simulator not running.

**Solution**:
```bash
# Android - check connected devices
adb devices
# Should show: emulator-5554  device

# If empty, start emulator
emulator -avd <your_avd_name>

# iOS - open Simulator
open -a Simulator
```

### Duplicate Step Definition Error

**Cause**: Same Cucumber step pattern in multiple Java files.

**Solution**: Search for duplicates and remove one:
```bash
grep -r "@Then.*navigate.*details" e2e-tests/java/src/
```

### iOS Tests: "Could not create simulator with name..."

**Cause**: Default device name in `AppiumDriverManager.java` doesn't match available simulators.

**Solution**: 
1. List available simulators:
   ```bash
   xcrun simctl list devices available | grep iPhone
   ```
2. Update default in `AppiumDriverManager.java` or set env variable:
   ```bash
   export IOS_DEVICE_NAME="iPhone 15"
   export IOS_PLATFORM_VERSION="18.1"
   ```

### iOS/Android Tests: Wrong platform used

**Cause**: Platform not detected from Cucumber tags.

**Solution**: The `Hooks.java` automatically detects platform from tags:
- `@ios` tag → `PLATFORM=iOS`
- `@android` tag → `PLATFORM=Android`
- `@mobile` without specific platform → defaults to Android

If still not working, set manually:
```bash
mvn test -Dtest=IosTestRunner -DPLATFORM=iOS
```

---

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies (Selenium, Appium, Cucumber versions) |
| `utils/WebDriverManager.java` | Chrome configuration and options |
| `utils/AppiumDriverManager.java` | Appium URL, Android/iOS capabilities |
| `resources/test.properties` | Test configuration (URLs, timeouts) |

---

## Dependency Versions (Working as of Dec 2025)

```xml
<selenium.version>4.27.0</selenium.version>
<appium.version>9.3.0</appium.version>
<cucumber.version>7.20.1</cucumber.version>
<junit.version>5.11.3</junit.version>
<webdrivermanager.version>5.9.2</webdrivermanager.version>
```

---

## References

- [Spec 016: E2E Java Migration](../specs/016-e2e-java-migration/) - Original Java stack setup
- [Spec 025: Remove TypeScript E2E](../specs/025-java-e2e-coverage/) - TypeScript removal
- [Constitution](../.specify/memory/constitution.md) - Principle XII: End-to-End Testing
