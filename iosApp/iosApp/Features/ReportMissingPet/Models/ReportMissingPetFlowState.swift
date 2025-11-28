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
    
    // MARK: - Step 3: Description
    
    /// Additional description about the pet (optional, multi-line)
    @Published var description: String?
    
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
        description = nil
        contactEmail = nil
        contactPhone = nil
        
        try? await photoAttachmentCache.clearCurrent()
    }
}

