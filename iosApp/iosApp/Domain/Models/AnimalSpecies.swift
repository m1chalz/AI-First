import Foundation

/// Animal species types supported by the system.
/// Determines icon/image display and filtering options.
enum AnimalSpecies {
    case dog
    case cat
    case bird
    case rabbit
    case other
    
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

