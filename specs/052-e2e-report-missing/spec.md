# Feature Specification: E2E Tests - Report Missing Pet Flow

**Feature Branch**: `052-e2e-report-missing`  
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
- **For Report Missing Flow**: Review specs 017, 018, 019, 028, 031, 034, 035, 037, 038, 039, 040, 041, 042, 043, 044

### 2. Test Flows, Not Atomic Features
E2E tests **MUST test complete user flows**, not atomic features:
- ❌ BAD: "Microchip field validates", "Photo uploads", "Species dropdown works"
- ✅ GOOD: "User completes entire 5-step flow and announcement appears in API"

### 3. API-Driven Test Data
Tests **MUST verify results via API**:
- After flow completion, verify announcement in backend via API
- Cleanup created announcements after test
- Each test is self-contained

### 4. Cross-Platform Consistency
Same scenarios **MUST run on all platforms** (Web, iOS, Android):
- Use Cucumber tags for platform selection
- Shared Gherkin, platform-specific step implementations

---

## Overview

This spec defines E2E test coverage for the complete Report Missing Pet flow across all platforms. The flow consists of 5 steps:

1. **Microchip Number** - Optional chip number entry
2. **Photo** - Photo upload (optional)
3. **Animal Details** - Species, breed, name, description, location
4. **Contact Info** - Phone, email, reward
5. **Summary/Confirmation** - Success message with management password

---

## Related Specs

| Spec | Name | Platform |
|------|------|----------|
| 017 | iOS Missing Pet Flow | iOS |
| 018 | Android Missing Pet Flow | Android |
| 019, 028, 031, 035, 044 | iOS Flow Steps | iOS |
| 038, 040, 042 | Android Flow Steps | Android |
| 034, 037, 039, 041, 043 | Web Flow Steps | Web |

---

## User Scenarios & Testing

### User Story 1 - Complete Happy Path (Priority: P0)

User completes entire flow and creates announcement.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I start report flow, **When** I complete all 5 steps with valid data, **Then** announcement is created in backend
2. **Given** I complete the flow, **Then** I see confirmation with management password
3. **Given** announcement was created, **When** I check API, **Then** all submitted data is saved correctly

---

### User Story 2 - Step 1: Microchip Number (Priority: P1)

User can enter optional microchip number.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I am on microchip screen, **When** I enter valid chip number, **Then** I can proceed to next step
2. **Given** I am on microchip screen, **When** I skip (empty), **Then** I can proceed to next step
3. **Given** microchip field, **Then** it validates format (15 digits)

---

### User Story 3 - Step 2: Photo Upload (Priority: P1)

User can upload pet photo.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I am on photo screen, **When** I upload a photo, **Then** I see photo preview
2. **Given** photo is uploaded, **When** I tap remove, **Then** photo is removed
3. **Given** I am on photo screen, **When** I skip (no photo), **Then** I can proceed

---

### User Story 4 - Step 3: Animal Details (Priority: P0)

User enters animal information.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I am on details screen, **When** I select species, **Then** breed field is enabled
2. **Given** I am on details screen, **When** I fill required fields (species, description), **Then** I can proceed
3. **Given** I am on details screen, **When** I tap GPS button, **Then** location is filled automatically
4. **Given** I am on details screen, **When** required fields empty, **Then** I see validation errors

---

### User Story 5 - Step 4: Contact Information (Priority: P0)

User enters contact details.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I am on contact screen, **When** I enter phone and/or email, **Then** I can proceed
2. **Given** I am on contact screen, **When** no contact info entered, **Then** I see validation error
3. **Given** I am on contact screen, **When** I enter optional reward, **Then** it is saved

---

### User Story 6 - Step 5: Confirmation (Priority: P0)

User sees success confirmation.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I submitted the form, **Then** I see success message
2. **Given** submission succeeded, **Then** I see management password
3. **Given** I am on confirmation, **When** I close/finish, **Then** I return to animal list

---

### User Story 7 - Navigation Between Steps (Priority: P1)

User can navigate back and forth.

**Platforms**: Web, iOS, Android

**Acceptance Scenarios**:

1. **Given** I am on step N, **When** I go back, **Then** I return to step N-1 with data preserved
2. **Given** I filled data on step 2, **When** I go back and forward, **Then** data is still there

---

## Requirements

### Functional Requirements

- **FR-001**: Tests MUST complete full flow and verify API announcement creation
- **FR-002**: Tests MUST verify each step's form fields and validation
- **FR-003**: Tests MUST verify navigation (back/forward) preserves data
- **FR-004**: Photo upload MUST be testable (may require test file)
- **FR-005**: GPS/location MUST be mockable or testable

### Test Infrastructure

- **FR-006**: Feature file at `/e2e-tests/java/src/test/resources/features/report-missing.feature`
- **FR-007**: Page Objects for each step: `MicrochipPage`, `PhotoPage`, `DetailsPage`, `ContactPage`, `SummaryPage`
- **FR-008**: Tags: @reportMissing, @web, @ios, @android
- **FR-009**: Cleanup: Delete created announcement after test

---

## Success Criteria

- **SC-001**: Happy path passes on all 3 platforms
- **SC-002**: Each step's validation is tested
- **SC-003**: Created announcement appears in backend API
- **SC-004**: Navigation between steps works correctly
- **SC-005**: Photo upload works (or gracefully skipped if not testable)

---

## Test Scenarios Summary

| # | Scenario | Web | iOS | Android |
|---|----------|-----|-----|---------|
| 1 | Complete happy path | ✓ | ✓ | ✓ |
| 2 | Skip microchip | ✓ | ✓ | ✓ |
| 3 | Upload photo | ✓ | ✓ | ✓ |
| 4 | Skip photo | ✓ | ✓ | ✓ |
| 5 | Fill animal details | ✓ | ✓ | ✓ |
| 6 | GPS location | ✓ | ✓ | ✓ |
| 7 | Details validation | ✓ | ✓ | ✓ |
| 8 | Contact info | ✓ | ✓ | ✓ |
| 9 | Contact validation | ✓ | ✓ | ✓ |
| 10 | Confirmation displayed | ✓ | ✓ | ✓ |
| 11 | Password displayed | ✓ | ✓ | ✓ |
| 12 | Back navigation | ✓ | ✓ | ✓ |
| 13 | Data preserved on back | ✓ | ✓ | ✓ |

**Total: ~13 scenarios × 3 platforms = ~39 test executions**

---

## Dependencies

- Spec 050 completed (TestDataApiHelper for verification/cleanup)
- Backend API for creating announcements
- Photo upload endpoint
- Location permissions (mobile)

---

## Out of Scope

- Edit existing announcement
- Delete announcement via UI
- Social sharing

