import SwiftUI

/// Pet Details screen (Step 2/3 of Found Pet flow).
/// Collects pet data: date, species, breed/race, gender, location (required), collar data (optional).
struct FoundPetAnimalDescriptionView: View {
    @ObservedObject var viewModel: FoundPetAnimalDescriptionViewModel
    
    var body: some View {
        ZStack(alignment: .bottom) {
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
                
                // Collar data / microchip (optional) - per Figma: position 2 after Date
                collarDataSection
                
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
                
                // Location coordinates (required for Found flow)
                CoordinateInputView(
                    model: viewModel.coordinateInputModel,
                    latitude: $viewModel.latitude,
                    longitude: $viewModel.longitude
                )
                
                // Description text area (optional)
                TextAreaView(
                    model: viewModel.descriptionTextAreaModel,
                    text: $viewModel.additionalDescription
                )
                }
                .padding()
            }
            .background(Color.white)
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: 120)
            }
            
            // Bottom section with toast and button (outside ScrollView)
            VStack(spacing: 12) {
                if viewModel.showToast {
                    ToastView(model: .init(text: viewModel.toastMessage))
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
                
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
            .padding(.horizontal, 22)
            .padding(.vertical, 24)
            .frame(maxWidth: .infinity)
            .background(Color.white.ignoresSafeArea(edges: .bottom))
        }
        .scrollDismissesKeyboard(.interactively)
        .animation(.easeInOut(duration: 0.3), value: viewModel.showToast)
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
    }
    
    // MARK: - Subviews
    
    /// Collar data (microchip) input with formatting
    private var collarDataSection: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(L10n.ReportFoundPet.PetDetails.collarDataLabel)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            TextField(
                L10n.ReportFoundPet.PetDetails.collarDataPlaceholder,
                text: Binding(
                    get: { viewModel.formattedCollarData },
                    set: { viewModel.updateCollarData($0) }
                )
            )
            .font(.custom("Hind-Regular", size: 16))
            .foregroundColor(Color(hex: "#364153"))
            .keyboardType(.numberPad)
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color.white)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Color(hex: "#D1D5DC"), lineWidth: 0.667)
            )
            .accessibilityIdentifier("reportFoundPet.petDetails.collarData.input")
        }
    }
}

