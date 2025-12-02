import Foundation

/// Orchestrates 2-step announcement submission (create + photo upload)
class AnnouncementSubmissionService {
    private let repository: AnimalRepositoryProtocol
    
    init(repository: AnimalRepositoryProtocol) {
        self.repository = repository
    }
    
    /// Submits complete announcement with photo.
    /// Orchestrates: (1) create announcement → (2) upload photo → return managementPassword.
    /// - Parameter flowState: ReportMissingPetFlowState with all data from Steps 1-4
    /// - Returns: managementPassword for summary screen
    /// - Throws: Error on submission failure (network, backend, validation)
    func submitAnnouncement(flowState: ReportMissingPetFlowState) async throws -> String {
        // Build domain model from FlowState
        let announcementData = try buildAnnouncementData(from: flowState)
        
        // Step 1: Create announcement
        let result = try await repository.createAnnouncement(data: announcementData)
        
        // Step 2: Upload photo (if exists)
        if let photoAttachment = flowState.photoAttachment {
            try await repository.uploadPhoto(
                announcementId: result.id,
                photo: photoAttachment,
                managementPassword: result.managementPassword
            )
        }
        
        // Return managementPassword for summary
        return result.managementPassword
    }
    
    private func buildAnnouncementData(from flowState: ReportMissingPetFlowState) throws -> CreateAnnouncementData {
        guard let contactDetails = flowState.contactDetails else {
            throw ValidationError.missingContactDetails
        }
        
        guard let species = flowState.animalSpecies else {
            throw ValidationError.missingSpecies
        }
        
        guard let gender = flowState.animalGender else {
            throw ValidationError.missingGender
        }
        
        guard let disappearanceDate = flowState.disappearanceDate else {
            throw ValidationError.missingDate
        }
        
        return CreateAnnouncementData(
            species: species.rawValue.uppercased(),
            sex: gender.rawValue.uppercased(),
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
            description: flowState.animalAdditionalDescription,
            reward: contactDetails.rewardDescription
        )
    }
}

// MARK: - Validation Errors

enum ValidationError: Error, LocalizedError {
    case missingContactDetails
    case missingSpecies
    case missingGender
    case missingDate
    
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
        }
    }
}

