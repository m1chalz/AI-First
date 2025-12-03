import SwiftUI

/**
 * SwiftUI view for displaying empty state when no animals are available.
 * Shows user-friendly message encouraging action.
 *
 * - Parameter model: Empty state presentation data (EmptyStateView.Model)
 */
struct EmptyStateView: View {
    let model: Model
    
    var body: some View {
        VStack {
            Spacer()
            
            Text(model.message)
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                .multilineTextAlignment(.center)
                .lineSpacing(8)
                .padding(.horizontal, 32)
            
            Spacer()
        }
    }
}

