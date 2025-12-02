import Foundation
import UIKit

// MARK: - LocationPermissionStatus Presentation Extension (User Story 3)

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

/**
 * ViewModel for Animal List screen following MVVM-C architecture.
 * Manages state using @Published properties for SwiftUI observation.
 * Communicates with coordinator via closures for navigation.
 *
 * State updates happen on main actor to ensure UI thread safety.
 */
@MainActor
class AnimalListViewModel: ObservableObject {
    // MARK: - Published Properties (State)
    
    /// List of animal card ViewModels (single source of truth)
    @Published var cardViewModels: [AnimalCardViewModel] = []
    
    /// Loading state indicator
    @Published var isLoading: Bool = false
    
    /// Error message (nil if no error)
    @Published var errorMessage: String?
    
    // MARK: - Location Properties (User Story 1 & 3)
    
    /// Current location permission status
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    
    /// Current user location (nil if unavailable)
    @Published var currentLocation: Coordinate?
    
    /// Controls custom permission denied popup display (User Story 3: recovery path)
    @Published var showPermissionDeniedAlert = false
    
    // MARK: - Computed Properties
    
    /// Computed: true when data loaded but list is empty
    var isEmpty: Bool {
        cardViewModels.isEmpty && !isLoading && errorMessage == nil
    }
    
    // MARK: - Coordinator Closures (Navigation)
    
    /// Called when user selects an animal card
    var onAnimalSelected: ((String) -> Void)?
    
    /// Called when user taps "Report a Missing Animal" button
    var onReportMissing: (() -> Void)?
    
    /// Called when user taps "Report Found Animal" button (not used in mobile UI, but included for completeness)
    var onReportFound: (() -> Void)?
    
    /// Called when user taps "Go to Settings" in permission popup (User Story 3: MVVM-C pattern)
    var onOpenAppSettings: (() -> Void)?
    
    // MARK: - Dependencies
    
    private let repository: AnimalRepositoryProtocol
    private let locationHandler: LocationPermissionHandler
    
    // MARK: - Session State (User Story 3)
    
    /// Session-level flag preventing repeated permission popups (FR-013)
    private var hasShownPermissionAlert = false
    
    /// Active load task for cancellation support (User Story 3: T065)
    /// Stores current loadAnimals task to enable cancellation when new load starts
    private var loadTask: Task<Void, Never>?

    // MARK: - Initialization
    
    /**
     * Initializes ViewModel with repository and location handler.
     * Immediately loads animals on creation.
     *
     * - Parameter repository: Repository for fetching animals (injected)
     * - Parameter locationHandler: Handler for location permission logic (injected)
     */
    init(
        repository: AnimalRepositoryProtocol,
        locationHandler: LocationPermissionHandler
    ) {
        self.repository = repository
        self.locationHandler = locationHandler
        
        // User Story 4: Observe app returning from background (dynamic permission change handling)
        locationHandler.startObservingForeground { [weak self] status, didBecomeAuthorized in
            guard let self = self else { return }
            // Callback is already dispatched to main thread by handler
            self.locationPermissionStatus = status
            // Auto-refresh ONLY if permission changed from unauthorized → authorized
            if didBecomeAuthorized {
                // Cancel previous task before starting new one
                self.loadTask?.cancel()
                self.loadTask = Task {
                    await self.loadAnimals()
                }
            }
        }
        
        // Load animals on initialization
        loadTask = Task {
            await loadAnimals()
        }
    }
    
    deinit {
        // User Story 3 (T065): Cancel active task to prevent memory leaks
        loadTask?.cancel()
        locationHandler.stopObservingForeground()
    }
    
    // MARK: - Public Methods
    
    /**
     * Requests data refresh from external source (e.g., coordinator after report sent).
     * User Story 3 (T066): Called by coordinator when user successfully submits announcement.
     * Encapsulates refresh logic without exposing internal loadAnimals() implementation.
     */
    func requestToRefreshData() {
        // User Story 3 (T065): Cancel previous load task before starting new one
        loadTask?.cancel()
        loadTask = Task { @MainActor in
            await loadAnimals()
        }
    }
    
    /**
     * Loads animals from repository with location-aware filtering.
     * Handles permission requests, fetches user location if granted, then queries animals.
     * Updates @Published properties (cardViewModels, isLoading, errorMessage, currentLocation, locationPermissionStatus).
     * Called automatically on init and can be called manually to refresh.
     *
     * User Story 1 (P1): Location-Aware Content for Authorized Users
     * - Checks permission status
     * - Fetches location if authorized
     * - Queries with coordinates when available
     * - Falls back to query without coordinates on any failure
     *
     * User Story 2 (P2): First-Time Location Permission Request
     * - Requests permission when status is .notDetermined (iOS shows system alert automatically)
     * - Updates status after user responds to alert
     * - Fetches location if user grants permission
     * - Continues query without location if user denies (non-blocking fallback)
     *
     * User Story 3 (P3): Task Cancellation for Refresh
     * - Cancels previous load task if still running (prevents stale data)
     * - Checks for cancellation after async operations
     *
     * Note: Calls repository directly per iOS MVVM-C architecture (no use case layer).
     */
    func loadAnimals() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Delegate location permission handling to handler
            let result = await locationHandler.requestLocationWithPermissions()
            
