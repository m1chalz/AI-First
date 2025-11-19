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
    
    /// List of animals displayed to user
    @Published var animals: [Animal] = []
    
    /// Loading state indicator
    @Published var isLoading: Bool = false
    
    /// Error message (nil if no error)
    @Published var errorMessage: String?
    
    // MARK: - Computed Properties
    
    /// Computed: true when data loaded but list is empty
    var isEmpty: Bool {
        animals.isEmpty && !isLoading && errorMessage == nil
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
     * Updates @Published properties (animals, isLoading, errorMessage).
     * Called automatically on init and can be called manually to refresh.
     */
    func loadAnimals() async {
        isLoading = true
        errorMessage = nil
        
        do {
            self.animals = try await getAnimalsUseCase.invoke()
        } catch {
            self.errorMessage = error.localizedDescription
        }
        
        isLoading = false
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
}

