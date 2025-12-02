# Quick Start: iOS Owner's Details Screen

**Feature**: iOS Owner's Details Screen (Step 4/4)  
**Date**: 2025-12-02  
**Platform**: iOS (Swift + SwiftUI)

## Overview

This guide helps developers quickly set up and implement the iOS Owner's Details screen. The screen is Step 4/4 in the Missing Pet flow and modifies existing placeholder files.

## Prerequisites

Before starting, ensure you have:

- [ ] Xcode 15+ installed
- [ ] iOS 15+ simulator or device configured
- [ ] Completed spec 017 (Missing Pet flow architecture) - provides coordinator, FlowState, navigation integration
- [ ] Completed spec 031 (Animal Description screen) - provides ValidatedTextField component
- [ ] Completed spec 028 (Animal Photo screen) - provides PhotoAttachmentMetadata, PhotoAttachmentCache
- [ ] Access to backend API (spec 009: announcements, spec 021: photo upload)

## Project Setup

### 1. Clone and Open Project

```bash
cd /Users/msz/dev/ai-first/AI-First
open iosApp/iosApp.xcodeproj
```

### 2. Verify Existing Components

Check that these components exist from previous specs:

**From Spec 017** (Missing Pet Flow):
- `/iosApp/iosApp/Coordinators/ReportMissingPetCoordinator.swift` - navigation coordinator
- `/iosApp/iosApp/Features/ReportMissingPet/ReportMissingPetFlowState.swift` - session state container
- `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsView.swift` - **placeholder to modify**
- `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsViewModel.swift` - **placeholder to modify**

**From Spec 031** (Animal Description):
- `/iosApp/iosApp/Features/Shared/Components/ValidatedTextField.swift` - reusable text input with validation

**From Spec 028** (Animal Photo):
- `/iosApp/iosApp/Domain/Models/PhotoAttachmentMetadata.swift` - photo metadata
- `/iosApp/iosApp/Features/ReportMissingPet/PhotoAttachmentCache.swift` - disk cache manager

### 3. Install Dependencies

SwiftGen should already be configured (from spec 017 setup):

```bash
# Verify swiftgen.yml exists
cat iosApp/swiftgen.yml

# Generate localization files (if not auto-generated)
cd iosApp
swiftgen
```

## Implementation Steps

### Step 1: Add Localization Strings

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (English)

```strings
/* Owner's Details Screen */
"owners_details.screen_title" = "Owner's details";
"owners_details.subtitle" = "Add your contact information and potential reward.";
"owners_details.phone.label" = "Phone number";
"owners_details.phone.placeholder" = "Enter phone number";
"owners_details.phone.error" = "Enter at least 7 digits";
"owners_details.email.label" = "Email";
"owners_details.email.placeholder" = "Enter email address";
"owners_details.email.error" = "Enter a valid email address";
"owners_details.reward.label" = "Reward (optional)";
"owners_details.reward.placeholder" = "Describe reward";
"owners_details.continue.button" = "Continue";
"owners_details.back.button" = "Back";

/* Error Alerts */
"owners_details.error.no_connection.title" = "No connection";
"owners_details.error.no_connection.message" = "Please check your network and try again.";
"owners_details.error.generic.title" = "Something went wrong";
"owners_details.error.generic.message" = "Please try again later.";
"owners_details.alert.try_again" = "Try Again";
"owners_details.alert.cancel" = "Cancel";
```

**File**: `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (Polish)

```strings
/* Owner's Details Screen */
"owners_details.screen_title" = "Dane właściciela";
"owners_details.subtitle" = "Dodaj swoje dane kontaktowe i ewentualną nagrodę.";
"owners_details.phone.label" = "Numer telefonu";
"owners_details.phone.placeholder" = "Wpisz numer telefonu";
"owners_details.phone.error" = "Wpisz co najmniej 7 cyfr";
"owners_details.email.label" = "Email";
"owners_details.email.placeholder" = "Wpisz adres email";
"owners_details.email.error" = "Wpisz poprawny adres email";
"owners_details.reward.label" = "Nagroda (opcjonalnie)";
"owners_details.reward.placeholder" = "Opisz nagrodę";
"owners_details.continue.button" = "Dalej";
"owners_details.back.button" = "Wstecz";

/* Error Alerts */
"owners_details.error.no_connection.title" = "Brak połączenia";
"owners_details.error.no_connection.message" = "Sprawdź połączenie i spróbuj ponownie.";
"owners_details.error.generic.title" = "Coś poszło nie tak";
"owners_details.error.generic.message" = "Spróbuj ponownie później.";
"owners_details.alert.try_again" = "Spróbuj ponownie";
"owners_details.alert.cancel" = "Anuluj";
```

Run SwiftGen to generate `L10n` enum:

```bash
cd iosApp
swiftgen
```

### Step 2: Extend AnimalRepositoryProtocol

**File**: `/iosApp/iosApp/Domain/Repositories/AnimalRepositoryProtocol.swift` (MODIFY EXISTING)

Add new methods for announcement operations:

```swift
protocol AnimalRepositoryProtocol {
    // ... existing methods (getAnimals, getPetDetails) ...
    
