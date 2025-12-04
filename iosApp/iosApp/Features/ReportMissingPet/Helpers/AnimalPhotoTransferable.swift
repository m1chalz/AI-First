import SwiftUI
import UniformTypeIdentifiers
import ImageIO

/// Transferable wrapper for the iOS `PhotosPicker` that normalizes everything we
/// need from a user-selected image (raw bytes, UTI, filename, pixel metadata).
/// `PhotosPicker` exposes selections through `Transferable`; by providing this
/// custom type we keep all parsing logic (reading `CGImageSource`, inferring
/// content type, computing fallback names) away from the UI layer and guarantee
/// the ViewModel receives a predictable payload no matter which image format the
/// user picks.
struct AnimalPhotoTransferable: Transferable {
    let data: Data
    let contentType: UTType
    let fileName: String?
    let pixelWidth: Int
    let pixelHeight: Int
    
    static var transferRepresentation: some TransferRepresentation {
        FileRepresentation(importedContentType: .image) { received in
            let fileURL = received.file
            let pathExtension = fileURL.pathExtension
            let data = try Data(contentsOf: fileURL)
            let metadata = AnimalPhotoTransferable.imageMetadata(
                from: data,
                pathExtension: pathExtension
            )
            return AnimalPhotoTransferable(
                data: data,
                contentType: metadata.type,
                fileName: fileURL.lastPathComponent,
                pixelWidth: metadata.width,
                pixelHeight: metadata.height
            )
        }
    }
    
    private static func imageMetadata(from data: Data, pathExtension: String) -> (width: Int, height: Int, type: UTType) {
        guard let source = CGImageSourceCreateWithData(data as CFData, nil) else {
            return (0, 0, fallbackType(for: pathExtension))
        }
        
        let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [CFString: Any]
        let width = properties?[kCGImagePropertyPixelWidth] as? Int ?? 0
        let height = properties?[kCGImagePropertyPixelHeight] as? Int ?? 0
        let inferredType = AnimalPhotoTransferable.extractedType(from: source)
            ?? fallbackType(for: pathExtension)
        
        return (width, height, inferredType)
    }
    
    private static func extractedType(from source: CGImageSource) -> UTType? {
        guard let cfType = CGImageSourceGetType(source) else {
            return nil
        }
        return UTType(cfType as String)
    }
    
    private static func fallbackType(for pathExtension: String) -> UTType {
        if let inferred = UTType(filenameExtension: pathExtension.lowercased()) {
            return inferred
        }
        return .data
    }
}

