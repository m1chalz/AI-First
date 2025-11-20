import SwiftUI

/**
 * SwiftUI view for displaying empty state when no animals are available.
 * Shows user-friendly message encouraging action.
 *
 * Message per FR-009: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
 */
struct EmptyStateView: View {
    var body: some View {
        VStack {
            Spacer()
            
            Text("No animals reported yet. Tap 'Report a Missing Animal' to add the first one.")
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                .multilineTextAlignment(.center)
                .lineSpacing(8)
                .padding(.horizontal, 32)
            
            Spacer()
        }
    }
}

