import Foundation
@testable import PetSpot

final class PhotoAttachmentCacheFake: PhotoAttachmentCacheProtocol {
    var savedMetadata: PhotoAttachmentMetadata?
    var saveError: Error?
    var lastSavedData: Data?
    
    var loadResult: PhotoAttachmentMetadata?
    var loadError: Error?
    
    var clearCallCount = 0
    var clearError: Error?
    
    var fileExistsResult = true
    
    func save(data: Data, metadata: PhotoAttachmentMetadata) async throws -> PhotoAttachmentMetadata {
        if let saveError {
            throw saveError
        }
        lastSavedData = data
        savedMetadata = metadata
        return metadata
    }
    
    func loadCurrent() async throws -> PhotoAttachmentMetadata? {
        if let loadError {
            throw loadError
        }
        return loadResult
    }
    
    func fileExists(at url: URL) async -> Bool {
        fileExistsResult
    }
    
    func clearCurrent() async throws {
        clearCallCount += 1
        if let clearError {
            throw clearError
        }
    }
}

