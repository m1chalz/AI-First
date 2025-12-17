import XCTest
@testable import iosApp

/// Unit tests for HeroPanelView.Model generic presentation model.
/// Feature-specific factory tests are in LandingPage feature directory.
final class HeroPanelViewModelTests: XCTestCase {
    
    // MARK: - Generic Initialization Tests
    
    func testInit_withCustomValues_shouldSetAllProperties() {
        // Given
        let leftButton = FloatingActionButtonModel(title: "Left", style: .secondary)
        let rightButton = FloatingActionButtonModel(title: "Right", style: .primary)
        
        // When
        let model = HeroPanelView.Model(
            title: "Custom Title",
            leftButton: leftButton,
            rightButton: rightButton,
            titleAccessibilityId: "custom.title",
            leftButtonAccessibilityId: "custom.left",
            rightButtonAccessibilityId: "custom.right"
        )
        
        // Then
        XCTAssertEqual(model.title, "Custom Title")
        XCTAssertEqual(model.leftButton.title, "Left")
        XCTAssertEqual(model.rightButton.title, "Right")
        XCTAssertEqual(model.titleAccessibilityId, "custom.title")
        XCTAssertEqual(model.leftButtonAccessibilityId, "custom.left")
        XCTAssertEqual(model.rightButtonAccessibilityId, "custom.right")
    }
    
    func testInit_withButtonStyles_shouldPreserveStyles() {
        // Given
        let leftButton = FloatingActionButtonModel(
            title: "Alert",
            style: .secondary,
            iconSource: .sfSymbol("exclamationmark.triangle")
        )
        let rightButton = FloatingActionButtonModel(
            title: "Success",
            style: .primary,
            iconSource: .sfSymbol("checkmark")
        )
        
        // When
        let model = HeroPanelView.Model(
            title: "Actions",
            leftButton: leftButton,
            rightButton: rightButton,
            titleAccessibilityId: "test.title",
            leftButtonAccessibilityId: "test.left",
            rightButtonAccessibilityId: "test.right"
        )
        
        // Then
        XCTAssertEqual(model.leftButton.style, .secondary)
        XCTAssertEqual(model.rightButton.style, .primary)
    }
    
    // MARK: - Closure Invocation Tests
    
    func testOnLeftButtonTap_whenInvoked_shouldCallClosure() {
        // Given
        var leftTapCalled = false
        let model = HeroPanelView.Model(
            title: "Test",
            leftButton: FloatingActionButtonModel(title: "Left", style: .secondary),
            rightButton: FloatingActionButtonModel(title: "Right", style: .primary),
            onLeftButtonTap: { leftTapCalled = true },
            onRightButtonTap: {},
            titleAccessibilityId: "test.title",
            leftButtonAccessibilityId: "test.left",
            rightButtonAccessibilityId: "test.right"
        )
        
        // When
        model.onLeftButtonTap()
        
        // Then
        XCTAssertTrue(leftTapCalled)
    }
    
    func testOnRightButtonTap_whenInvoked_shouldCallClosure() {
        // Given
        var rightTapCalled = false
        let model = HeroPanelView.Model(
            title: "Test",
            leftButton: FloatingActionButtonModel(title: "Left", style: .secondary),
            rightButton: FloatingActionButtonModel(title: "Right", style: .primary),
            onLeftButtonTap: {},
            onRightButtonTap: { rightTapCalled = true },
            titleAccessibilityId: "test.title",
            leftButtonAccessibilityId: "test.left",
            rightButtonAccessibilityId: "test.right"
        )
        
        // When
        model.onRightButtonTap()
        
        // Then
        XCTAssertTrue(rightTapCalled)
    }
    
    func testDefaultClosures_shouldNotCrash() {
        // Given
        let model = HeroPanelView.Model(
            title: "Test",
            leftButton: FloatingActionButtonModel(title: "Left", style: .secondary),
            rightButton: FloatingActionButtonModel(title: "Right", style: .primary),
            titleAccessibilityId: "test.title",
            leftButtonAccessibilityId: "test.left",
            rightButtonAccessibilityId: "test.right"
        )
        
        // When / Then - should not crash
        model.onLeftButtonTap()
        model.onRightButtonTap()
    }
}

