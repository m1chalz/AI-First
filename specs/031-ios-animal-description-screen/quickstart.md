# Quickstart: Animal Description Screen (iOS)

**Feature**: 031-ios-animal-description-screen  
**Date**: November 28, 2025  
**Purpose**: Developer setup and testing guide

---

## Prerequisites

- **Xcode**: 15.0+ (for iOS 18 SDK and Swift 5.9+)
- **iOS Simulator**: iPhone 15 with iOS 18.0+ (or physical device)
- **SwiftGen**: Installed globally (for localization string generation)
  ```bash
  brew install swiftgen
  ```
- **Git branch**: `031-ios-animal-description-screen`

---

## Setup Steps

### 1. Checkout Feature Branch

```bash
cd /Users/msz/dev/ai-first/AI-First
git checkout 031-ios-animal-description-screen
```

### 2. Open iOS Project in Xcode

```bash
open iosApp/iosApp.xcodeproj
```

### 3. Verify Existing Dependencies

This feature reuses existing components:
- **LocationService**: `/iosApp/iosApp/Data/LocationService.swift` (already exists)
- **LocationServiceProtocol**: `/iosApp/iosApp/Domain/Services/LocationServiceProtocol.swift` (already exists)
- **MissingPetFlowSession**: `/iosApp/iosApp/Features/ReportMissingPet/Session/MissingPetFlowSession.swift` (already exists per spec 017)
- **ServiceContainer**: `/iosApp/iosApp/DI/ServiceContainer.swift` (already exists)

No new external dependencies required.

### 4. Generate Localized Strings

**Note**: This step assumes localization keys will be added to `Localizable.strings` during implementation.

Run SwiftGen to regenerate localized string accessors:

```bash
cd iosApp
swiftgen
```

This updates `/iosApp/iosApp/Generated/Strings.swift` with `L10n` enum accessors.

### 5. Build Project

**Build command** (from Xcode):
- Select `iosApp` scheme
- Select iOS Simulator target (e.g., iPhone 15, iOS 18.0)
- Press `Cmd+B` to build

**Build command** (from terminal):

```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -sdk iphonesimulator \
  -configuration Debug \
  build
```

Expected output: `Build Succeeded`

---

## Development Workflow

### 1. Create Domain Models

**Order**:
1. `/iosApp/iosApp/Domain/Models/Gender.swift` (enum)
2. `/iosApp/iosApp/Domain/Models/SpeciesTaxonomyOption.swift` (struct)
3. `/iosApp/iosApp/Data/SpeciesTaxonomy.swift` (static data)
4. `/iosApp/iosApp/Domain/Models/AnimalDescriptionDetails.swift` (session data struct)

**Testing**: Create unit tests in `/iosApp/iosAppTests/Domain/` for validation rules (if complex).

### 2. Create Reusable Form Components

**Order** (Model pattern - no ViewModel, pure presentation):
1. `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/ValidatedTextField.swift` ← **Create first** (base component)
2. `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/DropdownView.swift`
3. `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/SelectorView.swift`
4. `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/LocationCoordinateView.swift` ← **Composes 2x ValidatedTextField**
5. `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/TextAreaView.swift`

**Note**: Date picker uses native SwiftUI `DatePicker` (no custom component needed)

**Component Template** (Model pattern):

```swift
import SwiftUI

struct ComponentName: View {
    let model: Model
    @Binding var value: ValueType
    
    var body: some View {
        // Pure presentation code
    }
}

extension ComponentName {
    struct Model {
        let label: String
        let errorMessage: String?
        let accessibilityID: String
        // ... other properties
    }
}
```

### 3. Create ViewModel

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionViewModel.swift`

**Template** (iOS MVVM-C):

```swift
import Foundation
import SwiftUI

