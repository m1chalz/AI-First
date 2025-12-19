# Implementation Plan: iOS Landing Page Full Content Scroll

**Branch**: `KAN-30-ios-landing-page-full-content-scroll` | **Date**: 2025-12-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-30-ios-landing-page-full-content-scroll/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Replace nested scroll architecture in iOS landing page with single outer ScrollView so all sections (hero panel, list header, and announcements list) scroll together as one continuous content area. This eliminates competing scroll regions and improves accessibility on smaller screens.

## Technical Context

**Language/Version**: Swift (iOS 18+ target, iPhone 16 Simulator)  
**Primary Dependencies**: SwiftUI, UIKit (coordinators)  
**Storage**: N/A (UI-only refactoring)  
**Testing**: XCTest with Swift Concurrency (async/await)  
**Target Platform**: iOS 18+
**Project Type**: Mobile (iOS)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: iOS-only (Android and Web explicitly out of scope)  
**Scale/Scope**: 3 affected files - LandingPageView, AnnouncementCardsListView, and tests

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only UI refactoring. Android, Web, and Backend checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only feature)
  - iOS: Changes only in `/iosApp` - no shared code
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes)
  - NO shared compiled code between platforms ✓
  - Violation justification: Not applicable - compliant

- [x] **Android MVI Architecture**: N/A (iOS-only feature)
  - Violation justification: Not applicable

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation ✓ (HomeCoordinator unchanged)
  - ViewModels conform to `ObservableObject` with `@Published` properties ✓ (LandingPageViewModel unchanged)
  - ViewModels communicate with coordinators via methods or closures ✓ (existing pattern preserved)
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✓ (LandingPageView refactored to improve scroll, no logic changes)
  - Violation justification: Not applicable - compliant

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A (iOS-only feature)
  - iOS: No new repositories (UI-only refactoring) ✓
  - Web: N/A (iOS-only feature)
  - Backend: N/A (iOS-only feature)
  - Violation justification: Not applicable - no repository changes

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (iOS-only feature)
  - iOS: Existing manual DI unchanged (ServiceContainer already in place) ✓
  - Web: N/A (iOS-only feature)
  - Backend: N/A (iOS-only feature)
  - Violation justification: Not applicable - no DI changes needed

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (iOS-only feature)
  - iOS: Existing tests updated to verify scroll behavior ✓ (LandingPageViewModelTests)
  - Web: N/A (iOS-only feature)
  - Backend: N/A (iOS-only feature)
  - Coverage target: 80% line + branch coverage per platform ✓ (maintained)
  - Violation justification: Not applicable - UI refactoring maintains existing coverage

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A (iOS-only feature)
  - Mobile: Existing Appium tests updated for scroll verification (Java + Cucumber)
  - Location: `/e2e-tests/java/src/test/resources/features/mobile/landing-page-scroll.feature`
  - Screen Object Model used ✓
  - User Story 1 & 2 covered by E2E scenarios
  - Violation justification: Not applicable - compliant

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A (iOS-only feature)
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✓ (existing pattern unchanged)
  - Web: N/A (iOS-only feature)
  - Backend: N/A (iOS-only feature)
  - No Combine used ✓
  - Violation justification: Not applicable - compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A (iOS-only feature)
  - iOS: Existing `accessibilityIdentifier` modifiers preserved ✓ (no new interactive elements)
  - Web: N/A (iOS-only feature)
  - Violation justification: Not applicable - no new UI controls

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A (iOS-only feature)
  - Swift: Existing SwiftDoc documentation updated where needed ✓
  - TypeScript: N/A (iOS-only feature)
  - Documentation concise and high-level ✓ (updated view documentation to reflect scroll changes)
  - Violation justification: Not applicable - compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - iOS: Unit tests follow Given-When-Then structure ✓ (existing convention maintained)
  - E2E tests: Gherkin scenarios naturally follow Given-When-Then ✓
  - Test names: Swift convention `test{Method}_when{Condition}_should{ExpectedBehavior}` ✓
  - Violation justification: Not applicable - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (iOS-only feature, `/server` not affected)
  - Violation justification: Not applicable

- [x] **Backend Code Quality**: N/A (iOS-only feature, `/server` not affected)
  - Violation justification: Not applicable

- [x] **Backend Dependency Management**: N/A (iOS-only feature, `/server` not affected)
  - Violation justification: Not applicable

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A (iOS-only feature, `/webApp` not affected)
  - Violation justification: Not applicable

- [x] **Web Code Quality**: N/A (iOS-only feature, `/webApp` not affected)
  - Violation justification: Not applicable

- [x] **Web Dependency Management**: N/A (iOS-only feature, `/webApp` not affected)
  - Violation justification: Not applicable

- [x] **Web Business Logic Extraction**: N/A (iOS-only feature, `/webApp` not affected)
  - Violation justification: Not applicable

- [x] **Web TDD Workflow**: N/A (iOS-only feature, `/webApp` not affected)
  - Violation justification: Not applicable

- [x] **Web Testing Strategy**: N/A (iOS-only feature, `/webApp` not affected)
  - Violation justification: Not applicable

- [x] **Backend Directory Structure**: N/A (iOS-only feature, `/server` not affected)
  - Violation justification: Not applicable

- [x] **Backend TDD Workflow**: N/A (iOS-only feature, `/server` not affected)
  - Violation justification: Not applicable

- [x] **Backend Testing Strategy**: N/A (iOS-only feature, `/server` not affected)
  - Violation justification: Not applicable

## Project Structure

### Documentation (this feature)

