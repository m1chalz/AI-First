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
        
        /// Initializer that converts AnnouncementStatus to display text and color hex
        init(imageUrl: String?, status: AnnouncementStatus, rewardText: String?) {
            self.imageUrl = imageUrl
            self.statusDisplayText = Self.statusDisplayText(from: status)
            self.statusBadgeColorHex = Self.statusBadgeColorHex(from: status)
            self.rewardText = rewardText
        }
        
        /// Converts AnnouncementStatus to display text
        private static func statusDisplayText(from status: AnnouncementStatus) -> String {
            switch status {
            case .active:
                return L10n.AnnouncementStatus.active
            case .found:
                return L10n.AnnouncementStatus.found
            case .closed:
                return L10n.AnnouncementStatus.closed
            }
        }
        
        /// Converts AnnouncementStatus to badge color hex
        private static func statusBadgeColorHex(from status: AnnouncementStatus) -> String {
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