@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    // MARK: - Published Properties (Field Values)
    @Published var disappearanceDate: Date = Date()
    @Published var selectedSpecies: SpeciesTaxonomyOption?
    @Published var race: String = ""
    @Published var selectedGender: Gender?
    @Published var age: String = ""
    @Published var latitude: String = ""
    @Published var longitude: String = ""
    @Published var additionalDescription: String = ""
    @Published var showPermissionDeniedAlert = false
    @Published var showToast = false
    @Published var toastMessage = ""
    
    // MARK: - Published Properties (Validation Errors)
    @Published var speciesErrorMessage: String?
    @Published var raceErrorMessage: String?
    @Published var genderErrorMessage: String?
    @Published var ageErrorMessage: String?
    @Published var latitudeErrorMessage: String?
    @Published var longitudeErrorMessage: String?
    
    // MARK: - Computed Properties (Component Models)
    
    /// Model for race text field (combines current validation state).
    var raceTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.raceLabel,
            placeholder: L10n.racePlaceholder,
            errorMessage: raceErrorMessage,  // ← from @Published property
            isDisabled: selectedSpecies == nil,  // disabled until species selected
            keyboardType: .default,
            accessibilityID: "animalDescription.raceTextField.input"
        )
    }
    
    /// Model for species dropdown (combines current validation state).
    var speciesDropdownModel: DropdownView.Model {
        DropdownView.Model(
            label: L10n.speciesLabel,
            placeholder: L10n.speciesPlaceholder,
            options: SpeciesTaxonomy.options.map { $0.displayName },
            errorMessage: speciesErrorMessage,  // ← from @Published property
            accessibilityID: "animalDescription.speciesDropdown.tap"
        )
    }
    
    // ... other computed models for components
    
    // MARK: - Coordinator Callbacks
    var onContinue: (() -> Void)?
    var onBack: (() -> Void)?
    var onOpenAppSettings: (() -> Void)?
    
    // MARK: - Dependencies
    private let session: MissingPetFlowSession
    private let locationService: LocationServiceProtocol
    
    // MARK: - Initialization (Manual DI)
    init(session: MissingPetFlowSession, locationService: LocationServiceProtocol) {
        self.session = session
        self.locationService = locationService
        
        // Load session data if returning from Step 4
        if let existingData = session.animalDescription {
            self.disappearanceDate = existingData.disappearanceDate
            self.selectedSpecies = existingData.species
            self.race = existingData.race
            self.selectedGender = existingData.gender
            self.age = existingData.age.map(String.init) ?? ""
            self.latitude = existingData.latitude.map { String(format: "%.5f", $0) } ?? ""
            self.longitude = existingData.longitude.map { String(format: "%.5f", $0) } ?? ""
            self.additionalDescription = existingData.additionalDescription ?? ""
        }
    }
    
    // MARK: - User Actions
    func selectSpecies(_ species: SpeciesTaxonomyOption) {
        selectedSpecies = species
        // Clear race field when species changes (per spec)
        race = ""
    }
    
    func requestGPSPosition() async {
        // Implementation: check permission, request if needed, fetch location
    }
    
    func onContinueTapped() {
        // Implementation: validate, show errors or navigate
    }
    
    // ... other methods
}
```

### 4. Create Main View

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionView.swift`

**Template**:

```swift
import SwiftUI

struct AnimalDescriptionView: View {
    @ObservedObject var viewModel: AnimalDescriptionViewModel
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Date picker (native SwiftUI DatePicker)
                VStack(alignment: .leading, spacing: 4) {
                    Text(L10n.dateOfDisappearanceLabel)
                    DatePicker(
                        "",
                        selection: $viewModel.disappearanceDate,
                        in: ...Date(),  // Limit to today or past
                        displayedComponents: .date
                    )
                    .datePickerStyle(.compact)
                    .labelsHidden()
                    .accessibilityIdentifier("animalDescription.datePicker.tap")
                }
                
                // Species dropdown (uses computed model property)
                DropdownView(
                    model: viewModel.speciesDropdownModel,  // ← computed property
                    selection: $viewModel.selectedSpeciesIndex
                )
                .onChange(of: viewModel.selectedSpeciesIndex) { newIndex in
                    if let index = newIndex {
                        viewModel.selectSpecies(viewModel.speciesOptions[index])
                    }
                }
                
                // Race text field (uses computed model property + @Binding)
                ValidatedTextField(
                    model: viewModel.raceTextFieldModel,  // ← computed property with error
                    text: $viewModel.race                  // ← @Binding to @Published value
                )
                
                // ... other components
                
                // Continue button
                Button(action: { viewModel.onContinueTapped() }) {
                    Text(L10n.continue)
                        .frame(maxWidth: .infinity)
                }
                .accessibilityIdentifier("animalDescription.continueButton.tap")
            }
            .padding()
        }
        .alert(L10n.permissionDeniedTitle, isPresented: $viewModel.showPermissionDeniedAlert) {
            Button(L10n.cancel, role: .cancel) {}
            Button(L10n.goToSettings) { viewModel.openSettings() }
        }
        .toast(isPresented: $viewModel.showToast, message: viewModel.toastMessage)
    }
}
```

### 5. Update Coordinator

**Location**: `/iosApp/iosApp/Coordinators/MissingPetFlowCoordinator.swift` (already exists per spec 017)

**Integration**:

