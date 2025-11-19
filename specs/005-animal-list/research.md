# Research & Design Decisions: Animal List Screen

**Feature**: Animal List Screen  
**Date**: 2025-11-19  
**Status**: Phase 0 Complete

## Overview

This document records research findings and design decisions made during planning for the Animal List Screen feature. All decisions align with project constitution and are based on established patterns in the PetSpot codebase.

## Research Tasks Completed

### 1. Mock Data Strategy for UI-Only Implementation

**Decision**: Use in-memory mock repositories with hardcoded animal data (8-12 items)

**Rationale**:
- Enables UI development without backend dependency
- Allows testing of scrolling, empty states, and navigation patterns
- Mock repositories implement same AnimalRepository interface as future real implementations
- Easy transition: swap mock with real HTTP repository via Koin DI in future sprint

**Implementation**:
- `MockAnimalRepository` provides fixed list of 8-12 Animal entities
- Data includes varied species (Dog, Cat), breeds, statuses (Missing, Found), locations
- Empty state tested by injecting empty mock repository in tests
- Mock repository returns `Result.success(animals)` to match real API contract

**Alternatives Considered**:
- JSON files: Rejected - adds file I/O complexity for simple UI prototype
- Shared mock in KMP: Rejected - mock generation might need platform-specific UUIDs or dates
- Random generation: Rejected - non-deterministic data makes tests flaky

---

### 2. Navigation Pattern for Card Taps & Button Actions

**Decision**: Mocked navigation with callbacks/effects, prepared for future coordinator/nav integration

