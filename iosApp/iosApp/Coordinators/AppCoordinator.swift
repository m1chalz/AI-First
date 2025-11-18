import UIKit

/// Root application coordinator.
/// Manages app-level navigation and initializes sub-coordinators.
class AppCoordinator: CoordinatorInterface {
    
    weak var navigationController: UINavigationController?
    
    // Sub-coordinators (lazy initialization)
    private lazy var listScreenCoordinator: ListScreenCoordinator = {
        let coordinator = ListScreenCoordinator()
        coordinator.navigationController = self.navigationController
        return coordinator
    }()
    
    /// Starts the app coordinator by launching the list screen flow.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        await listScreenCoordinator.start(animated: animated)
    }
}

