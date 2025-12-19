# Implementation Plan: iOS tab bar design update

**Branch**: `065-ios-tabbar-design` | **Date**: 2025-12-18 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/065-ios-tabbar-design/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Update the iOS bottom tab bar visual style to match the referenced Figma design. This is a visual-only change that preserves all existing navigation functionality. Custom icon assets will be exported from Figma and used as template images with tint colors applied via UITabBarAppearance. The update covers Light mode only (app forces Light mode). No changes to tab destinations, ordering, or labels are expected.

## Technical Context

**Language/Version**: Swift (iOS 18+ target)  
**Primary Dependencies**: UIKit (UITabBarController, UITabBarAppearance), SwiftUI (for wrapped views)  
**Storage**: N/A (visual update only)  
**Testing**: XCTest (unit tests for configuration logic if extracted), manual QA + design review  
**Target Platform**: iOS 18+ (iPhone 16 for testing)
**Project Type**: mobile (iOS)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: Light mode only (app forces Light mode), iPhone only, no functional changes to navigation  
**Scale/Scope**: Visual update to existing tab bar component (single file modification expected)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only visual update feature (tab bar styling). Most checks are N/A or compliant by design.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - iOS only: Updates `TabCoordinator.swift` in `/iosApp`
  - Android/Web/Backend: Not affected
  - NO shared compiled code between platforms
  - Violation justification: N/A - Compliant

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - N/A - iOS-only feature
  - Violation justification: N/A - Android not affected

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - This feature modifies `TabCoordinator` (existing coordinator)
  - No ViewModels needed (visual configuration only)
  - No SwiftUI views affected (UIKit tab bar)
  - Violation justification: N/A - Compliant (coordinator-level styling)

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - N/A - No domain logic, repositories, or use cases in this feature
  - Visual update only (UITabBarAppearance configuration)
  - Violation justification: N/A - No business logic

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - N/A - No new dependencies injected
  - Modifies existing TabCoordinator configuration method
  - Violation justification: N/A - No DI changes needed

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Existing TabCoordinatorTests will verify appearance configuration
  - Visual update requires minimal unit testing (color/appearance settings)
  - Primary validation: Manual QA + design review (per spec SC-001)
  - Coverage target: 80% maintained (no new business logic to test)
  - Violation justification: N/A - Visual updates primarily validated visually, not via unit tests

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - iOS E2E: Will update existing navigation tests to verify tab bar presence
  - Java/Cucumber tests in `/e2e-tests/java/` already test tab navigation
  - Visual validation performed in manual QA (design review)
  - No new E2E scenarios required (navigation behavior unchanged per FR-002)
  - Violation justification: N/A - Existing E2E tests cover navigation functionality

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - N/A - No async code in tab bar visual configuration
  - All changes are synchronous UITabBarAppearance setup
  - Violation justification: N/A - No async operations

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: Tab bar items ALREADY have accessibilityIdentifier (e.g., "tabs.home", "tabs.lostPet")
  - No changes to identifiers needed (visual update only)
  - Existing E2E tests use these identifiers
  - Violation justification: N/A - Compliant (identifiers already exist)

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - N/A - Modifying private method `configureTabBarAppearance()` in TabCoordinator
  - No public APIs added or changed
  - Existing documentation sufficient
  - Violation justification: N/A - No public API changes

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Existing TabCoordinatorTests already follow Given-When-Then
  - Any new tests will follow existing pattern
  - Violation justification: N/A - Compliant with existing test structure

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - N/A - iOS-only visual update, `/server` not affected
  - Violation justification: N/A

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - N/A - iOS-only visual update, `/server` not affected
  - Violation justification: N/A

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - N/A - iOS-only visual update, `/server` not affected
  - Violation justification: N/A

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - N/A - iOS-only visual update, `/webApp` not affected
  - Violation justification: N/A

- [x] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - N/A - iOS-only visual update, `/webApp` not affected
  - Violation justification: N/A

- [x] **Web Dependency Management**: Plan minimizes dependencies in `/webApp/package.json`
  - N/A - iOS-only visual update, `/webApp` not affected
  - Violation justification: N/A

- [x] **Web Business Logic Extraction**: Plan ensures business logic is extracted to testable functions
  - N/A - iOS-only visual update, `/webApp` not affected
  - Violation justification: N/A

- [x] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - N/A - iOS-only visual update, `/webApp` not affected
  - Violation justification: N/A

- [x] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - N/A - iOS-only visual update, `/webApp` not affected
  - Violation justification: N/A

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - N/A - iOS-only visual update, `/server` not affected
  - Violation justification: N/A

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - N/A - iOS-only visual update, `/server` not affected
  - Violation justification: N/A

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - N/A - iOS-only visual update, `/server` not affected
  - Violation justification: N/A

## Project Structure

### Documentation (this feature)

```text
specs/065-ios-tabbar-design/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (N/A - no data models)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (N/A - no API contracts)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
iosApp/
├── iosApp/
│   ├── Coordinators/
│   │   └── TabCoordinator.swift         # MODIFIED: configureTabBarAppearance(), configureTabBarItem()
│   ├── Assets.xcassets/
│   │   └── TabBar/                      # NEW: Custom tab bar icon assets (5 icons)
│   │       ├── home.imageset/           # NEW: Home tab icon
│   │       ├── lostPet.imageset/        # NEW: Lost Pet tab icon
│   │       ├── foundPet.imageset/       # NEW: Found Pet tab icon
│   │       ├── contactUs.imageset/      # NEW: Contact Us tab icon
│   │       └── account.imageset/        # NEW: Account tab icon
│   └── SceneDelegate.swift              # NO CHANGES (already configures tab bar)
└── iosAppTests/
    └── Coordinators/
        └── TabCoordinatorTests.swift    # MODIFIED: Verify new appearance properties
```

**Structure Decision**: iOS-only visual update. All changes confined to `/iosApp` module:
- Update `TabCoordinator.swift` to apply new design colors and custom icons
- Add custom icon assets to `Assets.xcassets/TabBar/` (5 image sets exported from Figma)
- Update unit tests to verify appearance configuration (optional - visual validation primary)

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations - Constitution Check passed. This is a straightforward visual update following iOS MVVM-C architecture.
