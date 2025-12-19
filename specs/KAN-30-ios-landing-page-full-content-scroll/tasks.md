# Tasks: iOS Landing Page Full Content Scroll

**Feature**: KAN-30 iOS Landing Page Full Content Scroll  
**Branch**: `KAN-30-ios-landing-page-full-content-scroll`  
**Input**: Design documents from `/specs/KAN-30-ios-landing-page-full-content-scroll/`

**Prerequisites**:
- ✅ plan.md (Implementation plan with technical context)
- ✅ spec.md (User stories with P1 priority)
- ✅ research.md (Architectural decisions)
- ✅ data-model.md (No data model changes - UI-only refactoring)
- ✅ quickstart.md (Step-by-step implementation guide)

**Tests**: Test requirements for this iOS-only feature:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/` (XCTest with Swift Concurrency)
- Coverage: 80% line + branch coverage maintained
- Scope: ViewModels (LandingPageViewModel unchanged, no new ViewModels)
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/java/src/test/resources/features/mobile/landing-page-scroll.feature`
- Framework: Java 21 + Maven + Appium + Cucumber (unified for all mobile platforms)
- Coverage: User Story 1 & 2 acceptance criteria
- Run: `mvn test -Dtest=IosTestRunner` (from e2e-tests/java/)
- Convention: Gherkin scenarios follow Given-When-Then structure

**Organization**: Tasks are grouped by user story to enable independent implementation and testing. However, both stories are tightly coupled (changing scroll architecture affects all interactions), so they are implemented together as a single coherent change.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify prerequisites and project readiness

- [ ] T001 Verify Xcode with iOS 18 SDK is installed and configured
- [ ] T002 Verify iPhone 16 Simulator is configured and accessible
- [ ] T003 Verify branch `KAN-30-ios-landing-page-full-content-scroll` is checked out
- [ ] T004 Verify iOS project builds successfully in current state
- [ ] T005 Verify E2E test infrastructure is functional (Appium + Maven + Java 21)

---

## Phase 2: User Story 1 & 2 Combined - Full Content Scroll with Preserved Interactions (Priority: P1) 🎯

**Goal**: Replace nested scroll architecture with single outer ScrollView so all landing page sections scroll continuously, while preserving all existing interactions (taps, navigation).

**Why Combined**: Both stories are tightly coupled - changing scroll architecture inherently affects interaction behavior. Implementing them together ensures consistent scroll + interaction behavior.

**Independent Test**: 
1. Open landing page on iPhone 16 Simulator
2. Scroll from top to bottom - verify all sections (hero, header, list) scroll together continuously
3. During/after scrolling, tap hero buttons, list header "View All", and announcement cards - verify same navigation occurs as before

### Tests for User Story 1 & 2 (MANDATORY) ✅

**iOS Unit Tests**:

> Note: No new unit tests needed. Existing LandingPageViewModelTests remain valid because ViewModel logic is unchanged (UI-only refactoring). Run existing tests to verify no regressions.

- [ ] T006 [US1] [US2] Run existing unit tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T007 [US1] [US2] Verify all LandingPageViewModelTests pass (no ViewModel logic changes, tests should remain green)
- [ ] T008 [US1] [US2] Verify AnnouncementCardsListViewModelTests pass (no ViewModel logic changes)
- [ ] T009 [US1] [US2] Verify coverage report shows maintained 80%+ coverage at `/iosApp/build/logs/test/`

**End-to-End Tests**:

