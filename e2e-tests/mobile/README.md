# Mobile E2E Testing with Appium

End-to-end testing guide for PetSpot mobile applications (Android & iOS) using Appium, WebdriverIO, and TypeScript.

## Overview

Mobile E2E tests validate Android (Jetpack Compose) and iOS (SwiftUI) applications using Appium with the Screen Object Model pattern.

## Quick Start

```bash
# Install dependencies (from repo root)
npm install

# Ensure Android SDK / Xcode are installed

# Run Android tests
npm run test:mobile:android

# Run iOS tests (macOS only)
npm run test:mobile:ios

# Dry run (validate configuration)
npm run test:mobile:android -- --dry-run
```

## Prerequisites

### Android Testing

1. **Android SDK** installed
2. **Android Emulator** configured or physical device
3. **App APK** built:
   ```bash
   ./gradlew :composeApp:assembleDebug
   ```

### iOS Testing (macOS only)

1. **Xcode** installed (with Command Line Tools)
2. **iOS Simulator** or physical device
3. **App** built for simulator in Xcode

## Directory Structure

```
e2e-tests/mobile/
├── specs/          # Test specifications (*.spec.ts)
├── screens/        # Screen Object Model classes
├── steps/          # Reusable step definitions
├── utils/          # Shared utilities
└── README.md       # This file
```

## Writing Tests

### 1. Create Screen Object

Screen Objects encapsulate screen structure and interactions:

```typescript
// screens/PetListScreen.ts
import { clickElement, getElementText, waitForElement } from '../steps/commonSteps';

export class PetListScreen {
  private driver: WebdriverIO.Browser;

  // Test IDs: {screen}.{element}.{action}
  private readonly testIds = {
    addButton: 'petList.addButton.click',
    petItem: (id: string) => `petList.item.${id}`,
    emptyMessage: 'petList.emptyMessage.display',
  };

  constructor(driver: WebdriverIO.Browser) {
    this.driver = driver;
  }

  async waitForScreenLoad(): Promise<void> {
    await waitForElement(this.driver, this.testIds.addButton);
  }

  async clickAddButton(): Promise<void> {
    await clickElement(this.driver, this.testIds.addButton);
  }

  async getPetName(id: string): Promise<string> {
    return await getElementText(this.driver, this.testIds.petItem(id));
  }

  async isEmptyMessageDisplayed(): Promise<boolean> {
    const selector = `~${this.testIds.emptyMessage}`;
    const element = await this.driver.$(selector);
    return await element.isDisplayed();
  }
}
```

### 2. Add Step Definitions (Optional)

For reusable actions, add to `steps/commonSteps.ts`:

```typescript
// steps/commonSteps.ts

export async function scrollToElement(
  driver: WebdriverIO.Browser,
  testId: string
): Promise<void> {
  const selector = `~${testId}`;
  const element = await driver.$(selector);
  await driver.execute('mobile: scroll', { element });
}
```

### 3. Write Test Spec

Tests follow Given-When-Then structure:

```typescript
// specs/pet-list.spec.ts
import { PetListScreen } from '../screens/PetListScreen';

describe('Pet List Feature - Mobile', () => {
  let petListScreen: PetListScreen;

  beforeEach(async () => {
    petListScreen = new PetListScreen(driver);
  });

  it('should display empty message when no pets', async () => {
    // Given - User opens app with no pets
    await petListScreen.waitForScreenLoad();

    // When - Screen loads
    const isEmpty = await petListScreen.isEmptyMessageDisplayed();

    // Then - Empty message is shown
    expect(isEmpty).toBe(true);
  });

  it('should add new pet', async () => {
    // Given - User is on pet list screen
    await petListScreen.waitForScreenLoad();

    // When - User taps add button
    await petListScreen.clickAddButton();
    // ... fill form and submit ...

    // Then - New pet appears in list
    const petName = await petListScreen.getPetName('123');
    expect(petName).toBe('Max');
  });
});
```

## Test Identifiers

All interactive elements MUST have test identifiers:

### Naming Pattern

`{screen}.{element}.{action}`

Examples:
- `petList.addButton.click`
- `petList.item.123`
- `petForm.nameInput.text`
- `petForm.submitButton.click`

### Adding to Native Code

**Android (Jetpack Compose):**
```kotlin
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

// Good ✅
Button(
  onClick = { onAddClick() },
  modifier = Modifier.testTag("petList.addButton.click")
) {
  Text("Add Pet")
}

TextField(
  value = name,
  onValueChange = { name = it },
  modifier = Modifier.testTag("petForm.nameInput.text")
)

// Bad ❌
Button(onClick = { }) { Text("Add") }  // No testTag
```

