import XCTest
@testable import PetSpot

/// Unit tests for HeroPanelView.Model.landingPage() factory.
/// Tests Landing Page specific configuration.
final class HeroPanelView_Model_LandingPageTests: XCTestCase {
    
    // MARK: - Landing Page Factory Tests
    
    func testLandingPage_shouldSetLocalizedTitle() {
        // Given / When
        let model = HeroPanelView.Model.landingPage()
        
        // Then
        XCTAssertEqual(model.title, L10n.LandingPage.Hero.title)
    }
    
    func testLandingPage_shouldSetLocalizedButtonTitles() {
        // Given / When
        let model = HeroPanelView.Model.landingPage()
        
        // Then
        XCTAssertEqual(model.leftButton.title, L10n.LandingPage.Hero.lostPetButton)
        XCTAssertEqual(model.rightButton.title, L10n.LandingPage.Hero.foundPetButton)
    }
    
    func testLandingPage_shouldSetCorrectButtonStyles() {
        // Given / When
        let model = HeroPanelView.Model.landingPage()
        
        // Then
        XCTAssertEqual(model.leftButton.style, .secondary, "Lost Pet should be secondary (red)")
        XCTAssertEqual(model.rightButton.style, .primary, "Found Pet should be primary (blue)")
    }
    
    func testLandingPage_shouldSetAccessibilityIds() {
        // Given / When
        let model = HeroPanelView.Model.landingPage()
        
        // Then
        XCTAssertEqual(model.titleAccessibilityId, "home.hero.title")
        XCTAssertEqual(model.leftButtonAccessibilityId, "home.hero.lostPetButton")
        XCTAssertEqual(model.rightButtonAccessibilityId, "home.hero.foundPetButton")
    }
    
    func testLandingPage_onLostPetTap_shouldInvokeLeftButtonClosure() {
        // Given
        var lostPetTapped = false
        let model = HeroPanelView.Model.landingPage(
            onLostPetTap: { lostPetTapped = true }
        )
        
        // When
        model.onLeftButtonTap()
        
        // Then
        XCTAssertTrue(lostPetTapped, "Lost Pet closure should be called on left button tap")
    }
    
    func testLandingPage_onFoundPetTap_shouldInvokeRightButtonClosure() {
        // Given
        var foundPetTapped = false
        let model = HeroPanelView.Model.landingPage(
            onFoundPetTap: { foundPetTapped = true }
        )
        
        // When
        model.onRightButtonTap()
        
        // Then
        XCTAssertTrue(foundPetTapped, "Found Pet closure should be called on right button tap")
    }
    
    func testLandingPage_defaultClosures_shouldNotCrash() {
        // Given
        let model = HeroPanelView.Model.landingPage()
        
        // When / Then - should not crash
        model.onLeftButtonTap()
        model.onRightButtonTap()
    }
    
    func testLandingPage_leftButton_shouldHaveAlertIcon() {
        // Given / When
        let model = HeroPanelView.Model.landingPage()
        
        // Then
        if case .sfSymbol(let name) = model.leftButton.iconSource {
            XCTAssertEqual(name, "exclamationmark.triangle")
        } else {
            XCTFail("Left button should have SF Symbol icon")
        }
    }
    
    func testLandingPage_rightButton_shouldHaveCheckmarkIcon() {
        // Given / When
        let model = HeroPanelView.Model.landingPage()
        
        // Then
        if case .sfSymbol(let name) = model.rightButton.iconSource {
            XCTAssertEqual(name, "checkmark")
        } else {
            XCTFail("Right button should have SF Symbol icon")
        }
    }
}
