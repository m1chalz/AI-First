import SwiftUI

/// Animal Description screen (Step 3/4 of Missing Pet flow).
/// Collects required pet data: date, species, breed/race, gender.
struct AnimalDescriptionView: View {
    @ObservedObject var viewModel: AnimalDescriptionViewModel
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Date input
                DateInputView(
                    model: viewModel.dateInputModel,
                    date: $viewModel.disappearanceDate
                )
                
                // Species dropdown
                DropdownView(
                    model: viewModel.speciesDropdownModel,
                    selectedIndex: $viewModel.selectedSpeciesIndex
                )
                .onChange(of: viewModel.selectedSpeciesIndex) { newIndex in
                    if let index = newIndex {
                        viewModel.selectSpecies(index)
                    }
                }
                
                // Race text field (disabled until species selected)
                ValidatedTextField(
                    model: viewModel.raceTextFieldModel,
                    text: $viewModel.race
                )
                
                // Gender selector
                SelectorView(
                    model: viewModel.genderSelectorModel,
                    selectedIndex: $viewModel.selectedGenderIndex
                )
                .onChange(of: viewModel.selectedGenderIndex) { newIndex in
                    if let index = newIndex {
                        viewModel.selectGender(index)
                    }
                }
                
                // Age text field (optional - US3)
                ValidatedTextField(
                    model: viewModel.ageTextFieldModel,
                    text: $viewModel.age
                )
                
                // Location coordinates with GPS button (US2)
                LocationCoordinateView(
                    model: viewModel.locationCoordinateModel,
                    latitude: $viewModel.latitude,
                    longitude: $viewModel.longitude,
                    onGPSButtonTap: {
                        await viewModel.requestGPSPosition()
                    }
                )
                
                // Description text area (optional - US3)
                TextAreaView(
                    model: viewModel.descriptionTextAreaModel,
                    text: $viewModel.additionalDescription
                )
                
                // Continue button
                Button(action: { viewModel.onContinueTapped() }) {
                    Text(L10n.AnimalDescription.continueButton)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .accessibilityIdentifier("animalDescription.continueButton.tap")
            }
            .padding()
        }
        .navigationTitle(L10n.ReportMissingPet.Description.title)
        .navigationBarTitleDisplayMode(.inline)
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

