import Foundation

/// DTO enum for animal status - maps to backend API string values
/// Note: Backend uses "MISSING" while Domain uses "ACTIVE"
enum AnimalStatusDTO: String, Codable {
    case missing = "MISSING"
    case found = "FOUND"
    case closed = "CLOSED"
    
    /// Creates DTO from domain model (Domain → DTO)
    init(domain: AnimalStatus) {
        switch domain {
        case .active: self = .missing
        case .found: self = .found
        case .closed: self = .closed
        }
    }
    
    /// Converts to domain model (DTO → Domain)
    var toDomain: AnimalStatus {
        switch self {
        case .missing: return .active
        case .found: return .found
        case .closed: return .closed
        }
    }
}

