import XCTest
@testable import PetSpot

/// Unit tests for ListHeaderRowView.Model.recentReports() factory.
/// Tests Landing Page specific configuration.
final class ListHeaderRowView_Model_LandingPageTests: XCTestCase {
    
    // MARK: - Landing Page Factory Tests
    
    func testRecentReports_shouldSetLocalizedTitle() {
        // Given / When
        let model = ListHeaderRowView.Model.recentReports()
        
        // Then
        XCTAssertEqual(model.title, L10n.LandingPage.ListHeader.title)
    }
    
    func testRecentReports_shouldSetLocalizedActionTitle() {
        // Given / When
        let model = ListHeaderRowView.Model.recentReports()
        
        // Then
        XCTAssertEqual(model.actionTitle, L10n.LandingPage.ListHeader.viewAll)
    }
    
    func testRecentReports_shouldSetAccessibilityIds() {
        // Given / When
        let model = ListHeaderRowView.Model.recentReports()
        
        // Then
        XCTAssertEqual(model.titleAccessibilityId, "home.recentReports.title")
        XCTAssertEqual(model.actionAccessibilityId, "home.recentReports.viewAll")
    }
    
    func testRecentReports_onViewAllTap_shouldInvokeClosure() {
        // Given
        var viewAllTapped = false
        let model = ListHeaderRowView.Model.recentReports(
            onViewAllTap: { viewAllTapped = true }
        )
        
        // When
        model.onActionTap()
        
        // Then
        XCTAssertTrue(viewAllTapped, "View All closure should be called on action tap")
    }
    
    func testRecentReports_defaultClosure_shouldNotCrash() {
        // Given
        let model = ListHeaderRowView.Model.recentReports()
        
        // When / Then - should not crash
        model.onActionTap()
    }
}

