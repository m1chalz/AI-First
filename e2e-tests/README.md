# PetSpot E2E Testing

End-to-end testing infrastructure for PetSpot multiplatform application covering web (Playwright) and mobile (Appium) platforms.

## Overview

This directory contains E2E tests that validate complete user flows across all platforms:
- **Web**: React TypeScript application (Playwright)
- **Mobile**: Android (Compose) and iOS (SwiftUI) applications (Appium + WebdriverIO)

All tests follow Constitution Principle VI (End-to-End Testing) and Principle XII (Given-When-Then convention).

## Directory Structure

```
e2e-tests/
├── web/                    # Playwright tests for web platform
│   ├── specs/              # Test specifications
│   ├── pages/              # Page Object Model
│   ├── steps/              # Reusable step definitions
│   └── fixtures/           # Test data fixtures
├── mobile/                 # Appium tests for mobile platforms
│   ├── specs/              # Test specifications
│   ├── screens/            # Screen Object Model
│   ├── steps/              # Reusable step definitions
│   └── utils/              # Shared mobile utilities
└── README.md               # This file
```

## Quick Start

### TL;DR - Run Android E2E Tests

**Prerequisites:**
- Android emulator running (or physical device connected)
- Android APK built: `./gradlew :composeApp:assembleDebug`
- Dependencies installed: `cd e2e-tests && npm install`

**Run tests:**
```bash
# Terminal 1: Start Appium (leave running)
cd e2e-tests
npm run appium:start

# Terminal 2: Run Android tests
cd e2e-tests
npm run test:mobile:android
```

That's it!

---

### Setup

**1. Build shared Kotlin/JS module (required for webApp):**
```bash
# From project root
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution
```

**2. Install webApp dependencies:**
```bash
cd webApp
npm install
cd ..
```

**3. Install e2e-tests dependencies:**
```bash
cd e2e-tests
npm install

# Install Playwright browsers
npx playwright install
```

**4. Install Appium drivers (for mobile tests only):**
```bash
# Appium 3.x is already installed locally via node_modules (package.json)
# Install drivers for the local Appium instance

cd e2e-tests
npx appium driver install uiautomator2  # For Android
npx appium driver install xcuitest      # For iOS (macOS only)

# Verify drivers are installed
npx appium driver list --installed
```

**Note**: Appium 3.x ships with this repo via `devDependencies`. Start the server manually with `npm run appium:start` before running mobile specs (`@wdio/appium-service` is disabled in `wdio.conf.ts`).

**5. Build mobile applications (for mobile tests only):**
```bash
# Android
./gradlew :composeApp:assembleDebug

# iOS (requires Xcode on macOS)
# Open iosApp/iosApp.xcodeproj in Xcode
# Build for simulator: Product > Build (Cmd+B)
```

### Prerequisites

**For Web Testing:**
- Node.js v20+ installed
- Java 17+ (for building Kotlin/JS shared module)
- Playwright browsers installed (via `npx playwright install`)
- **webApp must be running** before tests (see Running Tests section)

**For Mobile Testing:**
- Node.js v20+ installed
- Java 17+ (for building Android app)
- Appium 3.x available (bundled locally via `npx appium`; install globally only if you need to)
- Appium drivers: uiautomator2 (Android) and/or xcuitest (iOS)
- Android SDK and emulator/device (for Android tests)
- Xcode and iOS Simulator/device (macOS only, for iOS tests)

### Running Tests

**Web E2E Tests:**

⚠️ **IMPORTANT**: You must start webApp server manually before running tests:

```bash
# Terminal 1: Start web server (from webApp directory)
cd webApp
npm run start  # Runs on http://localhost:8080

# Terminal 2: Run Playwright tests (from e2e-tests directory)
cd e2e-tests
npm run test:web
npm run test:web:ui

# Or from project root
npm run test:e2e:web
npm run test:e2e:web:ui

# Run specific test file
npx playwright test web/specs/example.spec.ts

# Run in headed mode (visible browser)
npx playwright test --headed

# Dry run (validate without executing)
npx playwright test --dry-run
```

Auto-start from Playwright is disabled (see comment in `playwright.config.ts`), so always keep `webApp` running on `http://localhost:8080` while tests execute.

