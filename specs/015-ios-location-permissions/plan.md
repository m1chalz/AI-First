# Implementation Plan: iOS Location Permissions Handling

**Branch**: `015-ios-location-permissions` | **Date**: 2025-11-26 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/015-ios-location-permissions/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement location permission handling on iOS startup screen. App detects current permission status, requests permissions when needed, fetches location when authorized, and queries server for animal listings with or without location coordinates. System handles all permission states gracefully with custom UI for denied/restricted states and system alerts for initial requests.

## Technical Context

**Language/Version**: Swift 5.9+ (Xcode 15+)
**Primary Dependencies**: CoreLocation (iOS native framework for location services), SwiftUI
**Storage**: N/A (runtime state only, no persistence)
**Testing**: XCTest with Swift Concurrency (async/await)
**Target Platform**: iOS 15+
**Project Type**: mobile (iOS)
**Performance Goals**: N/A (low-frequency location requests on app startup)
**Constraints**: Must handle all CLAuthorizationStatus states gracefully, follow iOS privacy guidelines, provide clear user messaging for denied states
**Scale/Scope**: Single startup screen with location permission flow, affects existing AnimalListViewModel and StartupCoordinator

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only feature)
  - iOS: All code in `/iosApp` module (LocationService, ViewModels, Views)
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes, uses existing API)
  - NO shared compiled code between platforms
  - Violation justification: _Compliant - iOS-only feature_

- [x] **Android MVI Architecture**: N/A - iOS-only feature
  - Violation justification: _N/A - This feature affects only iOS platform_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators: Existing `StartupCoordinator` manages startup screen navigation
  - ViewModels: `AnimalListViewModel` (existing) extended with location permission logic
  - ViewModel communication: Uses `@Published` properties for permission status, location state
  - Coordinator communication: ViewModel → Coordinator via `onOpenAppSettings` closure for Settings navigation
  - SwiftUI views: `AnimalListView` (existing) extended with custom permission popup and scenePhase observation
  - Views remain pure: No business logic, no navigation logic (delegates to ViewModel → Coordinator)
  - Navigation responsibility: View → ViewModel.openSettings() → Coordinator.openAppSettings() → UIApplication
  - Violation justification: _Compliant - follows MVVM-C pattern with proper navigation delegation_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A (iOS-only feature)
  - iOS: New `LocationServiceProtocol` in `/iosApp/iosApp/Domain/` for location and permission handling
  - iOS: Implementation `LocationService` in `/iosApp/iosApp/Data/` using CoreLocation
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes)
  - ViewModels reference `LocationServiceProtocol`, not concrete `LocationService`
  - Violation justification: _Compliant - follows protocol-based design with "Protocol" suffix_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (iOS-only feature)
  - iOS: Manual DI via existing `ServiceContainer` in `/iosApp/iosApp/DI/`
    - Register `LocationServiceProtocol` → `LocationService` in ServiceContainer
    - Inject into `AnimalListViewModel` via constructor
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes)
  - Violation justification: _Compliant - uses manual DI with constructor injection_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (iOS-only feature)
  - iOS: Tests in `/iosApp/iosAppTests/Features/StartupScreen/`
    - `LocationServiceTests.swift` - test all permission states, location fetching, error handling
    - `AnimalListViewModelLocationTests.swift` - test ViewModel location integration
    - Run via XCTest: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
    - Coverage target: 80% line + branch coverage
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes)
  - Violation justification: _Compliant - unit tests cover all permission states and location scenarios_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A (iOS-only feature)
  - Mobile: Appium + Cucumber (Java) tests in `/e2e-tests/src/test/resources/features/mobile/ios-location-permissions.feature`
    - Screen Object Model in `/e2e-tests/src/test/java/.../screens/StartupScreen.java` (dual annotations for iOS/Android compatibility)
    - Step definitions in `/e2e-tests/src/test/java/.../steps-mobile/LocationPermissionSteps.java`
    - Scenarios cover: first-time request, granted permissions, denied permissions, Settings navigation
    - Run command: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"` (from repo root)
    - Report: `/e2e-tests/target/cucumber-reports/ios/index.html`
  - Each user story (P1-P4) has corresponding E2E test scenario (Gherkin format with @ios tag)
  - Violation justification: _Compliant - E2E tests cover all permission flows and user scenarios using Java/Maven/Cucumber per constitution v2.3.0_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A (iOS-only feature)
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` on ViewModel
    - `LocationService` methods are `async` (e.g., `func requestLocation() async -> UserLocation?`)
    - ViewModel methods use `async` (e.g., `func checkPermissionStatusChange() async`)
    - Permission status updates via polling on app foreground (`.onChange(of: scenePhase)`)
    - `@Published` properties in ViewModel for SwiftUI observation (ObservableObject, NOT Combine)
    - NO Combine, RxSwift, or callback-based patterns
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes)
  - Violation justification: _Compliant - uses Swift Concurrency exclusively_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A (iOS-only feature)
  - iOS: `accessibilityIdentifier` modifier on all interactive views
    - Custom permission popup: `startup.permissionPopup`
    - "Go to Settings" button: `startup.permissionPopup.goToSettings`
    - "Cancel" button: `startup.permissionPopup.cancel`
    - Permission popup title: `startup.permissionPopup.title`
    - Permission popup message: `startup.permissionPopup.message`
  - Web: N/A (iOS-only feature)
  - Naming convention: `startup.{element}` for startup screen elements
  - Violation justification: _Compliant - all interactive elements have accessibilityIdentifier_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Android: N/A (iOS-only feature)
  - iOS: SwiftDoc format (`/// ...`) for complex methods
    - `LocationServiceProtocol` methods documented (permission request flow, location fetching)
    - ViewModel methods documented when non-obvious (e.g., permission change handling)
    - Self-explanatory properties like `isLoading`, `errorMessage` not documented
    - Example: `/// Requests "When In Use" location permission from user. Displays system alert if not determined.`
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes)
  - Violation justification: _Compliant - SwiftDoc used for complex location/permission APIs_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Android: N/A (iOS-only feature)
  - iOS: Unit tests use Given-When-Then structure
    - Test names: `func testLoadAnimals_whenLocationPermissionGranted_shouldFetchLocationAndQueryWithCoordinates() async`
    - Comments mark phases: `// Given`, `// When`, `// Then`
    - Example: Given FakeLocationService with granted permission, When loadAnimals called, Then location fetched and used in query
  - E2E tests: Gherkin scenarios naturally follow Given-When-Then
    - `Given user hasn't been asked about location permissions`
    - `When startup screen appears`
    - `Then iOS system alert requesting location permission is displayed`
  - Violation justification: _Compliant - all tests use Given-When-Then structure_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - `/server` module not affected
  - Violation justification: _N/A - iOS-only feature, uses existing backend API_

