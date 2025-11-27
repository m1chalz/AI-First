import SwiftUI

/// Renders the confirmation card with filename, size and remove control.
struct AnimalPhotoConfirmationCard: View {
    let metadata: PhotoAttachmentMetadata
    let onRemove: () -> Void
    
    var body: some View {
        HStack(spacing: 16) {
            RoundedRectangle(cornerRadius: 6)
                .fill(Color(hex: "#D0FAE5"))
                .frame(width: 40, height: 40)
                .overlay(
                    Image(systemName: "photo.on.rectangle")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(Color(hex: "#1C8C5E"))
                )
            
            VStack(alignment: .leading, spacing: 2) {
                Text(metadata.fileName)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(hex: "#101828"))
                    .lineLimit(1)
                Text(metadata.formattedFileSize)
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "#717182"))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            Button(action: onRemove) {
                Image(systemName: "xmark")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(hex: "#2D2D2D"))
                    .frame(width: 32, height: 32)
                    .background(
                        Circle()
                            .fill(Color.white)
                    )
            }
            .accessibilityIdentifier("animalPhoto.remove")
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(Color.white)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 10)
                .stroke(Color.black.opacity(0.08), lineWidth: 0.67)
        )
        .accessibilityIdentifier("animalPhoto.confirmationCard")
    }
}

#if DEBUG
struct AnimalPhotoConfirmationCard_Previews: PreviewProvider {
    static var previews: some View {
        AnimalPhotoConfirmationCard(
            metadata: PhotoAttachmentMetadata(
                id: UUID(),
                fileName: "Max.img",
                fileSizeBytes: 1_200_000,
                utiIdentifier: "public.jpeg",
                pixelWidth: 1280,
                pixelHeight: 720,
                assetIdentifier: "asset",
                cachedURL: URL(fileURLWithPath: "/tmp"),
                savedAt: Date()
            ),
            onRemove: {}
        )
        .padding()
        .background(Color(hex: "#F5F6FA"))
        .previewLayout(.sizeThatFits)
    }
}
#endif

