import Foundation

/// DTO for announcement creation response (POST /api/v1/announcements returns HTTP 201)
/// Maps from snake_case JSON format used by backend API
struct AnnouncementResponseDTO: Codable {
    let id: String
    let managementPassword: String
    let species: String
    let sex: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let email: String
    let phone: String
    let status: String
    let microchipNumber: String?
    let description: String?
    let reward: String?
    let photoUrl: String?
    
    enum CodingKeys: String, CodingKey {
        case id
        case managementPassword = "management_password"
        case species
        case sex
        case lastSeenDate = "last_seen_date"
        case locationLatitude = "location_latitude"
        case locationLongitude = "location_longitude"
        case email
        case phone
        case status
        case microchipNumber = "microchip_number"
        case description
        case reward
        case photoUrl = "photo_url"
    }
}

