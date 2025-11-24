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
    
    // MARK: - Formatting Helpers
    
    func formatMicrochip(_ microchip: String?) -> String {
        guard let microchip = microchip else { return "—" }
        
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
    
    func formatSpecies(_ species: String) -> String {
        return species.capitalized
    }
    
    func genderIcon(_ gender: String) -> String {
        switch gender.uppercased() {
        case "MALE":
            return "arrow.up.right"
        case "FEMALE":
            return "arrow.down.right"
        default:
            return "questionmark"
        }
    }
    
    func formatDate(_ dateString: String) -> String {
        // Input format: YYYY-MM-DD (e.g., "2025-11-18")
        // Output format: MMM DD, YYYY (e.g., "Nov 18, 2025")
        
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = inputFormatter.date(from: dateString) else {
            return dateString // Return as-is if parsing fails
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        
        return outputFormatter.string(from: date)
    }
    
    func formatRadius(_ radius: Int?) -> String? {
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

