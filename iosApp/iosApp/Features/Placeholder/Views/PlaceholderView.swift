import SwiftUI

/// SwiftUI view displaying "Coming soon" placeholder for unimplemented features.
/// Shows a clock icon, title, and message in a centered layout.
///
/// Used for tabs that are not yet implemented: Home, Found Pet, Contact Us, Account.
/// Reusable for any future unimplemented features.
struct PlaceholderView: View {
    
    @ObservedObject var viewModel: PlaceholderViewModel
    
    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            
            // Clock icon indicating feature is coming soon
            Image(systemName: "clock.fill")
                .font(.system(size: 64))
                .foregroundColor(Color(hex: "#808080"))
                .accessibilityIdentifier("placeholder.comingSoon.icon")
            
            // Title (tab name)
            Text(viewModel.title)
                .font(.title)
                .fontWeight(.semibold)
                .foregroundColor(Color(hex: "#2D2D2D"))
            
            // "Coming soon" message
            Text(viewModel.message)
                .font(.body)
                .foregroundColor(Color(hex: "#808080"))
                .multilineTextAlignment(.center)
                .accessibilityIdentifier("placeholder.comingSoon.message")
            
            Spacer()
        }
        .padding(.horizontal, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(hex: "#FAFAFA"))
    }
}

#Preview {
    PlaceholderView(viewModel: PlaceholderViewModel(title: "Home"))
}

