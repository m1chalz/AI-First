import Foundation

/**
 * Presentation model for FloatingActionButton.
 * Contains display data without behavior.
 */
struct FloatingActionButtonModel {
    let title: String
    let style: FloatingActionButton.Style
    let icon: String?
    
    init(title: String, style: FloatingActionButton.Style, icon: String? = nil) {
        self.title = title
        self.style = style
        self.icon = icon
    }
}

