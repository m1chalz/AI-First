import UIKit
import SwiftUI

/// Coordinator for Pet Details Screen navigation flow.
/// Manages navigation lifecycle and dependency injection for pet details feature.
class PetDetailsCoordinator: CoordinatorInterface {
    // MARK: - Properties
    
    weak var parentCoordinator: CoordinatorInterface?
    var childCoordinators: [CoordinatorInterface] = []
    var navigationController: UINavigationController?

    private let petId: String
    private let repository: AnimalRepositoryProtocol
    
    // MARK: - Initialization
    
    /// Creates a new PetDetailsCoordinator
    /// - Parameters:
    ///   - navigationController: Navigation controller to push views onto
    ///   - petId: ID of the pet to display
    ///   - repository: Repository for fetching pet data
    init(
        navigationController: UINavigationController?,
        petId: String,
        repository: AnimalRepositoryProtocol
    ) {
        self.navigationController = navigationController
        self.petId = petId
        self.repository = repository
    }
    
    // MARK: - CoordinatorInterface
    
    /// Starts the pet details flow by pushing the details screen
    func start(animated: Bool) async {
        let viewModel = PetDetailsViewModel(
            repository: repository,
            petId: petId
        )
        
        let detailsView = PetDetailsView(viewModel: viewModel)
        let hostingController = UIHostingController(rootView: detailsView)
        
        // Configure navigation bar
        hostingController.title = "Pet Details"
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Configure back button appearance
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .white
        appearance.shadowColor = .clear
        
        // Style back button text
        let backButtonAppearance = UIBarButtonItemAppearance()
        backButtonAppearance.normal.titleTextAttributes = [
            .foregroundColor: UIColor(hex: "#007AFF"),
            .font: UIFont.systemFont(ofSize: 17)
        ]
        appearance.backButtonAppearance = backButtonAppearance
        
        hostingController.navigationItem.standardAppearance = appearance
        hostingController.navigationItem.scrollEdgeAppearance = appearance
        
        navigationController?.pushViewController(hostingController, animated: animated)
    }
    
    /// Cleans up coordinator when flow is finished
    func finish() {
        navigationController?.popViewController(animated: true)
//        parentCoordinator?.childDidFinish(self)
    }
}

