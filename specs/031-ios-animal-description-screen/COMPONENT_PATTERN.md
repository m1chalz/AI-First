# Component Communication Pattern

**Feature**: 031-ios-animal-description-screen  
**Purpose**: Explain how reusable form components communicate with ViewModel

---

## Pattern Overview

Reusable components (ValidatedTextField, DropdownView, etc.) use a **two-way communication pattern**:

1. **Model (one-way)**: Component receives `Model` struct with display properties (label, placeholder, errorMessage)
2. **Binding (two-way)**: Component receives `@Binding` to value for user input

## Example: ValidatedTextField + ViewModel

### Component Definition

```swift
struct ValidatedTextField: View {
    let model: Model              // ← One-way: ViewModel → Component
    @Binding var text: String     // ← Two-way: ViewModel ↔ Component
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
            TextField(model.placeholder, text: $text)  // ← $text is @Binding
                .disabled(model.isDisabled)
            
            if let error = model.errorMessage {
                Text(error)
                    .foregroundColor(.red)
            }
        }
    }
}

extension ValidatedTextField {
    struct Model {
        let label: String
        let placeholder: String
        let errorMessage: String?    // ← Validation error from ViewModel
        let isDisabled: Bool
        let keyboardType: UIKeyboardType
        let accessibilityID: String
    }
}
```

### ViewModel (Grouped Properties Pattern)

```swift
@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    // MARK: - Published State (Grouped)
    
    /// Form data (all field values)
    @Published var formData: FormData
    
    /// Validation errors (all error messages)
    @Published var validationErrors: ValidationErrors
    
    /// UI state (alerts, toasts)
    @Published var uiState: UIState
    
    // MARK: - State Structures
    
    struct FormData: Equatable {
        var disappearanceDate: Date = Date()
        var selectedSpecies: SpeciesTaxonomyOption?
        var race: String = ""
        var selectedGender: Gender?
        var age: String = ""
        var location = LocationData()
        var additionalDescription: String = ""
        
        struct LocationData: Equatable {
            var latitude: String = ""
            var longitude: String = ""
        }
    }
    
    struct ValidationErrors: Equatable {
        var species: String?
        var race: String?
        var gender: String?
        var age: String?
        var latitude: String?
        var longitude: String?
        
        static let clear = ValidationErrors()
    }
    
    struct UIState: Equatable {
        var showPermissionDeniedAlert = false
        var showToast = false
        var toastMessage = ""
        var gpsHelperText: String?
    }
    
    // MARK: - Computed Properties (Component Models)
    
    /// Model for race text field.
    /// Recomputes whenever validationErrors or formData changes.
    var raceTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.raceLabel,
            placeholder: L10n.racePlaceholder,
            errorMessage: validationErrors.race,           // ← from grouped struct
            isDisabled: formData.selectedSpecies == nil,   // ← from grouped struct
            keyboardType: .default,
            accessibilityID: "animalDescription.raceTextField.input"
        )
    }
    
    /// Model for species dropdown (maps to [String] for DropdownView).
    var speciesDropdownModel: DropdownView.Model {
        DropdownView.Model(
            label: L10n.speciesLabel,
            placeholder: L10n.speciesPlaceholder,
            options: speciesOptions.map { $0.displayName },  // ← Map to [String]
            errorMessage: validationErrors.species,
            accessibilityID: "animalDescription.speciesDropdown.tap"
        )
    }
    
    /// Model for gender selector (maps to [String] for SelectorView).
    var genderSelectorModel: SelectorView.Model {
        SelectorView.Model(
            label: L10n.genderLabel,
            options: genderOptions.map { $0.displayName },  // ← Map to [String]
            errorMessage: validationErrors.gender,
            accessibilityIDPrefix: "animalDescription.gender"
        )
    }
    
    // MARK: - Internal Data Access (for index mapping)
    
    /// Species options from curated taxonomy.
    /// Used internally to map selected index → SpeciesTaxonomyOption.
    var speciesOptions: [SpeciesTaxonomyOption] {
        SpeciesTaxonomy.options
    }
    
    /// Gender options (Gender.allCases).
    /// Used internally to map selected index → Gender enum.
    var genderOptions: [Gender] {
        Gender.allCases
    }
    
    // MARK: - User Actions
    
    func selectSpecies(_ species: SpeciesTaxonomyOption) {
        formData.selectedSpecies = species
        // Clear race when species changes (per spec requirement)
        formData.race = ""
        validationErrors.race = nil
    }
    
    func onContinueTapped() {
        // STEP 1: Clear previous errors
        clearValidationErrors()
        
        // STEP 2: Validate all fields
        let errors = validateAllFields()
        
        // STEP 3: If errors exist, show them and stay on screen
        if !errors.isEmpty {
            applyValidationErrors(errors)
            uiState.showToast = true
            uiState.toastMessage = L10n.validationErrorToast
            return
        }
        
        // STEP 4: All valid - update session and navigate
        updateSession()
        onContinue?()
    }
    
    private func validateAllFields() -> [ValidationError] {
        var errors: [ValidationError] = []
        
        // Validate required fields (access via formData)
        if formData.selectedSpecies == nil {
            errors.append(.missingSpecies)
        }
        
        if formData.race.trimmingCharacters(in: .whitespaces).isEmpty {
            errors.append(.missingRace)
        }
        
        if formData.selectedGender == nil {
            errors.append(.missingGender)
        }
        
        // Validate optional fields (if provided)
        if !formData.age.isEmpty, 
           let ageValue = Int(formData.age), 
           !(0...40).contains(ageValue) {
            errors.append(.invalidAge("Age must be between 0 and 40"))
        }
        
        // Validate coordinates
        let lat = formData.location.latitude
        let long = formData.location.longitude
        if !lat.isEmpty || !long.isEmpty {
            if let latValue = Double(lat), let longValue = Double(long) {
                if !(-90...90).contains(latValue) {
                    errors.append(.invalidLatitude("Latitude must be between -90 and 90"))
                }
                if !(-180...180).contains(longValue) {
                    errors.append(.invalidLongitude("Longitude must be between -180 and 180"))
                }
            } else {
                errors.append(.invalidLatitude("Invalid format"))
                errors.append(.invalidLongitude("Invalid format"))
            }
        }
        
        return errors
    }
    
    private func clearValidationErrors() {
        validationErrors = .clear  // ← Single line instead of 6!
    }
    
    private func applyValidationErrors(_ errors: [ValidationError]) {
        for error in errors {
            switch error {
            case .missingSpecies:
                validationErrors.species = L10n.errorMissingSpecies
            case .missingRace:
                validationErrors.race = L10n.errorMissingRace  // ← Sets struct property
            case .missingGender:
                validationErrors.gender = L10n.errorMissingGender
            case .invalidAge(let message):
                validationErrors.age = message
            case .invalidLatitude(let message):
                validationErrors.latitude = message
            case .invalidLongitude(let message):
                validationErrors.longitude = message
            }
        }
    }
}
```

