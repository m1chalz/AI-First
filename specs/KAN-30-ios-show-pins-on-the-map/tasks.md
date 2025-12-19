# Tasks: iOS Map Preview - Display Missing Pet Pins

**Input**: Design documents from `/specs/KAN-30-ios-show-pins-on-the-map/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅, quickstart.md ✅

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/LandingPage/Views/`
- Coverage: 80% line + branch coverage
- Framework: XCTest with Swift Concurrency (async/await)
- Scope: LandingPageViewModel pin model creation, MapPreviewView.Model equality
- Convention: MUST follow Given-When-Then structure with descriptive names
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`

**NOT APPLICABLE for this feature**:
- Android tests (iOS-only feature per FR-013)
- Web tests (iOS-only feature per FR-013)
- Backend tests (no backend changes - consumes existing API)
- E2E tests (pins are non-interactive per FR-007, FR-008 - visual-only)

**Organization**: Tasks grouped by user story for independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2)
- Include exact file paths in descriptions

## Path Conventions

- **iOS app**: `/iosApp/iosApp/` (source), `/iosApp/iosAppTests/` (tests)
- All paths are relative to repository root

---

## Phase 1: Setup

**Purpose**: Verify project state and branch readiness

- [ ] T001 Verify on feature branch `KAN-30-ios-show-pins-on-the-map`
- [ ] T002 [P] Build iOS project to confirm compilation: `xcodebuild build -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`

**Checkpoint**: Project builds successfully, ready for implementation

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before user stories

**⚠️ Note**: No foundational tasks needed - all dependencies exist:
- MapPreviewView_PinModel.swift ✅ already implemented
- MapPreviewView exists with static map support ✅
- LandingPageViewModel exists with map preview model ✅
- AnnouncementListQuery exists ✅
- Test infrastructure exists ✅

**Checkpoint**: Foundation ready - proceed to User Story 1

---

## Phase 3: User Story 1 - View Missing Pet Pins on Map Preview (Priority: P1) 🎯 MVP

**Goal**: Display visual pins on the iOS landing page map preview showing locations of missing pet announcements within ~10 km radius

**Independent Test**: Open landing page with location permission granted and missing pet announcements in database within 10 km. Verify pins appear at correct approximate locations.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation (TDD)**

**iOS Unit Tests**:

- [ ] T003 [P] [US1] RED: Write failing test for `AnnouncementListQuery.range` property in `/iosApp/iosAppTests/Domain/Models/AnnouncementListQueryTests.swift` (NEW file)
- [ ] T004 [P] [US1] RED: Write failing test for `AnnouncementListQuery.landingPageQuery` returning `range=10` in same file
- [ ] T005 [P] [US1] RED: Write failing test for `MapPreviewView.Model.map` case with `pins` parameter equality in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/MapPreviewView_ModelTests.swift`
- [ ] T006 [P] [US1] RED: Write failing test for `LandingPageViewModel` creating pins from `listViewModel.cardViewModels` in `/iosApp/iosAppTests/Features/LandingPage/Views/LandingPageViewModelTests.swift`
- [ ] T007 [P] [US1] RED: Write failing test for `LandingPageViewModel.mapPreviewModel` containing pins when announcements loaded in same file
- [ ] T008 [P] [US1] RED: Write failing test for empty pins array when no announcements exist in same file

### Implementation for User Story 1

> **Note**: iOS-only feature - no Android, Web, or Backend tasks

**iOS Domain Layer**:

- [ ] T009 [US1] GREEN: Add `range: Int` property to `AnnouncementListQuery` in `/iosApp/iosApp/Domain/Models/AnnouncementListQuery.swift`
- [ ] T010 [US1] GREEN: Update `landingPageQuery(location:)` factory to return `range: 10` in same file
- [ ] T011 [US1] GREEN: Update `defaultQuery(location:)` factory to return `range: 100` (default) in same file

**iOS Presentation Layer - Model**:

- [ ] T012 [US1] GREEN: Add `pins: [PinModel] = []` parameter to `.map` case in `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_Model.swift`
- [ ] T013 [US1] GREEN: Update `Equatable` implementation to compare pins array in same file

**iOS Presentation Layer - ViewModel**:

