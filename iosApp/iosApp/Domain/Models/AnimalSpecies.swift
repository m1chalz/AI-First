import Foundation

/// Animal species types supported by the system.
enum AnimalSpecies: Codable {
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
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let stringValue = try container.decode(String.self).uppercased()
        
        switch stringValue {
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
            self = .other
        }
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        let stringValue: String
        
        switch self {
        case .dog:
            stringValue = "DOG"
        case .cat:
            stringValue = "CAT"
        case .bird:
            stringValue = "BIRD"
        case .rabbit:
            stringValue = "RABBIT"
        case .rodent:
            stringValue = "RODENT"
        case .reptile:
            stringValue = "REPTILE"
        case .other:
            stringValue = "OTHER"
        }
        
        try container.encode(stringValue)
    }
}

