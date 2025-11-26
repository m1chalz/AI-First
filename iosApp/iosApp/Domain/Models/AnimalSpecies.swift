import Foundation

/// Animal species types supported by the system.
enum AnimalSpecies: Codable {
    case dog
    case cat
    case bird
    case rabbit
    case other
    
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
        case .other:
            stringValue = "OTHER"
        }
        
        try container.encode(stringValue)
    }
}

