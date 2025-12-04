import Foundation

/// DTO for announcement creation request (POST /api/v1/announcements)
/// Maps to camelCase JSON format used by backend API
struct CreateAnnouncementRequestDTO: Codable {
    let species: AnimalSpeciesDTO
    let breed: String?
    let sex: AnimalGenderDTO
    let age: Int?
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let email: String
    let phone: String
    let status: AnnouncementStatusDTO
    let microchipNumber: String?
    let petName: String?
    let description: String?
    let reward: String?
    
    enum CodingKeys: String, CodingKey {
        case species
        case breed
        case sex
        case age
        case lastSeenDate
        case locationLatitude
        case locationLongitude
        case email
        case phone
        case status
        case microchipNumber
        case petName
        case description
        case reward
    }
}