- [ ] T010 [P] [US1] [US2] Create E2E feature file in `/e2e-tests/java/src/test/resources/features/mobile/landing-page-scroll.feature` (Gherkin scenarios per quickstart.md)
- [ ] T011 [P] [US1] [US2] Create E2E step definitions in `/e2e-tests/java/src/test/java/com/petspot/e2e/steps/mobile/LandingPageScrollSteps.java` (Java + Appium)
- [ ] T012 [US1] Scenario: "Scroll entire landing page content continuously" (verify hero/header/list scroll together, no nested scroll)
- [ ] T013 [US2] Scenario: "Interactive elements work during and after scrolling" (tap card after scrolling, verify navigation)
- [ ] T014 [US1] Scenario: "Scroll position preserved during state changes" (scroll down, trigger reload, verify position maintained)
- [ ] T015 [US1] Scenario: "Short content does not cause blank space" (edge case: 2 announcements, verify no excessive blank space)
- [ ] T016 [US1] [US2] Run E2E tests: `cd /e2e-tests/java && mvn test -Dtest=IosTestRunner`
- [ ] T017 [US1] [US2] Verify all E2E scenarios pass (view report at `/e2e-tests/java/target/cucumber-reports/ios/index.html`)

### Implementation for User Story 1 & 2

**iOS Implementation** (View Layer Changes Only):

> **CRITICAL**: This is UI-only refactoring. NO changes to ViewModels, Coordinators, Repositories, Domain models, or DI setup.

- [ ] T018 [US1] [US2] Modify LandingPageView.swift at `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift`:
  - Replace `VStack` with `ScrollView { LazyVStack(spacing: 0) { ... } }`
  - Remove `.frame(maxHeight: .infinity)` from AnnouncementCardsListView
  - Add `hasOwnScrollView: false` parameter to AnnouncementCardsListView
  - Update SwiftDoc comments to reflect single ScrollView architecture
  - Preserve all existing modifiers (`.task`, `.alert`) and subviews (hero, header, list in same order)

- [ ] T019 [US1] [US2] Modify AnnouncementCardsListView.swift at `/iosApp/iosApp/Views/AnnouncementCardsListView.swift`:
  - Add `hasOwnScrollView: Bool = true` parameter (default: true for backwards compatibility)
  - Add explicit `init` with `hasOwnScrollView` parameter
  - Extract `listContent` as computed property (LazyVStack with announcement cards)
  - Conditionally wrap `listContent` in ScrollView based on `hasOwnScrollView` parameter
  - Update SwiftDoc comments to document autonomous component pattern and scroll behavior
  - Preserve all existing state rendering (ZStack with loading/error/empty/success states)

- [ ] T020 [P] [US1] [US2] Add SwiftDoc documentation to LandingPageView and AnnouncementCardsListView (update existing comments to reflect scroll architecture changes)

- [ ] T021 [US1] [US2] Verify existing accessibility identifiers preserved (no changes to identifiers, verify manually in code review):
  - `landingPage.heroPanel.*` (hero buttons)
  - `landingPage.listHeader.*` (list header actions)
  - `landingPage.announcementList.list` (list container)
  - `landingPage.announcementList.item.*` (individual cards)

**Checkpoint**: At this point, scroll architecture is changed. Next: verify implementation.

---

## Phase 3: Manual Verification & Testing

**Purpose**: Verify continuous scroll behavior and preserved interactions before E2E tests

- [ ] T022 [US1] [US2] Build iOS app: Open `/iosApp/iosApp.xcodeproj` in Xcode and build for iPhone 16 Simulator
- [ ] T023 [US1] [US2] Run app on iPhone 16 Simulator and navigate to landing page
- [ ] T024 [US1] Manual Test: Scroll from top to bottom - verify hero panel scrolls off screen, list header scrolls up, announcement cards scroll into view (continuous scroll)
- [ ] T025 [US1] Manual Test: Verify no nested scroll behavior (single scroll gesture moves all sections together)
- [ ] T026 [US2] Manual Test: Tap hero "Lost Pet" button after scrolling - verify navigation works (same as before)
- [ ] T027 [US2] Manual Test: Tap hero "Found Pet" button after scrolling - verify navigation works (same as before)
- [ ] T028 [US2] Manual Test: Tap list header "View All" after scrolling - verify navigation works (same as before)
- [ ] T029 [US2] Manual Test: Scroll to announcement list, tap a card - verify navigation to pet details works (no missed taps)
- [ ] T030 [US1] Manual Test: Trigger loading state (e.g., pull to refresh if available) - verify hero panel remains visible during loading (inline state rendering)
- [ ] T031 [US1] Manual Test: Trigger error state (e.g., disable network) - verify hero panel remains visible during error (inline state rendering)
- [ ] T032 [US1] Manual Test: Use iPhone SE simulator (smaller screen) - verify all sections reachable via continuous scroll
- [ ] T033 [US1] Manual Test: Enable larger text size (Accessibility > Larger Text) - verify content still scrolls continuously

