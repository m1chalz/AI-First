import Foundation

/// DTO for announcement creation response (POST /api/v1/announcements returns HTTP 201)
/// Maps from camelCase JSON format used by backend API
struct AnnouncementResponseDTO: Codable {
    let id: String
    let managementPassword: String
    let species: AnimalSpeciesDTO
    let sex: AnimalGenderDTO
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let email: String
    let phone: String
    let status: AnnouncementStatusDTO
    let microchipNumber: String?
    let description: String?
    let reward: String?
    let photoUrl: String?
    
    enum CodingKeys: String, CodingKey {
        case id
        case managementPassword
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
        case photoUrl
    }
}

