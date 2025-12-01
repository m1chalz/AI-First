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
        let showsLoadingIcon: Bool
        let thumbnailURL: URL?

        init(
            fileName: String,
            fileSizeText: String,
            iconSymbolName: String,
            iconBackgroundHex: String,
            iconForegroundHex: String,
            fileNameColorHex: String,
            fileSizeColorHex: String,
            removeIconForegroundHex: String,
            removeIconBackgroundHex: String,
            cardBackgroundHex: String,
            cardBorderHex: String,
            showsLoadingIcon: Bool = false,
            thumbnailURL: URL? = nil
        ) {
            self.fileName = fileName
            self.fileSizeText = fileSizeText
            self.iconSymbolName = iconSymbolName
            self.iconBackgroundHex = iconBackgroundHex
            self.iconForegroundHex = iconForegroundHex
            self.fileNameColorHex = fileNameColorHex
            self.fileSizeColorHex = fileSizeColorHex
            self.removeIconForegroundHex = removeIconForegroundHex
            self.removeIconBackgroundHex = removeIconBackgroundHex
            self.cardBackgroundHex = cardBackgroundHex
            self.cardBorderHex = cardBorderHex
            self.showsLoadingIcon = showsLoadingIcon
            self.thumbnailURL = thumbnailURL
        }

        init(metadata: PhotoAttachmentMetadata, showsLoadingIcon: Bool = false) {
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
                cardBorderHex: "#14000000",
                showsLoadingIcon: showsLoadingIcon,
                thumbnailURL: metadata.cachedURL.isFileURL && metadata.cachedURL.path != "/dev/null" ? metadata.cachedURL : nil
            )
        }
    }
}

