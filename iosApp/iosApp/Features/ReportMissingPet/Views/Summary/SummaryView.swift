import SwiftUI

/// View for Summary screen (Step 5 - no progress indicator).
/// Empty placeholder with Submit button only.
/// TODO: Display collected data in future iteration.
struct SummaryView: View {
    @ObservedObject var viewModel: SummaryViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // Placeholder text
            Text("Summary Screen")
                .font(.title)
                .foregroundColor(.gray)
            
            Spacer()
            
            // Submit button at bottom
            Button(action: viewModel.handleSubmit) {
                Text(L10n.ReportMissingPet.Button.submit)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(Color(hex: "#155DFC"))
                    .cornerRadius(10)
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 30)
            .accessibilityIdentifier("summary.submitButton")
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
    }
}