```swift
class MissingPetFlowCoordinator {
    private let session = MissingPetFlowSession()  // Already exists
    
    func showAnimalDescriptionScreen() {
        // Manual DI: inject session + LocationService
        let viewModel = AnimalDescriptionViewModel(
            session: session,
            locationService: ServiceContainer.shared.locationService
        )
        
        // Coordinator callbacks
        viewModel.onContinue = { [weak self] in
            self?.showContactDetailsScreen()  // Navigate to Step 4
        }
        viewModel.onBack = { [weak self] in
            self?.navigationController.popViewController(animated: true)
        }
        viewModel.onOpenAppSettings = {
            UIApplication.shared.openSettings()
        }
        
        // Create view and wrap in NavigationBackHiding + UIHostingController
        let view = AnimalDescriptionView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        navigationController.pushViewController(hostingController, animated: true)
    }
}
```

---

## Running the Feature

### 1. Launch App in Simulator

**Xcode**:
- Select `iosApp` scheme + iPhone 15 simulator
- Press `Cmd+R` to run

**Terminal**:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -configuration Debug \
  run
```

### 2. Navigate to Animal Description Screen

1. Launch app
2. Tap "Report Missing Pet" button (Animal List screen)
3. Complete Step 1 (Chip Number) → Continue
4. Complete Step 2 (Animal Photo) → Continue
5. **Step 3 (Animal Description)** should appear

### 3. Manual Testing Checklist

- [ ] Screen loads with default date (today)
- [ ] Species dropdown shows curated list
- [ ] Selecting species enables race text field
- [ ] Changing species clears race field
- [ ] Gender selector shows Male/Female options
- [ ] Tapping GPS button requests location permission
- [ ] Granting permission populates Lat/Long fields
- [ ] Denying permission shows custom alert with Cancel/Go Settings
- [ ] Description text area shows character counter (0/500)
- [ ] Typing beyond 500 characters is blocked
- [ ] Tapping Continue with empty required fields shows toast + inline errors
- [ ] Tapping Continue with invalid coordinates shows inline errors
- [ ] Tapping Continue with valid data navigates to Step 4
- [ ] Tapping Back returns to Step 2 without saving Step 3 data
- [ ] Returning from Step 4 to Step 3 preserves entered data

---

## Testing

### Unit Tests

**Location**: `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/`

**Create**:
1. `AnimalDescriptionViewModelTests.swift` (ViewModel logic tests)
2. `Fakes/FakeMissingPetFlowSession.swift` (test double for session)
3. `Fakes/FakeLocationService.swift` (test double for LocationService) - may already exist from spec 026

**Run unit tests**:

```bash
xcodebuild test \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -enableCodeCoverage YES
```

**Coverage report**: Xcode → Product → Show Code Coverage

**Target**: 80% line + branch coverage for ViewModel and validation logic.

**Test Template** (Given-When-Then):

```swift
import XCTest
@testable import iosApp

final class AnimalDescriptionViewModelTests: XCTestCase {
    var sut: AnimalDescriptionViewModel!
    var fakeSession: FakeMissingPetFlowSession!
    var fakeLocationService: FakeLocationService!
    
    override func setUp() {
        super.setUp()
        fakeSession = FakeMissingPetFlowSession()
        fakeLocationService = FakeLocationService()
        sut = AnimalDescriptionViewModel(
            session: fakeSession,
            locationService: fakeLocationService
        )
    }
    
    func testOnContinueTapped_whenRequiredFieldsEmpty_shouldShowValidationErrors() {
        // Given - empty required fields
        sut.selectedSpecies = nil
        sut.race = ""
        sut.selectedGender = nil
        
        // When - user taps Continue
        sut.onContinueTapped()
        
        // Then - validation errors displayed
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.speciesErrorMessage)
        XCTAssertNotNil(sut.raceErrorMessage)
        XCTAssertNotNil(sut.genderErrorMessage)
    }
    
    func testOnContinueTapped_whenAllFieldsValid_shouldUpdateSessionAndNavigate() {
        // Given - valid data
        sut.disappearanceDate = Date()
        sut.selectedSpecies = SpeciesTaxonomy.options.first
        sut.race = "Golden Retriever"
        sut.selectedGender = .male
        
        var didCallContinue = false
        sut.onContinue = { didCallContinue = true }
        
        // When - user taps Continue
        sut.onContinueTapped()
        
        // Then - session updated and coordinator callback invoked
        XCTAssertNotNil(fakeSession.animalDescription)
        XCTAssertEqual(fakeSession.animalDescription?.race, "Golden Retriever")
        XCTAssertTrue(didCallContinue)
    }
    
    // ... more tests
}
```

### E2E Tests

**Location**: `/e2e-tests/mobile/specs/animal-description.spec.ts`

**Framework**: Appium + Cucumber (Java) with `@ios` tag

**Run E2E tests**:

```bash
cd /Users/msz/dev/ai-first/AI-First
mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"
```

**Report**: `/e2e-tests/target/cucumber-reports/ios/index.html`

**Example Scenario** (Gherkin):

```gherkin
@ios
Feature: Animal Description Screen

  Scenario: Complete animal description with valid data
    Given user is on Animal Description screen (Step 3)
    When user selects species "Dog"
    And user enters race "Golden Retriever"
    And user selects gender "Male"
    And user taps Continue button
    Then user should navigate to Contact Details screen (Step 4)
    And Step 3 data should persist in session

  Scenario: Submit form with missing required fields
    Given user is on Animal Description screen (Step 3)
    When user taps Continue button without filling required fields
    Then toast message should appear
    And inline error messages should display under empty required fields
    And user should remain on Step 3

  Scenario: Capture GPS coordinates with permission granted
    Given user is on Animal Description screen (Step 3)
    And location permission is granted
    When user taps "Request GPS position" button
    Then latitude and longitude fields should populate with coordinates
