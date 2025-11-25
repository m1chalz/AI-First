import XCTest
@testable import PetSpot

/// Unit tests for EmptyStateView.Model
final class EmptyStateModelTests: XCTestCase {
    
    // MARK: - Tests
    
    func testInit_shouldSetMessage() {
        // Given
        let expectedMessage = "Test Message"
        
        // When
        let model = EmptyStateView.Model(message: expectedMessage)
        
        // Then
        XCTAssertEqual(model.message, expectedMessage)
    }
    
    func testDefault_shouldHaveCorrectMessage() {
        // Given
        let model = EmptyStateView.Model.default
        
        // Then
        XCTAssertFalse(model.message.isEmpty)
        // Note: We don't check exact localized string content to avoid brittle tests,
        // just ensuring it's not empty is sufficient for unit test
    }
    
    func testEquality_whenMessagesMatch_shouldBeEqual() {
        // Given
        let model1 = EmptyStateView.Model(message: "Message")
        let model2 = EmptyStateView.Model(message: "Message")
        
        // When + Then
        XCTAssertEqual(model1, model2)
    }
    
    func testEquality_whenMessagesDiffer_shouldNotBeEqual() {
        // Given
        let model1 = EmptyStateView.Model(message: "Message 1")
        let model2 = EmptyStateView.Model(message: "Message 2")
        
        // When + Then
        XCTAssertNotEqual(model1, model2)
    }
}


