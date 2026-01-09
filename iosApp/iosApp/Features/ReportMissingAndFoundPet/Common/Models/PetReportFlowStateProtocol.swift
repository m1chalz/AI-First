import Foundation

/// Protocol defining common properties required for pet report submission.
/// Both MissingPetReportFlowState and FoundPetReportFlowState conform to this protocol,
/// enabling shared submission logic via AnnouncementSubmissionService.
@MainActor
protocol PetReportFlowStateProtocol: AnyObject {
    // MARK: - Step 1: Chip Number
    
    var chipNumber: String? { get }
    
    // MARK: - Step 2: Photo
    
    var photoAttachment: PhotoAttachmentMetadata? { get }
    var photoStatus: PhotoAttachmentStatus { get }
    
    // MARK: - Step 3: Animal Description
    
    var disappearanceDate: Date? { get }
    var animalSpecies: AnimalSpecies? { get }
    var animalRace: String? { get }
    var animalGender: AnimalGender? { get }
    var animalAge: Int? { get }
    var petName: String? { get }
    var animalLatitude: Double? { get }
    var animalLongitude: Double? { get }
    var animalAdditionalDescription: String? { get }
    
    // MARK: - Step 4: Contact Details
    
    var contactDetails: OwnerContactDetails? { get }
    
    // MARK: - Step 5: Submission Result
    
    var managementPassword: String? { get set }
    
    // MARK: - Status (determined by flow type)
    
    /// Status to be sent to backend: .active for Missing flow, .found for Found flow
    var status: AnnouncementStatus { get }
}

