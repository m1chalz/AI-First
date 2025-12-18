# Quickstart Guide: iOS Landing Page (Home Tab)

**Branch**: `058-ios-landing-page-list` | **Date**: 2025-12-16  
**Audience**: iOS developers implementing this feature

This guide provides step-by-step instructions for implementing the iOS landing page feature on the Home tab.

---

## Prerequisites

Before starting implementation, ensure you have:

1. ✅ **Existing iOS project structure** at `/iosApp`
2. ✅ **Tab navigation** implemented (feature 054-ios-tab-navigation)
3. ✅ **Announcement list components** available for reuse:
   - `AnnouncementCard.swift` (displays announcement details)
   - `EmptyStateView.swift` (no data state)
   - `ErrorView.swift` (error handling)
4. ✅ **Domain models and repositories**:
   - `Announcement.swift` (domain model)
   - `AnnouncementRepository.swift` (protocol)
   - `AnnouncementRepositoryImpl.swift` (implementation)
   - `LocationService.swift` (location permission/fetching)
5. ✅ **Backend API** running with GET /api/v1/announcements endpoint

**Development Environment**:
- Xcode 15+
- iOS 15+ deployment target
- Swift 5.9+

---

## Implementation Steps

### Step 1: Create AnnouncementListQuery Model (10 minutes)

**Location**: `/iosApp/iosApp/Domain/Models/AnnouncementListQuery.swift` (NEW)

**Responsibilities**:
- Define query configuration for announcement list filtering and sorting
- Immutable value type (struct)

**Key Implementation Points**:

```swift
import Foundation

/// Configuration for querying announcement lists with filtering and sorting options.
struct AnnouncementListQuery {
    let limit: Int?
    let sortBy: SortOption
    let location: Coordinate?
    
    enum SortOption {
        case createdAtDescending
        case createdAtAscending
        case distanceFromUser  // Future extension
    }
    
    // MARK: - Convenience Factory Methods
    
    /// Creates default query: all announcements sorted by creation date (newest first)
    static func defaultQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(limit: nil, sortBy: .createdAtDescending, location: location)
    }
    
    /// Creates landing page query: 5 most recent announcements
    static func landingPageQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(limit: 5, sortBy: .createdAtDescending, location: location)
    }
}
```

---

### Step 2: Create AnnouncementCardsListViewModel (40 minutes)

**Location**: `/iosApp/iosApp/ViewModels/AnnouncementCardsListViewModel.swift` (NEW)

**Responsibilities**:
- Autonomous list component ViewModel (self-contained state and behavior)
- Fetch announcements from repository
- Apply query filtering (limit) and sorting
- Convert announcements to card ViewModels
- Fetch user location if permissions granted
- Manage loading, error, and success states
- Handle announcement tap events

**Key Implementation Points**:

