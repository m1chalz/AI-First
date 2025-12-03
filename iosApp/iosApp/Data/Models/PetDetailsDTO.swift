import Foundation

/// DTO for pet details from details endpoint
/// Maps to GET /api/v1/announcements/:id response
struct PetDetailsDTO: Codable {
    let id: String
    let petName: String?
    let species: AnimalSpeciesDTO
    let status: AnnouncementStatusDTO
    let photoUrl: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let breed: String?
    let sex: AnimalGenderDTO?
    let age: Int?
    let microchipNumber: String?
    let email: String?
    let phone: String
    let reward: String?
    let description: String
    let createdAt: String
    let updatedAt: String
}