```

**Screen Object Model**: `/e2e-tests/src/test/java/.../screens/AnimalDescriptionScreen.java`

```java
public class AnimalDescriptionScreen {
    @iOSXCUITFindBy(id = "animalDescription.speciesDropdown.tap")
    private WebElement speciesDropdown;
    
    @iOSXCUITFindBy(id = "animalDescription.raceTextField.input")
    private WebElement raceTextField;
    
    @iOSXCUITFindBy(id = "animalDescription.genderMale.tap")
    private WebElement genderMaleButton;
    
    @iOSXCUITFindBy(id = "animalDescription.continueButton.tap")
    private WebElement continueButton;
    
    public void selectSpecies(String species) {
        speciesDropdown.click();
        // Select species from picker
    }
    
    public void enterRace(String race) {
        raceTextField.sendKeys(race);
    }
    
    public void selectGenderMale() {
        genderMaleButton.click();
    }
    
    public void tapContinue() {
        continueButton.click();
    }
}
```

---

## Troubleshooting

### Build Errors

**Issue**: "Cannot find 'L10n' in scope"
- **Fix**: Run `swiftgen` to regenerate localized string accessors

**Issue**: "Type 'LocationServiceProtocol' not found"
- **Fix**: Verify existing LocationService implementation exists (should be from spec 026)

**Issue**: "Type 'MissingPetFlowSession' not found"
- **Fix**: Verify session container exists (should be from spec 017)

### Runtime Errors

**Issue**: Location permission alert not appearing
- **Fix**: Reset simulator permissions: `xcrun simctl privacy booted reset location`

**Issue**: Species dropdown shows empty list
- **Fix**: Verify `SpeciesTaxonomy.options` is populated with test data

### Test Failures

**Issue**: ViewModel tests fail with "Main actor isolation error"
- **Fix**: Mark test methods with `@MainActor` or wrap assertions in `await MainActor.run { }`

**Issue**: E2E tests cannot find accessibility identifiers
- **Fix**: Verify all interactive elements have `.accessibilityIdentifier()` modifiers

---

## Next Steps

After implementation is complete:

1. **Code Review**: Submit PR with branch `031-ios-animal-description-screen`
2. **QA Testing**: Manual testing checklist (see "Manual Testing Checklist" above)
3. **Coverage Verification**: Run unit tests with coverage report, ensure 80%+ coverage
4. **E2E Tests**: Run mobile E2E suite with `@ios` tag
5. **Documentation**: Update `/iosApp/README.md` with Missing Pet flow architecture changes (if needed)

---

## Reference Links

- **Spec**: [spec.md](./spec.md)
- **Plan**: [plan.md](./plan.md)
- **Research**: [research.md](./research.md)
- **Data Model**: [data-model.md](./data-model.md)
- **Contracts**: [contracts/](./contracts/)
- **Constitution**: `/.specify/memory/constitution.md` (iOS MVVM-C architecture)
- **Spec 017**: Missing Pet flow scaffolding (navigation, session management)
- **Spec 026**: Android location permissions (iOS LocationService reference implementation)

---

## Summary

This quickstart provides:
- **Setup**: Prerequisites, Xcode project setup, dependency verification
- **Development Workflow**: Step-by-step implementation order (models → components → ViewModel → view → coordinator)
- **Running**: Launch instructions and manual testing checklist
- **Testing**: Unit test template (Given-When-Then), E2E test scenarios (Cucumber), coverage verification
- **Troubleshooting**: Common build/runtime/test issues with fixes

Follow the workflow order for systematic implementation. Reference `research.md` and `data-model.md` for detailed technical decisions and model structures.

