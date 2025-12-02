import Foundation

/// DTO for announcement creation request (POST /api/v1/announcements)
/// Maps to camelCase JSON format used by backend API
struct CreateAnnouncementRequestDTO: Codable {
    let species: AnimalSpeciesDTO
    let sex: AnimalGenderDTO
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let email: String
    let phone: String
    let status: AnimalStatusDTO
    let microchipNumber: String?
    let description: String?
    let reward: String?
    
    enum CodingKeys: String, CodingKey {
        case species
        case sex
        case lastSeenDate
        case locationLatitude
        case locationLongitude
        case email
        case phone
        case status
        case microchipNumber
        case description
        case reward
    }
}

