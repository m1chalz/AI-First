# Tasks: E2E Tests - Animal List

**Branch**: `050-e2e-animal-list`  
**Spec**: [spec.md](./spec.md)

---

## Task Overview

| # | Task | Priority | Status |
|---|------|----------|--------|
| 1 | Test Infrastructure (API Helper) | P0 | ✅ |
| 2 | Feature File | P0 | ✅ |
| 3 | Web Implementation | P1 | ✅ |
| 4 | iOS Implementation | P2 | ✅ |
| 5 | Android Implementation | P2 | ✅ |
| 6 | Reorganize Features Folder | P1 | ✅ |
| 7 | Update Test Runners | P1 | ✅ |
| 8 | Geolocation Testing (Docker Selenium) | P3 | ⏳ |

---

## Task 1: Test Infrastructure ✅

### 1.1 Create TestDataApiHelper ✅
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/utils/TestDataApiHelper.java`
- **Status**: ✅ DONE

**Methods**:
- `createAnnouncement(Map<String, String> data)` - creates announcement via API
- `deleteAnnouncement(String id)` - deletes announcement via admin API
- `getAnnouncement(String id)` - gets announcement for verification
- `cleanupAllCreatedAnnouncements()` - cleanup all test data

### 1.2 Create Common Step Definitions ✅
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/utils/CommonSteps.java`
- **Status**: ✅ DONE

**Steps**:
```gherkin
Given I create a test announcement via API with name {string} and species {string}
Given I create a test announcement at coordinates {string} {string} with name {string}
And I delete the test announcement via API
And I delete all test announcements via API
```

---

## Task 2: Feature Files ✅

**Split into separate files (2025-12-17):**

### animal-list-web.feature ✅ WORKING
- **File**: `/e2e-tests/java/src/test/resources/features/animal-list-web.feature`
- **Status**: ✅ PASSING (2 scenarios)

**Scenarios**:
1. `@smoke @location` - User views animal list with all UI elements
2. Web user sees full list without location and filtered list with location

### animal-list-mobile.feature ⏳ BLOCKED  
- **File**: `/e2e-tests/java/src/test/resources/features/animal-list-mobile.feature`
- **Status**: ⏳ Infrastructure blocked (iOS/Android issues)

**Scenarios**:
1. `@ios @android @smoke @location` - User views animal list with all UI elements
2. `@ios @android @pending-*` - Mobile user sees full list without location + rationale popup
3. `@ios @android @locationDialog` - User reinstalls app and sees rationale dialog
4. `@ios @android @location` - User sees empty state when no animals in current location

---

## Task 3: Web Implementation ✅

### 3.1 Update PetListPage.java ✅
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/PetListPage.java`
- **Status**: ✅ DONE

### 3.2 Update PetListWebSteps.java ✅
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/PetListWebSteps.java`
- **Status**: ✅ DONE

**New steps added**:
```gherkin
When I scroll down the page
Then I should see empty state message
```

---

## Task 4: iOS Implementation ✅

### 4.1 PetListScreen.java ✅
- **Status**: ✅ Uses existing screen with dual annotations

### 4.2 PetListMobileSteps.java ✅
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/PetListMobileSteps.java`
- **Status**: ✅ DONE - Shared implementation with Android

### 4.3 Test Results ✅
- **Test 1 (smoke)**: ✅ PASSED
- Uses `XCUITest` for iOS automation
- Same swipe scroll approach works on iOS

---

## Task 5: Android Implementation ✅

### 5.1 PetListScreen.java ✅
- **Status**: ✅ Uses existing screen with dual annotations
- Added `performSwipeScroll()` for reliable Compose LazyColumn scrolling

### 5.2 PetListMobileSteps.java ✅
- **Status**: ✅ Shared implementation with iOS
- Uses page source for element detection (avoids accidental clicks)
- `scrollUntilAnnouncementVisible()` with direct swipe gestures

### 5.3 AppiumDriverManager.java ✅
- `restartApp()` - terminateApp + activateApp for fresh data reload
- Permissions always granted (no location blocking)

### 5.4 Test Results ✅
- **Test 1 (smoke)**: ✅ PASSED
- Uses `UiAutomator2` for Android automation
- Scroll works reliably with direct swipe gestures

---

## Task 6: Reorganize Features Folder ✅

### Old Structure:
```
features/
├── mobile/
│   ├── pet-list.feature
│   └── pet-details.feature
└── web/
    └── animal-list.feature
```

### New Structure (2025-12-17):
```
features/
├── animal-list-web.feature    ← @web (2 scenarios - WORKING ✅)
├── animal-list-mobile.feature ← @mobile @ios @android (4 scenarios - infrastructure blocked)
├── pet-details.feature        ← @web @ios @android @pending (placeholder)
├── report-missing.feature     ← @web @ios @android @pending (placeholder)
└── legacy/
    ├── pet-list.feature       ← @mobile @legacy
    └── pet-details.feature    ← @mobile @ios @legacy
