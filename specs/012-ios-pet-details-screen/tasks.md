---
description: "Task list for iOS Pet Details Screen implementation"
handoffs:
  - label: Implement US1
    agent: speckit.implement
    prompt: Implement User Story 1 for iOS Pet Details Screen
    send: true
---

# Tasks: iOS Pet Details Screen

**Input**: Design documents from `/specs/012-ios-pet-details-screen/`
**Prerequisites**: plan.md, spec.md, data-model.md
**Feature Branch**: `012-ios-pet-details-screen`

**Tests**:
- **iOS Unit Tests**: `/iosApp/iosAppTests/` (XCTest), 80% coverage
  - Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- **Mobile E2E Tests**: `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts` (Appium)
  - Run: `npm run test:mobile:ios`

## Phase 1: Setup (iOS Infrastructure)

**Purpose**: iOS-specific project initialization and structure

- [ ] T001 Create feature directory structure in `/iosApp/iosApp/Features/PetDetails/` (optional organization, or stick to standard MVVM-C folders)
- [ ] T002 [P] Verify `iosApp.xcodeproj` is ready for new files

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [ ] T003 Create `PetDetails` domain model in `/iosApp/iosApp/Domain/Models/PetDetails.swift` (full definition from data-model.md)
- [ ] T004 Extend existing `AnimalRepositoryProtocol` in `/iosApp/iosApp/Domain/Repositories/AnimalRepositoryProtocol.swift` with `getPetDetails(id: String) async throws -> PetDetails` method (no separate PetRepository type)
- [ ] T005 Extend existing `AnimalRepository` implementation in `/iosApp/iosApp/Domain/Repositories/AnimalRepository.swift` with `getPetDetails` mock implementation
- [ ] T006 (Future) Register `AnimalRepositoryProtocol` implementation in `/iosApp/iosApp/DI/ServiceContainer.swift` once ServiceContainer is introduced for iOS DI (for now, coordinator creates `AnimalRepository` directly)
- [ ] T007 Create `PetDetailsUiState` enum in `/iosApp/iosApp/ViewModels/PetDetailsUiState.swift` (or nested in ViewModel)
- [ ] T008 Create `PetDetailsViewModel` skeleton in `/iosApp/iosApp/ViewModels/PetDetailsViewModel.swift` (inject Repository)
- [ ] T009 Create `PetDetailsCoordinator` skeleton in `/iosApp/iosApp/Coordinators/PetDetailsCoordinator.swift`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - View Pet Details from List (Priority: P1) 🎯 MVP

**Goal**: Users can navigate to the details screen, see loading/error states, and view the basic screen structure.

**Independent Test**: Verify navigation from list, loading spinner, error handling with retry, and successful data load (displaying at least the photo).

### Tests for User Story 1
- [ ] T010 [US1] Create unit tests for ViewModel loading/error states in `/iosApp/iosAppTests/ViewModels/PetDetailsViewModelTests.swift`
- [ ] T011 [US1] Create Screen Object in `/e2e-tests/mobile/screens/PetDetailsScreen.ts`
- [ ] T012 [US1] Create E2E test spec in `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts` (cover navigation and state scenarios)

### Implementation for User Story 1
- [ ] T013 [US1] Implement `PetDetailsViewModel` loading logic (call repo, update state)
- [ ] T014 [US1] Implement `PetDetailsView` main structure in `/iosApp/iosApp/Views/PetDetailsView.swift` (handle .loading, .error, .loaded cases; ensure root is ScrollView)
- [ ] T015 [US1] Implement basic Photo display in `PetDetailsView` (include fallback "Image not available" state)
- [ ] T016 [US1] Implement `PetDetailsCoordinator` navigation logic (start method, hosting controller)
- [ ] T017 [US1] Implement Retry button logic in `PetDetailsView` and ViewModel
- [ ] T018 [US1] Add accessibility identifiers for View, Retry Button, Error Message

---

## Phase 4: User Story 6 - Identify Pet Status Visually (Priority: P2)

**Goal**: Users see the pet's status (MISSING, FOUND, CLOSED) as a badge on the photo.

**Independent Test**: Verify correct badge color and text appears based on pet status.

### Tests for User Story 6
- [ ] T019 [US6] Add status badge scenarios to `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`
- [ ] T020 [US6] Create unit test for `PetPhotoWithBadgesModel` mapping in `/iosApp/iosAppTests/Components/PetPhotoWithBadgesTests.swift`

### Implementation for User Story 6
- [ ] T021 [US6] Create `PetPhotoWithBadgesModel` struct in `/iosApp/iosApp/Views/Components/PetPhotoWithBadges.swift` (nested or separate)
- [ ] T022 [US6] Implement `PetPhotoWithBadges` component in `/iosApp/iosApp/Views/Components/PetPhotoWithBadges.swift`
- [ ] T023 [US6] Integrate `PetPhotoWithBadges` into `PetDetailsView` header
- [ ] T024 [US6] Add accessibility identifiers for status badge

---

## Phase 5: User Story 2 - Review Pet Identification Information (Priority: P2)

**Goal**: Users can view Microchip, Species, Breed, Sex, and Age.

