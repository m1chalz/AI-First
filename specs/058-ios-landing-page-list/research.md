# Research: iOS Landing Page (Home Tab)

**Branch**: `058-ios-landing-page-list` | **Date**: 2025-12-16

This document captures research findings for implementing the iOS landing page feature with coordinator-based cross-tab navigation, component reuse, and client-side data filtering.

---

## 1. Cross-Tab Navigation in iOS Coordinator Pattern

### Decision
HomeCoordinator will communicate with TabBarCoordinator via a closure/callback pattern to trigger programmatic tab switching combined with detail screen navigation.

### Rationale
- iOS coordinator pattern typically uses parent-child relationships where child coordinators communicate upward via closures or protocols
- TabBarCoordinator (parent) manages UITabBarController and owns all tab-specific child coordinators (HomeCoordinator, AnnouncementsCoordinator, etc.)
- When user taps an announcement on Home tab, HomeCoordinator invokes a closure that TabBarCoordinator provides during initialization
- TabBarCoordinator handles the cross-tab navigation: (1) switch selectedIndex to Lost Pets tab, (2) call AnnouncementsCoordinator's showPetDetails method
- This approach maintains separation of concerns - HomeCoordinator doesn't need direct reference to AnnouncementsCoordinator

### Alternatives Considered
1. **Direct coordinator reference**: HomeCoordinator holds reference to AnnouncementsCoordinator
   - Rejected: Creates tight coupling between coordinators, violates coordinator independence principle
2. **Notification-based**: Use NotificationCenter for cross-coordinator communication
   - Rejected: Less type-safe, harder to trace navigation flow, introduces implicit dependencies
3. **Shared navigation service**: Singleton service that all coordinators access
   - Rejected: Introduces global state, makes testing harder, violates coordinator pattern principles

### Implementation Pattern
```swift
// TabCoordinator creates HomeCoordinator directly (same pattern as AnnouncementListCoordinator)
class TabCoordinator {
    private let _tabBarController: UITabBarController
    private var childCoordinators: [CoordinatorInterface] = []
    
    init() {  // NO CHANGES to signature
        _tabBarController = UITabBarController()
        
        // BEFORE: PlaceholderCoordinator(title: L10n.Tabs.home)
        // AFTER: Real HomeCoordinator (created directly, same as AnnouncementListCoordinator)
        let homeCoordinator = HomeCoordinator(
            onShowPetDetails: { [weak self] announcementId in
                self?.showPetDetailsFromHome(announcementId)
            }
        )
        
        let lostPetCoordinator = AnnouncementListCoordinator()  // Same pattern
        // ... other tabs
        
        childCoordinators = [homeCoordinator, lostPetCoordinator, /* ... */]
    }
    
    // NEW: Handle cross-tab navigation from Home to Pet Details
    private func showPetDetailsFromHome(_ announcementId: String) {
        // Switch to Lost Pets tab (index 1)
        _tabBarController.selectedIndex = 1
        
        // Trigger detail screen on AnnouncementListCoordinator
        if let lostPetCoordinator = childCoordinators[1] as? AnnouncementListCoordinator {
            lostPetCoordinator.showPetDetails(for: announcementId)
        }
    }
}

// HomeCoordinator follows same pattern as AnnouncementListCoordinator
class HomeCoordinator {
    private let navigationController: UINavigationController
    private let onShowPetDetails: (String) -> Void
    
    // Simple init - just stores closure, no dependencies yet
    init(onShowPetDetails: @escaping (String) -> Void) {
        self.navigationController = UINavigationController()
        self.onShowPetDetails = onShowPetDetails
    }
    
    func start(animated: Bool) async {
        // Get dependencies from DI container (same as AnnouncementListCoordinator)
        let container = ServiceContainer.shared
        let repository = container.announcementRepository
        let locationHandler = container.locationPermissionHandler
        
        // Create ViewModel with dependencies
        let viewModel = LandingPageViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnnouncementTapped: onShowPetDetails
        )
        
        // Create view and show...
    }
}
```

