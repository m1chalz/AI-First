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
│   ├── fixtures/           # Test data fixtures
│   └── README.md           # Web testing guide
├── mobile/                 # Appium tests for mobile platforms
│   ├── specs/              # Test specifications
│   ├── screens/            # Screen Object Model
│   ├── steps/              # Reusable step definitions
│   ├── utils/              # Shared mobile utilities
│   └── README.md           # Mobile testing guide
└── README.md               # This file
```

## Quick Start

### Prerequisites

**For Web Testing:**
```bash
npm install
npx playwright install  # Install browsers
```

**For Mobile Testing:**
```bash
npm install

# Android
- Android SDK installed
- Android emulator running OR physical device connected

# iOS (macOS only)
- Xcode installed
- iOS Simulator running OR physical device connected
```

### Running Tests

**Web E2E Tests:**
```bash
# Run all web tests
npm run test:e2e:web

# Run with UI mode (interactive)
npm run test:e2e:web:ui

# Run specific test file
npx playwright test e2e-tests/web/specs/example.spec.ts

# Run in headed mode (visible browser)
npx playwright test --headed

# Dry run (validate without executing)
npx playwright test --dry-run
```

**Mobile E2E Tests:**
```bash
# Run Android tests
npm run test:mobile:android

# Run iOS tests
npm run test:mobile:ios

# Dry run (validate configuration)
npm run test:mobile:android -- --dry-run
```

## Architecture

### Web Testing (Playwright)

- **Framework**: Playwright with TypeScript
- **Pattern**: Page Object Model (POM)
- **Test IDs**: `data-testid` attributes with pattern `{screen}.{element}.{action}`
- **Step Definitions**: Reusable actions in `/web/steps/`

**Example:**
```typescript
import { ExamplePage } from '../pages/ExamplePage';

test('should submit form', async ({ page }) => {
  // Given
  const examplePage = new ExamplePage(page);
  await examplePage.navigate();
  
  // When
  await examplePage.fillInput('test');
  await examplePage.clickSubmit();
  
  // Then
  const result = await examplePage.getResult();
  expect(result).toContain('test');
});
```

### Mobile Testing (Appium + WebdriverIO)

- **Framework**: Appium 2.x + WebdriverIO + TypeScript
- **Pattern**: Screen Object Model (SOM)
- **Test IDs**: `testTag` (Android) / `accessibilityIdentifier` (iOS) with pattern `{screen}.{element}.{action}`
- **Step Definitions**: Reusable actions in `/mobile/steps/`

**Example:**
```typescript
import { ExampleScreen } from '../screens/ExampleScreen';

describe('Example Feature', () => {
  it('should submit form', async () => {
    // Given
    const screen = new ExampleScreen(driver);
    await screen.waitForScreenLoad();
    
    // When
    await screen.fillInput('test');
    await screen.clickSubmit();
    
    // Then
    const result = await screen.getResult();
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

Separate test logic from element locators:

**Web Page Object:**
```typescript
export class PetListPage {
  readonly page: Page;
  readonly addButtonLocator: Locator;
  
  constructor(page: Page) {
    this.page = page;
    this.addButtonLocator = page.getByTestId('petList.addButton.click');
  }
  
  async clickAddButton(): Promise<void> {
    await this.addButtonLocator.click();
  }
}
```

**Mobile Screen Object:**
```typescript
export class PetListScreen {
  private driver: WebdriverIO.Browser;
  
  async clickAddButton(): Promise<void> {
    await clickElement(this.driver, 'petList.addButton.click');
  }
}
```

### 4. Reusable Step Definitions

Common actions should be extracted to step definitions:

```typescript
// web/steps/commonSteps.ts
export async function navigateTo(page: Page, url: string) {
  await page.goto(url);
}

// Usage in Page Object
async navigate(): Promise<void> {
  await navigateTo(this.page, '/pets');
}
```

## Configuration

### Playwright Configuration

File: `playwright.config.ts` (repo root)

Key settings:
- Test directory: `./e2e-tests/web/specs`
- Base URL: `http://localhost:3000`
- Browsers: Chromium, Firefox, WebKit
- Reporters: HTML, List
- Retries: 2 on CI, 0 locally

### WebdriverIO Configuration

File: `wdio.conf.ts` (repo root)

Key settings:
- Specs: `./e2e-tests/mobile/specs/**/*.spec.ts`
- Appium service: Auto-start on port 4723
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

**Problem: Appium server not starting**
```bash
# Install/update Appium
npm install -g appium

# Start manually
appium --port 4723
```

**Problem: No emulator/simulator**
```bash
# Android
emulator -list-avds
emulator -avd <avd-name>

# iOS
xcrun simctl list devices
open -a Simulator
```

**Problem: App not installed**
```bash
# Build Android app
./gradlew :composeApp:assembleDebug

# Build iOS app
# Open Xcode and build for simulator
```

**Problem: Element not found**
- Verify test identifiers in app code
- Use Appium Inspector to check actual IDs
- Check if element needs wait time to appear

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
3. Check platform-specific README files
4. Review Constitution principles

---

**Last Updated**: 2024-11-18  
**Version**: 1.0.0  
**Maintained by**: PetSpot Team

