import Foundation

/// UI state for the Pet Details Screen
enum PetDetailsUiState: Equatable {
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
}

