import Foundation

/// Protocol for announcement submission orchestration
protocol AnnouncementSubmissionServiceProtocol {
    /// Submits complete announcement with photo.
    /// - Parameter flowState: PetReportFlowStateProtocol with all data from Steps 1-4
    /// - Returns: managementPassword for summary screen
    /// - Throws: Error on submission failure (network, backend, validation)
    @MainActor
    func submitAnnouncement(flowState: PetReportFlowStateProtocol) async throws -> String
}

/// Orchestrates 2-step announcement submission (create + photo upload)
class AnnouncementSubmissionService: AnnouncementSubmissionServiceProtocol {
    private let repository: AnnouncementRepositoryProtocol
    
    init(repository: AnnouncementRepositoryProtocol) {
        self.repository = repository
    }
    
    /// Submits complete announcement with photo.
    /// Orchestrates: (1) create announcement → (2) upload photo → return managementPassword.
    /// - Parameter flowState: PetReportFlowStateProtocol with all data from Steps 1-4
    /// - Returns: managementPassword for summary screen
    /// - Throws: Error on submission failure (network, backend, validation)
    @MainActor
    func submitAnnouncement(flowState: PetReportFlowStateProtocol) async throws -> String {
        // Validate photo upfront (required)
        guard let photoAttachment = flowState.photoAttachment else {
            throw SubmissionValidationError.missingPhoto
        }
        
        // Build domain model from FlowState
        let announcementData = try await buildAnnouncementData(from: flowState)
        
        // Step 1: Create announcement
        let result = try await repository.createAnnouncement(data: announcementData)
        
        // Step 2: Upload photo
        
        try await repository.uploadPhoto(
            announcementId: result.id,
            photo: photoAttachment,
            managementPassword: result.managementPassword
        )
        
        // Return managementPassword for summary
        return result.managementPassword
    }
    
    @MainActor
    private func buildAnnouncementData(from flowState: PetReportFlowStateProtocol) async throws -> CreateAnnouncementData {
        guard let contactDetails = flowState.contactDetails else {
            throw SubmissionValidationError.missingContactDetails
        }
        
        guard let species = flowState.animalSpecies else {
            throw SubmissionValidationError.missingSpecies
        }
        
        guard let gender = flowState.animalGender else {
            throw SubmissionValidationError.missingGender
        }
        
        guard let disappearanceDate = flowState.disappearanceDate else {
            throw SubmissionValidationError.missingDate
        }
        
        return CreateAnnouncementData(
            species: species,
            breed: flowState.animalRace,
            sex: gender,
            age: flowState.animalAge,
            lastSeenDate: disappearanceDate,
            location: (
                latitude: flowState.animalLatitude ?? 0.0,
                longitude: flowState.animalLongitude ?? 0.0
            ),
            contact: (
                email: contactDetails.email.trimmingCharacters(in: .whitespaces),
                phone: contactDetails.phone.filter { $0.isNumber || $0 == "+" }
            ),
            microchipNumber: flowState.chipNumber,
            petName: flowState.petName,
            description: flowState.animalAdditionalDescription,
            reward: contactDetails.rewardDescription
        )
    }
}

// MARK: - Validation Errors

enum SubmissionValidationError: Error, LocalizedError {
    case missingContactDetails
    case missingSpecies
    case missingGender
    case missingDate
    case missingPhoto
    
    var errorDescription: String? {
        switch self {
        case .missingContactDetails:
            return "Contact details are required"
        case .missingSpecies:
            return "Animal species is required"
        case .missingGender:
            return "Animal gender is required"
        case .missingDate:
            return "Disappearance date is required"
        case .missingPhoto:
            return "Photo is required for announcement submission"
        }
    }
}
