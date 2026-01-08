import XCTest
@testable import PetSpot

@MainActor
final class MissingPetChipNumberViewModelTests: XCTestCase {
    
    var flowState: MissingPetReportFlowState!
    var cache: PhotoAttachmentCacheFake!
    var sut: MissingPetChipNumberViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        flowState = MissingPetReportFlowState(photoAttachmentCache: cache)
        sut = MissingPetChipNumberViewModel(flowState: flowState)
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
    
    func testInit_shouldStartWithEmptyChipNumber() {
        // Given/When: ViewModel created in setUp
        
        // Then
        XCTAssertEqual(sut.chipNumber, "")
    }
    
    // MARK: - Formatting Tests
    
    func testFormatChipNumber_whenInputProvided_shouldApplyFormatter() {
        // Given
        let input = "123456"
        
        // When
        sut.formatChipNumber(input)
        
        // Then
        XCTAssertEqual(sut.chipNumber, "12345-6")
    }
    
    // MARK: - State Persistence
    
    func testInit_whenFlowStateHasDigits_shouldRestoreFormattedValue() {
        // Given
        flowState.chipNumber = "123456789012345"
        
        // When
        sut = MissingPetChipNumberViewModel(flowState: flowState)
        
        // Then
        XCTAssertEqual(sut.chipNumber, "12345-67890-12345")
    }
    
    func testHandleNext_whenChipNumberFilled_shouldSaveDigitsToFlowState() {
        // Given
        sut.chipNumber = "12345-6"
        
        // When
        sut.handleNext()
        
        // Then
        XCTAssertEqual(flowState.chipNumber, "123456")
    }
    
    func testHandleNext_whenChipNumberEmpty_shouldClearFlowState() {
        // Given
        sut.chipNumber = ""
        
        // When
        sut.handleNext()
        
        // Then
        XCTAssertNil(flowState.chipNumber)
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
    
    // MARK: - User Story 2: Backward Navigation Tests
    
    func testHandleBack_onStep1_shouldExitFlow() {
        // Given: onBack callback is set (exit flow on step 1)
        var exitFlowCalled = false
        sut.onBack = { exitFlowCalled = true }
        
        // When: handleBack() is called on step 1
        sut.handleBack()
        
        // Then: Flow should exit (callback triggered)
        XCTAssertTrue(exitFlowCalled, "Tapping back on step 1 should exit flow")
    }
}

