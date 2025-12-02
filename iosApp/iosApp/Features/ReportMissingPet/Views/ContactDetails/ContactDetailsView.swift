import SwiftUI

/// View for Contact Details screen (Step 4/4).
/// Displays phone, email, and reward input fields with validation.
struct ContactDetailsView: View {
    @ObservedObject var viewModel: ContactDetailsViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Title + Subtitle
            VStack(alignment: .leading, spacing: 8) {
                Text(L10n.OwnersDetails.screenTitle)
                    .font(.title2)
                    .bold()
                    .accessibilityIdentifier("ownersDetails.title")
                
                Text(L10n.OwnersDetails.subtitle)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .accessibilityIdentifier("ownersDetails.subtitle")
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 16)
            .padding(.top, 16)
            
            // Form fields
            ScrollView {
                VStack(spacing: 16) {
                    ValidatedTextField(
                        model: viewModel.phoneTextFieldModel,
                        text: $viewModel.phone
                    )
                    
                    ValidatedTextField(
                        model: viewModel.emailTextFieldModel,
                        text: $viewModel.email
                    )
                    
                    ValidatedTextField(
                        model: viewModel.rewardTextFieldModel,
                        text: $viewModel.rewardDescription
                    )
                }
                .padding(.horizontal, 16)
                .padding(.top, 24)
            }
            
            // Continue button
            Button(action: {
                Task {
                    await viewModel.submitForm()
                }
            }) {
                if viewModel.isSubmitting {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text(L10n.OwnersDetails.Continue.button)
                        .font(.system(size: 16, weight: .semibold))
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 50)
            .background(Color(hex: "#155DFC"))
            .foregroundColor(.white)
            .cornerRadius(10)
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
            .disabled(viewModel.isSubmitting)
            .accessibilityIdentifier("ownersDetails.continueButton")
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
        .alert(isPresented: $viewModel.showAlert) {
            Alert(
                title: Text(viewModel.alertMessage ?? ""),
                primaryButton: .default(Text(L10n.OwnersDetails.Alert.tryAgain)) {
                    Task {
                        await viewModel.submitForm()
                    }
                },
                secondaryButton: .cancel(Text(L10n.OwnersDetails.Alert.cancel))
            )
        }
    }
}