```swift
import Foundation

/// Autonomous ViewModel for announcement cards list component.
/// Reusable in multiple contexts (full list, landing page, search, etc.).
/// Location is provided via query parameter (parent prepares complete query).
@MainActor
class AnnouncementCardsListViewModel: ObservableObject {
    // MARK: - Published Properties (UI State)
    @Published private(set) var cardViewModels: [AnnouncementCardViewModel] = []
    @Published private(set) var isLoading: Bool = false
    @Published private(set) var errorMessage: String?
    
    // MARK: - Dependencies
    private let repository: AnnouncementRepositoryProtocol
    private var query: AnnouncementListQuery  // Mutable - can be updated by parent
    private let onAnnouncementTapped: (String) -> Void
    
    // MARK: - Task Management
    private var loadTask: Task<Void, Never>?
    
    // MARK: - Initialization
    init(
        repository: AnnouncementRepositoryProtocol,
        query: AnnouncementListQuery,
        onAnnouncementTapped: @escaping (String) -> Void
    ) {
        self.repository = repository
        self.query = query
        self.onAnnouncementTapped = onAnnouncementTapped
    }
    
    deinit {
        loadTask?.cancel()
    }
    
    // MARK: - Public Methods
    
    /// Sets query and triggers reload (called by parent ViewModel)
    func setQuery(_ newQuery: AnnouncementListQuery) {
        self.query = newQuery
        loadTask?.cancel()
        loadTask = Task { await loadAnnouncements() }
    }
    
    /// Reloads with current query (for retry button in error screen)
    func reload() {
        loadTask?.cancel()
        loadTask = Task { await loadAnnouncements() }
    }
    
    // MARK: - Private Methods
    
    /// Loads announcements from repository, applies query filters/sorting, and creates card ViewModels
    private func loadAnnouncements() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Fetch announcements with location from query (parent prepared location)
            let allAnnouncements = try await repository.getAnnouncements(near: query.location)
            
            // Check for cancellation
            try Task.checkCancellation()
            
            // Apply query filters and sorting
            let processedAnnouncements = applyQuery(to: allAnnouncements)
            
            // Convert to card ViewModels
            updateCardViewModels(with: processedAnnouncements)
            
            isLoading = false
        } catch is CancellationError {
            // Task cancelled - normal, don't show error
        } catch {
            errorMessage = "Failed to load announcements: \(error.localizedDescription)"
            cardViewModels = []
            isLoading = false
        }
    }
    
    // MARK: - Private Methods
    
    /// Applies query configuration (filtering, sorting) to announcements
    private func applyQuery(to announcements: [Announcement]) -> [Announcement] {
        var result = announcements
        
        // Apply sorting
        switch query.sortBy {
        case .createdAtDescending:
            result = result.sorted { $0.createdAt > $1.createdAt }
        case .createdAtAscending:
            result = result.sorted { $0.createdAt < $1.createdAt }
        case .distanceFromUser:
            // Future: sort by distance (requires location)
            result = result.sorted { $0.createdAt > $1.createdAt }
        }
        
        // Apply limit
        if let limit = query.limit {
            result = Array(result.prefix(limit))
        }
        
        return result
    }
    
    /// Updates card ViewModels array from announcements
    private func updateCardViewModels(with announcements: [Announcement]) {
        cardViewModels = announcements.map { announcement in
            AnnouncementCardViewModel(
                announcement: announcement,
                onAction: handleAnnouncementAction
            )
        }
    }
    
    /// Handles actions from announcement cards (taps, etc.)
    private func handleAnnouncementAction(_ action: AnnouncementAction) {
        switch action {
        case .selected(let id):
            onAnnouncementTapped(id)
        }
    }
}
```

**Benefits**:
- ✅ **Autonomous**: Self-contained state and behavior
- ✅ **Reusable**: Works with any query configuration
- ✅ **Testable**: Easy to unit test with mock repository
- ✅ **Query-driven**: Same component, different behavior via query params

---

### Step 3: Create LandingPageViewModel (15 minutes)

**Location**: `/iosApp/iosApp/ViewModels/LandingPageViewModel.swift` (NEW)

**Responsibilities**:
- Parent ViewModel for landing page screen (thin wrapper)
- Create and configure AnnouncementCardsListViewModel with landing page query
- Pass through cross-tab navigation closure

**Key Implementation Points**:

```swift
import Foundation

/// Parent ViewModel for Landing Page screen.
/// Acts as thin wrapper/factory for autonomous list component.
/// Handles location permissions and sets query on child ViewModel.
@MainActor
class LandingPageViewModel: ObservableObject {
    // MARK: - Child Component ViewModel
    let listViewModel: AnnouncementCardsListViewModel
    
    // MARK: - Dependencies
    private let locationHandler: LocationPermissionHandler
    
    // MARK: - Initialization
    init(
        repository: AnnouncementRepositoryProtocol,
        locationHandler: LocationPermissionHandler,
        onAnnouncementTapped: @escaping (String) -> Void
    ) {
        self.locationHandler = locationHandler
        
        // Create child list ViewModel with initial empty query (no data loaded yet)
        self.listViewModel = AnnouncementCardsListViewModel(
            repository: repository,
            query: .landingPageQuery(location: nil),
            onAnnouncementTapped: onAnnouncementTapped
        )
    }
    
    // MARK: - Public Methods
    
    /// Prepares query with location and triggers list load (called from View .task)
    func loadData() async {
        // Parent handles location permissions
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Set query on child ViewModel (triggers automatic reload)
        let queryWithLocation = AnnouncementListQuery.landingPageQuery(location: result.location)
        listViewModel.setQuery(queryWithLocation)
    }
    
    /// Refreshes data with updated location (for future use: returning from background, etc.)
    func refreshIfNeeded() async {
        await loadData()
    }
}
```

**Key Characteristics**:
- ✅ **Thin wrapper**: No state management (delegated to child)
- ✅ **Factory pattern**: Creates configured list ViewModel
- ✅ **Composition**: Parent composes autonomous child component
- ✅ **Extensible**: Can add landing page-specific logic later (analytics, refresh triggers, etc.)

**Testing**: Create `LandingPageViewModelTests.swift` (see Step 9 for test structure)

---

### Step 4: Create AnnouncementCardsListView (20 minutes)

**Location**: `/iosApp/iosApp/Views/Shared/AnnouncementCardsListView.swift` (NEW)

