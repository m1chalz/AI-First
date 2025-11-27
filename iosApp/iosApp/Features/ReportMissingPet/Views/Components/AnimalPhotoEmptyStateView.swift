import SwiftUI
import PhotosUI

/// Upload tile rendered in both empty and confirmed states.
struct AnimalPhotoEmptyStateView: View {
    @Binding var pickerSelection: PhotosPickerItem?
    let isLoading: Bool
    
    var body: some View {
        HStack(spacing: 16) {
            RoundedRectangle(cornerRadius: 10)
                .fill(Color(hex: "#EBE5FF"))
                .frame(width: 48, height: 48)
                .overlay(
                    Image(systemName: "square.and.arrow.up")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundColor(Color(hex: "#4F39F6"))
                )
            
            VStack(alignment: .leading, spacing: 4) {
                Text(L10n.AnimalPhoto.Tile.title)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(hex: "#101828"))
                Text(L10n.AnimalPhoto.Tile.subtitle)
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "#6A7282"))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            PhotosPicker(
                selection: $pickerSelection,
                matching: .any(of: .jpeg, .png, .heic, .gif, .webP)
            ) {
                Text(L10n.AnimalPhoto.Button.browse)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color(hex: "#4F39F6"))
                    .cornerRadius(10)
            }
            .disabled(isLoading)
            .accessibilityIdentifier("animalPhoto.browse")
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(Color.white)
                .shadow(color: Color.black.opacity(0.02), radius: 8, x: 0, y: 4)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 10)
                .stroke(Color(hex: "#E8ECF0"), lineWidth: 0.67)
        )
    }
}

#if DEBUG
struct AnimalPhotoEmptyStateView_Previews: PreviewProvider {
    struct PreviewWrapper: View {
        @State private var selection: PhotosPickerItem?
        
        var body: some View {
            AnimalPhotoEmptyStateView(
                pickerSelection: $selection,
                isLoading: false
            )
            .padding()
            .background(Color(hex: "#F5F6FA"))
        }
    }
    
    static var previews: some View {
        PreviewWrapper()
            .previewLayout(.sizeThatFits)
    }
}
#endif

