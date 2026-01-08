import XCTest
@testable import PetSpot

@MainActor
final class MissingPetSummaryViewModelTests: XCTestCase {
    
    var flowState: ReportMissingPetFlowState!
    var cache: PhotoAttachmentCacheFake!
    var toastScheduler: ToastSchedulerFake!
    var sut: MissingPetSummaryViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        toastScheduler = ToastSchedulerFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        sut = MissingPetSummaryViewModel(
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
    
    // MARK: - handleClose() Tests (US3 - T041)
    
    func testHandleClose_shouldTriggerOnCloseCallback() {
        // Given: onClose callback is set
        var closeCalled = false
        sut.onClose = { closeCalled = true }
        
        // When: handleClose() is called
        sut.handleClose()
        
        // Then: onClose callback should be triggered
        XCTAssertTrue(closeCalled)
    }
    
    func testHandleClose_whenOnCloseIsNil_shouldNotCrash() {
        // Given: onClose callback is nil (default)
        sut.onClose = nil
        
        // When: handleClose() is called
        // Then: Should not crash
        XCTAssertNoThrow(sut.handleClose())
    }
    
    func testHandleClose_fromSummaryScreen_shouldDismissFlow() {
        // Given: Summary screen with onClose callback
        var flowDismissed = false
        sut.onClose = { flowDismissed = true }
        
        // When: User taps Close button (handleClose called)
        sut.handleClose()
        
        // Then: Flow should be dismissed
        XCTAssertTrue(flowDismissed, "Close button should dismiss entire flow")
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