**Responsibilities**:
- Render scrollable list of announcement cards (autonomous component)
- Observe AnnouncementCardsListViewModel for state changes
- Trigger loadAnnouncements on appear
- Handle loading, error, empty states

**Key Implementation Points**:

```swift
import SwiftUI

/// Autonomous announcement cards list component.
/// Observes AnnouncementCardsListViewModel for state changes.
/// Does NOT trigger loading - parent ViewModel handles that via setQuery().
struct AnnouncementCardsListView: View {
    @ObservedObject var viewModel: AnnouncementCardsListViewModel
    let emptyStateModel: EmptyStateViewModel
    let listAccessibilityId: String
    
    // MARK: - Computed Properties
    private var isEmpty: Bool {
        viewModel.cardViewModels.isEmpty && !viewModel.isLoading && viewModel.errorMessage == nil
    }
    
    // MARK: - Body
    var body: some View {
        ZStack {
            if viewModel.isLoading {
                LoadingView(model: .init(
                    message: L10n.AnnouncementList.Loading.message,
                    accessibilityIdentifier: "\(listAccessibilityId).loading"
                ))
            } else if let errorMessage = viewModel.errorMessage {
                ErrorView(model: .init(
                    title: L10n.AnnouncementList.Error.title,
                    message: errorMessage,
                    onRetry: {
                        viewModel.reload()  // Retry with current query
                    },
                    accessibilityIdentifier: "\(listAccessibilityId).error"
                ))
            } else if isEmpty {
                EmptyStateView(model: emptyStateModel)
            } else {
                ScrollView {
                    LazyVStack(spacing: 8) {
                        ForEach(viewModel.cardViewModels, id: \.id) { cardViewModel in
                            AnnouncementCardView(viewModel: cardViewModel)
                        }
                    }
                    .padding(.horizontal, 16)
                }
                .accessibilityIdentifier("\(listAccessibilityId).list")
            }
        }
        // No .task here - parent View handles loading via parent ViewModel
    }
}
```

**Benefits**:
- ✅ **Autonomous**: Observes own ViewModel, triggers own load
- ✅ **Reusable**: Works with any AnnouncementCardsListViewModel configuration
- ✅ **Composable**: Parent views wrap with navigation, buttons, etc.

---

### Step 5: Create LandingPageView (15 minutes)

**Location**: `/iosApp/iosApp/Views/LandingPage/LandingPageView.swift` (NEW)

**Responsibilities**:
- Compose AnnouncementCardsListView with landing page context
- Wrap in NavigationView with "Recent Pets" title
- Pass listViewModel from parent ViewModel

**Key Implementation Points**:

```swift
import SwiftUI

struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        // NO NavigationView - coordinator manages UINavigationController
        AnnouncementCardsListView(
            viewModel: viewModel.listViewModel,  // Child ViewModel
            emptyStateModel: .init(
                title: "No recent announcements",
                description: "Check back later for pet reports in your area",
                imageName: "tray.fill",
                accessibilityIdentifier: "landingPage.emptyState"
            ),
            listAccessibilityId: "landingPage"
        )
        .task {
            // Parent View triggers parent ViewModel to prepare query and load data
            await viewModel.loadData()
        }
    }
}
```

**Accessibility Identifiers** (for E2E tests):
- Loading: `landingPage.loading`
- Error: `landingPage.error`
- Empty state: `landingPage.emptyState`
- List: `landingPage.list`
- Announcement cards: Inherited from AnnouncementCardView

---

### Step 6: Refactor AnnouncementListViewModel to Use Autonomous Component (30 minutes - Optional but Recommended)

**Location**: `/iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListViewModel.swift` (MODIFIED)

**Changes**: Extract list logic into AnnouncementCardsListViewModel, keep parent-specific logic (permissions, foreground, buttons)

**Before** (current implementation):
```swift
@MainActor
class AnnouncementListViewModel: ObservableObject {
    @Published var cardViewModels: [AnnouncementCardViewModel] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    
    // Inline list loading logic, permission handling, foreground observer, etc.
    func loadAnnouncements() async { /* ... */ }
}
```

