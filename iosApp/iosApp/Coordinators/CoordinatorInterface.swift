import UIKit

/// Protocol defining the contract for all coordinators.
/// Coordinators manage navigation flows and view controller presentation.
@MainActor
protocol CoordinatorInterface: AnyObject {
    /// Navigation controller used for presenting view controllers.
    /// Stored as weak reference to prevent retain cycles.
    var navigationController: UINavigationController? { get set }
    
    /// Array of child coordinators managed by this coordinator.
    /// Used to maintain strong references to child coordinators.
    var childCoordinators: [CoordinatorInterface] { get set }
    
    /// Starts the coordinator's navigation flow.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async
    
    /// Called by child coordinator when it finishes its flow.
    /// Parent coordinator should remove child from childCoordinators array.
    /// - Parameter child: Child coordinator that finished
    func childDidFinish(_ child: CoordinatorInterface)
}

extension CoordinatorInterface {
    /// Convenience method to start coordinator with animation enabled.
    func start() async {
        await start(animated: true)
    }
    
    /// Default implementation of childDidFinish.
    /// Removes child coordinator from childCoordinators array to prevent memory leaks.
    /// - Parameter child: Child coordinator that finished
    func childDidFinish(_ child: CoordinatorInterface) {
        childCoordinators.removeAll { $0 === child }
    }
}

