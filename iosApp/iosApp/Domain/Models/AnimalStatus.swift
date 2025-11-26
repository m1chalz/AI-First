import Foundation

/// Status of an animal in the system.
/// Determines badge color and text displayed in list.
enum AnimalStatus {
    case active
    case found
    case closed
    
    /// Human-readable status label
    var displayName: String {
        switch self {
        case .active: return "Active"
        case .found: return "Found"
        case .closed: return "Closed"
        }
    }
    
    /// Hex color for status badge
    var badgeColor: String {
        switch self {
        case .active: return "#FF0000"  // Red badge - actively missing/searching
        case .found: return "#0074FF"   // Blue badge - animal has been found
        case .closed: return "#93A2B4"  // Gray badge - case closed/resolved
        }
    }
}

