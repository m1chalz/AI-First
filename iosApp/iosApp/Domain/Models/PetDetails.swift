import Foundation

/// Represents comprehensive pet information displayed on the details screen.
struct PetDetails: Identifiable, Codable {
    // MARK: - Required Fields
    
    /// Unique identifier for the pet
    let id: String
    
    /// Name of the pet (from backend, not displayed in current UI design)
    let petName: String
    
    /// URL string for the pet's photo (nullable, show fallback if nil)
    let photoUrl: String?
    
    /// Status of the pet report (ACTIVE, FOUND, or CLOSED from API)
    /// Note: ViewModel maps ACTIVE → "MISSING" for display
    let status: AnimalStatus
    
    /// Date when the pet was last seen (YYYY-MM-DD format from API)
    let lastSeenDate: String
    
    /// Species of the pet (e.g., "DOG", "CAT")
    let species: String
    
    /// Sex of the pet (MALE, FEMALE, or UNKNOWN)
    let gender: AnimalGender
    
    /// Additional description text (required in backend, multi-line)
    let description: String
    
    /// Owner's phone number (required in backend)
    let phone: String
    
    // MARK: - Optional Fields
    
    /// Owner's email address (optional)
    let email: String?
    
    /// Breed of the pet (optional)
    let breed: String?
    
    /// Latitude coordinate where pet was last seen
    let latitude: Double
    
    /// Longitude coordinate where pet was last seen
    let longitude: Double
    
    /// Search radius around location in kilometers (optional, number from backend)
    /// Note: ViewModel formats to "±X km" for display
    let locationRadius: Int?
    
    /// Microchip number (optional, will be mocked until backend adds this field)
    let microchipNumber: String?
    
    /// Approximate age of the pet (optional, will be mocked until backend adds this field)
    let approximateAge: String?
    
    /// Reward amount text (optional, will be mocked until backend adds this field)
    let reward: String?
    
    // MARK: - Metadata (not displayed in UI)
    
    /// Timestamp when announcement was created
    let createdAt: String
    
    /// Timestamp when announcement was last updated
    let updatedAt: String
}