            // User Story 3 (T067): Check for cancellation after async operation
            try Task.checkCancellation()
            
            // Update published state with results
            locationPermissionStatus = result.status
            currentLocation = result.location
            
            // User Story 3: Show custom popup for denied/restricted (once per session)
            // ViewModel decides policy: show once per session
            if result.status.shouldShowCustomPopup && !hasShownPermissionAlert {
                showPermissionDeniedAlert = true
                hasShownPermissionAlert = true
            }
            
            // Query animals with optional location (nil = no filtering, graceful fallback per FR-009)
            // Non-blocking: query executes regardless of permission outcome (FR-007, SC-001)
            let animals = try await repository.getAnimals(near: result.location)
            
            // Check for cancellation before updating UI state
            try Task.checkCancellation()
            
            updateCardViewModels(with: animals)
        } catch is CancellationError {
            // Task was cancelled - this is normal, don't show error to user
            // Keep loading state but don't update error message
        } catch {
            self.errorMessage = L10n.AnimalList.Error.loadingFailed
        }
        
        isLoading = false
    }
    
    /**
     * Handles actions from animal card ViewModels.
     * Routes actions to appropriate coordinator closures.
     *
     * - Parameter action: Action performed on card
     */
    private func handleAnimalAction(_ action: AnimalAction) {
        switch action {
        case .selected(let id):
            selectAnimal(id: id)
        }
    }
    
    /**
     * Updates card ViewModels with fresh animal data.
     * Creates new VMs for new animals, updates existing ones, removes deleted ones.
     * Maintains array order from animals list.
     * Deduplicates animals by ID (keeps first occurrence).
     *
     * - Parameter animals: Fresh list of animals from repository
     */
    private func updateCardViewModels(with animals: [Animal]) {
        // Deduplicate animals by ID (keep first occurrence)
        var seenIDs = Set<String>()
        let uniqueAnimals = animals.filter { animal in
            seenIDs.insert(animal.id).inserted
//            if seenIDs.contains(animal.id) {
//                return false
//            } else {
//                seenIDs.insert(animal.id)
//                return true
//            }
        }
        
        // Create dictionary of existing ViewModels by ID for fast lookup
        let existingVMsByID = Dictionary(uniqueKeysWithValues: cardViewModels.map { ($0.animal.id, $0) })
        
        // Build new array maintaining order, reusing or creating ViewModels
        self.cardViewModels = uniqueAnimals.map { animal in
            if let existingVM = existingVMsByID[animal.id] {
                // Reuse and update existing ViewModel
                existingVM.update(with: animal)
                return existingVM
            } else {
                // Create new ViewModel for new animal
                return AnimalCardViewModel(
                    animal: animal,
                    onAction: handleAnimalAction
                )
            }
        }
    }
    
    /**
     * Handles animal selection.
     * Calls coordinator closure for navigation.
     *
     * - Parameter id: ID of selected animal
     */
    func selectAnimal(id: String) {
        onAnimalSelected?(id)
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
    
    /**
     * Updates a single animal in the list.
     * Useful when returning from detail screen or receiving real-time updates.
     * Finds and updates the corresponding card ViewModel.
     *
     * - Parameter animal: Updated animal entity
     */
    func animalUpdated(_ animal: Animal) {
        // Find and update the card ViewModel
        if let cardVM = cardViewModels.first(where: { $0.animal.id == animal.id }) {
            cardVM.update(with: animal)
        }
    }
    
    // MARK: - User Story 3: Recovery Path Methods
    
    /**
     * Opens iOS Settings app to this app's permission screen.
     * Delegates to coordinator via callback (MVVM-C pattern).
     * View → ViewModel → Coordinator → UIApplication
     */
    func openSettings() {
        onOpenAppSettings?()
    }
    
    /**
     * Continues without location when user dismisses permission popup.
     * Queries animals without location filtering (fallback mode).
     * Dismisses popup and allows user to browse animals.
     */
    func continueWithoutLocation() async {
        showPermissionDeniedAlert = false
        
        do {
            // Query without location (fallback mode)
            let animals = try await repository.getAnimals(near: nil)
            updateCardViewModels(with: animals)
        } catch {
            self.errorMessage = L10n.AnimalList.Error.loadingFailed
        }
    }
    
}

