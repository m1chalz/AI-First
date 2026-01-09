import XCTest
@testable import PetSpot

/// Unit tests for ContactDetailsViewModel error handling (User Story 4)
@MainActor
final class FoundPetContactDetailsViewModelErrorHandlingTests: XCTestCase {
    var sut: FoundPetContactDetailsViewModel!
    var fakeService: FakeAnnouncementSubmissionService!
    var flowState: FoundPetReportFlowState!
    var fakePhotoCache: PhotoAttachmentCacheFake!
    
    override func setUp() async throws {
        try await super.setUp()
        fakeService = FakeAnnouncementSubmissionService()
        fakePhotoCache = PhotoAttachmentCacheFake()
        flowState = FoundPetReportFlowState(photoAttachmentCache: fakePhotoCache)
        sut = FoundPetContactDetailsViewModel(
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
    
    // MARK: - Test: Network error handling
    
    func testSubmitForm_whenNetworkError_shouldShowNoConnectionAlert() async {
        // Given: Valid inputs, service throws URLError.notConnectedToInternet
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        fakeService.shouldThrow = true
        fakeService.throwsError = NSError(
            domain: NSURLErrorDomain,
            code: NSURLErrorNotConnectedToInternet
        )
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: isSubmitting = false, showAlert = true, alertMessage = no connection message
        XCTAssertFalse(sut.isSubmitting, "Should set isSubmitting = false after error")
        XCTAssertTrue(sut.showAlert, "Should show alert on error")
        XCTAssertEqual(sut.alertMessage, L10n.OwnersDetails.Error.NoConnection.message)
    }
    
    func testSubmitForm_whenTimeoutError_shouldShowGenericAlert() async {
        // Given: Valid inputs, service throws URLError.timedOut
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        fakeService.shouldThrow = true
        fakeService.throwsError = NSError(
            domain: NSURLErrorDomain,
            code: NSURLErrorTimedOut
        )
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: isSubmitting = false, showAlert = true, alertMessage = generic message
        XCTAssertFalse(sut.isSubmitting, "Should set isSubmitting = false after error")
        XCTAssertTrue(sut.showAlert, "Should show alert on error")
        // Note: Currently maps timeout to generic message (could be enhanced to show no connection)
    }
    
    // MARK: - Test: Backend error handling
    
    func testSubmitForm_whenBackendError_shouldShowGenericAlert() async {
        // Given: Valid inputs, service throws generic error
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        fakeService.shouldThrow = true
        fakeService.throwsError = RepositoryError.httpError(statusCode: 500)
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: isSubmitting = false, showAlert = true, alertMessage = generic message
        XCTAssertFalse(sut.isSubmitting, "Should set isSubmitting = false after error")
        XCTAssertTrue(sut.showAlert, "Should show alert on error")
        XCTAssertEqual(sut.alertMessage, L10n.OwnersDetails.Error.Generic.message)
    }
    
    // MARK: - Test: Input persistence on failure
    
    func testSubmitForm_whenError_shouldKeepInputsIntact() async {
        // Given: Valid inputs, service throws error
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        fakeService.shouldThrow = true
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: Inputs remain intact
        XCTAssertEqual(sut.phone, "+48123456789", "Phone should remain intact")
        XCTAssertEqual(sut.email, "owner@example.com", "Email should remain intact")
    }
    
    func testSubmitForm_whenError_shouldKeepValidationErrorsCleared() async {
        // Given: Valid inputs (no validation errors), service throws error
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        fakeService.shouldThrow = true
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: Validation errors remain cleared (not re-introduced by submission error)
        XCTAssertNil(sut.phoneError, "Validation errors should remain cleared")
        XCTAssertNil(sut.emailError, "Validation errors should remain cleared")
    }
    
    // MARK: - Test: Retry logic
    
    func testSubmitForm_whenRetry_shouldExecuteFullSubmission() async {
        // Given: Valid inputs, service throws error first time
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        fakeService.shouldThrow = true
        
        // When: Submit form (first attempt)
        await sut.submitForm()
        
        // Then: Error shown
        XCTAssertTrue(sut.showAlert, "Should show alert after first failure")
        
        // Given: Fix service (simulate retry after connection restored)
        fakeService.shouldThrow = false
        
        // When: Submit form again (retry)
        await sut.submitForm()
        
        // Then: Full 2-step submission retried from step 1
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should retry full submission")
        XCTAssertFalse(sut.showAlert, "Alert should be dismissed after successful retry")
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

