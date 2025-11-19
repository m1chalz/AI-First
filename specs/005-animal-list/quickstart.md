# Quickstart Guide: Animal List Screen Implementation

**Feature**: Animal List Screen  
**Date**: 2025-11-19  
**Branch**: `005-animal-list`

## Overview

This guide provides step-by-step instructions for implementing the Animal List Screen feature. Follow the order below to ensure proper architecture and dependencies.

**Estimated Time**: 6-8 hours for full implementation across all three platforms + tests

## Prerequisites

Before starting implementation:

- [ ] Read [spec.md](./spec.md) - feature requirements and clarifications
- [ ] Read [plan.md](./plan.md) - implementation plan and constitution check
- [ ] Read [research.md](./research.md) - design decisions and patterns
- [ ] Read [data-model.md](./data-model.md) - domain model definitions
- [ ] Read [contracts/AnimalRepository.kt.md](./contracts/AnimalRepository.kt.md) - repository interface contract

## Implementation Order

### Phase 1: Shared Module (Domain Layer)

**Time Estimate**: 1-1.5 hours

#### Step 1.1: Create Domain Models

Create domain models in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/`:

1. **Location.kt**
   - Data class with `city: String`, `radiusKm: Int`
   - Add `@OptIn(ExperimentalJsExport::class)` and `@JsExport`
   - Note: Display formatting should be done in UI layer, not here

2. **AnimalSpecies.kt**
   - Enum: DOG, CAT, BIRD, RABBIT, OTHER
   - Each with `displayName: String` property
   - Add `@OptIn(ExperimentalJsExport::class)` and `@JsExport`

3. **AnimalGender.kt**
   - Enum: MALE, FEMALE, UNKNOWN
   - Each with `displayName: String` property
   - Add `@OptIn(ExperimentalJsExport::class)` and `@JsExport`

4. **AnimalStatus.kt**
   - Enum: MISSING, FOUND
   - Each with `displayName: String` and `badgeColor: String` (hex) properties
   - Colors from spec: MISSING = "#FF0000", FOUND = "#0074FF"
   - Add `@OptIn(ExperimentalJsExport::class)` and `@JsExport`

5. **Animal.kt**
   - Data class with all properties (see data-model.md)
   - Add KDoc documentation
   - Add `@OptIn(ExperimentalJsExport::class)` and `@JsExport`

**Verification**:
```bash
./gradlew :shared:build
```

#### Step 1.2: Create Repository Interface

Create in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/`:

1. **AnimalRepository.kt**
   - Interface with `suspend fun getAnimals(): Result<List<Animal>>`
   - Add KDoc documentation (see contracts/)

**Verification**:
```bash
./gradlew :shared:build
```

#### Step 1.3: Create Use Case

Create in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/usecases/`:

1. **GetAnimalsUseCase.kt**
   - Constructor parameter: `AnimalRepository`
   - Operator invoke: `suspend operator fun invoke(): Result<List<Animal>>`
   - Implementation: `return repository.getAnimals()`
   - Add KDoc documentation

**Verification**:
```bash
./gradlew :shared:build
```

#### Step 1.4: Update Koin Domain Module

Update `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt`:

```kotlin
val domainModule = module {
    factory { GetAnimalsUseCase(get()) }  // NEW
    // ... existing use cases
}
```

**Verification**:
```bash
./gradlew :shared:build
```

#### Step 1.5: Write Unit Tests

Create in `/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/`:

1. **domain/repositories/FakeAnimalRepository.kt** (test fake)
2. **domain/usecases/GetAnimalsUseCaseTest.kt**
   - Test success scenario (returns animals)
   - Test failure scenario (repository throws exception)
   - Test empty scenario (returns empty list)
   - Use FakeAnimalRepository with Koin Test
   - Follow Given-When-Then structure

**Verification**:
```bash
./gradlew :shared:test
./gradlew :shared:test koverHtmlReport
# Open shared/build/reports/kover/html/index.html
# Verify 80%+ coverage
```

---

### Phase 2: Android Implementation (Compose + MVI)

**Time Estimate**: 2-2.5 hours

#### Step 2.1: Create Repository Implementation

Create in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/`:

