import UIKit
import SwiftUI

/// Coordinator for the main list screen.
/// Manages presentation of the list view using existing ContentView.
class ListScreenCoordinator: CoordinatorInterface {
    
    var navigationController: UINavigationController?
    
    /// Starts the list screen by replacing navigation root with ContentView.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        guard let navigationController = navigationController else {
            assertionFailure("NavigationController is nil in ListScreenCoordinator")
            return
        }
        
        // Wrap existing ContentView in UIHostingController
        let contentView = ContentView()
        let hostingController = UIHostingController(rootView: contentView)
        
        // Replace root view controller
        navigationController.setViewControllers([hostingController], animated: animated)
    }
}

