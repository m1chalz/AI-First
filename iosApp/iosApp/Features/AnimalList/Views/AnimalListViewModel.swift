import Foundation
import Shared

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
    
    private let getAnimalsUseCase: GetAnimalsUseCase
    
    // MARK: - Initialization
    
    /**
     * Initializes ViewModel with use case.
     * Immediately loads animals on creation.
     *
     * - Parameter getAnimalsUseCase: Use case for fetching animals (injected)
     */
    init(getAnimalsUseCase: GetAnimalsUseCase) {
        self.getAnimalsUseCase = getAnimalsUseCase
        
        // Load animals on initialization
        Task {
            await loadAnimals()
        }
    }
    
    // MARK: - Public Methods
    
    /**
     * Loads animals from repository.
     * Updates @Published properties (cardViewModels, isLoading, errorMessage).
     * Called automatically on init and can be called manually to refresh.
     */
    func loadAnimals() async {
        isLoading = true
        errorMessage = nil
        
        do {
            let animals = try await getAnimalsUseCase.invoke()
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
     *
     * - Parameter animals: Fresh list of animals from repository
     */
    private func updateCardViewModels(with animals: [Animal]) {
        // Create dictionary of existing ViewModels by ID for fast lookup
        let existingVMsByID = Dictionary(uniqueKeysWithValues: cardViewModels.map { ($0.animal.id, $0) })
        
        // Build new array maintaining order, reusing or creating ViewModels
        self.cardViewModels = animals.map { animal in
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

