# PetSpot iOS Constitution

> **Platform**: iOS (`/iosApp`) | **Language**: Swift | **UI**: SwiftUI | **Architecture**: MVVM-C

This document contains all iOS-specific architectural rules and standards. Read this file for iOS-only tasks.

## Build & Test Commands

```bash
# Build iOS
open /iosApp in Xcode and build

# Run iOS tests
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES

# View coverage
Xcode coverage report

# Run SwiftGen (after adding new strings to Localizable.strings)
swiftgen
```

## Technology Stack

- **Target**: iOS 18+
- **Device**: iPhone 16 (for tests and compilation)
- **Language**: Swift
- **UI Framework**: SwiftUI
- **Navigation**: UIKit-based Coordinators
- **Architecture**: MVVM-C (Model-View-ViewModel-Coordinator)
- **Async**: Swift Concurrency (`async`/`await`)
- **DI**: Manual dependency injection (constructor injection)
- **Testing**: XCTest with Swift Concurrency
- **Localization**: SwiftGen

## Core Principles

### Platform Independence

iOS (`/iosApp`) implements its full technology stack independently:

- Domain models (Swift structs/classes)
- Repository implementations (Swift)
- ViewModels with MVVM-C architecture (Swift + SwiftUI) - call repositories directly (NO use cases)
- UI layer (SwiftUI)
- Own dependency injection setup (manual DI with constructor injection)

**Architecture Rules**:
- MUST NOT share compiled code with other platforms
- MAY share design patterns and architectural conventions
- MUST consume backend APIs via HTTP/REST
- MUST be independently buildable, testable, and deployable
- Domain models MAY differ from other platforms based on iOS-specific needs

### 80% Unit Test Coverage (NON-NEGOTIABLE)

iOS MUST maintain minimum 80% unit test coverage:

- **Location**: `/iosApp/iosAppTests/`
- **Framework**: XCTest with Swift Concurrency (async/await)
- **Run command**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- **Report**: Xcode coverage report
- **Scope**: Domain models, ViewModels (ObservableObject with @Published properties)
- **Coverage target**: 80% line + branch coverage
- **Import**: Use `@testable import PetSpot` (NOT `@testable import iosApp`)

**Testing Requirements**:
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure
- MUST use descriptive test names following Swift conventions
- MUST test behavior, not implementation details
- MUST use test doubles (fakes, mocks) for dependencies

### Interface-Based Design (NON-NEGOTIABLE)

Repository protocols and implementations:

**Protocol definition** (`/iosApp/iosApp/Domain/Repositories/`):
```swift
// MUST use "Protocol" suffix
protocol PetRepositoryProtocol {
    func getPets() async throws -> [Pet]
    func getPetById(id: String) async throws -> Pet
}
```

**Implementation** (`/iosApp/iosApp/Data/Repositories/`):
```swift
// WITHOUT suffix - implements protocol
class PetRepository: PetRepositoryProtocol {
    private let httpClient: HTTPClient
    
    init(httpClient: HTTPClient) {
        self.httpClient = httpClient
    }
    
    func getPets() async throws -> [Pet] {
        return try await httpClient.get("/api/pets")
    }
    
    func getPetById(id: String) async throws -> Pet {
        return try await httpClient.get("/api/pets/\(id)")
    }
}
```

**Naming Convention**:
- Domain repository protocols MUST use "Protocol" suffix: `PetRepositoryProtocol`
- Data layer implementations WITHOUT suffix: `PetRepository: PetRepositoryProtocol`

### Dependency Injection (NON-NEGOTIABLE)

iOS MUST use manual dependency injection:

- MUST use manual dependency injection (constructor/initializer injection)
- NO DI frameworks (no Swinject, no third-party DI libraries)
- Rationale: Simplicity, no external dependencies, explicit dependency graph, Swift-native patterns

