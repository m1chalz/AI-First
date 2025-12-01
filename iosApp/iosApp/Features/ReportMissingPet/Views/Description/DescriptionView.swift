import SwiftUI

/// View for Description screen (Step 3/4).
/// Empty placeholder with Continue button only.
/// TODO: Add text area for description in future iteration.
struct DescriptionView: View {
    @ObservedObject var viewModel: DescriptionViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // Placeholder text
            Text("Description Screen")
                .font(.title)
                .foregroundColor(.gray)
            
            Spacer()
            
            // Continue button at bottom
            Button(action: viewModel.handleNext) {
                Text(L10n.Common.continue)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(Color(hex: "#155DFC"))
                    .cornerRadius(10)
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 30)
            .accessibilityIdentifier("description.continueButton")
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
    }
}