---

## 2. Extracting Shared Announcement List Component

### Decision
Create a new shared component **`AnnouncementCardsListView`** that encapsulates the common list rendering logic. This component will be reused by both `AnnouncementListView` (full list) and `LandingPageView` (5 most recent).

### Rationale
- **DRY Principle**: Announcement card list rendering appears in two places (full list screen + landing page). Extracting shared logic prevents code duplication.
- **Single Source of Truth**: List layout, spacing, loading/error/empty states, and card rendering logic exist in one place. Design changes propagate automatically.
- **Separation of Concerns**: 
  - `AnnouncementListView` handles: floating action buttons, permission popups, search placeholder, foreground refresh
  - `LandingPageView` handles: Home tab specific navigation, cross-tab routing
  - `AnnouncementCardsListView` handles: pure list rendering, states (loading/error/empty), card display
- **Testability**: Shared component can be unit tested independently with preview parameter providers
- **Composability**: Both parent views compose the shared list component with their specific wrappers (navigation, buttons, etc.)

### Alternatives Considered
1. **Duplicate list rendering in both views**: Copy-paste ScrollView + LazyVStack logic
   - Rejected: Violates DRY, synchronization nightmare when design changes, increases maintenance burden
2. **Reuse full AnnouncementListView**: Use existing view in landing page
   - Rejected: AnnouncementListView contains feature-specific logic (floating buttons, permission popups, search) that doesn't belong on landing page
3. **Protocol-based abstraction**: Define protocol for list data source
   - Rejected: Over-engineering for this use case, adds unnecessary complexity

### Component Structure

**New Shared Component**: `AnnouncementCardsListView` (Stateless, Reusable)

```swift
/// Shared stateless component for rendering announcement card lists.
/// Reusable in multiple contexts (full list, landing page, search results, etc.).
struct AnnouncementCardsListView: View {
    // MARK: - Input Properties
    let cardViewModels: [AnnouncementCardViewModel]
    let isLoading: Bool
    let errorMessage: String?
    let emptyStateModel: EmptyStateViewModel
    let onRetry: (() -> Void)?
    let listAccessibilityId: String
    
    // MARK: - Computed Properties
    private var isEmpty: Bool {
        cardViewModels.isEmpty && !isLoading && errorMessage == nil
    }
    
    // MARK: - Body
    var body: some View {
        if isLoading {
            LoadingView(model: .init(
                message: L10n.AnnouncementList.Loading.message,
                accessibilityIdentifier: "\(listAccessibilityId).loading"
            ))
        } else if let errorMessage = errorMessage {
            ErrorView(model: .init(
                title: L10n.AnnouncementList.Error.title,
                message: errorMessage,
                onRetry: onRetry,
                accessibilityIdentifier: "\(listAccessibilityId).error"
            ))
        } else if isEmpty {
            EmptyStateView(model: emptyStateModel)
        } else {
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(cardViewModels, id: \.id) { cardViewModel in
                        AnnouncementCardView(viewModel: cardViewModel)
                    }
                }
                .padding(.horizontal, 16)
            }
            .accessibilityIdentifier("\(listAccessibilityId).list")
        }
    }
}
```

**Usage in AnnouncementListView** (refactored):

```swift
struct AnnouncementListView: View {
    @ObservedObject var viewModel: AnnouncementListViewModel
    
    var body: some View {
        ZStack {
            Color(hex: "#FAFAFA").ignoresSafeArea()
            
            // Shared list component (replaces lines 28-69 of current implementation)
            AnnouncementCardsListView(
                cardViewModels: viewModel.cardViewModels,
                isLoading: viewModel.isLoading,
                errorMessage: viewModel.errorMessage,
                emptyStateModel: .default,
                onRetry: {
                    Task { await viewModel.loadAnnouncements() }
                },
                listAccessibilityId: "animalList"
            )
            
            // Feature-specific overlay: floating buttons, search placeholder, etc.
            floatingButtonsSection
        }
        .alert(...) // Permission popup (feature-specific)
    }
}
```

