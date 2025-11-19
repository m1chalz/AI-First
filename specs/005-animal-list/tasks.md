# Tasks: Animal List Screen

**Input**: Design documents from `/specs/005-animal-list/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/AnimalRepository.kt.md

**Tests**: Test requirements for this KMP project:

**MANDATORY - Shared Module Unit Tests**:
- Location: `/shared/src/commonTest`
- Coverage: 80% line + branch coverage
- Framework: Kotlin Test
- Scope: Domain models, use cases, business logic
- Convention: MUST follow Given-When-Then structure

**MANDATORY - ViewModel Unit Tests** (per platform):
- Android: `/composeApp/src/androidUnitTest/` (JUnit 6 + Turbine), 80% coverage
- iOS: `/iosApp/iosAppTests/ViewModels/` (XCTest), 80% coverage  
- Web: `/webApp/src/__tests__/hooks/` (Vitest + RTL), 80% coverage
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests**:
- Web: `/e2e-tests/web/specs/animal-list.spec.ts` (Playwright + TypeScript)
- Mobile: `/e2e-tests/mobile/specs/animal-list.spec.ts` (Appium + TypeScript)
- All user stories MUST have E2E test coverage
- Use Page Object Model / Screen Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story?] Description with file path`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create domain models directory structure in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/`
- [ ] T002 Create repositories directory structure in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/`
- [ ] T003 Create use cases directory structure in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/usecases/`
- [ ] T004 Create test directory structure in `/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/`
- [ ] T005 [P] Create Android feature directory structure in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/`
- [ ] T006 [P] Create Android data directory structure in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/`
- [ ] T007 [P] Create iOS ViewModels directory if not exists in `/iosApp/iosApp/ViewModels/`
- [ ] T008 [P] Create iOS Repositories directory if not exists in `/iosApp/iosApp/Repositories/`
- [ ] T009 [P] Create Web components directory in `/webApp/src/components/AnimalList/`
- [ ] T010 [P] Create Web hooks directory if not exists in `/webApp/src/hooks/`
- [ ] T011 [P] Create Web services directory if not exists in `/webApp/src/services/`
- [ ] T011a [P] Configure JUnit 6 in `/composeApp/build.gradle.kts` - add testImplementation("org.junit.jupiter:junit-jupiter:5.10.1") and useJUnitPlatform() in test block

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T012 Create Location model in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Location.kt` with @JsExport
- [ ] T013 [P] Create AnimalSpecies enum in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/AnimalSpecies.kt` with @JsExport
- [ ] T014 [P] Create AnimalGender enum in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/AnimalGender.kt` with @JsExport
- [ ] T015 [P] Create AnimalStatus enum in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/AnimalStatus.kt` with @JsExport and badge colors
- [ ] T016 Create Animal model in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt` with all properties and @JsExport
- [ ] T016a Create MockAnimalData helper in `/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/fixtures/MockAnimalData.kt` with fun generateMockAnimals(count: Int = 16): List<Animal> - single source of truth for test data across all platforms
- [ ] T017 Create AnimalRepository interface in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/AnimalRepository.kt` with suspend getAnimals() method
- [ ] T018 Create GetAnimalsUseCase in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/usecases/GetAnimalsUseCase.kt` with KDoc
- [ ] T019 Update DomainModule in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt` to register GetAnimalsUseCase
- [ ] T020 Build shared module to verify compilation with `./gradlew :shared:build`
- [ ] T021 [P] Create E2E web page object base in `/e2e-tests/web/pages/AnimalListPage.ts`
- [ ] T022 [P] Create E2E mobile screen object base in `/e2e-tests/mobile/screens/AnimalListScreen.ts`
- [ ] T023 [P] Create E2E web steps helpers in `/e2e-tests/web/steps/animalListSteps.ts`
- [ ] T024 [P] Create E2E mobile steps helpers in `/e2e-tests/mobile/steps/animalListSteps.ts`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Animal List (Priority: P1) 🎯 MVP

**Goal**: Display a scrollable list of animals (16 mock items fixed for MVP) with proper loading, error, and empty states

**Independent Test**: Open the application and verify that a scrollable list of animals is displayed with mock data. List should scroll smoothly and show all animal cards with proper visual design.

### Tests for User Story 1 (Write FIRST, ensure FAIL) ✅

**Test Convention Reminder**: All tests MUST follow Given-When-Then structure per constitution XII with descriptive names and clear phase separation (Given/When/Then comments in complex tests).

**Shared Module Unit Tests**:
- [ ] T025 [P] [US1] Create FakeAnimalRepository **implementing AnimalRepository interface** in `/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/repositories/FakeAnimalRepository.kt` for testing - use MockAnimalData.generateMockAnimals() from T016a
- [ ] T026 [P] [US1] Unit test GetAnimalsUseCase success scenario in `/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/usecases/GetAnimalsUseCaseTest.kt` using FakeAnimalRepository
- [ ] T027 [P] [US1] Unit test GetAnimalsUseCase failure scenario in GetAnimalsUseCaseTest.kt (repository throws exception)
- [ ] T028 [P] [US1] Unit test GetAnimalsUseCase empty list scenario in GetAnimalsUseCaseTest.kt (repository returns empty list)

**Android ViewModel Unit Tests**:
- [ ] T029 [P] [US1] Unit test AnimalListReducer success case in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/mvi/AnimalListReducerTest.kt`
- [ ] T030 [P] [US1] Unit test AnimalListReducer failure case in AnimalListReducerTest.kt (error state)
- [ ] T031 [P] [US1] Unit test AnimalListReducer empty case in AnimalListReducerTest.kt (isEmpty = true)
- [ ] T032 [P] [US1] Unit test AnimalListViewModel Refresh intent in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/viewmodels/AnimalListViewModelTest.kt` using Turbine
- [ ] T033 [P] [US1] Unit test AnimalListViewModel state transitions (loading → success) in AnimalListViewModelTest.kt with Turbine
- [ ] T034 [P] [US1] Unit test AnimalListViewModel SelectAnimal intent emits NavigateToDetails effect in AnimalListViewModelTest.kt

**iOS ViewModel Unit Tests**:
- [ ] T035 [P] [US1] Unit test AnimalListViewModel loadAnimals success in `/iosApp/iosAppTests/ViewModels/AnimalListViewModelTests.swift` (verify @Published animals updated)
- [ ] T036 [P] [US1] Unit test AnimalListViewModel loadAnimals failure in AnimalListViewModelTests.swift (verify errorMessage set)
- [ ] T037 [P] [US1] Unit test AnimalListViewModel isEmpty computed property in AnimalListViewModelTests.swift
- [ ] T038 [P] [US1] Unit test AnimalListViewModel selectAnimal callback invocation in AnimalListViewModelTests.swift

**Web Hook Unit Tests**:
- [ ] T039 [P] [US1] Unit test useAnimalList hook initial state in `/webApp/src/__tests__/hooks/useAnimalList.test.ts` using renderHook
- [ ] T040 [P] [US1] Unit test useAnimalList hook loadAnimals success in useAnimalList.test.ts (verify animals state updated)
- [ ] T041 [P] [US1] Unit test useAnimalList hook loadAnimals failure in useAnimalList.test.ts (verify error state)
- [ ] T042 [P] [US1] Unit test useAnimalList hook isEmpty derived state in useAnimalList.test.ts

**End-to-End Tests**:
- [ ] T043 [P] [US1] Implement AnimalListPage Page Object with locators in `/e2e-tests/web/pages/AnimalListPage.ts`
- [ ] T044 [P] [US1] Implement AnimalListScreen Screen Object with selectors in `/e2e-tests/mobile/screens/AnimalListScreen.ts`
- [ ] T045 [P] [US1] E2E test web: verify list displays animals in `/e2e-tests/web/specs/animal-list.spec.ts` (Given app loaded, When list renders, Then 16 animals visible)
- [ ] T046 [P] [US1] E2E test web: verify list is scrollable in animal-list.spec.ts (When user scrolls, Then all items accessible)
- [ ] T047 [P] [US1] E2E test mobile Android: verify list displays animals in `/e2e-tests/mobile/specs/animal-list.spec.ts`
- [ ] T048 [P] [US1] E2E test mobile Android: verify list scrollable in animal-list.spec.ts

### Implementation for User Story 1

**Shared Module**:
- [ ] T049 [US1] Verify all domain models have KDoc documentation (Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus) - all public classes per constitution XI
- [ ] T050 [US1] Verify AnimalRepository interface has KDoc documentation - all public methods per constitution XI
- [ ] T051 [US1] Verify GetAnimalsUseCase has KDoc documentation - all public classes, methods, properties per constitution XI

**Android Implementation**:
- [ ] T052 [US1] Create AnimalRepositoryImpl with 16 mock animals in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/AnimalRepositoryImpl.kt` (500ms delay) - use same mock data structure as MockAnimalData.generateMockAnimals() from T016a for consistency
- [ ] T053 [US1] Update DataModule in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DataModule.kt` to register AnimalRepositoryImpl
- [ ] T054 [US1] Create AnimalListUiState data class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/mvi/AnimalListUiState.kt` with isEmpty computed property
- [ ] T055 [P] [US1] Create AnimalListIntent sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/mvi/AnimalListIntent.kt` (Refresh, SelectAnimal, ReportMissing, ReportFound - UI uses only ReportMissing in US1)
- [ ] T056 [P] [US1] Create AnimalListEffect sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/mvi/AnimalListEffect.kt` (NavigateToDetails, NavigateToReportMissing, NavigateToReportFound - UI emits only NavigateToReportMissing in US1)
- [ ] T057 [US1] Create AnimalListReducer object with pure reduce function in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/mvi/AnimalListReducer.kt`
- [ ] T058 [US1] Create AnimalListViewModel with StateFlow and SharedFlow in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/viewmodels/AnimalListViewModel.kt`
- [ ] T058a [US1] Implement Refresh intent handler in AnimalListViewModel calling GetAnimalsUseCase and updating state via reducer (loading → success/error states)
- [ ] T058b [US1] Implement SelectAnimal intent handler in AnimalListViewModel emitting NavigateToDetails effect via SharedFlow
- [ ] T059 [US1] Update ViewModelModule in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt` to register AnimalListViewModel
- [ ] T060 [US1] Create AnimalCard composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt` with testTag "animalList.item.${animal.id}" and onClick callback parameter
- [ ] T061 [US1] Create EmptyState composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/EmptyState.kt` with empty state message
- [ ] T061a [US1] Verify EmptyState message matches spec FR-009 exactly: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- [ ] T062 [US1] Create AnimalListScreen composable with LazyColumn testTag "animalList.list" and wire AnimalCard onClick to dispatch SelectAnimal(id) intent (FR-011)
- [ ] T063 [US1] Add "Report a Missing Animal" button to AnimalListScreen with testTag "animalList.reportMissingButton" (fixed at bottom, single button only per mobile design)
- [ ] T064 [US1] Add collectAsStateWithLifecycle for state in AnimalListScreen
- [ ] T065 [US1] Add LaunchedEffect for effects collection in AnimalListScreen (navigation effects)
- [ ] T066 [US1] Add reserved space for search component at top of AnimalListScreen
- [ ] T067 [US1] Verify KDoc documentation on newly created Android classes: AnimalRepositoryImpl, all MVI artifacts (UiState, Intent, Effect, Reducer), and AnimalListViewModel - all public classes, methods, properties per constitution XI (first-pass verification during development)
- [ ] T068 [US1] Build Android app and verify UI with `./gradlew :composeApp:assembleDebug`
- [ ] T068a [US1] Set AnimalListScreen as start destination in Android MainActivity or Navigation graph (verify FR-010: app launches directly to animal list screen)

**iOS Implementation**:
- [ ] T069 [US1] Create AnimalRepositoryImpl.swift with 16 mock animals in `/iosApp/iosApp/Repositories/AnimalRepositoryImpl.swift` (0.5s delay) - use same mock data structure as MockAnimalData.generateMockAnimals() from T016a for consistency
- [ ] T070 [US1] Create AnimalListViewModel.swift with @Published properties in `/iosApp/iosApp/ViewModels/AnimalListViewModel.swift` (@MainActor, ObservableObject)
- [ ] T071 [US1] Add loadAnimals async method to AnimalListViewModel.swift
- [ ] T071a [US1] Implement loadAnimals() async method in AnimalListViewModel.swift calling GetAnimalsUseCase and updating @Published animals, isLoading, and errorMessage properties
- [ ] T071b [US1] Implement selectAnimal(id:) method in AnimalListViewModel.swift that calls onAnimalSelected?(id) coordinator closure
- [ ] T072 [US1] Add coordinator closures (onAnimalSelected, onReportMissing, onReportFound) to AnimalListViewModel.swift (UI calls only onReportMissing in US1)
- [ ] T073 [US1] Create AnimalCardView.swift in `/iosApp/iosApp/Views/AnimalCardView.swift` with accessibilityIdentifier "animalList.item.\(animal.id)" and onTap callback closure
- [ ] T074 [US1] Create EmptyStateView.swift in `/iosApp/iosApp/Views/EmptyStateView.swift` with empty state message
- [ ] T074a [US1] Verify EmptyStateView message matches spec FR-009 exactly: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- [ ] T075 [US1] Create AnimalListView.swift with LazyVStack accessibilityIdentifier "animalList.list" and wire AnimalCardView onTapGesture to call viewModel.selectAnimal(id:) (FR-011)
- [ ] T076 [US1] Add "Report a Missing Animal" button to AnimalListView.swift with accessibilityIdentifier "animalList.reportMissingButton" (fixed at bottom, single button only per mobile design)
- [ ] T077 [US1] Add reserved space for search component at top of AnimalListView.swift
- [ ] T078 [US1] Create AnimalListCoordinator.swift in `/iosApp/iosApp/Coordinators/AnimalListCoordinator.swift` with start() method
- [ ] T079 [US1] Add showAnimalDetails, showReportMissing, showReportFound methods to AnimalListCoordinator.swift (mocked with print for now; UI calls only showReportMissing in US1)
- [ ] T080 [US1] Update AppCoordinator.swift in `/iosApp/iosApp/Coordinators/AppCoordinator.swift` to set AnimalListCoordinator as root (verify FR-010: app launches directly to animal list screen)
- [ ] T081 [US1] Verify SwiftDoc documentation on newly created iOS classes: AnimalRepositoryImpl, AnimalListViewModel, and AnimalListCoordinator - all public classes, methods, properties per constitution XI (first-pass verification during development)
- [ ] T082 [US1] Build iOS app and verify UI with `xcodebuild -scheme iosApp build`

**Web Implementation**:
- [ ] T083 [US1] Create AnimalRepositoryImpl service in `/webApp/src/services/animalRepository.ts` with 16 mock animals (500ms delay) - use same mock data structure as MockAnimalData.generateMockAnimals() from T016a for consistency
- [ ] T084 [US1] Create useAnimalList hook in `/webApp/src/hooks/useAnimalList.ts` with state management (animals, isLoading, error, isEmpty)
- [ ] T084a [US1] Implement loadAnimals() method in useAnimalList.ts calling GetAnimalsUseCase and updating state (animals, isLoading, error)
- [ ] T084b [US1] Implement selectAnimal(id) method in useAnimalList.ts triggering navigation via React Router navigate(`/animal/${id}`)
- [ ] T085 [US1] Add loadAnimals, selectAnimal, reportMissing, reportFound methods to useAnimalList.ts (UI calls only reportMissing in US1)
- [ ] T086 [US1] Create AnimalCard component in `/webApp/src/components/AnimalList/AnimalCard.tsx` with data-testid "animalList.item.${animal.id}" and onClick prop
- [ ] T087 [US1] Create EmptyState component in `/webApp/src/components/AnimalList/EmptyState.tsx` with empty state message
- [ ] T087a [US1] Verify EmptyState component message matches spec FR-009 exactly: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- [ ] T088 [US1] Create AnimalList component with list rendering data-testid "animalList.list" and wire AnimalCard onClick to call selectAnimal(id) from hook (FR-011)
- [ ] T089 [US1] Add "Report a Missing Animal" button to AnimalList.tsx with data-testid "animalList.reportMissingButton" (at top-right per web design, secondary button added in US2)
- [ ] T090 [US1] Add reserved space for search component at top of AnimalList.tsx
- [ ] T091 [US1] Create AnimalList.module.css in `/webApp/src/components/AnimalList/AnimalList.module.css` with Figma design system styles
- [ ] T092 [US1] Verify JSDoc documentation on newly created Web code: animalRepository service, useAnimalList hook, and AnimalList component - all public functions, classes, methods per constitution XI (first-pass verification during development)
- [ ] T093 [US1] Build web app and verify UI with `npm run build && npm run start`
- [ ] T093a [US1] Set AnimalList as default route (/) in App.tsx or main routing configuration (verify FR-010: app launches directly to animal list screen)

**Verification for User Story 1**:
- [ ] T094 [US1] Run shared module tests with coverage: `./gradlew :shared:test koverHtmlReport` (verify 80%+ coverage)
- [ ] T095 [US1] Run Android ViewModel tests with coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` (verify 80%+ coverage)
- [ ] T096 [US1] Run iOS ViewModel tests with coverage: `xcodebuild test -scheme iosApp -enableCodeCoverage YES` (verify 80%+ coverage)
- [ ] T097 [US1] Run web hook tests with coverage: `npm test -- --coverage` (verify 80%+ coverage in webApp/)
- [ ] T098 [US1] Run web E2E tests: `npx playwright test e2e-tests/web/specs/animal-list.spec.ts`
- [ ] T099 [US1] Run mobile Android E2E tests: `npm run test:mobile:android`
- [ ] T100 [US1] Manual UI verification: Visual design matches Figma 100% (colors, typography, spacing, shadows)
- [ ] T101 [US1] Manual test: Verify list scrolls smoothly with 16 animals
- [ ] T102 [US1] Manual test: Verify empty state displays when mock data is cleared (temporarily modify AnimalRepositoryImpl to return emptyList() for this test, then revert)

