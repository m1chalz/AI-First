import XCTest
@testable import PetSpot

@MainActor
final class SummaryViewModelTests: XCTestCase {
    
    var flowState: ReportMissingPetFlowState!
    var cache: PhotoAttachmentCacheFake!
    var toastScheduler: ToastSchedulerFake!
    var sut: SummaryViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        toastScheduler = ToastSchedulerFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        sut = SummaryViewModel(
            flowState: flowState,
            toastScheduler: toastScheduler
        )
    }
    
    override func tearDown() {
        sut = nil
        toastScheduler = nil
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
    
    // MARK: - handleSubmit() Tests (US3 - T041)
    
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
    
    func testHandleSubmit_fromSummaryScreen_shouldDismissFlow() {
        // Given: Summary screen with onSubmit callback
        var flowDismissed = false
        sut.onSubmit = { flowDismissed = true }
        
        // When: User taps Close button (handleSubmit called)
        sut.handleSubmit()
        
        // Then: Flow should be dismissed
        XCTAssertTrue(flowDismissed, "Close button should dismiss entire flow")
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
    
    // MARK: - Management Password Display Tests (T012-T013)
    
    func testDisplayPassword_whenPasswordIsNil_shouldReturnEmptyString() {
        // Given: FlowState with nil password
        flowState.managementPassword = nil
        
        // When: displayPassword is accessed
        let result = sut.displayPassword
        
        // Then: Returns empty string
        XCTAssertEqual(result, "")
    }
    
    func testDisplayPassword_whenPasswordExists_shouldReturnPassword() {
        // Given: FlowState with password
        let expectedPassword = "5216577"
        flowState.managementPassword = expectedPassword
        
        // When: displayPassword is accessed
        let result = sut.displayPassword
        
        // Then: Returns password value
        XCTAssertEqual(result, expectedPassword)
    }
    
    // MARK: - Copy Password Tests (T014-T015)
    
    func testCopyPasswordToClipboard_whenPasswordExists_shouldCopyAndShowToast() {
        // Given: FlowState with password
        let expectedPassword = "5216577"
        flowState.managementPassword = expectedPassword
        
        // When: copyPasswordToClipboard is called
        sut.copyPasswordToClipboard()
        
        // Then: Password is copied to clipboard
        XCTAssertEqual(UIPasteboard.general.string, expectedPassword)
        
        // And: Toast is shown
        XCTAssertTrue(sut.showsCodeCopiedToast)
        
        // And: Toast is scheduled to hide after 2 seconds
        XCTAssertEqual(toastScheduler.scheduledDurations, [2.0])
    }
    
    func testCopyPasswordToClipboard_whenPasswordIsNil_shouldNotCopy() {
        // Given: FlowState with nil password
        flowState.managementPassword = nil
        UIPasteboard.general.string = "previous content"
        
        // When: copyPasswordToClipboard is called
        sut.copyPasswordToClipboard()
        
        // Then: Clipboard is unchanged
        XCTAssertEqual(UIPasteboard.general.string, "previous content")
        
        // And: Toast is not shown
        XCTAssertFalse(sut.showsCodeCopiedToast)
    }
}