### View Usage

```swift
struct AnimalDescriptionView: View {
    @ObservedObject var viewModel: AnimalDescriptionViewModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Race text field
            ValidatedTextField(
                model: viewModel.raceTextFieldModel,      // ← Computed model (includes errorMessage)
                text: $viewModel.formData.race           // ← @Binding to grouped property
            )
            
            Button("Continue") {
                viewModel.onContinueTapped()
            }
        }
        .toast(
            isPresented: $viewModel.uiState.showToast,   // ← Grouped UI state
            message: viewModel.uiState.toastMessage
        )
    }
}
```

## Data Flow

### 1. User Types in TextField

```
User types "Golden Retriever"
    ↓
TextField updates $text binding
    ↓
viewModel.formData.race = "Golden Retriever"  (@Published formData triggers view update)
    ↓
View re-renders (but model unchanged, so component stays same)
```

### 2. Validation on Submit (Submit-Based Validation)

```
User taps Continue
    ↓
viewModel.onContinueTapped()
    ↓
STEP 1: clearValidationErrors()
    → All errorMessage properties set to nil
    ↓
STEP 2: validateAllFields() → returns [ValidationError]
    → Check all required fields
    → Check optional field ranges (age, coordinates)
    ↓
STEP 3: If errors exist:
    applyValidationErrors(errors)
        → validationErrors.species = "Please select a species"
        → validationErrors.race = "Please enter race"  ← @Published triggers update
        → validationErrors.gender = "Please select gender"
    ↓
    uiState.showToast = true
    uiState.toastMessage = "Please correct highlighted fields"
    ↓
    return (stay on screen)
    ↓
View re-renders (triggered by @Published changes)
    ↓
Computed properties recompute:
    → raceTextFieldModel → new Model with errorMessage
    → speciesDropdownModel → new Model with errorMessage
    → genderSelectorModel → new Model with errorMessage
    ↓
Components re-render with red error text
    ↓
User sees: toast + inline errors under invalid fields
```

