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
            locationHandler: LocationPermissionHandler(locationService: container.locationService)
        )
        
        // Set unified tab navigation closure (handles both hero buttons and card taps)
        homeCoordinator.onSwitchToLostPetTab = { [weak self] announcementId in
            self?.switchToLostPetTab(withAnnouncementId: announcementId)
        }
        homeCoordinator.onSwitchToFoundPetTab = { [weak self] in
            self?.switchToFoundPetTab()
        }
        
        let lostPetCoordinator = AnnouncementListCoordinator(
            repository: container.announcementRepository,
            locationService: container.locationService,
            photoAttachmentCache: container.photoAttachmentCache,
            announcementSubmissionService: container.announcementSubmissionService
        )
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
        
        // Configure tab bar items with custom icons and localized titles
        configureTabBarItem(
            for: homeNav,
            title: L10n.Tabs.home,
            assetName: "home",
            accessibilityIdentifier: "tabs.home"
        )
        
        configureTabBarItem(
            for: lostPetNav,
            title: L10n.Tabs.lostPet,
            assetName: "lostPet",
            accessibilityIdentifier: "tabs.lostPet"
        )
        
        configureTabBarItem(
            for: foundPetNav,
            title: L10n.Tabs.foundPet,
            assetName: "foundPet",
            accessibilityIdentifier: "tabs.foundPet"
        )
        
        configureTabBarItem(
            for: contactUsNav,
            title: L10n.Tabs.contactUs,
            assetName: "contactUs",
            accessibilityIdentifier: "tabs.contactUs"
        )
        
        configureTabBarItem(
            for: accountNav,
            title: L10n.Tabs.account,
            assetName: "account",
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
    ///   - assetName: Custom asset name for the tab icon (from Assets.xcassets/TabBar/)
    ///   - accessibilityIdentifier: Accessibility identifier for testing
    private func configureTabBarItem(
        for navigationController: UINavigationController,
        title: String,
        assetName: String,
        accessibilityIdentifier: String
    ) {
        // Use template image - tint colors applied via UITabBarAppearance
        let tabBarItem = UITabBarItem(
            title: title,
            image: UIImage(named: assetName),
            selectedImage: nil
        )
        tabBarItem.accessibilityIdentifier = accessibilityIdentifier
        navigationController.tabBarItem = tabBarItem
    }
    
    // MARK: - Cross-Tab Navigation
    
    /// Switches to Lost Pet tab, optionally showing pet details.
    ///
    /// **Use Cases**:
    /// - Hero "Lost Pet" button: `switchToLostPetTab(withAnnouncementId: nil)` - only switch tab
    /// - "View All" button: `switchToLostPetTab(withAnnouncementId: nil)` - only switch tab
    /// - Announcement card tap: `switchToLostPetTab(withAnnouncementId: id)` - switch tab + show details
    ///
    /// **Back Navigation Behavior**:
    /// After viewing details, user remains on Lost Pets tab.
    /// Tapping Home tab returns to landing page.
    ///
    /// - Parameter announcementId: Optional ID of announcement to show details for.
    ///   If nil, only switches tab. If provided, also shows pet details.
    private func switchToLostPetTab(withAnnouncementId announcementId: String? = nil) {
        // Find AnnouncementListCoordinator and its index
        guard let (index, lostPetCoordinator) = findAnnouncementListCoordinator() else {
            print("Warning: Could not find AnnouncementListCoordinator for cross-tab navigation")
            return
        }
        
        // Switch to Lost Pets tab
        _tabBarController.selectedIndex = index
        
        // Only show details if announcementId provided
        if let announcementId = announcementId {
            lostPetCoordinator.showPetDetails(for: announcementId)
        }
    }
    
    /// Switches to Found Pet tab.
    ///
    /// **Use Case**: Hero "Found Pet" button tap
    private func switchToFoundPetTab() {
        // Found Pet tab is at index 2 (Home=0, Lost Pet=1, Found Pet=2)
        guard let foundPetIndex = findFoundPetTabIndex() else {
            print("Warning: Could not find Found Pet tab index")
            return
        }
        
        _tabBarController.selectedIndex = foundPetIndex
    }
    
    /// Finds the Found Pet tab index.
    /// - Returns: Tab index for Found Pet tab or nil if not found
    private func findFoundPetTabIndex() -> Int? {
        for (index, coordinator) in childCoordinators.enumerated() {
            if coordinator is PlaceholderCoordinator,
               let navController = coordinator.navigationController,
               navController.tabBarItem.accessibilityIdentifier == "tabs.foundPet" {
                return index
            }
        }
        return nil
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
    /// Background: #FFFFFF, Inactive: #6a7282, Active: #155dfc
    private func configureTabBarAppearance() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        
        // Background color: white (#FFFFFF)
        appearance.backgroundColor = UIColor(hex: "#FFFFFF")
        
        // Top border: thin black line
        appearance.shadowColor = UIColor.black
        appearance.shadowImage = UIImage()
        
        // Normal (inactive) item appearance
        let normalAttributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor(hex: "#6a7282"),
            .font: UIFont.systemFont(ofSize: 12)
        ]
        appearance.stackedLayoutAppearance.normal.iconColor = UIColor(hex: "#6a7282")
        appearance.stackedLayoutAppearance.normal.titleTextAttributes = normalAttributes
        
        // Selected (active) item appearance
        let selectedAttributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor(hex: "#155dfc"),
            .font: UIFont.systemFont(ofSize: 12)
        ]
        appearance.stackedLayoutAppearance.selected.iconColor = UIColor(hex: "#155dfc")
        appearance.stackedLayoutAppearance.selected.titleTextAttributes = selectedAttributes
        
        // Apply appearance to both standard and scrollEdge
        _tabBarController.tabBar.standardAppearance = appearance
        _tabBarController.tabBar.scrollEdgeAppearance = appearance
    }
}
