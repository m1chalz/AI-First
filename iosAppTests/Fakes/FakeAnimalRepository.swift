import Foundation
@testable import iosApp

/// Fake implementation of AnimalRepositoryProtocol for unit testing
class FakeAnimalRepository: AnimalRepositoryProtocol {
    // MARK: - Test Configuration
    
    var shouldThrowOnCreateAnnouncement = false
    var shouldThrowOnUploadPhoto = false
    var createAnnouncementThrowsError: Error?
    var uploadPhotoThrowsError: Error?
    
    var mockAnnouncementResult = AnnouncementResult(
        id: "test-announcement-id",
        managementPassword: "123456"
    )
    
    // MARK: - Call Tracking
    
    var createAnnouncementCalled = false
    var uploadPhotoCalled = false
    var lastCreateAnnouncementData: CreateAnnouncementData?
    var lastUploadPhotoAnnouncementId: String?
    var lastUploadPhotoMetadata: PhotoAttachmentMetadata?
    var lastUploadPhotoPassword: String?
    
    // MARK: - AnimalRepositoryProtocol Implementation
    
    func getAnimals(near location: Coordinate?, range: Int) async throws -> [Animal] {
        return []
    }
    
    func getPetDetails(id: String) async throws -> PetDetails {
        fatalError("Not implemented for this test")
    }
    
    func createAnnouncement(data: CreateAnnouncementData) async throws -> AnnouncementResult {
        createAnnouncementCalled = true
        lastCreateAnnouncementData = data
        
        if shouldThrowOnCreateAnnouncement {
            throw createAnnouncementThrowsError ?? RepositoryError.networkError(NSError(domain: "Test", code: -1))
        }
        
        return mockAnnouncementResult
    }
    
    func uploadPhoto(announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String) async throws {
        uploadPhotoCalled = true
        lastUploadPhotoAnnouncementId = announcementId
        lastUploadPhotoMetadata = photo
        lastUploadPhotoPassword = managementPassword
        
        if shouldThrowOnUploadPhoto {
            throw uploadPhotoThrowsError ?? RepositoryError.networkError(NSError(domain: "Test", code: -1))
        }
    }
}

