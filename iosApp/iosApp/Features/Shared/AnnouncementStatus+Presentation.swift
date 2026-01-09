import Foundation

extension AnnouncementStatus {
    /// Human-readable status label for UI display
    var displayName: String {
        switch self {
        case .active: return L10n.AnnouncementStatus.active
        case .found: return L10n.AnnouncementStatus.found
        case .closed: return L10n.AnnouncementStatus.closed
        }
    }
    
    /// Hex color string for status badge in announcement list (e.g., "#FF0000")
    var badgeColorHex: String {
        switch self {
        case .active: return "#FF0000"  // Red badge - actively missing/searching
        case .found: return "#0074FF"   // Blue badge - animal has been found
        case .closed: return "#93A2B4"  // Gray badge - case closed/resolved
        }
    }
    
    /// Hex color string for annotation callout badge (per FR-009 spec).
    /// Uses different colors than list card badge for visual distinction.
    var annotationBadgeColorHex: String {
        switch self {
        case .active: return "#FF9500"  // Orange badge - MISSING per FR-009
        case .found: return "#155DFC"   // Blue badge - FOUND per FR-009
        case .closed: return "#8E8E93"  // Gray badge - edge case (not in current spec)
        }
    }
}

