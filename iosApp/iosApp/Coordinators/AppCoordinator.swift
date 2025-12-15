import UIKit

/**
 * Root application coordinator managing tab-based navigation.
 *
 * **Does NOT conform to CoordinatorInterface** - AppCoordinator manages TabCoordinator,
 * not UINavigationController. The app's navigation structure is:
 * AppCoordinator → TabCoordinator → UITabBarController → Child Coordinators
 *
 * **Synchronous initialization**: TabCoordinator and all child coordinators are created
 * in init(), providing complete UI structure immediately. The start() method
 * asynchronously populates content in each tab.
 *
 * Use tabBarController property to set as window's rootViewController.
 */
@MainActor
final class AppCoordinator {
    
    // MARK: - Public Properties
    
    /// The tab bar controller to use as window's rootViewController.
    /// Exposes TabCoordinator's tab bar controller for SceneDelegate.
    var tabBarController: UITabBarController {
        tabCoordinator.tabBarController
    }
    
    // MARK: - Private Properties
    
    /// TabCoordinator managing the main tab bar and its child coordinators.
    private let tabCoordinator: TabCoordinator
    
    // MARK: - Initialization
    
    /// Creates AppCoordinator with fully configured tab bar structure.
    ///
    /// **Synchronous**: TabCoordinator creates all child coordinators and their
    /// navigation controllers immediately, providing complete UI structure
    /// without async/await.
    init() {
        tabCoordinator = TabCoordinator()
    }
    
    // MARK: - Public Methods
    
    /// Starts all child coordinators to populate tab content.
    ///
    /// **Asynchronous**: Each tab's coordinator populates its navigation controller
    /// with actual view content. Call this after setting tabBarController as window root.
    ///
    /// - Parameter animated: Whether to animate transitions (typically false for initial load)
    func start(animated: Bool = false) async {
        await tabCoordinator.start(animated: animated)
    }
}