**Checkpoint**: Manual verification complete. If issues found, iterate on implementation tasks T018-T019 before proceeding to automated tests.

---

## Phase 4: Automated Testing & Validation

**Purpose**: Run all automated tests and verify coverage

- [ ] T034 [US1] [US2] Run iOS unit tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T035 [US1] [US2] Verify all unit tests pass (LandingPageViewModelTests, AnnouncementCardsListViewModelTests)
- [ ] T036 [US1] [US2] Verify coverage maintained at 80%+ (view Xcode coverage report or `/iosApp/build/logs/test/`)
- [ ] T037 [US1] [US2] Run E2E tests: `cd /e2e-tests/java && mvn test -Dtest=IosTestRunner`
- [ ] T038 [US1] [US2] Verify all E2E scenarios pass (continuous scroll + interactions verified)
- [ ] T039 [US1] [US2] Review E2E test report at `/e2e-tests/java/target/cucumber-reports/ios/index.html`
- [ ] T040 [US1] [US2] Check Xcode console for runtime warnings or errors (should be clean)
- [ ] T041 [US1] [US2] Run Xcode static analyzer (Product > Analyze) - verify no new warnings

**Checkpoint**: All automated tests pass. Feature is complete and ready for code review.

---

## Phase 5: Polish & Documentation

**Purpose**: Final cleanup and documentation updates

- [ ] T042 [P] [US1] [US2] Review code changes in LandingPageView.swift and AnnouncementCardsListView.swift (verify Clean Code principles, SwiftDoc comments updated)
- [ ] T043 [P] [US1] [US2] Verify SwiftDoc documentation is concise and high-level (WHAT/WHY, not HOW - skip obvious comments)
- [ ] T044 [P] [US1] [US2] Verify quickstart.md implementation steps match actual implementation (validate guide accuracy)
- [ ] T045 [US1] [US2] Run final code review checklist from quickstart.md Step 6:
  - LandingPageView uses ScrollView with LazyVStack ✓
  - AnnouncementCardsListView has no nested ScrollView ✓
  - `.frame(maxHeight: .infinity)` removed ✓
  - Documentation comments updated ✓
  - Accessibility identifiers preserved ✓
  - Unit tests pass with 80%+ coverage ✓
  - Manual testing confirms continuous scroll ✓
  - E2E tests pass ✓
  - No ViewModel or Coordinator logic changed ✓
  - MVVM-C architecture preserved ✓

- [ ] T046 [US1] [US2] Verify no linter warnings or errors (run Xcode linter if configured)
- [ ] T047 [US1] [US2] Commit changes with descriptive message per project convention

**Checkpoint**: Feature complete, tested, and documented. Ready for PR/merge.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **User Story 1 & 2 Implementation (Phase 2)**: Depends on Setup completion
- **Manual Verification (Phase 3)**: Depends on Implementation (Phase 2) completion
- **Automated Testing (Phase 4)**: Depends on Manual Verification (Phase 3) passing
- **Polish (Phase 5)**: Depends on Automated Testing (Phase 4) passing

### Within Each Phase

**Phase 2 (Implementation)**:
1. T018 (LandingPageView changes) MUST complete before T022 (build)
2. T019 (AnnouncementCardsListView changes) MUST complete before T022 (build)
3. T018 and T019 can be worked on in parallel (different files)
4. T020 (documentation) can be done in parallel with T018/T019 or after

**Phase 2 (E2E Tests)**:
1. T010 (feature file) and T011 (step definitions) can be done in parallel [P]
2. T012-T015 (individual scenarios) can be written in parallel after T010/T011
3. T016 (run E2E tests) depends on T010-T015 completion
4. T017 (verify results) depends on T016 completion

