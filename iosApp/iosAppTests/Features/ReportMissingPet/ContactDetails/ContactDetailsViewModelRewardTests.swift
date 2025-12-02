import XCTest
@testable import PetSpot

/// Unit tests for ContactDetailsViewModel reward persistence (User Story 3)
@MainActor
final class ContactDetailsViewModelRewardTests: XCTestCase {
    var sut: ContactDetailsViewModel!
    var fakeService: FakeAnnouncementSubmissionService!
    var flowState: ReportMissingPetFlowState!
    var fakePhotoCache: PhotoAttachmentCacheFake!
    
    override func setUp() async throws {
        try await super.setUp()
        fakeService = FakeAnnouncementSubmissionService()
        fakePhotoCache = PhotoAttachmentCacheFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: fakePhotoCache)
        sut = ContactDetailsViewModel(
            submissionService: fakeService,
            flowState: flowState
        )
        setupValidFlowState()
    }
    
    override func tearDown() async throws {
        sut = nil
        fakeService = nil
        flowState = nil
        fakePhotoCache = nil
        try await super.tearDown()
    }
    
    // MARK: - Test: Reward persistence
    
    func testRewardPersistence_whenEmptyField_shouldStoreNil() async {
        // Given: Valid phone/email, empty reward
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        sut.rewardDescription = ""
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: contactDetails.rewardDescription is nil
        XCTAssertNil(flowState.contactDetails?.rewardDescription, "Empty reward should be nil")
    }
    
    func testRewardPersistence_whenNonEmpty_shouldStoreTextVerbatim() async {
        // Given: Valid phone/email, non-empty reward
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        sut.rewardDescription = "$250 gift card + hugs"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: contactDetails.rewardDescription stores text verbatim
        XCTAssertEqual(flowState.contactDetails?.rewardDescription, "$250 gift card + hugs")
    }
    
    func testRewardPersistence_whenNavigateAwayAndBack_shouldPersist() async {
        // Given: Valid phone/email/reward, submit form
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        sut.rewardDescription = "$250 gift card"
        await sut.submitForm()
        
        // When: Create new ViewModel (simulate navigation back)
        let newViewModel = ContactDetailsViewModel(
            submissionService: fakeService,
            flowState: flowState
        )
        
        // Then: Reward persists from FlowState
        XCTAssertEqual(newViewModel.rewardDescription, "$250 gift card")
    }
    
    // MARK: - Helpers
    
    private func setupValidFlowState() {
        flowState.animalSpecies = .dog
        flowState.animalGender = .male
        flowState.disappearanceDate = Date()
        flowState.animalLatitude = 52.2297
        flowState.animalLongitude = 21.0122
    }
}

