# Research: Pet Details Screen (iOS UI)

**Feature**: 012-ios-pet-details-screen  
**Date**: November 24, 2025  
**Status**: Complete

## Overview

This document consolidates technical decisions and best practices for implementing the iOS Pet Details Screen. All decisions align with the project constitution (v2.0.1) and iOS platform standards.

## Technology Decisions

### Decision 1: iOS MVVM-C Architecture

**What was chosen**: MVVM-Coordinator pattern with UIKit-based coordinators and SwiftUI views

**Rationale**:
- Constitution mandates MVVM-C for iOS features (Principle XI)
- Clear separation of concerns: UIKit coordinators handle navigation, ViewModels manage state, SwiftUI views render UI
- ViewModels call repositories directly (no use cases layer per constitution for iOS)
- Enables testable presentation logic without UIKit dependencies
- Coordinator pattern centralizes navigation flow and enables complex navigation patterns

**Alternatives considered**:
- **Pure SwiftUI navigation (NavigationStack)**: Rejected because constitution mandates UIKit-based coordinators for consistency across iOS codebase
- **Use cases layer between ViewModel and Repository**: Rejected because constitution explicitly states iOS ViewModels should call repositories directly (simpler architecture)
- **VIPER architecture**: Rejected due to unnecessary complexity for typical CRUD operations; MVVM-C provides sufficient separation with less boilerplate

**Implementation approach**:
- `PetDetailsCoordinator`: UIKit-based class managing navigation (push to details screen, handle back navigation)
- `PetDetailsViewModel`: `@MainActor` class conforming to `ObservableObject` with `@Published` properties
- `PetDetailsView`: SwiftUI view observing ViewModel via `@ObservedObject` or `@StateObject`
- ViewModel receives repository via constructor injection from coordinator
- ViewModel communicates with coordinator via closures (e.g., `onBack`, optional navigation callbacks)

---

### Decision 2: Swift Concurrency for Async Operations

**What was chosen**: Swift Concurrency (`async`/`await`) with `@MainActor` for UI updates

**Rationale**:
- Constitution mandates Swift Concurrency for iOS async operations (Principle V)
- Modern, type-safe async pattern built into Swift language
- `@MainActor` ensures UI updates happen on main thread automatically
- Better compiler support and error handling compared to callbacks or Combine
- Simplified testing with async test methods in XCTest

**Alternatives considered**:
- **Combine framework**: Rejected because constitution explicitly prohibits Combine for new code (Principle V)
- **Completion handlers / callbacks**: Rejected as outdated pattern; Swift Concurrency is the modern standard
- **PromiseKit / third-party async libraries**: Rejected to avoid external dependencies when native solution available

**Implementation approach**:
- Repository protocol method: `func getPetDetails(id: String) async throws -> PetDetails`
- ViewModel method: `func loadPetDetails() async` (called in `task` modifier or init)
- Use `@MainActor` attribute on ViewModel class to ensure all `@Published` property updates happen on main thread
- Error handling with `do-catch` blocks, setting error state on `@Published` property

---

### Decision 3: Manual Dependency Injection with ServiceContainer

**What was chosen**: Manual constructor-based dependency injection using ServiceContainer pattern

**Rationale**:
- Constitution mandates manual DI for iOS (Principle IV) - NO third-party DI frameworks
- Zero external dependencies, explicit dependency graph
- Simple singleton ServiceContainer with lazy properties for service instances
- Constructor injection ensures compile-time safety and testability
- Easy to mock dependencies in unit tests

**Alternatives considered**:
- **Swinject or other DI frameworks**: Rejected because constitution explicitly prohibits third-party DI frameworks for iOS
- **Property injection**: Rejected because constructor injection provides better compile-time safety and makes dependencies explicit
- **Service locator without container**: Rejected for lack of centralized configuration and harder testing setup