- [x] **Backend Code Quality**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend code changes_

- [x] **Backend Dependency Management**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend dependencies added_

- [x] **Backend Directory Structure**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend structure changes_

- [x] **Backend TDD Workflow**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend tests required_

- [x] **Backend Testing Strategy**: N/A - `/server` module not affected
  - Violation justification: _N/A - Backend API already exists and tested_

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
iosApp/
├── iosApp/
│   ├── Domain/
│   │   └── LocationServiceProtocol.swift (NEW - protocol for location permissions and fetching)
│   ├── Data/
│   │   └── LocationService.swift (NEW - CoreLocation implementation)
│   ├── DI/
│   │   └── ServiceContainer.swift (MODIFIED - register LocationService)
│   ├── Features/
│   │   └── StartupScreen/
│   │       ├── AnimalListViewModel.swift (MODIFIED - add location permission handling)
│   │       └── AnimalListView.swift (MODIFIED - add custom permission popup + scenePhase)
│   ├── Coordinators/
│   │   └── StartupCoordinator.swift (MODIFIED - inject LocationService + Settings callback)
│   └── Info.plist (MODIFIED - add NSLocationWhenInUseUsageDescription key)
└── iosAppTests/
    └── Features/
        └── StartupScreen/
            ├── LocationServiceTests.swift (NEW - test permission handling)
            └── AnimalListViewModelLocationTests.swift (NEW - test ViewModel integration)

e2e-tests/
├── src/
│   └── test/
│       ├── resources/
│       │   └── features/
│       │       └── mobile/
│       │           └── ios-location-permissions.feature (NEW - Gherkin scenarios)
│       └── java/
│           └── .../
│               ├── screens/
│               │   └── StartupScreen.java (MODIFIED - add permission popup elements)
│               └── steps-mobile/
│                   └── LocationPermissionSteps.java (NEW - step definitions)
```

**Structure Decision**: iOS-only feature affecting `/iosApp` module. New protocol and service for location handling, modifications to existing AnimalListViewModel and AnimalListView. E2E tests added to existing mobile test suite with iOS-specific scenarios.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

None - all Constitution checks are compliant. Feature follows iOS MVVM-C architecture with protocol-based design, manual DI, and comprehensive testing.
