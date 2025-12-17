import UIKit

/// Coordinator managing the main tab bar navigation.
///
/// **Does NOT conform to CoordinatorInterface** - manages UITabBarController,
/// not UINavigationController. TabCoordinator creates and configures the main
/// tab bar with 5 tabs, each having its own navigation stack via child coordinators.
///
/// **Synchronous initialization**: All child coordinators and their navigation controllers
/// are created in init(), making the complete tab bar structure available immediately.
/// The start() method asynchronously populates content in each tab.
///
/// **Tab structure**:
/// - Home: PlaceholderCoordinator (Coming soon)
/// - Lost Pet: AnnouncementListCoordinator (Existing feature)
/// - Found Pet: PlaceholderCoordinator (Coming soon)
/// - Contact Us: PlaceholderCoordinator (Coming soon)
/// - Account: PlaceholderCoordinator (Coming soon)
@MainActor
final class TabCoordinator {
    
    // MARK: - Public Properties
    
    /// The tab bar controller managed by this coordinator.
    /// Use this property to set as window's rootViewController.
    var tabBarController: UITabBarController {
        _tabBarController
    }
    
    /// Number of child coordinators (for testing purposes).
    var childCoordinatorCount: Int {
        childCoordinators.count
    }
    
    // MARK: - Private Properties
    
    /// Internal tab bar controller instance.
    private let _tabBarController: UITabBarController
    
    /// Child coordinators managing each tab's navigation stack.
    /// Strong references prevent deallocation while tabs are active.
    private var childCoordinators: [CoordinatorInterface] = []
    
    // MARK: - Initialization
    
    /// Creates TabCoordinator with fully configured tab bar structure.
    ///
    /// **Synchronous**: All coordinators and navigation controllers are created immediately,
    /// providing complete UI structure without async/await. Content is populated later via start().
    init() {
        _tabBarController = UITabBarController()
        
        // Get shared dependencies from DI container
        let container = ServiceContainer.shared
        
        // Create child coordinators (root coordinator pattern - each creates own UINavigationController)
        let homeCoordinator = HomeCoordinator(
            repository: container.announcementRepository,
            locationHandler: container.makeLocationPermissionHandler(),
            onShowPetDetails: { [weak self] announcementId in
                self?.showPetDetailsFromHome(announcementId)
            }
        )
        let lostPetCoordinator = AnnouncementListCoordinator()
        let foundPetCoordinator = PlaceholderCoordinator(title: L10n.Tabs.foundPet)
        let contactUsCoordinator = PlaceholderCoordinator(title: L10n.Tabs.contactUs)
        let accountCoordinator = PlaceholderCoordinator(title: L10n.Tabs.account)
        
        // Store strong references to prevent deallocation
        childCoordinators = [
            homeCoordinator,
            lostPetCoordinator,
            foundPetCoordinator,
            contactUsCoordinator,
            accountCoordinator
        ]
        
        // Retrieve navigation controllers from coordinators
        guard let homeNav = homeCoordinator.navigationController,
              let lostPetNav = lostPetCoordinator.navigationController,
              let foundPetNav = foundPetCoordinator.navigationController,
              let contactUsNav = contactUsCoordinator.navigationController,
              let accountNav = accountCoordinator.navigationController else {
            fatalError("Coordinator must have navigationController")
        }
        
        // Configure tab bar items with SF Symbols and localized titles
        configureTabBarItem(
            for: homeNav,
            title: L10n.Tabs.home,
            imageName: "house",
            accessibilityIdentifier: "tabs.home"
        )
        
        configureTabBarItem(
            for: lostPetNav,
            title: L10n.Tabs.lostPet,
            imageName: "magnifyingglass",
            accessibilityIdentifier: "tabs.lostPet"
        )
        
        configureTabBarItem(
            for: foundPetNav,
            title: L10n.Tabs.foundPet,
            imageName: "pawprint",
            accessibilityIdentifier: "tabs.foundPet"
        )
        
        configureTabBarItem(
            for: contactUsNav,
            title: L10n.Tabs.contactUs,
            imageName: "envelope",
            accessibilityIdentifier: "tabs.contactUs"
        )
        
        configureTabBarItem(
            for: accountNav,
            title: L10n.Tabs.account,
            imageName: "person.circle",
            accessibilityIdentifier: "tabs.account"
        )
        
        // Set view controllers on tab bar
        _tabBarController.viewControllers = [
            homeNav,
            lostPetNav,
            foundPetNav,
            contactUsNav,
            accountNav
        ]
        
        // Configure tab bar appearance
        configureTabBarAppearance()
    }
    
