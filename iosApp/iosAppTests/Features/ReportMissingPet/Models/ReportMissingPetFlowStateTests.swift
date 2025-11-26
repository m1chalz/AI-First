import XCTest
@testable import PetSpot

@MainActor
final class ReportMissingPetFlowStateTests: XCTestCase {
    
    var sut: ReportMissingPetFlowState!
    
    override func setUp() {
        super.setUp()
        sut = ReportMissingPetFlowState()
    }
    
    override func tearDown() {
        sut = nil
        super.tearDown()
    }
    
    // MARK: - Initialization Tests
    
    func testInit_shouldInitializeAllPropertiesAsNil() {
        // Given/When: Fresh instance created in setUp
        
        // Then: All properties should be nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photo)
        XCTAssertNil(sut.description)
        XCTAssertNil(sut.contactEmail)
        XCTAssertNil(sut.contactPhone)
    }
    
    // MARK: - clear() Method Tests
    
    func testClear_whenPropertiesHaveValues_shouldResetAllToNil() {
        // Given: Flow state with populated properties
        sut.chipNumber = "123456789012345"
        sut.photo = UIImage() // Create dummy image
        sut.description = "Test description"
        sut.contactEmail = "test@example.com"
        sut.contactPhone = "123456789"
        
        // When: clear() is called
        sut.clear()
        
        // Then: All properties should be nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photo)
        XCTAssertNil(sut.description)
        XCTAssertNil(sut.contactEmail)
        XCTAssertNil(sut.contactPhone)
    }
    
    func testClear_whenPropertiesAlreadyNil_shouldRemainNil() {
        // Given: Fresh flow state (all properties nil from setUp)
        
        // When: clear() is called
        sut.clear()
        
        // Then: All properties should remain nil
        XCTAssertNil(sut.chipNumber)
        XCTAssertNil(sut.photo)
        XCTAssertNil(sut.description)
        XCTAssertNil(sut.contactEmail)
        XCTAssertNil(sut.contactPhone)
    }
}

