import Foundation

/**
 * Presentation model for EmptyStateView.
 * Contains display data without behavior.
 */
struct EmptyStateModel {
    let message: String
    
    init(message: String) {
        self.message = message
    }
    
    /// Default empty state message per FR-009
    static let `default` = EmptyStateModel(
        message: L10n.AnimalList.EmptyState.message
    )
}

