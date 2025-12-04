# Implementation Plan: iOS Microchip Number Screen

**Branch**: `019-ios-chip-number-screen` | **Date**: 2025-11-27 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/019-ios-chip-number-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

**Complete the implementation** of the Chip Number screen (Step 1/4) in the existing "Report Missing Pet" flow. The coordinator, flow state, and view/ViewModel skeleton already exist - we only need to add the input field UI, formatting logic, and state persistence. Users will be able to optionally enter a 15-digit microchip number with automatic formatting (00000-00000-00000). The Continue button is always enabled, and the coordinator already handles navigation and dismissal.

**Existing Infrastructure**:
- ✅ `ReportMissingPetCoordinator` - Complete navigation flow (4 steps + summary)
- ✅ `ReportMissingPetFlowState` - Complete state management (property: `chipNumber`)
- ✅ `ChipNumberViewModel` - Skeleton with navigation callbacks
- ✅ `ChipNumberView` - Placeholder with Continue button
- ✅ `ChipNumberViewModelTests` - Test file exists (needs expansion)

**What We Need to Implement**:
- TextField with numeric keyboard and formatting logic
- `MicrochipNumberFormatter` helper for reusable formatting
- Save/restore chip number to/from `flowState.chipNumber`
- Expand unit tests to cover formatting and state persistence

## Technical Context

**Language/Version**: Swift 5.9+ (iOS 15+ deployment target)  
**Primary Dependencies**: SwiftUI (UI layer), UIKit (coordinators, navigation), Foundation (data types), SwiftGen (localization - mandatory)  
**Storage**: In-memory flow state (ReportMissingPetFlowState class) - owned by coordinator, lifetime = flow duration  
**Testing**: XCTest with Swift Concurrency (async/await), coverage target 80%  
**Target Platform**: iOS 15+  
**Project Type**: Mobile (iOS - MVVM-C architecture)  
**Performance Goals**: Input formatting < 50ms latency per keystroke, screen transitions < 300ms  
**Constraints**: Max 15 digits for microchip number, no cursor jumping during formatting, back button must close entire flow (not just current screen)  
**Scale/Scope**: Single screen (1/4 of larger flow), ~200-300 LOC total (ViewModel + View + Coordinator)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - iOS: Domain models, use cases, repositories, ViewModels in `/iosApp`
  - Web: Domain models, services, state management in `/webApp`
  - Backend: Independent Node.js/Express API in `/server`
  - NO shared compiled code between platforms
  - Violation justification: N/A - iOS-only feature

- [x] **Android MVI Architecture**: N/A - iOS-only feature, Android not affected
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow`
  - Navigation MUST use Jetpack Navigation Component (androidx.navigation:navigation-compose)
  - Navigation graph defined with `NavHost` composable
  - ViewModels trigger navigation via `UiEffect`, not direct `NavController` calls
  - Composable screens follow two-layer pattern: state host (stateful) + stateless content composable
  - Stateless composables MUST have `@Preview` with `@PreviewParameter` using custom `PreviewParameterProvider<UiState>`
  - Callback lambdas MUST be defaulted to no-ops in stateless composables
  - Previews focus on light mode only (no dark mode previews required)
  - Violation justification: N/A - Android not affected

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation and create `UIHostingController` instances
  - ViewModels conform to `ObservableObject` with `@Published` properties
  - ViewModels communicate with coordinators via methods or closures
  - SwiftUI views observe ViewModels (no business/navigation logic in views)
  - Violation justification: N/A - fully compliant

- [x] **Interface-Based Design**: N/A - No repositories needed (UI-only screen, no data fetching)
  - Android: Repository interfaces in `/composeApp/src/androidMain/.../domain/repositories/`
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/`
  - Web: Service interfaces in `/webApp/src/services/`
  - Backend: Repository interfaces in `/server/src/database/repositories/`
  - Implementations in platform-specific data/repositories modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: N/A - no repositories/interfaces needed

