import Foundation

/// ViewModel for placeholder "Coming soon" screens.
/// Used for tabs that are not yet implemented (Home, Found Pet, Contact Us, Account).
///
/// The title is provided at initialization to customize the screen header,
/// while the message is always the standard "Coming soon" localized string.
@MainActor
final class PlaceholderViewModel: ObservableObject {
    
    // MARK: - Published Properties
    
    /// Title displayed at the top of the placeholder screen.
    /// Typically matches the tab name (e.g., "Home", "Found Pet").
    @Published private(set) var title: String
    
    // MARK: - Computed Properties
    
    /// Localized message explaining the feature is under development.
    var message: String {
        L10n.Placeholder.message
    }
    
    // MARK: - Initialization
    
    /// Creates a PlaceholderViewModel with the specified title.
    /// - Parameter title: Title to display on the placeholder screen
    init(title: String) {
        self.title = title
    }
}

