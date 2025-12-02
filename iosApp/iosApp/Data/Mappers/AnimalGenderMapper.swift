import Foundation

/// Extension for converting backend DTO strings to AnimalGender enum
/// Located in Data layer to prevent Domain layer from knowing about Data layer conversion needs
extension AnimalGender {
    /// Failable initializer from backend API string (case-insensitive)
    /// Used for DTO→Domain conversion in repository
    init?(fromDTO string: String) {
        let uppercased = string.uppercased()
        switch uppercased {
        case "MALE":
            self = .male
        case "FEMALE":
            self = .female
        case "UNKNOWN":
            self = .unknown
        default:
            return nil
        }
    }
}

