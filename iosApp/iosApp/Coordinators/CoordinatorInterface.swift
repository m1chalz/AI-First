import UIKit

/// Protocol defining the contract for all coordinators.
/// Coordinators manage navigation flows and view controller presentation.
protocol CoordinatorInterface: AnyObject {
    /// Navigation controller used for presenting view controllers.
    /// Stored as weak reference to prevent retain cycles.
    var navigationController: UINavigationController? { get set }
    
    /// Starts the coordinator's navigation flow.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async
}

extension CoordinatorInterface {
    /// Convenience method to start coordinator with animation enabled.
    func start() async {
        await start(animated: true)
    }
}

