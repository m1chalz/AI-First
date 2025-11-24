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
        
        // Setup coordinator callbacks
        viewModel.onBack = { [weak self] in
            self?.finish()
        }
        
        let detailsView = PetDetailsView(viewModel: viewModel)
        let hostingController = UIHostingController(rootView: detailsView)
        
        // Configure navigation bar
        hostingController.title = "Pet Details"
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Configure navigation bar appearance
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .white
        appearance.shadowColor = .clear
        
        hostingController.navigationItem.standardAppearance = appearance
        hostingController.navigationItem.scrollEdgeAppearance = appearance
        
        // Hide default back button
        hostingController.navigationItem.hidesBackButton = true
        
        // Create custom back button
        let backButton = UIButton(type: .system)
        backButton.setImage(UIImage(systemName: "chevron.left"), for: .normal)
        backButton.setTitle(" Back", for: .normal)
        backButton.titleLabel?.font = UIFont.systemFont(ofSize: 17)
        backButton.tintColor = UIColor(hex: "#007AFF")
        backButton.addAction(UIAction { [weak viewModel] _ in
            viewModel?.handleBack()
        }, for: .touchUpInside)
        
        let backBarButtonItem = UIBarButtonItem(customView: backButton)
        hostingController.navigationItem.leftBarButtonItem = backBarButtonItem
        
        navigationController?.pushViewController(hostingController, animated: animated)
    }
    
    /// Cleans up coordinator when flow is finished
    func finish() {
        navigationController?.popViewController(animated: true)
//        parentCoordinator?.childDidFinish(self)
    }
}

