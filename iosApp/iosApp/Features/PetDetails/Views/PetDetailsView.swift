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
        VStack(spacing: 20) {
            ProgressView()
                .accessibilityIdentifier("petDetails.loading")
            Text("Loading pet details...")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
    
    // MARK: - Error State
    
    private func errorView(message: String) -> some View {
        VStack(spacing: 20) {
            Image(systemName: "exclamationmark.triangle")
                .font(.largeTitle)
                .foregroundColor(.red)
            
            Text("Failed to load pet details")
                .font(.headline)
            
            Text(message)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
                .accessibilityIdentifier("petDetails.error.message")
            
            Button("Retry") {
                viewModel.retry()
            }
            .buttonStyle(.borderedProminent)
            .accessibilityIdentifier("petDetails.retry.button")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding()
    }
    
    // MARK: - Loaded State
    
    private func detailsView(petDetails: PetDetails) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                // Pet Photo with Badges and Back Button
                PetPhotoWithBadges(
                    model: PetPhotoWithBadgesModel(from: petDetails),
                    onBack: { viewModel.handleBack() }
                )
                
                // Content Container with padding
                VStack(alignment: .leading, spacing: 20) {
                    // Date of Disappearance (full width)
                    LabelValueRow(model: LabelValueRowModel(
                        label: "Date of Disappearance",
                        value: formatDate(petDetails.lastSeenDate)
                    ))
                    .accessibilityIdentifier("petDetails.date.field")
                    .padding(.bottom, 10.667)
                    .overlay(
                        Divider().background(Color.gray.opacity(0.2)),
                        alignment: .bottom
                    )
                    
                    // Contact Owner - Phone
                    LabelValueRow(model: LabelValueRowModel(
                        label: "Contact owner",
                        value: petDetails.phone,
                        onTap: { handlePhoneTap(petDetails.phone) }
                    ))
                    .accessibilityIdentifier("petDetails.phone.tap")
                    
                    // Contact Owner - Email
                    LabelValueRow(model: LabelValueRowModel(
                        label: "Contact owner",
                        value: petDetails.email ?? "—",
                        onTap: petDetails.email != nil ? { handleEmailTap(petDetails.email!) } : nil
                    ))
                    .accessibilityIdentifier("petDetails.email.tap")
                    .padding(.bottom, 10.667)
                    .overlay(
                        Divider().background(Color.gray.opacity(0.2)),
                        alignment: .bottom
                    )
                    
                    // Animal Name & Microchip (2 columns)
                    HStack(spacing: 12) {
                        LabelValueRow(model: LabelValueRowModel(
                            label: "Animal Name",
                            value: petDetails.petName
                        ))
                        .accessibilityIdentifier("petDetails.name.field")
                        
                        LabelValueRow(model: LabelValueRowModel(
                            label: "Microchip number",
                            value: formatMicrochip(petDetails.microchipNumber)
                        ))
                        .accessibilityIdentifier("petDetails.microchip.field")
                    }
                    
                    // Species & Breed (2 columns)
                    HStack(spacing: 12) {
                        LabelValueRow(model: LabelValueRowModel(
                            label: "Animal Species",
                            value: formatSpecies(petDetails.species)
                        ))
                        .accessibilityIdentifier("petDetails.species.field")
                        
                        LabelValueRow(model: LabelValueRowModel(
                            label: "Animal Race",
                            value: petDetails.breed ?? "—"
                        ))
                        .accessibilityIdentifier("petDetails.breed.field")
                    }
                    
                    // Sex & Age (2 columns)
                    HStack(spacing: 12) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Animal Sex")
                                .font(.system(size: 16))
                                .foregroundColor(Color(hex: "#6a7282"))
                            
                            HStack(spacing: 8) {
                                Image(systemName: genderIcon(petDetails.gender))
                                    .font(.system(size: 20))
                                    .foregroundColor(Color(hex: "#155dfc"))
                                
                                Text(petDetails.gender.capitalized)
                                    .font(.system(size: 16))
                                    .foregroundColor(Color(hex: "#101828"))
                            }
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityIdentifier("petDetails.sex.field")
                        
                        LabelValueRow(model: LabelValueRowModel(
                            label: "Animal Approx. Age",
                            value: petDetails.approximateAge ?? "—"
                        ))
                        .accessibilityIdentifier("petDetails.age.field")
                    }
                    
                    // Place of Disappearance / City
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Place of Disappearance / City")
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#6a7282"))
                        
                        HStack(spacing: 8) {
                            Image(systemName: "mappin.circle")
                                .font(.system(size: 20))
                                .foregroundColor(Color(hex: "#101828"))
                            
                            Text(petDetails.location)
                                .font(.system(size: 16))
                                .foregroundColor(Color(hex: "#101828"))
                            
                            if let radius = petDetails.locationRadius {
                                Text("•")
                                    .font(.system(size: 16))
                                    .foregroundColor(Color(hex: "#6a7282"))
                                
                                Text("±\(radius) km")
                                    .font(.system(size: 16))
                                    .foregroundColor(Color(hex: "#4a5565"))
                            }
                        }
                    }
                    .accessibilityIdentifier("petDetails.location.field")
                    
                    // Show on the map button (bordered, blue)
                    Button(action: handleShowMap) {
                        Text("Show on the map")
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
                    
                    // Vaccination ID
                    LabelValueRow(model: LabelValueRowModel(
                        label: "Vaccination ID",
                        value: petDetails.vaccinationId ?? "—"
                    ))
                    .accessibilityIdentifier("petDetails.vaccination.field")
                    
                    // Animal Additional Description
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Animal Additional Description")
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#6a7282"))
                        
                        Text(petDetails.description)
                            .font(.system(size: 16))
                            .foregroundColor(Color(hex: "#101828"))
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .accessibilityIdentifier("petDetails.description.text")
                    
                    // Remove Report button (full width, red)
                    Button(action: handleRemoveReport) {
                        Text("Remove Report")
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
    
    // MARK: - Action Handlers
    
    private func handleRemoveReport() {
        print("Remove Report button tapped (placeholder)")
        // TODO: Implement report removal in future feature
    }
    
    private func handleShowMap() {
        print("Show on the map button tapped (placeholder)")
        // TODO: Implement map view navigation in future feature
    }
    
    private func handlePhoneTap(_ phone: String) {
        guard let url = URL(string: "tel://\(phone.filter { !$0.isWhitespace })") else {
            print("Invalid phone number")
            return
        }
        
        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url)
        } else {
            print("Cannot open dialer")
        }
    }
    
    private func handleEmailTap(_ email: String) {
        guard let url = URL(string: "mailto:\(email)") else {
            print("Invalid email address")
            return
        }
        
        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url)
        } else {
            print("Cannot open mail composer")
        }
    }
    
    // MARK: - Formatting Helpers
    
    private func formatMicrochip(_ microchip: String?) -> String {
        guard let microchip = microchip else { return "—" }
        
        // Format as 000-000-000-000 if it's a plain number
        let digits = microchip.filter { $0.isNumber }
        guard digits.count >= 12 else { return microchip }
        
        let formatted = digits.enumerated().map { index, char -> String in
            if index > 0 && index % 3 == 0 && index < 12 {
                return "-\(char)"
            }
            return String(char)
        }.joined()
        
        return formatted
    }
    
    private func formatSpecies(_ species: String) -> String {
        return species.capitalized
    }
    
    private func genderIcon(_ gender: String) -> String {
        switch gender.uppercased() {
        case "MALE":
            return "arrow.up.right"
        case "FEMALE":
            return "arrow.down.right"
        default:
            return "questionmark"
        }
    }
    
    private func formatDate(_ dateString: String) -> String {
        // Input format: YYYY-MM-DD (e.g., "2025-11-18")
        // Output format: MMM DD, YYYY (e.g., "Nov 18, 2025")
        
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = inputFormatter.date(from: dateString) else {
            return dateString // Return as-is if parsing fails
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM dd, yyyy"
        
        return outputFormatter.string(from: date)
    }
}

