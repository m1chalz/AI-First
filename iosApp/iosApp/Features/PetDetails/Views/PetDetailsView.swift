import SwiftUI

/// Main view for Pet Details Screen.
/// Displays comprehensive pet information following Figma design specifications.
struct PetDetailsView: View {
    @ObservedObject var viewModel: PetDetailsViewModel
    
    var body: some View {
        Group {
            switch viewModel.state {
            case .loading:
                loadingView
            case .loaded(let petDetails):
                detailsView(petDetails: petDetails)
            case .error(let message):
                errorView(message: message)
            }
        }
        .accessibilityIdentifier("petDetails.view")
        .task {
            await viewModel.loadPetDetails()
        }
    }
    
    // MARK: - Loading State
    
    private var loadingView: some View {
        LoadingView(model: .init(
            message: L10n.PetDetails.Loading.message,
            accessibilityIdentifier: "petDetails.loading"
        ))
    }
    
    // MARK: - Error State
    
    private func errorView(message: String) -> some View {
        ErrorView(model: .init(
            title: L10n.PetDetails.Error.title,
            message: message,
            onRetry: { viewModel.retry() },
            accessibilityIdentifier: "petDetails.error"
        ))
    }
    
    // MARK: - Loaded State
    
    private func detailsView(petDetails: PetDetails) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                // Pet Photo with Badges
                if let model = viewModel.photoWithBadgesModel {
                    PetPhotoWithBadgesView(model: model)
                        .frame(height: 229)
                        .frame(maxWidth: .infinity)
                }
                
                // Content Container with padding
                VStack(alignment: .leading, spacing: 20) {
                    // Date of Disappearance (full width)
                    LabelValueRowView(model: .init(
                        label: L10n.PetDetails.Label.dateOfDisappearance,
                        value: viewModel.formattedDate
                    ))
                    .accessibilityIdentifier("petDetails.date.field")
                    .padding(.bottom, 10.667)
                    .overlay(
                        Divider().background(Color.gray.opacity(0.2)),
                        alignment: .bottom
                    )
                    
                    // Contact Owner - Phone
                    LabelValueRowView(model: .init(
                        label: L10n.PetDetails.Label.contactOwner,
                        value: petDetails.phone
                    ))
                    .accessibilityIdentifier("petDetails.phone.tap")
                    
                    // Contact Owner - Email
                    LabelValueRowView(model: .init(
                        label: L10n.PetDetails.Label.contactOwner,
                        value: petDetails.email ?? "—"
                    ))
                    .accessibilityIdentifier("petDetails.email.tap")
                    .padding(.bottom, 10.667)
                    .overlay(
                        Divider().background(Color.gray.opacity(0.2)),
                        alignment: .bottom
                    )
                    
                    // Animal Name & Microchip (2 columns)
                    HStack(spacing: 12) {
                        LabelValueRowView(model: .init(
                            label: L10n.PetDetails.Label.animalName,
                            value: viewModel.formattedPetName
                        ))
                        .accessibilityIdentifier("petDetails.name.field")
                        
                        LabelValueRowView(model: .init(
                            label: L10n.PetDetails.Label.microchipNumber,
                            value: viewModel.formattedMicrochip
                        ))
                        .accessibilityIdentifier("petDetails.microchip.field")
                    }
                    
                    // Species & Breed (2 columns)
                    HStack(spacing: 12) {
                        LabelValueRowView(model: .init(
                            label: L10n.PetDetails.Label.animalSpecies,
                            value: viewModel.formattedSpecies
                        ))
                        .accessibilityIdentifier("petDetails.species.field")
                        
                        LabelValueRowView(model: .init(
                            label: L10n.PetDetails.Label.animalRace,
                            value: petDetails.breed ?? "—"
                        ))
                        .accessibilityIdentifier("petDetails.breed.field")
                    }
                    
                    // Sex & Age (2 columns)
                    HStack(spacing: 12) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(L10n.PetDetails.Label.animalSex)
                                .font(.system(size: 16))
                                .foregroundColor(Color(hex: "#6a7282"))
                            
                            HStack(spacing: 8) {
                                Text(viewModel.genderSymbol)
                                    .font(.system(size: 20))
                                    .foregroundColor(Color(hex: "#155dfc"))
                                
                                Text(petDetails.gender.displayName)
                                    .font(.system(size: 16))
                                    .foregroundColor(Color(hex: "#101828"))
                            }
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityIdentifier("petDetails.sex.field")
                        
                        LabelValueRowView(model: .init(
                            label: L10n.PetDetails.Label.animalAge,
                            value: viewModel.formattedAge
                        ))
                        .accessibilityIdentifier("petDetails.age.field")
                    }
                    
                    // Place of Disappearance / Coordinates
                    VStack(alignment: .leading, spacing: 4) {
                        Text(L10n.PetDetails.Label.placeOfDisappearance)
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#6a7282"))
                        
                        HStack(spacing: 8) {
                            Image(systemName: "mappin.circle")
                                .font(.system(size: 20))
                                .foregroundColor(Color(hex: "#101828"))
                            
                            Text(viewModel.formattedCoordinates)
                                .font(.system(size: 16))
                                .foregroundColor(Color(hex: "#101828"))
                        }
                    }
                    .accessibilityIdentifier("petDetails.location.field")
                    
                    // Show on the map button (bordered, blue)
                    Button(action: viewModel.handleShowMap) {
                        Text(L10n.PetDetails.Button.showOnMap)
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#155dfc"))
                            .padding(.horizontal, 24)
                            .padding(.vertical, 8)
                            .frame(height: 40)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color(hex: "#155dfc"), lineWidth: 2)
                            )
                    }
                    .accessibilityIdentifier("petDetails.showMap.button")
                    
                    // Animal Additional Description
                    VStack(alignment: .leading, spacing: 4) {
                        Text(L10n.PetDetails.Label.additionalDescription)
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#6a7282"))
                        
                        Text(viewModel.formattedDescription)
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#101828"))
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .accessibilityIdentifier("petDetails.description.text")
                    
                    // Remove Report button (full width, red)
                    Button(action: viewModel.handleRemoveReport) {
                        Text(L10n.PetDetails.Button.removeReport)
                            .font(.system(size: 16))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(Color(hex: "#fb2c36"))
                            .cornerRadius(10)
                    }
                    .accessibilityIdentifier("petDetails.removeReport.button")
                }
                .padding(.horizontal, 23)
                .padding(.top, 20)
                .padding(.bottom, 30)
            }
        }
        .background(Color.white)
    }
    
}
