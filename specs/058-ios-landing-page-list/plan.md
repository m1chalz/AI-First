# Implementation Plan: iOS Landing Page (Home Tab)

**Branch**: `058-ios-landing-page-list` | **Date**: 2025-12-16 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/058-ios-landing-page-list/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a landing page on iOS Home tab that displays the 5 most recent pet announcements fetched from the existing GET /api/v1/announcements endpoint. The implementation introduces an **autonomous component pattern** by creating `AnnouncementCardsListViewModel` - a self-contained, reusable ViewModel that manages list state and behavior independently. This ViewModel is composed by both `LandingPageViewModel` (with query limit: 5) and `AnnouncementListViewModel` (with query limit: nil), enabling true component autonomy and eliminating code duplication (DRY principle). The accompanying `AnnouncementCardsListView` observes its own ViewModel and handles loading/error/empty states internally. When users tap an announcement card, the app navigates to the Lost Pets tab and displays the selected pet's detail screen using coordinator-based cross-tab navigation. The feature includes query-driven configuration (filtering, sorting), proper separation of concerns (parent ViewModels handle feature-specific logic, child ViewModel handles list logic), and location-based distance display when permissions are granted.

## Technical Context

**Language/Version**: Swift 5.9+ (iOS 15+)  
**Primary Dependencies**: SwiftUI (UI), UIKit (Coordinators), URLSession or Alamofire (HTTP client), existing announcement models and repositories  
**Storage**: N/A (in-memory state management via ViewModels; no persistent storage for landing page cache)  
**Testing**: XCTest with Swift Concurrency (async/await), unit tests for ViewModels and repositories  
**Target Platform**: iOS 15+
**Project Type**: Mobile (iOS native app with coordinator-based navigation)  
**Performance Goals**: N/A (Performance is not a concern for this project)  
**Constraints**: 
  - Must reuse existing announcement list UI components (AnnouncementCard) for consistency
  - Client-side filtering and sorting (backend returns all announcements, iOS limits to 5 most recent)
  - No pull-to-refresh or data refresh on tab return (data loaded once per app launch)
  - Tab navigation managed by existing TabBarCoordinator from feature 054-ios-tab-navigation
  - Cross-tab navigation (Home → Lost Pets tab with detail screen) requires coordinator coordination
**Scale/Scope**: Single screen with list of 5 announcement cards, integrates with existing tab navigation and announcement detail flow

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature affecting only `/iosApp` module. Android, Web, and Backend checks are marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected by this feature)
  - iOS: ✅ Full iOS stack in `/iosApp` - domain models (Announcement), repositories (AnnouncementRepository protocol), ViewModels (LandingPageViewModel), coordinators (HomeCoordinator), and SwiftUI views (LandingPageView)
  - Web: N/A (not affected by this feature)
  - Backend: N/A (reuses existing GET /api/v1/announcements endpoint, no backend changes)
  - NO shared compiled code between platforms ✅
  - Violation justification: N/A