**Example** (`/iosApp/iosApp/DI/`):
```swift
class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var petRepository: PetRepositoryProtocol = PetRepository(
        httpClient: httpClient
    )
    
    lazy var httpClient: HTTPClient = HTTPClientImpl()
}

// Usage in coordinator
let viewModel = PetListViewModel(
    repository: ServiceContainer.shared.petRepository
)
```

### Asynchronous Programming (NON-NEGOTIABLE)

iOS MUST use Swift Concurrency:

- ViewModels MUST use **Swift Concurrency** (`async`/`await`)
- MUST NOT use Combine framework anywhere in the codebase
- MUST NOT use RxSwift or PromiseKit
- Use `@MainActor` for UI updates

**Example**:
```swift
@MainActor
class PetListViewModel: ObservableObject {
    @Published var pets: [Pet] = []
    @Published var isLoading = false

    private let repository: PetRepositoryProtocol

    func loadPets() async {
        isLoading = true
        defer { isLoading = false }

        do {
            self.pets = try await repository.getPets()
        } catch {
            // Handle error
        }
    }
}
```

**Prohibited Patterns** (NON-NEGOTIABLE):
- ❌ **Combine framework** - DO NOT USE anywhere
- ❌ RxSwift
- ❌ PromiseKit
- ❌ Callbacks (except platform APIs that require them)

### Test Identifiers (NON-NEGOTIABLE)

All interactive UI elements MUST have stable test identifiers:

- Use `.accessibilityIdentifier()` modifier on all interactive views
- Naming convention: `{screen}.{element}` (e.g., `petList.addButton`)

**Example**:
```swift
Button("Add Pet") {
    // action
}
.accessibilityIdentifier("petList.addButton")

List(pets) { pet in
    PetRow(pet: pet)
        .accessibilityIdentifier("petList.item.\(pet.id)")
}
.accessibilityIdentifier("petList.list")
```

**Requirements**:
- MUST be unique within a screen/page
- MUST be stable (not change between test runs)
- MUST NOT use dynamic values EXCEPT for list item IDs
- Lists/collections MUST use stable IDs (database ID, not array index)

### Public API Documentation

**Documentation Requirements**:
- MUST document public APIs ONLY when the name alone is insufficient
- MUST use SwiftDoc format (`/// ...` or `/** ... */`)
- MUST be concise and high-level (focus on WHAT and WHY, not HOW)
- SHOULD be one to three sentences maximum

**GOOD Example**:
```swift
/// Manages pet list state with automatic repository synchronization.
@MainActor
class PetListViewModel: ObservableObject {
    // NO DOCUMENTATION - Property names are clear
    @Published var pets: [Pet] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    // NO DOCUMENTATION - Method name is self-explanatory
    func loadPets() async {
        // implementation
    }
    
    /// Retries failed pet sync with exponential backoff.
    func retrySync() async {
        // implementation
    }
}
```

### Given-When-Then Test Convention (NON-NEGOTIABLE)

All unit tests MUST follow Given-When-Then structure:

```swift
func testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState() async {
    // Given - setup initial state
    let expectedPets = [
        Pet(id: "1", name: "Max", species: .dog),
        Pet(id: "2", name: "Luna", species: .cat)
    ]
    let fakeRepository = FakePetRepository(pets: expectedPets)
    let viewModel = PetListViewModel(repository: fakeRepository)

    // When - perform action
    await viewModel.loadPets()

    // Then - verify results
    XCTAssertEqual(viewModel.pets.count, 2)
    XCTAssertEqual(viewModel.pets.first?.name, "Max")
    XCTAssertFalse(viewModel.isLoading)
}
```

**Test Naming Convention**: `test{Method}_when{Condition}_should{ExpectedBehavior}`

## iOS MVVM-C Architecture (NON-NEGOTIABLE)

All iOS presentation features MUST follow the Model-View-ViewModel-Coordinator (MVVM-C) pattern.

### Core Components

