import XCTest
@testable import PetSpot

/// Unit tests for CreateAnnouncementMapper (KAN-34)
/// Tests domain model → DTO conversion, specifically status mapping
final class CreateAnnouncementMapperTests: XCTestCase {
    
    var sut: CreateAnnouncementMapper!
    
    override func setUp() {
        super.setUp()
        sut = CreateAnnouncementMapper()
    }
    
    override func tearDown() {
        sut = nil
        super.tearDown()
    }
    
    // MARK: - Status Mapping Tests (KAN-34 User Story 1)
    
    func testToDTO_whenStatusIsActive_shouldMapToMissing() {
        // Given: CreateAnnouncementData with .active status (Missing flow)
        let data = makeTestData(status: .active)
        
        // When: Converting to DTO
        let dto = sut.toDTO(data)
        
        // Then: DTO status should be .missing (maps to "MISSING" in JSON)
        XCTAssertEqual(dto.status, .missing,
                       "iOS .active status should map to DTO .missing (backend MISSING)")
    }
    
    func testToDTO_whenStatusIsActive_shouldEncodeAsMISSING() throws {
        // Given: CreateAnnouncementData with .active status
        let data = makeTestData(status: .active)
        
        // When: Converting to DTO and encoding to JSON
        let dto = sut.toDTO(data)
        let encoder = JSONEncoder()
        let jsonData = try encoder.encode(dto)
        let jsonString = String(data: jsonData, encoding: .utf8)!
        
        // Then: JSON should contain "status":"MISSING"
        XCTAssertTrue(jsonString.contains("\"status\":\"MISSING\""),
                      "JSON should contain status: MISSING, got: \(jsonString)")
    }
    
    // MARK: - Status Mapping Tests (KAN-34 User Story 2)
    
    func testToDTO_whenStatusIsFound_shouldMapToFound() {
        // Given: CreateAnnouncementData with .found status (Found flow)
        let data = makeTestData(status: .found)
        
        // When: Converting to DTO
        let dto = sut.toDTO(data)
        
        // Then: DTO status should be .found (maps to "FOUND" in JSON)
        XCTAssertEqual(dto.status, .found,
                       "iOS .found status should map to DTO .found (backend FOUND)")
    }
    
    func testToDTO_whenStatusIsFound_shouldEncodeAsFOUND() throws {
        // Given: CreateAnnouncementData with .found status
        let data = makeTestData(status: .found)
        
        // When: Converting to DTO and encoding to JSON
        let dto = sut.toDTO(data)
        let encoder = JSONEncoder()
        let jsonData = try encoder.encode(dto)
        let jsonString = String(data: jsonData, encoding: .utf8)!
        
        // Then: JSON should contain "status":"FOUND"
        XCTAssertTrue(jsonString.contains("\"status\":\"FOUND\""),
                      "JSON should contain status: FOUND, got: \(jsonString)")
    }
    
    // MARK: - Payload Shape Test (KAN-34 User Story 2)
    
    func testToDTO_whenEncoded_shouldContainOnlyBackendSchemaKeys() throws {
        // Given: CreateAnnouncementData with all fields populated
        let data = makeTestData(status: .found)
        
        // When: Converting to DTO and encoding to JSON
        let dto = sut.toDTO(data)
        let encoder = JSONEncoder()
        let jsonData = try encoder.encode(dto)
        let jsonObject = try JSONSerialization.jsonObject(with: jsonData) as! [String: Any]
        let actualKeys = Set(jsonObject.keys)
        
        // Then: JSON keys should match backend schema exactly
        // Backend schema (from server/src/lib/announcement-validation.ts):
        // Required: species, sex, lastSeenDate, status, locationLatitude, locationLongitude, email/phone
        // Optional: petName, breed, age, description, microchipNumber, reward
        let expectedKeys: Set<String> = [
            "species",
            "breed",
            "sex",
            "age",
            "lastSeenDate",
            "locationLatitude",
            "locationLongitude",
            "email",
            "phone",
            "status",
            "microchipNumber",
            "petName",
            "description",
            "reward"
        ]
        
        XCTAssertEqual(actualKeys, expectedKeys,
                       "DTO should contain only backend schema keys. Extra: \(actualKeys.subtracting(expectedKeys)), Missing: \(expectedKeys.subtracting(actualKeys))")
    }
    
    func testToDTO_whenFoundFlow_shouldNotContainIOSOnlyFields() throws {
        // Given: CreateAnnouncementData from Found flow
        let data = makeTestData(status: .found)
        
        // When: Converting to DTO and encoding to JSON
        let dto = sut.toDTO(data)
        let encoder = JSONEncoder()
        let jsonData = try encoder.encode(dto)
        let jsonString = String(data: jsonData, encoding: .utf8)!
        
        // Then: JSON should NOT contain iOS-only fields (per FR-016)
        // iOS-only fields that should NOT be sent: caregiverPhoneNumber, currentPhysicalAddress
        XCTAssertFalse(jsonString.contains("caregiverPhoneNumber"),
                       "iOS-only field 'caregiverPhoneNumber' should not be in JSON payload")
        XCTAssertFalse(jsonString.contains("currentPhysicalAddress"),
                       "iOS-only field 'currentPhysicalAddress' should not be in JSON payload")
    }
    
    // MARK: - Helper Methods
    
    private func makeTestData(status: AnnouncementStatus) -> CreateAnnouncementData {
        CreateAnnouncementData(
            species: .dog,
            breed: "Labrador",
            sex: .male,
            age: 5,
            lastSeenDate: Date(timeIntervalSince1970: 1701388800), // 2023-12-01
            location: (latitude: 52.2297, longitude: 21.0122),
            contact: (email: "owner@example.com", phone: "+48123456789"),
            microchipNumber: "123456789012345",
            petName: "Max",
            description: "Friendly golden retriever",
            reward: "$500 reward",
            status: status
        )
    }
}