- [ ] **Android MVI Architecture**: N/A (Android platform not affected by this iOS-only feature)

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - ✅ HomeCoordinator (UIKit-based) manages navigation, creates UIHostingController for LandingPageView
  - ✅ LandingPageViewModel conforms to ObservableObject with @Published properties (announcements, isLoading, errorMessage, etc.)
  - ✅ ViewModel communicates with HomeCoordinator via closure (e.g., onAnnouncementTapped) to trigger cross-tab navigation
  - ✅ LandingPageView (SwiftUI) observes LandingPageViewModel, no business/navigation logic in view
  - ✅ Cross-tab navigation: HomeCoordinator calls TabBarCoordinator method to switch to Lost Pets tab and push detail screen
  - Violation justification: N/A

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A
  - iOS: ✅ Reuses existing AnnouncementRepository protocol in `/iosApp/iosApp/Domain/Repositories/` (no new repository needed)
  - Web: N/A
  - Backend: N/A
  - ✅ LandingPageViewModel references AnnouncementRepository protocol, not concrete implementation
  - Violation justification: N/A

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A
  - iOS: ✅ Manual DI via ServiceContainer (constructor injection) - LandingPageViewModel receives AnnouncementRepository and LocationService via constructor
  - Web: N/A
  - Backend: N/A
  - Violation justification: N/A

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A
  - iOS: ✅ Unit tests in `/iosApp/iosAppTests/ViewModels/LandingPageViewModelTests.swift`
    - Test coverage for: loadAnnouncements success/error, client-side filtering (limit 5), sorting (newest first), empty state, location permission handling
    - Target: 80% line + branch coverage
    - Run via XCTest in Xcode
  - Web: N/A
  - Backend: N/A
  - Violation justification: N/A

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A
  - Mobile: ✅ Appium tests in `/e2e-tests/java/` (Java 21 + Cucumber + Appium)
    - Feature file: `/e2e-tests/java/src/test/resources/features/mobile/landing-page.feature`
    - Screen Object: `/e2e-tests/java/src/test/java/.../screens/LandingPageScreen.java`
    - Step definitions: `/e2e-tests/java/src/test/java/.../steps/mobile/LandingPageSteps.java`
    - Coverage: User Story 1 (view announcements), User Story 2 (navigate to details), empty state, error handling
  - Violation justification: N/A

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A
  - iOS: ✅ Swift Concurrency (async/await) with @MainActor for ViewModel state updates
  - Web: N/A
  - Backend: N/A
  - No Combine, RxSwift, or callback-based patterns ✅
  - Violation justification: N/A

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A
  - iOS: ✅ `.accessibilityIdentifier()` modifier on:
    - Announcement cards: `landingPage.announcementCard.${announcementId}`
    - Loading indicator: `landingPage.loading`
    - Empty state view: `landingPage.emptyState`
    - Error message: `landingPage.error`
  - Web: N/A
  - Naming convention: `{screen}.{element}.{id/action}` ✅
  - Violation justification: N/A

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A
  - Swift: ✅ SwiftDoc format (`/// ...`) for:
    - LandingPageViewModel public methods (loadAnnouncements, handleAnnouncementTap)
    - HomeCoordinator public methods (showLandingPage, navigateToPetDetails)
    - Complex logic (client-side filtering/sorting) documented with rationale
  - TypeScript: N/A
  - Documentation concise (1-3 sentences: WHAT/WHY) ✅
  - Skip self-explanatory names ✅
  - Violation justification: N/A

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - ✅ Unit tests (Swift): `func testLoadAnnouncements_whenRepositoryReturnsData_shouldDisplayFirst5MostRecent() async { /* Given */ ... /* When */ ... /* Then */ ... }`
  - ✅ E2E tests (Cucumber): Gherkin scenarios with Given-When-Then structure
  - ✅ Test names follow Swift convention (camelCase_with_underscores)
  - ✅ Comments mark test phases in complex tests
  - Violation justification: N/A

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A (Backend not affected - reuses existing GET /api/v1/announcements endpoint)

- [ ] **Backend Code Quality**: N/A (Backend not affected)

- [ ] **Backend Dependency Management**: N/A (Backend not affected)

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: N/A (Web app not affected by this iOS-only feature)

- [ ] **Web Code Quality**: N/A (Web app not affected)

- [ ] **Web Dependency Management**: N/A (Web app not affected)

- [ ] **Web Business Logic Extraction**: N/A (Web app not affected)

- [ ] **Web TDD Workflow**: N/A (Web app not affected)

- [ ] **Web Testing Strategy**: N/A (Web app not affected)

- [ ] **Backend Directory Structure**: N/A (Backend not affected)

- [ ] **Backend TDD Workflow**: N/A (Backend not affected)

- [ ] **Backend Testing Strategy**: N/A (Backend not affected)

## Project Structure

### Documentation (this feature)