```

---

## Task 7: Update Test Runners ✅

### WebTestRunner.java ✅
- **Change**: `@SelectClasspathResource("features/web")` → `@SelectClasspathResource("features")`
- **Filter**: `@web and not @pending and not @legacy`

### IosTestRunner.java ✅
- **Already correct**: `@SelectClasspathResource("features")`
- **Filter**: `@ios and not @pending and not @legacy`

### AndroidTestRunner.java ✅
- **Already correct**: `@SelectClasspathResource("features")`
- **Filter**: `@android and not @pending and not @legacy`

---

## Task 8: Geolocation Testing (Docker Selenium) ⏳

Test 2 (location filtering + empty state) requires geolocation mocking which needs:
- Chrome DevTools Protocol (CDP) support
- Compatible Chrome version (v131 or older)

**Current Issue**: Local Chrome (v142) is too new for Selenium 4.29.0 CDP support.

**Solution**: Use Docker Selenium Grid with Chrome v131.

**Spec created**: `specs/053-selenium-docker/`

---

## Task 9: iOS Test Infrastructure Issues ⚠️

### Issue 1: Simulator Not Auto-Started
- **Status**: TODO - investigate
- **Description**: E2E test does not automatically start iOS Simulator
- **Impact**: User must manually open Simulator before running tests
- **Expected**: Test should auto-start simulator or check if running
- **Action**: Check AppiumDriverManager.java iOS driver creation

### Issue 2: WebDriverAgent Connection Loop
- **Status**: TODO - investigate  
- **Description**: After manual simulator start, Appium loops with:
  ```
  [XCUITestDriver@86fa] connect ECONNREFUSED 127.0.0.1:8100
  [XCUITestDriver@c216] Matched '/status' to command name 'getStatus'
  [XCUITestDriver@c216] Proxying [GET /status] to [GET http://127.0.0.1:8100/status] with no body
  ```
- **Impact**: Tests cannot start - Appium cannot connect to WebDriverAgent
- **Root Cause**: WebDriverAgent not running in simulator on port 8100
- **Possible Solutions**:
  - WebDriverAgent needs to be built/installed in simulator
  - Check Appium XCUITest driver installation: `appium driver list`
  - Try manual WDA setup: `xcrun simctl install booted <path-to-wda>`
  - Check if Xcode command line tools configured: `xcode-select -p`
- **Related**: Cursor sandbox blocks `~/.appium` access (see PROGRESS-2025-12-16.md)
- **Action**: Must run from external terminal (iTerm/Terminal.app), not Cursor

### Next Steps
1. Verify Appium XCUITest driver installed: `appium driver list`
2. Check WebDriverAgent location: `ls ~/.appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent/`
3. Try building WDA manually if needed
4. Document required pre-setup for iOS E2E tests

---

## Execution Commands

```bash
# Run all @animalList tests on Web (only non-pending, non-legacy)
cd e2e-tests/java
mvn test -Dtest=WebTestRunner -Dcucumber.filter.tags="@animalList"

# Run all @animalList tests on iOS
mvn test -Dtest=IosTestRunner -Dcucumber.filter.tags="@animalList"

# Run all @animalList tests on Android
mvn test -Dtest=AndroidTestRunner -Dcucumber.filter.tags="@animalList"

# Run smoke tests only (all platforms)
mvn test -Dcucumber.filter.tags="@smoke and not @legacy and not @pending"
```

---

## Definition of Done

- [x] TestDataApiHelper.java creates/deletes announcements
- [x] Feature files with @animalList tag (split: web + mobile)
- [x] Web tests pass (2 scenarios) ✅ **WORKING**
- [x] iOS step definitions implemented
- [x] Android step definitions implemented
- [x] Runners updated to read from new features/ structure
- [x] Features folder reorganized (web/mobile split)
- [x] No dependency on seed data
- [x] Web location filtering + empty state works ✅ **WORKING (2025-12-17)**
- [ ] Android tests pass (blocked - infrastructure issues)
- [ ] iOS tests pass (blocked - infrastructure issues)
- [ ] Mobile location filtering + empty state (blocked - requires infrastructure fix)

---

## Summary of Changes (Session 2025-12-04)

1. **Added scroll + button verification** to Test 1 (FR-003 from spec 005)
2. **Added empty state verification** to Test 2 (FR-019 from spec 032)
3. **Reorganized features folder** - unified structure with legacy subfolder
4. **Updated all Runners** to use new features/ path and exclude @legacy
5. **Created placeholder files** for pet-details.feature and report-missing.feature

## Summary of Changes (Session 2025-12-05)

1. **Android Test 1 PASSED** - smoke test working end-to-end
2. **iOS Test 1 PASSED** - smoke test working end-to-end
3. **Fixed mobile scrolling** - direct swipe gestures instead of UiScrollable (Compose compatibility)
4. **Fixed element detection** - page source check avoids accidental clicks on mobile cards
5. **Added app restart** - terminateApp + activateApp ensures fresh data after API calls
6. **Fixed breed visibility** - test data uses petName as breed for UI visibility
7. **Platform detection fix** - runners set PLATFORM property explicitly

## Summary of Changes (Session 2025-12-17)

1. **Split feature file** - `animal-list.feature` → `animal-list-web.feature` + `animal-list-mobile.feature`
2. **Web tests PASSING** - 2 scenarios working with location filtering ✅
3. **Location filtering via URL params** - `?e2eLat=X&e2eLng=Y` for web (CDP version mismatch workaround)
4. **Fixed breed in test data** - `CommonSteps.java` now sets `breed = petName` for iOS visibility
5. **Mobile tests blocked** - iOS infrastructure issues (Settings app opened instead of PetSpot after restart)

### Test Results (Web - 2025-12-17)

| Scenario | Status |
|----------|--------|
| User views animal list with all UI elements | ✅ PASSED |
| Web user sees full list without location and filtered list with location | ✅ PASSED |

**Location filtering verified:**
- Without location: 10 announcements visible (all)
- With location (Wroclaw): 6 announcements visible (nearby only)
- `E2E-DistantPet` correctly filtered out when location set ✅
