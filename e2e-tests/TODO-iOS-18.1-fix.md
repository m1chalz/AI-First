# TODO: Fix iOS 18.1 Appium Incompatibility

## Problem
**iOS 18.1 + Appium (current version) incompatibility with `getPageSource()`**

### Error:
```
org.openqa.selenium.WebDriverException: 
An unknown server-side error occurred while processing the command. 
Original error: -[XCUIApplicationProcess waitForQuiescenceIncludingAnimationsIdle:]: 
unrecognized selector sent to instance 0x...
```

### Root Cause:
- iOS 18.1 removed or changed `waitForQuiescenceIncludingAnimationsIdle:` method
- Appium's XCUITest driver calls this method when executing `getPageSource()`
- This affects ALL methods that use `driver.getPageSource()`

## Affected Files

### `PetListMobileSteps.java` - Multiple methods:
1. **`iScrollUntilISeeTheAnnouncementFor()` (line ~938)**
   - Used to find announcements while scrolling
   - Called by: Test 1, Test 2

2. **`iShouldSeeLocationRationaleDialog()` (line ~849)**
   - Used to verify rationale dialog is visible
   - Called by: Test 2, Test 3

3. **Other potential uses** (need to check)

## Solutions (in order of preference)

### Option 1: Update Appium (RECOMMENDED)
```bash
npm install -g appium@latest
npm install -g appium-xcuitest-driver@latest
```
Check if newer versions fix iOS 18.1 compatibility.

### Option 2: Replace getPageSource() with findElements()

**For scrolling:**
```java
// OLD (broken):
String pageSource = driver.getPageSource();
if (pageSource.contains(petName)) { ... }

// NEW (works):
var elements = driver.findElements(
    By.xpath("//*[contains(@label, '" + petName + "')]")
);
if (!elements.isEmpty()) { ... }
```

**For dialog detection:**
```java
// OLD (broken):
String pageSource = driver.getPageSource();
if (pageSource.contains("location") || pageSource.contains("Location")) { ... }

// NEW (works):
var dialogs = driver.findElements(
    By.xpath("//*[contains(@label, 'location') or contains(@label, 'Location')]")
);
if (!dialogs.isEmpty()) { ... }
```

### Option 3: Use iOS 17.x simulator
Downgrade simulator to iOS 17.5 which doesn't have this bug.

### Option 4: Disable waitForQuiescence
Add to iOS capabilities:
```java
options.setCapability("waitForQuiescence", false);
```
**Note:** Already set in current code but doesn't fix the issue.

## Action Items

- [ ] Try updating Appium to latest version
- [ ] If update doesn't work, replace ALL `getPageSource()` calls with `findElements()`
- [ ] Test on both iOS 17.x and 18.1 to ensure compatibility
- [ ] Update e2e-tests README with iOS version requirements

## Workaround (Temporary)
Mark iOS tests as `@pending-ios` until fixed:
```gherkin
@mobile @ios @android @pending-ios
Scenario: ...
```

## References
- Appium issue tracker (check for iOS 18.1 compatibility issues)
- XCUITest driver GitHub repository



