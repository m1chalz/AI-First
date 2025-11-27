import SwiftUI

/// View for Photo screen (Step 2/4).
/// Empty placeholder with Continue button only.
/// TODO: Add photo picker in future iteration.
struct PhotoView: View {
    @ObservedObject var viewModel: PhotoViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // Placeholder text
            Text("Photo Screen")
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
            .accessibilityIdentifier("photo.continueButton")
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
    }
}

