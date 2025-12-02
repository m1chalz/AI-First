import XCTest
@testable import PetSpot

/**
 * Unit tests for AnimalMapper
 * Tests DTO → Domain model conversion
 * Follows Given-When-Then structure per constitution
 */
final class AnimalMapperTests: XCTestCase {
    
    var sut: AnimalMapper!
    var mockPhotoURLMapper: PhotoURLMapper!
    
    override func setUp() {
        super.setUp()
        mockPhotoURLMapper = PhotoURLMapper(baseURL: "http://localhost:3000")
        sut = AnimalMapper(photoURLMapper: mockPhotoURLMapper)
    }
    
    override func tearDown() {
        sut = nil
        mockPhotoURLMapper = nil
        super.tearDown()
    }
    
    // MARK: - Success Cases
    
    func test_map_whenValidDTO_shouldReturnAnimal() {
        // Given - valid DTO
        let dto = AnnouncementDTO(
            id: "test-id",
            petName: "Buddy",
            species: .dog,
            status: .missing,
            photoUrl: "/images/buddy.jpg",
            lastSeenDate: "2024-12-01",
            locationLatitude: 52.2297,
            locationLongitude: 21.0122,
            breed: "Golden Retriever",
            sex: .male,
            age: 5,
            description: "Friendly dog",
            phone: "+48123456789",
            email: "owner@example.com"
        )
        
        // When
        let animal = sut.map(dto)
        
        // Then
        XCTAssertNotNil(animal)
        XCTAssertEqual(animal?.id, "test-id")
        XCTAssertEqual(animal?.name, "Buddy")
        XCTAssertEqual(animal?.species, .dog)
        XCTAssertEqual(animal?.status, .active)
        XCTAssertEqual(animal?.photoUrl, "http://localhost:3000/images/buddy.jpg") // Resolved URL
        XCTAssertEqual(animal?.breed, "Golden Retriever")
        XCTAssertEqual(animal?.gender, .male)
        XCTAssertEqual(animal?.coordinate.latitude, 52.2297)
        XCTAssertEqual(animal?.coordinate.longitude, 21.0122)
    }
    
    func test_map_whenMISSINGStatus_shouldMapToACTIVE() {
        // Given - DTO with MISSING status
        let dto = makeTestDTO(status: .missing)
        
        // When
        let animal = sut.map(dto)
        
        // Then
        XCTAssertNotNil(animal)
        XCTAssertEqual(animal?.status, .active)
    }
    
    func test_map_whenNullSex_shouldDefaultToUnknown() {
        // Given - DTO with null sex
        let dto = makeTestDTO(sex: nil)
        
        // When
        let animal = sut.map(dto)
        
        // Then
        XCTAssertNotNil(animal)
        XCTAssertEqual(animal?.gender, .unknown)
    }
    
    func test_map_whenNullBreed_shouldBeNil() {
        // Given - DTO with null breed
        let dto = makeTestDTO(breed: nil)
        
        // When
        let animal = sut.map(dto)
        
        // Then
        XCTAssertNotNil(animal)
        XCTAssertNil(animal?.breed)
    }
    
    // MARK: - Failure Cases
    
    // MARK: - Helper Methods
    
    private func makeTestDTO(
        id: String = "test-id",
        species: AnimalSpeciesDTO = .dog,
        status: AnimalStatusDTO = .missing,
        photoUrl: String = "/images/test.jpg",
        breed: String? = "Test Breed",
        sex: AnimalGenderDTO? = .male
    ) -> AnnouncementDTO {
        return AnnouncementDTO(
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
            age: 5,
            description: "Test description",
            phone: "+48123456789",
            email: "test@example.com"
        )
    }
}

