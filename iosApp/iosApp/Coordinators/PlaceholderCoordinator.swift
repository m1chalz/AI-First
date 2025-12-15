import UIKit
import SwiftUI

/// Coordinator for placeholder "Coming soon" screens.
///
/// **Root coordinator pattern**: Creates its own UINavigationController in init,
/// suitable for use as a tab's root coordinator where navigation controller
/// is not provided externally.
///
/// Used for tabs that are not yet implemented: Home, Found Pet, Contact Us, Account.
/// Each PlaceholderCoordinator manages a single placeholder screen within its
/// own navigation stack.
@MainActor
final class PlaceholderCoordinator: CoordinatorInterface {
    
    // MARK: - CoordinatorInterface Properties
    
    var navigationController: UINavigationController?
    var childCoordinators: [CoordinatorInterface] = []
    
    // MARK: - Private Properties
    
    /// Title to display on the placeholder screen.
    private let title: String
    
    // MARK: - Initialization
    
    /// Creates a PlaceholderCoordinator with its own navigation controller.
    ///
    /// **Root coordinator pattern**: This coordinator creates and owns its
    /// UINavigationController, making it suitable for use as a tab's root.
    ///
    /// - Parameter title: Title to display on the placeholder screen (e.g., "Home")
    init(title: String) {
        self.title = title
        self.navigationController = UINavigationController()
    }
    
    // MARK: - CoordinatorInterface Methods
    
    /// Starts the placeholder flow by creating and displaying the placeholder screen.
    ///
    /// Creates PlaceholderViewModel, wraps PlaceholderView in UIHostingController,
    /// and sets it as the navigation controller's root view controller.
    ///
    /// - Parameter animated: Whether to animate the transition (typically false for initial load)
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Create ViewModel with title
        let viewModel = PlaceholderViewModel(title: title)
        
        // Create SwiftUI view
        let view = PlaceholderView(viewModel: viewModel)
        
        // Wrap in UIHostingController
        let hostingController = UIHostingController(rootView: view)
        
        // Configure navigation bar to be hidden (placeholder doesn't need nav bar)
        navigationController.isNavigationBarHidden = true
        
        // Set as root view controller
        navigationController.setViewControllers([hostingController], animated: animated)
    }
}
