# Implementation Plan: Android Animal List Screen Layout Update

**Branch**: `013-animal-list-screen` | **Date**: 2025-11-25 | **Spec**: `specs/013-animal-list-screen/spec.md`  
**Figma Design**: [Missing animals list app](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7556&m=dev)

## Summary

Update the Android `AnimalListScreen` Jetpack Compose UI to match the new Figma design:
- Rebrand screen title from "Missing animals list" to "PetSpot" (left-aligned, Hind 32px)
- Restructure animal cards to three-column layout (photo | info | status/date)
- Update card styling: 14dp radius, 1px border, no shadow
- Replace bottom bar with floating pill-shaped button
- Preserve all existing behaviour (loading, empty, error states)

## Technical Context

**Language/Version**: Kotlin (Android, version as defined in Gradle `libs.versions.toml`)  
**Primary Dependencies**: Jetpack Compose UI, AndroidX Navigation, Kotlin Coroutines + Flow, Koin (DI), JUnit + Kotlin Test for unit tests  
**Testing**: Android unit tests in `/composeApp/src/androidUnitTest/` plus mobile E2E tests in `/e2e-tests/mobile/`  
**Target Platform**: Android mobile app (`/composeApp`, Animal List feature under `features/animallist`)  
**Constraints**: No changes to networking, repositories, or ViewModel logic; iOS and Web must remain unaffected

## Files to Modify

| File | Changes |
|------|---------|
| `AnimalCard.kt` | Restructure layout, update styling, update test tag |
| `AnimalListContent.kt` | Replace TopAppBar with title, add floating button, update test tags |
| `res/font/` | Add Hind font files |
| `res/values/strings.xml` | Update string resources if needed |

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - iOS: Domain models, use cases, repositories, ViewModels in `/iosApp`
  - Web: Domain models, services, state management in `/webApp`
  - Backend: Independent Node.js/Express API in `/server`
  - Violation justification: Android-only UI change; iOS/Web remain untouched per spec.
  
- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages
  - Violation justification: `AnimalListScreen` already uses MVI; this plan only updates presentation.
  
- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - Violation justification: N/A - iOS is not modified.
  
- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Violation justification: No domain layer changes.
  
- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Violation justification: No new DI modules required.
  
- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Violation justification: This feature does not add new business logic; existing coverage maintained.
  
- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Violation justification: Visual refresh; E2E tests will use updated test tags.
  
- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Violation justification: No new async logic introduced.
  
- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `animalList.cardItem` for cards, `animalList.reportButton` for button
  
- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Violation justification: Existing composables; update KDoc if responsibilities change.
  
- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Violation justification: Any test updates will follow convention.

### Backend Architecture & Quality Standards

- [N/A] **Backend Technology Stack**: /server not affected
- [N/A] **Backend Code Quality**: /server not affected
- [N/A] **Backend Dependency Management**: /server not affected
- [N/A] **Backend Directory Structure**: /server not affected
- [N/A] **Backend TDD Workflow**: /server not affected
- [N/A] **Backend Testing Strategy**: /server not affected

## Project Structure

### Documentation (this feature)

```text
specs/013-animal-list-screen/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Design decisions
├── data-model.md        # Entity and layout details
├── quickstart.md        # Implementation guide
├── contracts/           # API contracts (N/A for this feature)
└── tasks.md             # Detailed implementation tasks
```

### Source Code (this feature)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/animallist/
│   ├── presentation/
│   │   ├── mvi/
│   │   │   ├── AnimalListUiState.kt      # (unchanged)
│   │   │   ├── AnimalListIntent.kt       # (unchanged)
│   │   │   └── AnimalListEffect.kt       # (unchanged)
│   │   └── viewmodels/
│   │       └── AnimalListViewModel.kt    # (unchanged)
│   └── ui/
│       ├── AnimalListScreen.kt           # (unchanged - state host)
│       ├── AnimalListContent.kt          # UPDATE: title, button, layout
│       └── AnimalCard.kt                 # UPDATE: card layout, styling
└── res/
    └── font/
        └── hind_regular.ttf              # ADD: Hind font
```

## Design Specifications

### Screen Layout

```
┌─────────────────────────────────────┐
│ PetSpot                             │  ← Left-aligned, Hind 32px
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 📷 │ 📍 Central Park • 2.5 km  │M│ │  ← Card row 1: Location
│ │    │ Dog • Golden Retriever    │I│ │  ← Card row 2: Species
│ │    │                           │S│ │  ← Status badge (right)
│ │    │                      18/11│S│ │  ← Date (right)
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ 📷 │ 📍 Central Park • 2.5 km  │F│ │
│ │    │ Cat • Siamese             │O│ │
│ │    │                           │U│ │
│ │    │                      17/11│N│ │
│ └─────────────────────────────────┘ │
│                                     │
│     ┌───────────────────────────┐   │
│     │ Report a Missing Animal 🐾│   │  ← Floating button
│     └───────────────────────────┘   │
└─────────────────────────────────────┘
```

### Card Layout Detail

```
┌─────────────────────────────────────────────────────────┐
│ Photo │        Info Section          │   Status Section │
│       │                              │                  │
│ ┌───┐ │ 📍 Location • Distance       │    ┌─────────┐  │
│ │   │ │ Species • Breed              │    │ MISSING │  │
│ │ 🐕 │ │                              │    └─────────┘  │
│ │   │ │                              │    18/11/2025   │
│ └───┘ │                              │                  │
│       │                              │                  │
│ 64px  │         Flexible             │      78px       │
└─────────────────────────────────────────────────────────┘
Height: 100px | Border: 1px #E5E9EC | Radius: 14dp
```

## Implementation Phases

### Phase 1: AnimalCard Restructure (US1)

1. Update card shape: 14dp radius, 1px border, remove shadow
2. Restructure to three-column Row layout
3. Add location icon before location text
4. Move status badge and date to right column
5. Update typography and colors per design
6. Update test tag to `animalList.cardItem`

### Phase 2: AnimalListContent Update (US1 + US2)

1. Remove `CenterAlignedTopAppBar`
2. Add "PetSpot" title with Hind font
3. Remove `ReportMissingBottomBar`
4. Add floating button composable
5. Update list padding to 23dp horizontal
6. Update button test tag to `animalList.reportButton`

### Phase 3: Font and Resources (US1)

1. Add Hind font to resources
2. Create FontFamily reference
3. Update string resources if needed

### Phase 4: State Preservation (US3)

1. Verify loading indicator styling
2. Verify empty state styling
3. Verify error state styling
4. Ensure button visibility matches current behavior

### Phase 5: Testing and Polish

1. Run unit tests: `./gradlew :composeApp:testDebugUnitTest`
2. Update E2E test selectors if needed
3. Visual review against Figma
4. Manual testing of all states

## Complexity Tracking

| Aspect | Complexity | Notes |
|--------|------------|-------|
| Card restructure | Medium | Three-column layout requires careful alignment |
| Floating button | Low | Standard Compose positioning |
| Font addition | Low | Standard Android font resource |
| State preservation | Low | No logic changes |
| Test tag updates | Low | Simple string changes |