**After** (refactored with autonomous component):
```swift
@MainActor
class AnnouncementListViewModel: ObservableObject {
    // MARK: - Child Component ViewModel
    let listViewModel: AnnouncementCardsListViewModel
    
    // MARK: - Dependencies
    private let locationHandler: LocationPermissionHandler
    
    // MARK: - Parent-Specific State (permissions, popups, etc.)
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    @Published var showPermissionDeniedAlert = false
    
    // MARK: - Navigation Closures
    var onAnimalSelected: ((String) -> Void)?
    var onReportMissing: (() -> Void)?
    var onOpenAppSettings: (() -> Void)?
    
    // MARK: - Session State
    private var hasShownPermissionAlert = false
    
    init(repository: AnnouncementRepositoryProtocol, 
         locationHandler: LocationPermissionHandler) {
        
        self.locationHandler = locationHandler
        
        // Create child list ViewModel with initial empty query
        self.listViewModel = AnnouncementCardsListViewModel(
            repository: repository,
            query: .defaultQuery(location: nil),
            onAnnouncementTapped: { [weak self] id in
                self?.onAnimalSelected?(id)
            }
        )
        
        // Setup foreground observer for refresh (parent-specific)
        setupForegroundObserver()
    }
    
    // MARK: - Public Methods
    
    /// Prepares query with location and triggers list load (called from View .task)
    func loadData() async {
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Update parent state
        locationPermissionStatus = result.status
        
        // Show permission popup if needed (parent UI decision)
        if result.status.shouldShowCustomPopup && !hasShownPermissionAlert {
            showPermissionDeniedAlert = true
            hasShownPermissionAlert = true
        }
        
        // Set query on child ViewModel (triggers automatic reload)
        let queryWithLocation = AnnouncementListQuery.defaultQuery(location: result.location)
        listViewModel.setQuery(queryWithLocation)
    }
    
    // MARK: - Private Methods
    
    private func setupForegroundObserver() {
        locationHandler.startObservingForeground { [weak self] status, didBecomeAuthorized in
            guard let self = self else { return }
            self.locationPermissionStatus = status
            if didBecomeAuthorized {
                // Refresh location and reload
                Task {
                    await self.loadData()
                }
            }
        }
    }
    
    // Parent-specific methods (permission popups, buttons, etc.)
    func reportMissing() { onReportMissing?() }
    func openSettings() { onOpenAppSettings?() }
}
```

**AnnouncementListView Update**:
```swift
struct AnnouncementListView: View {
    @ObservedObject var viewModel: AnnouncementListViewModel
    
    var body: some View {
        ZStack {
            Color(hex: "#FAFAFA").ignoresSafeArea()
            
            // Use autonomous list component
            AnnouncementCardsListView(
                viewModel: viewModel.listViewModel,
                emptyStateModel: .default,
                listAccessibilityId: "animalList"
            )
            
            // Parent-specific overlays
            floatingButtonsSection
        }
        .alert(...) // Permission popup (parent-specific)
        .task {
            // Parent View triggers parent ViewModel to prepare query and load data
            await viewModel.loadData()
        }
    }
}
```

**Benefits**:
- ✅ **Consistency**: Both screens use same autonomous component pattern
- ✅ **DRY**: List logic exists in one place (AnnouncementCardsListViewModel)
- ✅ **Separation**: Parent handles permissions/buttons, child handles list

**Note**: This refactoring is optional but recommended. If skipped, AnnouncementListView continues with inline implementation. Both approaches work, but refactoring ensures consistency and reduces duplication.

---

### Step 7: Create HomeCoordinator (45 minutes)

**Location**: `/iosApp/iosApp/Coordinators/HomeCoordinator.swift` (NEW)

**Responsibilities**:
- Manage Home tab navigation
- Create and present LandingPageView wrapped in UIHostingController
- Handle announcement tap events by invoking cross-tab navigation closure

**Pattern**: Follow `AnnouncementListCoordinator` structure exactly:
1. Simple `init()` stores only closure (no dependencies yet)
2. `start()` fetches dependencies from `ServiceContainer.shared`
3. Creates ViewModel and presents view

**Key Implementation Points**:

