import Foundation

// MARK: - Model

extension ListHeaderRowView {
    /// Presentation model for ListHeaderRowView.
    /// Generic structure for any list header with title and action.
    struct Model {
        // MARK: - Display Properties
        
        /// Section title text
        let title: String
        
        /// Action button label
        let actionTitle: String
        
        /// Called when action button is tapped
        let onActionTap: () -> Void
        
        // MARK: - Accessibility Identifiers
        
        let titleAccessibilityId: String
        let actionAccessibilityId: String
        
        // MARK: - Initialization
        
        init(
            title: String,
            actionTitle: String,
            onActionTap: @escaping () -> Void = {},
            titleAccessibilityId: String,
            actionAccessibilityId: String
        ) {
            self.title = title
            self.actionTitle = actionTitle
            self.onActionTap = onActionTap
            self.titleAccessibilityId = titleAccessibilityId
            self.actionAccessibilityId = actionAccessibilityId
        }
    }
}

