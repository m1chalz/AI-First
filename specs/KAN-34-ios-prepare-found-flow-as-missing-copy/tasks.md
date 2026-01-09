---
description: "Actionable, dependency-ordered task list for implementing KAN-34-ios-prepare-found-flow-as-missing-copy"
---

# Tasks: iOS Prepare Found Pet Flow as Missing Pet Copy (KAN-34)

**Input**: Design documents from `/specs/KAN-34-ios-prepare-found-flow-as-missing-copy/`

**Scope**: iOS only (`/iosApp`). No backend, Android, Web changes.

**Tests** (MANDATORY for this project):

- iOS Unit Tests: `/iosApp/iosAppTests/` (XCTest), 80% coverage
  - Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES` (from repo root)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] T### [P?] [US?] Description with file path`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US1]/[US2]**: Which user story this task belongs to
- Every task below includes exact file/directory paths

---

## Phase 1: Setup (iOS-only scaffolding)

**Purpose**: Ensure the feature has the intended target structure and a clean baseline before changes.

- [ ] T001 Capture baseline by running iOS unit tests before changes in `/iosApp/iosAppTests/` (store output locally if needed)
- [ ] T002 Confirm source-of-truth Missing flow directory exists at `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/`
- [ ] T003 Confirm feature target directory does not exist yet (or is empty) at `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Identify exact wiring points and prepare safe, mechanical copy/rename strategy.

**⚠️ CRITICAL**: No user story implementation should start until this phase is complete.

- [ ] T004 Identify the prepared but commented “Report Found Animal” entry button block in `iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift`
- [ ] T005 Identify the existing `showReportFound()` stub and expected DI dependencies in `iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift`
- [ ] T006 Define a deterministic rename checklist for the copy step (type names + accessibilityIdentifier prefixes) and apply it only within `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/`

**Checkpoint**: Baseline known, wiring points confirmed, rename rules set

---

## Phase 3: User Story 1 - Enter “Report Found Animal” flow from announcement list (Priority: P1) 🎯 MVP

**Goal**: User can start “Report Found Animal” from announcements list; flow is a temporary copy of Missing flow, with Found-prefixed types and identifiers.

**Independent Test**: On announcements list, tap “Report Found Animal” (`animalList.reportFoundButton`) and confirm a full-screen, multi-step flow is presented; complete flow triggers announcements refresh.

### Implementation (US1)

- [ ] T007 [US1] Create directory `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/` (mirror structure of `ReportMissingPet/`)
- [ ] T008 [P] [US1] Copy and rename flow source files into `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/`:
  - `Coordinators/FoundPetReportCoordinator.swift`
  - `Models/FoundPetReportFlowState.swift`
  - `Views/AnimalDescription/FoundPetAnimalDescriptionView.swift`
  - `Views/AnimalDescription/FoundPetAnimalDescriptionViewModel.swift`
  - `Views/ChipNumber/FoundPetChipNumberView.swift`
  - `Views/ChipNumber/FoundPetChipNumberViewModel.swift`
  - `Views/ContactDetails/FoundPetContactDetailsView.swift`
  - `Views/ContactDetails/FoundPetContactDetailsViewModel.swift`
  - `Views/Photo/FoundPetPhotoView.swift`
  - `Views/Photo/FoundPetPhotoViewModel.swift`
  - `Views/Summary/FoundPetSummaryView.swift`
  - `Views/Summary/FoundPetSummaryView+Constants.swift`
  - `Views/Summary/FoundPetSummaryViewModel.swift`
- [ ] T009 [US1] Update all copied iOS source code identifiers inside `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/`:
  - `MissingPet*` → `FoundPet*`
  - `missingPet` → `foundPet`
  - `reportMissingPet.` → `reportFoundPet.`
  - `missingPet.` → `foundPet.`
- [ ] T010 [P] [US1] Copy and rename unit tests into `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/`:
  - `FoundPetAnimalDescriptionViewModelTests.swift`
  - `FoundPetChipNumberViewModelTests.swift`
  - `FoundPetContactDetailsViewModelErrorHandlingTests.swift`
  - `FoundPetContactDetailsViewModelRewardTests.swift`
  - `FoundPetContactDetailsViewModelTests.swift`
  - `FoundPetContactDetailsViewModelValidationTests.swift`
  - `FoundPetPhotoViewModelTests.swift`
  - `FoundPetSummaryViewModelTests.swift`
  - `Models/FoundPetReportFlowStateTests.swift`
- [ ] T011 [US1] Update all copied test code identifiers inside `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/` to use `FoundPet*` and `reportFoundPet.`/`foundPet.` equivalents
- [ ] T012 [P] [US1] Wire the entry point UI by uncommenting/enabling the secondary “Report Found Animal” floating button in `iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift` (ensure `accessibilityIdentifier` is `animalList.reportFoundButton`)
- [ ] T013 [P] [US1] Implement `showReportFound()` to start the flow using `FoundPetReportCoordinator` in `iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift` (present `.fullScreen` with its own `UINavigationController`, mirror Missing flow)
- [ ] T014 [US1] Ensure completion callback refreshes announcements list by calling `AnnouncementListViewModel.requestToRefreshData()` from `iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift`
- [ ] T015 [US1] Add new Found flow source files to the Xcode project at `iosApp/iosApp.xcodeproj/project.pbxproj`
- [ ] T016 [US1] Add new Found flow test files to the Xcode project at `iosApp/iosApp.xcodeproj/project.pbxproj`

### Tests / Verification (US1)

- [ ] T017 [US1] Run iOS unit tests after the copy+wiring in `/iosApp/iosAppTests/` and verify Found-flow test suite passes
- [ ] T018 [US1] Manual smoke: start Found flow from announcements list and verify `.fullScreen` presentation + first screen renders (entry point in `iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift`)
- [ ] T019 [US1] Manual smoke: complete Found flow and verify announcements list refresh triggers via `requestToRefreshData()` (wiring in `iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift`)

**Checkpoint**: US1 works independently (entry point + flow presentation + refresh callback)

---

## Phase 4: User Story 2 - Keep Missing Pet flow unchanged (Priority: P1)

**Goal**: Existing “Report Missing Animal” continues to behave exactly as before, with no regressions.

**Independent Test**: Start Missing flow from announcements list and verify it still presents and navigates; all pre-existing Missing tests still pass.

### Verification (US2)

- [ ] T020 [US2] Manual smoke: start Missing flow from announcements list and verify it presents and shows first screen (entry point in `iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift`)
- [ ] T021 [US2] Manual smoke: navigate back from the first Missing screen and ensure returning behavior is unchanged (navigation code in `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/Coordinators/MissingPetReportCoordinator.swift`)
- [ ] T022 [US2] Re-run iOS unit tests and verify all existing Missing Pet tests still pass in `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportMissingPet/`

**Checkpoint**: US2 verified (no regressions)

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Consistency, maintainability, and guardrails for the “temporary copy” approach.

- [ ] T023 [P] Ensure no accessibilityIdentifier collisions remain between flows by auditing identifiers in `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/` and `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/`
- [ ] T024 [P] Ensure Found flow uses the existing localization key `L10n.AnnouncementList.Button.reportFound` without introducing new keys (audit usage in `iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift`)
- [ ] T025 Run `xcodebuild build` for the app scheme to confirm compilation (project at `iosApp/iosApp.xcodeproj`)
- [ ] T026 Run `/specs/KAN-34-ios-prepare-found-flow-as-missing-copy/quickstart.md` verification steps and confirm they match final state

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup completion — **blocks all user story work**
- **US1 (Phase 3)**: Depends on Foundational
- **US2 (Phase 4)**: Depends on US1 implementation being complete (verification is meaningful only after changes land)
- **Polish (Phase 5)**: Depends on US1 + US2 completion

### User Story Dependencies

- **US1 (P1)**: Starts after Phase 2, no dependency on other user stories
- **US2 (P1)**: Verification depends on US1 changes being integrated; goal is ensuring no regression of Missing flow

### Parallel Opportunities

- In **US1**, tasks **T010–T011** (copy/rename tests) can run in parallel with **T012–T014** (entry wiring), once **T007–T009** are underway
- Xcode project additions (**T015–T016**) can run in parallel with wiring tasks (**T012–T014**) if performed carefully in `iosApp/iosApp.xcodeproj/project.pbxproj`

---

## Parallel Example: User Story 1

```bash
# In parallel after T007–T009 starts:
Task: "Copy/rename tests into iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/** (T010–T011)"
Task: "Uncomment Report Found button in iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift (T012)"
Task: "Implement showReportFound() in iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift (T013–T014)"
```

---

## Implementation Strategy

### MVP First (US1 Only)

1. Complete Phase 1–2 (baseline + wiring points)
2. Implement US1 (Phase 3)
3. **Stop and validate**: run unit tests and do smoke checks (T017–T019)

### Incremental Delivery

1. Land US1 + tests
2. Validate US2 (regression checks)
3. Polish (collision audit + quickstart validation)