```swift
import UIKit
import SwiftUI

/**
 * Coordinator for Home tab navigation following MVVM-C architecture.
 * Manages landing page presentation and cross-tab navigation.
 *
 * **Root coordinator pattern**: Creates its own UINavigationController in init(),
 * suitable for use as a tab's root coordinator.
 *
 * **Same pattern as AnnouncementListCoordinator**:
 * - Simple init() with only closure parameter
 * - Dependencies fetched in start() from ServiceContainer.shared
 */
@MainActor
class HomeCoordinator: CoordinatorInterface {
    var navigationController: UINavigationController?
    var childCoordinators: [CoordinatorInterface] = []
    
    // MARK: - Private Properties
    
    /// Closure to handle cross-tab navigation when user taps announcement
    private let onShowPetDetails: (String) -> Void
    
    // MARK: - Initialization
    
    /**
     * Creates HomeCoordinator with its own navigation controller.
     *
     * **Root coordinator pattern**: This coordinator creates and owns its
     * UINavigationController, making it suitable for use as a tab's root.
     *
     * - Parameter onShowPetDetails: Closure invoked when user taps announcement
     */
    init(onShowPetDetails: @escaping (String) -> Void) {
        self.navigationController = UINavigationController()
        self.onShowPetDetails = onShowPetDetails
    }
    
    // MARK: - CoordinatorInterface Methods
    
    /**
     * Starts the Home tab flow by showing landing page.
     * Fetches dependencies from ServiceContainer (same as AnnouncementListCoordinator).
     *
     * - Parameter animated: Whether to animate the transition
     */
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Get dependencies from DI container (same pattern as AnnouncementListCoordinator)
        let container = ServiceContainer.shared
        let repository = container.announcementRepository
        let locationHandler = container.locationPermissionHandler
        
        // Create ViewModel with dependencies
        let viewModel = LandingPageViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnnouncementTapped: onShowPetDetails
        )
        
        // Create SwiftUI view with ViewModel
        let landingPageView = LandingPageView(viewModel: viewModel)
        
        // Wrap in UIHostingController for UIKit navigation
        let hostingController = UIHostingController(rootView: landingPageView)
        
        // Configure navigation bar (UIKit - coordinator responsibility, NOT SwiftUI .navigationTitle())
        hostingController.title = L10n.Tabs.home
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Show navigation bar and set as root
        navigationController.isNavigationBarHidden = false
        navigationController.setViewControllers([hostingController], animated: animated)
    }
}
```

**Note**: This follows the exact same pattern as `AnnouncementListCoordinator` - simple init with minimal parameters, dependencies fetched in `start()`. Consistent and predictable! 🎯

---

### Step 8: Update TabCoordinator for Cross-Tab Navigation (30 minutes)

**Location**: `/iosApp/iosApp/Coordinators/TabCoordinator.swift` (MODIFIED)

**Changes Required**:
1. Replace `PlaceholderCoordinator` with `HomeCoordinator` for Home tab (line 54)
2. Add `showPetDetailsFromHome` method to handle cross-tab navigation

**Pattern**: Follow same approach as `AnnouncementListCoordinator` - create coordinator directly, get dependencies in `start()` from `ServiceContainer.shared`.

**Before** (current implementation with PlaceholderCoordinator):
```swift
init() {
    _tabBarController = UITabBarController()
    
    // Create child coordinators
    let homeCoordinator = PlaceholderCoordinator(title: L10n.Tabs.home)  // ❌ Placeholder
    let lostPetCoordinator = AnnouncementListCoordinator()
    // ... other tabs
}
```

**After** (with real HomeCoordinator - same pattern as AnnouncementListCoordinator):
```swift
init() {  // NO CHANGES to signature ✅
    _tabBarController = UITabBarController()
    
    // Create child coordinators
    let homeCoordinator = HomeCoordinator(  // ✅ Created directly (same as AnnouncementListCoordinator)
        onShowPetDetails: { [weak self] announcementId in
            self?.showPetDetailsFromHome(announcementId)
        }
    )
    let lostPetCoordinator = AnnouncementListCoordinator()
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
```

**No Changes Required**:
- ❌ AppCoordinator - stays as-is (no ServiceContainer parameter needed)
- ❌ ServiceContainer - no factory method needed (dependencies fetched in `start()` like AnnouncementListCoordinator)
```

**Navigation Flow**:
1. User taps announcement card on Home tab
2. LandingPageView → LandingPageViewModel.handleAnnouncementTap()
3. ViewModel invokes `onAnnouncementTapped` closure (provided by HomeCoordinator)
4. HomeCoordinator invokes `onShowPetDetails` closure (provided by TabBarCoordinator)
5. TabBarCoordinator switches tab and calls AnnouncementsCoordinator to push detail screen

---

### Step 9: ~~Update ServiceContainer DI~~ NO CHANGES NEEDED ✅

**Location**: `/iosApp/iosApp/DI/ServiceContainer.swift` - **NO MODIFICATIONS**

**Why No Changes?**
HomeCoordinator follows the same pattern as AnnouncementListCoordinator:
- Dependencies fetched in `start()` from `ServiceContainer.shared`
- No factory method needed
- Keeps codebase consistent

**Existing ServiceContainer** (reused as-is):
```swift
class ServiceContainer {
    static let shared = ServiceContainer()
    
    // HomeCoordinator uses these existing properties:
    lazy var announcementRepository: AnnouncementRepositoryProtocol = AnnouncementRepository()
    lazy var locationPermissionHandler: LocationPermissionHandler =
        LocationPermissionHandler(locationService: locationService)
    
