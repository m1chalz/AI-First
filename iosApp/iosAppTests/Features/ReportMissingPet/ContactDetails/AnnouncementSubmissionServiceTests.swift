import XCTest
@testable import PetSpot

/// Unit tests for AnnouncementSubmissionService
@MainActor
final class AnnouncementSubmissionServiceTests: XCTestCase {
    var sut: AnnouncementSubmissionService!
    var fakeRepository: FakeAnimalRepository!
    var flowState: ReportMissingPetFlowState!
    var fakePhotoCache: PhotoAttachmentCacheFake!
    
    override func setUp() async throws {
        try await super.setUp()
        fakeRepository = FakeAnimalRepository()
        fakePhotoCache = PhotoAttachmentCacheFake()
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
        flowState.photoAttachment = PhotoAttachmentMetadata(
            id: UUID(),
            fileName: "test.jpg",
            fileSizeBytes: 1024,
            utiIdentifier: "public.jpeg",
            pixelWidth: 800,
            pixelHeight: 600,
            assetIdentifier: nil,
            cachedURL: URL(fileURLWithPath: "/tmp/test.jpg"),
            savedAt: Date()
        )
        
        // When: Submit announcement
        _ = try await sut.submitAnnouncement(flowState: flowState)
        
        // Then: Repository called with correct data
        XCTAssertTrue(fakeRepository.createAnnouncementCalled, "createAnnouncement should be called")
        
        let data = fakeRepository.lastCreateAnnouncementData
        XCTAssertNotNil(data)
        XCTAssertEqual(data?.species, .dog)
        XCTAssertEqual(data?.sex, .male)
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
        
        // When: Submit announcement
        let password = try await sut.submitAnnouncement(flowState: flowState)
        
        // Then: Both steps executed in order
        XCTAssertTrue(fakeRepository.createAnnouncementCalled, "Step 1: createAnnouncement should be called")
        XCTAssertTrue(fakeRepository.uploadPhotoCalled, "Step 2: uploadPhoto should be called")
        XCTAssertEqual(password, "123456", "Should return managementPassword from step 1")
        
        // Verify photo upload parameters
        XCTAssertEqual(fakeRepository.lastUploadPhotoAnnouncementId, "test-announcement-id")
        XCTAssertEqual(fakeRepository.lastUploadPhotoPassword, "123456")
        XCTAssertEqual(fakeRepository.lastUploadPhotoMetadata?.cachedURL, flowState.photoAttachment?.cachedURL)
    }
    
    func testSubmitAnnouncement_whenPhotoMissing_shouldThrowError() async {
        // Given: Valid FlowState without photo attachment (photo is required)
        setupValidFlowState()
        flowState.photoAttachment = nil
        
        // When/Then: Should throw missingPhoto error
        do {
            _ = try await sut.submitAnnouncement(flowState: flowState)
            XCTFail("Expected error to be thrown")
        } catch let error as SubmissionValidationError {
            XCTAssertEqual(error, .missingPhoto, "Should throw missingPhoto error")
            XCTAssertFalse(fakeRepository.createAnnouncementCalled, "createAnnouncement should not be called")
            XCTAssertFalse(fakeRepository.uploadPhotoCalled, "uploadPhoto should not be called")
        } catch {
            XCTFail("Expected SubmissionValidationError, got \(error)")
        }
    }
    
    // MARK: - Test: Returns managementPassword on success
    
    func testSubmitAnnouncement_whenSuccess_shouldReturnManagementPassword() async throws {
        // Given: Valid FlowState with photo
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
        // Given: Valid FlowState with photo, repository throws error on createAnnouncement
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
        // Photo is required for submission
        flowState.photoAttachment = PhotoAttachmentMetadata(
            id: UUID(),
            fileName: "test.jpg",
            fileSizeBytes: 1024,
            utiIdentifier: "public.jpeg",
            pixelWidth: 800,
            pixelHeight: 600,
            assetIdentifier: nil,
            cachedURL: URL(fileURLWithPath: "/tmp/test.jpg"),
            savedAt: Date()
        )
    }
}

