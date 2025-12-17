import Foundation
import UIKit

/**
 * Parent ViewModel for Announcement List screen following MVVM-C architecture.
 * Acts as thin wrapper for autonomous list component, handling feature-specific concerns.
 *
 * **Autonomous Component Pattern**:
 * - Delegates list state management to `listViewModel: AnnouncementCardsListViewModel`
 * - Handles feature-specific logic: location fetching, floating buttons, foreground observer
 * - Communicates with coordinator via closures for navigation
 *
 * **Responsibilities**:
 * - Location fetching (no permission popup - handled by LandingPage)
 * - Foreground observer for dynamic permission changes (auto-refresh when granted)
 * - Floating button callbacks (Report Missing, Report Found)
 * - Cross-tab navigation closure (`onAnimalSelected`)
 *
 * **Permission Popup**:
 * - NOT handled here - LandingPage (Home tab) handles permission popup
 * - This ViewModel only observes permission changes on app foreground
 * - Auto-refreshes when permission changes from denied → authorized
 *
 * State updates happen on main actor to ensure UI thread safety.
 */
@MainActor
class AnnouncementListViewModel: ObservableObject {
    // MARK: - Child Component ViewModel
    
    /// Autonomous list ViewModel managing announcement cards state and behavior.
    /// Observed by `AnnouncementCardsListView` for UI updates.
    let listViewModel: AnnouncementCardsListViewModel
    
    // MARK: - UI Configuration
    
    /// Empty state model for the list component
    let emptyStateModel = EmptyStateView.Model.default
    
    /// Accessibility identifier prefix for list elements
    let listAccessibilityId = "animalList"
    
    // MARK: - Location Properties
    
    /// Current location permission status (for foreground observer)
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    
    /// Current user location (nil if unavailable)
    @Published var currentLocation: Coordinate?
    
    // MARK: - Coordinator Closures (Navigation)
    
    /// Called when user taps "Report a Missing Animal" button
    var onReportMissing: (() -> Void)?
    
    /// Called when user taps "Report Found Animal" button (not used in mobile UI, but included for completeness)
    var onReportFound: (() -> Void)?
    
    // MARK: - Dependencies
    
    private let locationHandler: LocationPermissionHandler
    
    // MARK: - Session State
    
    /// Active load task for cancellation support
    /// Stores current loadAnnouncements task to enable cancellation when new load starts
    private var loadTask: Task<Void, Never>?

    // MARK: - Initialization
    
    /**
     * Initializes ViewModel with repository and location handler.
     * Creates child list ViewModel and sets up foreground observer.
     *
     * - Parameter repository: Repository for fetching animals (passed to child ViewModel)
     * - Parameter locationHandler: Handler for location permission logic (injected)
     * - Parameter onAnimalSelected: Closure invoked when user taps announcement card
     */
    init(
        repository: AnnouncementRepositoryProtocol,
        locationHandler: LocationPermissionHandler,
        onAnimalSelected: @escaping (String) -> Void
    ) {
        self.locationHandler = locationHandler
        
        // Create child list ViewModel (query not set yet - list remains empty)
        // loadData() will set query after fetching location
        self.listViewModel = AnnouncementCardsListViewModel(
            repository: repository,
            onAnnouncementTapped: onAnimalSelected
        )
        
        // Observe app returning from background (dynamic permission change handling)
        // Auto-refresh when permission changes from unauthorized → authorized
        locationHandler.startObservingForeground { [weak self] status, didBecomeAuthorized in
            guard let self = self else { return }
            // Callback is already dispatched to main thread by handler
            self.locationPermissionStatus = status
            // Auto-refresh ONLY if permission changed from unauthorized → authorized
            if didBecomeAuthorized {
                self.loadTask?.cancel()
                self.loadTask = Task {
                    await self.loadData()
                }
            }
        }
        
        // Load animals on initialization
        loadTask = Task {
            await loadData()
        }
    }
    
    deinit {
        // Cancel active task to prevent memory leaks
        loadTask?.cancel()
        locationHandler.stopObservingForeground()
    }
    
    // MARK: - Public Methods
    
    /**
     * Prepares query with location and triggers list load.
     * Fetches location and sets query on child ViewModel.
     *
     * **Flow**:
     * 1. Requests location from LocationPermissionHandler
     * 2. Updates location state
     * 3. Creates query with location (or nil if unavailable)
     * 4. Sets query on child ViewModel (triggers automatic reload)
     *
     * **Note**: Permission popup is NOT shown here - handled by LandingPage.
     */
    func loadData() async {
        // Delegate location permission handling to handler
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Update published state with results
        locationPermissionStatus = result.status
        currentLocation = result.location
        
        // Set query on child ViewModel (triggers automatic reload)
        let queryWithLocation = AnnouncementListQuery.defaultQuery(location: result.location)
        listViewModel.query = queryWithLocation
    }
    
    /**
     * Requests data refresh from external source (e.g., coordinator after report sent).
     * Called by coordinator when user successfully submits announcement.
     * Encapsulates refresh logic without exposing internal loadData() implementation.
     */
    func requestToRefreshData() {
        // Cancel previous load task before starting new one
        loadTask?.cancel()
        loadTask = Task { @MainActor in
            await loadData()
        }
    }
    
    /**
     * Handles "Report a Missing Animal" action.
     * Calls coordinator closure for navigation.
     */
    func reportMissing() {
        onReportMissing?()
    }
    
    /**
     * Handles "Report Found Animal" action.
     * Calls coordinator closure for navigation.
     * Note: Not exposed in iOS mobile UI per design, but included for completeness.
     */
    func reportFound() {
        onReportFound?()
    }
    
    // MARK: - Legacy API (Backward Compatibility)
    
    // These properties delegate to listViewModel for backward compatibility with tests
    // and coordinator code that may reference them directly.
    
    /// List of announcement card ViewModels - delegates to child ViewModel
    var cardViewModels: [AnnouncementCardViewModel] {
        get { listViewModel.cardViewModels }
    }
    
    /// Loading state indicator - delegates to child ViewModel
    var isLoading: Bool {
        listViewModel.isLoading
    }
    
    /// Error message (nil if no error) - delegates to child ViewModel
    var errorMessage: String? {
        listViewModel.errorMessage
    }
    
    /// Computed: true when data loaded but list is empty - delegates to child ViewModel
    var isEmpty: Bool {
        listViewModel.cardViewModels.isEmpty && !listViewModel.isLoading && listViewModel.errorMessage == nil
    }
    
    /// Legacy method for tests - now delegates to loadData()
    func loadAnnouncements() async {
        await loadData()
    }
    
    /// Legacy method for animal selection - now handled via closure in init
    func selectAnimal(id: String) {
        // This is now handled via onAnnouncementTapped closure passed to listViewModel
        // Keeping for backward compatibility with tests
    }
    
}

