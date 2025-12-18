import Foundation

// MARK: - Model

extension HeroPanelView {
    /// Presentation model for HeroPanelView.
    /// Generic structure for any screen requiring a prominent call-to-action section.
    struct Model {
        // MARK: - Display Properties
        
        /// Main title text displayed above buttons
        let title: String
        
        /// Left button model (first action)
        let leftButton: FloatingActionButtonModel
        
        /// Right button model (second action)
        let rightButton: FloatingActionButtonModel
        
        /// Left button action closure
        let onLeftButtonTap: () -> Void
        
        /// Right button action closure
        let onRightButtonTap: () -> Void
        
        // MARK: - Accessibility Identifiers
        
        let titleAccessibilityId: String
        let leftButtonAccessibilityId: String
        let rightButtonAccessibilityId: String
        
        // MARK: - Initialization
        
        init(
            title: String,
            leftButton: FloatingActionButtonModel,
            rightButton: FloatingActionButtonModel,
            onLeftButtonTap: @escaping () -> Void = {},
            onRightButtonTap: @escaping () -> Void = {},
            titleAccessibilityId: String,
            leftButtonAccessibilityId: String,
            rightButtonAccessibilityId: String
        ) {
            self.title = title
            self.leftButton = leftButton
            self.rightButton = rightButton
            self.onLeftButtonTap = onLeftButtonTap
            self.onRightButtonTap = onRightButtonTap
            self.titleAccessibilityId = titleAccessibilityId
            self.leftButtonAccessibilityId = leftButtonAccessibilityId
            self.rightButtonAccessibilityId = rightButtonAccessibilityId
        }
    }
}

