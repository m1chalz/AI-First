import Foundation

extension AnimalSpecies {
    /// Human-readable display name for the species
    var displayName: String {
        switch self {
        case .dog: return "Dog"
        case .cat: return "Cat"
        case .bird: return "Bird"
        case .rabbit: return "Rabbit"
        case .other: return "Other"
        }
    }
}

