import Foundation

/// Extension for converting backend DTO strings to AnimalSpecies enum
/// Located in Data layer to prevent Domain layer from knowing about Data layer conversion needs
extension AnimalSpecies {
    /// Failable initializer from backend API string (case-insensitive)
    /// Used for DTO→Domain conversion in repository
    init?(fromDTO string: String) {
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

