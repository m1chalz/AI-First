import Foundation

extension PetPhotoWithBadgesView {
    /// Model for configuring the pet photo with overlaid badges
    struct Model: Equatable {
        /// URL string for the pet photo (nullable)
        let imageUrl: String?
        
        /// Status display text for the badge ("MISSING", "FOUND", or "CLOSED")
        let statusDisplayText: String
        
        /// Hex color string for status badge (e.g., "#FF0000")
        let statusBadgeColorHex: String
        
        /// Optional reward text (nil if no reward)
        let rewardText: String?
        
        /// Initializer that converts AnimalStatus to display text and color hex
        init(imageUrl: String?, status: AnimalStatus, rewardText: String?) {
            self.imageUrl = imageUrl
            self.statusDisplayText = Self.statusDisplayText(from: status)
            self.statusBadgeColorHex = Self.statusBadgeColorHex(from: status)
            self.rewardText = rewardText
        }
        
        /// Converts AnimalStatus to display text (maps ACTIVE → "MISSING")
        private static func statusDisplayText(from status: AnimalStatus) -> String {
            switch status {
            case .active:
                return "MISSING"
            case .found:
                return "FOUND"
            case .closed:
                return "CLOSED"
            }
        }
        
        /// Converts AnimalStatus to badge color hex
        private static func statusBadgeColorHex(from status: AnimalStatus) -> String {
            switch status {
            case .active:
                return "#FF0000"  // Red badge - actively missing/searching
            case .found:
                return "#00FF00"   // Green badge - animal has been found
            case .closed:
                return "#808080"   // Gray badge - case closed/resolved
            }
        }
    }
}

