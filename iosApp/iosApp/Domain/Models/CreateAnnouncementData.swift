import Foundation

/// Pure domain model for announcement creation data
/// Used by Service to pass business data to Repository
struct CreateAnnouncementData {
    let species: AnimalSpecies
    let sex: AnimalGender
    let lastSeenDate: Date
    let location: (latitude: Double, longitude: Double)
    let contact: (email: String, phone: String)
    let microchipNumber: String?
    let petName: String?
    let description: String?
    let reward: String?
}