1. **AnimalRepositoryImpl.kt**
   - Implement `AnimalRepository` interface
   - Return 16 mock Animal entities (see contracts/ and MockAnimalData from T016a) - mocked data for UI development
   - Simulate 500ms delay with `kotlinx.coroutines.delay()`

**Verification**:
```bash
./gradlew :composeApp:build
```

#### Step 2.2: Update Koin Modules

1. **DataModule.kt** (`/composeApp/src/androidMain/.../di/`):
   ```kotlin
   val dataModule = module {
       single<AnimalRepository> { AnimalRepositoryImpl() }
       // ... existing repositories
   }
   ```

2. **ViewModelModule.kt** (`/composeApp/src/androidMain/.../di/`):
   ```kotlin
   val viewModelModule = module {
       viewModel { AnimalListViewModel(get()) }  // NEW
       // ... existing ViewModels
   }
   ```

#### Step 2.3: Create MVI Contracts

Create in `/composeApp/src/androidMain/.../features/animallist/presentation/mvi/`:

1. **AnimalListUiState.kt**
   - Data class: `animals`, `isLoading`, `error`
   - Computed `isEmpty` property
   - `companion object { val Initial }`

2. **AnimalListIntent.kt**
   - Sealed interface: Refresh, SelectAnimal(id), ReportMissing, ReportFound

3. **AnimalListEffect.kt**
   - Sealed interface: NavigateToDetails(id), NavigateToReportMissing, NavigateToReportFound

4. **AnimalListReducer.kt**
   - Object with `reduce(state, Result<List<Animal>>): AnimalListUiState` function
   - Pure reducer - no side effects

#### Step 2.4: Create ViewModel

Create in `/composeApp/src/androidMain/.../features/animallist/presentation/viewmodels/`:

1. **AnimalListViewModel.kt**
   - Extends `ViewModel`
   - Private `_state: MutableStateFlow<AnimalListUiState>`
   - Public `state: StateFlow<AnimalListUiState>`
   - Private `_effects: MutableSharedFlow<AnimalListEffect>`
   - Public `effects: SharedFlow<AnimalListEffect>`
   - Method: `fun dispatchIntent(intent: AnimalListIntent)`
   - Handle intents in `viewModelScope.launch`
   - Use reducer for state updates

**Verification**:
```bash
./gradlew :composeApp:build
```

#### Step 2.5: Create UI Components

Create in `/composeApp/src/androidMain/.../features/animallist/ui/`:

1. **AnimalListScreen.kt**
   - Composable function
   - Collect state with `collectAsStateWithLifecycle()`
   - LazyColumn with test tag `animalList.list`
   - Loading indicator when `isLoading`
   - Error message when `error != null`
   - Empty state when `isEmpty`
   - Floating buttons at bottom (Report Missing, Report Found) with test tags
   - Collect effects in `LaunchedEffect` for navigation

2. **AnimalCard.kt**
   - Composable for single animal item
   - Card with test tag `animalList.item.${animal.id}`
   - Display: image, location, species | breed, status badge, date
   - Colors from Figma spec (see design/)
   - OnClick callback for selection

3. **EmptyState.kt**
   - Composable showing empty state message
   - Message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."

**Verification**:
```bash
./gradlew :composeApp:assembleDebug
# Run app on emulator/device
# Verify UI matches Figma design
```

#### Step 2.6: Add Navigation

Create in `/composeApp/src/androidMain/.../features/animallist/navigation/`:

1. **AnimalListNavigation.kt**
   - Add route to Navigation graph
   - Handle effects for navigation (mocked - print log for now)

#### Step 2.7: Write Tests

Create in `/composeApp/src/androidUnitTest/.../features/animallist/`:

1. **presentation/mvi/AnimalListReducerTest.kt**
   - Test all reducer branches (success, failure, empty)
   - Verify state transitions

2. **presentation/viewmodels/AnimalListViewModelTest.kt**
   - Test intent handling with Turbine
   - Test state emissions
   - Test effect emissions
   - Use FakeAnimalRepository with Koin Test

