import Foundation

/// Animal gender/sex.
enum AnimalGender {
    case male
    case female
    case unknown
    
    /// Failable initializer from string (case-insensitive)
    init?(fromString string: String) {
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