**iOS (SwiftUI):**
```swift
import SwiftUI

// Good ✅
Button("Add Pet") {
  onAddTap()
}
.accessibilityIdentifier("petList.addButton.click")

TextField("Name", text: $name)
  .accessibilityIdentifier("petForm.nameInput.text")

// Bad ❌
Button("Add") { }  // No accessibilityIdentifier
```

## Screen Object Model Pattern

### Structure

```typescript
export class MyScreen {
  // 1. Properties
  private driver: WebdriverIO.Browser;
  private readonly testIds = {
    title: 'my.title.display',
    button: 'my.button.click',
  };

  // 2. Constructor
  constructor(driver: WebdriverIO.Browser) {
    this.driver = driver;
  }

  // 3. Wait for load
  async waitForScreenLoad(): Promise<void> {
    await waitForElement(this.driver, this.testIds.title);
  }

  // 4. Actions
  async clickButton(): Promise<void> {
    await clickElement(this.driver, this.testIds.button);
  }

  // 5. Queries
  async getTitle(): Promise<string> {
    return await getElementText(this.driver, this.testIds.title);
  }

  // 6. State checks
  async isButtonEnabled(): Promise<boolean> {
    const selector = `~${this.testIds.button}`;
    const element = await this.driver.$(selector);
    return await element.isEnabled();
  }
}
```

### Best Practices

1. ✅ One Screen Object per screen
2. ✅ Use accessibility IDs (`testTag` / `accessibilityIdentifier`)
3. ✅ Return promises with explicit types
4. ✅ Add JSDoc documentation
5. ✅ Use step definitions for common actions
6. ✅ Keep methods focused
7. ✅ Avoid test assertions in Screen Objects
8. ✅ Handle platform differences gracefully

## Configuration

### wdio.conf.ts

Key settings for mobile tests:

```typescript
export const config: Options.Testrunner = {
  specs: ['./e2e-tests/mobile/specs/**/*.spec.ts'],
  
  capabilities: [{
    platformName: 'Android',  // or 'iOS'
    'appium:deviceName': 'Android Emulator',
    'appium:platformVersion': '13.0',
    'appium:automationName': 'UiAutomator2',  // or 'XCUITest'
    'appium:app': path.join(process.cwd(), './app.apk'),
    'appium:appPackage': 'com.intive.aifirst.petspot',
    'appium:appActivity': '.MainActivity',
  }],
  
  services: [['appium', {
    args: { address: 'localhost', port: 4723 }
  }]],
  
  framework: 'mocha',
  reporters: ['spec'],
};
```

## Running Tests

### Basic Commands

```bash
# Android tests
npm run test:mobile:android

# iOS tests
npm run test:mobile:ios

# Specific file
wdio run wdio.conf.ts --spec e2e-tests/mobile/specs/example.spec.ts

# Dry run (validate)
wdio run wdio.conf.ts --dry-run

# Verbose logging
wdio run wdio.conf.ts --logLevel trace
```

### Platform-Specific Options

```bash
# Android with specific device
wdio run wdio.conf.ts --spec specs/example.spec.ts \
  --appium-device-name="Pixel_5_API_31"

# iOS with specific simulator
wdio run wdio.conf.ts --spec specs/example.spec.ts \
  --appium-device-name="iPhone 14"
```

## Debugging

### 1. Appium Inspector

Best tool for debugging mobile tests:

```bash
# 1. Start Appium server
appium

# 2. Open Appium Inspector (separate app)
# 3. Configure capabilities from wdio.conf.ts:
{
  "platformName": "Android",
  "appium:deviceName": "Android Emulator",
  "appium:app": "/path/to/app.apk",
  "appium:automationName": "UiAutomator2"
}

# 4. Start session and inspect elements
```

### 2. Verbose Logging

```bash
wdio run wdio.conf.ts --logLevel trace
```

### 3. Screenshots

```typescript
// Take screenshot on failure
afterEach(async function() {
  if (this.currentTest?.state === 'failed') {
    await driver.saveScreenshot(`./screenshots/${this.currentTest.title}.png`);
  }
});
```

### 4. Pause Execution

```typescript
it('debug test', async () => {
  await driver.pause(5000);  // Pause for 5 seconds
  await driver.debug();       // Interactive debugging
});
```

## Platform Differences

### Handling Android vs iOS

```typescript
describe('Platform-specific tests', () => {
  it('should handle platform differences', async () => {
    const platform = driver.capabilities.platformName;
    
    if (platform === 'Android') {
      // Android-specific behavior
      await driver.pressKeyCode(4);  // Back button
    } else if (platform === 'iOS') {
      // iOS-specific behavior
      await driver.execute('mobile: swipe', { direction: 'left' });
    }
  });
});
```

