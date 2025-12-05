# Feature Specification: E2E Tests - Animal List

**Feature Branch**: `050-e2e-animal-list`  
**Created**: 2025-12-04  
**Status**: Draft  
**Platforms**: Web, iOS, Android

---

## ⚠️ E2E Testing Principles (MANDATORY)

### 1. Review All Related Specs
Before implementing E2E tests, **MUST review all delivered specs** related to the feature:
- Check feature specs for acceptance criteria
- Check platform-specific specs (iOS, Android, Web) for UI details
- Check backend specs for API contracts

### 2. Test Flows, Not Atomic Features
E2E tests **MUST test complete user flows**, not atomic features:
- ❌ BAD: "Button is visible", "Field accepts input", "Loading shows"
- ✅ GOOD: "User opens list, filters by location, taps animal, sees details"

### 3. API-Driven Test Data
Tests **MUST create their own data via API**:
- No dependency on seed data
- Each test is self-contained
- Cleanup after test (pass or fail)

### 4. Cross-Platform Consistency
Same scenarios **MUST run on all platforms** (Web, iOS, Android):
- Use Cucumber tags for platform selection
- Shared Gherkin, platform-specific step implementations

---

## Overview

This spec defines E2E test coverage for the Animal List feature across all platforms (Web, iOS, Android). Tests verify:
- Displaying all animal announcements with UI elements
- Location-based filtering (nearby animals only)

**Note**: Date sorting is NOT tested - per backend spec 006 (FR-017): "System MUST return announcements in database default order with no specific sorting applied".

---

## Related Specs

| Spec | Name | Relevance |
|------|------|-----------|
| 005 | Animal List | Core requirements (button visibility while scrolling, empty state) |
| 013 | Animal List Screen | Android UI |
| 015 | iOS Location Permissions | iOS location handling and fallback mode |
| 032 | Web Location Query | Web location filtering, empty state message |
| 033 | Announcements Location Query | Backend API for location filtering |

---

## User Scenarios & Testing

### Test 1: Display Animal List with UI Elements (Priority: P0)

User opens the app and sees the animal list with all UI elements.

**Platforms**: Web, iOS, Android

**What is tested**:
- Page loads successfully
- Announcements are displayed
- "Report a Missing Animal" button is visible
- **Button remains visible while scrolling** (FR-003 from spec 005)

---

### Test 2: Location-Based Filtering + Empty State (Priority: P0) - @pending

User provides their location and sees only animals in their area. When no animals are nearby, empty state is displayed.

**Platforms**: Web, iOS, Android

**Status**: `@pending` - Requires geolocation mocking:
- Web: Selenium CDP (blocked by Chrome 142+ compatibility)
- iOS: Appium GPS simulation or Simulator location settings
- Android: Appium GPS mock

Will be enabled with Docker Selenium Grid (spec 053).

**What is tested**:
- Navigate with location far from any announcements → **empty state message displayed** (FR-019 from spec 032)
- Navigate with location near announcement → announcement is visible
- "Report a Missing Animal" button visible in both states

---

## Requirements

### Functional Requirements

**Test Infrastructure**:

- **FR-001**: `TestDataApiHelper.java` MUST exist with methods for creating/deleting announcements via API
- **FR-002**: Tests MUST create their own data before test and cleanup after (no dependency on seed data)
- **FR-003**: Tests MUST use Cucumber tags: @web, @ios, @android for platform selection
- **FR-004**: Feature file MUST use @animalList tag

**Feature File**:

- **FR-005**: Feature file MUST be at `/e2e-tests/java/src/test/resources/features/animal-list.feature`
- **FR-006**: Scenarios MUST be tagged with platforms they run on (@web @ios @android)
- **FR-007**: Scenarios MUST follow Given-When-Then structure with API setup/cleanup

**Page/Screen Objects**:

- **FR-008**: Web: `PetListPage.java` with methods for list verification, announcement check
- **FR-009**: iOS: `PetListScreen.java` with dual annotations for all elements
- **FR-010**: Android: Same `PetListScreen.java` (dual annotations support both)

**Step Definitions**:

- **FR-011**: Common steps in `CommonSteps.java`: API setup/cleanup, navigation
- **FR-012**: Web steps in `PetListWebSteps.java`: web-specific interactions
- **FR-013**: Mobile steps in `PetListMobileSteps.java`: mobile-specific interactions

---

## Success Criteria

- **SC-001**: Test 1 (smoke) passes on all platforms
- **SC-002**: Tests pass on Web (`mvn test -Dtest=WebTestRunner -Dcucumber.filter.tags="@animalList"`)
- **SC-003**: Tests pass on iOS (`mvn test -Dtest=IosTestRunner -Dcucumber.filter.tags="@animalList"`)
- **SC-004**: Tests pass on Android (`mvn test -Dtest=AndroidTestRunner -Dcucumber.filter.tags="@animalList"`)
- **SC-005**: Tests are self-contained (no seed data dependency)
- **SC-006**: Location filtering works correctly on all platforms (deferred to spec 053 - Docker Selenium)

---

## Test Scenarios Summary

| # | Scenario | Web | iOS | Android | Status |
|---|----------|-----|-----|---------|--------|
| 1 | Display list with UI elements (announcements, report button, button visible on scroll) | ✓ | ✓ | ✓ | ✅ Active |
| 2 | Location filtering + empty state (show nearby, hide far away, empty state when no animals in area) | ✓ | ✓ | ✓ | ⏳ @pending |

**Note**: Date sorting removed - not a backend requirement (see spec 006, FR-017).

**Total: 2 scenarios × 3 platforms = 6 test executions** (currently 1 active + 1 pending)

---

## Dependencies

- Backend running on `http://localhost:3000`
- Web app running on `http://localhost:8080`
- iOS simulator with app installed
- Android emulator with app installed
- Appium server running on `localhost:4723`

---

## Out of Scope

- Pet details navigation and content (covered in 051-e2e-pet-details)
- Report missing animal flow (covered in 052-e2e-report-missing)
- Loading state verification (atomic, not flow)
- Clear location filter (web app doesn't have UI for this - location is automatic from browser)
- Performance testing
- Visual regression testing

