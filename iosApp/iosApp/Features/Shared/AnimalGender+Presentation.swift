import Foundation

extension AnimalGender {
    /// Human-readable display name for the gender
    var displayName: String {
        switch self {
        case .male: return L10n.AnimalGender.male
        case .female: return L10n.AnimalGender.female
        case .unknown: return L10n.AnimalGender.unknown
        }
    }
}

