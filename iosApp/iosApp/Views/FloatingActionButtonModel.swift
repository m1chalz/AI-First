import Foundation

/**
 * Presentation model for FloatingActionButton.
 * Contains display data without behavior.
 */
struct FloatingActionButtonModel {
    let title: String
    let style: FloatingActionButton.Style
    let iconSource: FloatingActionButton.IconSource?
    let iconPosition: FloatingActionButton.IconPosition
    /// When true, button expands to fill available horizontal space.
    let expandsHorizontally: Bool
    
    init(
        title: String,
        style: FloatingActionButton.Style,
        iconSource: FloatingActionButton.IconSource? = nil,
        iconPosition: FloatingActionButton.IconPosition = .left,
        expandsHorizontally: Bool = false
    ) {
        self.title = title
        self.style = style
        self.iconSource = iconSource
        self.iconPosition = iconPosition
        self.expandsHorizontally = expandsHorizontally
    }
}