**Usage in LandingPageView** (new implementation):

```swift
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        NavigationView {
            AnnouncementCardsListView(
                cardViewModels: viewModel.cardViewModels,
                isLoading: viewModel.isLoading,
                errorMessage: viewModel.errorMessage,
                emptyStateModel: .init(
                    title: "No recent announcements",
                    description: "Check back later for pet reports in your area",
                    imageName: "tray.fill",
                    accessibilityIdentifier: "landingPage.emptyState"
                ),
                onRetry: {
                    Task { await viewModel.loadAnnouncements() }
                },
                listAccessibilityId: "landingPage"
            )
            .navigationTitle("Recent Pets")
            .task {
                await viewModel.loadAnnouncements()
            }
        }
    }
}
```

### ViewModel Strategy - Autonomous Component Pattern

**Three-Layer ViewModel Architecture**:

1. **AnnouncementCardsListViewModel** (NEW, shared): Autonomous list component ViewModel
   - Manages: `cardViewModels`, `isLoading`, `errorMessage` (internal state)
   - Accepts: `AnnouncementRepositoryProtocol`, `AnnouncementListQuery` (configuration)
   - Responsibilities: Load announcements, handle errors, create card ViewModels
   - Reusable: Works for any announcement list context (full list, landing, search, etc.)

2. **AnnouncementListViewModel**: Parent ViewModel for full list screen
   - Creates: `AnnouncementCardsListViewModel` instance (with query: limit=nil)
   - Manages: Location permissions, foreground refresh, floating button actions, permission popups
   - Delegates: List rendering and data fetching to AnnouncementCardsListViewModel
   - Can trigger: `listViewModel.reload()` when returning from background

3. **LandingPageViewModel**: Parent ViewModel for landing page screen
   - Creates: `AnnouncementCardsListViewModel` instance (with query: limit=5)
   - Manages: Cross-tab navigation closure, Home tab specific logic
   - Delegates: List rendering and data fetching to AnnouncementCardsListViewModel
   - Can trigger: `listViewModel.reload()` if needed

**Component Composition**:

```swift
// Parent ViewModel (Landing Page)
@MainActor
class LandingPageViewModel: ObservableObject {
    let listViewModel: AnnouncementCardsListViewModel  // Child component ViewModel
    private let locationHandler: LocationPermissionHandler  // Parent handles location
    
    init(repository: AnnouncementRepositoryProtocol, 
         locationHandler: LocationPermissionHandler,
         onAnnouncementTapped: @escaping (String) -> Void) {
        
        self.locationHandler = locationHandler
        
        // Create child list ViewModel with initial empty query
        self.listViewModel = AnnouncementCardsListViewModel(
            repository: repository,
            query: .landingPageQuery(location: nil),
            onAnnouncementTapped: onAnnouncementTapped
        )
        
        // Note: loadData() is called from View .task, not here
    }
    
    func loadData() async {
        // Parent handles location permissions
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Set query on child ViewModel (triggers automatic reload)
        let queryWithLocation = AnnouncementListQuery.landingPageQuery(location: result.location)
        listViewModel.setQuery(queryWithLocation)
    }
}

// Autonomous List ViewModel (shared) - NO location handling, receives location via query
@MainActor
class AnnouncementCardsListViewModel: ObservableObject {
    @Published private(set) var cardViewModels: [AnnouncementCardViewModel] = []
    @Published private(set) var isLoading: Bool = false
    @Published private(set) var errorMessage: String?
    
    private let repository: AnnouncementRepositoryProtocol
    private var query: AnnouncementListQuery  // Mutable - parent can update
    private let onAnnouncementTapped: (String) -> Void
    
    init(repository: AnnouncementRepositoryProtocol,
         query: AnnouncementListQuery,
         onAnnouncementTapped: @escaping (String) -> Void) {
        self.repository = repository
        self.query = query
        self.onAnnouncementTapped = onAnnouncementTapped
    }
    
    func setQuery(_ newQuery: AnnouncementListQuery) {
        self.query = newQuery
        loadTask?.cancel()
        loadTask = Task { await loadAnnouncements() }
    }
    
    func reload() {
        loadTask?.cancel()
        loadTask = Task { await loadAnnouncements() }
    }
    
    private func loadAnnouncements() async {
        // Fetch announcements with location from query (parent prepared location)
        let announcements = try await repository.getAnnouncements(near: query.location)
        
        // Apply query filters (limit, sort), update state...
    }
}

// Query Configuration
struct AnnouncementListQuery {
    let limit: Int?       // nil = all items, 5 = landing page limit
    let sortBy: SortOption
    let location: Coordinate?  // Parent provides location (nil = no location)
    
    enum SortOption {
        case createdAtDescending
        case createdAtAscending
        case distanceFromUser
    }
    
    static func landingPageQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(limit: 5, sortBy: .createdAtDescending, location: location)
    }
    
    static func defaultQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(limit: nil, sortBy: .createdAtDescending, location: location)
    }
}
```

