# Implementation Plan: Android Tab Navigation

**Branch**: `056-android-tab-navigation` | **Date**: December 15, 2025 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/056-android-tab-navigation/spec.md`

## Summary

Implement Material Design Bottom Navigation Bar for Android platform with 5 tabs (Home, Lost Pet, Found Pet, Contact Us, Account). This provides tab-based navigation infrastructure using Jetpack Navigation Component with a single NavHost containing nested navigation graphs (one per tab) for per-tab back stack preservation, configuration change survival, and instant tab switching. Each tab maintains its own navigation state. Unimplemented tab destinations show a shared "Coming soon" placeholder screen.

## Technical Context

**Language/Version**: Kotlin 2.2.20 with Android target  
**Primary Dependencies**:
- Jetpack Compose Material 3 (for Bottom Navigation Bar components)
- Jetpack Navigation Compose (androidx.navigation:navigation-compose 2.9.0 for single NavHost with nested graphs)
- Koin 3.5.3 (for dependency injection)
- Kotlin Coroutines 1.9.0 + Flow (for state management)
- kotlinx-serialization 1.8.0 (for type-safe navigation routes)

**Storage**: N/A (no data persistence - tab state is in-memory only)  
**Testing**: JUnit 6 + Kotlin Test + Turbine (for Flow testing)  
**Target Platform**: Android (minimum SDK 24, target SDK 34)  
**Project Type**: Mobile (Android) - source in `/composeApp/src/androidMain/`  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: 
- Navigation infrastructure only (no tab content implementation)
- No authentication integration (Account tab shows placeholder)
- No accessibility requirements beyond platform defaults
- Tab state not persisted across app restarts

**Scale/Scope**: 
- 5 tabs in bottom navigation
- Single shared placeholder composable
- Single NavHost with nested navigation graphs (one per tab)
- Configuration change handling (rotation, dark mode)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - iOS: Domain models, use cases, repositories, ViewModels in `/iosApp`
  - Web: Domain models, services, state management in `/webApp`
  - Backend: Independent Node.js/Express API in `/server`
  - NO shared compiled code between platforms
  - Violation justification: N/A - Android-only feature, no cross-platform concerns

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<TabNavigationUiState>` source of truth with immutable data class
  - Sealed `TabNavigationUserIntent` for tab selection and re-tap actions
  - Optional `TabNavigationUiEffect` for navigation events (sealed class)
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow`
  - Navigation MUST use Jetpack Navigation Component (androidx.navigation:navigation-compose)
  - Navigation graph defined with single `NavHost` containing nested `navigation()` graphs (one per tab)
  - ViewModels trigger navigation via `UiEffect`, not direct `NavController` calls
  - Composable screens follow two-layer pattern: state host (stateful) + stateless content composable
  - Stateless composables MUST have `@Preview` with `@PreviewParameter` using custom `PreviewParameterProvider<UiState>`
  - Callback lambdas MUST be defaulted to no-ops in stateless composables
  - Previews focus on light mode only (no dark mode previews required)
  - Violation justification: N/A - Full MVI compliance planned

- [ ] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - Violation justification: N/A - Android-only feature

- [ ] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Violation justification: N/A - No repositories needed (navigation infrastructure only, no data layer)

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/`
  - Module will provide: TabNavigationViewModel, NavHostControllers
  - Violation justification: N/A - Koin DI compliance planned

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - Coverage target: 80% line + branch coverage
  - Test scope: TabNavigationViewModel (state management, intent handling, navigation effects)
  - Test cases: Tab selection, re-tap behavior, back button handling, configuration changes
  - Violation justification: N/A - Full test coverage planned

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Java/Maven/Cucumber tests in `/e2e-tests/java/`
  - Feature file: `src/test/resources/features/mobile/056-tab-navigation.feature`
  - Screen objects: BottomNavigationScreen, PlaceholderScreen
  - Test tags: @android, @mobile
  - Coverage: All 6 acceptance scenarios from spec
  - Violation justification: N/A - E2E tests planned

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - StateFlow for UI state, SharedFlow for one-off effects
  - No blocking operations in UI thread
  - Violation justification: N/A - Coroutines + Flow compliance planned

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - Bottom navigation tabs: `bottomNav.homeTab`, `bottomNav.lostPetTab`, `bottomNav.foundPetTab`, `bottomNav.contactTab`, `bottomNav.accountTab`
  - Placeholder screen: `placeholder.comingSoonText`
  - Naming convention: `{screen}.{element}.{action}`
  - Violation justification: N/A - Test tags planned per FR-017

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format (`/** ... */`)
  - Documentation for ViewModel public methods (dispatchIntent, state exposure)
  - Documentation for composable screens and parameters
  - Skip documentation for self-explanatory code
  - Violation justification: N/A - Documentation planned for non-obvious APIs

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names in backticks: `fun \`should select home tab when user taps home tab\`()`
  - Comments mark test phases: `// given`, `// when`, `// then`
  - Violation justification: N/A - GWT compliance planned

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Violation justification: N/A - Backend not affected (Android-only feature)