    /// Creates a new missing pet announcement
    func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse
    
    /// Uploads photo for an existing announcement
    func uploadPhoto(request: PhotoUploadRequest) async throws
}
```

**File**: `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (MODIFY EXISTING)

Add implementations for new methods:

```swift
class AnimalRepository: AnimalRepositoryProtocol {
    // ... existing methods (getAnimals, getPetDetails) ...
    
    func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse {
        // POST /api/v1/announcements implementation
        // See data-model.md for complete code
    }
    
    func uploadPhoto(request: PhotoUploadRequest) async throws {
        // POST /api/v1/announcements/:id/photos implementation
        // See data-model.md for complete code
    }
}
```

See [data-model.md](./data-model.md) for full implementation (URLSession HTTP calls).

**Note**: We're extending existing AnimalRepository instead of creating new AnnouncementRepository. Naming will be refactored later.

---

### Step 3: Create Announcement Submission Service

**File**: `/iosApp/iosApp/Domain/Services/AnnouncementSubmissionService.swift`

```swift
import Foundation

/// Orchestrates 2-step announcement submission (create + photo upload)
class AnnouncementSubmissionService {
    private let repository: AnimalRepositoryProtocol
    
    init(repository: AnimalRepositoryProtocol) {
        self.repository = repository
    }
    
    /// Submits complete announcement with photo
    /// - Parameter flowState: ReportMissingPetFlowState with all data from Steps 1-4
    /// - Returns: managementPassword for summary screen
    /// - Throws: NetworkError on submission failure
    func submitAnnouncement(flowState: ReportMissingPetFlowState) async throws -> String {
        // Build request from FlowState
        let request = buildAnnouncementRequest(from: flowState)
        
        // Step 1: Create announcement
        let response = try await repository.createAnnouncement(request: request)
        
        // Step 2: Upload photo (if exists)
        if let photoAttachment = flowState.photoAttachment {
            let uploadRequest = PhotoUploadRequest(
                announcementId: response.id,
                photo: photoAttachment,
                managementPassword: response.managementPassword
            )
            try await repository.uploadPhoto(request: uploadRequest)
        }
        
        // Return managementPassword for summary
        return response.managementPassword
    }
    
    private func buildAnnouncementRequest(from flowState: ReportMissingPetFlowState) -> CreateAnnouncementRequest {
        guard let contactDetails = flowState.contactDetails,
              let species = flowState.animalSpecies,
              let gender = flowState.animalGender,
              let disappearanceDate = flowState.disappearanceDate else {
            fatalError("Required fields must be set before submission")
        }
        
        return CreateAnnouncementRequest(
            species: species.rawValue.uppercased(),
            sex: gender.rawValue.uppercased(),
            lastSeenDate: ISO8601DateFormatter().string(from: disappearanceDate),
            locationLatitude: flowState.animalLatitude ?? 0.0,
            locationLongitude: flowState.animalLongitude ?? 0.0,
            email: contactDetails.email.trimmingCharacters(in: .whitespaces),
            phone: contactDetails.phone.filter { $0.isNumber || $0 == "+" },  // Sanitize
            status: "MISSING",
            microchipNumber: flowState.chipNumber,
            description: flowState.animalAdditionalDescription,
            reward: contactDetails.rewardDescription
        )
    }
}
```

See [data-model.md](./data-model.md) for complete implementation details.

### Step 4: Modify ContactDetailsViewModel

**File**: `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsViewModel.swift`

Replace placeholder content with full implementation (see [data-model.md](./data-model.md) for complete code).

**Key Changes**:
- Add `@Published` properties: `phone`, `email`, `rewardDescription`, `phoneError`, `emailError`, `isSubmitting`, `alertMessage`, `showAlert`
- Add initializer with manual DI: `init(submissionService: AnnouncementSubmissionService, flowState: ReportMissingPetFlowState)`
- Add validation computed properties: `isPhoneValid`, `isEmailValid`
- Implement `submitForm() async` method that delegates to service (NOT repository directly)

### Step 5: Modify ContactDetailsView

**File**: `/iosApp/iosApp/Features/ReportMissingPet/ContactDetails/Views/ContactDetailsView.swift`

Replace placeholder content with SwiftUI UI implementation.

