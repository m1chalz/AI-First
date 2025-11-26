import Foundation

extension LoadingView {
    /// Model configuration for reusable LoadingView component
    struct Model: Equatable {
        /// Optional message text to display below the spinner
        let message: String?
        
        /// Accessibility identifier for UI testing
        let accessibilityIdentifier: String
        
        /// Creates a new LoadingView model
        /// - Parameters:
        ///   - message: Optional text to display (default: nil)
        ///   - accessibilityIdentifier: ID for UI tests (default: "loadingView")
        init(message: String? = nil, accessibilityIdentifier: String = "loadingView") {
            self.message = message
            self.accessibilityIdentifier = accessibilityIdentifier
        }
    }
}