**Verification**:
```bash
./gradlew :composeApp:testDebugUnitTest
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# Open composeApp/build/reports/kover/html/index.html
# Verify 80%+ coverage
```

---

### Phase 3: iOS Implementation (SwiftUI + MVVM-C)

**Time Estimate**: 2-2.5 hours

#### Step 3.1: Create Repository Implementation

Create in `/iosApp/iosApp/Repositories/`:

1. **AnimalRepositoryImpl.swift**
   - Function: `async func getAnimals() throws -> [Animal]`
   - Return 16 mock animals (same structure as Android, see MockAnimalData from T016a) - mocked data for UI development
   - Simulate delay with `Task.sleep(nanoseconds:)`

**Verification**:
```bash
cd iosApp
xcodebuild -scheme iosApp build -destination 'platform=iOS Simulator,name=iPhone 15'
```

#### Step 3.2: Create ViewModel

Create in `/iosApp/iosApp/ViewModels/`:

1. **AnimalListViewModel.swift**
   - Conform to `ObservableObject`
   - `@Published var animals: [Animal]`
   - `@Published var isLoading: Bool`
   - `@Published var errorMessage: String?`
   - Computed `var isEmpty: Bool`
   - Closures: `var onAnimalSelected: ((String) -> Void)?`, `onReportMissing: (() -> Void)?`, `onReportFound: (() -> Void)?`
   - Method: `@MainActor func loadAnimals() async`
   - Methods for user actions (selectAnimal, reportMissing, reportFound) that call closures

**Verification**:
```bash
xcodebuild -scheme iosApp build -destination 'platform=iOS Simulator,name=iPhone 15'
```

#### Step 3.3: Create SwiftUI Views

Create in `/iosApp/iosApp/Views/`:

1. **AnimalListView.swift**
   - SwiftUI view observing ViewModel
   - `ScrollView` with `LazyVStack` (for performance with large lists) with `.accessibilityIdentifier("animalList.list")`
   - Loading spinner when `isLoading`
   - Error message when `errorMessage != nil`
   - Empty state when `isEmpty`
   - Buttons for Report Missing/Found with accessibility IDs

2. **AnimalCardView.swift**
   - SwiftUI view for single animal
   - Accessibility ID: `animalList.item.${animal.id}`
   - Display all animal properties
   - Colors from Figma spec

3. **EmptyStateView.swift**
   - SwiftUI view for empty state
   - Same message as Android

**Verification**:
```bash
xcodebuild -scheme iosApp build -destination 'platform=iOS Simulator,name=iPhone 15'
# Run in simulator
# Verify UI matches Figma
```

#### Step 3.4: Create Coordinator

Create in `/iosApp/iosApp/Coordinators/`:

1. **AnimalListCoordinator.swift**
   - Property: `navigationController: UINavigationController`
   - Method: `func start()`
   - Creates ViewModel, sets up closures, creates UIHostingController
   - Methods: `showAnimalDetails(animalId:)`, `showReportMissing()`, `showReportFound()` (mocked - print log)

2. **Update AppCoordinator.swift**:
   - Set AnimalListCoordinator as root coordinator
   - Initialize and call `start()`

**Verification**:
```bash
xcodebuild -scheme iosApp build -destination 'platform=iOS Simulator,name=iPhone 15'
# Run and verify navigation structure
```

#### Step 3.5: Write Tests

Create in `/iosApp/iosAppTests/ViewModels/`:

1. **AnimalListViewModelTests.swift**
   - Test `loadAnimals()` success scenario
   - Test failure scenario (error message set)
   - Test empty scenario (isEmpty true)
   - Test callback invocations (onAnimalSelected, etc.)
   - Use async/await with XCTest

**Verification**:
```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
# Check coverage report
# Verify 80%+ coverage
```

---

### Phase 4: Web Implementation (React + TypeScript)

**Time Estimate**: 1.5-2 hours

#### Step 4.1: Create Mock Repository

Create in `/webApp/src/services/`:

