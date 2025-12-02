import XCTest
@testable import iosApp

/// Unit tests for AnnouncementSubmissionService
@MainActor
final class AnnouncementSubmissionServiceTests: XCTestCase {
    var sut: AnnouncementSubmissionService!
    var fakeRepository: FakeAnimalRepository!
    var flowState: ReportMissingPetFlowState!
    var fakePhotoCache: FakePhotoAttachmentCache!
    
    override func setUp() async throws {
        try await super.setUp()
        fakeRepository = FakeAnimalRepository()
        fakePhotoCache = FakePhotoAttachmentCache()
        sut = AnnouncementSubmissionService(repository: fakeRepository)
        flowState = ReportMissingPetFlowState(photoAttachmentCache: fakePhotoCache)
    }
    
    override func tearDown() async throws {
        sut = nil
        fakeRepository = nil
        flowState = nil
        fakePhotoCache = nil
        try await super.tearDown()
    }
    
    // MARK: - Test: buildAnnouncementData from FlowState
    
    func testSubmitAnnouncement_whenFlowStateValid_shouldBuildCorrectData() async throws {
        // Given: Valid FlowState with all required fields
        flowState.animalSpecies = .dog
        flowState.animalGender = .male
        flowState.disappearanceDate = Date(timeIntervalSince1970: 1701388800) // 2023-12-01
        flowState.animalLatitude = 52.2297
        flowState.animalLongitude = 21.0122
        flowState.chipNumber = "123456789012345"
        flowState.animalAdditionalDescription = "Golden retriever, very friendly"
        flowState.contactDetails = OwnerContactDetails(
            phone: "+48123456789",
            email: "owner@example.com",
            rewardDescription: "$250 gift card"
        )
        
        // When: Submit announcement
        _ = try await sut.submitAnnouncement(flowState: flowState)
        
        // Then: Repository called with correct data
        XCTAssertTrue(fakeRepository.createAnnouncementCalled, "createAnnouncement should be called")
        
        let data = fakeRepository.lastCreateAnnouncementData
        XCTAssertNotNil(data)
        XCTAssertEqual(data?.species, "DOG")
        XCTAssertEqual(data?.sex, "MALE")
        XCTAssertEqual(data?.location.latitude, 52.2297)
        XCTAssertEqual(data?.location.longitude, 21.0122)
        XCTAssertEqual(data?.contact.phone, "+48123456789")
        XCTAssertEqual(data?.contact.email, "owner@example.com")
        XCTAssertEqual(data?.microchipNumber, "123456789012345")
        XCTAssertEqual(data?.description, "Golden retriever, very friendly")
        XCTAssertEqual(data?.reward, "$250 gift card")
    }
    
    // MARK: - Test: 2-step orchestration
    
    func testSubmitAnnouncement_whenPhotoExists_shouldOrchestrate2Steps() async throws {
        // Given: Valid FlowState with photo attachment
        setupValidFlowState()
        let mockPhotoMetadata = PhotoAttachmentMetadata(
            cachedURL: URL(fileURLWithPath: "/tmp/test.jpg"),
            mimeType: "image/jpeg",
            fileSize: 1024,
            capturedAt: Date()
        )
        flowState.photoAttachment = mockPhotoMetadata
        
        // When: Submit announcement
        let password = try await sut.submitAnnouncement(flowState: flowState)
        
        // Then: Both steps executed in order
        XCTAssertTrue(fakeRepository.createAnnouncementCalled, "Step 1: createAnnouncement should be called")
        XCTAssertTrue(fakeRepository.uploadPhotoCalled, "Step 2: uploadPhoto should be called")
        XCTAssertEqual(password, "123456", "Should return managementPassword from step 1")
        
        // Verify photo upload parameters
        XCTAssertEqual(fakeRepository.lastUploadPhotoAnnouncementId, "test-announcement-id")
        XCTAssertEqual(fakeRepository.lastUploadPhotoPassword, "123456")
        XCTAssertEqual(fakeRepository.lastUploadPhotoMetadata?.cachedURL, mockPhotoMetadata.cachedURL)
    }
    
