import Foundation

/// Represents an animal announcement in the PetSpot system.
/// Created from AnnouncementDTO received from the backend API.
/// Contains all information needed for list display and detail views.
///
/// - Note: Translated from Kotlin Multiplatform shared module to Swift.
struct Announcement {
    /// Unique identifier (UUID or database ID)
    let id: String
    
    /// Name of the animal (e.g., "Buddy", "Mittens")
    let name: String?
    
    /// URL or placeholder identifier for animal photo
    let photoUrl: String
    
    /// Geographic coordinate (latitude and longitude)
    let coordinate: Coordinate
    
    /// Animal species (Dog, Cat, Bird, etc.)
    let species: AnimalSpecies
    
    /// Specific breed name (e.g., "Maine Coon", "German Shepherd")
    let breed: String?
    
    /// Biological sex (Male, Female, Unknown)
    let gender: AnimalGender
    
    /// Current status (Active, Found, Closed)
    let status: AnnouncementStatus
    
    /// Date when animal was last seen (for Active status) or found (for Found status)
    /// Format: DD/MM/YYYY
    let lastSeenDate: String
    
    /// Detailed text description (optional)
    let description: String?
    
    /// Contact email of the person who reported/owns the animal (optional)
    let email: String?
    
    /// Contact phone number of the person who reported/owns the animal (optional)
    let phone: String?
}

