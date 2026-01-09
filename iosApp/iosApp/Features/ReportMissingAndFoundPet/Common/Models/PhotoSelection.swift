import Foundation
import UniformTypeIdentifiers

/// Raw selection payload produced by PhotosPicker before caching.
struct PhotoSelection {
    let data: Data
    let fileName: String
    let contentType: UTType
    let pixelWidth: Int
    let pixelHeight: Int
    let assetIdentifier: String?
}