**Mobile E2E Tests:**

⚠️ **IMPORTANT**: You must start Appium server manually before running mobile tests.

```bash
# Terminal 1: Start Appium server (from e2e-tests directory)
cd e2e-tests
npm run appium:start  # Runs on http://localhost:4723

# Terminal 2: Run mobile tests (from e2e-tests directory)
cd e2e-tests
npm run test:mobile:android  # Runs ONLY Android tests
npm run test:mobile:ios       # Runs ONLY iOS tests

# Or from project root
npm run test:mobile:android
npm run test:mobile:ios

# Dry run (validate configuration)
npm run test:mobile:android -- --dry-run

# To stop Appium and free port 4723
npm run clean:appium
```

**Note on platform selection:**
- Tests are filtered by platform using `PLATFORM` environment variable (see `wdio.conf.ts`)
- `test:mobile:android` runs **ONLY** Android platform capabilities
- `test:mobile:ios` runs **ONLY** iOS platform capabilities
- This prevents running all platforms when you only want to test one

**Note on Appium startup:**
Appium 3.x must be started manually (`npm run appium:start` from `e2e-tests`). Auto-start via `@wdio/appium-service` is disabled in `wdio.conf.ts` due to initialization timing issues.

### Verify Setup

Before running tests, verify your environment is configured correctly:

**Check installed tools:**
```bash
# Node.js version (should be v20+)
node --version

# Java version (should be 17+)
java -version

# Playwright version
npx playwright --version

# Local Appium version (should be 3.x)
cd e2e-tests && npx appium --version

# Appium drivers (for mobile tests)
cd e2e-tests && npx appium driver list --installed
# Should show: uiautomator2@6.x (Android) and/or xcuitest (iOS)

# Android SDK location (for Android tests)
echo $ANDROID_HOME
# Should show something like: /Users/yourname/Library/Android/sdk (macOS)
# If empty, set it: export ANDROID_HOME=$HOME/Library/Android/sdk
```

**Check devices (for mobile tests):**
```bash
# Android: List connected devices/emulators
$ANDROID_HOME/platform-tools/adb devices
# Should show at least one device in 'device' state
# Example output:
#   List of devices attached
#   emulator-5554	device

# If no devices, start an emulator:
$ANDROID_HOME/emulator/emulator -list-avds
$ANDROID_HOME/emulator/emulator -avd <avd-name> &

# iOS: List available simulators (macOS only)
xcrun simctl list devices | grep Booted
# Should show at least one booted simulator
```

**Check built applications (for mobile tests):**
```bash
# Verify Android APK exists
ls -lh composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Verify iOS app exists (macOS only)
ls -lh iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app
```

**Quick smoke test:**
```bash
# Web: Validate Playwright configuration
cd e2e-tests
npx playwright test --list

# Mobile: Validate WebdriverIO configuration
# First, ensure Appium is running (Terminal 1):
cd e2e-tests
npm run appium:start

# Then in Terminal 2:
cd e2e-tests
npm run test:mobile:android -- --dry-run
```

## Architecture

### Separation of Concerns

**Pages/Screens** → Store only test IDs and locators (Single Responsibility)  
**Steps** → Contain reusable actions (fillInput, clickElement, navigate)  
**Tests** → Combine pages and steps into Given-When-Then scenarios

### Web Testing (Playwright)

- **Framework**: Playwright with TypeScript
- **Pattern**: Page Object Model (POM) + Step Definitions
- **Test IDs**: `data-testid` attributes with pattern `{screen}.{element}.{action}`
- **Step Definitions**: Reusable actions in `/web/steps/`

**Example:**
```typescript
import { ExamplePage } from '../pages/ExamplePage';
import { waitForElement } from '../steps/urlSteps';
import { fillInput, getElementText } from '../steps/elementSteps';
import { clickElement } from '../steps/mouseSteps';

test('should submit form', async ({ page }) => {
  // Given
  const examplePage = new ExamplePage(page);
  await examplePage.navigate();
  await waitForElement(page, examplePage.testIds.title);
  
  // When
  await fillInput(page, examplePage.testIds.input, 'test');
  await clickElement(page, examplePage.testIds.submitButton);
  
  // Then
  const result = await getElementText(page, examplePage.testIds.result);
  expect(result).toContain('test');
});
```

