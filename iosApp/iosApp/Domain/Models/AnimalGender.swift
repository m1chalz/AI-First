import Foundation

/// Animal gender/sex.
/// Displayed as icon on web version (per Figma spec).
enum AnimalGender {
    case male
    case female
    case unknown
    
    /// Human-readable display name for the gender
    var displayName: String {
        switch self {
        case .male: return "Male"
        case .female: return "Female"
        case .unknown: return "Unknown"
        }
    }
}

