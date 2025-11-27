import Foundation

extension AnimalPhotoItemView {
    struct Model {
        let fileName: String
        let fileSizeText: String
        let iconSymbolName: String
        let iconBackgroundHex: String
        let iconForegroundHex: String
        let fileNameColorHex: String
        let fileSizeColorHex: String
        let removeIconForegroundHex: String
        let removeIconBackgroundHex: String
        let cardBackgroundHex: String
        let cardBorderHex: String
    }
}

extension AnimalPhotoItemView.Model {
    init(metadata: PhotoAttachmentMetadata) {
        self.init(
            fileName: metadata.fileName,
            fileSizeText: metadata.formattedFileSize,
            iconSymbolName: "photo.on.rectangle",
            iconBackgroundHex: "#D0FAE5",
            iconForegroundHex: "#1C8C5E",
            fileNameColorHex: "#101828",
            fileSizeColorHex: "#717182",
            removeIconForegroundHex: "#2D2D2D",
            removeIconBackgroundHex: "#FFFFFF",
            cardBackgroundHex: "#FFFFFF",
            cardBorderHex: "#14000000"
        )
    }
}

