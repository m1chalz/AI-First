---
description: "Task list for iOS Map Preview Component (KAN-30)"
---

# Tasks: iOS Map Preview Component

**Feature**: KAN-30 - Embed Map in Landing Page  
**Platform**: iOS only (SwiftUI)  
**Input**: Design documents from `/specs/KAN-30-embed-map-in-landing-page/`  
**Available Docs**: research.md, data-model.md, quickstart.md

**Architecture**: Simple SwiftUI Map with disabled interactions, no async complexity, all logic in LandingPageViewModel

**Test Coverage**: 80% minimum for iOS unit tests (XCTest)  
**Test Location**: `/iosApp/iosAppTests/Features/LandingPage/`  
**Run Tests**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`

## Format: `- [ ] [ID] [P?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Localization & Domain Extensions)

**Purpose**: Basic infrastructure for map preview component

- [ ] T001 [P] Add English localization strings in `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (mapPreview.permission.message, mapPreview.permission.settingsButton)
- [ ] T002 [P] Add Polish localization strings in `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (mapPreview.permission.message, mapPreview.permission.settingsButton)
- [ ] T003 Run `cd iosApp && swiftgen` to regenerate localization constants
- [ ] T004 Add `mapRegion()` helper extension to `/iosApp/iosApp/Domain/Models/Coordinate.swift` (creates MKCoordinateRegion with 10km radius)

**Checkpoint**: Localization and domain extensions ready

---

## Phase 2: Model & View Components

**Purpose**: MapPreviewView with its Model enum

**iOS Unit Tests** (TDD - Write FIRST, ensure they FAIL):
- [ ] T005 [P] Unit test for MapPreviewView.Model Equatable in `/iosApp/iosAppTests/Features/LandingPage/MapPreviewView_ModelTests.swift` (test .loading, .map, .permissionRequired equality)
- [ ] T006 [P] Unit test for Coordinate.mapRegion() helper in `/iosApp/iosAppTests/Domain/Models/CoordinateTests.swift` (verify 20km span calculation)

**Implementation**:
- [ ] T007 Create MapPreviewView.Model enum in `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_Model.swift` (3 cases: loading, map with region+onTap, permissionRequired with message+onGoToSettings)
- [ ] T008 Implement Equatable for MapPreviewView.Model (compare by coordinates and messages, ignore closures)
- [ ] T009 Create MapPreviewView in `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView.swift` (SwiftUI Map with interactionModes: [], overlay with tap gesture)
- [ ] T010 Add accessibility identifiers to MapPreviewView elements (.accessibilityIdentifier("landingPage.mapPreview"), "landingPage.mapPreview.settings")
- [ ] T011 Add SwiftDoc to MapPreviewView.Model (document each case purpose)
- [ ] T012 Add SwiftDoc to MapPreviewView (document component purpose and interaction pattern)

**Checkpoint**: MapPreviewView component ready, tests pass

---

## Phase 3: ViewModel Integration

**Purpose**: Integrate map preview logic into LandingPageViewModel

**iOS Unit Tests** (TDD - Write FIRST, ensure they FAIL):
- [ ] T013 [P] Unit test: loadData with authorized location should set .map model in `/iosApp/iosAppTests/Features/LandingPage/LandingPageViewModelTests.swift` (assert mapPreviewModel has correct region)
- [ ] T014 [P] Unit test: loadData with denied location should set .permissionRequired model in `/iosApp/iosAppTests/Features/LandingPage/LandingPageViewModelTests.swift` (assert permissionRequired state)
- [ ] T015 [P] Unit test: handleMapTap should log to console in `/iosApp/iosAppTests/Features/LandingPage/LandingPageViewModelTests.swift` (manual verification via console output)
- [ ] T015a [P] Unit test: map component should never call requestWhenInUseAuthorization in `/iosApp/iosAppTests/Features/LandingPage/LandingPageViewModelTests.swift` (verify LocationService spy never received permission request call)

**Implementation**:
- [ ] T016 Add `@Published var mapPreviewModel: MapPreviewView.Model = .loading` property to `/iosApp/iosApp/Features/LandingPage/LandingPageViewModel.swift`
- [ ] T017 Implement `updateMapPreviewModel(location:)` private method in LandingPageViewModel (sets .map if location available, .permissionRequired otherwise)
- [ ] T018 Implement `handleMapTap()` private method in LandingPageViewModel (prints log message for now)
- [ ] T019 Modify `loadData()` in LandingPageViewModel to call `updateMapPreviewModel` after location permissions resolved
- [ ] T020 Add SwiftDoc to mapPreviewModel property (document purpose and state flow)
- [ ] T021 Add SwiftDoc to updateMapPreviewModel and handleMapTap methods (document behavior)