    func testSubmitAnnouncement_whenPhotoMissing_shouldSkipPhotoUpload() async throws {
        // Given: Valid FlowState without photo attachment
        setupValidFlowState()
        flowState.photoAttachment = nil
        
        // When: Submit announcement
        let password = try await sut.submitAnnouncement(flowState: flowState)
        
        // Then: Only step 1 executed, step 2 skipped
        XCTAssertTrue(fakeRepository.createAnnouncementCalled, "Step 1: createAnnouncement should be called")
        XCTAssertFalse(fakeRepository.uploadPhotoCalled, "Step 2: uploadPhoto should be skipped")
        XCTAssertEqual(password, "123456", "Should return managementPassword from step 1")
    }
    
    // MARK: - Test: Returns managementPassword on success
    
    func testSubmitAnnouncement_whenSuccess_shouldReturnManagementPassword() async throws {
        // Given: Valid FlowState
        setupValidFlowState()
        fakeRepository.mockAnnouncementResult = AnnouncementResult(
            id: "test-id",
            managementPassword: "999888"
        )
        
        // When: Submit announcement
        let password = try await sut.submitAnnouncement(flowState: flowState)
        
        // Then: Return managementPassword from response
        XCTAssertEqual(password, "999888")
    }
    
    // MARK: - Test: Error handling for step 1 failure
    
    func testSubmitAnnouncement_whenStep1Fails_shouldThrowError() async {
        // Given: Valid FlowState, repository throws error on createAnnouncement
        setupValidFlowState()
        fakeRepository.shouldThrowOnCreateAnnouncement = true
        fakeRepository.createAnnouncementThrowsError = RepositoryError.networkError(
            NSError(domain: NSURLErrorDomain, code: NSURLErrorNotConnectedToInternet)
        )
        
        // When/Then: Submit announcement throws error
        do {
            _ = try await sut.submitAnnouncement(flowState: flowState)
            XCTFail("Should throw error")
        } catch {
            XCTAssertTrue(error is RepositoryError, "Should throw RepositoryError")
            XCTAssertFalse(fakeRepository.uploadPhotoCalled, "Step 2 should not be called after step 1 failure")
        }
    }
    
    // MARK: - Test: Error handling for step 2 failure
    
    func testSubmitAnnouncement_whenStep2Fails_shouldThrowError() async {
        // Given: Valid FlowState with photo, repository throws error on uploadPhoto
        setupValidFlowState()
        flowState.photoAttachment = PhotoAttachmentMetadata(
            cachedURL: URL(fileURLWithPath: "/tmp/test.jpg"),
            mimeType: "image/jpeg",
            fileSize: 1024,
            capturedAt: Date()
        )
        fakeRepository.shouldThrowOnUploadPhoto = true
        fakeRepository.uploadPhotoThrowsError = RepositoryError.unauthorized
        
        // When/Then: Submit announcement throws error
        do {
            _ = try await sut.submitAnnouncement(flowState: flowState)
            XCTFail("Should throw error")
        } catch {
            XCTAssertTrue(error is RepositoryError, "Should throw RepositoryError")
            XCTAssertTrue(fakeRepository.createAnnouncementCalled, "Step 1 should be called")
            XCTAssertTrue(fakeRepository.uploadPhotoCalled, "Step 2 should be called before throwing")
        }
    }
    
    // MARK: - Helpers
    
    private func setupValidFlowState() {
        flowState.animalSpecies = .dog
        flowState.animalGender = .male
        flowState.disappearanceDate = Date()
        flowState.contactDetails = OwnerContactDetails(
            phone: "+48123456789",
            email: "owner@example.com",
            rewardDescription: nil
        )
    }
}

/// Fake photo cache for testing
class FakePhotoAttachmentCache: PhotoAttachmentCacheProtocol {
    func saveCurrent(from url: URL) async throws -> PhotoAttachmentMetadata {
        fatalError("Not implemented")
    }
    
    func getCurrent() -> PhotoAttachmentMetadata? {
        return nil
    }
    
    func clearCurrent() async throws {
        // No-op
    }
}