**Phase 3 (Manual Verification)**:
- All manual tests (T024-T033) can be done in any order after T022/T023 (build and launch)
- Recommend testing critical paths first: T024 (continuous scroll), T029 (tap card)

**Phase 4 (Automated Testing)**:
- T034-T036 (unit tests) can run in parallel with T037-T039 (E2E tests) [P]
- T040-T041 (final checks) depend on all tests passing

**Phase 5 (Polish)**:
- T042-T044 (documentation review) can be done in parallel [P]
- T045 (code review checklist) should be done after T042-T044
- T046-T047 (final checks and commit) must be done last

### Parallel Opportunities

- **Phase 1 Setup**: All tasks (T001-T005) can be verified in parallel [P]
- **Phase 2 Implementation**: T018 (LandingPageView) and T019 (AnnouncementCardsListView) can be edited in parallel (different files) [P]
- **Phase 2 E2E Tests**: T010 (feature file) and T011 (step definitions) can be written in parallel [P]
- **Phase 2 Documentation**: T020 can be done in parallel with T018/T019 [P]
- **Phase 3 Manual Tests**: Multiple manual tests can be performed in quick succession (no blocking dependencies)
- **Phase 4 Automated Tests**: T034-T036 (unit tests) and T037-T039 (E2E tests) can run in parallel [P]
- **Phase 5 Polish**: T042-T044 (documentation review) can be done in parallel [P]

---

## Implementation Strategy

### Single-Phase Delivery (Recommended)

This feature is a small, tightly scoped UI refactoring affecting 2 files. Recommended approach:

1. **Complete all phases in one session**:
   - Phase 1: Setup (5 min)
   - Phase 2: Implementation + E2E test setup (30-45 min)
   - Phase 3: Manual verification (15 min)
   - Phase 4: Automated testing (10 min)
   - Phase 5: Polish (10 min)
   - **Total: ~1.5 hours** for complete feature

2. **Commit strategy**: Single atomic commit with both file changes + E2E tests

3. **Testing strategy**: Manual verification first (quick feedback), then automated tests

### Rollback Plan (If Issues Found)

If issues discovered during manual or automated testing:

1. **Identify issue**: Which acceptance criteria failed? (continuous scroll vs. interactions)
2. **Revert changes**: `git revert HEAD` (if committed) or discard changes in Xcode
3. **Restore nested scroll**:
   - LandingPageView: Change `ScrollView` back to `VStack`, re-add `.frame(maxHeight: .infinity)`
   - AnnouncementCardsListView: Wrap LazyVStack in `ScrollView` (remove conditional)
4. **Run tests**: Verify tests pass with reverted code
5. **Iterate**: Fix issue in separate branch, re-test, then merge

**Risk**: Low (UI-only change, easily reversible, 2 file changes)

---

## Parallel Example: Implementation Tasks

```bash
# Task T018 and T019 can be done in parallel (different files):
Developer A: Edit /iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift
Developer B: Edit /iosApp/iosApp/Views/AnnouncementCardsListView.swift

# Task T010 and T011 can be done in parallel:
Developer A: Write /e2e-tests/java/src/test/resources/features/mobile/landing-page-scroll.feature
Developer B: Write /e2e-tests/java/src/test/java/.../steps/mobile/LandingPageScrollSteps.java
```

---

## Notes

- **[P] tasks**: Different files, no dependencies - can run in parallel
- **[Story] label**: Maps task to specific user story (US1, US2) for traceability
- **User Story 1 & 2 Combined**: Both stories implemented together due to tight coupling (scroll architecture affects interactions)
- **No new unit tests**: Existing ViewModel tests remain valid (UI-only refactoring)
- **E2E tests**: Cover both User Story 1 (continuous scroll) and User Story 2 (preserved interactions) in integrated scenarios
- **Commit strategy**: Atomic commit with both file changes ensures feature is never in half-implemented state
- **Edge cases**: Small screens, large text, short content - tested in manual verification (Phase 3)
- **Constitution compliance**: iOS MVVM-C architecture preserved, no ViewModel/Coordinator/Repository changes, 80% coverage maintained

