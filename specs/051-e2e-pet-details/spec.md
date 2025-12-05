# Feature Specification: E2E Tests - Pet Details

**Feature Branch**: `051-e2e-pet-details`  
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
- ❌ BAD: "Photo is visible", "Badge has color", "Button exists"
- ✅ GOOD: "User opens details, verifies pet info, contacts owner via phone"

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

This spec defines E2E test coverage for the Pet Details feature across all platforms. Tests verify:
- Opening pet details (modal on web, screen on mobile)
- Displaying all pet information fields
- Contact actions (phone, email)
- Status badge display (MISSING/FOUND)
- Photo display / placeholder
- Close/back navigation

---

## Related Specs

| Spec | Name | Platform |
|------|------|----------|
| 010 | Pet Details Screen | Mobile (general) |
| 012 | iOS Pet Details Screen | iOS |
| 027 | Web Pet Details Screen | Web |

---

## User Scenarios & Testing

### User Story 1 - Display Pet Information (Priority: P0)

User opens pet details and sees all information about the animal.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I create announcement with full data via API, **When** I open pet details, **Then** I see name, species, breed, status, description
2. **Given** announcement has photo, **When** I open pet details, **Then** I see the pet photo
3. **Given** announcement has NO photo, **When** I open pet details, **Then** I see placeholder "Image not available"

---

### User Story 2 - Contact Owner (Priority: P1)

User can contact the pet owner via phone or email.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** announcement has phone number, **When** I tap phone, **Then** dialer/phone action is triggered
2. **Given** announcement has email, **When** I tap email, **Then** mail composer/email action is triggered
3. **Given** announcement has NO contact info, **Then** contact buttons are not displayed

---

### User Story 3 - Status Badge (Priority: P1)

Pet status is clearly displayed with colored badge.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** pet status is MISSING, **Then** I see red badge with "MISSING" text
2. **Given** pet status is FOUND, **Then** I see blue badge with "FOUND" text

---

### User Story 4 - Loading & Error States (Priority: P2)

User sees appropriate feedback during loading or errors.

**Platforms**: iOS, Android (web uses modal, less relevant)

**Acceptance Scenarios**:

1. **When** pet details are loading, **Then** I see loading indicator
2. **Given** pet ID does not exist, **When** I open details, **Then** I see error message with retry button
3. **Given** error is displayed, **When** I tap retry, **Then** loading starts again

---

### User Story 5 - Navigation (Priority: P1)

User can close details and return to list.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I am viewing pet details, **When** I close/go back, **Then** I return to animal list
2. **Web**: Close button closes modal
3. **Mobile**: Back button/gesture returns to list

---

## Requirements

### Functional Requirements

- **FR-001**: Tests MUST create announcement via API with specific fields for verification
- **FR-002**: Tests MUST verify all displayed fields match API-created data
- **FR-003**: Contact actions MUST be verifiable (at minimum tap triggers action)
- **FR-004**: Status badge color MUST be verified (MISSING=red, FOUND=blue)
- **FR-005**: Photo/placeholder logic MUST be tested

### Test Infrastructure

- **FR-006**: Reuse `TestDataApiHelper` from 050
- **FR-007**: Feature file at `/e2e-tests/java/src/test/resources/features/pet-details.feature`
- **FR-008**: Tags: @petDetails, @web, @ios, @android

---

## Success Criteria

- **SC-001**: All 5 User Stories have passing E2E tests
- **SC-002**: Tests pass on all 3 platforms
- **SC-003**: Field verification matches API data exactly
- **SC-004**: Contact actions are testable

---

## Test Scenarios Summary

| # | Scenario | Web | iOS | Android |
|---|----------|-----|-----|---------|
| 1 | Display all pet info | ✓ | ✓ | ✓ |
| 2 | Display photo | ✓ | ✓ | ✓ |
| 3 | Display placeholder (no photo) | ✓ | ✓ | ✓ |
| 4 | Tap phone | ✓ | ✓ | ✓ |
| 5 | Tap email | ✓ | ✓ | ✓ |
| 6 | Status badge MISSING (red) | ✓ | ✓ | ✓ |
| 7 | Status badge FOUND (blue) | ✓ | ✓ | ✓ |
| 8 | Loading state | - | ✓ | ✓ |
| 9 | Error state + retry | - | ✓ | ✓ |
| 10 | Close/back navigation | ✓ | ✓ | ✓ |

**Total: ~10 scenarios × 3 platforms = ~30 test executions**

---

## Dependencies

- Spec 050 completed (TestDataApiHelper, common steps)
- Backend API for creating announcements with all fields
- Photo upload capability or mock photo URL

---

## Out of Scope

- Remove Report button (future feature)
- Map integration
- Share functionality