**Usage in Views**:

```swift
// Landing Page View
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        NavigationView {
            AnnouncementCardsListView(viewModel: viewModel.listViewModel)
                .navigationTitle("Recent Pets")
        }
    }
}

// Announcement List View (refactored)
struct AnnouncementListView: View {
    @ObservedObject var viewModel: AnnouncementListViewModel
    
    var body: some View {
        ZStack {
            AnnouncementCardsListView(viewModel: viewModel.listViewModel)
            floatingButtonsSection
        }
        .alert(...) // Permission popup (parent-specific)
        .onAppear {
            // Parent handles foreground refresh trigger
            if needsRefresh { viewModel.listViewModel.reload() }
        }
    }
}
```

**Rationale for Three-Layer Architecture**:

✅ **Autonomous Component**: AnnouncementCardsListViewModel is self-contained (state + behavior), no Combine dependency
✅ **Separation of Concerns**: 
  - Parent ViewModels: Feature-specific logic (location permissions, navigation, buttons, popups)
  - Child ViewModel: Pure list logic (fetch with query.location, filter, sort, error handling)
✅ **Reusability**: AnnouncementCardsListViewModel works in any context with different queries
✅ **Testability**: Child ViewModel can be unit tested independently with mock repository and query
✅ **Composition**: Parent ViewModels prepare complete query (including location) and pass to child
✅ **Query-Based Configuration**: Same component, different behavior via query params (limit, sort, location)
✅ **Simple Dependencies**: No closure injection, no Combine - just Foundation + query parameter
✅ **Update Pattern**: Parent can update child query when location changes (e.g., foreground refresh)
```

---

## 3. Client-Side Filtering and Sorting in Swift

### Decision
LandingPageViewModel will perform client-side sorting (by createdAt descending) and filtering (limit to first 5 items) on the announcements array returned from AnnouncementRepository.

### Rationale
- Backend GET /api/v1/announcements returns all announcements without pagination parameters in this iteration (per FR-002a)
- Swift standard library provides efficient `sorted(by:)` and `prefix()` methods for array manipulation
- Sorting by `createdAt` field (newest first) ensures users see most recent pet reports
- Taking first 5 items after sorting limits display to match requirement FR-005
- Client-side approach is acceptable for low-to-medium data volumes (expected < 100 announcements in typical usage)
- Future optimization: Backend pagination can be added later without breaking iOS client (additive change)

### Alternatives Considered
1. **Backend filtering**: Add query parameters `?sort=createdAt:desc&limit=5` to GET /api/v1/announcements
   - Rejected: Requires backend API changes (out of scope for this iOS-only feature), backend spec says all announcements returned
2. **Manual loop-based sorting**: Implement custom sorting algorithm
   - Rejected: Reinventing the wheel, Swift's `sorted(by:)` is optimized and well-tested

### Implementation Pattern
```swift
class LandingPageViewModel: ObservableObject {
    @Published private(set) var announcements: [Announcement] = []
    private let repository: AnnouncementRepository
    
