import Foundation
import CoreTransferable
import UniformTypeIdentifiers

/// Abstraction describing what is needed from a Photos picker item.
protocol PhotoPickerItemProviding {
    var itemIdentifier: String? { get }
    func loadTransferable<T: Transferable>(type: T.Type) async throws -> T?
}

/// Processes `PhotosPickerItem` instances into `PhotoSelection` models.
protocol PhotoSelectionProcessing {
    func process(_ item: PhotoPickerItemProviding) async throws -> PhotoSelection
}

/// Default implementation that extracts binary data and metadata from a picker item.
struct PhotoSelectionProcessor: PhotoSelectionProcessing {
    func process(_ item: PhotoPickerItemProviding) async throws -> PhotoSelection {
        guard let transferable = try await item.loadTransferable(type: AnimalPhotoTransferable.self) else {
            throw PhotoSelectionProcessorError.emptyTransferable
        }
        
        return PhotoSelection(
            data: transferable.data,
            fileName: transferable.fileName ?? fallbackFilename(for: transferable.contentType),
            contentType: transferable.contentType,
            pixelWidth: transferable.pixelWidth,
            pixelHeight: transferable.pixelHeight,
            assetIdentifier: item.itemIdentifier
        )
    }
    
    private func fallbackFilename(for type: UTType) -> String {
        let fileExtension = type.preferredFilenameExtension ?? "img"
        return "animal-photo.\(fileExtension)"
    }
}

enum PhotoSelectionProcessorError: Error, Equatable {
    case emptyTransferable
}