### Common Platform Gestures

```typescript
// Android
await driver.pressKeyCode(4);  // Back
await driver.pressKeyCode(3);  // Home

// iOS
await driver.execute('mobile: swipe', { direction: 'left' });
await driver.execute('mobile: pressButton', { name: 'home' });

// Both platforms
await element.touchAction([
  { action: 'press', x: 100, y: 200 },
  { action: 'moveTo', x: 100, y: 400 },
  'release'
]);
```

## Best Practices

### Wait Strategies

```typescript
// Good ✅
await element.waitForDisplayed({ timeout: 5000 });
await driver.waitUntil(async () => {
  return (await element.getText()) === 'Expected';
}, { timeout: 5000 });

// Bad ❌
await driver.pause(5000);  // Arbitrary waits
```

### Element Selection

```typescript
// Good ✅ - Accessibility ID
await driver.$('~petList.button.click');

// Acceptable - Resource ID (Android) / Label (iOS)
await driver.$('id=com.app:id/button');

// Bad ❌ - XPath
await driver.$('//android.widget.Button[@text="Add"]');
```

### Test Independence

```typescript
// Good ✅
beforeEach(async () => {
  // Reset app state
  await driver.reset();
});

// Or use app state management
beforeEach(async () => {
  await driver.execute('mobile: clearApp');
});
```

## Troubleshooting

### Appium Server Not Starting

```bash
# Check if already running
lsof -i :4723

# Kill existing process
kill -9 $(lsof -t -i:4723)

# Start manually with logging
appium --log-level debug
```

### Element Not Found

1. Use Appium Inspector to verify element exists
2. Check test identifier in native code
3. Add explicit wait:
   ```typescript
   await element.waitForDisplayed({ timeout: 10000 });
   ```
4. Verify correct selector format (`~` for accessibility ID)

### App Not Installing

```bash
# Android
adb devices  # Check device connected
adb install -r app.apk

# iOS
xcrun simctl list  # List simulators
xcrun simctl install booted path/to/app.app
```

### Slow Tests

1. Reduce implicit waits
2. Use explicit waits only when needed
3. Minimize app resets
4. Run tests on faster emulator/simulator

### Session Errors

```bash
# Clear Appium sessions
curl -X DELETE http://localhost:4723/sessions

# Restart Appium server
appium --session-override
```

## CI/CD Integration

### GitHub Actions

```yaml
- name: Set up Android SDK
  uses: android-actions/setup-android@v2
  
- name: Start emulator
  run: |
    emulator -avd test_avd -no-window &
    adb wait-for-device
    
- name: Run mobile tests
  run: npm run test:mobile:android
  
- uses: actions/upload-artifact@v3
  if: always()
  with:
    name: mobile-test-screenshots
    path: e2e-tests/mobile/screenshots/
```

## Device Management

### Android Emulators

```bash
# List AVDs
emulator -list-avds

# Create new AVD
avdmanager create avd -n test_device -k "system-images;android-31;google_apis;x86_64"

# Start emulator
emulator -avd test_device -no-snapshot-load

# List running devices
adb devices
```

### iOS Simulators

```bash
# List available simulators
xcrun simctl list devices available

# Boot simulator
xcrun simctl boot "iPhone 14"

# Open Simulator app
open -a Simulator

# Install app
xcrun simctl install booted path/to/app.app
```

## Resources

- [Appium Documentation](https://appium.io/docs/)
- [WebdriverIO Documentation](https://webdriver.io/)
- [Appium Inspector](https://github.com/appium/appium-inspector)
- [Android Testing](https://developer.android.com/training/testing)
- [iOS Testing](https://developer.apple.com/documentation/xctest)
- [Main E2E README](../README.md)

## Common Patterns

### Login Flow

```typescript
export class LoginScreen {
  async login(username: string, password: string): Promise<void> {
    await fillInput(this.driver, 'login.username.input', username);
    await fillInput(this.driver, 'login.password.input', password);
    await clickElement(this.driver, 'login.submit.button');
    await waitForElement(this.driver, 'home.title.display');
  }
}
```

### List Handling

```typescript
export class PetListScreen {
  async getPetCount(): Promise<number> {
    const elements = await this.driver.$$('~petList.item');
    return elements.length;
  }

  async scrollToPet(id: string): Promise<void> {
    await driver.execute('mobile: scroll', {
      strategy: 'accessibility id',
      selector: `petList.item.${id}`
    });
  }
}
```

---

**Version**: 1.0.0  
**Last Updated**: 2024-11-18

