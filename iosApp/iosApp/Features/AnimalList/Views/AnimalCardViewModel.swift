import Foundation

/**
 * Actions that can be performed on an animal card.
 * Used for communication between card ViewModel and parent ViewModel.
 */
enum AnimalAction {
    case selected(String)
    // Future actions can be added here (e.g., toggleFavorite, share)
}

/**
 * ViewModel for a single animal card.
 * Manages card-specific state and handles user interactions.
 * Transforms raw Animal data into presentation-ready properties.
 * Communicates with parent ViewModel via action callback.
 *
 * Note: Instance is owned and cached by AnimalListViewModel.
 * Uses @ObservedObject in view (not @StateObject) since lifecycle is managed externally.
 */
@MainActor
class AnimalCardViewModel: ObservableObject {
    // MARK: - Published Properties
    
    /// Card-specific UI state (e.g., for future animations, expanded state)
    @Published var isExpanded = false
    
    // MARK: - Internal Properties (accessible for coordinator/parent)
    
    /// Raw animal data (internal - views should use computed properties)
    @Published private(set) var animal: Animal
    
    // MARK: - Computed Properties (Presentation Layer)
    
    /// Formatted location text: "City, +XYZkm"
    var locationText: String {
        L10n.AnimalCard.Location.format(
            animal.location.city,
            Int(animal.location.radiusKm)
        )
    }
    
    /// Species display name
    var speciesName: String {
        animal.species.displayName
    }
    
    /// Breed name
    var breedName: String {
        animal.breed
    }
    
    /// Status badge text
    var statusText: String {
        animal.status.displayName
    }
    
    /// Status badge color as hex string (e.g., "#FF6B6B")
    var statusColorHex: String {
        animal.status.badgeColor
    }
    
    /// Formatted date text
    var dateText: String {
        animal.lastSeenDate
        // TODO: Format properly when date format is finalized
        // DateFormatter.shared.format(animal.lastSeenDate)
    }
    
    /// Unique identifier for SwiftUI ForEach
    var id: String {
        animal.id
    }
    
    // MARK: - Private Properties
    
    /// Callback to communicate actions to parent ViewModel
    private let onAction: (AnimalAction) -> Void
    
    // MARK: - Initialization
    
    /**
     * Initializes card ViewModel with animal data and action callback.
     *
     * - Parameter animal: Animal entity to display
     * - Parameter onAction: Callback invoked when user performs actions on card
     */
    init(animal: Animal, onAction: @escaping (AnimalAction) -> Void) {
        self.animal = animal
        self.onAction = onAction
    }
    
    // MARK: - Public Methods
    
    /**
     * Updates card with new animal data.
     * Called by parent ViewModel when data refreshes.
     *
     * - Parameter animal: Updated animal entity
     */
    func update(with animal: Animal) {
        self.animal = animal
    }
    
    /**
     * Handles card tap gesture.
     * Notifies parent ViewModel via action callback.
     */
    func handleTap() {
        onAction(.selected(animal.id))
    }
}