// MARK: - Previews

#if DEBUG
struct PetDetailsView_Previews: PreviewProvider {
    static var previews: some View {
        // Mock repository for preview
        class MockRepository: AnimalRepositoryProtocol {
            func getAnimals() async throws -> [Animal] {
                return []
            }
            
            func getPetDetails(id: String) async throws -> PetDetails {
                return PetDetails(
                    id: "preview-id",
                    petName: "Max",
                    photoUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
                    status: "ACTIVE",
                    lastSeenDate: "2025-11-20",
                    species: "DOG",
                    gender: "MALE",
                    description: "Friendly and energetic golden retriever looking for a loving home. Great with kids and other pets.",
                    location: "Warsaw",
                    phone: "+48 123 456 789",
                    email: "test@example.com",
                    breed: "Doberman",
                    locationRadius: 15,
                    microchipNumber: "000-000-000-000",
                    approximateAge: "3 years",
                    reward: "500 PLN",
                    vaccinationId: "VAC-2023-001234",
                    createdAt: "2025-11-20T10:00:00Z",
                    updatedAt: "2025-11-20T10:00:00Z"
                )
            }
        }
        
        let viewModel = PetDetailsViewModel(repository: MockRepository(), petId: "preview-id")
        
        return NavigationView {
            PetDetailsView(viewModel: viewModel)
                .navigationBarHidden(true)
        }
    }
}
#endif
