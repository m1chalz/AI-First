import XCTest
@testable import PetSpot

/// Unit tests for ContactDetailsViewModel validation logic (User Story 2)
@MainActor
final class MissingPetContactDetailsViewModelValidationTests: XCTestCase {
    var sut: MissingPetContactDetailsViewModel!
    var fakeService: FakeAnnouncementSubmissionService!
    var flowState: ReportMissingPetFlowState!
    var fakePhotoCache: PhotoAttachmentCacheFake!
    
    override func setUp() async throws {
        try await super.setUp()
        fakeService = FakeAnnouncementSubmissionService()
        fakePhotoCache = PhotoAttachmentCacheFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: fakePhotoCache)
        sut = MissingPetContactDetailsViewModel(
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
    
    // MARK: - Test: Phone validation
    
    func testPhoneValidation_when7Digits_shouldBeValid() async {
        // Given: Phone with exactly 7 digits
        sut.phone = "1234567"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No phone error, service called
        XCTAssertNil(sut.phoneError, "Should accept 7 digits")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    func testPhoneValidation_when11Digits_shouldBeValid() async {
        // Given: Phone with exactly 11 digits
        sut.phone = "12345678901"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No phone error, service called
        XCTAssertNil(sut.phoneError, "Should accept 11 digits")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    func testPhoneValidation_whenLessThan7Digits_shouldBeInvalid() async {
        // Given: Phone with less than 7 digits
        sut.phone = "123456"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: Phone error set, service not called
        XCTAssertNotNil(sut.phoneError, "Should show error for < 7 digits")
        XCTAssertEqual(sut.phoneError, L10n.OwnersDetails.Phone.error)
        XCTAssertFalse(fakeService.submitAnnouncementCalled, "Should not submit when invalid")
    }
    
    func testPhoneValidation_whenMoreThan11Digits_shouldBeInvalid() async {
        // Given: Phone with more than 11 digits
        sut.phone = "123456789012"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: Phone error set, service not called
        XCTAssertNotNil(sut.phoneError, "Should show error for > 11 digits")
        XCTAssertFalse(fakeService.submitAnnouncementCalled, "Should not submit when invalid")
    }
    
    func testPhoneValidation_whenLeadingPlus_shouldBeValid() async {
        // Given: Phone with leading +
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No phone error, service called
        XCTAssertNil(sut.phoneError, "Should accept leading +")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    func testPhoneValidation_whenSpacesAndDashes_shouldSanitizeAndValidate() async {
        // Given: Phone with spaces and dashes (11 digits after sanitization)
        sut.phone = "+48 123-456-789"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No phone error (sanitized to 11 digits), service called
        XCTAssertNil(sut.phoneError, "Should sanitize spaces/dashes and validate")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    // MARK: - Test: Email validation
    
    func testEmailValidation_whenValidFormat_shouldBeValid() async {
        // Given: Valid email with local@domain.tld format
        sut.phone = "+48123456789"
        sut.email = "owner@example.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No email error, service called
        XCTAssertNil(sut.emailError, "Should accept valid email")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    func testEmailValidation_whenMissingAt_shouldBeInvalid() async {
        // Given: Email without @
        sut.phone = "+48123456789"
        sut.email = "ownerexample.com"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: Email error set, service not called
        XCTAssertNotNil(sut.emailError, "Should show error for missing @")
        XCTAssertEqual(sut.emailError, L10n.OwnersDetails.Email.error)
        XCTAssertFalse(fakeService.submitAnnouncementCalled, "Should not submit when invalid")
    }
    
    func testEmailValidation_whenCaseInsensitive_shouldBeValid() async {
        // Given: Email with mixed case
        sut.phone = "+48123456789"
        sut.email = "Owner@Example.COM"
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No email error, service called
        XCTAssertNil(sut.emailError, "Should accept case-insensitive email")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    func testEmailValidation_whenWhitespace_shouldTrimAndValidate() async {
        // Given: Email with leading/trailing whitespace
        sut.phone = "+48123456789"
        sut.email = "  owner@example.com  "
        
        // When: Submit form
        await sut.submitForm()
        
        // Then: No email error (trimmed), service called
        XCTAssertNil(sut.emailError, "Should trim whitespace and validate")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
    }
    
    // MARK: - Test: Error clearing
    
    func testSubmitForm_whenInvalidPhone_thenValidPhone_shouldClearError() async {
        // Given: Invalid phone first
        sut.phone = "123"
        sut.email = "owner@example.com"
        
        // When: Submit form (invalid)
        await sut.submitForm()
        
        // Then: Phone error set
        XCTAssertNotNil(sut.phoneError, "Should show error")
        
        // Given: Fix phone to valid
        sut.phone = "+48123456789"
        
        // When: Submit form again
        await sut.submitForm()
        
        // Then: Phone error cleared, service called
        XCTAssertNil(sut.phoneError, "Should clear error when corrected")
        XCTAssertTrue(fakeService.submitAnnouncementCalled, "Should submit when valid")
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

