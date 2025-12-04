import Foundation

extension ErrorView {
    /// Model configuration for reusable ErrorView component
    struct Model: Equatable {
        /// Title text (e.g., "Failed to load")
        let title: String
        
        /// Detailed error message
        let message: String
        
        /// Text for the retry button
        let retryButtonTitle: String
        
        /// Action to perform when retry button is tapped (optional)
        let onRetry: (() -> Void)?
        
        /// Base accessibility identifier
        let accessibilityIdentifier: String
        
        /// Retry button accessibility identifier
        let retryButtonAccessibilityIdentifier: String
        
        /// Creates a new ErrorView model
        /// - Parameters:
        ///   - title: Error title
        ///   - message: Detailed error message
        ///   - retryButtonTitle: Button text (default: L10n.Common.retry)
        ///   - onRetry: Retry callback (default: nil)
        ///   - accessibilityIdentifier: Base ID for UI tests (default: "errorView")
        ///   - retryButtonAccessibilityIdentifier: Custom retry button ID (default: "{accessibilityIdentifier}.retryButton")
        init(
            title: String,
            message: String,
            retryButtonTitle: String = L10n.Common.retry,
            onRetry: (() -> Void)? = nil,
            accessibilityIdentifier: String = "errorView",
            retryButtonAccessibilityIdentifier: String? = nil
        ) {
            self.title = title
            self.message = message
            self.retryButtonTitle = retryButtonTitle
            self.onRetry = onRetry
            self.accessibilityIdentifier = accessibilityIdentifier
            self.retryButtonAccessibilityIdentifier = retryButtonAccessibilityIdentifier ?? "\(accessibilityIdentifier).retryButton"
        }
        
        static func == (lhs: Model, rhs: Model) -> Bool {
            lhs.title == rhs.title &&
            lhs.message == rhs.message &&
            lhs.retryButtonTitle == rhs.retryButtonTitle &&
            lhs.accessibilityIdentifier == rhs.accessibilityIdentifier &&
            lhs.retryButtonAccessibilityIdentifier == rhs.retryButtonAccessibilityIdentifier
        }
    }
}

