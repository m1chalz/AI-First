import Foundation
import SwiftUI

// MARK: - Model

extension MapSectionHeaderView {
    /// Presentation model for MapSectionHeaderView.
    /// Configures title and legend items displayed above the map preview.
    ///
    /// **Title behavior**:
    /// - `title: String` - Shows title above legend (landing page)
    /// - `title: nil` - Shows legend only (fullscreen map - title in nav bar)
    struct Model {
        // MARK: - Display Properties
        
        /// Section title text (nil = legend only, no title)
        let title: String?
        
        /// Legend items to display (color dot + label)
        let legendItems: [LegendItem]
        
        // MARK: - Accessibility Identifiers
        
        /// Accessibility ID for title (nil when title is nil)
        let titleAccessibilityId: String?
        let legendAccessibilityIdPrefix: String
        
        // MARK: - Initialization
        
        init(
            title: String?,
            legendItems: [LegendItem],
            titleAccessibilityId: String?,
            legendAccessibilityIdPrefix: String
        ) {
            self.title = title
            self.legendItems = legendItems
            self.titleAccessibilityId = titleAccessibilityId
            self.legendAccessibilityIdPrefix = legendAccessibilityIdPrefix
        }
    }
    
    /// Single legend item with colored dot and label
    struct LegendItem: Identifiable {
        let id: String
        let colorHex: String
        let label: String
        
        var color: Color {
            Color(hex: colorHex)
        }
    }
}

