# Tasks: iOS Animal Photo Screen

**Input**: Design documents from `/specs/028-ios-animal-photo-screen/`
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `quickstart.md`

**Tests**:
- **iOS Unit Tests**: `/iosApp/iosAppTests/` (XCTest with async/await, 80% coverage). Run `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`.
- **Mobile E2E Tests**: `/e2e-tests/java/` (Appium + Cucumber). Run `mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios and @missingPetPhoto"`.
- Follow Given-When-Then naming, ensure identifiers like `animalPhoto.browse`, `animalPhoto.continue`, `animalPhoto.remove`, `animalPhoto.toast`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish localization, automation scaffolding, and platform minimums required by every story.

- [ ] T001 Add animal photo localization keys in `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` and `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings`, then regenerate SwiftGen outputs by running `swiftgen` inside `/iosApp`.
- [ ] T002 [P] Create the `@ios @missingPetPhoto` scenario shell in `/e2e-tests/java/src/test/resources/features/mobile/missing_pet_photo.feature` and wire simulator photo injection hooks in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/utils/Hooks.java`.
---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core models, cache services, and DI wiring that every user story depends on. All of these must land before feature work.

- [ ] T003 Create `PhotoAttachmentState.swift` under `/iosApp/iosApp/Features/ReportMissingPet/Models/` with `PhotoAttachmentMetadata`, `PhotoAttachmentStatus`, and helpers for supported `UTType` + formatted sizes.
- [ ] T004 [P] Extend `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` with `@Published var photoAttachment` and `photoStatus`, plus update `clear()` to delete cached blobs.
- [ ] T005 [P] Implement `/iosApp/iosApp/Features/ReportMissingPet/Services/PhotoAttachmentCache.swift` with async `save/load/clear` APIs persisting files under `Library/Caches/PetSpot/ReportMissingPet/`.
- [ ] T006 [P] Register the cache inside `/iosApp/iosApp/DI/ServiceContainer.swift`, ensuring `PhotoViewModel` receives it even though PhotosPicker is used directly in SwiftUI without an intermediate coordinator.
- [ ] T007 [P] Add reusable fakes (cache + toast scheduler) under `/iosApp/iosAppTests/Features/ReportMissingPet/Support/` to unblock XCTest coverage for all upcoming stories.

---

## Phase 3: User Story 1 - Attach photo via SwiftUI PhotosPicker (Priority: P1) 🎯 MVP

**Goal**: Let iOS reporters pick a supported image through SwiftUI `PhotosPicker` and immediately see the confirmation card from Figma node `297:8041`, persisting selection across navigation.

**Independent Test**: Jump to the Animal Photo step, select a single HEIC/JPG asset, verify the confirmation card renders with filename/size, and confirm Continue advances without re-uploading when re-entering the screen.

### Tests for User Story 1 (MANDATORY)

- [ ] T009 [P] [US1] Add `PhotoAttachmentStateTests.swift` in `/iosApp/iosAppTests/Features/ReportMissingPet/Models/` covering empty→loading→confirmed transitions and size formatting.
- [ ] T010 [P] [US1] Add `PhotoAttachmentCacheTests.swift` in `/iosApp/iosAppTests/Features/ReportMissingPet/Services/` validating save/load/clear logic and cache directory hygiene.
- [ ] T011 [P] [US1] Extend `/iosApp/iosAppTests/Features/ReportMissingPet/Views/PhotoViewModelTests.swift` with happy-path tests for browse success, cache persistence, and coordinator navigation.

### Implementation for User Story 1

- [ ] T012 [P] [US1] Build `AnimalPhotoEmptyStateView.swift` in `/iosApp/iosApp/Features/ReportMissingPet/Views/Components/` to match Figma node `297:7991`, including helper copy and `animalPhoto.browse` identifier.
- [ ] T013 [P] [US1] Implement `AnimalPhotoConfirmationCard.swift` in `/iosApp/iosApp/Features/ReportMissingPet/Views/Components/` with icon, filename, size, and `animalPhoto.remove` accessibility identifier.
- [ ] T014 [US1] Complete `PhotoViewModel.swift` in `/iosApp/iosApp/Features/ReportMissingPet/Views/` to map picker outputs into `PhotoAttachmentStatus`, persist metadata to FlowState, and expose `continueTapped` gating.
- [ ] T015 [US1] Replace the placeholder UI in `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoView.swift` with the PhotosPicker-driven layout, wiring `PhotosPicker` to the ViewModel intents and adding previews.
- [ ] T016 [US1] Update `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift` to instantiate the new ViewModel, handle `onNext`, and keep step 2/4 navigation consistent.
- [ ] T017 [P] [US1] Flesh out the happy-path scenario inside `/e2e-tests/java/src/test/resources/features/mobile/missing_pet_photo.feature` plus matching steps in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/ReportMissingPetSteps.java` to validate browse→confirm→continue.

---