**Checkpoint**: ViewModel logic complete, unit tests pass, 80% coverage verified

---

## Phase 4: UI Integration

**Purpose**: Add MapPreviewView to LandingPageView layout

- [ ] T022 Add MapPreviewView between HeroPanelView and ListHeaderRowView in `/iosApp/iosApp/Features/LandingPage/LandingPageView.swift` (16:9 aspect ratio, horizontal padding 16pt, vertical padding 16pt)
- [ ] T023 Pass `viewModel.mapPreviewModel` to MapPreviewView

**Checkpoint**: Map preview visible in LandingPage, all states render correctly

---

## Phase 5: Validation & Verification

**Purpose**: Final checks and manual testing

- [ ] T024 Run unit tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T025 Verify 80% code coverage for LandingPageViewModel and MapPreviewView components
- [ ] T026 Manual test: Map preview appears between Hero panel and Recent Reports
- [ ] T027 Manual test: Map shows 16:9 aspect ratio, full width minus padding
- [ ] T028 Manual test: Map centers on user location with ~10km visible area
- [ ] T029 Manual test: Map has no zoom/pan interactions (static preview)
- [ ] T030 Manual test: Tapping map logs to console
- [ ] T031 Manual test: Permission required state shows "Go to Settings" button
- [ ] T032 Manual test: Loading state shows during initial load
- [ ] T033 Manual test: Accessibility identifiers present for E2E testing
- [ ] T033a Manual test: App background/foreground transition preserves map state (map region remains unchanged)
- [ ] T034 [P] Run SwiftLint and fix violations (if configured)
- [ ] T035 Update quickstart.md if any implementation details changed

**Checkpoint**: Feature complete and verified ✅

---

## Dependencies & Execution Order

### Phase Dependencies

1. **Setup (Phase 1)**: No dependencies - can start immediately
2. **Model & View (Phase 2)**: Depends on Setup (needs localization strings)
3. **ViewModel Integration (Phase 3)**: Depends on Phase 2 (needs MapPreviewView.Model)
4. **UI Integration (Phase 4)**: Depends on Phase 3 (needs ViewModel.mapPreviewModel)
5. **Validation (Phase 5)**: Depends on all previous phases

### Within Each Phase

- Tests MUST be written and FAIL before implementation (TDD)
- Model before View
- ViewModel logic before UI integration
- Unit tests before manual validation

### Parallel Opportunities

- T001, T002 (localization files) can run in parallel
- T005, T006 (unit tests for Model and Coordinate) can run in parallel
- T013, T014, T015 (ViewModel unit tests) can run in parallel
- T034, T035 (linting and docs) can run in parallel

---

## Implementation Strategy

### Single-Path Execution

This is a small iOS-only feature with clear dependencies:

1. **Phase 1**: Setup infrastructure (localization + domain extension)
2. **Phase 2**: Build MapPreviewView component (model + view)
3. **Phase 3**: Integrate into LandingPageViewModel (business logic)
4. **Phase 4**: Wire up in LandingPageView (UI integration)
5. **Phase 5**: Validate and verify (tests + manual checks)

### TDD Workflow

- Write unit tests FIRST for each phase
- Ensure tests FAIL (red)
- Implement minimal code to pass (green)
- Run tests, verify coverage
- Manual validation at end

### Estimated Time

- Phase 1: 30 minutes
- Phase 2: 1-2 hours (model, view, tests)
- Phase 3: 1-2 hours (ViewModel integration, tests)
- Phase 4: 30 minutes (UI wiring)
- Phase 5: 1 hour (validation)

**Total**: ~4-6 hours for complete implementation

---

## Notes

- No backend changes required (iOS-only feature)
- No E2E tests in this task list (can be added separately if needed)
- Uses existing LocationPermissionHandler infrastructure
- No new dependencies (SwiftUI Map + MapKit only)
- Future enhancement: handleMapTap will navigate to fullscreen map view
- All accessibility identifiers follow convention: `landingPage.mapPreview.*`
- SwiftDoc required only for public APIs and complex logic (skip obvious getters/setters)

