import Foundation

/// Animal species types supported by the system.
enum AnimalSpecies {
    case dog
    case cat
    case bird
    case rabbit
    case rodent
    case reptile
    case other
    
    /// Failable initializer from string (case-insensitive)
    init?(fromString string: String) {
        let uppercased = string.uppercased()
        switch uppercased {
        case "DOG":
            self = .dog
        case "CAT":
            self = .cat
        case "BIRD":
            self = .bird
        case "RABBIT":
            self = .rabbit
        case "RODENT":
            self = .rodent
        case "REPTILE":
            self = .reptile
        case "OTHER":
            self = .other
        default:
            return nil
        }
    }
}