    // ... other existing services
}
```

**Dependency Flow**:
1. TabCoordinator creates `HomeCoordinator(onShowPetDetails: { ... })`
2. HomeCoordinator.start() fetches dependencies from `ServiceContainer.shared`
3. HomeCoordinator creates `LandingPageViewModel(repository, locationHandler, ...)`
4. LandingPageViewModel creates `AnnouncementCardsListViewModel(...)`

**Pattern**: Same as existing code - simple, consistent, no extra abstraction layers.

---

### Step 10: Write Unit Tests (70 minutes)

**Location**: 
- `/iosApp/iosAppTests/ViewModels/AnnouncementCardsListViewModelTests.swift` (NEW - primary tests)
- `/iosApp/iosAppTests/ViewModels/LandingPageViewModelTests.swift` (NEW - integration tests)

**Test Coverage Requirements** (minimum 80%):

**AnnouncementCardsListViewModel Tests** (core logic):
1. ✅ Load announcements successfully with query limit
2. ✅ Load announcements when backend has < limit items (returns all)
3. ✅ Load announcements when backend is empty (empty state)
4. ✅ Handle network error (error state with message)
5. ✅ Sort announcements by createdAt descending (query.sortBy)
6. ✅ Apply query limit correctly (5 items for landing page, nil for full list)
7. ✅ Fetch user location when permissions granted
8. ✅ Skip location fetch when permissions denied
9. ✅ Handle announcement tap (invoke closure with correct ID)
10. ✅ Reload cancels previous task and starts new one

**LandingPageViewModel Tests** (integration):
1. ✅ Creates listViewModel with correct query configuration (limit: 5)
2. ✅ listViewModel is accessible from parent
3. ✅ refreshIfNeeded() triggers listViewModel.reload()

**Example Test** (Given-When-Then structure):

```swift
import XCTest
@testable import iosApp

// MARK: - AnnouncementCardsListViewModel Tests

final class AnnouncementCardsListViewModelTests: XCTestCase {
    private var viewModel: AnnouncementCardsListViewModel!
    private var mockRepository: MockAnnouncementRepository!
    private var mockLocationHandler: MockLocationPermissionHandler!
    private var capturedAnnouncementId: String?
    
    override func setUp() {
        super.setUp()
        mockRepository = MockAnnouncementRepository()
        mockLocationHandler = MockLocationPermissionHandler()
    }
    
    func testLoadAnnouncements_whenQueryLimitIs5AndRepositoryReturns10_shouldDisplayFirst5MostRecent() async {
        // Given: Repository returns 10 announcements, query limit is 5
        let announcements = createMockAnnouncements(count: 10)
        mockRepository.mockAnnouncements = announcements
        
        let query = AnnouncementListQuery(limit: 5, sortBy: .createdAtDescending)
        viewModel = AnnouncementCardsListViewModel(
            repository: mockRepository,
            locationHandler: mockLocationHandler,
            query: query,
            onAnnouncementTapped: { [weak self] id in
                self?.capturedAnnouncementId = id
            }
        )
        
        // When: ViewModel loads announcements
        await viewModel.loadAnnouncements()
        
        // Then: ViewModel has exactly 5 card ViewModels
        XCTAssertEqual(viewModel.cardViewModels.count, 5)
        
        // Then: Announcements are sorted by createdAt descending (newest first)
        let dates = viewModel.cardViewModels.map { $0.announcement.createdAt }
        XCTAssertTrue(dates[0] > dates[1])
        XCTAssertTrue(dates[1] > dates[2])
        
        // Then: Loading state is false, no error
        XCTAssertFalse(viewModel.isLoading)
        XCTAssertNil(viewModel.errorMessage)
    }
    
    func testLoadAnnouncements_whenRepositoryFails_shouldShowErrorMessage() async {
        // Given: Repository throws error
        mockRepository.shouldThrowError = true
        
        let query = AnnouncementListQuery(limit: 5, sortBy: .createdAtDescending)
        viewModel = AnnouncementCardsListViewModel(
            repository: mockRepository,
            locationHandler: mockLocationHandler,
            query: query,
            onAnnouncementTapped: { _ in }
        )
        
        // When: ViewModel loads announcements
        await viewModel.loadAnnouncements()
        
        // Then: Error message is set
        XCTAssertNotNil(viewModel.errorMessage)
        XCTAssertTrue(viewModel.errorMessage!.contains("Failed to load"))
        
        // Then: Card ViewModels array is empty, loading is false
        XCTAssertTrue(viewModel.cardViewModels.isEmpty)
        XCTAssertFalse(viewModel.isLoading)
    }
    