- [x] **Dependency Injection**: Plan includes DI setup for iOS (manual DI with closures)
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS: MUST use manual DI - setup in `/iosApp/iosApp/DI/` (ServiceContainer with constructor injection)
  - Web: SHOULD use React Context - setup in `/webApp/src/di/`
  - Backend: Manual DI in `/server/src/` (constructor injection, factory functions)
  - Violation justification: N/A - using manual DI (coordinator closures, no ServiceContainer changes needed)

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for iOS ViewModel and flow state
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/`, run via XCTest
  - Web: Tests in `/webApp/src/__tests__/`, run `npm test -- --coverage`
  - Backend: Tests in `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: N/A - iOS unit tests in iosAppTests, target 80%

- [x] **End-to-End Tests**: Plan includes E2E tests for user stories using the unified Java + Maven + Cucumber stack
  - Web: Selenium WebDriver tests with Cucumber features under `/e2e-tests/java/src/test/resources/features/web/` (tagged `@web`)
  - Mobile: Appium tests with Cucumber features under `/e2e-tests/java/src/test/resources/features/mobile/` (tagged `@ios` for this flow)
  - All E2E tests written in Java with Cucumber (Gherkin scenarios)
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - Violation justification: N/A - E2E tests planned in tasks.md (E2E Phase)

- [x] **Asynchronous Programming Standards**: iOS uses Swift Concurrency (async/await with @MainActor)
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
  - Backend: Native `async`/`await` (Express async handlers)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: N/A - no async operations needed (synchronous UI state updates)

- [x] **Test Identifiers for UI Controls**: Plan includes accessibilityIdentifier for all interactive iOS elements
  - Android: `testTag` modifier on all interactive composables
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - List items use stable IDs (e.g., `petList.item.${id}`)
  - Violation justification: N/A - iOS accessibilityIdentifiers will be added to all interactive views

- [x] **Public API Documentation**: Plan ensures SwiftDoc format for complex APIs, skip self-explanatory methods
  - Kotlin: KDoc format (`/** ... */`)
  - Swift: SwiftDoc format (`/// ...`)
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone
  - Skip documentation for self-explanatory methods, variables, and constants
  - Violation justification: N/A - most methods self-explanatory, complex formatting logic will be documented