    @MainActor
    func loadAnnouncements() async {
        isLoading = true
        errorMessage = nil
        
        do {
            let allAnnouncements = try await repository.fetchAnnouncements()
            
            // Sort by creation date (newest first) and take first 5
            announcements = allAnnouncements
                .sorted { $0.createdAt > $1.createdAt }
                .prefix(5)
                .map { $0 }  // Convert Slice to Array
            
            isLoading = false
        } catch {
            errorMessage = "Failed to load announcements: \(error.localizedDescription)"
            isLoading = false
        }
    }
}
```

**Performance Note**: For 100 announcements, sorting + prefix is O(n log n) ≈ 664 operations. This is negligible on modern iOS devices and does not require optimization (per constitution Principle XIV - performance is not a concern).

---

## 4. Location Permission Handling in iOS

### Decision
LandingPageViewModel will check location permission state via existing LocationService and conditionally display location coordinates for announcements when permission is granted.

### Rationale
- iOS requires explicit user permission to access location via CLLocationManager
- Existing LocationService (from previous features) encapsulates permission checking and location fetching
- When permission granted: ViewModel fetches user location and passes it to AnnouncementCard for location coordinate display
- When permission denied: ViewModel passes `nil` location to AnnouncementCard, which hides location coordinates
- This approach matches existing behavior in full announcement list (requirement FR-013, FR-014)

### Alternatives Considered
1. **Always show location with fallback**: Display "Unknown location" text when permission denied
   - Rejected: UX guideline is to hide location coordinates entirely when unavailable, not show placeholder text
2. **Request permission on Home tab load**: Prompt user for location permission when landing page appears
   - Rejected: Permission prompts should be contextual (when user explicitly needs location feature), not automatic on tab load

### Implementation Pattern
```swift
class LandingPageViewModel: ObservableObject {
    @Published private(set) var userLocation: CLLocation?
    private let locationService: LocationService
    
    @MainActor
    func loadAnnouncements() async {
        // Check location permission and fetch if granted
        if locationService.hasLocationPermission {
            userLocation = try? await locationService.getCurrentLocation()
        } else {
            userLocation = nil
        }
        
        // Fetch announcements...
    }
}

// AnnouncementCard handles nil location
struct AnnouncementCard: View {
    let announcement: Announcement
    let userLocation: CLLocation?  // Optional location
    
    var body: some View {
        // Show location coordinates only if userLocation is available
        if let userLocation = userLocation {
            Text(announcement.locationText)
        }
    }
}
```

---

## 5. Swift Concurrency Patterns for ViewModel Async Loading

### Decision
LandingPageViewModel will use Swift Concurrency (`async`/`await`) with `@MainActor` annotation to ensure UI state updates occur on the main thread.

### Rationale
- Swift Concurrency is the mandated async pattern for iOS (per constitution - no Combine for new code)
- `@MainActor` annotation ensures all `@Published` property updates happen on main thread (required for SwiftUI observation)
- Repository methods return `async throws` results, which ViewModel awaits
- Clean error handling via `do-catch` blocks with proper error state updates
- Task lifecycle managed automatically by Swift runtime (cancellation when ViewModel deallocates)

### Alternatives Considered
1. **Combine framework**: Use `AnyCancellable` and publishers for async operations
   - Rejected: Constitution explicitly prohibits Combine for new code, requires Swift Concurrency
2. **Completion handler callbacks**: Traditional closure-based async pattern
   - Rejected: Less readable than async/await, harder to handle errors, deprecated pattern in modern Swift

### Implementation Pattern
```swift
@MainActor
class LandingPageViewModel: ObservableObject {
    @Published private(set) var announcements: [Announcement] = []
    @Published private(set) var isLoading: Bool = false
    @Published private(set) var errorMessage: String?
    
