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
    
    // MARK: - Tab Navigation Closures
    
    /// Closure to switch to Lost Pet tab with optional announcement ID.
    /// - nil: Just switch tab (hero button, "View All")
    /// - id: Switch tab and show pet details (announcement card tap)
    /// Set by TabCoordinator during initialization.
    var onSwitchToLostPetTab: ((String?) -> Void)?
    
    /// Closure to switch to Found Pet tab.
    /// Set by TabCoordinator during initialization.
    var onSwitchToFoundPetTab: (() -> Void)?
    
    // MARK: - Initialization
    
    /// Creates HomeCoordinator with its own navigation controller.
    ///
    /// **Root coordinator pattern**: This coordinator creates and owns its
    /// UINavigationController, making it suitable for use as a tab's root.
    ///
    /// - Parameters:
    ///   - repository: Repository for fetching announcements
    ///   - locationHandler: Handler for location permissions
    init(
        repository: AnnouncementRepositoryProtocol,
        locationHandler: LocationPermissionHandler
    ) {
        self.navigationController = UINavigationController()
        self.repository = repository
        self.locationHandler = locationHandler
    }
    
    // MARK: - CoordinatorInterface Methods
    
    /// Starts the Home tab flow by showing landing page.
    ///
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        guard let navigationController = navigationController else { return }
        
        // Create ViewModel with injected dependencies
        // onAnnouncementTapped uses the same closure as hero button (with id)
        let viewModel = LandingPageViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnnouncementTapped: { [weak self] id in
                self?.onSwitchToLostPetTab?(id)
            }
        )
        
        // Set coordinator callback for Settings navigation (MVVM-C pattern)
        viewModel.onOpenAppSettings = { [weak self] in
            self?.openAppSettings()
        }
        
        // Set tab navigation closures (MVVM-C pattern - coordinator manages navigation)
        viewModel.onSwitchToLostPetTab = onSwitchToLostPetTab
        viewModel.onSwitchToFoundPetTab = onSwitchToFoundPetTab
        
        // Set fullscreen map navigation callback (MVVM-C pattern)
        viewModel.onShowFullscreenMap = { [weak self] location in
            self?.showFullscreenMap(userLocation: location)
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
    
    // MARK: - Fullscreen Map Navigation
    
    /// Navigates to fullscreen map view with push animation.
    /// Uses UIHostingController to wrap SwiftUI view in UIKit navigation.
    ///
    /// - Parameter userLocation: User's current location to center the map
    private func showFullscreenMap(userLocation: Coordinate) {
        guard let navigationController else { return }
        
        let viewModel = FullscreenMapViewModel(
            userLocation: userLocation,
            repository: repository
        )
        let view = NavigationBackHiding {
            FullscreenMapView(viewModel: viewModel)
        }
        let hostingController = UIHostingController(rootView: view)
        
        hostingController.title = L10n.FullscreenMap.navigationTitle
        hostingController.navigationItem.largeTitleDisplayMode = .never
        
        // Create custom back button (chevron only, no text, black color)
        let backButton = UIButton(type: .system)
        backButton.setImage(UIImage(systemName: "chevron.left"), for: .normal)
        backButton.tintColor = UIColor(hex: "#2D2D2D")
        backButton.addAction(UIAction { [weak navigationController] _ in
            navigationController?.popViewController(animated: true)
        }, for: .touchUpInside)
        
        let backBarButtonItem = UIBarButtonItem(customView: backButton)
        hostingController.navigationItem.leftBarButtonItem = backBarButtonItem
        
        navigationController.pushViewController(hostingController, animated: true)
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
