import UIKit
import SwiftUI

/// Coordinator for Pet Details Screen navigation flow.
/// Manages navigation lifecycle and dependency injection for pet details feature.
class PetDetailsCoordinator: CoordinatorInterface {
    // MARK: - Properties
    
    weak var parentCoordinator: CoordinatorInterface?
    var childCoordinators: [CoordinatorInterface] = []
    var navigationController: UINavigationController
    
    private let petId: String
    private let repository: AnimalRepositoryProtocol
    
    // MARK: - Initialization
    
    /// Creates a new PetDetailsCoordinator
    /// - Parameters:
    ///   - navigationController: Navigation controller to push views onto
    ///   - petId: ID of the pet to display
    ///   - repository: Repository for fetching pet data
    init(
        navigationController: UINavigationController,
        petId: String,
        repository: AnimalRepositoryProtocol
    ) {
        self.navigationController = navigationController
        self.petId = petId
        self.repository = repository
    }
    
    // MARK: - CoordinatorInterface
    
    /// Starts the pet details flow by pushing the details screen
    func start() {
        let viewModel = PetDetailsViewModel(
            repository: repository,
            petId: petId
        )
        
        // Setup coordinator callbacks
        viewModel.onBack = { [weak self] in
            self?.finish()
        }
        
        let detailsView = PetDetailsView(viewModel: viewModel)
        let hostingController = UIHostingController(rootView: detailsView)
        
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    /// Cleans up coordinator when flow is finished
    func finish() {
        navigationController.popViewController(animated: true)
        parentCoordinator?.childDidFinish(self)
    }
}