- [ ] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - Violation justification: N/A - Backend not affected

- [ ] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Violation justification: N/A - Backend not affected

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - Violation justification: N/A - Web not affected (Android-only feature)

- [ ] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - Violation justification: N/A - Web not affected

- [ ] **Web Dependency Management**: Plan minimizes dependencies in `/webApp/package.json`
  - Violation justification: N/A - Web not affected

- [ ] **Web Business Logic Extraction**: Plan ensures business logic is extracted to testable functions
  - Violation justification: N/A - Web not affected

- [ ] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - Violation justification: N/A - Web not affected

- [ ] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - Violation justification: N/A - Web not affected

- [ ] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - Violation justification: N/A - Backend not affected

- [ ] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - Violation justification: N/A - Backend not affected

- [ ] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Violation justification: N/A - Backend not affected

## Project Structure

### Documentation (this feature)

```text
specs/056-android-tab-navigation/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output - Jetpack Navigation patterns, Material Design guidance
├── data-model.md        # Phase 1 output - TabNavigationUiState, UserIntent, UiEffect models
├── quickstart.md        # Phase 1 output - How to add new tabs and navigation destinations
├── contracts/           # Phase 1 output - Navigation routes contract
│   └── navigation-routes.md  # Tab navigation routes and NavHost configuration
├── checklists/
│   └── requirements.md  # Specification quality checklist (already exists)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/
├── kotlin/.../
│   ├── di/
│   │   └── NavigationModule.kt          # Koin module for ViewModel and NavControllers
│   ├── domain/
│   │   └── models/
│   │       └── TabDestination.kt        # Enum for 5 tab destinations
│   ├── presentation/
│   │   └── navigation/
│   │       ├── TabNavigationViewModel.kt     # MVI ViewModel (StateFlow + Intent + Effect)
│   │       ├── TabNavigationUiState.kt       # Immutable data class for selected tab, nav stacks
│   │       ├── TabNavigationUserIntent.kt    # Sealed class: SelectTab, ReTapActiveTab
│   │       └── TabNavigationUiEffect.kt      # Sealed class: PopToRoot
│   └── ui/
│       ├── navigation/
│       │   ├── MainScaffold.kt               # State host - wires ViewModel to MainScaffoldContent
│       │   ├── MainScaffoldContent.kt        # Stateless composable with bottom nav + single NavHost
│       │   ├── BottomNavigationBar.kt        # Material 3 NavigationBar composable
│       │   ├── TabRoutes.kt                  # Type-safe navigation route definitions
│       │   └── PlaceholderScreen.kt          # Shared "Coming soon" composable
│       └── preview/
│           └── TabNavigationPreviewProvider.kt  # PreviewParameterProvider for UiState variants

composeApp/src/androidUnitTest/
└── kotlin/.../
    └── presentation/
        └── navigation/
            └── TabNavigationViewModelTest.kt    # Unit tests (Given-When-Then, Turbine for Flow)
```

**Structure Decision**: Android mobile structure selected. Navigation feature is contained within `/composeApp/src/androidMain/` following MVI architecture. No backend or web changes required. **Single NavHost with nested navigation graphs** (one `navigation()` block per tab) provides per-tab back stack preservation using Navigation Component 2.9.0 native multi-back-stack support (`saveState`/`restoreState` flags). Type-safe navigation uses kotlinx-serialization with `@Serializable` route objects. Koin DI module provides ViewModel. NavController is managed by Compose (`rememberNavController()`).

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations - all constitution checks pass or are marked N/A for out-of-scope platforms.

