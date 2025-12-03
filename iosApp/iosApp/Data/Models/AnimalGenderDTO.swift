import Foundation

/// DTO enum for animal gender - maps to backend API string values
enum AnimalGenderDTO: String, Codable {
    case male = "MALE"
    case female = "FEMALE"
    case unknown = "UNKNOWN"
    
    /// Creates DTO from domain model (Domain → DTO)
    init(domain: AnimalGender) {
        switch domain {
        case .male: self = .male
        case .female: self = .female
        case .unknown: self = .unknown
        }
    }
    
    /// Converts to domain model (DTO → Domain)
    var toDomain: AnimalGender {
        switch self {
        case .male: return .male
        case .female: return .female
        case .unknown: return .unknown
        }
    }
}

