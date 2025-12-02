import Foundation

/// DTO for single announcement from list endpoint
/// Maps to individual announcement object in backend response
struct AnnouncementDTO: Codable {
    let id: String
    let petName: String
    let species: AnimalSpeciesDTO
    let status: String
    let photoUrl: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let breed: String?
    let sex: AnimalGenderDTO?
    let age: Int?
    let description: String
    let phone: String?
    let email: String?
}

