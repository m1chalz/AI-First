---
description: "Actionable, dependency-ordered task list for implementing iOS fullscreen interactive map with legend"
---

# Tasks: iOS Display Fullscreen Interactive Map with Legend

**Input**: Design documents from `/specs/KAN-32-ios-display-fullscreen-map/`  
**Prerequisites**: `specs/KAN-32-ios-display-fullscreen-map/plan.md` (required), `specs/KAN-32-ios-display-fullscreen-map/spec.md` (required), `specs/KAN-32-ios-display-fullscreen-map/research.md`, `specs/KAN-32-ios-display-fullscreen-map/data-model.md`, `specs/KAN-32-ios-display-fullscreen-map/quickstart.md`

**Tests (MANDATORY - iOS)**:
- Location: `/iosApp/iosAppTests/` (XCTest), 80% coverage
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive names

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] T### [P?] [US?] Description with file path`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US#]**: Which user story this task belongs to (US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Validate prerequisites and ensure you can build/run iOS target for fast iteration

- [ ] T001 Verify prerequisite feature is present (navigation to fullscreen map) by checking `specs/KAN-32-ios-navigation-to-fullscreen-map/spec.md`
- [ ] T002 Verify existing placeholder screen exists and builds: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [ ] T003 Verify existing `Coordinate.mapRegion(radiusMeters:)` helper exists for city-level zoom: `iosApp/iosApp/Domain/Models/Coordinate.swift`
- [ ] T004 [P] Verify required localization keys already exist: `iosApp/iosApp/Resources/en.lproj/Localizable.strings` and `iosApp/iosApp/Resources/pl.lproj/Localizable.strings`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish shared foundations that both user stories rely on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Confirm planned source layout and target files match repo structure (paths in `specs/KAN-32-ios-display-fullscreen-map/plan.md`)
- [ ] T006 Define/confirm accessibility identifier naming for this screen (`fullscreenMap.*`) and document it in code comments in `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [ ] T007 [P] Audit current `MapSectionHeaderView` usage and API surface before changing it: `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView.swift` and `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model.swift`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Display Interactive Map (Priority: P1) 🎯 MVP

**Goal**: Replace placeholder with an interactive SwiftUI Map centered on user location and supporting zoom/pan/double-tap.

**Independent Test**: Open fullscreen map and verify an interactive map renders and supports zoom/pan/double-tap (see scenarios in `specs/KAN-32-ios-display-fullscreen-map/spec.md`).

### Tests for User Story 1 (MANDATORY) ✅

- [ ] T008 [P] [US1] Add ViewModel unit tests for initial region centering and city-level zoom in `iosApp/iosAppTests/Features/LandingPage/FullscreenMap/FullscreenMapViewModelTests.swift`

### Implementation for User Story 1

- [ ] T009 [P] [US1] Update fullscreen map ViewModel to compute `mapRegion` from injected location in `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [ ] T010 [P] [US1] Update landing page callback to pass current location to coordinator in `iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`
- [ ] T011 [P] [US1] Update coordinator to accept `Coordinate` and create ViewModel with location in `iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`
- [ ] T012 [US1] Replace fullscreen placeholder with interactive SwiftUI `Map(initialPosition: .region(viewModel.mapRegion))` and add identifiers (`fullscreenMap.container`, `fullscreenMap.map`) in `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [ ] T013 [US1] Manual verification of map interactions against acceptance scenarios in `specs/KAN-32-ios-display-fullscreen-map/spec.md` (pinch zoom, pan, double-tap)

**Checkpoint**: US1 delivers an interactive fullscreen map (independently testable)

---

## Phase 4: User Story 2 - Display Map Legend (Priority: P2)

**Goal**: Display a static legend overlay above the map explaining future pin meanings (missing/found placeholders).

**Independent Test**: Open fullscreen map and verify legend is visible and stable during map interactions (see scenarios in `specs/KAN-32-ios-display-fullscreen-map/spec.md`).

### Tests for User Story 2 (MANDATORY) ✅

- [ ] T014 [P] [US2] Extend ViewModel unit tests to validate legend model configuration (missing/found items) in `iosApp/iosAppTests/Features/LandingPage/FullscreenMap/FullscreenMapViewModelTests.swift`

### Implementation for User Story 2

- [ ] T015 [US2] Make `MapSectionHeaderView.Model.title` and `titleAccessibilityId` optional (preserve existing behavior for landing page) in `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model.swift`
- [ ] T016 [US2] Update `MapSectionHeaderView` to conditionally render title only when present (and keep legend layout unchanged) in `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView.swift`
- [ ] T017 [P] [US2] Add fullscreen-specific factory `.fullscreenMap()` (legend-only) in `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model+FullscreenMap.swift`
- [ ] T018 [US2] Extend fullscreen map ViewModel to expose `legendModel` (using `.fullscreenMap()`) in `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [ ] T019 [US2] Add legend above the map using `MapSectionHeaderView(model: viewModel.legendModel)` and add identifier(s) under `fullscreenMap.legend*` in `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [ ] T020 [US2] Manual verification: legend visibility + non-obstruction during map interaction per scenarios in `specs/KAN-32-ios-display-fullscreen-map/spec.md`

**Checkpoint**: US2 delivers a visible legend on top of the interactive map (independently testable)

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Quality, consistency, and coverage gates for the completed feature

- [ ] T021 [P] Ensure all interactive UI elements on fullscreen map have stable accessibility identifiers in `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [ ] T022 Run iOS unit tests with coverage and confirm 80%+ for new/changed ViewModel logic: `iosApp/iosAppTests/Features/LandingPage/FullscreenMap/FullscreenMapViewModelTests.swift`
- [ ] T023 [P] If implementation differs from the documented quickstart, update steps/checklist in `specs/KAN-32-ios-display-fullscreen-map/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: Depend on Foundational phase completion
- **Polish (Final Phase)**: Depends on desired user stories being complete

### User Story Dependencies

- **External dependency**: This feature depends on existing navigation infrastructure from `specs/KAN-32-ios-navigation-to-fullscreen-map/spec.md`
- **US1 (P1)**: No dependency on US2
- **US2 (P2)**: Can be implemented after Foundational, but touches shared files with US1 (`FullscreenMapView.swift`, `FullscreenMapViewModel.swift`) so coordinate merges carefully if parallelized

### Within Each User Story

- Tests MUST be added/updated before finalizing implementation (keep Given-When-Then)
- ViewModel updates before view wiring
- Manual verification against acceptance scenarios before declaring story complete

---

## Parallel Opportunities

- [ ] T024 [P] US1 navigation wiring can be done in parallel with US2 component refactor (US1: `iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift` + `iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`; US2: `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView.swift` + `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model.swift` + `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model+FullscreenMap.swift`)

---

## Parallel Example: User Story 1

```bash
# Parallelizable tasks that touch different files:
Task: "T010 Update LandingPageViewModel callback in iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift"
Task: "T011 Update HomeCoordinator wiring in iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift"
Task: "T009 Update FullscreenMapViewModel in iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Verify US1 independently using scenarios in `specs/KAN-32-ios-display-fullscreen-map/spec.md`

### Incremental Delivery

1. Setup + Foundational → foundation ready
2. Add US1 → validate → demo
3. Add US2 → validate → demo