    // MARK: - Public Methods
    
    /// Starts all child coordinators to populate tab content.
    ///
    /// **Asynchronous**: Each coordinator's start() method populates its navigation controller
    /// with actual view content. Call this after setting tabBarController as window root.
    ///
    /// - Parameter animated: Whether to animate transitions (typically false for initial load)
    func start(animated: Bool) async {
        for coordinator in childCoordinators {
            await coordinator.start(animated: animated)
        }
    }
    
    // MARK: - Private Methods
    
    /// Configures a tab bar item for a navigation controller.
    /// - Parameters:
    ///   - navigationController: Navigation controller to configure
    ///   - title: Localized title for the tab
    ///   - imageName: SF Symbol name for the tab icon
    ///   - accessibilityIdentifier: Accessibility identifier for testing
    private func configureTabBarItem(
        for navigationController: UINavigationController,
        title: String,
        imageName: String,
        accessibilityIdentifier: String
    ) {
        let tabBarItem = UITabBarItem(
            title: title,
            image: UIImage(systemName: imageName),
            selectedImage: UIImage(systemName: "\(imageName).fill") ?? UIImage(systemName: imageName)
        )
        tabBarItem.accessibilityIdentifier = accessibilityIdentifier
        navigationController.tabBarItem = tabBarItem
    }
    
    // MARK: - Cross-Tab Navigation
    
    /// Handles cross-tab navigation from Home to Pet Details.
    /// Switches to Lost Pets tab and shows pet detail screen.
    ///
    /// **Navigation Flow**:
    /// 1. User taps announcement card on Home tab
    /// 2. HomeCoordinator invokes `onShowPetDetails` closure
    /// 3. This method switches tab and triggers detail screen
    ///
    /// - Parameter announcementId: ID of announcement to show details for
    private func showPetDetailsFromHome(_ announcementId: String) {
        // Find AnnouncementListCoordinator and its index
        guard let (index, lostPetCoordinator) = findAnnouncementListCoordinator() else {
            print("Warning: Could not find AnnouncementListCoordinator for cross-tab navigation")
            return
        }
        
        // Switch to Lost Pets tab
        _tabBarController.selectedIndex = index
        
        // Future: lostPetCoordinator.showPetDetails(for: announcementId)
        // For now, the pet detail screen will be pushed by existing implementation
        print("Cross-tab navigation: Show pet details for \(announcementId)")
        _ = lostPetCoordinator // Silence unused variable warning until showPetDetails is implemented
    }
    
    /// Finds the AnnouncementListCoordinator and its tab index.
    /// - Returns: Tuple of (index, coordinator) or nil if not found
    private func findAnnouncementListCoordinator() -> (Int, AnnouncementListCoordinator)? {
        for (index, coordinator) in childCoordinators.enumerated() {
            if let announcementListCoordinator = coordinator as? AnnouncementListCoordinator {
                return (index, announcementListCoordinator)
            }
        }
        return nil
    }
    
    /// Configures tab bar appearance with design system colors.
    /// Background: #FAFAFA, Inactive: #808080, Active: #FF6B35
    private func configureTabBarAppearance() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(hex: "#FAFAFA")
        
        // Normal (inactive) item appearance
        let normalAttributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor(hex: "#808080")
        ]
        appearance.stackedLayoutAppearance.normal.iconColor = UIColor(hex: "#808080")
        appearance.stackedLayoutAppearance.normal.titleTextAttributes = normalAttributes
        
        // Selected (active) item appearance
        let selectedAttributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor(hex: "#FF6B35")
        ]
        appearance.stackedLayoutAppearance.selected.iconColor = UIColor(hex: "#FF6B35")
        appearance.stackedLayoutAppearance.selected.titleTextAttributes = selectedAttributes
        
        // Apply appearance
        _tabBarController.tabBar.standardAppearance = appearance
        _tabBarController.tabBar.scrollEdgeAppearance = appearance
    }
}
