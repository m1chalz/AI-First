import UIKit
import SwiftUI

/// Coordinator for Home tab navigation following MVVM-C architecture.
/// Manages landing page presentation and cross-tab navigation.
///
/// **Root coordinator pattern**: Creates its own UINavigationController in init(),
/// suitable for use as a tab's root coordinator where navigation controller
/// is not provided externally.
///
/// **Constructor injection**: All dependencies passed via init (no ServiceContainer access).
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
    
    // MARK: - Dependencies
    
    private let repository: AnnouncementRepositoryProtocol
    private let locationHandler: LocationPermissionHandler
    
    /// Closure to handle cross-tab navigation when user taps announcement
    private let onShowPetDetails: (String) -> Void
    
    // MARK: - Initialization
    
    /// Creates HomeCoordinator with its own navigation controller.
    ///
    /// **Root coordinator pattern**: This coordinator creates and owns its
    /// UINavigationController, making it suitable for use as a tab's root.
    ///
    /// - Parameters:
    ///   - repository: Repository for fetching announcements
    ///   - locationHandler: Handler for location permissions
    ///   - onShowPetDetails: Closure invoked when user taps announcement card.
    ///     TabCoordinator provides this closure to handle cross-tab navigation.
    init(
        repository: AnnouncementRepositoryProtocol,
        locationHandler: LocationPermissionHandler,
        onShowPetDetails: @escaping (String) -> Void
    ) {
        self.navigationController = UINavigationController()
        self.repository = repository
        self.locationHandler = locationHandler
        self.onShowPetDetails = onShowPetDetails
    }
    
    // MARK: - CoordinatorInterface Methods
    
    /// Starts the Home tab flow by showing landing page.
    ///
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Create ViewModel with injected dependencies
        let viewModel = LandingPageViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnnouncementTapped: onShowPetDetails
        )
        
        // Set coordinator callback for Settings navigation (MVVM-C pattern)
        viewModel.onOpenAppSettings = { [weak self] in
            self?.openAppSettings()
        }
        
        // Create SwiftUI view with ViewModel
        let landingPageView = LandingPageView(viewModel: viewModel)
        
        // Wrap in UIHostingController for UIKit navigation
        let hostingController = UIHostingController(rootView: landingPageView)
        
        // Configure navigation bar (UIKit - coordinator responsibility, NOT SwiftUI .navigationTitle())
        hostingController.title = L10n.LandingPage.navigationTitle
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Show navigation bar and set as root
        navigationController.isNavigationBarHidden = false
        navigationController.setViewControllers([hostingController], animated: animated)
    }
    
    // MARK: - Settings Navigation
    
    /// Opens iOS Settings app to this app's permission screen.
    /// Handles system navigation (MVVM-C pattern: Coordinator manages navigation).
    private func openAppSettings() {
        guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
            return
        }
        UIApplication.shared.open(settingsUrl)
    }
    
    // MARK: - Deinitialization
    
    deinit {
        print("deinit HomeCoordinator")
    }
}

