import SwiftUI

/// View for Contact Details screen (Step 4/4).
/// Empty placeholder with Continue button only.
/// TODO: Add email and phone input fields in future iteration.
struct ContactDetailsView: View {
    @ObservedObject var viewModel: ContactDetailsViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // Placeholder text
            Text("Contact Details Screen")
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
            .accessibilityIdentifier("contactDetails.continueButton")
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
    }
}

