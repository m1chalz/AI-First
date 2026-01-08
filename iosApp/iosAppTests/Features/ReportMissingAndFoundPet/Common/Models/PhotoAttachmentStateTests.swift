import XCTest
import UniformTypeIdentifiers
@testable import PetSpot

final class PhotoAttachmentStateTests: XCTestCase {
    
    func testFormattedFileSize_shouldMatchByteCountFormatter() {
        // Given
        let metadata = makeMetadata(fileSizeBytes: 1_536)
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useKB, .useMB]
        formatter.countStyle = .decimal
        formatter.includesUnit = true
        let expected = formatter.string(fromByteCount: Int64(metadata.fileSizeBytes))
        
        // When
        let result = metadata.formattedFileSize
        
        // Then
        XCTAssertEqual(result, expected)
    }
    
    func testIsSupportedType_whenUtiMatches_shouldReturnTrue() {
        // Given
        let metadata = makeMetadata(uti: UTType.jpeg.identifier)
        
        // Then
        XCTAssertTrue(metadata.isSupportedType)
        XCTAssertTrue(PhotoAttachmentMetadata.isSupported(utiIdentifier: UTType.heic.identifier))
    }
    
    func testPhotoAttachmentStatus_hasAttachmentFlag() {
        // Given
        let metadata = makeMetadata()
        
        // When/Then
        XCTAssertFalse(PhotoAttachmentStatus.empty.hasAttachment)
        XCTAssertTrue(PhotoAttachmentStatus.confirmed(metadata: metadata).hasAttachment)
    }
    
    func testUpdatingCacheLocation_shouldReturnNewURLAndDate() {
        // Given
        let metadata = makeMetadata()
        let newURL = URL(fileURLWithPath: "/tmp/\(UUID().uuidString)")
        
        // When
        let updated = metadata.updatingCacheLocation(newURL, savedAt: Date(timeIntervalSince1970: 10))
        
        // Then
        XCTAssertEqual(updated.cachedURL, newURL)
        XCTAssertEqual(updated.savedAt, Date(timeIntervalSince1970: 10))
    }
    
    // MARK: - Helpers
    
    private func makeMetadata(
        fileSizeBytes: Int = 512,
        uti: String = UTType.jpeg.identifier
    ) -> PhotoAttachmentMetadata {
        PhotoAttachmentMetadata(
            id: UUID(),
            fileName: "pet.jpg",
            fileSizeBytes: fileSizeBytes,
            utiIdentifier: uti,
            pixelWidth: 100,
            pixelHeight: 100,
            assetIdentifier: nil,
            cachedURL: URL(fileURLWithPath: "/tmp/\(UUID().uuidString)"),
            savedAt: Date()
        )
    }
}