    func testReload_shouldCancelPreviousTaskAndStartNew() async {
        // Given: ViewModel with mock repository
        let query = AnnouncementListQuery(limit: 5, sortBy: .createdAtDescending)
        viewModel = AnnouncementCardsListViewModel(
            repository: mockRepository,
            locationHandler: mockLocationHandler,
            query: query,
            onAnnouncementTapped: { _ in }
        )
        
        // When: reload() is called (triggers new load task)
        viewModel.reload()
        
        // Then: Verify reload triggers loading state
        // (Implementation detail: reload() creates Task { await loadAnnouncements() })
        try? await Task.sleep(nanoseconds: 100_000_000)  // Wait for async
        XCTAssertTrue(viewModel.isLoading || !viewModel.cardViewModels.isEmpty)
    }
}

// MARK: - LandingPageViewModel Tests

final class LandingPageViewModelTests: XCTestCase {
    func testInit_shouldCreateListViewModelWithLandingPageQuery() {
        // Given: Mock dependencies
        let mockRepository = MockAnnouncementRepository()
        let mockLocationHandler = MockLocationPermissionHandler()
        
        // When: LandingPageViewModel is created
        let viewModel = LandingPageViewModel(
            repository: mockRepository,
            locationHandler: mockLocationHandler,
            onAnnouncementTapped: { _ in }
        )
        
        // Then: listViewModel exists with correct query configuration
        XCTAssertNotNil(viewModel.listViewModel)
        // Note: Query internals are private, test behavior via integration tests
    }
}
```

**Run Tests**:
```bash
# Via Xcode: Cmd+U
# Via command line:
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

**Coverage Target**: 80% line + branch coverage (check in Xcode Coverage Report)

---

### Step 11: Write E2E Tests (60 minutes)

**Location**: `/e2e-tests/java/src/test/resources/features/mobile/landing-page.feature`

**Test Scenarios** (Cucumber/Gherkin):

```gherkin
Feature: iOS Landing Page
  As a user
  I want to see recent pet announcements on the Home tab
  So that I can quickly check for new pet reports

  Background:
    Given the iOS app is launched
    And the user is on the Home tab

  Scenario: Display 5 most recent announcements
    Given the backend has 10 pet announcements
    When the landing page loads
    Then I should see exactly 5 announcement cards
    And the announcements should be sorted by date (newest first)

  Scenario: Display empty state when no announcements
    Given the backend has 0 pet announcements
    When the landing page loads
    Then I should see an empty state view
    And the empty state should display "No recent announcements"

  Scenario: Navigate to pet details from landing page
    Given the backend has 5 pet announcements
    And the landing page is loaded
    When I tap on the first announcement card
    Then the app should switch to the Lost Pets tab
    And the pet details screen should be displayed

  Scenario: Display error message when backend is unavailable
    Given the backend API is unavailable
    When the landing page loads
    Then I should see an error message
    And the error message should explain the connection problem
```

**Screen Object** (`LandingPageScreen.java`):

```java
public class LandingPageScreen {
    private final IOSDriver driver;
    
    public LandingPageScreen(IOSDriver driver) {
        this.driver = driver;
    }
    
    public boolean isAnnouncementCardDisplayed(String announcementId) {
        String identifier = "landingPage.announcementCard." + announcementId;
        return driver.findElements(
            MobileBy.AccessibilityId(identifier)
        ).size() > 0;
    }
    
    public int getAnnouncementCardCount() {
        return driver.findElements(
            MobileBy.iOSNsPredicateString("name BEGINSWITH 'landingPage.announcementCard.'")
        ).size();
    }
    
    public void tapAnnouncementCard(String announcementId) {
        String identifier = "landingPage.announcementCard." + announcementId;
        driver.findElement(MobileBy.AccessibilityId(identifier)).click();
    }
    
    public boolean isEmptyStateDisplayed() {
        return driver.findElement(
            MobileBy.AccessibilityId("landingPage.emptyState")
        ).isDisplayed();
    }
    
    public boolean isErrorMessageDisplayed() {
        return driver.findElement(
            MobileBy.AccessibilityId("landingPage.errorMessage")
        ).isDisplayed();
    }
}
```

**Run E2E Tests**:
```bash
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
```

**Reports**: View at `e2e-tests/java/target/cucumber-reports/ios/index.html`

---

## Testing Checklist

Before marking implementation complete, verify:

- [ ] **Unit Tests**: 80%+ coverage for LandingPageViewModel
- [ ] **Manual Testing**: 
  - [ ] Landing page displays 5 announcements when backend has 10+
  - [ ] Landing page displays 3 announcements when backend has 3
  - [ ] Empty state displays when backend has 0 announcements
  - [ ] Error state displays when backend is unavailable
  - [ ] Tapping announcement navigates to Lost Pets tab with detail screen
  - [ ] Back navigation returns to Lost Pets tab, not Home tab
  - [ ] Location coordinates displayed when location permissions granted
  - [ ] Location coordinates hidden when location permissions denied
