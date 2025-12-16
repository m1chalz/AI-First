import UIKit
import SwiftUI

/// Coordinator for Home tab navigation following MVVM-C architecture.
/// Manages landing page presentation and cross-tab navigation.
///
/// **Root coordinator pattern**: Creates its own UINavigationController in init(),
/// suitable for use as a tab's root coordinator where navigation controller
/// is not provided externally.
///
/// **Same pattern as AnnouncementListCoordinator**:
/// - Simple init() with only closure parameter
/// - Dependencies fetched in start() from ServiceContainer.shared
///
/// **Responsibilities**:
/// - Creates and configures LandingPageViewModel
/// - Sets up cross-tab navigation closure
/// - Creates UIHostingController with SwiftUI view
/// - Configures navigation bar appearance
@MainActor
class HomeCoordinator: CoordinatorInterface {
    var navigationController: UINavigationController?
    var childCoordinators: [CoordinatorInterface] = []
    
    // MARK: - Private Properties
    
    /// Closure to handle cross-tab navigation when user taps announcement
    private let onShowPetDetails: (String) -> Void
    
    // MARK: - Initialization
    
    /// Creates HomeCoordinator with its own navigation controller.
    ///
    /// **Root coordinator pattern**: This coordinator creates and owns its
    /// UINavigationController, making it suitable for use as a tab's root.
    ///
    /// - Parameter onShowPetDetails: Closure invoked when user taps announcement card.
    ///   TabCoordinator provides this closure to handle cross-tab navigation.
    init(onShowPetDetails: @escaping (String) -> Void) {
        self.navigationController = UINavigationController()
        self.onShowPetDetails = onShowPetDetails
    }
    
    // MARK: - CoordinatorInterface Methods
    
    /// Starts the Home tab flow by showing landing page.
    /// Fetches dependencies from ServiceContainer (same as AnnouncementListCoordinator).
    ///
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Get dependencies from DI container (same pattern as AnnouncementListCoordinator)
        let container = ServiceContainer.shared
        let repository = container.announcementRepository
        let locationHandler = container.locationPermissionHandler
        
        // Create ViewModel with dependencies
        let viewModel = LandingPageViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnnouncementTapped: onShowPetDetails
        )
        
        // Create SwiftUI view with ViewModel
        let landingPageView = LandingPageView(viewModel: viewModel)
        
        // Wrap in UIHostingController for UIKit navigation
        let hostingController = UIHostingController(rootView: landingPageView)
        
        // Configure navigation bar (UIKit - coordinator responsibility, NOT SwiftUI .navigationTitle())
        hostingController.title = L10n.Tabs.home
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Show navigation bar and set as root
        navigationController.isNavigationBarHidden = false
        navigationController.setViewControllers([hostingController], animated: animated)
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit HomeCoordinator")
    }
}

