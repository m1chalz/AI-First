# Implementation Plan: iOS Landing Page - Top Panel

**Branch**: `060-ios-landing-page` | **Date**: 2025-12-17 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/060-ios-landing-page/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Add top panel UI above the existing Home list on iOS: hero section with "Find Your Pet" title and two primary action buttons ("Lost Pet" / "Found Pet"), plus a list header row with "Recent Reports" title and "View All" action. The implementation uses SwiftUI with MVVM-C architecture, leveraging existing tab navigation and list behavior without modifications. No backend changes required - pure iOS UI enhancement.

## Technical Context

**Language/Version**: Swift 5.0  
**Primary Dependencies**: SwiftUI (UI components), UIKit (Coordinators for navigation)  
**Storage**: N/A (pure UI feature, no local persistence)  
**Testing**: XCTest with Swift Concurrency (async/await)  
**Target Platform**: iOS 18.0+
**Project Type**: mobile (iOS)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: N/A (single screen UI enhancement)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only UI feature. Backend, Android, and Web-related checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - iOS: Domain models, use cases, repositories, ViewModels in `/iosApp`
  - Web: Domain models, services, state management in `/webApp`
  - Backend: Independent Node.js/Express API in `/server`
  - NO shared compiled code between platforms
  - **Status**: COMPLIANT - iOS-only UI feature, no cross-platform code sharing

