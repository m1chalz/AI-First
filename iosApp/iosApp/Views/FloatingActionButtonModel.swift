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
    
    init(
        title: String,
        style: FloatingActionButton.Style,
        iconSource: FloatingActionButton.IconSource? = nil,
        iconPosition: FloatingActionButton.IconPosition = .left
    ) {
        self.title = title
        self.style = style
        self.iconSource = iconSource
        self.iconPosition = iconPosition
    }
}
