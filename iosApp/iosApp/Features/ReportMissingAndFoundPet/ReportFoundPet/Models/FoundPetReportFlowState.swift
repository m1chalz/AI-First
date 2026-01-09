import Foundation

/// Shared state for Found Pet Report flow.
/// Owned by FoundPetReportCoordinator and injected into all ViewModels.
/// Persists data during forward/backward navigation within active session.
@MainActor
final class FoundPetReportFlowState: ObservableObject, PetReportFlowStateProtocol {
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
    
    /// Pet name (optional, trimmed before API submission)
    @Published var petName: String?
    
    /// Last known latitude (optional, -90 to 90 range)
    @Published var animalLatitude: Double?
    
    /// Last known longitude (optional, -180 to 180 range)
    @Published var animalLongitude: Double?
    
    /// Additional description (optional, max 500 characters)
    @Published var animalAdditionalDescription: String?
    
    // MARK: - Step 4: Contact Details
    
    /// Owner's contact details (phone, email, reward)
    @Published var contactDetails: OwnerContactDetails?
    
    // MARK: - Step 4b: iOS-only Contact Fields (not sent to backend per FR-016)
    
    /// Optional caregiver phone number (7-11 digits when provided)
    /// iOS-only field - kept in flow state but NOT sent to backend
    @Published var caregiverPhoneNumber: String?
    
    /// Optional address where animal is currently located (multiline, max 500 chars)
    /// iOS-only field - kept in flow state but NOT sent to backend
    @Published var currentPhysicalAddress: String?
    
    // MARK: - Step 5: Submission Result
    
    /// Management password returned by backend after successful report submission.
    /// Used for report management (edit/delete). Also sent to user's email.
    /// Nil before submission completes.
    @Published var managementPassword: String?
    
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
        petName = nil
        animalLatitude = nil
        animalLongitude = nil
        animalAdditionalDescription = nil
        contactDetails = nil
        managementPassword = nil
        // Clear iOS-only fields (per FR-016)
        caregiverPhoneNumber = nil
        currentPhysicalAddress = nil

        try? await photoAttachmentCache.clearCurrent()
    }
}