- **Model**: Domain models defined in iOS platform code (Swift structs/classes)
- **View**: SwiftUI views that observe ViewModels via `@ObservedObject` or `@StateObject`
- **ViewModel**: `ObservableObject` classes containing presentation logic and `@Published` state properties
- **Coordinator**: UIKit-based objects managing navigation flow and creating `UIHostingController` instances

### Architecture Rules

1. **Coordinators manage navigation**: All screen transitions, modal presentations, and flow logic
   reside in coordinator classes (UIKit-based). SwiftUI views MUST NOT directly trigger navigation.

2. **ViewModels own presentation state**: All UI-related state (loading flags, data, errors) lives
   in ViewModel `@Published` properties. Views observe and render based on these properties.

3. **ViewModel-Coordinator communication**: ViewModels communicate with coordinators via:
   - Direct method calls (e.g., `coordinator.showDetails(petId:)`)
   - Closure/callback properties set by coordinator during ViewModel initialization

4. **UIHostingController wrapping**: Coordinators create SwiftUI views and wrap them:
   - First in `NavigationBackHiding` wrapper (hides back button when needed)
   - Then in `UIHostingController` for UIKit integration
   - Example: `UIHostingController(rootView: NavigationBackHiding { PetListView(viewModel: viewModel) })`

5. **Navigation title in UIKit**: Navigation bar titles MUST be set in UIKit (coordinator level),
   NOT in SwiftUI views. Use `hostingController.title = "Screen Title"` or set via `UINavigationItem`.

6. **SwiftUI views remain pure**: Views MUST NOT contain business logic, navigation logic, or
   direct repository calls. Views only render UI based on ViewModel state and trigger ViewModel methods.

7. **Coordinator hierarchy**: Parent coordinators manage child coordinators for nested flows.
   Child coordinators notify parents via delegation or closures when flow completes.

8. **NO use cases layer**: iOS ViewModels call repositories directly without use case layer.
   Business logic lives in ViewModels when needed.

### View Naming Convention (MANDATORY)

- SwiftUI view struct names MUST end with `View` suffix (e.g., `PetCardView`, `PetListView`)
- Exception: Only when explicitly indicated otherwise in specification

### View Patterns

**ViewModel pattern** (for full-screen views with dynamic data):
- Use for screens with network calls, complex state, or business logic
- ViewModel is `ObservableObject` with `@Published` properties
- View observes ViewModel via `@StateObject` or `@ObservedObject`
- ViewModel file: `{ViewName}ViewModel.swift` (e.g., `PetListViewModel.swift`)

**Model pattern** (for subviews/components with static data):
- Use for reusable components, list items, cards without own state management
- Define `struct Model` in extension to view struct
- Model MUST be placed in separate file: `{ViewName}_Model.swift`
- Model passed via initializer parameter (no `@Published` properties)

**File Structure Example**:
```
Views/
├── PetCardView.swift           # View struct
├── PetCardView_Model.swift     # Model extension (separate file!)
├── PetListView.swift           # View struct
└── PetListViewModel.swift      # ViewModel (ObservableObject)
```

**PetCardView.swift**:
```swift
struct PetCardView: View {
    let model: Model
    
    var body: some View {
        VStack {
            Text(model.name)
            Text(model.species)
        }
    }
}
```

**PetCardView_Model.swift**:
```swift
extension PetCardView {
    struct Model {
        let name: String
        let species: String
    }
}
```

### Localization (MANDATORY)

- Project MUST use SwiftGen for all displayed text
- ALL user-facing strings MUST be localized (no hardcoded strings in views)
- Access localized strings via SwiftGen-generated code: `L10n.petListTitle`
- String keys defined in `Localizable.strings` files

**String Reuse Policy**:
- **Common strings** (`done`, `ok`, `close`, `cancel`, etc.) - defined once, reused everywhere
- **Screen-specific strings** - each string used only in ONE place (no sharing between screens)

**SwiftGen Workflow**:
1. Add new string to `Localizable.strings` file for ALL languages (files must be kept in sync)
2. Run `swiftgen` command in terminal
3. Access via generated `L10n.yourNewString` constant

