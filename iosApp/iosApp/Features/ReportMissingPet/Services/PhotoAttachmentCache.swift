import Foundation

/// Abstraction over disk-backed storage of the currently selected attachment.
protocol PhotoAttachmentCacheProtocol: AnyObject {
    /**
     Stores the provided binary data inside the PetSpot caches namespace.
     
     - Parameters:
       - data: Raw image data returned by `PhotosPicker`.
       - metadata: Lightweight metadata describing the attachment.
     - Returns: Updated metadata containing the persisted file URL.
     */
    func save(data: Data, metadata: PhotoAttachmentMetadata) async throws -> PhotoAttachmentMetadata
    
    /**
     Loads the previously cached attachment, if any.
     
     - Returns: Latest metadata + URL or `nil` when no attachment exists.
     */
    func loadCurrent() async throws -> PhotoAttachmentMetadata?
    
    /// Convenience helper used to validate that the cached file still exists on disk.
    func fileExists(at url: URL) async -> Bool
    
    /// Deletes the cached file and metadata from disk.
    func clearCurrent() async throws
}

/// Errors emitted by `PhotoAttachmentCache`.
enum PhotoAttachmentCacheError: Error, Equatable {
    case cachesDirectoryUnavailable
    case directoryCreationFailed
    case lowDiskSpace
    case writeFailed
    case metadataEncodingFailed
    case metadataDecodingFailed
    case missingCachedFile
    case clearFailed
}

/// Persists attachment blobs inside Library/Caches/PetSpot/ReportMissingPet.
actor PhotoAttachmentCache: PhotoAttachmentCacheProtocol {
    private let fileManager: FileManager
    private let encoder: JSONEncoder
    private let decoder: JSONDecoder
    private let namespacePath = "PetSpot/ReportMissingPet"
    private let metadataFileName = "metadata.json"
    private let customBaseDirectory: URL?
    
    init(fileManager: FileManager = .default, baseDirectory: URL? = nil) {
        self.fileManager = fileManager
        self.encoder = JSONEncoder()
        self.encoder.outputFormatting = .prettyPrinted
        self.decoder = JSONDecoder()
        self.customBaseDirectory = baseDirectory
    }
    
    func save(data: Data, metadata: PhotoAttachmentMetadata) async throws -> PhotoAttachmentMetadata {
        let directory = try makeDirectoryIfNeeded()
        let fileURL = directory.appendingPathComponent("\(metadata.id.uuidString).img", isDirectory: false)
        
        do {
            try data.write(to: fileURL, options: .atomic)
        } catch {
            if let nsError = error as NSError?, nsError.code == NSFileWriteOutOfSpaceError {
                throw PhotoAttachmentCacheError.lowDiskSpace
            }
            throw PhotoAttachmentCacheError.writeFailed
        }
        
        let updatedMetadata = metadata.updatingCacheLocation(fileURL)
        do {
            let encoded = try encoder.encode(updatedMetadata)
            try encoded.write(to: metadataURL(in: directory), options: .atomic)
        } catch is EncodingError {
            throw PhotoAttachmentCacheError.metadataEncodingFailed
        } catch {
            throw PhotoAttachmentCacheError.writeFailed
        }
        
        return updatedMetadata
    }
    
    func loadCurrent() async throws -> PhotoAttachmentMetadata? {
        let directory = try makeDirectoryIfNeeded()
        let metadataURL = self.metadataURL(in: directory)
        guard fileManager.fileExists(atPath: metadataURL.path) else {
            return nil
        }
        
        do {
            let data = try Data(contentsOf: metadataURL)
            let metadata = try decoder.decode(PhotoAttachmentMetadata.self, from: data)
            guard fileManager.fileExists(atPath: metadata.cachedURL.path) else {
                try? fileManager.removeItem(at: metadataURL)
                return nil
            }
            return metadata
        } catch is DecodingError {
            throw PhotoAttachmentCacheError.metadataDecodingFailed
        } catch {
            throw PhotoAttachmentCacheError.metadataDecodingFailed
        }
    }
    
    func fileExists(at url: URL) async -> Bool {
        fileManager.fileExists(atPath: url.path)
    }
    
    func clearCurrent() async throws {
        let directory = try makeDirectoryIfNeeded()
        let metadataURL = self.metadataURL(in: directory)
        guard fileManager.fileExists(atPath: metadataURL.path) else {
            return
        }
        
        do {
            let data = try Data(contentsOf: metadataURL)
            let metadata = try decoder.decode(PhotoAttachmentMetadata.self, from: data)
            if fileManager.fileExists(atPath: metadata.cachedURL.path) {
                try fileManager.removeItem(at: metadata.cachedURL)
            }
            try fileManager.removeItem(at: metadataURL)
        } catch is DecodingError {
            throw PhotoAttachmentCacheError.metadataDecodingFailed
        } catch {
            throw PhotoAttachmentCacheError.clearFailed
        }
    }
    
    private func makeDirectoryIfNeeded() throws -> URL {
        let baseURL: URL
        if let customBaseDirectory {
            baseURL = customBaseDirectory
        } else if let cachesURL = fileManager.urls(for: .cachesDirectory, in: .userDomainMask).first {
            baseURL = cachesURL
        } else {
            throw PhotoAttachmentCacheError.cachesDirectoryUnavailable
        }
        
        let directory = baseURL.appendingPathComponent(namespacePath, isDirectory: true)
        if !fileManager.fileExists(atPath: directory.path) {
            do {
                try fileManager.createDirectory(at: directory, withIntermediateDirectories: true)
            } catch {
                throw PhotoAttachmentCacheError.directoryCreationFailed
            }
        }
        
        return directory
    }
    
    private func metadataURL(in directory: URL) -> URL {
        directory.appendingPathComponent(metadataFileName, isDirectory: false)
    }
}

