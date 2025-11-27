import SwiftUI

/// Renders the confirmation card with filename, size and remove control.
struct AnimalPhotoItemView: View {
    let model: Model
    let onRemove: () -> Void
    
    var body: some View {
        HStack(spacing: 16) {
            RoundedRectangle(cornerRadius: 6)
                .fill(Color(hex: model.iconBackgroundHex))
                .frame(width: 40, height: 40)
                .overlay(
                    Image(systemName: model.iconSymbolName)
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(Color(hex: model.iconForegroundHex))
                )
            
            VStack(alignment: .leading, spacing: 2) {
                Text(model.fileName)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(hex: model.fileNameColorHex))
                    .lineLimit(1)
                Text(model.fileSizeText)
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: model.fileSizeColorHex))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            Button(action: onRemove) {
                Image(systemName: "xmark")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(hex: model.removeIconForegroundHex))
                    .frame(width: 32, height: 32)
                    .background(
                        Circle()
                            .fill(Color(hex: model.removeIconBackgroundHex))
                    )
            }
            .accessibilityIdentifier("animalPhoto.remove")
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(Color(hex: model.cardBackgroundHex))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 10)
                .stroke(Color(hex: model.cardBorderHex), lineWidth: 0.67)
        )
        .accessibilityIdentifier("animalPhoto.confirmationCard")
    }
}

#if DEBUG
struct AnimalPhotoItemView_Previews: PreviewProvider {
    static var previews: some View {
        AnimalPhotoItemView(
            model: .init(
                fileName: "Max.img",
                fileSizeText: "1.2 MB",
                iconSymbolName: "photo.on.rectangle",
                iconBackgroundHex: "#D0FAE5",
                iconForegroundHex: "#1C8C5E",
                fileNameColorHex: "#101828",
                fileSizeColorHex: "#717182",
                removeIconForegroundHex: "#2D2D2D",
                removeIconBackgroundHex: "#FFFFFF",
                cardBackgroundHex: "#FFFFFF",
                cardBorderHex: "#14000000"
            ),
            onRemove: {}
        )
        .padding()
        .background(Color(hex: "#F5F6FA"))
        .previewLayout(.sizeThatFits)
    }
}
#endif

