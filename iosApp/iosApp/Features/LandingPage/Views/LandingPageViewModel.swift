import Foundation

/// Parent ViewModel for Landing Page screen.
/// Acts as thin wrapper/factory for autonomous list component.
///
/// **Responsibilities**:
/// - Creates and configures `AnnouncementCardsListViewModel` with landing page query (limit: 5)
/// - Handles location permissions via `LocationPermissionHandler`
/// - Fetches user location and sets query on child ViewModel via `setQuery()`
/// - Passes through cross-tab navigation closure (`onAnnouncementTapped`)
///
/// **Autonomous Component Pattern**:
/// - LandingPageViewModel is a thin wrapper - no list state management
/// - All list state (`cardViewModels`, `isLoading`, `errorMessage`) managed by `listViewModel`
/// - Parent can trigger reload via `listViewModel.setQuery()` or `listViewModel.reload()`
///
/// **Loading Flow**:
/// 1. LandingPageView has `.task { await viewModel.loadData() }`
/// 2. `loadData()` fetches location from LocationPermissionHandler
/// 3. `loadData()` calls `listViewModel.setQuery(queryWithLocation)`
/// 4. Child ViewModel automatically reloads with new query
@MainActor
class LandingPageViewModel: ObservableObject {
    // MARK: - Child Component ViewModel
    
    /// Autonomous list ViewModel managing announcement cards state and behavior.
    /// Observed by `AnnouncementCardsListView` for UI updates.
    let listViewModel: AnnouncementCardsListViewModel
    
    // MARK: - UI Configuration
    
    /// Empty state model for the list component
    let emptyStateModel = EmptyStateView.Model(
        message: L10n.LandingPage.EmptyState.message
    )
    
    /// Accessibility identifier prefix for list elements
    let listAccessibilityId = "landingPage"
    
    // MARK: - Dependencies
    
    private let locationHandler: LocationPermissionHandler
    
    // MARK: - Initialization
    
    /// Creates LandingPageViewModel with repository and location handler.
    ///
    /// - Parameters:
    ///   - repository: Repository for fetching announcements (passed to child ViewModel)
    ///   - locationHandler: Handler for location permission logic
    ///   - onAnnouncementTapped: Closure invoked when user taps announcement card (cross-tab navigation)
    init(
        repository: AnnouncementRepositoryProtocol,
        locationHandler: LocationPermissionHandler,
        onAnnouncementTapped: @escaping (String) -> Void
    ) {
        self.locationHandler = locationHandler
        
        // Create child list ViewModel (query not set yet - list remains empty)
        // loadData() will call setQuery() after fetching location
        self.listViewModel = AnnouncementCardsListViewModel(
            repository: repository,
            onAnnouncementTapped: onAnnouncementTapped
        )
    }
    
    // MARK: - Public Methods
    
    /// Prepares query with location and triggers list load.
    /// Called from View's `.task` modifier.
    ///
    /// **Flow**:
    /// 1. Requests location from LocationPermissionHandler
    /// 2. Creates landing page query with location (or nil if unavailable)
    /// 3. Sets query on child ViewModel (triggers automatic reload)
    func loadData() async {
        // Parent handles location permissions
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Set query on child ViewModel (triggers automatic reload)
        let queryWithLocation = AnnouncementListQuery.landingPageQuery(location: result.location)
        listViewModel.setQuery(queryWithLocation)
    }
    
    /// Refreshes data with updated location.
    /// Called when app returns from background or when manual refresh is needed.
    func refreshIfNeeded() async {
        await loadData()
    }
}