### Mobile Testing (Appium + WebdriverIO)

- **Framework**: Appium 3.x + WebdriverIO + TypeScript
- **Pattern**: Screen Object Model (SOM) + Step Definitions
- **Test IDs**: `testTag` (Android) / `accessibilityIdentifier` (iOS) with pattern `{screen}.{element}.{action}`
- **Step Definitions**: Reusable actions in `/mobile/steps/`

**Example:**
```typescript
import { ExampleScreen } from '../screens/ExampleScreen';
import { waitForElement } from '../steps/urlSteps';
import { fillInput, getElementText } from '../steps/elementSteps';
import { clickElement } from '../steps/mouseSteps';

describe('Example Feature', () => {
  it('should submit form', async () => {
    // Given
    const screen = new ExampleScreen(driver);
    await waitForElement(driver, screen.testIds.title);
    
    // When
    await fillInput(driver, screen.testIds.input, 'test');
    await clickElement(driver, screen.testIds.submitButton);
    
    // Then
    const result = await getElementText(driver, screen.testIds.result);
    expect(result).toContain('test');
  });
});
```

## Test Writing Guidelines

### 1. Given-When-Then Structure (MANDATORY)

All tests MUST follow Given-When-Then (Arrange-Act-Assert) pattern:

```typescript
test('descriptive test name', async () => {
  // Given - Setup initial state
  const page = new ExamplePage(driver);
  await page.navigate();
  
  // When - Perform action
  await page.clickSubmit();
  
  // Then - Verify outcome
  expect(await page.getResult()).toBe('expected');
});
```

### 2. Test Identifiers (MANDATORY)

All interactive elements MUST have stable test identifiers:

**Web (data-testid):**
```tsx
<button data-testid="petList.addButton.click">Add Pet</button>
```

**Android (testTag):**
```kotlin
Button(
  modifier = Modifier.testTag("petList.addButton.click")
) { Text("Add Pet") }
```

**iOS (accessibilityIdentifier):**
```swift
Button("Add Pet")
  .accessibilityIdentifier("petList.addButton.click")
```

**Naming pattern:** `{screen}.{element}.{action}`

### 3. Page/Screen Object Model (MANDATORY)

Pages/Screens contain ONLY test IDs and locators (no actions):

**Web Page Object:**
```typescript
export class PetListPage {
  readonly page: Page;
  
  // Test IDs
  readonly testIds = {
    addButton: 'petList.addButton.click',
    petItem: (id: string) => `petList.item.${id}`,
  };
  
  // Locators (optional, for convenience)
  readonly addButtonLocator: Locator;
  
  constructor(page: Page) {
    this.page = page;
    this.addButtonLocator = page.getByTestId(this.testIds.addButton);
  }
  
  async navigate(): Promise<void> {
    await navigateTo(this.page, '/pets');
  }
}
```

**Mobile Screen Object:**
```typescript
export class PetListScreen {
  private driver: WebdriverIO.Browser;
  
  // Test IDs only
  readonly testIds = {
    addButton: 'petList.addButton.click',
    petItem: (id: string) => `petList.item.${id}`,
  };
  
  constructor(driver: WebdriverIO.Browser) {
    this.driver = driver;
  }
}
```

### 4. Reusable Step Definitions (MANDATORY)

All actions are extracted to step definitions:

```typescript
// web/steps/urlSteps.ts
export async function navigateTo(page: Page, url: string) {
  await page.goto(url);
}

export async function waitForElement(page: Page, testId: string) {
  await page.getByTestId(testId).waitFor({ state: 'visible' });
}

// web/steps/elementSteps.ts  
export async function getElementText(page: Page, testId: string) {
  return await page.getByTestId(testId).textContent() || '';
}

export async function fillInput(page: Page, testId: string, text: string) {
  await page.getByTestId(testId).fill(text);
}

// web/steps/mouseSteps.ts
export async function clickElement(page: Page, testId: string) {
  await page.getByTestId(testId).click();
}

// Usage in tests (NOT in Page Objects)
await clickElement(page, petListPage.testIds.addButton);
```