- [ ] T014 [US1] GREEN: Update `updateMapPreviewModel(location:)` to accept `announcements: [AnnouncementCardViewModel]` parameter in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`
- [ ] T015 [US1] GREEN: Implement pin model creation from `listViewModel.cardViewModels` in `updateMapPreviewModel()` in same file
- [ ] T016 [US1] GREEN: Pass pins array to `.map(region:pins:onTap:)` call in same file
- [ ] T017 [US1] Update call site of `updateMapPreviewModel()` in `loadData()` to pass card view models in same file

**iOS View Layer**:

- [ ] T018 [US1] Update `mapView(region:onTap:)` signature to include `pins: [PinModel]` in `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView.swift`
- [ ] T019 [US1] Replace deprecated `Map(coordinateRegion:interactionModes:)` with `Map(initialPosition:interactionModes:) { content }` in same file
- [ ] T020 [US1] Add `ForEach(pins) { pin in Marker("", coordinate: pin.clLocationCoordinate).tint(.red) }` inside Map content builder in same file
- [ ] T021 [US1] Update `contentView` switch case to pass pins to `mapView()` in same file
- [ ] T022 [P] [US1] Update `#Preview("Map")` to include sample pins in same file

**iOS Tests - REFACTOR**:

- [ ] T023 [US1] REFACTOR: Verify all tests pass and achieve 80% coverage for modified files
- [ ] T024 [P] [US1] Add SwiftDoc documentation to `updateMapPreviewModel()` explaining pin creation logic in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`

**Checkpoint**: User Story 1 complete - pins display on map preview at announcement locations

---

## Phase 4: User Story 2 - Understand Pin Representation is Static (Priority: P2)

**Goal**: Confirm pins and map preview are completely non-interactive (no-op on tap)

**Independent Test**: Tap on pins and map preview, verify no action occurs (no navigation, no pop-ups)

**Note**: This user story requires NO code changes - existing implementation already satisfies requirements:
- `interactionModes: []` disables all map gestures (FR-008) ✅
- `Marker` does not have tap handlers by default (FR-007) ✅
- No instructional text in map preview (FR-009) ✅

### Verification for User Story 2

- [ ] T025 [US2] Manual test: Tap on pin - verify nothing happens
- [ ] T026 [US2] Manual test: Tap on map preview (not on pin) - verify nothing happens
- [ ] T027 [US2] Manual test: Verify no instructional text suggesting interactivity

**Checkpoint**: User Story 2 verified - pins are correctly static and non-interactive

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and cleanup

- [ ] T028 Run full iOS test suite: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T029 Verify 80% test coverage for modified files in Xcode coverage report
- [ ] T030 Run quickstart.md manual testing checklist
- [ ] T031 [P] Review all modified files for SwiftDoc documentation completeness

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: N/A - no tasks needed
- **User Story 1 (Phase 3)**: Can start after Setup verification
- **User Story 2 (Phase 4)**: Depends on User Story 1 completion (verification of implementation)
- **Polish (Phase 5)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories - core implementation
- **User Story 2 (P2)**: Depends on US1 completion - verification only, no code changes

### Within User Story 1

- Tests (T003-T008) MUST be written and FAIL before implementation
- Domain layer (T009-T011) before Presentation layer (T012-T017)
- Model changes (T012-T013) before ViewModel changes (T014-T017)
- ViewModel changes (T014-T017) before View changes (T018-T022)
- All GREEN tasks (T009-T022) before REFACTOR tasks (T023-T024)

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All test tasks marked [P] can run in parallel (T003-T008)
- Documentation tasks marked [P] can run in parallel with other tasks

---

## Parallel Example: User Story 1 Tests

```bash
# Launch all RED tests for User Story 1 together:
T003 [P] [US1] AnnouncementListQuery.range test
T004 [P] [US1] landingPageQuery range=10 test
T005 [P] [US1] MapPreviewView.Model pins equality test
T006 [P] [US1] LandingPageViewModel pin creation test
T007 [P] [US1] mapPreviewModel with pins test
T008 [P] [US1] empty pins array test
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T002)
2. Skip Phase 2: No foundational tasks needed
3. Complete Phase 3: User Story 1 (T003-T024)
4. **STOP and VALIDATE**: Test pin display independently
5. Deploy/demo if ready (pins visible on map preview)

### Incremental Delivery

1. Complete Setup → Branch ready
2. Add User Story 1 → Test independently → Pins display on map (MVP!)
3. Verify User Story 2 → Confirm non-interactive behavior
4. Polish → Full test coverage, documentation

### TDD Workflow (CRITICAL)

For each implementation task:
1. **RED**: Write failing test first (T003-T008)
2. **GREEN**: Implement minimal code to pass test (T009-T022)
3. **REFACTOR**: Improve code quality, add documentation (T023-T024)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- User Story 2 requires verification only (no code changes)
- PinModel already exists - focus on integration
- Backend returns only `active`/`found` announcements - no client-side status filtering needed
- Use modern SwiftUI Map API (`Map(initialPosition:) { content }`) per research.md decision

