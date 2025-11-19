import UIKit

/**
 * Root application coordinator.
 * Manages app-level navigation and initializes sub-coordinators.
 * Sets AnimalListCoordinator as root flow per FR-010.
 */
class AppCoordinator: CoordinatorInterface {
    var navigationController: UINavigationController?
    var childCoordinators: [CoordinatorInterface] = []
    
    /**
     * Initializes app coordinator with navigation controller.
     *
     * - Parameter navigationController: Root navigation controller
     */
    init(navigationController: UINavigationController) {
        self.navigationController = navigationController
    }
    
    /**
     * Starts the app coordinator by launching the animal list flow.
     * Sets AnimalListScreen as primary entry point per FR-010.
     * 
     * - Parameter animated: Whether to animate the transition
     */
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        let animalListCoordinator = AnimalListCoordinator(navigationController: navigationController)
        childCoordinators.append(animalListCoordinator)
        await animalListCoordinator.start(animated: animated)
    }
}

