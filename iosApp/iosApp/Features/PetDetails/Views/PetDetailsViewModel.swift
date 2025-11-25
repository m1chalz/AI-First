import Foundation

/// UI state for the Pet Details Screen
enum PetDetailsUiState {
    /// Initial state or loading data
    case loading
    
    /// Successfully loaded pet details
    case loaded(PetDetails)
    
    /// Failed to load pet details
    case error(String)
}

/// ViewModel for Pet Details Screen following MVVM-C pattern.
/// Manages state, handles user interactions, and communicates with coordinator.
@MainActor
class PetDetailsViewModel: ObservableObject {
    // MARK: - Published Properties
    
    /// Current UI state
    @Published private(set) var state: PetDetailsUiState = .loading
    
    // MARK: - Dependencies
    
    private let repository: AnimalRepositoryProtocol
    private let petId: String
    
    // MARK: - Coordinator Communication
    
    /// Callback to coordinator when user wants to navigate back
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    /// Creates a new PetDetailsViewModel
    /// - Parameters:
    ///   - repository: Repository for fetching pet data
    ///   - petId: ID of the pet to display
    init(repository: AnimalRepositoryProtocol, petId: String) {
        self.repository = repository
        self.petId = petId
    }
    
    // MARK: - Public Methods
    
    /// Loads pet details from repository
    func loadPetDetails() async {
        state = .loading
        
        do {
            let petDetails = try await repository.getPetDetails(id: petId)
            state = .loaded(petDetails)
        } catch {
            state = .error(error.localizedDescription)
        }
    }
    
    // MARK: - Formatting Helpers (Computed Properties)
    
    /// Formats microchip number as 000-000-000-000 if it's a plain number
    var formattedMicrochip: String {
        guard case .loaded(let petDetails) = state else { return "—" }
        guard let microchip = petDetails.microchipNumber else { return "—" }
        
        // Format as 000-000-000-000 if it's a plain number
        let digits = microchip.filter { $0.isNumber }
        guard digits.count >= 12 else { return microchip }
        
        let formatted = digits.enumerated().map { index, char -> String in
            if index > 0 && index % 3 == 0 && index < 12 {
                return "-\(char)"
            }
            return String(char)
        }.joined()
        
        return formatted
    }
    
    /// Returns capitalized species name
    var formattedSpecies: String {
        guard case .loaded(let petDetails) = state else { return "" }
        return petDetails.species.capitalized
    }
    
    /// Returns gender symbol (Unicode character)
    var genderSymbol: String {
        guard case .loaded(let petDetails) = state else { return "?" }
        switch petDetails.gender {
        case .male:
            return "♂"
        case .female:
            return "♀"
        case .unknown:
            return "?"
        }
    }
    
    /// Returns formatted date string (MMM dd, yyyy format)
    var formattedDate: String {
        guard case .loaded(let petDetails) = state else { return "" }
        // Input format: YYYY-MM-DD (e.g., "2025-11-18")
        // Output format: MMM DD, YYYY (e.g., "Nov 18, 2025")
        
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = inputFormatter.date(from: petDetails.lastSeenDate) else {
            return petDetails.lastSeenDate // Return as-is if parsing fails
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        
        return outputFormatter.string(from: date)
    }
    
    /// Returns formatted coordinates string (latitude, longitude)
    var formattedCoordinates: String {
        guard case .loaded(let petDetails) = state else { return "—" }
        return String(format: "%.4f, %.4f", petDetails.latitude, petDetails.longitude)
    }
    
    /// Returns formatted radius string or nil if not available
    var formattedRadius: String? {
        guard case .loaded(let petDetails) = state else { return nil }
        guard let radius = petDetails.locationRadius else { return nil }
        return L10n.PetDetails.Location.radiusFormat(radius)
    }
    
    /// Creates PetPhotoWithBadgesView.Model with pet details
    var photoWithBadgesModel: PetPhotoWithBadgesView.Model? {
        guard case .loaded(let petDetails) = state else { return nil }
        return PetPhotoWithBadgesView.Model(
            imageUrl: petDetails.photoUrl,
            status: petDetails.status,
            rewardText: petDetails.reward
        )
    }
    
    // MARK: - Static Formatting Helpers (for testing and reuse)
    
    /// Static helper to format microchip number
    static func formatMicrochip(_ microchip: String?) -> String {
        guard let microchip = microchip else { return "—" }
        
        let digits = microchip.filter { $0.isNumber }
        guard digits.count >= 12 else { return microchip }
        
        let formatted = digits.enumerated().map { index, char -> String in
            if index > 0 && index % 3 == 0 && index < 12 {
                return "-\(char)"
            }
            return String(char)
        }.joined()
        
        return formatted
    }
    
    /// Static helper to format species
    static func formatSpecies(_ species: String) -> String {
        return species.capitalized
    }
    
    /// Static helper to get gender symbol (Unicode character)
    static func genderSymbol(_ gender: AnimalGender) -> String {
        switch gender {
        case .male:
            return "♂"
        case .female:
            return "♀"
        case .unknown:
            return "?"
        }
    }
    
    /// Static helper to format date string
    static func formatDate(_ dateString: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = inputFormatter.date(from: dateString) else {
            return dateString
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        
        return outputFormatter.string(from: date)
    }
    
    /// Static helper to format radius
    static func formatRadius(_ radius: Int?) -> String? {
        guard let radius = radius else { return nil }
        return L10n.PetDetails.Location.radiusFormat(radius)
    }

    /// Retries loading pet details after an error
    func retry() {
        Task {
            await loadPetDetails()
        }
    }
    
    /// Handles back navigation
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Action Handlers
    
    /// Handles remove report action (placeholder for future feature)
    func handleRemoveReport() {
        print("Remove Report button tapped (placeholder)")
        // TODO: Implement report removal in future feature
    }
    
    /// Handles show on map action (placeholder for future feature)
    func handleShowMap() {
        print("Show on the map button tapped (placeholder)")
        // TODO: Implement map view navigation in future feature
    }
}

