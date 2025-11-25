import Foundation

extension EmptyStateView {
    /**
     * Presentation model for EmptyStateView.
     * Contains display data without behavior.
     */
    struct Model: Equatable {
        let message: String
        
        init(message: String) {
            self.message = message
        }
        
        /// Default empty state message per FR-009
        static let `default` = Model(
            message: L10n.AnimalList.EmptyState.message
        )
    }
}

