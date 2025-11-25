import Foundation

extension PetPhotoWithBadgesView {
    /// Model for configuring the pet photo with overlaid badges
    struct Model: Equatable {
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
}

