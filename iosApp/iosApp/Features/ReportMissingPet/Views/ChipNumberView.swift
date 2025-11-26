import SwiftUI

/// View for Chip Number screen (Step 1/4).
/// Empty placeholder with Continue button only.
/// TODO: Add chip number input field in future iteration.
struct ChipNumberView: View {
    @ObservedObject var viewModel: ChipNumberViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // Placeholder text
            Text("Chip Number Screen")
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
            .accessibilityIdentifier("chipNumber.continueButton")
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
    }
}

