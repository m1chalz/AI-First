import SwiftUI

/// View for Summary screen (Step 5 - no progress indicator).
/// Empty placeholder with Submit button only.
/// TODO: Display collected data in future iteration.
struct SummaryView: View {
    @ObservedObject var viewModel: SummaryViewModel
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack {
                    Spacer()
                        .frame(height: 100)
                    
                    // Placeholder text
                    Text("Summary Screen")
                        .font(.title)
                        .foregroundColor(.gray)
                    
                    Spacer()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(.bottom, 120)
            }
            .background(Color.white)
            
            // Submit button at bottom
            VStack(spacing: 0) {
                Button(action: viewModel.handleSubmit) {
                    Text(L10n.ReportMissingPet.Button.submit)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(Color(hex: "#155DFC"))
                        .cornerRadius(10)
                }
                .accessibilityIdentifier("summary.submitButton")
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 24)
            .frame(maxWidth: .infinity)
            .background(Color.white.ignoresSafeArea(edges: .bottom))
        }
        .background(Color.white.ignoresSafeArea())
    }
}

