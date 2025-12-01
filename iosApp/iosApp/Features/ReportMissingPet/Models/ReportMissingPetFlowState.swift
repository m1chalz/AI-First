import Foundation

/// Shared state for Missing Pet Report flow.
/// Owned by ReportMissingPetCoordinator and injected into all ViewModels.
/// Persists data during forward/backward navigation within active session.
@MainActor
final class ReportMissingPetFlowState: ObservableObject {
    private let photoAttachmentCache: PhotoAttachmentCacheProtocol
    
    // MARK: - Step 1: Chip Number
    
    /// Microchip number (optional, formatted as 00000-00000-00000 for display)
    /// Stored as digits-only string (no dashes)
    @Published var chipNumber: String?
    
    // MARK: - Step 2: Photo
    
    /// Persisted attachment metadata for confirmation card rendering.
    @Published var photoAttachment: PhotoAttachmentMetadata?
    
    /// Attachment lifecycle to drive UI state machine.
    @Published var photoStatus: PhotoAttachmentStatus = .empty
    
    // MARK: - Step 3: Animal Description
    
    /// Date when the animal disappeared (required)
    @Published var disappearanceDate: Date?
    
    /// Animal species (required)
    @Published var animalSpecies: AnimalSpecies?
    
    /// Animal breed/race (required)
    @Published var animalRace: String?
    
    /// Animal gender (required)
    @Published var animalGender: AnimalGender?
    
    /// Animal age in years (optional, 0-40 range)
    @Published var animalAge: Int?
    
    /// Last known latitude (optional, -90 to 90 range)
    @Published var animalLatitude: Double?
    
    /// Last known longitude (optional, -180 to 180 range)
    @Published var animalLongitude: Double?
    
    /// Additional description (optional, max 500 characters)
    @Published var animalAdditionalDescription: String?
    
    // MARK: - Step 4: Contact Details
    
    /// Owner's email address (optional)
    @Published var contactEmail: String?
    
    /// Owner's phone number (optional)
    @Published var contactPhone: String?
    
    // MARK: - Initialization
    
    init(photoAttachmentCache: PhotoAttachmentCacheProtocol) {
        self.photoAttachmentCache = photoAttachmentCache
    }
    
    // MARK: - Methods
    
    /// Clears all flow state (called when exiting flow)
    func clear() async {
        chipNumber = nil
        photoAttachment = nil
        photoStatus = .empty
        disappearanceDate = nil
        animalSpecies = nil
        animalRace = nil
        animalGender = nil
        animalAge = nil
        animalLatitude = nil
        animalLongitude = nil
        animalAdditionalDescription = nil
        contactEmail = nil
        contactPhone = nil
        
        try? await photoAttachmentCache.clearCurrent()
    }
}

