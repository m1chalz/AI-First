import Foundation
import UniformTypeIdentifiers

/// Supported photo attachment metadata persisted inside the flow state.
struct PhotoAttachmentMetadata: Codable, Equatable, Identifiable {
    let id: UUID
    let fileName: String
    let fileSizeBytes: Int
    let utiIdentifier: String
    let pixelWidth: Int
    let pixelHeight: Int
    let assetIdentifier: String?
    let cachedURL: URL
    let savedAt: Date
    
    /// Human-readable size description (e.g. "1.4 MB").
    var formattedFileSize: String {
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useKB, .useMB]
        formatter.countStyle = .decimal
        formatter.includesUnit = true
        return formatter.string(fromByteCount: Int64(fileSizeBytes))
    }
    
    /// System `UTType` resolved from the stored identifier.
    var uniformType: UTType? {
        UTType(utiIdentifier)
    }
    
    /// Whether the attachment type is part of the allowed formats list.
    var isSupportedType: Bool {
        PhotoAttachmentMetadata.supportedTypes.contains { type in
            guard let uniformType else { return false }
            return uniformType.conforms(to: type)
        }
    }
    
    /// Returns copy with updated cached file URL and timestamp.
    func updatingCacheLocation(_ url: URL, savedAt newDate: Date = Date()) -> PhotoAttachmentMetadata {
        PhotoAttachmentMetadata(
            id: id,
            fileName: fileName,
            fileSizeBytes: fileSizeBytes,
            utiIdentifier: utiIdentifier,
            pixelWidth: pixelWidth,
            pixelHeight: pixelHeight,
            assetIdentifier: assetIdentifier,
            cachedURL: url,
            savedAt: newDate
        )
    }
}

extension PhotoAttachmentMetadata {
    /// Allowed UTTypes per business requirement.
    static let supportedTypes: [UTType] = [
        .jpeg,
        .png,
        .heic,
        .gif,
        .webP
    ]
    
    /// Convenience for validating arbitrary identifiers.
    static func isSupported(utiIdentifier: String) -> Bool {
        guard let type = UTType(utiIdentifier) else { return false }
        return supportedTypes.contains { type.conforms(to: $0) }
    }
}

/// Attachment lifecycle used by the ViewModel + FlowState.
enum PhotoAttachmentStatus: Equatable {
    case empty
    case loading(progress: Double?)
    case confirmed(metadata: PhotoAttachmentMetadata)
    case error(message: String)
    
    /// `true` when a confirmed attachment exists.
    var hasAttachment: Bool {
        if case .confirmed = self {
            return true
        }
        return false
    }
}