**Independent Test**: Verify identification fields display correctly with proper formatting.

### Tests for User Story 2
- [ ] T025 [US2] Add identification field scenarios to `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`
- [ ] T026 [US2] Create unit tests for `LabelValueRow` in `/iosApp/iosAppTests/Components/LabelValueRowTests.swift`

### Implementation for User Story 2
- [ ] T027 [US2] Create `LabelValueRowModel` struct in `/iosApp/iosApp/Views/Components/LabelValueRow.swift`
- [ ] T028 [US2] Implement `LabelValueRow` component in `/iosApp/iosApp/Views/Components/LabelValueRow.swift`
- [ ] T029 [US2] Add Species and Breed rows to `PetDetailsView`
- [ ] T030 [US2] Add Sex and Age rows to `PetDetailsView` (implement sex symbol mapping)
- [ ] T031 [US2] Add Microchip number row to `PetDetailsView`
- [ ] T032 [US2] Add Date of Disappearance row to `PetDetailsView`
- [ ] T033 [US2] Add accessibility identifiers for all identification fields

---

## Phase 6: User Story 3 - Access Location and Contact Information (Priority: P2)

**Goal**: Users can view Location, Phone, Email, and use "Show on map".

**Independent Test**: Verify location display, tappable phone/email, and map button.

### Tests for User Story 3
- [ ] T034 [US3] Add location and contact scenarios to `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`

### Implementation for User Story 3
- [ ] T035 [US3] Add Location and Radius rows to `PetDetailsView`
- [ ] T036 [US3] Implement "Show on the map" button in `PetDetailsView` (console log action)
- [ ] T037 [US3] Add Phone row to `PetDetailsView` with URL opener logic (`tel://`)
- [ ] T038 [US3] Add Email row to `PetDetailsView` with URL opener logic (`mailto:`)
- [ ] T039 [US3] Add accessibility identifiers for location, map button, phone, and email

---

## Phase 7: User Story 4 - Review Additional Pet Details (Priority: P3)

**Goal**: Users can read the full multi-line description.

**Independent Test**: Verify long text displays correctly and screen scrolls.

### Tests for User Story 4
- [ ] T040 [US4] Add description scenarios to `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`

### Implementation for User Story 4
- [ ] T041 [US4] Add Additional Description section to `PetDetailsView` (ensure ScrollView works)
- [ ] T042 [US4] Add accessibility identifier for description text

---

## Phase 8: User Story 5 - View Reward Information (Priority: P3)

**Goal**: Users see a reward badge on the photo if a reward is offered.

**Independent Test**: Verify reward badge appears only when reward is present.

### Tests for User Story 5
- [ ] T043 [US5] Add reward scenarios to `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`

### Implementation for User Story 5
- [ ] T044 [US5] Update `PetPhotoWithBadgesModel` to accept reward text
- [ ] T045 [US5] Update `PetPhotoWithBadges` view to render reward badge
- [ ] T046 [US5] Pass reward data from `PetDetailsView` to `PetPhotoWithBadges`
- [ ] T047 [US5] Add accessibility identifier for reward badge

---

## Phase 9: User Story 7 - Remove Pet Report (Priority: P3)

**Goal**: Users see a "Remove Report" button (placeholder).

**Independent Test**: Verify button exists and logs to console.

### Tests for User Story 7
- [ ] T048 [US7] Add remove button scenarios to `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`

### Implementation for User Story 7
- [ ] T049 [US7] Add "Remove Report" button to `PetDetailsView` footer
- [ ] T050 [US7] Add accessibility identifier for remove button

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T051 [P] Add SwiftDoc documentation to all new public APIs
- [ ] T052 [P] Verify all accessibility identifiers against requirements
- [ ] T053 [P] Run full test suite to ensure 80% coverage
- [ ] T054 [P] Perform final code cleanup and formatting
- [ ] T055 Refactor iOS repository module structure to comply with constitution (move `AnimalRepository.swift` implementation from `Domain/Repositories` to `Data/Repositories` and update references)

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup & Foundational (Phase 1-2)**: Blocks ALL user stories.
- **US1 (View Details)**: Blocks US6, US2, US3, US4, US5, US7 (logically, as it provides the screen container).
- **US6, US2, US3, US4, US5, US7**: Can be implemented in parallel after US1, though US6 (Status) and US2/US3 (ID/Contact) are higher priority.

### Parallel Opportunities
- T010 (Unit Tests), T011 (Screen Object), T012 (E2E Spec) can run in parallel.
- T019, T025, T033, T039, T042, T047 (E2E Scenarios) can be written in parallel.
- Once US1 is done, different developers could tackle US2 (ID fields) and US3 (Contact fields) simultaneously.

## Implementation Strategy

1. **Foundation**: Setup models and repositories first.
2. **MVP (US1)**: Get the screen navigation and basic state loading working.
3. **Components (US6, US2)**: Build the reusable components (`PetPhotoWithBadges`, `LabelValueRow`).
4. **Content**: Populate the screen with data sections using the components (US2, US3, US4, US5).
5. **Actions**: Add remaining actions (US7).

