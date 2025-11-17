import UIKit

/// Application-level lifecycle manager.
/// Handles app launch and global setup.
@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    /// Called when the application has finished launching.
    /// Use this method to initialize global services (analytics, crash reporting, etc.)
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Initialize Koin dependency injection at app startup
        KoinInitializer().initialize()
        
        // Future: Initialize global services (analytics, crash reporting, etc.)
        return true
    }
    
    // MARK: UISceneSession Lifecycle
    
    /// Called when a new scene session is being created.
    /// Use this method to select a configuration to create the new scene with.
    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(
            name: "Default Configuration",
            sessionRole: connectingSceneSession.role
        )
    }
    
    /// Called when the user discards a scene session.
    /// Use this method to release any resources that were specific to the discarded scenes.
    func application(
        _ application: UIApplication,
        didDiscardSceneSessions sceneSessions: Set<UISceneSession>
    ) {
        // Called when the user discards a scene session.
        // Release any resources that were specific to the discarded scenes.
    }
}

