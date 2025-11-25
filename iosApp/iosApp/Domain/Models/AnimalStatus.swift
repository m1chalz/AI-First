import Foundation

/// Status of an animal in the system.
enum AnimalStatus: String, Codable {
    case active = "ACTIVE"
    case found = "FOUND"
    case closed = "CLOSED"
}

