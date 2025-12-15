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
    private let repository: AnnouncementRepositoryProtocol
    
    // MARK: - Initialization
    
    /// Creates a new PetDetailsCoordinator
    /// - Parameters:
    ///   - navigationController: Navigation controller to push views onto
    ///   - petId: ID of the pet to display
    ///   - repository: Repository for fetching pet data
    init(
        navigationController: UINavigationController?,
        petId: String,
        repository: AnnouncementRepositoryProtocol
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
        
        viewModel.onShowMap = { [weak self] coordinate in
            self?.openInMaps(coordinate: coordinate)
        }
        
        let detailsView = NavigationBackHiding {
            PetDetailsView(viewModel: viewModel)
        }
        let hostingController = UIHostingController(rootView: detailsView)
        
        // Create custom back button (chevron only, no text)
        let backButton = UIButton(type: .system)
        backButton.setImage(UIImage(systemName: "chevron.left"), for: .normal)
        backButton.tintColor = UIColor(hex: "#2D2D2D")
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
    
    // MARK: - Private Methods
    
    /// Opens Apple Maps with a pin at the specified coordinate
    /// - Parameter coordinate: Geographic coordinate to display
    private func openInMaps(coordinate: Coordinate) {
        let urlString = "https://maps.apple.com/?ll=\(coordinate.latitude),\(coordinate.longitude)&q=X"
        
        guard let url = URL(string: urlString),
              UIApplication.shared.canOpenURL(url) else {
            print("Failed to open Apple Maps: invalid URL or app not available")
            return
        }
        
        UIApplication.shared.open(url)
    }
}

