import Foundation

/// Animal gender/sex.
enum AnimalGender: Codable {
    case male
    case female
    case unknown
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let stringValue = try container.decode(String.self).uppercased()
        
        switch stringValue {
        case "MALE":
            self = .male
        case "FEMALE":
            self = .female
        case "UNKNOWN":
            self = .unknown
        default:
            self = .unknown
        }
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        let stringValue: String
        
        switch self {
        case .male:
            stringValue = "MALE"
        case .female:
            stringValue = "FEMALE"
        case .unknown:
            stringValue = "UNKNOWN"
        }
        
        try container.encode(stringValue)
    }
}

