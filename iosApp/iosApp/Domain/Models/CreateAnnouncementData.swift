import Foundation

/// Pure domain model for announcement creation data
/// Used by Service to pass business data to Repository
struct CreateAnnouncementData {
    let species: AnimalSpecies
    let breed: String?
    let sex: AnimalGender
    let age: Int?
    let lastSeenDate: Date
    let location: (latitude: Double, longitude: Double)
    let contact: (email: String, phone: String)
    let microchipNumber: String?
    let petName: String?
    let description: String?
    let reward: String?
    /// Status to be sent to backend: .active for Missing flow (→ "MISSING"), .found for Found flow (→ "FOUND")
    let status: AnnouncementStatus
}

