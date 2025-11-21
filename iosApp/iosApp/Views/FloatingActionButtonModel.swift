import Foundation

/**
 * Presentation model for FloatingActionButton.
 * Contains display data without behavior.
 */
struct FloatingActionButtonModel {
    let title: String
    let style: FloatingActionButton.Style
    
    init(title: String, style: FloatingActionButton.Style) {
        self.title = title
        self.style = style
    }
}