    private let repository: AnnouncementRepository
    private let locationService: LocationService
    
    init(repository: AnnouncementRepository, locationService: LocationService) {
        self.repository = repository
        self.locationService = locationService
    }
    
    func loadAnnouncements() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Fetch location if permission granted
            let location = locationService.hasLocationPermission 
                ? try? await locationService.getCurrentLocation() 
                : nil
            
            // Fetch announcements
            let allAnnouncements = try await repository.fetchAnnouncements()
            
            // Filter and sort
            announcements = allAnnouncements
                .sorted { $0.createdAt > $1.createdAt }
                .prefix(5)
                .map { $0 }
            
            userLocation = location
            isLoading = false
        } catch {
            errorMessage = "Failed to load announcements: \(error.localizedDescription)"
            isLoading = false
        }
    }
}

// SwiftUI View calls async method in task
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        // UI code...
        .task {
            await viewModel.loadAnnouncements()
        }
    }
}
```

**Note**: `.task` modifier automatically cancels the task when the view disappears, preventing memory leaks and unnecessary network calls.

---

## 6. Manual Dependency Injection with ServiceContainer

### Decision
ServiceContainer will register LandingPageViewModel and HomeCoordinator with constructor injection, providing AnnouncementRepository and LocationService dependencies.

### Rationale
- iOS architecture mandates manual DI via ServiceContainer (no frameworks like Koin or Swinject)
- ServiceContainer uses constructor injection pattern for explicit dependency passing
- LandingPageViewModel dependencies: AnnouncementRepository (data fetching), LocationService (location permission/fetching)
- HomeCoordinator dependencies: UINavigationController (navigation stack), closure for cross-tab navigation, LandingPageViewModel (view creation)
- Constructor injection makes dependencies explicit, improves testability (easy to inject mocks)

### Alternatives Considered
1. **Property injection**: Set dependencies after object creation via public properties
   - Rejected: Dependencies can be nil during initialization, less type-safe, harder to enforce required dependencies
2. **Service locator pattern**: ViewModels fetch dependencies from global container
   - Rejected: Hides dependencies, makes testing harder, violates explicit dependency principle

### Implementation Pattern
```swift
class ServiceContainer {
    // Existing services
    let announcementRepository: AnnouncementRepository
    let locationService: LocationService
    
    init() {
        // Initialize existing services
        self.announcementRepository = AnnouncementRepositoryImpl(/* ... */)
        self.locationService = LocationService()
    }
    
    // Factory method for LandingPageViewModel
    func makeLandingPageViewModel() -> LandingPageViewModel {
        return LandingPageViewModel(
            repository: announcementRepository,
            locationService: locationService
        )
    }
    
    // Factory method for HomeCoordinator
    func makeHomeCoordinator(
        navigationController: UINavigationController,
        onShowPetDetails: @escaping (String) -> Void
    ) -> HomeCoordinator {
        return HomeCoordinator(
            navigationController: navigationController,
            viewModelFactory: makeLandingPageViewModel,  // Pass factory closure
            onShowPetDetails: onShowPetDetails
        )
    }
}

// TabBarCoordinator uses ServiceContainer to create HomeCoordinator
class TabBarCoordinator {
    private let serviceContainer: ServiceContainer
    private var homeCoordinator: HomeCoordinator?
    
    init(serviceContainer: ServiceContainer, /* ... */) {
        self.serviceContainer = serviceContainer
    }
    
