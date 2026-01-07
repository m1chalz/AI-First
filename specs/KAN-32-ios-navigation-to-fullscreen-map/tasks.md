# Tasks: iOS Navigation to Fullscreen Map View

**Input**: Design documents from `/specs/KAN-32-ios-navigation-to-fullscreen-map/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, quickstart.md ✓, contracts/ ✓

**Tests**: Test requirements for this feature:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/FullscreenMap/`
- Framework: XCTest with Swift Concurrency
- Coverage target: 80% for ViewModel
- Scope: FullscreenMapViewModel, LandingPageViewModel navigation callback
- Convention: Given-When-Then structure with descriptive names

**NOT INCLUDED - E2E Tests**:
- Per plan.md: "Will be addressed in separate ticket (navigation-only E2E testing)"
- Justification: E2E tests for navigation can be added post-implementation; unit tests cover ViewModel behavior

**Platform Scope**: iOS only (no Android, Web, Backend changes)

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Localization Infrastructure)

**Purpose**: Add localization strings required for fullscreen map navigation title

- [X] T001 Add fullscreen map localization key to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- [X] T002 [P] Add fullscreen map localization key to `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings`
- [X] T003 Regenerate Strings.swift by running `swiftgen` in `/iosApp/`

---

## Phase 2: Foundational (No Blocking Prerequisites)

**Purpose**: No foundational/blocking tasks for this feature - localization setup in Phase 1 is sufficient

**⚠️ NOTE**: This feature has no complex infrastructure requirements. Proceed directly to User Story 1.

---

## Phase 3: User Story 1 - Navigate to Fullscreen Map View (Priority: P1) 🎯 MVP

**Goal**: Users can tap the landing page map preview to open a fullscreen map view, then return using the back button or edge swipe gesture.

**Independent Test**: Launch app → Navigate to Home tab → Tap map preview → Verify fullscreen map opens with "Pet Locations" title → Tap back or swipe from left edge → Verify returns to landing page.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests**:
- [X] T004 [P] [US1] Create test file `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift` with initial test structure
- [X] T005 [P] [US1] Write test `testInit_shouldCreateViewModel()` verifying ViewModel instantiation in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T006 [P] [US1] Write test `testHandleMapTap_whenCallbackSet_shouldInvokeCallback()` in `/iosApp/iosAppTests/Features/LandingPage/LandingPageViewModelTests.swift`

### Implementation for User Story 1

**ViewModel & View** (Core components):
- [X] T007 [P] [US1] Create directory structure `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/`
- [X] T008 [US1] Create FullscreenMapViewModel in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift` (minimal @MainActor class, ObservableObject, empty placeholder)
- [X] T009 [US1] Create FullscreenMapView in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift` (SwiftUI view with Color(.systemBackground) and accessibilityIdentifier)

**LandingPageViewModel Modification**:
- [X] T010 [US1] Add `onShowFullscreenMap: (() -> Void)?` closure property to LandingPageViewModel in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`
- [X] T011 [US1] Update map tap handler to invoke `onShowFullscreenMap?()` in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`

**HomeCoordinator Modification** (Navigation integration):
- [X] T012 [US1] Add `showFullscreenMap()` private method to HomeCoordinator in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`
- [X] T013 [US1] Wire `viewModel.onShowFullscreenMap` callback to `showFullscreenMap()` in HomeCoordinator's `start()` method in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`
- [X] T014 [US1] Implement navigation push with UIHostingController, title, and largeTitleDisplayMode in `showFullscreenMap()` in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`

**Test Identifiers** (E2E preparation):
- [X] T015 [US1] Verify `accessibilityIdentifier("fullscreenMap.container")` is set on FullscreenMapView root element in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`

**Checkpoint**: User Story 1 should be fully functional - tapping map preview opens fullscreen map, back button/swipe returns to landing page

---

## Phase 4: Polish & Verification

**Purpose**: Final validation and cleanup

- [X] T016 Build iOS project in Xcode and verify no compilation errors
- [X] T017 Run unit tests with `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [X] T018 Verify test coverage meets 80% threshold for FullscreenMapViewModel
- [ ] T019 Run manual verification per quickstart.md checklist (all 10 verification steps)
- [X] T020 [P] Add SwiftDoc comments to `showFullscreenMap()` method if purpose is not self-explanatory

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - start immediately
- **User Story 1 (Phase 3)**: Depends on Phase 1 completion (localization strings must exist before SwiftGen can generate Strings.swift)
- **Polish (Phase 4)**: Depends on Phase 3 completion

### Within User Story 1

1. **Tests first**: T004-T006 (write failing tests)
2. **Core implementation**: T007-T009 (ViewModel + View)
3. **Integration**: T010-T014 (wire up navigation)
4. **Verification**: T015 (test identifiers)

### Task Dependencies

```
T001 ─┬─► T003 ─► T004-T006 (tests) ─► T007-T009 (implementation)
T002 ─┘                                       │
                                              ▼
                              T010-T011 (LandingPageViewModel)
                                              │
                                              ▼
                              T012-T014 (HomeCoordinator)
                                              │
                                              ▼
                              T015 (test identifiers)
                                              │
                                              ▼
                              T016-T020 (verification)
```

### Parallel Opportunities

- T001 and T002 (localization strings) can run in parallel
- T004, T005, T006 (test files) can be created in parallel
- T007 (directory) must complete before T008-T009
- T020 (documentation) can run in parallel with T016-T019

---

## Parallel Example: User Story 1 Tests

```bash
# Launch all test creation tasks together:
Task T004: "Create test file structure"
Task T005: "Write FullscreenMapViewModel init test"
Task T006: "Write LandingPageViewModel callback test"
```

---

## Implementation Strategy

### MVP (User Story 1 Only)

This feature has only one user story (P1), which IS the MVP:

1. Complete Phase 1: Setup (localization) - ~5 minutes
2. Complete Phase 3: User Story 1 - ~30 minutes
3. Complete Phase 4: Polish & Verification - ~10 minutes
4. **DONE**: Feature is complete and testable

### Estimated Total Time

- **Setup**: 5 minutes
- **Tests**: 10 minutes
- **Implementation**: 20 minutes
- **Verification**: 10 minutes
- **Total**: ~45 minutes

---

## Files Summary

| File | Action | Phase |
|------|--------|-------|
| `iosApp/iosApp/Resources/en.lproj/Localizable.strings` | Modify | 1 |
| `iosApp/iosApp/Resources/pl.lproj/Localizable.strings` | Modify | 1 |
| `iosApp/iosApp/Generated/Strings.swift` | Regenerate | 1 |
| `iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift` | Create | 3 |
| `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift` | Create | 3 |
| `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift` | Create | 3 |
| `iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift` | Modify | 3 |
| `iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift` | Modify | 3 |

---

## Notes

- [P] tasks = different files, no dependencies
- [US1] label maps all tasks to User Story 1 (the only story)
- This is a simple iOS-only feature with no cross-platform concerns
- UIKit handles rapid-tap prevention automatically (no custom implementation needed)
- Navigation title uses SwiftGen-generated `L10n.FullscreenMap.navigationTitle`
- Placeholder view uses `Color(.systemBackground)` - MapKit integration in future ticket