**Why Submit-Based (Not Real-Time)?**
- Per spec clarifications: "When should field validation occur? → A: On submit"
- Better UX: Don't show errors while user is still typing
- Continue button always enabled (per FR-012)
- Validation only on Continue tap

### 3. Species Change Clears Race

```
User selects new species
    ↓
viewModel.selectSpecies(newSpecies)
    ↓
viewModel.formData.selectedSpecies = newSpecies  (@Published formData)
viewModel.formData.race = ""                     (@Published formData)
viewModel.validationErrors.race = nil            (@Published validationErrors)
    ↓
View re-renders
    ↓
viewModel.raceTextFieldModel recomputes (isDisabled=false, errorMessage=nil)
    ↓
ValidatedTextField re-renders (enabled, cleared text, no error)
```

## Key Points

### Why Computed Model Properties?

✅ **Reactive**: Model recomputes automatically when dependencies change (@Published properties)
✅ **Clean separation**: Component doesn't know about validation logic
✅ **Single source of truth**: errorMessage stored in ViewModel, not duplicated in component
✅ **Testable**: ViewModel validation logic can be unit tested without UI

### Why @Binding for Value?

✅ **Two-way communication**: Component can update value, ViewModel can read/set value
✅ **SwiftUI pattern**: Standard way to pass mutable state to child views
✅ **Automatic updates**: Changes propagate immediately via @Published

### Why Not Pass ViewModel to Component?

❌ **Tight coupling**: Component would depend on specific ViewModel type
❌ **Not reusable**: Can't use component with different ViewModels
❌ **Hard to test**: Component tests would need full ViewModel setup

✅ **Better**: Pass Model struct + @Binding = component is generic and reusable

## Other Components Follow Same Pattern

### DropdownView

```swift
DropdownView(
    model: viewModel.speciesDropdownModel,  // ← Computed model with [String] options
    selection: $viewModel.selectedSpeciesIndex  // ← @Binding to index
)
.onChange(of: viewModel.selectedSpeciesIndex) { newIndex in
    if let index = newIndex {
        // Map index back to SpeciesTaxonomyOption using internal array
        viewModel.selectSpecies(viewModel.speciesOptions[index])
    }
}
```

**Pattern**: 
- ViewModel stores `speciesOptions: [SpeciesTaxonomyOption]` internally
- Computed model maps to `[String]` for DropdownView: `.map { $0.displayName }`
- onChange handler maps selected index → SpeciesTaxonomyOption

### SelectorView (Radio Buttons)

```swift
SelectorView(
    model: viewModel.genderSelectorModel,  // ← Computed model with [String] options
    selectedIndex: $viewModel.selectedGenderIndex  // ← @Binding to index
)
.onChange(of: viewModel.selectedGenderIndex) { newIndex in
    if let index = newIndex {
        // Map index back to Gender enum using internal array
        viewModel.selectGender(viewModel.genderOptions[index])
    }
}
```

**Pattern**:
- ViewModel stores `genderOptions: [Gender]` internally (Gender.allCases)
- Computed model maps to `[String]` for SelectorView: `.map { $0.displayName }`
- onChange handler maps selected index → Gender enum

### TextAreaView

```swift
TextAreaView(
    model: viewModel.descriptionTextAreaModel,  // ← Computed model with counter
    text: $viewModel.additionalDescription  // ← @Binding to text
)
```

### LocationCoordinateView

```swift
LocationCoordinateView(
    model: viewModel.locationCoordinateModel,  // ← Computed model (composes 2x TextField models)
    latitude: $viewModel.latitude,   // ← @Binding to latitude
    longitude: $viewModel.longitude,  // ← @Binding to longitude
    onGPSButtonTap: { await viewModel.requestGPSPosition() }
)
```

## Summary

**Pattern**: Computed Model + @Binding

- **Model** = immutable display properties (label, placeholder, errorMessage)
- **@Binding** = mutable value (text, selection, index)
- **Computed property** = creates Model from current ViewModel state
- **@Published** = triggers view updates when state changes

This pattern keeps components generic, reusable, and testable while maintaining clear separation of concerns.

