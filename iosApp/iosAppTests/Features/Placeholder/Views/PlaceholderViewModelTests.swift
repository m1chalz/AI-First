import XCTest
@testable import PetSpot

/// Unit tests for PlaceholderViewModel.
/// Tests verify that the ViewModel correctly initializes with title and exposes localized message.
@MainActor
final class PlaceholderViewModelTests: XCTestCase {
    
    // MARK: - Test: ViewModel initializes with correct title (T009)
    
    func testInit_withTitle_shouldStoreTitle() {
        // Given
        let expectedTitle = "Test Title"
        
        // When
        let viewModel = PlaceholderViewModel(title: expectedTitle)
        
        // Then
        XCTAssertEqual(viewModel.title, expectedTitle)
    }
    
    // MARK: - Test: ViewModel exposes correct localized message (T010)
    
    func testMessage_shouldReturnLocalizedPlaceholderMessage() {
        // Given
        let viewModel = PlaceholderViewModel(title: "Any Title")
        
        // When
        let message = viewModel.message
        
        // Then
        XCTAssertEqual(message, L10n.Placeholder.message)
    }
    
    // MARK: - Test: ViewModel title can be different values
    
    func testInit_withDifferentTitles_shouldStoreCorrectTitle() {
        // Given
        let titles = ["Home", "Found Pet", "Contact Us", "Account"]
        
        for expectedTitle in titles {
            // When
            let viewModel = PlaceholderViewModel(title: expectedTitle)
            
            // Then
            XCTAssertEqual(viewModel.title, expectedTitle, "Title should be '\(expectedTitle)'")
        }
    }
}

