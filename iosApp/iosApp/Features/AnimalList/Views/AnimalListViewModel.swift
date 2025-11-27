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
    @Published var currentLocation: UserLocation?
    
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
    private let locationService: LocationServiceProtocol
    
    // MARK: - Session State (User Story 3)
    
    /// Session-level flag preventing repeated permission popups (FR-013)
    private var hasShownPermissionAlert = false
    
    // MARK: - User Story 4: App Lifecycle Observation
    
    /// Notification observer token for app foreground notifications
    private var foregroundObserver: NSObjectProtocol?

    // MARK: - Initialization
    
    /**
     * Initializes ViewModel with repository and location service.
     * Immediately loads animals on creation.
     *
     * - Parameter repository: Repository for fetching animals (injected)
     * - Parameter locationService: Service for location permissions and fetching (injected)
     */
    init(
        repository: AnimalRepositoryProtocol,
        locationService: LocationServiceProtocol
    ) {
        self.repository = repository
        self.locationService = locationService
        
        // User Story 4: Observe app returning from background (dynamic permission change handling)
        foregroundObserver = NotificationCenter.default.addObserver(
            forName: UIApplication.willEnterForegroundNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            // App is about to enter foreground (user may have changed permissions in Settings)
            Task { @MainActor [weak self] in
                await self?.checkPermissionStatusChange()
            }
        }
        
        // Load animals on initialization
        Task {
            await loadAnimals()
        }
    }
    
    deinit {
        // Remove notification observer on deallocation
        if let observer = foregroundObserver {
            NotificationCenter.default.removeObserver(observer)
        }
    }
    
    // MARK: - Public Methods
    
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
     * Note: Calls repository directly per iOS MVVM-C architecture (no use case layer).
     */
    func loadAnimals() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Check current permission status
            var status = await locationService.authorizationStatus
            
            // User Story 2: Request permission if not determined (iOS shows system alert automatically)
            if status == .notDetermined {
                status = await locationService.requestWhenInUseAuthorization()
            }
            
            // Update published status (reactive UI updates)
            locationPermissionStatus = status
            
            // User Story 3: Show custom popup for denied/restricted (once per session)
            if status.shouldShowCustomPopup && !hasShownPermissionAlert {
                showPermissionDeniedAlert = true
                hasShownPermissionAlert = true
            }
            
            // Fetch location if authorized (User Story 1 & 2: authorized users get location-aware content)
            if status.isAuthorized {
                currentLocation = await locationService.requestLocation()
            } else {
                currentLocation = nil
            }
            
            // Query animals with optional location (nil = no filtering, graceful fallback per FR-009)
            // Non-blocking: query executes regardless of permission outcome (FR-007, SC-001)
            let animals = try await repository.getAnimals(near: currentLocation)
            updateCardViewModels(with: animals)
        } catch {
            self.errorMessage = error.localizedDescription
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
            self.errorMessage = error.localizedDescription
        }
    }
    
    // MARK: - User Story 4: Dynamic Permission Change Handling
    
    /**
     * Checks if location permission status changed when app returns to foreground.
     * Refreshes animal list with location if permission changed from unauthorized to authorized.
     * Updates permission status property reactively.
     *
     * User Story 4: Dynamic Permission Change Handling
     * - Detects permission changes when user returns from Settings
     * - Auto-refreshes with location when permission granted
     * - Updates status without refresh when permission denied
     *
     * Called automatically when app returns to foreground (NotificationCenter observation in init).
     */
    func checkPermissionStatusChange() async {
        let newStatus = await locationService.authorizationStatus
        
        // If permission changed from unauthorized → authorized, refresh with location (FR-011)
        if (newStatus.isAuthorized && !locationPermissionStatus.isAuthorized) || newStatus == .notDetermined {
            locationPermissionStatus = newStatus
            await loadAnimals() // Refresh with location
        } else {
            // Update status without refresh (permission denied or unchanged)
            locationPermissionStatus = newStatus
        }
    }
}

