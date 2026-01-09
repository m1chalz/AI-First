import Foundation
import UniformTypeIdentifiers

extension PhotoAttachmentMetadata {
    /// Converts UTI identifier to MIME type for HTTP Content-Type header
    var mimeType: String {
        if let utType = UTType(utiIdentifier) {
            return utType.preferredMIMEType ?? "application/octet-stream"
        }
        return "application/octet-stream"
    }
}

