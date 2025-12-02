import Foundation

/// DTO enum for animal species - maps to backend API string values
enum AnimalSpeciesDTO: String, Codable {
    case dog = "DOG"
    case cat = "CAT"
    case bird = "BIRD"
    case rabbit = "RABBIT"
    case rodent = "RODENT"
    case reptile = "REPTILE"
    case other = "OTHER"
    
    /// Creates DTO from domain model (Domain → DTO)
    init(domain: AnimalSpecies) {
        switch domain {
        case .dog: self = .dog
        case .cat: self = .cat
        case .bird: self = .bird
        case .rabbit: self = .rabbit
        case .rodent: self = .rodent
        case .reptile: self = .reptile
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
        case .rodent: return .rodent
        case .reptile: return .reptile
        case .other: return .other
        }
    }
}

