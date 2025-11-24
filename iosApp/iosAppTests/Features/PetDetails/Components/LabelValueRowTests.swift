import XCTest
@testable import iosApp

/// Unit tests for LabelValueRowModel
/// Tests model initialization and equality
final class LabelValueRowTests: XCTestCase {
    
    // MARK: - Tests
    
    func testInit_withLabelAndValue_shouldSetProperties() {
        // Given + When
        let model = LabelValueRowModel(
            label: "Species",
            value: "Dog"
        )
        
        // Then
        XCTAssertEqual(model.label, "Species")
        XCTAssertEqual(model.value, "Dog")
        XCTAssertNil(model.valueProcessor)
        XCTAssertNil(model.onTap)
    }
    
    func testInit_withValueProcessor_shouldSetProcessor() {
        // Given
        let processor: (String) -> String = { value in
            return value.uppercased()
        }
        
        // When
        let model = LabelValueRowModel(
            label: "Name",
            value: "fluffy",
            valueProcessor: processor
        )
        
        // Then
        XCTAssertEqual(model.label, "Name")
        XCTAssertEqual(model.value, "fluffy")
        XCTAssertNotNil(model.valueProcessor)
        
        // Test processor functionality
        let processed = model.valueProcessor?("test")
        XCTAssertEqual(processed, "TEST")
    }
    
    func testInit_withOnTap_shouldSetTapHandler() {
        // Given
        var tapCalled = false
        let tapHandler: () -> Void = {
            tapCalled = true
        }
        
        // When
        let model = LabelValueRowModel(
            label: "Phone",
            value: "+48 123 456 789",
            onTap: tapHandler
        )
        
        // Then
        XCTAssertEqual(model.label, "Phone")
        XCTAssertEqual(model.value, "+48 123 456 789")
        XCTAssertNotNil(model.onTap)
        
        // Test tap handler functionality
        model.onTap?()
        XCTAssertTrue(tapCalled)
    }
    
    func testEquality_whenLabelAndValueMatch_shouldBeEqual() {
        // Given
        let model1 = LabelValueRowModel(label: "Species", value: "Dog")
        let model2 = LabelValueRowModel(label: "Species", value: "Dog")
        
        // When + Then
        XCTAssertEqual(model1, model2)
    }
    
    func testEquality_whenLabelDiffers_shouldNotBeEqual() {
        // Given
        let model1 = LabelValueRowModel(label: "Species", value: "Dog")
        let model2 = LabelValueRowModel(label: "Breed", value: "Dog")
        
        // When + Then
        XCTAssertNotEqual(model1, model2)
    }
    
    func testEquality_whenValueDiffers_shouldNotBeEqual() {
        // Given
        let model1 = LabelValueRowModel(label: "Species", value: "Dog")
        let model2 = LabelValueRowModel(label: "Species", value: "Cat")
        
        // When + Then
        XCTAssertNotEqual(model1, model2)
    }
    
    func testEquality_shouldIgnoreClosures() {
        // Given
        let model1 = LabelValueRowModel(
            label: "Phone",
            value: "+48 123 456 789",
            valueProcessor: { $0.uppercased() },
            onTap: { print("tap1") }
        )
        let model2 = LabelValueRowModel(
            label: "Phone",
            value: "+48 123 456 789",
            valueProcessor: { $0.lowercased() }, // Different processor
            onTap: { print("tap2") } // Different tap handler
        )
        
        // When + Then
        // Models should be equal because equality ignores closures
        XCTAssertEqual(model1, model2)
    }
    
    func testEquality_withNilValueProcessor_shouldBeEqual() {
        // Given
        let model1 = LabelValueRowModel(
            label: "Location",
            value: "Warsaw",
            valueProcessor: nil
        )
        let model2 = LabelValueRowModel(
            label: "Location",
            value: "Warsaw",
            valueProcessor: nil
        )
        
        // When + Then
        XCTAssertEqual(model1, model2)
    }
}