- [x] **Android MVI Architecture**: N/A - iOS-only feature

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation and create `UIHostingController` instances
  - ViewModels conform to `ObservableObject` with `@Published` properties
  - ViewModels communicate with coordinators via methods or closures
  - SwiftUI views observe ViewModels (no business/navigation logic in views)
  - **Status**: COMPLIANT - Extending existing HomeCoordinator for tab navigation, using presentation models for new UI components

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: Repository interfaces in `/composeApp/src/androidMain/.../domain/repositories/`
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/`
  - Web: Service interfaces in `/webApp/src/services/`
  - Backend: Repository interfaces in `/server/src/database/repositories/`
  - Implementations in platform-specific data/repositories modules
  - Use cases reference interfaces, not concrete implementations
  - **Status**: N/A - Pure UI feature, no new repositories needed (reuses existing AnnouncementRepositoryProtocol)

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS: MUST use manual DI - setup in `/iosApp/iosApp/DI/` (ServiceContainer with constructor injection)
  - Web: SHOULD use React Context - setup in `/webApp/src/di/`
  - Backend: Manual DI in `/server/src/` (constructor injection, factory functions)
  - **Status**: COMPLIANT - iOS manual DI via ServiceContainer, dependencies injected via HomeCoordinator init

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/`, run via XCTest
  - Web: Tests in `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/`, run `npm test --coverage`
  - Backend: Tests in `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`, run `npm test --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - **Status**: COMPLIANT - iOS unit tests for presentation models and view logic in `/iosApp/iosAppTests/Features/LandingPage/`

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/[feature-name].spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
  - All tests written in TypeScript
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - **Status**: COMPLIANT - Java/Appium E2E tests in `/e2e-tests/java/` for iOS user stories (User Story 1: See top panel, User Story 2: Use top panel actions)

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
  - Backend: Native `async`/`await` (Express async handlers)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - **Status**: COMPLIANT - iOS Swift Concurrency with `@MainActor` (no async operations in this pure UI feature)

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - List items use stable IDs (e.g., `petList.item.${id}`)
  - **Status**: COMPLIANT - iOS accessibility identifiers per FR-010: `home.hero.title`, `home.hero.lostPetButton`, `home.hero.foundPetButton`, `home.recentReports.title`, `home.recentReports.viewAll`

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format (`/** ... */`)
  - Swift: SwiftDoc format (`/// ...`)
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone
  - Skip documentation for self-explanatory methods, variables, and constants
  - **Status**: COMPLIANT - Swift documentation for presentation models and coordinator methods (minimal, only when purpose not clear from name)

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (backticks for Kotlin, camelCase_with_underscores for Swift, descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - **Status**: COMPLIANT - iOS unit tests follow Given-When-Then with Swift naming convention (camelCase_with_underscores)

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - iOS-only UI feature, no backend changes

- [x] **Backend Code Quality**: N/A - iOS-only UI feature, no backend changes

- [x] **Backend Dependency Management**: N/A - iOS-only UI feature, no backend changes

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A - iOS-only UI feature, no web changes

- [x] **Web Code Quality**: N/A - iOS-only UI feature, no web changes

- [x] **Web Dependency Management**: N/A - iOS-only UI feature, no web changes

- [x] **Web Business Logic Extraction**: N/A - iOS-only UI feature, no web changes

- [x] **Web TDD Workflow**: N/A - iOS-only UI feature, no web changes

- [x] **Web Testing Strategy**: N/A - iOS-only UI feature, no web changes

- [x] **Backend Directory Structure**: N/A - iOS-only UI feature, no backend changes

- [x] **Backend TDD Workflow**: N/A - iOS-only UI feature, no backend changes

- [x] **Backend Testing Strategy**: N/A - iOS-only UI feature, no backend changes

## Project Structure

### Documentation (this feature)

```text
specs/060-ios-landing-page/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command) - N/A for UI-only feature
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
iosApp/iosApp/Features/LandingPage/
├── Coordinators/
│   └── HomeCoordinator.swift           # MODIFIED: Add tab navigation closure properties (onSwitchToLostPetTab, onSwitchToFoundPetTab)
├── Views/
│   ├── LandingPageView.swift           # MODIFIED: Add hero panel + list header row above AnnouncementCardsListView
│   ├── LandingPageViewModel.swift      # MODIFIED: Add tab navigation closure properties
│   └── Components/                     # NEW: Presentation models for new UI components
│       ├── HeroPanelView_Model.swift   # NEW: Presentation model for hero section (title + 2 buttons)
│       ├── HeroPanelView.swift         # NEW: SwiftUI view for hero section
│       ├── ListHeaderRowView_Model.swift # NEW: Presentation model for "Recent Reports / View All" row
│       └── ListHeaderRowView.swift     # NEW: SwiftUI view for list header row

iosApp/iosApp/Coordinators/
└── TabCoordinator.swift                # MODIFIED: Refactor showPetDetailsFromHome to switchToLostPetTab(withAnnouncementId:)
                                        #           Add switchToFoundPetTab() method
                                        #           Set tab navigation closures on HomeCoordinator during init

iosApp/iosAppTests/Features/LandingPage/
├── Views/
│   └── Components/                     # NEW: Unit tests for new components
│       ├── HeroPanelView_ModelTests.swift  # NEW: Tests for HeroPanelView_Model presentation logic
│       └── ListHeaderRowView_ModelTests.swift  # NEW: Tests for ListHeaderRowView_Model presentation logic

e2e-tests/java/
├── src/test/resources/features/mobile/
│   └── landing-page-top-panel.feature  # NEW: Gherkin scenarios for iOS top panel (User Story 1 & 2)
└── src/test/java/.../steps/mobile/
    └── LandingPageTopPanelSteps.java   # NEW: Step definitions for top panel E2E tests
```

**Structure Decision**: iOS mobile feature using existing MVVM-C architecture. Extends `LandingPageView` with new UI components (hero panel + list header row) above the existing `AnnouncementCardsListView`. Tab navigation handled by refactoring existing `TabCoordinator.showPetDetailsFromHome()` method to support optional detail parameter (`switchToLostPetTab(withAnnouncementId: String? = nil)`), reusing cross-tab navigation pattern for hero buttons. No backend, Android, or Web changes required.

## Complexity Tracking

No violations or complexity concerns - all Constitution Check items compliant or N/A (iOS-only UI feature).
