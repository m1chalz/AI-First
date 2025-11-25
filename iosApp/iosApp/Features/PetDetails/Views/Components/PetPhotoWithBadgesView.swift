import SwiftUI

/// Reusable component displaying pet photo with status and optional reward badges
struct PetPhotoWithBadgesView: View {
    let model: Model
    
    var body: some View {
        ZStack {
            // Pet Photo
            photoView
            
            // Status Badge (top right)
            VStack {
                HStack {
                    Spacer()
                    statusBadge
                        .padding(18)
                }
                Spacer()
            }
            
            // Reward Badge (bottom left, over photo)
            if let rewardText = model.rewardText {
                VStack {
                    Spacer()
                    HStack {
                        rewardBadge(text: rewardText)
                            .padding(20)
                        Spacer()
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .clipped()
    }
    
    // MARK: - Photo View
    
    @ViewBuilder
    private var photoView: some View {
        if let imageUrl = model.imageUrl, let url = URL(string: imageUrl) {
            Rectangle()
                .fill(Color.clear)
                .overlay(
                    AsyncImage(url: url) { phase in
                        switch phase {
                        case .empty:
                            ProgressView()
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                        case .failure:
                            imagePlaceholder
                        @unknown default:
                            imagePlaceholder
                        }
                    }
                )
                .accessibilityIdentifier("petDetails.photo.image")
        } else {
            imagePlaceholder
        }
    }
    
    private var imagePlaceholder: some View {
        VStack(spacing: 10) {
            Image(systemName: "photo")
                .font(.system(size: 60))
                .foregroundColor(.gray)
            Text(L10n.PetDetails.Photo.notAvailable)
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.red)
        .accessibilityIdentifier("petDetails.photo.image")
    }
    
    // MARK: - Status Badge
    
    private var statusBadge: some View {
        Text(model.status)
            .font(.system(size: 16))
            .foregroundColor(.white)
            .padding(.horizontal, 12)
            .padding(.vertical, 2)
            .background(statusBadgeColor)
            .cornerRadius(22369600)
            .accessibilityIdentifier("petDetails.status.badge")
    }
    
    private var statusBadgeColor: Color {
        switch model.status {
        case "MISSING":
            return Color.red
        case "FOUND":
            return Color.green
        case "CLOSED":
            return Color.gray
        default:
            return Color.blue
        }
    }
    
    // MARK: - Reward Badge
    
    private func rewardBadge(text: String) -> some View {
        HStack(spacing: 12) {
            Image(systemName: "bag")
                .font(.system(size: 24))
                .foregroundColor(.white)
            
            VStack(alignment: .leading, spacing: 0) {
                Text(L10n.PetDetails.Reward.label)
                    .font(.system(size: 16))
                    .foregroundColor(.white)
                Text(text)
                    .font(.system(size: 16))
                    .foregroundColor(.white)
            }
        }
        .accessibilityIdentifier("petDetails.reward.badge")
    }
}

// MARK: - Previews

#if DEBUG
struct PetPhotoWithBadgesView_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 20) {
            // Missing pet with reward
            PetPhotoWithBadgesView(model: .init(
                imageUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
                status: "MISSING",
                rewardText: "$500 reward"
            ))
            .frame(height: 229)
            
            // Found pet without reward
            PetPhotoWithBadgesView(model: .init(
                imageUrl: "https://images.dog.ceo/breeds/shepherd-german/n02106662_10908.jpg",
                status: "FOUND",
                rewardText: nil
            ))
            .frame(height: 229)
            
            // No photo available
            PetPhotoWithBadgesView(model: .init(
                imageUrl: nil,
                status: "MISSING",
                rewardText: "$200"
            ))
            .frame(height: 229)
        }
    }
}
#endif