```text
specs/KAN-30-ios-landing-page-full-content-scroll/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command) - N/A (no data models)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command) - N/A (no API contracts)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

**iOS-Only Mobile Feature** - Affected Files:

```text
iosApp/iosApp/
├── Features/
│   └── LandingPage/
│       ├── Views/
│       │   ├── LandingPageView.swift          # MODIFIED - wrap in ScrollView, pass hasOwnScrollView: false
│       │   └── LandingPageViewModel.swift     # UNCHANGED - no logic changes
│       └── Coordinators/
│           └── HomeCoordinator.swift          # UNCHANGED
└── Views/
    └── AnnouncementCardsListView.swift        # MODIFIED - add hasOwnScrollView parameter (default: true)

iosApp/iosAppTests/
└── Views/
    └── AnnouncementCardsListViewModelTests.swift  # UNCHANGED - ViewModel logic not affected

e2e-tests/java/
├── src/test/resources/features/mobile/
│   └── landing-page-scroll.feature           # NEW - Gherkin scenarios
└── src/test/java/.../steps/mobile/
    └── LandingPageScrollSteps.java           # NEW - step definitions
```

**Structure Decision**: This is an iOS-only UI refactoring affecting 2 main SwiftUI views and E2E test coverage. No backend or other platform changes. The change adds `hasOwnScrollView` parameter to `AnnouncementCardsListView` (backwards compatible) and wraps `LandingPageView` in outer ScrollView with `hasOwnScrollView: false` to achieve continuous page scrolling while preserving component reusability.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| **FR-001 Partial Violation** | `AnnouncementCardsListView` is a reusable component used in multiple contexts. Adding `hasOwnScrollView` parameter allows LandingPageView to disable nested scroll while AnnouncementListView keeps it. | Removing ScrollView entirely would break `AnnouncementListView` (standalone full-screen list). Creating two separate components adds code duplication and maintenance burden. |

**Justification Detail**:
- **FR-001** states: "The iOS landing page MUST scroll vertically as a single continuous content area (no nested/independent scroll regions)"
- **Implementation**: `AnnouncementCardsListView` gets `hasOwnScrollView: Bool = true` parameter
- **LandingPageView**: Passes `hasOwnScrollView: false` → no nested scroll (FR-001 satisfied)
- **AnnouncementListView**: Uses default `true` → has own scroll (standalone screen, not affected by FR-001)
- **Risk**: Technically, someone could still create nested scroll by wrapping component in ScrollView with `hasOwnScrollView: true`. However, this is developer error, not architectural issue. The parameter provides explicit control.
- **Trade-off Accepted**: Reusability > strict FR-001 compliance. The spec requirement is satisfied for LandingPageView specifically.

## Post-Design Constitution Check Re-evaluation

*Re-evaluated after Phase 1 design completion (research.md, quickstart.md generated)*

### Verification Summary

All Constitution Check items remain compliant after design phase (with one justified violation):

**Platform Architecture Compliance**:
- ✅ **Platform Independence**: Confirmed iOS-only changes in `/iosApp` (research.md Decision 1)
- ✅ **iOS MVVM-C Architecture**: Confirmed View layer only - ViewModel/Coordinator unchanged (quickstart.md Step 1-2)
- ✅ **Interface-Based Design**: No repository changes (data-model.md confirms no data layer changes)
- ✅ **Dependency Injection**: No DI changes needed (research.md confirms existing ServiceContainer unchanged)
- ✅ **80% Test Coverage**: Unit tests maintained + E2E tests added (quickstart.md Step 5)
- ✅ **E2E Tests**: Gherkin scenarios added for User Story 1 & 2 (quickstart.md Step 5)
- ✅ **Async Programming**: No async changes (UI-only refactoring, research.md confirms)
- ✅ **Test Identifiers**: Preserved existing identifiers (quickstart.md verifies no changes)
- ✅ **Documentation**: SwiftDoc updated for views (quickstart.md Step 1-2)
- ✅ **Given-When-Then Tests**: E2E tests follow Gherkin structure (quickstart.md Step 5)

**Backend/Web/Android Checks**:
- ✅ All marked N/A (iOS-only feature) - confirmed in design phase

**Spec Requirement Adjustment (FR-001)**:
- ⚠️ **FR-001 Partial Violation**: `hasOwnScrollView` parameter approach technically allows nested scrolling when misused
- **Justification**: Reusability of `AnnouncementCardsListView` across multiple screens (LandingPage, AnnouncementList) is more valuable than strict FR-001 compliance
- **Mitigation**: LandingPageView explicitly passes `hasOwnScrollView: false`, satisfying FR-001 for the specific screen
- **See**: Complexity Tracking section for full justification

### Design Artifacts Verification

**research.md**:
- Documents single outer ScrollView decision (aligned with iOS best practices)
- Documents `hasOwnScrollView` parameter approach (Decision 2 updated)
- Confirms MVVM-C architecture preserved (no ViewModel changes)

**data-model.md**:
- Confirms no data model changes (UI-only refactoring)
- Existing models (Announcement, ViewModels) unchanged

**quickstart.md**:
- Implementation steps preserve MVVM-C architecture
- Shows `hasOwnScrollView: false` parameter usage in LandingPageView
- Test coverage maintained (unit tests + E2E tests)
- All accessibility identifiers preserved
- Documentation updated appropriately

### Final Gate Status: ✅ PASSED (with justified violation)

All Constitution Check items compliant after Phase 1 design. FR-001 partial violation documented and justified in Complexity Tracking. Ready to proceed with implementation (Phase 2 - tasks.md).
