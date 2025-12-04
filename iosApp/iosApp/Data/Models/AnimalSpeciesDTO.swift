import Foundation

/// DTO enum for animal species - maps to backend API string values
/// Gracefully handles unknown species from API by defaulting to .other
enum AnimalSpeciesDTO: String, Codable {
    case dog = "DOG"
    case cat = "CAT"
    case bird = "BIRD"
    case rabbit = "RABBIT"
    case other = "OTHER"
    
    /// Custom decoder that defaults to .other for unknown species values
    /// Ensures backward compatibility when API introduces new species
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let rawValue = try container.decode(String.self)
        self = AnimalSpeciesDTO(rawValue: rawValue) ?? .other
    }
    
    /// Creates DTO from domain model (Domain → DTO)
    init(domain: AnimalSpecies) {
        switch domain {
        case .dog: self = .dog
        case .cat: self = .cat
        case .bird: self = .bird
        case .rabbit: self = .rabbit
        case .other: self = .other
        }
    }
    
    /// Converts to domain model (DTO → Domain)
    var toDomain: AnimalSpecies {
        switch self {
        case .dog: return .dog
        case .cat: return .cat
        case .bird: return .bird
        case .rabbit: return .rabbit
        case .other: return .other
        }
    }
}

