import Foundation

/// Pure domain model representing successful announcement creation
/// No Codable, no snake_case - business data only
struct AnnouncementResult {
    let id: String
    let managementPassword: String
}