**Implementation approach**:
- Extend existing `ServiceContainer` with `petRepository` property
- Coordinator receives repository from ServiceContainer during initialization
- Coordinator passes repository to ViewModel via constructor injection
- Unit tests create fake repository implementations and inject into ViewModel
- Example:
  ```swift
  // In ServiceContainer
  lazy var petRepository: PetRepository = PetRepositoryImpl(httpClient: httpClient)
  
  // In Coordinator
  let viewModel = PetDetailsViewModel(repository: ServiceContainer.shared.petRepository)
  ```

---

### Decision 4: Repository Pattern with Protocol

**What was chosen**: Protocol-based repository with mocked data implementation

**Rationale**:
- Constitution mandates interface-based design for repositories (Principle III)
- Repository protocol enables dependency inversion and testability
- Mocked implementation allows UI development before backend endpoint is ready
- Easy to swap mock implementation with real HTTP client implementation later
- Protocol references ensure ViewModel depends on abstraction, not concrete implementation

**Alternatives considered**:
- **Direct HTTP client calls in ViewModel**: Rejected because violates separation of concerns and makes testing harder
- **Concrete repository class without protocol**: Rejected because violates constitution's interface-based design principle
- **Shared repository from Android/KMP**: Rejected because constitution mandates platform-independent implementations (Principle I)

**Implementation approach**:
- Define `PetRepository` protocol in `/iosApp/iosApp/Domain/Repositories/`
- Protocol method: `func getPetDetails(id: String) async throws -> PetDetails`
- Implement `PetRepositoryImpl` in `/iosApp/iosApp/Data/Repositories/` returning hardcoded mock data
- ViewModel depends on protocol type, not concrete implementation
- When backend endpoint `GET /api/v1/announcements/:id` is available, update implementation to call real API

---

### Decision 5: Reusable SwiftUI Components with Model Structs

**What was chosen**: Extract reusable components with dedicated model structs for configuration

**Rationale**:
- Spec requires reusable label-value component and pet photo with badges component (FR-022, FR-024)
- Model structs as nested types (via extension) provide clean API and type safety
- Convenience init extensions accepting `PetDetails` reduce boilerplate at call site
- Follows SwiftUI best practices for component composition
- Improves testability (can test components in isolation with mock models)

**Alternatives considered**:
- **Inline all UI in single view**: Rejected due to poor code organization and reusability
- **Pass individual parameters to components**: Rejected because leads to long parameter lists and harder refactoring
- **Use ViewBuilder closures**: Considered but rejected for this case; model struct approach is more explicit and testable

**Implementation approach**:
- **Component 1: PetPhotoWithBadges**
  - Model struct: `PetPhotoWithBadgesModel(imageUrl: String, status: String, rewardText: String?)`
  - Convenience init: `extension PetPhotoWithBadgesModel { init(from petDetails: PetDetails) }`
  - Component displays AsyncImage with status badge (upper right) and optional reward badge (lower left)
  
- **Component 2: LabelValueRow**
  - Model struct: `LabelValueRowModel(label: String, value: String, valueProcessor: ((String) -> String)? = nil, onTap: (() -> Void)? = nil)`
  - Component displays label (leading, secondary style) and value (trailing, primary style)
  - Optional `valueProcessor` for formatting (e.g., date formatting, phone number formatting)
  - Optional `onTap` closure for interactive values (phone, email)
  
- Both components are stateless and easily unit testable

---

## Best Practices Research

### iOS MVVM-C Patterns

**Key patterns identified**:

1. **Coordinator responsibilities**:
   - Create and configure ViewModels with injected dependencies
   - Create SwiftUI views and wrap in `UIHostingController`
   - Push/present view controllers via `UINavigationController`
   - Handle child coordinator lifecycle (start/finish)
   - Clean up coordinator references when flow completes

