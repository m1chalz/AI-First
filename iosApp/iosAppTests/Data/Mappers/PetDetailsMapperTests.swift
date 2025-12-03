import XCTest
@testable import PetSpot

/**
 * Unit tests for PetDetailsMapper
 * Tests DTO → Domain model conversion
 * Follows Given-When-Then structure per constitution
 */
final class PetDetailsMapperTests: XCTestCase {
    
    var sut: PetDetailsMapper!
    var mockPhotoURLMapper: PhotoURLMapper!
    
    override func setUp() {
        super.setUp()
        mockPhotoURLMapper = PhotoURLMapper(baseURL: "http://localhost:3000")
        sut = PetDetailsMapper(photoURLMapper: mockPhotoURLMapper)
    }
    
    override func tearDown() {
        sut = nil
        mockPhotoURLMapper = nil
        super.tearDown()
    }
    
    // MARK: - Success Cases
    
    func test_map_whenValidDTO_shouldReturnPetDetails() {
        // Given - valid DTO with all fields
        let dto = PetDetailsDTO(
            id: "test-id",
            petName: "Max",
            species: .dog,
            status: .missing,
            photoUrl: "/images/max.jpg",
            lastSeenDate: "2024-12-01",
            locationLatitude: 52.2297,
            locationLongitude: 21.0122,
            breed: "Labrador",
            sex: .male,
            age: 3,
            microchipNumber: "123456789012345",
            email: "owner@example.com",
            phone: "+48123456789",
            reward: "500 PLN",
            description: "Friendly dog",
            createdAt: "2024-11-15T10:30:00Z",
            updatedAt: "2024-11-20T14:45:00Z"
        )
        
        // When
        let petDetails = sut.map(dto)
        
        // Then
        XCTAssertNotNil(petDetails)
        XCTAssertEqual(petDetails?.id, "test-id")
        XCTAssertEqual(petDetails?.petName, "Max")
        XCTAssertEqual(petDetails?.species, .dog)
        XCTAssertEqual(petDetails?.status, .active)
        XCTAssertEqual(petDetails?.photoUrl, "http://localhost:3000/images/max.jpg") // Resolved
        XCTAssertEqual(petDetails?.breed, "Labrador")
        XCTAssertEqual(petDetails?.gender, .male)
        XCTAssertEqual(petDetails?.approximateAge, 3)
        XCTAssertEqual(petDetails?.phone, "+48123456789")
    }
    
    func test_map_whenMISSINGStatus_shouldMapToACTIVE() {
        // Given - DTO with MISSING status
        let dto = makeTestDTO(status: .missing)
        
        // When
        let petDetails = sut.map(dto)
        
        // Then
        XCTAssertNotNil(petDetails)
        XCTAssertEqual(petDetails?.status, .active)
    }
    
    func test_map_whenNullSex_shouldDefaultToUnknown() {
        // Given - DTO with null sex
        let dto = makeTestDTO(sex: nil)
        
        // When
        let petDetails = sut.map(dto)
        
        // Then
        XCTAssertNotNil(petDetails)
        XCTAssertEqual(petDetails?.gender, .unknown)
    }
    
    func test_map_whenNullOptionalFields_shouldHandleGracefully() {
        // Given - DTO with null optional fields
        let dto = makeTestDTO(
            breed: nil,
            age: nil,
            microchipNumber: nil,
            email: nil,
            reward: nil
        )
        
        // When
        let petDetails = sut.map(dto)
        
        // Then
        XCTAssertNotNil(petDetails)
        XCTAssertNil(petDetails?.breed)
        XCTAssertNil(petDetails?.approximateAge)
        XCTAssertNil(petDetails?.microchipNumber)
        XCTAssertNil(petDetails?.email)
        XCTAssertNil(petDetails?.reward)
        XCTAssertNotNil(petDetails?.phone) // phone is required
    }
    
    func test_map_whenNullDescription_shouldDefaultToEmptyString() {
        // Given - DTO with null description
        let dto = makeTestDTO(description: nil)
        
        // When
        let petDetails = sut.map(dto)
        
        // Then
        XCTAssertNotNil(petDetails)
        XCTAssertEqual(petDetails?.description, "")
    }
    
    // MARK: - Failure Cases
    
    // MARK: - Helper Methods
    
    private func makeTestDTO(
        id: String = "test-id",
        species: AnimalSpeciesDTO = .cat,
        status: AnnouncementStatusDTO = .found,
        photoUrl: String = "/images/test.jpg",
        breed: String? = "Persian",
        sex: AnimalGenderDTO? = .female,
        age: Int? = 2,
        microchipNumber: String? = "123456789012345",
        email: String? = "test@example.com",
        reward: String? = "100 PLN",
        description: String? = "Test description"
    ) -> PetDetailsDTO {
        return PetDetailsDTO(
            id: id,
            petName: "TestPet",
            species: species,
            status: status,
            photoUrl: photoUrl,
            lastSeenDate: "2024-12-01",
            locationLatitude: 52.2297,
            locationLongitude: 21.0122,
            breed: breed,
            sex: sex,
            age: age,
            microchipNumber: microchipNumber,
            email: email,
            phone: "+48123456789",
            reward: reward,
            description: description,
            createdAt: "2024-11-15T10:30:00Z",
            updatedAt: "2024-11-20T14:45:00Z"
        )
    }
}

