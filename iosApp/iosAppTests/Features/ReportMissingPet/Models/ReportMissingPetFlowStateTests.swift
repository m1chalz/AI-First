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
        XCTAssertNil(sut.description)
        XCTAssertNil(sut.contactEmail)
        XCTAssertNil(sut.contactPhone)
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
        sut.description = "Test description"
        sut.contactEmail = "test@example.com"
        sut.contactPhone = "123456789"
        
        // When: clear() is called
        await sut.clear()
        
        // Then: All properties should be nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photoAttachment)
        XCTAssertEqual(sut.photoStatus, .empty)
        XCTAssertNil(sut.description)
        XCTAssertNil(sut.contactEmail)
        XCTAssertNil(sut.contactPhone)
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
        XCTAssertNil(sut.description)
        XCTAssertNil(sut.contactEmail)
        XCTAssertNil(sut.contactPhone)
        XCTAssertEqual(cache.clearCallCount, 1)
    }
}

