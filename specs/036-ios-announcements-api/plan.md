# Implementation Plan: iOS Announcements API Integration

**Branch**: `036-ios-announcements-api` | **Date**: 2025-12-01 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/036-ios-announcements-api/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Connect iOS Animal List and Pet Details screens to backend REST API endpoints (GET /api/v1/announcements, GET /api/v1/announcements/:id) to display real pet announcement data instead of mock data. iOS ViewModels will call the AnimalRepository (HTTP implementation) directly following MVVM-C architecture with manual dependency injection. URLSession with async/await for networking, with default timeout handling and optional location-based filtering.

## Technical Context

**Language/Version**: Swift 5.9+, iOS 15+  
**Primary Dependencies**: UIKit (coordinators), SwiftUI (views), Foundation (URLSession for HTTP)  
**Storage**: N/A (consuming REST API only, no local persistence in this feature)  
**Testing**: XCTest with async/await for ViewModels, fake repositories for testing  
**Target Platform**: iOS 15+ (iPhone/iPad)
**Project Type**: Mobile (iOS only)  
**Performance Goals**: Load animal list within 2 seconds, pet details within 1.5 seconds (normal network)  
**Constraints**: URLSession default timeout (~60s resource), HTTP allowed for localhost development  
**Scale/Scope**: iOS app displaying announcements from backend (no pagination client-side, backend responsible for limiting)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected)
  - iOS: YES - will implement HTTP-based AnimalRepository in `/iosApp/iosApp/Data/Repositories/`
  - Web: N/A (not affected)
  - Backend: N/A (backend endpoints already exist, no changes)
  - NO shared compiled code between platforms
  - Violation justification: _Compliant - iOS-only feature_

- [ ] **Android MVI Architecture**: N/A (Android not affected)

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation: YES - existing coordinators (AnimalListCoordinator, PetDetailsCoordinator) will remain unchanged
  - ViewModels conform to `ObservableObject` with `@Published` properties: YES - existing ViewModels (AnimalListViewModel, PetDetailsViewModel) already have `@Published` properties
  - ViewModels communicate with coordinators via methods or closures: YES - existing pattern preserved
  - SwiftUI views observe ViewModels (no business/navigation logic in views): YES - no changes to views needed
  - Violation justification: _Compliant - using existing MVVM-C structure_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A
  - iOS: YES - will use existing `AnimalRepositoryProtocol` (protocol suffix) from `/iosApp/iosApp/Domain/Repositories/`
  - Web: N/A
  - Backend: N/A
  - Implementations in platform-specific data/repositories modules: YES - `AnimalRepository` (no suffix) in `/iosApp/iosApp/Data/Repositories/`
  - ViewModels reference protocol, not concrete implementation: YES
  - Violation justification: _Compliant - following iOS protocol naming convention_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A
  - iOS: YES - will update existing ServiceContainer in `/iosApp/iosApp/DI/` to provide HTTP-based AnimalRepository implementation
  - Web: N/A
  - Backend: N/A
  - Violation justification: _Compliant - using manual DI via ServiceContainer_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A
  - iOS: YES - will write unit tests for HTTP-based AnimalRepository and update ViewModel tests to verify API integration in `/iosApp/iosAppTests/`
  - Web: N/A
  - Backend: N/A (backend tests already exist)
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: _Compliant - will achieve 80% coverage_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A
  - Mobile: YES - will add Appium tests in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts` covering all user stories (display announcements, location filtering, error handling)
  - All tests written in TypeScript: YES
  - Screen Object Model used: YES - will reuse existing screen objects
  - Each user story has at least one E2E test: YES
  - Violation justification: _Compliant - E2E tests planned for all user stories_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A
  - iOS: YES - Swift Concurrency (`async`/`await`) with `@MainActor` for ViewModels
  - Web: N/A
  - Backend: N/A
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code: YES
  - Violation justification: _Compliant - using Swift Concurrency only_

- [x] **Test Identifiers for UI Controls**: N/A - existing UI components already have accessibility identifiers
  - iOS: Existing views (AnimalListView, PetDetailsView) already have `accessibilityIdentifier` modifiers from previous features
  - No new UI elements added in this feature (only data source changes from mock to API)
  - Violation justification: _N/A - no UI changes_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A
  - Swift: YES - will add SwiftDoc to HTTP repository implementation methods when purpose is not clear from name
  - TypeScript: N/A
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW): YES
  - Document only when purpose is not clear from name alone: YES
  - Violation justification: _Compliant - will document non-obvious APIs_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then): YES
  - ViewModel tests use Given-When-Then pattern with descriptive names: YES - using Swift convention `testMethodName_whenCondition_shouldBehavior`
  - E2E tests structure scenarios with Given-When-Then phases: YES
  - Test names follow platform conventions: YES - Swift camelCase_with_underscores
  - Comments mark test phases in complex tests: YES
  - Violation justification: _Compliant - following Given-When-Then structure_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - `/server` not affected
  - Backend endpoints (GET /api/v1/announcements, GET /api/v1/announcements/:id) already exist
  - No backend code changes required
  - Violation justification: _N/A - iOS feature only, backend already implemented_

- [x] **Backend Code Quality**: N/A - `/server` not affected

- [x] **Backend Dependency Management**: N/A - `/server` not affected

- [x] **Backend Directory Structure**: N/A - `/server` not affected

- [x] **Backend TDD Workflow**: N/A - `/server` not affected

- [x] **Backend Testing Strategy**: N/A - `/server` not affected

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   └── Animal.swift (existing - no changes)
│   └── Repositories/
│       └── AnimalRepositoryProtocol.swift (existing - no changes)
├── Data/
│   └── Repositories/
│       └── AnimalRepository.swift (NEW - HTTP implementation)
├── DI/
│   └── ServiceContainer.swift (UPDATE - wire HTTP repository)
├── Features/
│   ├── AnimalList/
│   │   └── ViewModels/
│   │       └── AnimalListViewModel.swift (existing - no changes, already uses protocol)
│   └── PetDetails/
│       └── ViewModels/
│           └── PetDetailsViewModel.swift (existing - no changes, already uses protocol)

iosApp/iosAppTests/
├── Data/
│   └── Repositories/
│       └── AnimalRepositoryTests.swift (NEW - HTTP repository tests)
├── Features/
│   ├── AnimalList/
│   │   └── ViewModels/
│   │       └── AnimalListViewModelTests.swift (UPDATE - verify API integration)
│   └── PetDetails/
│       └── ViewModels/
│           └── PetDetailsViewModelTests.swift (UPDATE - verify API integration)

e2e-tests/mobile/
├── specs/
│   └── ios-announcements-api.spec.ts (NEW - E2E scenarios)
└── screens/
    └── (reuse existing screen objects)
```

**Structure Decision**: iOS mobile platform structure. This feature adds a new HTTP-based implementation of the existing `AnimalRepositoryProtocol` that consumes backend REST API. Existing ViewModels and views require no changes as they already depend on the protocol abstraction. ServiceContainer is updated to inject the HTTP implementation instead of mock/fake. Unit tests added for new repository and updated for ViewModels to verify API integration behavior. E2E tests added to verify end-to-end flows with real backend.

## Complexity Tracking

> **No violations identified** - All Constitution checks passed. Feature follows iOS MVVM-C architecture with manual DI, uses existing protocol abstractions, and maintains 80% test coverage requirement.