1. **animalRepository.ts**
   - Export `AnimalRepositoryImpl` class implementing repository interface
   - Method: `async getAnimals(): Promise<Animal[]>`
   - Return 16 mock animals (same structure as MockAnimalData from T016a for consistency)
   - Simulate delay with `setTimeout` (500ms)
   - Note: This is production code with mocked data - backend integration will replace mock data later

**Verification**:
```bash
cd webApp
npm run build
```

#### Step 4.2: Create Custom Hook

Create in `/webApp/src/hooks/`:

1. **useAnimalList.ts**
   - State: `animals`, `isLoading`, `error`
   - Computed: `isEmpty`
   - Methods: `loadAnimals`, `selectAnimal`, `reportMissing`, `reportFound`
   - Call `loadAnimals()` in `useEffect`

**Verification**:
```bash
npm run build
```

#### Step 4.3: Create Components

Create in `/webApp/src/components/AnimalList/`:

1. **AnimalList.tsx**
   - Use `useAnimalList` hook
   - Render list with `data-testid="animalList.list"`
   - Loading indicator
   - Error message
   - Empty state
   - Buttons with `data-testid`

2. **AnimalCard.tsx**
   - Render animal properties
   - `data-testid="animalList.item.${animal.id}"`
   - OnClick handler

3. **EmptyState.tsx**
   - Same message as Android/iOS

4. **AnimalList.module.css** (or styled-components)
   - Colors from Figma spec
   - Typography from Figma spec
   - Spacing from Figma spec

**Verification**:
```bash
npm run start
# Verify UI in browser
# Check design matches Figma
```

#### Step 4.4: Write Tests

Create in `/webApp/src/__tests__/`:

1. **hooks/useAnimalList.test.ts**
   - Test hook state updates
   - Test loading/error/empty states
   - Use `renderHook` from React Testing Library

2. **components/AnimalList.test.tsx**
   - Test component rendering
   - Test user interactions
   - Use React Testing Library

**Verification**:
```bash
npm test -- --coverage
# Verify 80%+ coverage
```

---

### Phase 5: End-to-End Tests

**Time Estimate**: 1.5-2 hours

#### Step 5.1: Web E2E Tests (Playwright)

Create in `/e2e-tests/web/`:

1. **pages/AnimalListPage.ts** (Page Object Model)
   - Locators for all interactive elements
   - Methods: `goto()`, `getAnimalCards()`, `clickReportMissing()`, etc.

2. **steps/animalListSteps.ts** (Reusable steps)
   - Functions: `givenUserIsOnAnimalListPage()`, `whenUserScrollsList()`, `thenAnimalCardsAreVisible()`, etc.

3. **specs/animal-list.spec.ts**
   - Test User Story 1: View List (displays animals, scrollable)
   - Test User Story 2: Report Action (button visible, click triggers action)
   - Test User Story 3: Card Tap (navigation triggered)
   - Test Empty State (shows message)
   - Follow Given-When-Then structure

**Verification**:
```bash
npx playwright test e2e-tests/web/specs/animal-list.spec.ts
```

#### Step 5.2: Mobile E2E Tests (Appium)

Create in `/e2e-tests/mobile/`:

1. **screens/AnimalListScreen.ts** (Screen Object Model)
   - Locators using accessibility IDs
   - Methods: `getAnimalCards()`, `clickReportMissing()`, etc.

2. **steps/animalListSteps.ts** (Reusable steps)
   - Functions for Given-When-Then actions

3. **specs/animal-list.spec.ts**
   - Same test scenarios as web (User Story 1, 2, 3, empty state)
   - Platform-specific conditions if needed

**Verification**:
```bash
npm run test:mobile:android  # For Android
npm run test:mobile:ios      # For iOS
```

---

## Final Verification Checklist

Before marking feature complete:

### Constitution Compliance

- [ ] Shared module contains only domain logic (no UI, no ViewModels)
- [ ] Each platform has native presentation layer (Compose MVI, SwiftUI MVVM-C, React hooks)
- [ ] 80% test coverage achieved:
  - [ ] Shared module: `./gradlew :shared:test koverHtmlReport`
  - [ ] Android ViewModels: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - [ ] iOS ViewModels: XCTest with coverage
  - [ ] Web hooks: `npm test -- --coverage`
