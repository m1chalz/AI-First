import XCTest
@testable import PetSpot

/// Unit tests for PetPhotoWithBadgesView.Model
/// Tests model initialization, status display text conversion, and equality
final class PetPhotoWithBadgesViewTests: XCTestCase {
    
    // MARK: - Tests
    
    func testInit_whenPhotoUrlIsPresent_shouldSetImageUrl() {
        // Given
        let photoUrl = "https://example.com/test-photo.jpg"
        
        // When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: photoUrl,
            status: .active,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.imageUrl, photoUrl)
    }
    
    func testInit_whenPhotoUrlIsNil_shouldSetImageUrlToNil() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .active,
            rewardText: nil
        )
        
        // Then
        XCTAssertNil(model.imageUrl)
    }
    
    func testStatusDisplayText_whenStatusIsActive_shouldReturnMissing() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .active,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.statusDisplayText, L10n.AnnouncementStatus.active)
    }
    
    func testStatusDisplayText_whenStatusIsFound_shouldReturnFound() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .found,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.statusDisplayText, L10n.AnnouncementStatus.found)
    }
    
    func testStatusDisplayText_whenStatusIsClosed_shouldReturnClosed() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .closed,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.statusDisplayText, L10n.AnnouncementStatus.closed)
    }
    
    func testStatusBadgeColorHex_whenStatusIsActive_shouldReturnRedHex() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .active,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.statusBadgeColorHex, "#FF0000")
    }
    
    func testStatusBadgeColorHex_whenStatusIsFound_shouldReturnGreenHex() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .found,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.statusBadgeColorHex, "#00FF00")
    }
    
    func testStatusBadgeColorHex_whenStatusIsClosed_shouldReturnGrayHex() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .closed,
            rewardText: nil
        )
        
        // Then
        XCTAssertEqual(model.statusBadgeColorHex, "#808080")
    }
    
    func testInit_whenRewardIsPresent_shouldSetRewardText() {
        // Given
        let reward = "$500 reward"
        
        // When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .active,
            rewardText: reward
        )
        
        // Then
        XCTAssertEqual(model.rewardText, reward)
    }
    
    func testInit_whenRewardIsNil_shouldSetRewardTextToNil() {
        // Given + When
        let model = PetPhotoWithBadgesView.Model(
            imageUrl: nil,
            status: .active,
            rewardText: nil
        )
        
        // Then
        XCTAssertNil(model.rewardText)
    }
    
    func testEquality_whenAllFieldsMatch_shouldBeEqual() {
        // Given
        let model1 = PetPhotoWithBadgesView.Model(
            imageUrl: "https://example.com/photo.jpg",
            status: .active,
            rewardText: "$500"
        )
        let model2 = PetPhotoWithBadgesView.Model(
            imageUrl: "https://example.com/photo.jpg",
            status: .active,
            rewardText: "$500"
        )
        
        // When + Then
        XCTAssertEqual(model1, model2)
    }
    
    func testEquality_whenStatusDiffers_shouldNotBeEqual() {
        // Given
        let model1 = PetPhotoWithBadgesView.Model(
            imageUrl: "https://example.com/photo.jpg",
            status: .active,
            rewardText: nil
        )
        let model2 = PetPhotoWithBadgesView.Model(
            imageUrl: "https://example.com/photo.jpg",
            status: .found,
            rewardText: nil
        )
        
        // When + Then
        XCTAssertNotEqual(model1, model2)
    }
}

