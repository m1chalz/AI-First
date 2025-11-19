import UIKit
import SwiftUI

/// Scene lifecycle manager.
/// Configures window, navigation controller, and root coordinator.
class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    private var appCoordinator: AppCoordinator?
    
    /// Called when connecting a new scene session.
    /// Use this method to configure the window and initialize coordinators.
    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        // Configure navigation bar appearance
        configureNavigationBarAppearance()
        
        // Create window
        let window = UIWindow(windowScene: windowScene)
        self.window = window
        
        // Create navigation controller
        let navigationController = UINavigationController()
        navigationController.navigationBar.isHidden = true // Hide nav bar (views manage their own)
        
        // Set window root and make visible
        window.rootViewController = navigationController
        window.makeKeyAndVisible()
        
        // Initialize and start app coordinator
        // Sets AnimalListScreen as primary entry point per FR-010
        let coordinator = AppCoordinator(navigationController: navigationController)
        self.appCoordinator = coordinator
        
        // Start coordinator asynchronously without animation (initial load)
        Task {
            await coordinator.start(animated: false)
        }
    }
    
    // MARK: - Navigation Bar Configuration
    
    /// Configures transparent green semi-transparent navigation bar for all appearances.
    private func configureNavigationBarAppearance() {
        let appearance = UINavigationBarAppearance()
        appearance.configureWithTransparentBackground()
        appearance.backgroundColor = UIColor.green.withAlphaComponent(0.5)
        
        // Apply to all appearance states
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
    
    /// Called as the scene is being released by the system.
    /// Use this method to release any resources associated with this scene.
    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // Release any resources associated with this scene.
    }
    
    /// Called when the scene has moved from an inactive state to an active state.
    /// Use this method to restart any tasks that were paused (or not yet started).
    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state.
    }
    
    /// Called when the scene will move from an active state to an inactive state.
    /// This may occur due to temporary interruptions (ex. an incoming phone call).
    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
    }
    
    /// Called as the scene transitions from the background to the foreground.
    /// Use this method to undo the changes made on entering the background.
    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called as the scene transitions from the background to the foreground.
    }
    
    /// Called as the scene transitions from the foreground to the background.
    /// Use this method to save data, release shared resources, and store enough scene-specific state.
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
    }
}

