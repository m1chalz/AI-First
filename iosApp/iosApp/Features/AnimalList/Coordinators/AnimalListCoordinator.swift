import UIKit
import SwiftUI

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
    
    /// Animal List ViewModel reference for triggering refresh after report sent (User Story 3: T066)
    /// Weak to avoid keeping ViewModel in memory when screen is not visible
    /// UIHostingController/SwiftUI holds the strong reference
    private weak var animalListViewModel: AnimalListViewModel?
    
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
        
        // Get dependencies from DI container
        let container = ServiceContainer.shared
        let repository = container.announcementRepository
        let locationHandler = container.locationPermissionHandler
        
        // Create ViewModel with dependencies (iOS MVVM-C: ViewModels call repositories directly)
        let animalListViewModel = AnimalListViewModel(
            repository: repository,
            locationHandler: locationHandler
        )
        
        // Store weak reference for refresh triggering after report sent (User Story 3: T066)
        self.animalListViewModel = animalListViewModel
        
        // Set up coordinator closures for navigation
        animalListViewModel.onAnimalSelected = { [weak self] animalId in
            self?.showAnimalDetails(animalId: animalId)
        }
        
        animalListViewModel.onReportMissing = { [weak self] in
            self?.showReportMissing()
        }
        
        animalListViewModel.onReportFound = { [weak self] in
            self?.showReportFound()
        }
        
        // User Story 3: Set coordinator callback for Settings navigation (MVVM-C pattern)
        animalListViewModel.onOpenAppSettings = { [weak self] in
            self?.openAppSettings()
        }
        
        // Create SwiftUI view with ViewModel
        let view = AnimalListView(viewModel: animalListViewModel)
        
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
        
        // Create repository (should use DI container in future)
        let repository = AnnouncementRepository()

        let coordinator = PetDetailsCoordinator(
            navigationController: navigationController,
            petId: animalId,
            repository: repository
        )

        childCoordinators.append(coordinator)

        Task { @MainActor in
            await coordinator.start(animated: true)
        }

    }
    
    /**
     * Shows Report Missing Animal form.
     * Creates and starts ReportMissingPetCoordinator as child coordinator.
     * User Story 3 (T066): Sets up callback chain to refresh list when report is sent.
     */
    private func showReportMissing() {
        guard let navigationController = navigationController else { return }
        
        // Create child coordinator
        let reportCoordinator = ReportMissingPetCoordinator(
            parentNavigationController: navigationController
        )
        reportCoordinator.parentCoordinator = self
        
        // User Story 3 (T066): Set callback to refresh list when user sends report
        // Coordinator -> ViewModel communication via public method
        reportCoordinator.onReportSent = { [weak self] in
            self?.animalListViewModel?.requestToRefreshData()
        }
        
        childCoordinators.append(reportCoordinator)
        
        // Start modal flow
        Task { @MainActor in
            await reportCoordinator.start(animated: true)
        }
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
    
    // MARK: - User Story 3: Settings Navigation
    
    /**
     * Opens iOS Settings app to this app's permission screen.
     * Handles system navigation (MVVM-C pattern: Coordinator manages navigation).
     *
     * User Story 3: Recovery path for denied permissions.
     */
    private func openAppSettings() {
        guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
            return
        }
        UIApplication.shared.open(settingsUrl)
    }
    
    // MARK: - CoordinatorInterface Protocol
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit AnimalListCoordinator")
    }
}

