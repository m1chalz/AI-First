import XCTest
@testable import PetSpot

@MainActor
final class SummaryViewModelTests: XCTestCase {
    
    var flowState: ReportMissingPetFlowState!
    var cache: PhotoAttachmentCacheFake!
    var sut: SummaryViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        sut = SummaryViewModel(flowState: flowState)
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
    
    // MARK: - handleSubmit() Tests
    
    func testHandleSubmit_shouldTriggerOnSubmitCallback() {
        // Given: onSubmit callback is set
        var submitCalled = false
        sut.onSubmit = { submitCalled = true }
        
        // When: handleSubmit() is called
        sut.handleSubmit()
        
        // Then: onSubmit callback should be triggered
        XCTAssertTrue(submitCalled)
    }
    
    func testHandleSubmit_whenOnSubmitIsNil_shouldNotCrash() {
        // Given: onSubmit callback is nil (default)
        sut.onSubmit = nil
        
        // When: handleSubmit() is called
        // Then: Should not crash
        XCTAssertNoThrow(sut.handleSubmit())
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
    
    // MARK: - User Story 2: Backward Navigation Tests
    
    func testHandleBack_fromSummary_shouldNavigateToContactDetails() {
        // Given: onBack callback is set on summary screen
        var navigatedBack = false
        sut.onBack = { navigatedBack = true }
        
        // When: handleBack() is called from summary
        sut.handleBack()
        
        // Then: Should navigate back to contact details screen (step 4)
        XCTAssertTrue(navigatedBack, "Tapping back on summary should navigate to step 4")
    }
}