- [ ] All interactive elements have test identifiers (testTag, accessibilityIdentifier, data-testid)
- [ ] All public APIs have documentation (KDoc, SwiftDoc, JSDoc)
- [ ] All tests follow Given-When-Then structure
- [ ] E2E tests cover all user stories for web and mobile

### Feature Requirements (from spec.md)

- [ ] FR-001: Scrollable list displays 8-12 mock animals
- [ ] FR-002: "Report a Missing Animal" button at bottom
- [ ] FR-003: Button remains visible while scrolling
- [ ] FR-004: Space reserved for future search component
- [ ] FR-005: Layout matches Figma design (100% visual accuracy)
- [ ] FR-006: Business logic for loading animals (mocked)
- [ ] FR-007: Business logic for button actions (mocked)
- [ ] FR-008: List scrollable when content exceeds screen
- [ ] FR-009: Empty state displays correct message
- [ ] FR-010: Screen is primary entry point on all platforms
- [ ] FR-011: Card tap action handled (mocked navigation)

### Success Criteria (from spec.md)

- [ ] SC-001: Buttons accessible within single tap from any scroll position
- [ ] SC-002: Layout matches Figma design with 100% visual accuracy:
  - [ ] All colors match hex values from spec
  - [ ] Typography uses Inter and Roboto fonts at specified sizes
  - [ ] Spacing matches 8px card gap, 16px padding, etc.
  - [ ] Shadows, border radius match Figma
- [ ] SC-003: Empty state message is clear and understandable

### Cross-Platform Consistency

- [ ] Mock data identical across Android, iOS, Web (16 animals, same structure per MockAnimalData from T016a)
- [ ] Empty state message identical on all platforms
- [ ] Test identifiers follow same naming convention (`animalList.element.action`)
- [ ] Colors, typography, spacing match Figma exactly on all platforms
- [ ] Navigation behavior consistent (mocked placeholders on all platforms)

---

## Troubleshooting

### Common Issues

**Issue**: Kotlin/JS export errors for `Animal` model  
**Solution**: Ensure `@OptIn(ExperimentalJsExport::class)` and `@JsExport` on all exported classes

**Issue**: Koin dependency injection fails  
**Solution**: Check module ordering in `startKoin { modules(...) }` - domainModule must be before platformModules

**Issue**: iOS shared framework not found  
**Solution**: Run `./gradlew :shared:embedAndSignAppleFrameworkForXcode` or build shared module first

**Issue**: Test coverage below 80%  
**Solution**: Add missing test cases for error scenarios, empty scenarios, edge cases

**Issue**: E2E tests can't find elements  
**Solution**: Verify test identifiers are applied to correct UI elements and follow naming convention

---

## Next Steps

After completing implementation:

1. **Code Review**: Submit PR with all changes, reference spec and plan
2. **QA Testing**: Manual testing on all three platforms
3. **Performance Testing**: Verify smooth scrolling (60 FPS target, though not mandatory per spec)
4. **Accessibility Testing**: Verify screen readers work with test identifiers
5. **Design Review**: Compare with Figma mockups side-by-side
6. **Documentation**: Update project README with feature description
7. **Demo**: Record video demo showing feature on all platforms

**Subsequent Features**:
- Search functionality (currently reserved space)
- Detail screen (currently mocked navigation)
- Report Missing Animal form (currently mocked button action)
- Backend integration (replace mock repositories)

---

## Support & Resources

- **Spec**: [spec.md](./spec.md)
- **Plan**: [plan.md](./plan.md)
- **Research**: [research.md](./research.md)
- **Data Model**: [data-model.md](./data-model.md)
- **Contracts**: [contracts/](./contracts/)
- **Design**: [design/README.md](./design/README.md)
- **Constitution**: `/.specify/memory/constitution.md`
- **Figma**: [PetSpot-wireframes](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes)

**Questions?** Refer to constitution or ask team lead.

**Good luck with implementation!** 🚀