## Configuration

### Playwright Configuration

File: `e2e-tests/playwright.config.ts`

Key settings:
- Test directory: `./web/specs`
- Base URL: `http://localhost:8080`
- Browsers: Chromium, Firefox, WebKit
- Reporters: HTML, List
- Retries: 2 on CI, 0 locally
- Web server: Manual start required (`npm run start` in `webApp`; auto-start block commented out)

### WebdriverIO Configuration

File: `e2e-tests/wdio.conf.ts`

Key settings:
- Specs: `./mobile/specs/**/*.spec.ts`
- Appium service: Disabled; start server manually on port 4723 (`npm run appium:start`)
- Capabilities: Android (UiAutomator2), iOS (XCUITest)
- Framework: Mocha
- Reporters: Spec, JUnit

## Debugging

### Web Tests (Playwright)

```bash
# Run with UI mode (best for debugging)
npx playwright test --ui

# Run in headed mode
npx playwright test --headed

# Debug specific test
npx playwright test --debug example.spec.ts

# Trace viewer (after test run)
npx playwright show-trace trace.zip
```

### Mobile Tests (Appium)

```bash
# Verbose logging
npm run test:mobile:android -- --logLevel trace

# Appium inspector
# 1. Start Appium server: appium
# 2. Open Appium Inspector
# 3. Configure capabilities from wdio.conf.ts
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: E2E Tests

on: [push, pull_request]

jobs:
  web-e2e:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: npm install
      - run: npx playwright install --with-deps
      - run: npm run test:e2e:web
      
  mobile-e2e:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: npm install
      - run: npm run test:mobile:ios
```

## Troubleshooting

### Web (Playwright)

**Problem: Tests timing out**
```bash
# Increase timeout in playwright.config.ts
use: {
  actionTimeout: 30000,  // 30 seconds
}
```

**Problem: Browser not found**
```bash
npx playwright install chromium firefox webkit
```

**Problem: Base URL not responding**
```bash
# Ensure web app is running
npm run start  # In separate terminal
```

### Mobile (Appium)

**Problem: Connection refused / Appium not responding**
```bash
# Make sure Appium server is running
lsof -i :4723 | grep LISTEN

# Start Appium server (Terminal 1)
cd e2e-tests
npm run appium:start
```

**Problem: Port already in use**
```bash
# Use cleanup script to kill process on port 4723
cd e2e-tests
npm run clean:appium
```

**Problem: "Neither ANDROID_HOME nor ANDROID_SDK_ROOT" error**
```bash
# Set ANDROID_HOME environment variable
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
# Or on Linux: export ANDROID_HOME=$HOME/Android/Sdk

# Make permanent by adding to ~/.zshrc or ~/.bash_profile
echo 'export ANDROID_HOME=$HOME/Library/Android/sdk' >> ~/.zshrc
```

**Problem: Cannot find device or emulator**
```bash
# Check connected devices
$ANDROID_HOME/platform-tools/adb devices

# Start emulator if needed
$ANDROID_HOME/emulator/emulator -avd <avd-name> &
```


## Best Practices

1. ✅ **Write tests for user flows, not implementation**
2. ✅ **Use stable test identifiers, not text or XPath**
3. ✅ **Follow Given-When-Then structure**
4. ✅ **Keep tests independent and isolated**
5. ✅ **Use Page/Screen Object Model**
6. ✅ **Extract reusable steps**
7. ✅ **Add meaningful assertions**
8. ✅ **Clean up test data after tests**
9. ✅ **Run tests in parallel when possible**
10. ✅ **Keep tests fast (< 30s per test)**

## Resources

- [Playwright Documentation](https://playwright.dev/)
- [WebdriverIO Documentation](https://webdriver.io/)
- [Appium Documentation](https://appium.io/)
- [Constitution Principle VI](../.specify/memory/constitution.md#vi-end-to-end-testing)
- [Constitution Principle XII](../.specify/memory/constitution.md#xii-given-when-then-test-convention)

## Support

For issues or questions:
1. Check this documentation
2. Review example tests in `specs/`
3. Review Constitution principles

---

**Last Updated**: 2024-11-18  
**Version**: 1.0.0  
**Maintained by**: PetSpot Team

