import Foundation

/// Response wrapper for announcements list endpoint
/// Maps to GET /api/v1/announcements response structure
struct AnnouncementsListResponse: Codable {
    let data: [AnnouncementDTO]
}

