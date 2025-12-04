import XCTest
import UniformTypeIdentifiers
@testable import PetSpot

final class PhotoSelectionProcessorTests: XCTestCase {
    func testProcess_whenTransferableExists_shouldReturnSelection() async throws {
        // Given
        let data = Data([0x01, 0x02])
        let transferable = AnimalPhotoTransferable(
            data: data,
            contentType: .jpeg,
            fileName: "pet.jpg",
            pixelWidth: 640,
            pixelHeight: 480
        )
        let pickerItem = PhotoPickerItemTestDouble(itemIdentifier: "asset-123") {
            transferable
        }
        let processor = PhotoSelectionProcessor()
        
        // When
        let selection = try await processor.process(pickerItem)
        
        // Then
        XCTAssertEqual(selection.data, data)
        XCTAssertEqual(selection.fileName, "pet.jpg")
        XCTAssertEqual(selection.assetIdentifier, "asset-123")
        XCTAssertEqual(selection.pixelWidth, 640)
        XCTAssertEqual(selection.pixelHeight, 480)
    }
    
    func testProcess_whenFilenameMissing_shouldUseFallback() async throws {
        // Given
        let transferable = AnimalPhotoTransferable(
            data: Data(),
            contentType: .png,
            fileName: nil,
            pixelWidth: 100,
            pixelHeight: 200
        )
        let pickerItem = PhotoPickerItemTestDouble(itemIdentifier: nil) {
            transferable
        }
        let processor = PhotoSelectionProcessor()
        
        // When
        let selection = try await processor.process(pickerItem)
        
        // Then
        XCTAssertEqual(selection.fileName, "animal-photo.png")
    }
    
    func testProcess_whenTransferableMissing_shouldThrowError() async {
        // Given
        let pickerItem = PhotoPickerItemTestDouble(itemIdentifier: "missing") {
            nil
        }
        let processor = PhotoSelectionProcessor()
        
        // When / Then
        do {
            _ = try await processor.process(pickerItem)
            XCTFail("Expected processor to throw when transferable is missing")
        } catch let error as PhotoSelectionProcessorError {
            XCTAssertEqual(error, .emptyTransferable)
        } catch {
            XCTFail("Unexpected error type: \(error)")
        }
    }
}

private struct PhotoPickerItemTestDouble: PhotoPickerItemProviding {
    var itemIdentifier: String?
    var loadTransferableHandler: (() async throws -> Any?)?
    
    init(itemIdentifier: String? = nil, loadTransferableHandler: (() async throws -> Any?)? = nil) {
        self.itemIdentifier = itemIdentifier
        self.loadTransferableHandler = loadTransferableHandler
    }
    
    func loadTransferable<T>(type: T.Type) async throws -> T? {
        guard let handler = loadTransferableHandler else { return nil }
        let value = try await handler()
        return value as? T
    }
}

