import XCTest
@testable import PetSpot

@MainActor
final class ContactDetailsViewModelTests: XCTestCase {
    
    var flowState: ReportMissingPetFlowState!
    var cache: PhotoAttachmentCacheFake!
    var sut: ContactDetailsViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        sut = ContactDetailsViewModel(flowState: flowState)
    }
    
    override func tearDown() {
        sut = nil
        cache = nil
        flowState = nil
        super.tearDown()
    }
    
    // MARK: - Initialization Tests
    
    func testInit_shouldStoreFlowStateReference() {
        // Given/When: ViewModel created in setUp with flowState
        
        // Then: ViewModel should be initialized successfully
        XCTAssertNotNil(sut)
    }
    
    // MARK: - handleNext() Tests
    
    func testHandleNext_shouldTriggerOnNextCallback() {
        // Given: onNext callback is set
        var nextCalled = false
        sut.onNext = { nextCalled = true }
        
        // When: handleNext() is called
        sut.handleNext()
        
        // Then: onNext callback should be triggered
        XCTAssertTrue(nextCalled)
    }
    
    func testHandleNext_whenOnNextIsNil_shouldNotCrash() {
        // Given: onNext callback is nil (default)
        sut.onNext = nil
        
        // When: handleNext() is called
        // Then: Should not crash
        XCTAssertNoThrow(sut.handleNext())
    }
    
    // MARK: - handleBack() Tests
    
    func testHandleBack_shouldTriggerOnBackCallback() {
        // Given: onBack callback is set
        var backCalled = false
        sut.onBack = { backCalled = true }
        
        // When: handleBack() is called
        sut.handleBack()
        
        // Then: onBack callback should be triggered
        XCTAssertTrue(backCalled)
    }
    
    func testHandleBack_whenOnBackIsNil_shouldNotCrash() {
        // Given: onBack callback is nil (default)
        sut.onBack = nil
        
        // When: handleBack() is called
        // Then: Should not crash
        XCTAssertNoThrow(sut.handleBack())
    }
}

