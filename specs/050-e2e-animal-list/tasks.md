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
| 4 | iOS Implementation | P2 | ⚠️ (permissions issue) |
| 5 | Android Implementation | P2 | ⚠️ (not tested yet) |
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

## Task 2: Feature File ✅

- **File**: `/e2e-tests/java/src/test/resources/features/animal-list.feature`
- **Status**: ✅ DONE (reorganized from `features/web/`)

**Content** (2 scenarios):
```gherkin
@animalList
Feature: Animal List

  Background:
    Given the application is running

  # Test 1: Display animal list with UI elements
  @web @ios @android @smoke
  Scenario: User views animal list with all UI elements
    Given I create a test announcement via API with name "E2E-TestDog" and species "DOG"
    When I navigate to the pet list page
    Then the page should load successfully
    And I should see the announcement for "E2E-TestDog"
    And I should see the "Report a Missing Animal" button
    When I scroll down the page
    Then I should see the "Report a Missing Animal" button
    And I delete the test announcement via API

  # Test 2: Location-based filtering + Empty state (PENDING)
  @web @ios @android @pending
  Scenario: User sees only nearby animals and empty state when no animals in area
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-NearbyPet"
    When I navigate to the pet list page with location "40.7" "-74.0"
    Then the page should load successfully
    And I should NOT see the announcement for "E2E-NearbyPet"
    And I should see empty state message
    When I navigate to the pet list page with location "51.1" "17.0"
    Then the page should load successfully
    And I should see the announcement for "E2E-NearbyPet"
    And I should see the "Report a Missing Animal" button
    And I delete all test announcements via API
```

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

## Task 4: iOS Implementation ⚠️

### 4.1 PetListScreen.java ✅
- **Status**: ✅ Uses existing screen with dual annotations

### 4.2 PetListMobileSteps.java ✅
- **File**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/PetListMobileSteps.java`
- **Status**: ✅ DONE - All required steps implemented

**New steps added**:
```gherkin
When I scroll down the page
Then I should see empty state message
```

### 4.3 Known Issue: iOS Location Permissions ⚠️
- **Issue**: When app launches, iOS redirects to Settings screen for location permissions
- **Root Cause**: PetSpot requires location permissions; iOS shows Settings instead of permission alert
- **Workaround Options**:
  1. Pre-grant permissions: `xcrun simctl privacy <device-id> grant location com.petspot.app`
  2. Handle Settings screen navigation in test code
  3. Use `appium:autoGrantPermissions` capability (iOS 16.4+)

---

## Task 5: Android Implementation ⚠️

### 5.1 PetListScreen.java ✅
- **Status**: ✅ Uses existing screen with dual annotations

### 5.2 PetListMobileSteps.java ✅
- **Status**: ✅ Same as iOS - shared implementation

### 5.3 Status: Not Tested Yet ⚠️
- Android tests have not been executed yet
- May have similar permission issues as iOS

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

### New Structure:
```
features/
├── animal-list.feature        ← @web @ios @android (active)
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
- [x] Feature file has scenarios with @animalList tag
- [x] Web tests pass (Test 1 - smoke)
- [x] iOS step definitions implemented
- [x] Android step definitions implemented
- [x] Runners updated to read from new features/ structure
- [x] Features folder reorganized (legacy/ subfolder)
- [x] No dependency on seed data
- [ ] iOS tests pass (blocked by location permissions issue)
- [ ] Android tests pass (not tested yet)
- [ ] Location filtering + empty state works (blocked by Chrome version - spec 053)

---

## Summary of Changes (Session 2025-12-04)

1. **Added scroll + button verification** to Test 1 (FR-003 from spec 005)
2. **Added empty state verification** to Test 2 (FR-019 from spec 032)
3. **Reorganized features folder** - unified structure with legacy subfolder
4. **Updated all Runners** to use new features/ path and exclude @legacy
5. **Created placeholder files** for pet-details.feature and report-missing.feature
