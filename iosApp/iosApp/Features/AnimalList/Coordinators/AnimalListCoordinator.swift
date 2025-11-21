import UIKit
import SwiftUI
import Shared

/**
 * Coordinator for Animal List flow following MVVM-C architecture.
 * Manages navigation and creates UIHostingController for SwiftUI views.
 *
 * Responsibilities:
 * - Creates and configures AnimalListViewModel
 * - Sets up coordinator closures for navigation
 * - Creates UIHostingController with SwiftUI view
 * - Handles navigation to child screens (details, report forms)
 */
class AnimalListCoordinator: CoordinatorInterface {
    var navigationController: UINavigationController?
    var childCoordinators: [CoordinatorInterface] = []
    
    /**
     * Initializes coordinator with navigation controller.
     *
     * - Parameter navigationController: Navigation controller for managing view stack
     */
    init(navigationController: UINavigationController) {
        self.navigationController = navigationController
    }
    
    /**
     * Starts the Animal List flow.
     * Creates ViewModel, sets up navigation closures, and presents view.
     * 
     * - Parameter animated: Whether to animate the transition
     */
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Create repository (mock implementation for now)
        let repository = AnimalRepositoryImpl()
        let getAnimalsUseCase = GetAnimalsUseCase(repository: repository)
        
        // Create ViewModel
        let viewModel = AnimalListViewModel(getAnimalsUseCase: getAnimalsUseCase)
        
        // Set up coordinator closures for navigation
        viewModel.onAnimalSelected = { [weak self] animalId in
            self?.showAnimalDetails(animalId: animalId)
        }
        
        viewModel.onReportMissing = { [weak self] in
            self?.showReportMissing()
        }
        
        viewModel.onReportFound = { [weak self] in
            self?.showReportFound()
        }
        
        // Create SwiftUI view with ViewModel
        let view = AnimalListView(viewModel: viewModel)
        
        // Wrap in UIHostingController for UIKit navigation
        let hostingController = UIHostingController(rootView: view)
        
        // Configure navigation bar
        hostingController.title = L10n.AnimalList.navigationTitle
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Show navigation bar and replace splash screen with animal list
        navigationController.isNavigationBarHidden = false
        navigationController.setViewControllers([hostingController], animated: animated)
    }
    
    // MARK: - Navigation Methods
    
    /**
     * Shows animal details screen.
     * Mocked for now - will create AnimalDetailCoordinator in future.
     *
     * - Parameter animalId: ID of selected animal
     */
    private func showAnimalDetails(animalId: String) {
        // Placeholder: print log (real implementation will create child coordinator)
        print("Navigate to animal details: \(animalId)")
        // Future: let detailCoordinator = AnimalDetailCoordinator(...)
        // Future: detailCoordinator.start()
    }
    
    /**
     * Shows Report Missing Animal form.
     * Mocked for now - will create ReportMissingCoordinator in future.
     */
    private func showReportMissing() {
        // Placeholder: print log (real implementation will create child coordinator)
        print("Navigate to report missing form")
        // Future: let reportCoordinator = ReportMissingCoordinator(...)
        // Future: reportCoordinator.start()
    }
    
    /**
     * Shows Report Found Animal form.
     * Mocked for now - will create ReportFoundCoordinator in future.
     * Note: Not exposed in iOS mobile UI per design, but included for completeness.
     */
    private func showReportFound() {
        // Placeholder: print log (real implementation will create child coordinator)
        print("Navigate to report found form")
        // Future: let reportCoordinator = ReportFoundCoordinator(...)
        // Future: reportCoordinator.start()
    }
}