```text
specs/058-ios-landing-page-list/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── announcements-api.yaml  # Existing API contract (no changes)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (iOS App)

```text
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   ├── Announcement.swift           # Existing model (reused)
│   │   └── AnnouncementListQuery.swift  # NEW: Query configuration model
│   └── Repositories/
│       └── AnnouncementRepositoryProtocol.swift # Existing protocol (reused)
├── Data/
│   └── Repositories/
│       └── AnnouncementRepository.swift # Existing implementation (reused)
├── Views/
│   ├── AnnouncementCardsListViewModel.swift  # NEW: Autonomous list component ViewModel (reusable across features)
│   └── AnnouncementCardsListView.swift       # NEW: Autonomous list component (observes own ViewModel)
├── Features/
│   ├── LandingPage/
│   │   ├── Coordinators/
│   │   │   └── HomeCoordinator.swift            # NEW: Manages Home tab navigation (same pattern as AnnouncementListCoordinator)
│   │   └── Views/
│   │       ├── LandingPageViewModel.swift       # NEW: Parent ViewModel for landing page (thin wrapper)
│   │       └── LandingPageView.swift            # NEW: Landing page view (composes autonomous component)
│   └── AnnouncementList/
│       ├── Coordinators/
│       │   └── AnnouncementListCoordinator.swift # Existing coordinator (may be extended for cross-tab navigation)
│       └── Views/
│           ├── AnnouncementListViewModel.swift   # MODIFIED (optional): Refactor to use AnnouncementCardsListViewModel
│           ├── AnnouncementListView.swift        # MODIFIED (optional): Use autonomous component
│           ├── AnnouncementCardView.swift        # Existing component (reused)
│           └── AnnouncementCardViewModel.swift   # Existing component VM (reused)
├── Coordinators/
│   └── TabCoordinator.swift                 # MODIFIED: Replace PlaceholderCoordinator with HomeCoordinator for Home tab
└── DI/
    └── ServiceContainer.swift           # MODIFIED: Register ViewModels and Coordinators

iosApp/iosAppTests/
├── Views/
│   └── AnnouncementCardsListViewModelTests.swift  # NEW: Unit tests for autonomous component ViewModel
├── Features/
│   └── LandingPage/
│       ├── Views/
│       │   └── LandingPageViewModelTests.swift    # NEW: Unit tests for parent ViewModel
│       └── Coordinators/
│           └── HomeCoordinatorTests.swift         # NEW: Unit tests for HomeCoordinator

e2e-tests/java/
├── src/test/resources/features/mobile/
│   └── landing-page.feature             # NEW: Gherkin scenarios for landing page
├── src/test/java/.../screens/
│   └── LandingPageScreen.java           # NEW: Screen Object Model
└── src/test/java/.../steps/mobile/
    └── LandingPageSteps.java            # NEW: Step definitions
```

**Structure Decision**: iOS-only feature using MVVM-C architecture with **autonomous component pattern**. The core innovation is a three-layer ViewModel architecture:

**Three-Layer Architecture**:
1. **AnnouncementCardsListViewModel** (autonomous, reusable): Self-contained list logic with state (`cardViewModels`, `isLoading`, `errorMessage`) and behavior (`loadAnnouncements()`, `reload()`). Configured via `AnnouncementListQuery` (limit, sortBy).

2. **LandingPageViewModel / AnnouncementListViewModel** (parents, feature-specific): Thin wrappers that create and compose the autonomous list ViewModel with context-specific configuration:
   - `LandingPageViewModel`: Creates `AnnouncementCardsListViewModel` with `query: .landingPage` (limit: 5)
   - `AnnouncementListViewModel`: Creates `AnnouncementCardsListViewModel` with `query: .default` (limit: nil)

3. **AnnouncementCardsListView** (autonomous UI component): Observes `AnnouncementCardsListViewModel`, triggers `loadAnnouncements()` on appear, handles loading/error/empty states internally.

**Key Benefits**:
- ✅ **Autonomy**: List component manages its own state and lifecycle
- ✅ **Reusability**: Same component works in any context via query configuration
- ✅ **Separation**: Parents handle feature-specific logic (permissions, buttons, navigation), child handles list logic
- ✅ **Testability**: Autonomous ViewModel can be unit tested independently
- ✅ **Composition**: Parent ViewModels compose child ViewModel and trigger reload as needed

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations. All Constitution Check items pass without requiring justification.