## Phase 4: User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

**Goal**: Keep Continue responsive yet blocked until a stored photo exists by surfacing the “Photo is mandatory” toast and clearing attachments when Remove is tapped.

**Independent Test**: Attempt to Continue with no photo, observe the toast for 3 seconds, attach a file, advance, then Remove and confirm Continue replays the toast until a new photo is chosen.

### Tests for User Story 2 (MANDATORY)

- [ ] T018 [P] [US2] Expand `/iosApp/iosAppTests/Features/ReportMissingPet/Views/PhotoViewModelTests.swift` with Given-When-Then cases for Continue without attachment, Remove resets, and toast timing.

### Implementation for User Story 2

- [ ] T019 [US2] Add toast state (`showsMandatoryToast`, timer handling) inside `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoViewModel.swift` and render the toast in `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoView.swift` with identifier `animalPhoto.toast`.
- [ ] T020 [US2] Implement the Remove control logic plus helper copy fallback in `/iosApp/iosApp/Features/ReportMissingPet/Views/Components/AnimalPhotoConfirmationCard.swift` and ensure FlowState cleanup in `ReportMissingPetFlowState.swift`.
- [ ] T021 [P] [US2] Update the Appium scenario + steps (`missing_pet_photo.feature` and `ReportMissingPetSteps.java`) to cover toast assertions and Remove-driven resets.

---

## Phase 5: User Story 3 - Recover from picker or asset issues (Priority: P3)

**Goal**: Provide resilient UX for picker cancellation, iCloud downloads, and transfer errors so users retain progress and can retry without re-entering data.

**Independent Test**: Cancel the picker, confirm helper text + toast messaging, then simulate an iCloud download that succeeds and one that fails, verifying the confirmation card persistence/clearing logic and Continue gating.

### Tests for User Story 3 (MANDATORY)

- [ ] T022 [P] [US3] Extend `/iosApp/iosAppTests/Features/ReportMissingPet/Views/PhotoViewModelTests.swift` with scenarios for picker cancellation, iCloud progress completion, and transfer failure recovery using the new fakes.

### Implementation for User Story 3

- [ ] T023 [US3] Add progress tracking + cancellation handling in `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoViewModel.swift` and render the loading indicator plus helper copy in `PhotoView.swift`.
- [ ] T024 [US3] Ensure cached metadata reloads on init/background resume by enhancing `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` and lifecycle hooks inside `PhotoViewModel`.
- [ ] T025 [P] [US3] Expand `missing_pet_photo.feature`, `ReportMissingPetSteps.java`, and `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/utils/Hooks.java` with steps that simulate picker cancellation and iCloud failures.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Documentation, accessibility, and verification tasks that span all stories.

- [ ] T026 [P] Add SwiftDoc comments to `PhotoAttachmentCache.swift` and `PhotoViewModel.swift` plus update `/specs/028-ios-animal-photo-screen/quickstart.md` with any deviations.
- [ ] T027 [P] Verify accessibility identifiers inside `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoView.swift` and components follow the `animalPhoto.*` naming convention for UI automation.
- [ ] T028 Run the full regression suite (`xcodebuild test ...` + `mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios and @missingPetPhoto"`) and capture coverage artefacts in `/iosApp/iosAppTests/Reports/` for sign-off.

---

## Dependencies & Execution Order

- **Phase Dependencies**:
  - Phase 1 → Phase 2 → User Stories → Phase 6; later phases cannot start until prerequisites complete.
- **User Story Ordering**:
  - US1 (P1) unlocks core attachment experience and must finish before US2/US3.
  - US2 (P2) builds on US1’s ViewModel/UI to enforce mandatory gating.
  - US3 (P3) relies on US1+US2 infrastructure to handle cancellation/error recovery.
- **Task-Level Notes**:
  - Tests (T009-T011, T018, T022) should fail before implementation tasks in their stories proceed.
  - DI wiring (T006) blocks all ViewModel/UI tasks; cache fakes (T007) block every XCTest addition.

---

## Parallel Execution Examples

- **US1**: While T009-T011 run in parallel using the shared fakes, another dev can implement T012-T013, and a third can handle T014-T017; merge when tests for happy path pass.
- **US2**: Run T018 in parallel with T019-T020 (test-first loop), while automation engineers update the Appium steps for T021.
- **US3**: Execute T022 concurrently with T023-T024, and keep T025 in a separate thread that focuses on Appium + Hooks updates.

---

## Implementation Strategy

1. **MVP First**: Complete Phases 1-2 and US1 (T001–T017) to unlock the PhotosPicker confirmation flow; demo as soon as US1 tests pass.
2. **Incremental Delivery**: Land US2 (T018–T021) to enforce the mandatory rule, then US3 (T022–T025) for resilience, verifying each story independently before moving on.
3. **Final Polish**: Use Phase 6 (T026–T028) to document, run coverage, and perform accessibility/performance sweeps prior to release.

---