**Rationale**:
- Spec clarifies navigation is "for later" - tap triggers placeholder action
- Maintains proper architecture separation (ViewModel doesn't know about navigation destinations)
- Android: Emit `NavigateToDetails(animalId)` effect via SharedFlow, handled by Compose Navigation
- iOS: ViewModel calls `onAnimalSelected?(animalId)` closure set by coordinator
- Web: React Router navigation triggered from component after hook method call

**Implementation**:
- Android: `AnimalListEffect.NavigateToDetails(id: String)` - collected in LaunchedEffect block
- iOS: `var onAnimalSelected: ((String) -> Void)?` closure property in ViewModel
- Web: `navigate('/animal/' + id)` in onClick handler after hook method call
- Future: Replace mocked navigation with real screen destinations

**Test Strategy**:
- Android: Verify effect emission with Turbine when SelectAnimal intent dispatched
- iOS: Capture callback invocation in ViewModel test
- Web: Mock navigate function and assert it was called with correct path
- E2E: Verify placeholder behavior (button clicks don't crash, cards are tappable)

---

### 3. Android MVI State Management Pattern

**Decision**: Single `StateFlow<AnimalListUiState>` with immutable data class + sealed intents/effects

**Rationale**:
- Constitution mandates MVI loop for Android Compose
- Single source of truth prevents UI drift and race conditions
- Pure reducers enable isolated unit testing without mocking coroutines
- Effect channel separates one-off events (navigation, snackbars) from continuous state

**Implementation**:
```kotlin
data class AnimalListUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
) {
    companion object { val Initial = AnimalListUiState() }
}

sealed interface AnimalListIntent {
    data object Refresh : AnimalListIntent
    data class SelectAnimal(val id: String) : AnimalListIntent
    data object ReportMissing : AnimalListIntent
    data object ReportFound : AnimalListIntent
}

sealed interface AnimalListEffect {
    data class NavigateToDetails(val animalId: String) : AnimalListEffect
    data object NavigateToReportMissing : AnimalListEffect
    data object NavigateToReportFound : AnimalListEffect
}
```

**Reducer Logic**:
- `reduce(currentState, Result<List<Animal>>)` produces next `UiState`
- Loading state set before use case call, cleared after
- Empty check: `isEmpty = animals.isEmpty() && !isLoading && error == null`
- Error state preserves previous animals list (allows retry without losing UI)

**Test Coverage**:
- Reducer tests: All branches (success, failure, empty, loading)
- ViewModel tests: Intent handling, state transitions, effect emissions (Turbine)
- UI tests: E2E verification of MVI loop (not unit tests)

---

### 4. iOS MVVM-C Coordinator Pattern

**Decision**: UIKit-based coordinator creates `UIHostingController` with SwiftUI views observing ViewModels

**Rationale**:
- Constitution mandates MVVM-C for iOS
- Coordinator owns navigation logic (no navigation in ViewModel or View)
- SwiftUI views remain pure and testable (only render based on @Published state)
- UIHostingController enables UIKit navigation (push, present, dismiss)
- Parent-child coordinator pattern enables complex flows

**Implementation**:
```swift
// Coordinator (UIKit-based)
class AnimalListCoordinator {
    private let navigationController: UINavigationController
    
    func start() {
        let viewModel = AnimalListViewModel(getAnimalsUseCase: /* inject */)
        
        // Set up navigation callbacks
        viewModel.onAnimalSelected = { [weak self] animalId in
            self?.showAnimalDetails(animalId: animalId)
        }
        viewModel.onReportMissing = { [weak self] in
            self?.showReportMissing()
        }
        viewModel.onReportFound = { [weak self] in
            self?.showReportFound()
        }
        
        let view = AnimalListView(viewModel: viewModel)
        let hostingController = UIHostingController(rootView: view)
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    private func showAnimalDetails(animalId: String) {
        // Placeholder: print("Navigate to details: \(animalId)")
        // Future: Create AnimalDetailCoordinator and call start()
    }
}

// View (SwiftUI with LazyVStack for performance)
struct AnimalListView: View {
    @ObservedObject var viewModel: AnimalListViewModel
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 8) {  // Lazy loading for large lists
                ForEach(viewModel.animals) { animal in
                    AnimalCardView(animal: animal)
                        .accessibilityIdentifier("animalList.item.\(animal.id)")
                        .onTapGesture {
                            viewModel.selectAnimal(id: animal.id)
                        }
                }
            }
            .padding(.horizontal, 16)
            .accessibilityIdentifier("animalList.list")
        }
    }
}

// ViewModel (ObservableObject)
@MainActor
class AnimalListViewModel: ObservableObject {
    @Published var animals: [Animal] = []
    @Published var isLoading = false
    @Published var error: String?
    
    // Coordinator callbacks
    var onAnimalSelected: ((String) -> Void)?
    var onReportMissing: (() -> Void)?
    var onReportFound: (() -> Void)?
    
    func selectAnimal(id: String) {
        onAnimalSelected?(id)  // Coordinator handles navigation
    }
}
```

**Test Strategy**:
- ViewModel tests: Verify @Published property updates, callback invocations
- Coordinator tests (optional): Verify navigation methods called
- E2E tests: Verify actual navigation behavior end-to-end

**Performance Note: LazyVStack vs List**:
- **Decision**: Use `LazyVStack` inside `ScrollView` instead of `List`
- **Rationale**:
  - Both are lazy-loaded (render only visible items)
  - `LazyVStack` provides more layout control and customization
  - Better for complex layouts and custom scroll behavior
  - Easier to add pull-to-refresh, infinite scroll, custom animations
  - More flexible for future enhancements (e.g., sticky headers, parallax effects)
  - `List` uses UITableView internally (more restrictive for custom designs)
- **Implementation**: `ScrollView { LazyVStack(spacing: 8) { ForEach(...) } }`
- **Performance**: Identical to `List` for lazy loading; both recycle views efficiently
- **Future-proof**: Supports pagination, infinite scroll, custom scroll indicators

---

### 5. Web React State Management with Hooks

**Decision**: Custom `useAnimalList` hook encapsulates state and business logic

**Rationale**:
- React hooks pattern is idiomatic for React 18+
- Separates business logic from UI rendering (testable in isolation)
- Async/await for data fetching (no Promise chains per constitution)
- Hook can be reused across multiple components if needed

**Implementation**:
```typescript
export function useAnimalList() {
    const [animals, setAnimals] = useState<Animal[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    
    const loadAnimals = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const result = await mockAnimalRepository.getAnimals();
            setAnimals(result);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };
    
    useEffect(() => {
        loadAnimals();
    }, []);
    
    const selectAnimal = (id: string) => {
        // Future: navigate('/animal/' + id)
        console.log('Select animal:', id);
    };
    
    return { animals, isLoading, error, loadAnimals, selectAnimal };
}
```

**Test Strategy**:
- Hook tests: React Testing Library's `renderHook`, verify state updates
- Component tests: Render with hook, verify UI reflects hook state
- E2E tests: Playwright verifies full user interaction flow

---

### 6. Empty State Handling Across Platforms

**Decision**: Conditional rendering based on `isEmpty` flag (animals.length === 0 && !isLoading && !error)

**Rationale**:
- Spec defines exact empty state message: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
- Empty state must be distinguishable from loading (spinner) and error (error message)
- All platforms use same logic: show empty state only when data loaded successfully but list is empty

**Implementation**:
- Android: `if (state.isEmpty) EmptyState()` composable
- iOS: SwiftUI `if viewModel.animals.isEmpty && !viewModel.isLoading { EmptyStateView() }`
- Web: `{animals.length === 0 && !isLoading && <EmptyState />}`

**Visual Design**:
- Empty state message (from spec clarification)
- Optional icon/illustration (to be determined by designer)
- "Report a Missing Animal" button remains visible (call-to-action)

---

### 7. Test Identifier Naming Convention

**Decision**: Use `{screen}.{element}.{action}` format across all platforms

**Rationale**:
- Constitution mandates stable test identifiers for E2E tests
- Consistent naming across Android (testTag), iOS (accessibilityIdentifier), Web (data-testid)
- Screen-prefixed IDs prevent collisions across features
- Action suffix (e.g., `.click`) documents interaction intent

**Implementation**:
```kotlin
// Android
LazyColumn(modifier = Modifier.testTag("animalList.list")) {
    items(animals) { animal ->
        AnimalCard(
            animal = animal,
            modifier = Modifier.testTag("animalList.item.${animal.id}")
        )
    }
}
Button(
    onClick = { /*...*/ },
    modifier = Modifier.testTag("animalList.reportMissingButton.click")
)
```

```swift
// iOS - Using LazyVStack for better performance with large lists
ScrollView {
    LazyVStack(spacing: 8) {
        ForEach(animals) { animal in
            AnimalCardView(animal: animal)
                .accessibilityIdentifier("animalList.item.\(animal.id)")
        }
    }
    .accessibilityIdentifier("animalList.list")
}

Button("Report a Missing Animal") { /*...*/ }
    .accessibilityIdentifier("animalList.reportMissingButton.click")
```

```tsx
// Web
<ul data-testid="animalList.list">
    {animals.map(animal => (
        <li key={animal.id} data-testid={`animalList.item.${animal.id}`}>
            <AnimalCard animal={animal} />
        </li>
    ))}
</ul>
<button data-testid="animalList.reportMissingButton.click">
    Report a Missing Animal
</button>
```

**E2E Test Usage**:
- Playwright: `await page.locator('[data-testid="animalList.list"]').waitFor()`
- Appium: `await driver.$('~animalList.list').waitForDisplayed()`

---

### 8. Visual Design Implementation from Figma

**Decision**: Extract exact values from Figma design specs (colors, typography, spacing)

**Rationale**:
- Spec includes complete design specifications from Figma (colors, fonts, spacing)
- 100% visual accuracy is a success criterion (SC-002)
- Hardcoded values ensure pixel-perfect implementation
- Design tokens/theme system can be refactored later

**Key Values from Spec**:
- **Colors**:
  - Primary text: `#2D2D2D`
  - Secondary text: `#545F71`
  - Tertiary text: `#93A2B4`
  - Background: `#FAFAFA`
  - Placeholder: `#EEEEEE`
  - Status Missing: `#FF0000`
  - Status Found: `#0074FF`
  - Primary button: `#2D2D2D`
  - Secondary button: `#E5E9EC`
  
- **Typography**:
  - Screen title: Inter 24px Regular
  - Card species: Inter 16px Regular
  - Card breed: Inter 14px Regular
  - Card location: Inter 13px Regular
  - Status badge: Roboto 12px Regular
  - Button labels: Inter 16px Regular

- **Spacing**:
  - Card gap: 8px
  - Card padding: 16px horizontal
  - Card border radius: 4px
  - Badge radius: 10px
  - Button radius: 2px
  - Card shadow: `0px 1px 4px 0px rgba(0,0,0,0.05)`

**Implementation Strategy**:
- Android: Define `Color`, `Typography`, and `Dimens` in theme files
- iOS: Define color/font extensions in `DesignSystem.swift`
- Web: CSS custom properties or styled-components theme
- All values match Figma exactly (no approximations)

---

## Best Practices Applied

### 1. Given-When-Then Test Structure

All tests follow mandated GWT structure:
- **Given**: Setup initial state (mock repositories, test data)
- **When**: Execute action (load animals, dispatch intent, tap button)
- **Then**: Verify expected outcome (state updated, effect emitted, UI rendered)

Example (Android):
```kotlin
@Test
fun `should display animals when repository returns data`() = runTest {
    // Given - mock repository with test data
    val mockAnimals = listOf(
        Animal(id = "1", name = "Max", species = Species.DOG, ...),
        Animal(id = "2", name = "Luna", species = Species.CAT, ...)
    )
    coEvery { mockRepository.getAnimals() } returns Result.success(mockAnimals)
    
    // When - ViewModel dispatches Refresh intent
    viewModel.dispatchIntent(AnimalListIntent.Refresh)
    
    // Then - state contains animals and loading is false
    val state = viewModel.state.first()
    assertEquals(2, state.animals.size)
    assertFalse(state.isLoading)
    assertNull(state.error)
}
```

### 2. Documentation for Public APIs

All public APIs documented with concise, high-level comments:

```kotlin
/**
 * Retrieves list of animals from repository.
 * Returns mock data until backend integration is implemented.
 */
class GetAnimalsUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(): Result<List<Animal>> =
        repository.getAnimals()
}

/**
 * Repository interface for animal data operations.
 * Mock implementation provides hardcoded test data.
 * Future implementation will fetch from REST API.
 */
interface AnimalRepository {
    /** Fetches all animals. Returns Result.success with list or Result.failure on error. */
    suspend fun getAnimals(): Result<List<Animal>>
}
```

### 3. Dependency Injection with Koin

All dependencies injected via Koin modules:

```kotlin
// Shared domain module
val domainModule = module {
    factory { GetAnimalsUseCase(get()) }
}

// Android data module
val dataModule = module {
    single<AnimalRepository> { MockAnimalRepository() }
}

// Android ViewModel module
val viewModelModule = module {
    viewModel { AnimalListViewModel(get()) }
}
```

### 4. Asynchronous Operations

Modern async patterns enforced:
- Shared: `suspend fun getAnimals(): Result<List<Animal>>`
- Android: `viewModelScope.launch { }` with Flow/StateFlow
- iOS: `async func loadAnimals()` with `@MainActor`
- Web: `async function loadAnimals()` with native async/await

No callbacks, RxJava, Combine, or Promise chains used.

---

## Migration Path from Mock to Real Implementation

When backend API is ready:

1. **Shared Module**: Add network client dependency (Ktor or similar)
2. **Create RealAnimalRepository**: Implement `AnimalRepository` interface with HTTP calls
3. **Update Koin Modules**: Replace `MockAnimalRepository` with `RealAnimalRepository` in DI
4. **Add Error Handling**: Map HTTP errors to domain errors (network timeout, 404, 500, etc.)
5. **Add Loading States**: Ensure UI handles loading/error states from real API
6. **Update Tests**: Add integration tests with mock HTTP server (MockWebServer, WireMock)
7. **No UI Changes Required**: ViewModels and UI remain unchanged (interface-based design)

---

## Risks & Mitigation

| Risk | Mitigation |
|------|-----------|
| Mock data not representative of real API | Design mock data structure based on backend spec; validate with backend team |
| Navigation architecture mismatch | Use effect/callback patterns that map cleanly to real nav routers |
| Platform-specific UI quirks | Reference Figma design; conduct visual QA on all three platforms |
| Test identifiers change during implementation | Review checklist in constitution; enforce in code review |
| 80% coverage not achieved | Write tests in TDD fashion (test first); measure coverage frequently |

---

## Summary

All design decisions align with project constitution and spec requirements:
- ✅ Thin shared layer (domain models, repository interfaces, use cases only)
- ✅ Native presentation (Android MVI, iOS MVVM-C, Web React hooks)
- ✅ Mock data strategy enables UI development without backend
- ✅ Navigation prepared for future integration (effects/callbacks)
- ✅ Test identifiers, documentation, GWT structure enforced
- ✅ Visual design matches Figma specs (100% accuracy)
- ✅ 80% test coverage planned for all modules
- ✅ E2E tests cover all user stories

**Status**: Research complete. Ready for Phase 1 (Data Model & Contracts).

