import SwiftUI

/// Main view for Pet Details Screen.
/// Displays comprehensive pet information with loading and error states.
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
            VStack(alignment: .leading, spacing: 20) {
                // Pet Photo with Badges
                PetPhotoWithBadges(model: PetPhotoWithBadgesModel(from: petDetails))
                
                // Identification Information Section
                identificationSection(petDetails: petDetails)
                    .padding(.horizontal)
                
                // Location and Contact Section
                locationContactSection(petDetails: petDetails)
                    .padding(.horizontal)
                
                // Description Section
                descriptionSection(petDetails: petDetails)
                    .padding(.horizontal)
                
                // Remove Report Button (footer)
                removeReportButton
                    .padding()
                
                Spacer()
            }
        }
    }
    
    // MARK: - Identification Section
    
    private func identificationSection(petDetails: PetDetails) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            // Section Header
            Text("Identification")
                .font(.headline)
                .padding(.bottom, 8)
            
            // Microchip Number
            LabelValueRow(model: LabelValueRowModel(
                label: "Microchip",
                value: formatMicrochip(petDetails.microchipNumber)
            ))
            .accessibilityIdentifier("petDetails.microchip.field")
            
            Divider()
            
            // Species and Breed (two-column layout)
            LabelValueRow(model: LabelValueRowModel(
                label: "Species",
                value: formatSpecies(petDetails.species)
            ))
            .accessibilityIdentifier("petDetails.species.field")
            
            Divider()
            
            LabelValueRow(model: LabelValueRowModel(
                label: "Breed",
                value: petDetails.breed ?? "—"
            ))
            .accessibilityIdentifier("petDetails.breed.field")
            
            Divider()
            
            // Sex
            LabelValueRow(model: LabelValueRowModel(
                label: "Sex",
                value: petDetails.gender,
                valueProcessor: formatGender
            ))
            .accessibilityIdentifier("petDetails.sex.field")
            
            Divider()
            
            // Age
            LabelValueRow(model: LabelValueRowModel(
                label: "Approximate Age",
                value: petDetails.approximateAge ?? "—"
            ))
            .accessibilityIdentifier("petDetails.age.field")
            
            Divider()
            
            // Date of Disappearance
            LabelValueRow(model: LabelValueRowModel(
                label: "Date of Disappearance",
                value: formatDate(petDetails.lastSeenDate)
            ))
            .accessibilityIdentifier("petDetails.date.field")
        }
    }
    
    // MARK: - Location and Contact Section
    
    private func locationContactSection(petDetails: PetDetails) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            // Section Header
            Text("Location & Contact")
                .font(.headline)
                .padding(.bottom, 8)
            
            // Location
            LabelValueRow(model: LabelValueRowModel(
                label: "Location",
                value: petDetails.location
            ))
            .accessibilityIdentifier("petDetails.location.field")
            
            Divider()
            
            // Radius
            LabelValueRow(model: LabelValueRowModel(
                label: "Radius",
                value: formatRadius(petDetails.locationRadius)
            ))
            .accessibilityIdentifier("petDetails.radius.field")
            
            Divider()
            
            // Show on the map button
            Button(action: handleShowMap) {
                HStack {
                    Image(systemName: "map")
                    Text("Show on the map")
                }
                .font(.subheadline)
                .foregroundColor(.blue)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
            }
            .accessibilityIdentifier("petDetails.showMap.button")
            
            Divider()
            
            // Phone
            LabelValueRow(model: LabelValueRowModel(
                label: "Phone",
                value: petDetails.phone,
                onTap: { handlePhoneTap(petDetails.phone) }
            ))
            .accessibilityIdentifier("petDetails.phone.tap")
            
            Divider()
            
            // Email
            LabelValueRow(model: LabelValueRowModel(
                label: "Email",
                value: petDetails.email ?? "—",
                onTap: petDetails.email != nil ? { handleEmailTap(petDetails.email!) } : nil
            ))
            .accessibilityIdentifier("petDetails.email.tap")
        }
    }
    
    // MARK: - Description Section
    
    private func descriptionSection(petDetails: PetDetails) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            // Section Header
            Text("Additional Information")
                .font(.headline)
            
            // Description Text
            Text(petDetails.description)
                .font(.subheadline)
                .foregroundColor(.primary)
                .fixedSize(horizontal: false, vertical: true)
                .accessibilityIdentifier("petDetails.description.text")
        }
    }
    
    // MARK: - Remove Report Button
    
    private var removeReportButton: some View {
        Button(action: handleRemoveReport) {
            Text("Remove Report")
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                .background(Color.red)
                .cornerRadius(12)
        }
        .accessibilityIdentifier("petDetails.removeReport.button")
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
    
    private func formatRadius(_ radius: Int?) -> String {
        guard let radius = radius else { return "—" }
        return "±\(radius) km"
    }
    
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
    
    private func formatGender(_ gender: String) -> String {
        switch gender.uppercased() {
        case "MALE":
            return "♂ Male"
        case "FEMALE":
            return "♀ Female"
        case "UNKNOWN":
            return "? Unknown"
        default:
            return gender
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
                    petName: "Preview Pet",
                    photoUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
                    status: "ACTIVE",
                    lastSeenDate: "2025-11-20",
                    species: "DOG",
                    gender: "MALE",
                    description: "Test description",
                    location: "Warsaw",
                    phone: "+48 123 456 789",
                    email: "test@example.com",
                    breed: "York",
                    locationRadius: 5,
                    microchipNumber: "123-456-789",
                    approximateAge: "3 years",
                    reward: "$500",
                    createdAt: "2025-11-20T10:00:00Z",
                    updatedAt: "2025-11-20T10:00:00Z"
                )
            }
        }
        
        let viewModel = PetDetailsViewModel(repository: MockRepository(), petId: "preview-id")
        
        return NavigationView {
            PetDetailsView(viewModel: viewModel)
                .navigationTitle("Pet Details")
        }
    }
}
#endif