- [x] **Given-When-Then Test Structure**: Plan ensures XCTest tests use Given-When-Then with camelCase_with_underscores naming
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (backticks for Kotlin, camelCase_with_underscores for Swift, descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - Violation justification: _[Required if tests don't follow convention]_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - iOS-only UI feature, /server not affected
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration)
  - Violation justification: N/A - /server not affected

- [x] **Backend Code Quality**: N/A - /server not affected
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles applied:
    - Small, focused functions (single responsibility)
    - Descriptive naming (avoid unclear abbreviations)
    - Maximum 3 nesting levels
    - DRY principle (extract reusable logic)
    - JSDoc documentation for all public APIs
  - Violation justification: N/A - /server not affected

- [x] **Backend Dependency Management**: N/A - /server not affected
  - Only add dependencies providing significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regular `npm audit` security checks planned
  - Violation justification: N/A - /server not affected

- [x] **Backend Directory Structure**: N/A - /server not affected
  - `/middlewares/` - Express middlewares (auth, logging, error handling)
  - `/routes/` - REST API endpoint definitions (Express routers)
  - `/services/` - Business logic layer (testable, pure functions)
  - `/database/` - Database config, migrations, query repositories
  - `/lib/` - Utility functions, helpers (pure, reusable)
  - `/__test__/` - Integration tests for REST API endpoints
  - `app.ts` - Express app configuration
  - `index.ts` - Server entry point
  - Violation justification: N/A - /server not affected

- [x] **Backend TDD Workflow**: N/A - /server not affected
  - RED: Write failing test first
  - GREEN: Write minimal code to pass test
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code
  - Violation justification: N/A - /server not affected

- [x] **Backend Testing Strategy**: N/A - /server not affected
  - Unit tests (Vitest):
    - Location: `/src/services/__test__/`, `/src/lib/__test__/`
    - Coverage target: 80% line + branch coverage
    - Scope: Business logic and utility functions
  - Integration tests (Vitest + SuperTest):
    - Location: `/src/__test__/`
    - Coverage target: 80% for API endpoints
    - Scope: REST API end-to-end (request → response)
  - All tests follow Given-When-Then structure
  - Run commands: `npm test`, `npm test -- --coverage`
  - Violation justification: _[Required if coverage < 80% or N/A if /server not affected]_

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
├── Features/
│   └── ReportMissingPet/
│       ├── Coordinators/
│       │   └── ReportMissingPetCoordinator.swift  # ✅ EXISTING - Complete 4-step flow navigation
│       ├── Models/
│       │   └── ReportMissingPetFlowState.swift    # ✅ EXISTING - Shared flow state (class)
│       ├── Views/
│       │   ├── ChipNumberViewModel.swift          # ✅ EXISTING - NEEDS EXPANSION (add formatting logic)
│       │   └── ChipNumberView.swift               # ✅ EXISTING - NEEDS EXPANSION (add TextField UI)
│       └── Helpers/
│           └── MicrochipNumberFormatter.swift     # 🆕 NEW - Formatting utility (stateless)
├── Views/
│   └── NavigationBackHiding.swift                 # ✅ EXISTING - Wrapper to hide navigation back button
├── Resources/
│   ├── en.lproj/
│   │   └── Localizable.strings                    # ✅ EXISTING - May need new keys
│   └── pl.lproj/
│       └── Localizable.strings                    # ✅ EXISTING - May need new keys
├── Generated/
│   └── Strings.swift                              # ✅ GENERATED - SwiftGen output (auto-updated)
└── DI/
    └── ServiceContainer.swift                     # ✅ NO CHANGES - No repositories needed

iosAppTests/
└── Features/
    └── ReportMissingPet/
        ├── Views/
        │   └── ChipNumberViewModelTests.swift     # ✅ EXISTING - NEEDS EXPANSION (add formatting tests)
        └── Helpers/
            └── MicrochipNumberFormatterTests.swift # 🆕 NEW - Unit tests for formatter
    
e2e-tests/java/
├── pom.xml                                        # ✅ EXISTING - Maven project for web + mobile E2E
└── src/
    └── test/
        ├── java/
        │   └── .../screens/
        │       └── MicrochipNumberScreen.java     # ✅ EXISTING/PLANNED - Screen Object for microchip screen
        └── resources/
            └── features/
                └── mobile/
                    └── report-missing-pet.feature # ✅ EXISTING/PLANNED - Scenarios for report missing pet flow (tagged @ios)
```

**Structure Decision**: iOS mobile app structure following MVVM-C architecture. 

**Feature Status**: The `ReportMissingPet` flow infrastructure **already exists** with complete navigation scaffolding. This task completes the Chip Number screen (Step 1/4) implementation by adding:

- ✅ **EXISTING**: `ReportMissingPetCoordinator` - Complete 4-step navigation + modal presentation
- ✅ **EXISTING**: `ReportMissingPetFlowState` - Flow state with `chipNumber` property
- ✅ **EXISTING**: `ChipNumberViewModel` skeleton - has navigation callbacks, needs formatting logic
- ✅ **EXISTING**: `ChipNumberView` placeholder - has Continue button, needs TextField UI
- ✅ **EXISTING**: `ChipNumberViewModelTests` - test file exists, needs expansion
- 🆕 **NEW**: `MicrochipNumberFormatter` helper for formatting logic
- 🆕 **NEW**: `MicrochipNumberFormatterTests` for helper tests

**Key Insight**: We're not building from scratch - we're **completing an existing skeleton**. The coordinator already configures navigation bar, progress indicator (1/4), custom dismiss button, and handles the entire 4-step flow.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations - all constitution checks passed. This feature fully complies with iOS MVVM-C architecture requirements.
