import XCTest
@testable import iosApp

/// Unit tests for PetPhotoWithBadgesModel
/// Tests status mapping and model initialization
final class PetPhotoWithBadgesTests: XCTestCase {
    
    // MARK: - Helper Methods
    
    private func makeMockPetDetails(
        id: String = "test-id",
        photoUrl: String? = "https://example.com/photo.jpg",
        status: String = "ACTIVE",
        reward: String? = nil
    ) -> PetDetails {
        return PetDetails(
            id: id,
            petName: "Test Pet",
            photoUrl: photoUrl,
            status: status,
            lastSeenDate: "2025-11-20",
            species: "DOG",
            gender: "MALE",
            description: "Test description",
            location: "Test City",
            phone: "+48 123 456 789",
            email: "test@example.com",
            breed: "Test Breed",
            locationRadius: 5,
            microchipNumber: "123-456-789",
            approximateAge: "2 years",
            reward: reward,
            createdAt: "2025-11-20T10:00:00.000Z",
            updatedAt: "2025-11-20T10:00:00.000Z"
        )
    }
    
    // MARK: - Tests
    
    func testInit_whenStatusIsActive_shouldMapToMissing() {
        // Given
        let petDetails = makeMockPetDetails(status: "ACTIVE")
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertEqual(model.status, "MISSING")
    }
    
    func testInit_whenStatusIsFound_shouldKeepFound() {
        // Given
        let petDetails = makeMockPetDetails(status: "FOUND")
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertEqual(model.status, "FOUND")
    }
    
    func testInit_whenStatusIsClosed_shouldKeepClosed() {
        // Given
        let petDetails = makeMockPetDetails(status: "CLOSED")
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertEqual(model.status, "CLOSED")
    }
    
    func testInit_whenPhotoUrlIsPresent_shouldSetImageUrl() {
        // Given
        let photoUrl = "https://example.com/test-photo.jpg"
        let petDetails = makeMockPetDetails(photoUrl: photoUrl)
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertEqual(model.imageUrl, photoUrl)
    }
    
    func testInit_whenPhotoUrlIsNil_shouldSetImageUrlToNil() {
        // Given
        let petDetails = makeMockPetDetails(photoUrl: nil)
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertNil(model.imageUrl)
    }
    
    func testInit_whenRewardIsPresent_shouldSetRewardText() {
        // Given
        let reward = "$500 reward"
        let petDetails = makeMockPetDetails(reward: reward)
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertEqual(model.rewardText, reward)
    }
    
    func testInit_whenRewardIsNil_shouldSetRewardTextToNil() {
        // Given
        let petDetails = makeMockPetDetails(reward: nil)
        
        // When
        let model = PetPhotoWithBadgesModel(from: petDetails)
        
        // Then
        XCTAssertNil(model.rewardText)
    }
    
    func testEquality_whenAllFieldsMatch_shouldBeEqual() {
        // Given
        let model1 = PetPhotoWithBadgesModel(
            imageUrl: "https://example.com/photo.jpg",
            status: "MISSING",
            rewardText: "$500"
        )
        let model2 = PetPhotoWithBadgesModel(
            imageUrl: "https://example.com/photo.jpg",
            status: "MISSING",
            rewardText: "$500"
        )
        
        // When + Then
        XCTAssertEqual(model1, model2)
    }
    
    func testEquality_whenStatusDiffers_shouldNotBeEqual() {
        // Given
        let model1 = PetPhotoWithBadgesModel(
            imageUrl: "https://example.com/photo.jpg",
            status: "MISSING",
            rewardText: nil
        )
        let model2 = PetPhotoWithBadgesModel(
            imageUrl: "https://example.com/photo.jpg",
            status: "FOUND",
            rewardText: nil
        )
        
        // When + Then
        XCTAssertNotEqual(model1, model2)
    }
}

