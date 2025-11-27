import Foundation

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
    
    // MARK: - Location Properties (User Story 1)
    
    /// Current location permission status
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    
    /// Current user location (nil if unavailable)
    @Published var currentLocation: UserLocation?
    
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
    
    // MARK: - Dependencies
    
    private let repository: AnimalRepositoryProtocol
    private let locationService: LocationServiceProtocol

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
        
        // Load animals on initialization
        Task {
            await loadAnimals()
        }
    }
    
    // MARK: - Public Methods
    
    /**
     * Loads animals from repository with location-aware filtering.
     * Fetches user location if permission is granted, then queries animals.
     * Updates @Published properties (cardViewModels, isLoading, errorMessage, currentLocation).
     * Called automatically on init and can be called manually to refresh.
     *
     * User Story 1 (P1): Location-Aware Content for Authorized Users
     * - Checks permission status
     * - Fetches location if authorized
     * - Queries with coordinates when available
     * - Falls back to query without coordinates on any failure
     *
     * Note: Calls repository directly per iOS MVVM-C architecture (no use case layer).
     */
    func loadAnimals() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Check current permission status (User Story 1)
            let status = await locationService.authorizationStatus
            locationPermissionStatus = status
            
            // Fetch location if authorized (User Story 1: authorized users get location-aware content)
            if status.isAuthorized {
                currentLocation = await locationService.requestLocation()
            } else {
                currentLocation = nil
            }
            
            // Query animals with optional location (nil = no filtering, graceful fallback per FR-009)
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
}