2. **ViewModel-Coordinator communication**:
   - Coordinator passes closures to ViewModel during initialization
   - ViewModel invokes closures for navigation events (e.g., `onBack?()`)
   - Avoid direct coordinator references in ViewModel to prevent retain cycles
   - Use `[weak self]` in coordinator closures

3. **ViewModel state management**:
   - Single `@Published` property for complex state or multiple `@Published` properties for simple cases
   - Use Swift enums for mutually exclusive states (loading/loaded/error)
   - Perform async operations in `Task` blocks within ViewModel methods
   - Use `@MainActor` on ViewModel class to ensure main thread updates

**Sources**:
- Constitution Principle XI (iOS MVVM-C Architecture)
- Apple WWDC sessions on SwiftUI + UIKit integration
- iOS community best practices (Coordinator pattern by Soroush Khanlou)

---

### SwiftUI Reusable Components

**Key patterns identified**:

1. **Model-driven components**:
   - Define model struct (often as nested type via extension)
   - Model contains all configuration data (values, closures, formatting rules)
   - Component init accepts model struct
   - Convenience init extensions map domain models to component models

2. **Stateless components**:
   - Prefer stateless components (no `@State` inside)
   - State managed by parent view or ViewModel
   - Easier to test and reason about

3. **Composable design**:
   - Small, focused components with single responsibility
   - Combine via ViewBuilder for complex layouts
   - Use `ViewModifier` for cross-cutting concerns (e.g., accessibility)

**Sources**:
- Apple SwiftUI documentation
- SwiftUI by Example (Paul Hudson)
- Constitution Principle VI (Test Identifiers) and VII (Documentation)

---

### Swift Concurrency Best Practices

**Key patterns identified**:

1. **@MainActor for UI classes**:
   - Mark entire ViewModel class with `@MainActor`
   - All `@Published` properties update on main thread automatically
   - No need for manual `DispatchQueue.main.async` calls

2. **Async init tasks**:
   - Use `.task` modifier on SwiftUI view for async init work
   - Alternatively, create async init method called from synchronous init
   - Task cancellation handled automatically when view disappears

3. **Error handling**:
   - Use `throws` for repository methods
   - Catch errors in ViewModel and update error state
   - Display error state in UI with retry button

4. **Testing async code**:
   - XCTest supports async test methods: `func testFoo() async`
   - Use `await` directly in test methods
   - No need for expectations or waitForExpectations

**Sources**:
- Constitution Principle V (Asynchronous Programming Standards)
- Apple Swift Concurrency documentation (WWDC 2021, 2022)
- Swift.org async/await proposal and migration guide

---

### Manual Dependency Injection in iOS

**Key patterns identified**:

1. **ServiceContainer pattern**:
   - Singleton container with lazy properties for services
   - Each service initialized once and reused
   - Constructor injection pattern: container creates dependencies and passes to constructors

2. **Constructor injection**:
   - Dependencies passed via init parameters
   - Makes dependencies explicit and compile-time checked
   - Enables easy mocking in unit tests

3. **Testing with fake implementations**:
   - Create protocol for each service (e.g., `PetRepository`)
   - Create fake implementation for tests (e.g., `FakePetRepository`)
   - Inject fake via constructor in test setup
   - No need for mocking frameworks (Mockito, etc.)

**Sources**:
- Constitution Principle IV (Dependency Injection)
- iOS community best practices (John Sundell's Swift by Sundell blog)
- Clean Swift architecture resources

---

## Summary

All technical decisions are aligned with project constitution v2.0.1. The implementation will use:

- **Architecture**: MVVM-C with UIKit coordinators, `ObservableObject` ViewModels, SwiftUI views
- **Async**: Swift Concurrency (`async`/`await`, `@MainActor`)
- **DI**: Manual constructor injection via ServiceContainer
- **Data Layer**: Protocol-based repository with mocked implementation
- **UI Components**: Reusable SwiftUI components with model structs

No unresolved clarifications remain. Ready to proceed to Phase 1 (Design & Contracts).

