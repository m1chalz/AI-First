import SwiftUI

/// View for Contact Details screen (Step 4/4).
/// Displays phone, email, and reward input fields with validation.
struct MissingPetContactDetailsView: View {
    @ObservedObject var viewModel: MissingPetContactDetailsViewModel
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Title + Subtitle
                    VStack(alignment: .leading, spacing: 8) {
                        Text(L10n.OwnersDetails.mainTitle)
                            .font(.title2)
                            .bold()
                            .accessibilityIdentifier("ownersDetails.title")
                        
                        Text(L10n.OwnersDetails.subtitle)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .accessibilityIdentifier("ownersDetails.subtitle")
                    }
                    
                    // Form fields
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
                .padding(.top, 16)
            }
            .background(Color.white)
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: 120)
            }
            
            // Continue button
            VStack(spacing: 0) {
                Button(action: {
                    Task {
                        await viewModel.submitForm()
                    }
                }) {
                    Group {
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
                }
                .disabled(viewModel.isSubmitting)
                .accessibilityIdentifier("ownersDetails.continueButton")
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 24)
            .frame(maxWidth: .infinity)
            .background(Color.white.ignoresSafeArea(edges: .bottom))
        }
        .background(Color.white.ignoresSafeArea())
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