**Multi-Language Consistency (MANDATORY)**:
- All `Localizable.strings` files MUST contain the same keys
- When adding a new string, add it to ALL language files simultaneously
- Missing keys in any language file will cause runtime issues

```swift
// Localizable.strings
"common.done" = "Done";
"common.cancel" = "Cancel";
"petList.title" = "My Pets";
"petList.emptyState" = "No pets found";

// Usage in code
Text(L10n.Common.done)       // Reusable common string
Text(L10n.PetList.title)     // Screen-specific string
```

### Presentation Model Extensions (MANDATORY)

- Domain models for presentation MUST have extensions in `/iosApp/iosApp/Features/Shared/`
- Extensions provide formatting, colors, derived properties - NOT in domain model definition
- MUST NOT include localization or color logic directly in domain model definition

### Presentation Layer Independence (MANDATORY)

- ViewModels and Models MUST be independent of SwiftUI presentation layer
- Colors stored as hex strings in models (e.g., `statusColor: "#FF5733"`)
- Presentation layer converts hex to `Color` or `UIColor` when rendering
- Example: `Color(hex: model.statusColor)`

### Data Formatting (MANDATORY)

ALL data formatting logic MUST reside in ViewModels or Models, NOT in SwiftUI views:

**GOOD** (formatting in ViewModel):
```swift
@MainActor
class PetDetailViewModel: ObservableObject {
    @Published var pet: Pet?
    
    var birthDateText: String {
        guard let birthDate = pet?.birthDate else { return L10n.unknown }
        return DateFormatter.shortDate.string(from: birthDate)
    }
    
    var ownerPhoneText: String {
        guard let phone = pet?.ownerPhone else { return L10n.noPhone }
        return formatPhoneNumber(phone)
    }
}

struct PetDetailView: View {
    @ObservedObject var viewModel: PetDetailViewModel
    
    var body: some View {
        VStack {
            Text(viewModel.birthDateText)  // Just display
            Text(viewModel.ownerPhoneText)  // Just display
        }
    }
}
```

**BAD** (formatting in View):
```swift
// ❌ Views MUST NOT contain formatters, calculations, or conditional text logic
struct PetDetailView: View {
    var body: some View {
        if let birthDate = viewModel.pet?.birthDate {
            Text(DateFormatter.shortDate.string(from: birthDate))  // ❌ BAD
        }
    }
}
```

### Full MVVM-C Example

```swift
// ViewModel
@MainActor
class PetListViewModel: ObservableObject {
    @Published var pets: [Pet] = []
    @Published var isLoading = false
    
    // Coordinator callback for navigation
    var onPetSelected: ((String) -> Void)?
    
    private let petRepository: PetRepositoryProtocol
    
    init(petRepository: PetRepositoryProtocol) {
        self.petRepository = petRepository
    }
    
    func loadPets() async {
        isLoading = true
        defer { isLoading = false }
        
        do {
            // iOS ViewModels call repositories directly (NO use cases)
            self.pets = try await petRepository.getPets()
        } catch {
            print("Failed to load pets: \(error)")
        }
    }
    
    func selectPet(id: String) {
        onPetSelected?(id)  // Coordinator handles navigation
    }
}

// Coordinator with manual DI
class PetListCoordinator {
    private let navigationController: UINavigationController
    private let petRepository: PetRepositoryProtocol
    
    init(navigationController: UINavigationController, petRepository: PetRepositoryProtocol) {
        self.navigationController = navigationController
        self.petRepository = petRepository
    }
    
    func start() {
        let viewModel = PetListViewModel(petRepository: petRepository)
        viewModel.onPetSelected = { [weak self] petId in
            self?.showPetDetails(petId: petId)
        }
        
        let view = PetListView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    private func showPetDetails(petId: String) {
        let detailCoordinator = PetDetailCoordinator(
            navigationController: navigationController,
            petRepository: petRepository
        )
        detailCoordinator.start()
    }
}
```