**Checkpoint**: User Story 1 complete - List displays animals, scrolls smoothly, handles empty state, all tests pass with 80%+ coverage

---

## Phase 4: User Story 2 - "Report a Missing Animal" Button Action (Priority: P2)

**Goal**: "Report a Missing Animal" button is prominently displayed, always accessible (fixed at bottom), and triggers expected action when tapped

**Independent Test**: Verify that the "Report a Missing Animal" button is visible at all times (even when scrolling) and tapping it triggers the expected mocked action (console log / placeholder navigation).

### Tests for User Story 2 (Write FIRST, ensure FAIL) ✅

**Android ViewModel Unit Tests**:
- [ ] T103 [P] [US2] Unit test AnimalListViewModel ReportMissing intent emits NavigateToReportMissing effect in AnimalListViewModelTest.kt
- [ ] T104 [P] [US2] Unit test AnimalListViewModel ReportFound intent emits NavigateToReportFound effect in AnimalListViewModelTest.kt - **MVI completeness test (Android UI doesn't expose this button)**

**iOS ViewModel Unit Tests**:
- [ ] T105 [P] [US2] Unit test AnimalListViewModel reportMissing method calls onReportMissing closure in AnimalListViewModelTests.swift
- [ ] T106 [P] [US2] Unit test AnimalListViewModel reportFound method calls onReportFound closure in AnimalListViewModelTests.swift - **Completeness test (iOS UI doesn't expose this button)**

**Web Hook Unit Tests**:
- [ ] T107 [P] [US2] Unit test useAnimalList hook reportMissing method triggers navigation in useAnimalList.test.ts
- [ ] T108 [P] [US2] Unit test useAnimalList hook reportFound method triggers navigation in useAnimalList.test.ts - **Web uses both buttons per Figma 71:9154**

**End-to-End Tests**:
- [ ] T109 [P] [US2] E2E test web: verify "Report a Missing Animal" button visible when page loads in animal-list.spec.ts
- [ ] T110 [P] [US2] E2E test web: verify "Report a Missing Animal" button remains visible when scrolling list in animal-list.spec.ts
- [ ] T111 [P] [US2] E2E test web: verify clicking "Report a Missing Animal" button triggers action in animal-list.spec.ts
- [ ] T112 [P] [US2] E2E test mobile: verify "Report a Missing Animal" button visible and tappable in animal-list.spec.ts
- [ ] T113 [P] [US2] E2E test mobile: verify "Report a Missing Animal" button remains visible when scrolling in animal-list.spec.ts

### Implementation for User Story 2

**Android Implementation (Mobile - Single Button)**:
- [ ] T114 [US2] Verify ReportMissing and ReportFound intents exist in AnimalListIntent.kt (already added in US1 for MVI completeness)
- [ ] T115 [US2] Verify NavigateToReportMissing and NavigateToReportFound effects exist in AnimalListEffect.kt (already added in US1 for MVI completeness)
- [ ] T116 [US2] Implement ReportMissing intent handling in AnimalListViewModel (dispatch intent → emit effect)
- [ ] T117 [US2] Implement ReportFound intent handling in AnimalListViewModel (dispatch intent → emit effect) - **NOTE: Android mobile UI does NOT use this, but included for MVI completeness**
- [ ] T118 [US2] Verify "Report a Missing Animal" button in AnimalListScreen has correct testTag "animalList.reportMissingButton" (already added in US1)
- [ ] T119 [US2] Verify button remains fixed at bottom in AnimalListScreen (outside LazyColumn scroll area) - **Mobile has single button only per Figma 48:6096**
- [ ] T120 [US2] Add onClick handler for button to dispatch ReportMissing intent in AnimalListScreen
- [ ] T121 [US2] Verify LaunchedEffect in AnimalListScreen handles NavigateToReportMissing effect (log for now)

**iOS Implementation (Mobile - Single Button)**:
- [ ] T122 [US2] Implement reportMissing() method in AnimalListViewModel.swift (calls onReportMissing? closure)
- [ ] T123 [US2] Implement reportFound() method in AnimalListViewModel.swift (calls onReportFound? closure) - **NOTE: iOS mobile UI does NOT use this, but included for completeness**
- [ ] T124 [US2] Verify "Report a Missing Animal" button in AnimalListView has accessibilityIdentifier "animalList.reportMissingButton" (already added in US1)
- [ ] T125 [US2] Verify button remains fixed at bottom in AnimalListView (outside ScrollView) - **Mobile has single button only per Figma 48:6096**
- [ ] T126 [US2] Add button action to call viewModel.reportMissing()
- [ ] T127 [US2] Verify AnimalListCoordinator handles onReportMissing closure (already added in US1)

**Web Implementation (Two Buttons at Top-Right)**:
- [ ] T128 [US2] Verify reportMissing() and reportFound() methods exist in useAnimalList.ts (already added in US1)
- [ ] T129 [US2] Implement reportMissing() to trigger placeholder navigation (console.log or navigate('/report-missing'))
- [ ] T130 [US2] Implement reportFound() to trigger placeholder navigation (console.log or navigate('/report-found'))
- [ ] T131 [US2] Verify "Report a Missing Animal" button in AnimalList.tsx has data-testid "animalList.reportMissingButton" (already added in US1)
- [ ] T132 [US2] Add "Report Found Animal" button (secondary) to AnimalList.tsx with data-testid "animalList.reportFoundButton" - **Web has two buttons at top-right per Figma 71:9154**
- [ ] T133 [US2] Verify buttons remain fixed at top-right in AnimalList.tsx (web layout differs from mobile per spec L170)
- [ ] T134 [US2] Add onClick handlers for buttons to call reportMissing() and reportFound() from hook
- [ ] T135 [US2] Update AnimalList.module.css with button styles per Figma (primary: #2D2D2D, secondary: #E5E9EC)

**Verification for User Story 2**:
- [ ] T136 [US2] Run Android ViewModel tests: verify ReportMissing/Found intent tests pass
- [ ] T137 [US2] Run iOS ViewModel tests: verify reportMissing/Found callback tests pass
- [ ] T138 [US2] Run web hook tests: verify reportMissing/Found method tests pass
- [ ] T139 [US2] Run web E2E tests: verify both buttons visible, clickable, and trigger actions
- [ ] T140 [US2] Run mobile E2E tests: verify single button visible, tappable, and triggers action
- [ ] T141 [US2] Manual test Android: Scroll list and verify single button remains visible at bottom
- [ ] T142 [US2] Manual test iOS: Scroll list and verify single button remains visible at bottom
- [ ] T143 [US2] Manual test Web: Scroll list and verify both buttons remain fixed at top-right (not bottom)

**Checkpoint**: User Story 2 complete - Report buttons visible, accessible, and functional. All tests pass.

---

## Phase 5: User Story 3 - Search Preparation (Priority: P3)

**Goal**: Screen layout reserves appropriate space at the top for a future search component without implementing actual search functionality

**Independent Test**: Verify that the layout has appropriate spacing at the top of the screen where the search component will be added. Space should be part of the scrollable content or fixed header depending on design.

### Tests for User Story 3 (Write FIRST, ensure FAIL) ✅

**End-to-End Tests**:
- [ ] T144 [P] [US3] E2E test web: verify reserved search space exists at top of screen in animal-list.spec.ts
- [ ] T145 [P] [US3] E2E test web: verify search space has correct height/dimensions in animal-list.spec.ts
- [ ] T146 [P] [US3] E2E test mobile: verify reserved search space exists at top in animal-list.spec.ts
- [ ] T147 [P] [US3] E2E test mobile: verify layout adapts correctly with reserved space in animal-list.spec.ts

### Implementation for User Story 3

**Android Implementation**:
- [ ] T148 [US3] Verify reserved search space exists in AnimalListScreen (already added in US1 T066)
- [ ] T149 [US3] Add placeholder search component (non-functional) to AnimalListScreen with testTag "animalList.searchPlaceholder"
- [ ] T150 [US3] Style placeholder search area per Figma design (186dp wide button, outlined style, 48-56dp height with 16dp padding per FR-004)
- [ ] T151 [US3] Verify search placeholder does not interfere with list scrolling

**iOS Implementation**:
- [ ] T152 [US3] Verify reserved search space exists in AnimalListView (already added in US1 T077)
- [ ] T153 [US3] Add placeholder search component (non-functional) to AnimalListView with accessibilityIdentifier "animalList.searchPlaceholder"
- [ ] T154 [US3] Style placeholder search area per Figma design (186pt wide button, outlined style, 48-56pt height with 16pt padding per FR-004)
- [ ] T155 [US3] Verify search placeholder does not interfere with LazyVStack scrolling

**Web Implementation**:
- [ ] T156 [US3] Verify reserved search space exists in AnimalList.tsx (already added in US1 T090)
- [ ] T157 [US3] Add placeholder search component (non-functional) to AnimalList.tsx with data-testid "animalList.searchPlaceholder"
- [ ] T158 [US3] Style placeholder search area per Figma web design (582px search bar + filter controls on right)
- [ ] T159 [US3] Verify search placeholder does not interfere with list scrolling

**Verification for User Story 3**:
- [ ] T160 [US3] Run web E2E tests: verify search placeholder element exists and has correct dimensions
- [ ] T161 [US3] Run mobile E2E tests: verify search placeholder exists and layout is correct
- [ ] T162 [US3] Manual test Android: Verify search space reserved, list layout looks correct
- [ ] T163 [US3] Manual test iOS: Verify search space reserved, list layout looks correct
- [ ] T164 [US3] Manual test Web: Verify search space reserved with correct width (582px search + filters)

**Checkpoint**: User Story 3 complete - Search space prepared for future implementation. Layout validated.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories and final quality checks

**Documentation Strategy** (Constitution XI compliance):
- **First-pass verification**: T067 (Android), T081 (iOS), T092 (Web) verify documentation DURING development (per-platform, incremental)
- **Second-pass comprehensive audit**: T168-T171 perform final cross-feature validation (Phase 6, comprehensive)
- This two-stage approach ensures documentation is created with code (constitution requirement) and validated comprehensively before release

- [ ] T165 [P] **FINAL VALIDATION**: Audit all Android UI elements for testTag identifiers (screen.element.action convention) - verify completeness across all screens
- [ ] T166 [P] **FINAL VALIDATION**: Audit all iOS UI elements for accessibilityIdentifier (screen.element.action convention) - verify completeness across all screens
- [ ] T167 [P] **FINAL VALIDATION**: Audit all Web UI elements for data-testid (screen.element.action convention) - verify completeness across all screens
- [ ] T168 [P] **FINAL COMPREHENSIVE AUDIT**: Re-verify ALL shared module public API documentation (KDoc) - comprehensive review of all classes, methods, properties across entire feature per constitution XI (second-pass comprehensive validation)
- [ ] T169 [P] **FINAL COMPREHENSIVE AUDIT**: Re-verify ALL Android ViewModels and repositories documentation (KDoc) - comprehensive review across entire feature per constitution XI (second-pass comprehensive validation)
- [ ] T170 [P] **FINAL COMPREHENSIVE AUDIT**: Re-verify ALL iOS ViewModels and repositories documentation (SwiftDoc) - comprehensive review across entire feature per constitution XI (second-pass comprehensive validation)
- [ ] T171 [P] **FINAL COMPREHENSIVE AUDIT**: Re-verify ALL Web hooks and services documentation (JSDoc) - comprehensive review across entire feature per constitution XI (second-pass comprehensive validation)
- [ ] T172 Visual design review: Compare Android UI with Figma mobile design (48:6096) - colors, typography, spacing, shadows per design QA checklist
- [ ] T173 Visual design review: Compare iOS UI with Figma mobile design (48:6096) - colors, typography, spacing per design QA checklist
- [ ] T174 Visual design review: Compare Web UI with Figma web design (71:9154) - colors, typography, spacing, layout per design QA checklist
- [ ] T175 [P] Run final shared module test coverage check: `./gradlew :shared:test koverHtmlReport` (must be 80%+)
- [ ] T176 [P] Run final Android ViewModel test coverage check: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` (must be 80%+)
- [ ] T177 [P] Run final iOS ViewModel test coverage: XCTest with coverage enabled (must be 80%+)
- [ ] T178 [P] Run final Web hook test coverage: `npm test -- --coverage` (must be 80%+)
- [ ] T179 [P] Run all E2E tests web: `npx playwright test e2e-tests/web/specs/animal-list.spec.ts`
- [ ] T180 [P] Run all E2E tests mobile Android: `npm run test:mobile:android`
- [ ] T181 Run linter checks for Android: `./gradlew :composeApp:lint`
- [ ] T182 Run linter checks for iOS: verify no SwiftLint warnings
- [ ] T183 Run linter checks for Web: `npm run lint` (if configured)
- [ ] T184 Performance test Android: Verify smooth scrolling with 10+ animals (60 FPS best practice, not strict requirement)
- [ ] T185 Performance test iOS: Verify smooth scrolling with LazyVStack (60 FPS best practice, not strict requirement)
- [ ] T186 Performance test Web: Verify smooth scrolling in browser (60 FPS best practice, not strict requirement)
- [ ] T187 Accessibility test Android: Verify TalkBack reads all interactive elements correctly
- [ ] T188 Accessibility test iOS: Verify VoiceOver reads all interactive elements correctly
- [ ] T189 Accessibility test Web: Verify screen reader (NVDA/JAWS) reads all elements correctly
- [ ] T190 Empty state test: Manually test empty state on all platforms (temporarily modify AnimalRepositoryImpl to return emptyList()) and verify message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one." (then revert mock data)
- [ ] T191 Error state test: Manually test error state on all platforms (modify mock repository to throw exception)
- [ ] T192 Loading state test: Manually test loading state on all platforms (increase mock delay to 3 seconds)
- [ ] T193 Update main README.md with Animal List Screen feature description
- [ ] T194 Create demo video showing feature on all three platforms (Android, iOS, Web)
- [ ] T195 Run quickstart.md validation: Follow quickstart guide and verify all steps work (requires quickstart.md from Phase 1)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Extends US1 UI but is independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Extends US1 UI but is independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Shared module → Platform repositories → ViewModels → UI → E2E tests
- Tests can run in parallel where marked [P]
- UI components can be built in parallel where marked [P]
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks (Phase 1) can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Android, iOS, and Web implementations can proceed in parallel for each story
- All Polish tasks marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# After Foundational phase completes, launch all US1 tests in parallel:
Task T025: Create FakeAnimalRepository (parallel with T026-T042)
Task T026: Unit test GetAnimalsUseCase success (parallel with other tests)
Task T029: Unit test AnimalListReducer success (parallel with other tests)
Task T035: Unit test iOS AnimalListViewModel (parallel with other tests)
Task T039: Unit test Web useAnimalList hook (parallel with other tests)

# Launch all platform implementations in parallel after tests:
Task T052-T068: Android implementation (Developer A)
Task T069-T082: iOS implementation (Developer B)
Task T083-T093: Web implementation (Developer C)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only) 🎯

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (View Animal List)
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready (MVP = scrollable list with mock data)

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo (buttons functional)
4. Add User Story 3 → Test independently → Deploy/Demo (search space prepared)
5. Complete Polish → Final quality checks → Production release

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together (T001-T024)
2. Once Foundational is done:
   - **Developer A**: User Story 1 Android (T025-T068)
   - **Developer B**: User Story 1 iOS (T069-T082)
   - **Developer C**: User Story 1 Web (T083-T093)
3. After US1 complete, repeat for US2 and US3
4. Team completes Polish together (T167-T197)

---

## Notes

- **[P] tasks**: Different files, no dependencies - can run in parallel
- **[Story] label**: Maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests FAIL before implementing (TDD approach)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All test identifiers follow strict convention: `{screen}.{element}.{action}`
- All documentation must be concise (1-3 sentences: WHAT/WHY, not HOW)
- 80% test coverage is MANDATORY for shared module, ViewModels
- E2E tests MANDATORY for all user stories

---

## Summary

**Total Tasks**: 208 (195 original + 6 ViewModel implementation tasks + 5 verification tasks + 1 shared mock data generator: T016a + 1 JUnit 6 config: T011a)  
**User Story 1 Tasks**: T025-T102 + subtasks (90 tasks including ViewModel methods, empty state verification, entry point configuration, shared mock data)  
**User Story 2 Tasks**: T103-T143 (41 tasks - mobile has single button, web has two)  
**User Story 3 Tasks**: T144-T164 (21 tasks)  
**Parallel Opportunities**: 88 tasks marked [P] (T011a added)  
**MVP Scope**: Phase 1 + Phase 2 + Phase 3 (User Story 1 only) = ~113 tasks

**Mock Data Consistency**: T016a (MockAnimalData.generateMockAnimals()) provides single source of truth for mock animals used across:
- Shared tests (T025: FakeAnimalRepository)
- Android production mock (T052: AnimalRepositoryImpl)
- iOS production mock (T069: AnimalRepositoryImpl.swift)
- Web production mock (T083: animalRepository.ts)
This ensures consistent test data across all platforms and reduces maintenance burden.  

**Platform Button Differences**:
- **Mobile (Android/iOS)**: Single "Report a Missing Animal" button at bottom (per Figma 48:6096)
- **Web**: Two buttons at top-right - "Report a Missing Animal" + "Report Found Animal" (per Figma 71:9154)