    func start() {
        let homeNavController = UINavigationController()
        homeCoordinator = serviceContainer.makeHomeCoordinator(
            navigationController: homeNavController,
            onShowPetDetails: { [weak self] announcementId in
                self?.showPetDetailsFromHome(announcementId)
            }
        )
        homeCoordinator?.start()
    }
}
```

---

## Summary

All technical clarifications have been resolved. The implementation approach leverages existing iOS MVVM-C patterns with a key architectural improvement: **extraction of a shared list component** (`AnnouncementCardsListView`) that eliminates code duplication between the full announcement list and the new landing page.

**Key Decisions**:
1. ✅ **Autonomous Component Pattern**: `AnnouncementCardsListViewModel` is self-contained (state + behavior), receives complete query from parent
2. ✅ **Query-Based Configuration**: `AnnouncementListQuery` contains limit, sortBy, and location - parent prepares complete query
3. ✅ **Location Handling**: Parent ViewModels (LandingPage, AnnouncementList) fetch location and set child query via `setQuery()`
4. ✅ **Loading Flow**: Parent View `.task` → Parent ViewModel `loadData()` → Child ViewModel `setQuery()` → automatic reload (private `loadAnnouncements()`)
5. ✅ **No Closure Injection**: Child ViewModel receives query parameter (simple, testable), no location closure complexity
6. ✅ **Private Implementation**: `loadAnnouncements()` is private, only `setQuery()` and `reload()` are public
7. ✅ **Separate ViewModels**: LandingPageViewModel and AnnouncementListViewModel remain independent with feature-specific logic
8. ✅ **Closure-Based Navigation**: Cross-tab routing via TabBarCoordinator closures (HomeCoordinator → TabBarCoordinator → AnnouncementsCoordinator)
9. ✅ **Client-Side Filtering**: Query-driven sorting and limiting (sort by `createdAt` descending + limit to 5 items)
10. ✅ **Swift Concurrency**: async/await with @MainActor (no Combine, only Foundation)
11. ✅ **Manual DI**: ServiceContainer with constructor injection (no frameworks)

**Architectural Benefits**:
- **Autonomy**: List component is self-contained, receives query, manages own loading/error/success states
- **DRY**: List logic exists in one place (AnnouncementCardsListViewModel)
- **Query-Driven**: Same component, different behavior via query configuration (limit: 5 vs. nil, location: Coordinate? vs. nil)
- **Clear Loading Flow**: Parent View `.task` → Parent ViewModel `loadData()` → Child ViewModel `setQuery()` → automatic reload
- **Testability**: Autonomous ViewModel easily unit tested with mock repository and query parameters (no closure mocking)
- **Separation**: Parent ViewModels handle location permissions/fetching, child handles list rendering
- **Simplicity**: No Combine, no closure injection for location - just query parameter and setQuery() method
- **Private Implementation**: `loadAnnouncements()` is private - only `setQuery()` (from parent) and `reload()` (from retry) are public
- **Update Pattern**: Parent can update child query dynamically via `setQuery()` (e.g., when location changes on foreground return)
- **Composability**: Parents compose child ViewModel via factory pattern (ServiceContainer)
- **Reusability**: Works in any context with different query configurations

**Component Lifecycle & Loading Flow**:
```
LandingPageView (UI)
  └── .task { await viewModel.loadData() }
      └── LandingPageViewModel.loadData()
          ├── fetches location (LocationPermissionHandler)
          └── calls listViewModel.setQuery(queryWithLocation)
              └── AnnouncementCardsListViewModel.setQuery()
                  └── triggers loadAnnouncements() (private)
                      └── updates @Published state (cardViewModels, isLoading, errorMessage)
                          └── AnnouncementCardsListView (UI) observes changes
```

**Flow Highlights**:
- Parent View (Landing/AnnouncementList) has `.task` that calls parent ViewModel's `loadData()`
- Parent ViewModel fetches location and prepares complete query
- Parent calls `listViewModel.setQuery()` which automatically triggers reload
- Child ViewModel (AnnouncementCardsListViewModel) has **private** `loadAnnouncements()` - not exposed to parent
- Only public method: `setQuery()` (from parent) and `reload()` (from retry button)

No new dependencies or architectural changes required beyond adding new components to existing iOS structure. Optional refactoring of AnnouncementListView to use the autonomous component pattern is strongly recommended for consistency and to maximize DRY benefits.

