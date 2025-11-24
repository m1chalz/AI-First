import SwiftUI

/// Model for configuring the pet photo with overlaid badges
struct PetPhotoWithBadgesModel: Equatable {
    /// URL string for the pet photo (nullable)
    let imageUrl: String?
    
    /// Status text for the badge ("MISSING", "FOUND", or "CLOSED")
    let status: String
    
    /// Optional reward text (nil if no reward)
    let rewardText: String?
    
    /// Convenience initializer mapping from PetDetails
    init(from petDetails: PetDetails) {
        self.imageUrl = petDetails.photoUrl
        // Map ACTIVE → MISSING for display
        self.status = petDetails.status == "ACTIVE" ? "MISSING" : petDetails.status
        self.rewardText = petDetails.reward
    }
    
    /// Direct initializer for testing and custom usage
    init(imageUrl: String?, status: String, rewardText: String?) {
        self.imageUrl = imageUrl
        self.status = status
        self.rewardText = rewardText
    }
}

/// Reusable component displaying pet photo with status and optional reward badges
struct PetPhotoWithBadges: View {
    let model: PetPhotoWithBadgesModel
    
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
        .frame(height: 229)
    }
    
    // MARK: - Photo View
    
    @ViewBuilder
    private var photoView: some View {
        if let imageUrl = model.imageUrl, let url = URL(string: imageUrl) {
            AsyncImage(url: url) { phase in
                switch phase {
                case .empty:
                    ProgressView()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(height: 300)
                        .clipped()
                case .failure:
                    imagePlaceholder
                @unknown default:
                    imagePlaceholder
                }
            }
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
            Text("Image not available")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.gray.opacity(0.1))
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
                Text("Reward")
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
struct PetPhotoWithBadges_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 20) {
            // Missing pet with reward
            PetPhotoWithBadges(model: PetPhotoWithBadgesModel(
                imageUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
                status: "MISSING",
                rewardText: "$500 reward"
            ))
            
            // Found pet without reward
            PetPhotoWithBadges(model: PetPhotoWithBadgesModel(
                imageUrl: "https://images.dog.ceo/breeds/shepherd-german/n02106662_10908.jpg",
                status: "FOUND",
                rewardText: nil
            ))
            
            // No photo available
            PetPhotoWithBadges(model: PetPhotoWithBadgesModel(
                imageUrl: nil,
                status: "MISSING",
                rewardText: "$200"
            ))
        }
    }
}
#endif

