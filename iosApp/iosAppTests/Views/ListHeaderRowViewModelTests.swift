import XCTest
@testable import iosApp

/// Unit tests for ListHeaderRowView.Model generic presentation model.
/// Feature-specific factory tests are in LandingPage feature directory.
final class ListHeaderRowViewModelTests: XCTestCase {
    
    // MARK: - Generic Initialization Tests
    
    func testInit_withCustomValues_shouldSetAllProperties() {
        // Given / When
        let model = ListHeaderRowView.Model(
            title: "Custom Section",
            actionTitle: "See More",
            titleAccessibilityId: "custom.title",
            actionAccessibilityId: "custom.action"
        )
        
        // Then
        XCTAssertEqual(model.title, "Custom Section")
        XCTAssertEqual(model.actionTitle, "See More")
        XCTAssertEqual(model.titleAccessibilityId, "custom.title")
        XCTAssertEqual(model.actionAccessibilityId, "custom.action")
    }
    
    // MARK: - Closure Invocation Tests
    
    func testOnActionTap_whenInvoked_shouldCallClosure() {
        // Given
        var actionTapCalled = false
        let model = ListHeaderRowView.Model(
            title: "Test",
            actionTitle: "Action",
            onActionTap: { actionTapCalled = true },
            titleAccessibilityId: "test.title",
            actionAccessibilityId: "test.action"
        )
        
        // When
        model.onActionTap()
        
        // Then
        XCTAssertTrue(actionTapCalled)
    }
    
    func testDefaultClosure_shouldNotCrash() {
        // Given
        let model = ListHeaderRowView.Model(
            title: "Test",
            actionTitle: "Action",
            titleAccessibilityId: "test.title",
            actionAccessibilityId: "test.action"
        )
        
        // When / Then - should not crash
        model.onActionTap()
    }
}

