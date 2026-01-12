import XCTest
import UniformTypeIdentifiers
@testable import PetSpot

final class PhotoAttachmentCacheTests: XCTestCase {
    
    var fileManager: FileManager!
    var temporaryDirectory: URL!
    var sut: PhotoAttachmentCache!
    
    override func setUp() {
        super.setUp()
        fileManager = FileManager()
        temporaryDirectory = FileManager.default.temporaryDirectory
            .appendingPathComponent(UUID().uuidString, isDirectory: true)
        sut = PhotoAttachmentCache(fileManager: fileManager, baseDirectory: temporaryDirectory)
    }
    
    override func tearDown() {
        try? fileManager.removeItem(at: temporaryDirectory)
        sut = nil
        temporaryDirectory = nil
        fileManager = nil
        super.tearDown()
    }
    
    func testSave_shouldPersistFileAndMetadata() async throws {
        // Given
        let metadata = makeMetadata()
        let data = Data(repeating: 1, count: 1024)
        
        // When
        let result = try await sut.save(data: data, metadata: metadata)
        
        // Then
        XCTAssertTrue(fileManager.fileExists(atPath: result.cachedURL.path))
        let metadataURL = temporaryDirectory
            .appendingPathComponent("PetSpot/ReportMissingPet/metadata.json")
        XCTAssertTrue(fileManager.fileExists(atPath: metadataURL.path))
    }
    
    func testLoadCurrent_whenAttachmentSaved_shouldReturnMetadata() async throws {
        // Given
        let metadata = makeMetadata()
        _ = try await sut.save(data: Data(repeating: 3, count: 2048), metadata: metadata)
        
        // When
        let loaded = try await sut.loadCurrent()
        
        // Then
        XCTAssertNotNil(loaded)
        XCTAssertEqual(loaded?.fileName, metadata.fileName)
    }
    
    func testClearCurrent_shouldRemoveFiles() async throws {
        // Given
        let metadata = makeMetadata()
        let saved = try await sut.save(data: Data(repeating: 5, count: 512), metadata: metadata)
        let metadataURL = temporaryDirectory
            .appendingPathComponent("PetSpot/ReportMissingPet/metadata.json")
        
        // When
        try await sut.clearCurrent()
        
        // Then
        XCTAssertFalse(fileManager.fileExists(atPath: saved.cachedURL.path))
        XCTAssertFalse(fileManager.fileExists(atPath: metadataURL.path))
    }
    
    // MARK: - Helpers
    
    private func makeMetadata() -> PhotoAttachmentMetadata {
        PhotoAttachmentMetadata(
            id: UUID(),
            fileName: "pet.jpg",
            fileSizeBytes: 1024,
            utiIdentifier: UTType.jpeg.identifier,
            pixelWidth: 400,
            pixelHeight: 400,
            assetIdentifier: "asset-1",
            cachedURL: URL(fileURLWithPath: "/tmp/\(UUID().uuidString)"),
            savedAt: Date()
        )
    }
}