- [ ] **E2E Tests**: All Cucumber scenarios pass for iOS
- [ ] **Accessibility Identifiers**: All interactive elements have testIDs
- [ ] **Code Quality**: SwiftDoc comments on public APIs, follows MVVM-C pattern

---

## Common Issues & Solutions

### Issue 1: Cross-Tab Navigation Not Working

**Symptom**: Tapping announcement on Home tab doesn't navigate to detail screen

**Solution**: Verify closure chain is connected:
1. Check `onShowPetDetails` closure passed from TabBarCoordinator to HomeCoordinator
2. Check `onAnnouncementTapped` closure passed from HomeCoordinator to LandingPageViewModel
3. Verify `tabBarController.selectedIndex` is being set to correct tab index
4. Ensure AnnouncementsCoordinator has `showPetDetails` method implemented

### Issue 2: Announcements Not Sorted Correctly

**Symptom**: Announcements appear in random order on landing page

**Solution**: Verify sorting logic in `loadAnnouncements`:
```swift
announcements = allAnnouncements
    .sorted { $0.createdAt > $1.createdAt }  // Must use > for descending
    .prefix(5)
    .map { $0 }
```

### Issue 3: Location Coordinates Not Displayed

**Symptom**: Location coordinates missing on announcement cards

**Solution**: Check location permission flow:
1. Verify `locationService.hasLocationPermission` returns true
2. Verify `userLocation` is set in ViewModel after successful location fetch
3. Verify `AnnouncementCard` receives non-nil `userLocation` parameter
4. Check Info.plist has `NSLocationWhenInUseUsageDescription` key

### Issue 4: E2E Tests Fail to Find Elements

**Symptom**: Appium can't locate announcement cards by accessibility identifier

**Solution**: 
1. Verify `.accessibilityIdentifier()` modifiers are applied to SwiftUI views
2. Check accessibility IDs in Xcode Accessibility Inspector
3. Verify Screen Object uses correct `MobileBy.AccessibilityId()` syntax
4. Ensure iOS Simulator has accessibility enabled

---

## Next Steps

After completing implementation and testing:

1. ✅ Run full test suite (`xcodebuild test`)
2. ✅ Run E2E tests (`mvn test -Dtest=IosTestRunner`)
3. ✅ Verify 80% code coverage (Xcode Coverage Report)
4. ✅ Test manually on iOS Simulator and physical device
5. ✅ (Optional but recommended) Refactor AnnouncementListView to use AnnouncementCardsListView (Step 4)
6. ✅ Create pull request with branch `058-ios-landing-page-list`
7. ✅ Request code review from iOS team

**Note**: If Step 4 (refactoring AnnouncementListView) is included, ensure existing AnnouncementListView tests still pass. The refactoring should be behavior-preserving - only the implementation changes, not the functionality.

**Estimated Total Implementation Time**: 5 hours (5.5 hours with optional refactoring)

**Breakdown**:
- Step 1 (AnnouncementListQuery model): 10 min
- Step 2 (AnnouncementCardsListViewModel - autonomous): 40 min
- Step 3 (LandingPageViewModel - wrapper): 15 min
- Step 4 (AnnouncementCardsListView): 20 min
- Step 5 (LandingPageView): 15 min
- Step 6 (Refactor AnnouncementListView - optional): 30 min
- Step 7 (HomeCoordinator): 45 min
- Step 8 (TabCoordinator): 30 min
- Step 9 (ServiceContainer): 0 min ✅ (no changes needed)
- Step 10 (Unit tests): 70 min
- Step 11 (E2E tests): 60 min
- **Total**: ~5 hours (5.5 hours with refactoring)

---

## Resources

- [Feature Spec](./spec.md) - Full feature requirements
- [Research Document](./research.md) - Technical research findings
- [Data Model](./data-model.md) - Entity definitions and validation rules
- [API Contracts](./contracts/README.md) - API endpoint documentation
- [iOS MVVM-C Guide](../../docs/implementation/ios-mvvmc-architecture.md) - iOS architecture patterns
- [Swift Concurrency Guide](https://docs.swift.org/swift-book/LanguageGuide/Concurrency.html) - Async/await patterns

---

**Questions?** Contact iOS team lead or refer to existing announcement list implementation in `/iosApp/iosApp/Views/AnnouncementList/` for reference patterns.

