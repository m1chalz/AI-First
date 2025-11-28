import SwiftUI

/// Animal Description screen (Step 3/4 of Missing Pet flow).
/// Collects required pet data: date, species, breed/race, gender.
struct AnimalDescriptionView: View {
    @ObservedObject var viewModel: AnimalDescriptionViewModel
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Heading
                Text(L10n.AnimalDescription.heading)
                    .font(.custom("Inter-Regular", size: 32))
                    .foregroundColor(Color(hex: "#2D2D2D"))
                
                // Subheading
                Text(L10n.AnimalDescription.subheading)
                    .font(.custom("Inter-Regular", size: 16))
                    .foregroundColor(Color(hex: "#545F71"))
                
                // Date input
                DateInputView(
                    model: viewModel.dateInputModel,
                    date: $viewModel.disappearanceDate
                )
                
                // Species dropdown
                DropdownView(
                    model: viewModel.speciesDropdownModel,
                    selectedValue: $viewModel.selectedSpecies
                )
                .onChange(of: viewModel.selectedSpecies) { _, _ in
                    viewModel.handleSpeciesChange()
                }
                
                // Race text field (disabled until species selected)
                ValidatedTextField(
                    model: viewModel.raceTextFieldModel,
                    text: $viewModel.race
                )
                .onChange(of: viewModel.race) { _, newValue in
                    viewModel.handleRaceChange(newValue)
                }
                
                // Gender selector
                SelectorView(
                    model: viewModel.genderSelectorModel,
                    selectedValue: $viewModel.selectedGender
                )
                .onChange(of: viewModel.selectedGender) { _, _ in
                    viewModel.handleGenderChange()
                }
                
                // Age text field (optional - US3)
                ValidatedTextField(
                    model: viewModel.ageTextFieldModel,
                    text: $viewModel.age
                )
                
                // GPS button
                Button(action: {
                    Task {
                        await viewModel.requestGPSPosition()
                    }
                }) {
                    Text(L10n.AnimalDescription.requestGPSButton)
                        .font(.custom("Hind-Regular", size: 16))
                        .foregroundColor(Color(hex: "#155DFC"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 40)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(Color(hex: "#155DFC"), lineWidth: 2)
                        )
                }
                .buttonStyle(PlainButtonStyle())
                .accessibilityIdentifier("animalDescription.requestGPSButton.tap")
                
                // Location coordinates (optional)
                CoordinateInputView(
                    model: viewModel.coordinateInputModel,
                    latitude: $viewModel.latitude,
                    longitude: $viewModel.longitude
                )
                
                // Description text area (optional - US3)
                TextAreaView(
                    model: viewModel.descriptionTextAreaModel,
                    text: $viewModel.additionalDescription
                )
                
                // Continue button
                Button(action: { viewModel.onContinueTapped() }) {
                    Text(L10n.AnimalDescription.continueButton)
                        .font(.custom("Hind-Regular", size: 18))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color(hex: "#155DFC"))
                        .cornerRadius(10)
                }
                .accessibilityIdentifier("animalDescription.continueButton.tap")
            }
            .padding()
            .padding(.bottom, 20) // Extra bottom padding for keyboard
        }
        .scrollDismissesKeyboard(.interactively)
        .alert("Location Permission Required", isPresented: $viewModel.showPermissionDeniedAlert) {
            Button("Cancel", role: .cancel) {}
            Button("Go to Settings") {
                // Open app settings
                if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(settingsURL)
                }
            }
        } message: {
            Text("Location access is needed to capture GPS coordinates. Please enable it in Settings.")
        }
        .overlay(
            // Toast notification for validation errors
            Group {
                if viewModel.showToast {
                    VStack {
                        Spacer()
                        Text(viewModel.toastMessage)
                            .padding()
                            .background(Color.black.opacity(0.8))
                            .foregroundColor(.white)
                            .cornerRadius(8)
                            .padding(.bottom, 50)
                    }
                    .transition(.move(edge: .bottom))
                    .animation(.easeInOut, value: viewModel.showToast)
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                            viewModel.showToast = false
                        }
                    }
                }
            }
        )
    }
}

