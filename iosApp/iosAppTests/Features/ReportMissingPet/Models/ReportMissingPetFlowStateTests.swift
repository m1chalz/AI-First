import XCTest
import UniformTypeIdentifiers
@testable import PetSpot

@MainActor
final class ReportMissingPetFlowStateTests: XCTestCase {
    
    var sut: ReportMissingPetFlowState!
    var cache: PhotoAttachmentCacheFake!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        sut = ReportMissingPetFlowState(photoAttachmentCache: cache)
    }
    
    override func tearDown() {
        cache = nil
        sut = nil
        super.tearDown()
    }
    
    // MARK: - Initialization Tests
    
    func testInit_shouldInitializeAllPropertiesAsNil() {
        // Given/When: Fresh instance created in setUp
        
        // Then: All properties should be nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photoAttachment)
        XCTAssertEqual(sut.photoStatus, .empty)
        XCTAssertNil(sut.disappearanceDate)
        XCTAssertNil(sut.animalSpecies)
        XCTAssertNil(sut.animalRace)
        XCTAssertNil(sut.animalGender)
        XCTAssertNil(sut.animalAge)
        XCTAssertNil(sut.animalLatitude)
        XCTAssertNil(sut.animalLongitude)
        XCTAssertNil(sut.animalAdditionalDescription)
        XCTAssertNil(sut.contactDetails)
        XCTAssertNil(sut.managementPassword)
    }
    
    // MARK: - Management Password Tests
    
    func testManagementPassword_whenSetToValue_shouldRetainValue() {
        // Given: Flow state with management password
        let expectedPassword = "5216577"
        
        // When: Property is set
        sut.managementPassword = expectedPassword
        
        // Then: Password is retained
        XCTAssertEqual(sut.managementPassword, expectedPassword)
    }
    
    func testManagementPassword_whenSetToNil_shouldBeNil() {
        // Given: Flow state with password
        sut.managementPassword = "5216577"
        
        // When: Property is set to nil
        sut.managementPassword = nil
        
        // Then: Password is nil
        XCTAssertNil(sut.managementPassword)
    }
    
    // MARK: - clear() Method Tests
    
    func testClear_whenPropertiesHaveValues_shouldResetAllToNil() async {
        // Given: Flow state with populated properties
        sut.chipNumber = "123456789012345"
        sut.photoAttachment = PhotoAttachmentMetadata(
            id: UUID(),
            fileName: "pet.jpg",
            fileSizeBytes: 1_024,
            utiIdentifier: UTType.jpeg.identifier,
            pixelWidth: 100,
            pixelHeight: 100,
            assetIdentifier: "asset",
            cachedURL: URL(fileURLWithPath: "/tmp/asset"),
            savedAt: Date()
        )
        sut.photoStatus = .confirmed(metadata: sut.photoAttachment!)
        sut.disappearanceDate = Date()
        sut.animalSpecies = .dog
        sut.animalRace = "Labrador"
        sut.animalGender = .male
        sut.animalAge = 5
        sut.animalLatitude = 52.2297
        sut.animalLongitude = 21.0122
        sut.animalAdditionalDescription = "Test description"
        sut.contactDetails = OwnerContactDetails(
            phone: "123456789",
            email: "test@example.com",
            rewardDescription: nil
        )
        sut.managementPassword = "5216577"
        
        // When: clear() is called
        await sut.clear()
        
        // Then: All properties should be nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photoAttachment)
        XCTAssertEqual(sut.photoStatus, .empty)
        XCTAssertNil(sut.disappearanceDate)
        XCTAssertNil(sut.animalSpecies)
        XCTAssertNil(sut.animalRace)
        XCTAssertNil(sut.animalGender)
        XCTAssertNil(sut.animalAge)
        XCTAssertNil(sut.animalLatitude)
        XCTAssertNil(sut.animalLongitude)
        XCTAssertNil(sut.animalAdditionalDescription)
        XCTAssertNil(sut.contactDetails)
        XCTAssertNil(sut.managementPassword)
        XCTAssertEqual(cache.clearCallCount, 1)
    }
    
    func testClear_whenPropertiesAlreadyNil_shouldRemainNil() async {
        // Given: Fresh flow state (all properties nil from setUp)
        
        // When: clear() is called
        await sut.clear()
        
        // Then: All properties should remain nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photoAttachment)
        XCTAssertEqual(sut.photoStatus, .empty)
        XCTAssertNil(sut.disappearanceDate)
        XCTAssertNil(sut.animalSpecies)
        XCTAssertNil(sut.animalRace)
        XCTAssertNil(sut.animalGender)
        XCTAssertNil(sut.animalAge)
        XCTAssertNil(sut.animalLatitude)
        XCTAssertNil(sut.animalLongitude)
        XCTAssertNil(sut.animalAdditionalDescription)
        XCTAssertNil(sut.contactDetails)
        XCTAssertNil(sut.managementPassword)
        XCTAssertEqual(cache.clearCallCount, 1)
    }
}