**Layout Structure**:
```swift
struct ContactDetailsView: View {
    @ObservedObject var viewModel: ContactDetailsViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Title + Subtitle
            VStack(alignment: .leading, spacing: 8) {
                Text(L10n.tr("owners_details.screen_title"))
                    .font(.title2)
                    .bold()
                    .accessibilityIdentifier("ownersDetails.title")
                
                Text(L10n.tr("owners_details.subtitle"))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .accessibilityIdentifier("ownersDetails.subtitle")
            }
            .padding(.horizontal, 16)
            .padding(.top, 16)
            
            // Form fields
            ScrollView {
                VStack(spacing: 16) {
                    ValidatedTextField(
                        label: L10n.tr("owners_details.phone.label"),
                        placeholder: L10n.tr("owners_details.phone.placeholder"),
                        text: $viewModel.phone,
                        error: viewModel.phoneError
                    )
                    .accessibilityIdentifier("ownersDetails.phoneInput")
                    
                    ValidatedTextField(
                        label: L10n.tr("owners_details.email.label"),
                        placeholder: L10n.tr("owners_details.email.placeholder"),
                        text: $viewModel.email,
                        error: viewModel.emailError
                    )
                    .accessibilityIdentifier("ownersDetails.emailInput")
                    
                    ValidatedTextField(
                        label: L10n.tr("owners_details.reward.label"),
                        placeholder: L10n.tr("owners_details.reward.placeholder"),
                        text: $viewModel.rewardDescription,
                        error: nil,
                        maxLength: 120
                    )
                    .accessibilityIdentifier("ownersDetails.rewardInput")
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
                    Text(L10n.tr("owners_details.continue.button"))
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 50)
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(8)
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
            .disabled(viewModel.isSubmitting)
            .accessibilityIdentifier("ownersDetails.continueButton")
        }
        .alert(isPresented: $viewModel.showAlert) {
            Alert(
                title: Text(viewModel.alertMessage ?? ""),
                primaryButton: .default(Text(L10n.tr("owners_details.alert.try_again"))) {
                    Task {
                        await viewModel.submitForm()
                    }
                },
                secondaryButton: .cancel(Text(L10n.tr("owners_details.alert.cancel")))
            )
        }
    }
}
```

### Step 6: Update ReportMissingPetCoordinator

**File**: `/iosApp/iosApp/Coordinators/ReportMissingPetCoordinator.swift`

Update coordinator to inject service into ContactDetailsViewModel during initialization:

```swift
func showContactDetails() {
    let submissionService = ServiceContainer.shared.announcementSubmissionService
    let viewModel = ContactDetailsViewModel(submissionService: submissionService, flowState: flowState)
    
    viewModel.onReportSent = { [weak self] managementPassword in
        self?.showSummary(managementPassword: managementPassword)
    }
    
    let view = ContactDetailsView(viewModel: viewModel)
    let hostingController = UIHostingController(rootView: NavigationBackHiding { view })
    navigationController.pushViewController(hostingController, animated: true)
}
```

### Step 7: Update ServiceContainer

**File**: `/iosApp/iosApp/DI/ServiceContainer.swift`

Add `announcementRepository` and `announcementSubmissionService` to dependency container:

```swift
class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var httpClient: HTTPClient = URLSession.shared
    
    // Repository (data layer) - ALREADY EXISTS
    lazy var animalRepository: AnimalRepositoryProtocol = AnimalRepository(
        httpClient: httpClient
    )
    
    // Service (business logic layer) - receives repository in constructor
    lazy var announcementSubmissionService: AnnouncementSubmissionService = AnnouncementSubmissionService(
        repository: animalRepository
    )
    
    // ... other dependencies
}
```

**DI Chain**: AnimalRepository → AnnouncementSubmissionService → ContactDetailsViewModel

**Note**: We reuse existing `animalRepository` instead of creating new `announcementRepository`.

## Running the App

### 1. Build and Run

```bash
# Open Xcode
open iosApp/iosApp.xcodeproj

# Select iOS Simulator (iPhone 15, iOS 17+)
# Build and run: Cmd+R
```

### 2. Navigate to Owner's Details Screen

To test the screen in isolation:

1. Run the app
2. Navigate to Missing Pet flow
3. Complete Steps 1-3 (chip, photo, description)
4. Step 4 (Owner's Details) should appear

**Deep Link for Testing** (if implemented):
```swift
// In AppDelegate or SceneDelegate
func deepLinkToContactDetails() {
    let flowState = ReportMissingPetFlowState.mockWithSteps1To3Completed()
    let coordinator = ReportMissingPetCoordinator(navigationController: navigationController, flowState: flowState)
    coordinator.showContactDetails()
}
```

### 3. Test Scenarios

**Happy Path**:
1. Enter valid phone: `+48123456789`
2. Enter valid email: `owner@example.com`
3. Tap Continue
4. Verify 2-step submission (check Xcode console logs)
5. Verify navigation to summary with managementPassword

**Validation Errors**:
1. Enter invalid phone: `123` (< 7 digits)
2. Tap Continue
3. Verify inline error appears: "Enter at least 7 digits"
4. Correct phone to `+48123456789`
5. Tap Continue again
6. Verify submission succeeds

**Network Errors**:
1. Disable network (Airplane mode in simulator)
2. Enter valid phone and email
3. Tap Continue
4. Verify alert popup: "No connection. Please check your network and try again."
5. Tap "Try Again"
6. Verify submission retries

## Testing

### 1. Unit Tests

**File**: `/iosAppTests/Features/ReportMissingPet/ContactDetails/ContactDetailsViewModelTests.swift`

```bash
# Run unit tests
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES

# View coverage report
open ~/Library/Developer/Xcode/DerivedData/iosApp-*/Logs/Test/*.xcresult
```

**Test Cases for ContactDetailsViewModel**:
- Phone validation (valid: 7-11 digits, invalid: < 7 digits)
- Email validation (valid: local@domain.tld, invalid: missing @)
- submitForm() success flow (calls service, receives managementPassword, invokes onReportSent)
- submitForm() failure flow (service throws error, shows alert)
- Loading state management (isSubmitting = true during submission, false after)
- Alert state management (showAlert = true on error, correct alertMessage)

**Test Cases for AnnouncementSubmissionService**:
- buildAnnouncementRequest() builds correct request from FlowState
- submitAnnouncement() orchestrates 2-step flow (announcement + photo)
- submitAnnouncement() returns managementPassword on success
- submitAnnouncement() throws error on step 1 failure
- submitAnnouncement() throws error on step 2 failure
- Photo upload skipped when photoAttachment is nil

**Test Doubles**:
- `FakeAnimalRepository`: Mock repository for service tests (extends existing FakeAnimalRepository if it exists)
- `FakeAnnouncementSubmissionService`: Mock service for ViewModel tests

### 2. E2E Tests

**File**: `/e2e-tests/mobile/specs/owners-details.spec.ts`

```bash
# Run E2E tests for iOS
npm run test:mobile:ios -- --spec owners-details
```

## Debugging

### Common Issues

**Issue**: SwiftGen `L10n` not found
- **Solution**: Run `swiftgen` in `iosApp/` directory, rebuild project

**Issue**: ValidatedTextField not found
- **Solution**: Verify spec 031 (Animal Description) is completed, check file exists at `/iosApp/iosApp/Features/Shared/Components/ValidatedTextField.swift`

**Issue**: PhotoAttachmentMetadata not found
- **Solution**: Verify spec 028 (Animal Photo) is completed, check file exists at `/iosApp/iosApp/Domain/Models/PhotoAttachmentMetadata.swift`

**Issue**: Backend API returns 401 on photo upload
- **Solution**: Verify Basic auth header is correct: `Authorization: Basic <base64(announcementId:managementPassword)>`

**Issue**: Submission fails with "invalid response"
- **Solution**: Check backend API is running, verify endpoint URLs in repository implementation

### Logging

Add logging to repository methods for debugging:

```swift
// In AnimalRepository
func createAnnouncement(request: CreateAnnouncementRequest) async throws -> AnnouncementResponse {
    print("[AnimalRepository] Creating announcement: \(request)")
    
    let response = try await httpClient.data(for: urlRequest)
    
    print("[AnimalRepository] Response: \(response)")
    
    return try decoder.decode(AnnouncementResponse.self, from: data)
}
```

## Next Steps

After completing this implementation:

1. [ ] Run unit tests and verify 80% coverage
2. [ ] Run E2E tests and verify all user stories pass
3. [ ] Test on physical device (not just simulator)
4. [ ] Verify localization for Polish and English
5. [ ] Test with backend API integration
6. [ ] Submit PR for review

## References

- [Feature Spec](./spec.md) - full requirements and user stories
- [Data Model](./data-model.md) - Swift models and ViewModel structure
- [API Contracts](./contracts/api-contracts.md) - backend API details
- [Research](./research.md) - technical decisions and rationale
- [Spec 017: Missing Pet Flow](../017-ios-missing-pet-flow/spec.md) - coordinator, FlowState
- [Spec 031: Animal Description](../031-ios-animal-description-screen/spec.md) - ValidatedTextField component
- [Spec 028: Animal Photo](../028-ios-animal-photo-screen/spec.md) - PhotoAttachmentMetadata
- [Constitution](../../.specify/memory/constitution.md) - iOS MVVM-C architecture

## Support

For questions or issues:
- Check existing implementation in spec 031 (Animal Description) for similar patterns
- Review constitution for iOS MVVM-C requirements
- Consult backend API contracts in spec 009 and 021