## Module Structure

**`/iosApp/iosApp/`** (iOS - Full Stack):

```
/iosApp/iosApp/
├── Domain/
│   ├── Models/          - Swift structs/classes for domain entities
│   └── Repositories/    - Repository protocols (MUST use "Protocol" suffix)
├── Data/
│   └── Repositories/    - Repository implementations (WITHOUT suffix)
├── Coordinators/        - UIKit-based coordinators managing navigation
├── Views/               - SwiftUI views + ViewModels
│   ├── PetListView.swift
│   ├── PetListViewModel.swift
│   ├── PetCardView.swift
│   └── PetCardView_Model.swift   # Subview models in separate files!
├── Features/
│   └── Shared/          - Presentation model extensions (colors, formatting)
├── DI/                  - Manual dependency injection setup (ServiceContainer)
├── Resources/           - Localizable.strings, assets
└── Generated/           - SwiftGen generated code (L10n)
```

## Testing Standards

### Unit Tests (MANDATORY)

- **Location**: `/iosApp/iosAppTests/`
- **Framework**: XCTest with Swift Concurrency (async/await)
- **Scope**: Domain models, ViewModels (ObservableObject), coordinators (optional)
- **Coverage target**: 80% line + branch coverage
- **Import**: `@testable import PetSpot` (NOT `@testable import iosApp`)
- **Device**: iPhone 16 Simulator

**Requirements**:
- ViewModels MUST have unit tests in `/iosApp/iosAppTests/Features/`
- ViewModel tests MUST verify `@Published` property updates
- ViewModel tests MUST verify coordinator callback invocations
- Model structs (simple data) MAY skip tests if purely data containers

**Test File Template**:
```swift
import XCTest
@testable import PetSpot

final class PetListViewModelTests: XCTestCase {
    func testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState() async {
        // Given
        // When
        // Then
    }
}
```

### E2E Tests (MANDATORY)

- **Framework**: Appium (Java + Cucumber)
- **Test Scenarios**: `/e2e-tests/src/test/resources/features/mobile/*.feature` (Gherkin with `@ios` tag)
- **Screen Object Model**: `/e2e-tests/src/test/java/.../screens/` (Unified for iOS/Android)
  - Uses `@iOSXCUITFindBy(id = "...")` annotations
- **Step Definitions**: `/e2e-tests/src/test/java/.../steps-mobile/`
- **Run command**: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"`
- **Report**: `/e2e-tests/target/cucumber-reports/ios/index.html`

## Compliance Checklist

All iOS pull requests MUST:

- [ ] Run unit tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] Verify 80%+ test coverage in Xcode coverage report
- [ ] Verify tests use `@testable import PetSpot` (NOT `iosApp`)
- [ ] Verify new interactive UI elements have `.accessibilityIdentifier()` modifier
- [ ] Verify screens follow MVVM-C architecture:
  - [ ] UIKit-based coordinators manage navigation
  - [ ] Navigation titles set in UIKit (NOT in SwiftUI views)
  - [ ] ViewModels conform to `ObservableObject` with `@Published` properties
  - [ ] ViewModels communicate with coordinators via methods or closures
  - [ ] SwiftUI views observe ViewModels (no business/navigation logic)
  - [ ] ViewModels call repositories directly (NO use cases)
- [ ] Verify NO Combine framework usage anywhere
- [ ] Verify all new tests follow Given-When-Then structure
- [ ] Verify all user-facing strings use SwiftGen localization (`L10n.xxx`)
- [ ] Verify common strings (done/ok/close/cancel) are reused, screen-specific strings are unique
- [ ] Verify data formatting logic is in ViewModels/Models, not in Views
- [ ] Verify colors are stored as hex strings in models
- [ ] Verify SwiftUI view names end with `View` suffix
- [ ] Verify subview Models are in separate `{ViewName}_Model.swift` files

---

**Version**: 1.1.0 | **Based on Constitution**: v3.0.0

