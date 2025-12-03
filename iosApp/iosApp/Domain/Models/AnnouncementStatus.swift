import Foundation

/// Status of an announcement in the system.
enum AnnouncementStatus: String, Codable {
    case active = "ACTIVE"
    case found = "FOUND"
    case closed = "CLOSED"
}

