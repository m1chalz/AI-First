import Foundation
import UIKit

// MARK: - LocationPermissionStatus Presentation Extension

/**
 * Presentation layer extension for UI decisions.
 * Determines when to show custom permission popup (denied/restricted states).
 * This is UI concern, not domain logic - domain should not know about "popups".
 */
extension LocationPermissionStatus {
    /// Whether to show custom permission popup (presentation logic, not domain logic).
    /// Custom popup is shown for denied/restricted states (recovery path).
    /// iOS system alert is shown automatically for .notDetermined state.
    var shouldShowCustomPopup: Bool {
        self == .denied || self == .restricted
    }
}

/// Parent ViewModel for Landing Page screen.
/// Handles location permissions, permission popups, and creates list component.
///
/// **Responsibilities**:
/// - Creates and configures `AnnouncementCardsListViewModel` with landing page query (limit: 5)
/// - Handles location permissions via `LocationPermissionHandler`
/// - Shows permission denied/restricted popup (once per session)
/// - Observes real-time permission changes via handler's `startObservingLocationPermissionChanges()`
/// - Fetches user location and sets query on child ViewModel via `setQuery()`
/// - Passes through cross-tab navigation closure (`onAnnouncementTapped`)
///
/// **Autonomous Component Pattern**:
/// - LandingPageViewModel manages permissions and coordinates list loading
/// - All list state (`cardViewModels`, `isLoading`, `errorMessage`) managed by `listViewModel`
/// - Parent can trigger reload via `listViewModel.query = ...`
///
/// **Loading Flow**:
/// 1. LandingPageView has `.task { await viewModel.loadData() }`
/// 2. `loadData()` fetches location from LocationPermissionHandler
/// 3. Shows permission popup if needed (once per session)
/// 4. `loadData()` sets `listViewModel.query = queryWithLocation`
/// 5. Child ViewModel automatically reloads with new query
///
/// **Permission Change Handling**:
/// - Handler observes both real-time stream AND foreground notifications
/// - Auto-reloads data when permission changes from unauthorized → authorized
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
    
    // MARK: - Location Properties
    
    /// Current location permission status
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    
    /// Current user location (nil if unavailable)
    @Published var currentLocation: Coordinate?
    
    /// Controls custom permission denied popup display (recovery path)
    @Published var showPermissionDeniedAlert = false
    
    // MARK: - Coordinator Closures (Navigation)
    
    /// Called when user taps "Go to Settings" in permission popup (MVVM-C pattern)
    var onOpenAppSettings: (() -> Void)?
    
    // MARK: - Dependencies
    
    private let locationHandler: LocationPermissionHandler
    
    // MARK: - Session State
    
    /// Session-level flag preventing repeated permission popups
    private var hasShownPermissionAlert = false
    
    /// Active load task for cancellation support
    private var loadTask: Task<Void, Never>?
    
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
        
        // Start observing permission changes (real-time stream + foreground notifications)
        locationHandler.startObservingLocationPermissionChanges { [weak self] status, didBecomeAuthorized in
            guard let self else { return }
            self.locationPermissionStatus = status
            
            // Auto-refresh ONLY if permission changed from unauthorized → authorized
            if didBecomeAuthorized {
                self.loadTask?.cancel()
                self.loadTask = Task {
                    await self.loadData()
                }
            }
        }
    }
    
    deinit {
        loadTask?.cancel()
        locationHandler.stopObservingLocationPermissionChanges()
    }
    
    // MARK: - Public Methods
    
    /// Prepares query with location and triggers list load.
    /// Called from View's `.task` modifier.
    ///
    /// **Flow**:
    /// 1. Requests location from LocationPermissionHandler
    /// 2. Updates location state and shows permission popup if needed
    /// 3. Creates landing page query with location (or nil if unavailable)
    /// 4. Sets query on child ViewModel (triggers automatic reload)
    func loadData() async {
        // Parent handles location permissions
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Update published state with results
        locationPermissionStatus = result.status
        currentLocation = result.location
        
        // Show custom popup for denied/restricted (once per session)
        if result.status.shouldShowCustomPopup && !hasShownPermissionAlert {
            showPermissionDeniedAlert = true
            hasShownPermissionAlert = true
        }
        
        // Set query on child ViewModel (triggers automatic reload)
        let queryWithLocation = AnnouncementListQuery.landingPageQuery(location: result.location)
        print("[msz] LandingPageViewModel loadData query \(result.location)")
        listViewModel.query = queryWithLocation
    }
    
    /// Refreshes data with updated location.
    /// Called when app returns from background or when manual refresh is needed.
    func refreshIfNeeded() async {
        await loadData()
    }
    
    // MARK: - Recovery Path Methods
    
    /// Opens iOS Settings app to this app's permission screen.
    /// Delegates to coordinator via callback (MVVM-C pattern).
    func openSettings() {
        onOpenAppSettings?()
    }
    
    /// Continues without location when user dismisses permission popup.
    /// Sets query without location on child ViewModel (triggers reload).
    func continueWithoutLocation() {
        showPermissionDeniedAlert = false
        
        // Set query without location on child ViewModel (triggers automatic reload)
        let queryWithoutLocation = AnnouncementListQuery.landingPageQuery(location: nil)
        listViewModel.query = queryWithoutLocation
    }
}

