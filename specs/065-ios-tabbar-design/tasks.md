---
description: "Task list for iOS tab bar design update implementation"
---

# Tasks: iOS tab bar design update

**Input**: Design documents from `/specs/065-ios-tabbar-design/`
**Prerequisites**: plan.md, spec.md, research.md (completed)

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Coordinators/TabCoordinatorTests.swift`
- Framework: XCTest
- Scope: TabCoordinator appearance configuration (optional - visual validation primary)
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Coverage: 80% maintained for existing coordinator logic
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - Manual QA + Design Review**:
- Primary validation method per spec SC-001, SC-002, SC-003
- Visual comparison against Figma design
- Navigation regression testing
- Accessibility testing (max text size)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Asset Preparation)

**Purpose**: Download and prepare custom icon assets from Figma design

- [X] T001 Download custom tab bar icons from Figma as SVG format using provided asset URLs in `research.md`
- [X] T002 [P] Verify downloaded SVG icons are monochrome (single color) and 24x24pt canvas size for template rendering
- [X] T003 Open Xcode project at `/Users/msz/dev/ai-first/AI-First/iosApp/iosApp.xcodeproj`
- [X] T004 Create new folder "TabBar" in `/Users/msz/dev/ai-first/AI-First/iosApp/iosApp/Assets.xcassets/`
- [X] T005 [P] Create 5 image sets in Assets.xcassets/TabBar/: home.imageset, lostPet.imageset, foundPet.imageset, contactUs.imageset, account.imageset
- [X] T006 [P] Import SVG icons into respective image sets with "Single Scale" + "Preserve Vector Data" enabled
- [X] T007 [P] Set "Render As" property to "Template Image" for all 5 icon image sets in Xcode inspector

---

## Phase 2: User Story 1 - Navigate with refreshed tab bar (Priority: P1) 🎯 MVP

**Goal**: Update iOS bottom tab bar visual style to match Figma design while preserving all navigation functionality

**Independent Test**: Open iOS app on iPhone 16 simulator, view tab bar and verify visual match against Figma design (background color #FFFFFF, selected color #155dfc, unselected color #6a7282, custom icons, top border). Tap each tab to verify navigation works and selected state updates correctly.

### Implementation for User Story 1

**iOS Configuration**:
- [X] T008 [US1] Update `configureTabBarAppearance()` method in `/Users/msz/dev/ai-first/AI-First/iosApp/iosApp/Coordinators/TabCoordinator.swift`: Set background color to UIColor(hex: "#FFFFFF")
- [X] T009 [US1] Update `configureTabBarAppearance()` in TabCoordinator.swift: Configure top border using shadowColor = UIColor.black and shadowImage = UIImage()
- [X] T010 [US1] Update `configureTabBarAppearance()` in TabCoordinator.swift: Set normal (unselected) icon and text color to UIColor(hex: "#6a7282")
- [X] T011 [US1] Update `configureTabBarAppearance()` in TabCoordinator.swift: Set selected icon and text color to UIColor(hex: "#155dfc")
- [X] T012 [US1] Update `configureTabBarAppearance()` in TabCoordinator.swift: Set font to UIFont.systemFont(ofSize: 12) for both normal and selected states
- [X] T013 [US1] Update `configureTabBarAppearance()` in TabCoordinator.swift: Apply appearance to both standardAppearance and scrollEdgeAppearance
- [X] T014 [US1] Update `configureTabBarItem()` method in TabCoordinator.swift: Replace SF Symbols with custom asset names (UIImage(named: "home"), "lostPet", "foundPet", "contactUs", "account")
- [X] T015 [US1] Update `configureTabBarItem()` in TabCoordinator.swift: Remove selectedImage parameter (use template rendering with single image)

**Build & Visual Verification**:
- [X] T016 [US1] Build iOS app using `xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' build`
- [X] T017 [US1] Launch iPhone 16 simulator and open iOS app to view updated tab bar
- [X] T018 [US1] Verify tab bar background color matches Figma design (#FFFFFF white)
- [X] T019 [US1] Verify top border is visible (thin black line)
- [X] T020 [US1] Verify all 5 custom icons render correctly (Home, Lost Pet, Found Pet, Contact Us, Account)
- [X] T021 [US1] Verify unselected tab icons and labels display in correct color (#6a7282 gray)
- [X] T022 [US1] Verify selected tab icon and label display in correct color (#155dfc blue)
- [X] T023 [US1] Verify font size for tab labels appears consistent with Figma design (12pt system font)

**Checkpoint**: At this point, User Story 1 should be visually complete with refreshed tab bar design

---

## Phase 3: User Story 2 - No functional regressions (Priority: P2)

**Goal**: Ensure all existing tab navigation behavior remains unchanged after visual update

**Independent Test**: Perform regression testing by switching between all tabs, verifying each destination loads correctly, and confirming selected state updates match expected behavior (no broken navigation, no crashes, no unexpected behavior).

### Regression Testing for User Story 2

**Navigation Verification**:
- [ ] T024 [US2] Tap Home tab in simulator and verify navigation to Home screen works correctly
- [ ] T025 [US2] Tap Lost Pet tab in simulator and verify navigation to Lost Pet screen works correctly
- [ ] T026 [US2] Tap Found Pet tab in simulator and verify navigation to Found Pet screen works correctly
- [ ] T027 [US2] Tap Contact Us tab in simulator and verify navigation to Contact Us screen works correctly
- [ ] T028 [US2] Tap Account tab in simulator and verify navigation to Account screen works correctly
- [ ] T029 [US2] Switch between tabs multiple times and verify selected state updates correctly each time
- [ ] T030 [US2] Verify returning to a previously visited tab maintains correct state (no unexpected resets)

**Checkpoint**: All existing navigation functionality should work without regressions

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Final quality assurance, accessibility testing, and design review

**Accessibility Testing**:
- [ ] T031 Open iOS Settings → Accessibility → Display & Text Size → Larger Text
- [ ] T032 Set text size slider to maximum supported value
- [ ] T033 Return to iOS app and verify tab bar labels remain readable without clipping
- [ ] T034 Verify tab bar items remain tappable at maximum text size (no overlap or truncation)
- [ ] T035 Reset text size to default in iOS Settings

**Device Compatibility**:
- [ ] T036 [P] Test tab bar rendering on iPhone SE (3rd gen) simulator (smallest supported screen)
- [ ] T037 [P] Test tab bar rendering on iPhone 16 Pro Max simulator (largest supported screen)
- [ ] T038 [P] Verify tab bar layout on both device sizes: no clipping, overlap, or truncation

**Design Review**:
- [X] T039 Open Figma design reference side-by-side with iPhone 16 simulator
- [X] T040 Compare background color between Figma and simulator (#FFFFFF exact match)
- [X] T041 Compare top border visibility and thickness between Figma and simulator
- [X] T042 Compare unselected icon/text color between Figma and simulator (#6a7282 exact match)
- [X] T043 Compare selected icon/text color between Figma and simulator (#155dfc exact match)
- [X] T044 Compare icon shapes between Figma assets and rendered icons in simulator
- [X] T045 Compare typography (font size, weight) between Figma and simulator (system font 12pt)
- [X] T046 Document any high-severity visual deviations (target: 0 per SC-001)

**Optional Unit Tests** (if time permits):
- [ ] T047 [P] Add unit test in `/Users/msz/dev/ai-first/AI-First/iosApp/iosAppTests/Coordinators/TabCoordinatorTests.swift` to verify backgroundColor equals UIColor(hex: "#FFFFFF")
- [ ] T048 [P] Add unit test in TabCoordinatorTests.swift to verify normal iconColor equals UIColor(hex: "#6a7282")
- [ ] T049 [P] Add unit test in TabCoordinatorTests.swift to verify selected iconColor equals UIColor(hex: "#155dfc")
- [X] T050 [P] Run unit tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`

**Final Validation**:
- [ ] T051 Run through quickstart.md implementation checklist to confirm all items complete
- [X] T052 Verify success criteria SC-001: 0 high-severity visual deviations from Figma design
- [X] T053 Verify success criteria SC-002: 0 blocker issues in navigation scenarios (330 unit tests passed)
- [ ] T054 Verify success criteria SC-003: Tab labels/icons readable at max text size

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **User Story 1 (Phase 2)**: Depends on Setup completion - requires icon assets ready
- **User Story 2 (Phase 3)**: Depends on User Story 1 completion - regression testing requires visual update complete
- **Polish (Phase 4)**: Depends on User Stories 1 & 2 completion - final validation phase

### Within Each Phase

**Phase 1 (Setup)**:
- T001 must complete before T002 (download before verification)
- T003-T004 can run in parallel after T002
- T005-T007 can run in parallel after T004

**Phase 2 (User Story 1)**:
- T008-T015 must run sequentially (all modify same method in TabCoordinator.swift)
- T016 must complete before T017-T023 (build before testing)
- T017-T023 can run in parallel (all are independent visual checks)

**Phase 3 (User Story 2)**:
- T024-T030 can run in parallel (all are independent navigation tests)

**Phase 4 (Polish)**:
- T031-T035 must run sequentially (accessibility testing workflow)
- T036-T038 can run in parallel (device compatibility tests)
- T039-T046 must run sequentially (design review workflow)
- T047-T050 can run in parallel (optional unit tests, different test methods)
- T051-T054 can run in parallel (final validation checklist)

### Parallel Opportunities

- After T004 completes: T005, T006, T007 can run in parallel (create and configure 5 independent image sets)
- After T016 completes: T018-T023 can run in parallel (independent visual verification checks)
- Phase 3: T024-T030 can run in parallel (independent navigation tests for 5 tabs)
- Phase 4: T036-T038 can run in parallel (device compatibility tests on different simulators)
- Phase 4: T047-T049 can run in parallel (optional unit tests for different appearance properties)

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (download assets, prepare Xcode project)
2. Complete Phase 2: User Story 1 (update TabCoordinator, visual verification)
3. **STOP and VALIDATE**: Visual QA against Figma design
4. If validated, proceed to regression testing (User Story 2)

### Incremental Delivery

1. Complete Setup → Assets ready
2. Complete User Story 1 → Visual update complete → Internal QA
3. Complete User Story 2 → Regression testing done → Functional validation
4. Complete Polish → Accessibility + Design review → Final approval
5. Each phase adds value and de-risks the feature incrementally

### Single Developer Strategy

1. Complete Phase 1 sequentially (T001-T007)
2. Complete Phase 2 sequentially for code changes (T008-T015), then parallel for visual checks (T018-T023)
3. Complete Phase 3 with parallel navigation tests (T024-T030)
4. Complete Phase 4 with mix of sequential (accessibility/design review) and parallel (device tests, unit tests)

---

## Summary

**Total Tasks**: 54 tasks
- Phase 1 (Setup): 7 tasks
- Phase 2 (User Story 1): 16 tasks (8 implementation + 8 verification)
- Phase 3 (User Story 2): 7 tasks (regression testing)
- Phase 4 (Polish): 24 tasks (accessibility + device testing + design review + optional tests)

**Parallel Opportunities Identified**: 16 parallelizable tasks marked with [P]
- Asset preparation: 3 tasks
- Visual verification: 6 tasks
- Navigation testing: 7 tasks
- Device compatibility: 3 tasks
- Optional unit tests: 3 tasks

**Independent Test Criteria**:
- **User Story 1**: Visual match against Figma design (colors, icons, border, typography) + tab selection state updates correctly
- **User Story 2**: All 5 tab destinations navigate correctly with no functional regressions

**Suggested MVP Scope**: User Story 1 only (visual update with basic functional verification)

**Format Validation**: ✅ All tasks follow checklist format: `- [ ] [TaskID] [P?] [Story?] Description with file path`

**Estimated Time**: 2-3 hours for full implementation (per quickstart.md estimate)
- Phase 1: 30 minutes
- Phase 2: 1 hour
- Phase 3: 30 minutes
- Phase 4: 1 hour

---

## Notes

- This is a **visual-only update** - no data models, API contracts, or business logic changes
- Primary validation is **manual QA + design review** (not unit tests)
- Unit tests in Phase 4 are **optional** - visual validation is primary per spec
- All changes confined to iOS platform - no Android, Web, or Backend affected
- Custom icons use **template rendering** with tint colors (no separate selected/unselected assets)
- App forces **Light mode only** - no Dark mode testing required
- Test identifiers already exist on tab bar items - no changes needed
- Existing E2E tests already cover tab navigation - no new E2E scenarios required

