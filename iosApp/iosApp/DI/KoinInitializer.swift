import Foundation
import Shared

/// Initializes Koin dependency injection for iOS platform.
///
/// This class provides a Swift-friendly wrapper around the Kotlin/Native Koin initialization.
/// Call `initialize()` once at application startup, typically in the @main app struct's init().
///
/// Example usage:
/// ```swift
/// @main
/// struct PetSpotApp: App {
///     init() {
///         KoinInitializer().initialize()
///     }
///
///     var body: some Scene {
///         WindowGroup {
///             ContentView()
///         }
///     }
/// }
/// ```
class KoinInitializer {
    
    /// Initializes the Koin dependency injection container.
    ///
    /// This method calls the Kotlin/Native `initKoin()` function from the shared module,
    /// which registers all domain dependencies and makes them available for injection.
    ///
    /// - Note: This should only be called once during app initialization.
    /// - Warning: Calling this multiple times may result in undefined behavior.
    func initialize() {
        // Call Kotlin/Native Koin initialization from shared module
        KoinIosKt.initKoin()
    }
}

