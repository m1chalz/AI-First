# Data Model: Animal Description Screen (iOS)

**Feature**: 031-ios-animal-description-screen  
**Date**: November 28, 2025  
**Purpose**: Define domain models, session state, and validation structures

---

## 1. Domain Models

### AnimalDescriptionDetails

**Purpose**: Session-bound structure containing all animal description data for Step 3 of Missing Pet flow.

**Location**: `/iosApp/iosApp/Domain/Models/AnimalDescriptionDetails.swift`

**Definition**:

```swift
import Foundation

/// Animal description data collected in Step 3 of Missing Pet flow.
/// Stored in MissingPetFlowSession and persists across navigation until flow completion.
struct AnimalDescriptionDetails: Equatable {
    let disappearanceDate: Date
    let species: SpeciesTaxonomyOption
    let race: String
    let gender: Gender
    let age: Int?                    // Optional: nil if not provided
    let latitude: Double?            // Optional: nil if not provided
    let longitude: Double?           // Optional: nil if not provided
    let additionalDescription: String?  // Optional: nil or empty if not provided
    let lastUpdated: Date            // Metadata: when this struct was last saved
    
    init(
        disappearanceDate: Date,
        species: SpeciesTaxonomyOption,
        race: String,
        gender: Gender,
        age: Int? = nil,
        latitude: Double? = nil,
        longitude: Double? = nil,
        additionalDescription: String? = nil,
        lastUpdated: Date = Date()
    ) {
        self.disappearanceDate = disappearanceDate
        self.species = species
        self.race = race
        self.gender = gender
        self.age = age
        self.latitude = latitude
        self.longitude = longitude
        self.additionalDescription = additionalDescription
        self.lastUpdated = lastUpdated
    }
}
```

**Field Validation Rules**:

| Field                  | Required | Validation                              | Error Message                          |
|------------------------|----------|-----------------------------------------|----------------------------------------|
| `disappearanceDate`    | ✅ Yes   | Must be today or past date              | _Proactive blocking by DatePicker_     |
| `species`              | ✅ Yes   | Must be valid `SpeciesTaxonomyOption`   | "Please select a species"              |
| `race`                 | ✅ Yes   | Non-empty string after trim             | "Please enter the animal's breed/race" |
| `gender`               | ✅ Yes   | Must be `.male` or `.female`            | "Please select a gender"               |
| `age`                  | ❌ No    | If provided: 0-40, whole number         | "Age must be between 0 and 40"         |
| `latitude`             | ❌ No    | If provided: -90 to 90                  | "Latitude must be between -90 and 90"  |
| `longitude`            | ❌ No    | If provided: -180 to 180                | "Longitude must be between -180 and 180"|
| `additionalDescription`| ❌ No    | If provided: max 500 characters         | _Enforced by component, no error_      |

---

### SpeciesTaxonomyOption

**Purpose**: Represents a selectable species from the curated list bundled with the app.

**Location**: `/iosApp/iosApp/Domain/Models/SpeciesTaxonomyOption.swift`

**Definition**:

```swift
/// Species option from curated taxonomy list bundled with app.
/// Used in species dropdown for Step 3 (Animal Description screen).
struct SpeciesTaxonomyOption: Identifiable, Equatable, Hashable {
    let id: String           // Unique identifier (e.g., "dog", "cat")
    let displayName: String  // Localized display name (e.g., "Dog", "Cat")
}
```

**Static Data Source** (`/iosApp/iosApp/Data/SpeciesTaxonomy.swift`):

```swift
struct SpeciesTaxonomy {
    /// Curated list of species options bundled with app.
    /// Offline-capable (no network dependency).
    static let options: [SpeciesTaxonomyOption] = [
        SpeciesTaxonomyOption(id: "dog", displayName: L10n.speciesDog),
        SpeciesTaxonomyOption(id: "cat", displayName: L10n.speciesCat),
        SpeciesTaxonomyOption(id: "bird", displayName: L10n.speciesBird),
        SpeciesTaxonomyOption(id: "rabbit", displayName: L10n.speciesRabbit),
        SpeciesTaxonomyOption(id: "rodent", displayName: L10n.speciesRodent),
        SpeciesTaxonomyOption(id: "reptile", displayName: L10n.speciesReptile),
        SpeciesTaxonomyOption(id: "other", displayName: L10n.speciesOther)
    ]
}
```

**Notes**:
- Display names MUST use SwiftGen-generated localized strings (`L10n.*`)
- Species IDs MUST be stable (used for serialization if session persists to disk in future)
- List is bundled at compile time (no runtime loading or API dependency)

---

### Gender

**Purpose**: Enum for animal gender options (binary for this release).

**Location**: `/iosApp/iosApp/Domain/Models/Gender.swift`

**Definition**:

```swift
/// Gender options for animals (binary for initial release).
/// Future releases may expand to include additional options.
enum Gender: String, CaseIterable, Codable {
    case male = "male"
    case female = "female"
    
    var displayName: String {
        switch self {
        case .male: return L10n.genderMale
        case .female: return L10n.genderFemale
        }
    }
}
```

**Notes**:
- Display names MUST use SwiftGen-generated localized strings
- Spec assumption: "Gender options remain binary for this release, matching the provided iOS design"

---

## 2. Validation Models

### ValidationError

**Purpose**: Represents validation errors for individual fields.

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/ValidationError.swift`

**Definition**:

```swift
/// Validation errors for Animal Description form fields.
/// Used by ViewModel to track and display field-specific error messages.
enum ValidationError: Equatable {
    case missingDate
    case missingSpecies
    case missingRace
    case missingGender
    case invalidAge(String)         // Error message parameter
    case invalidLatitude(String)    // Error message parameter
    case invalidLongitude(String)   // Error message parameter
    
    /// User-facing error message for display in UI.
    var message: String {
        switch self {
        case .missingDate:
            return L10n.errorMissingDate
        case .missingSpecies:
            return L10n.errorMissingSpecies
        case .missingRace:
            return L10n.errorMissingRace
        case .missingGender:
            return L10n.errorMissingGender
        case .invalidAge(let msg):
            return msg
        case .invalidLatitude(let msg):
            return msg
        case .invalidLongitude(let msg):
            return msg
        }
    }
    
    /// Field identifier for mapping errors to UI components.
    var field: FormField {
        switch self {
        case .missingDate: return .date
        case .missingSpecies: return .species
        case .missingRace: return .race
        case .missingGender: return .gender
        case .invalidAge: return .age
        case .invalidLatitude: return .latitude
        case .invalidLongitude: return .longitude
        }
    }
}

/// Form field identifiers for validation error mapping.
enum FormField {
    case date
    case species
    case race
    case gender
    case age
    case latitude
    case longitude
    case description
}
```

---

### CoordinateValidationResult

**Purpose**: Result type for latitude/longitude validation.

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/CoordinateValidationResult.swift`

**Definition**:

```swift
/// Result of coordinate validation for latitude and longitude fields.
enum CoordinateValidationResult: Equatable {
    case valid
    case invalid(latError: String?, longError: String?)
    
    var isValid: Bool {
        if case .valid = self {
            return true
        }
        return false
    }
}
```

---

## 3. Component Models

### ValidatedTextField.Model

**Purpose**: Model for reusable text field component with validation error display.

**Location**: Defined as extension in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/ValidatedTextField.swift`

**Definition**:

```swift
extension ValidatedTextField {
    struct Model {
        let label: String
        let placeholder: String
        let errorMessage: String?
        let isDisabled: Bool
        let keyboardType: UIKeyboardType
        let accessibilityID: String
        
        init(
            label: String,
            placeholder: String = "",
            errorMessage: String? = nil,
            isDisabled: Bool = false,
            keyboardType: UIKeyboardType = .default,
            accessibilityID: String
        ) {
            self.label = label
            self.placeholder = placeholder
            self.errorMessage = errorMessage
            self.isDisabled = isDisabled
            self.keyboardType = keyboardType
            self.accessibilityID = accessibilityID
        }
    }
}
```

---

### DropdownView.Model

**Purpose**: Model for generic dropdown component with validation error display.

**Location**: Defined as extension in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/DropdownView.swift`

**Definition**:

```swift
extension DropdownView {
    struct Model {
        let label: String
        let placeholder: String
        let options: [String]        // Generic string array for any dropdown options
        let errorMessage: String?
        let accessibilityID: String
    }
}
```

**Usage Note**: ViewModel maps `SpeciesTaxonomyOption` to display names when creating model:

```swift
// In ViewModel
var speciesDropdownModel: DropdownView.Model {
    DropdownView.Model(
        label: L10n.speciesLabel,
        placeholder: L10n.speciesPlaceholder,
        options: SpeciesTaxonomy.options.map { $0.displayName },  // Convert to [String]
        errorMessage: speciesErrorMessage,
        accessibilityID: "animalDescription.speciesDropdown.tap"
    )
}
```

---

### SelectorView.Model

**Purpose**: Model for generic radio button selector component.

**Location**: Defined as extension in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/SelectorView.swift`

**Definition**:

```swift
extension SelectorView {
    struct Model {
        let label: String
        let options: [String]            // Generic string array for any selector options
        let errorMessage: String?
        let accessibilityIDPrefix: String  // e.g., "animalDescription.gender"
    }
}
```

**Usage Note**: ViewModel maps domain enums to display names when creating model:

```swift
// In ViewModel
var genderSelectorModel: SelectorView.Model {
    SelectorView.Model(
        label: L10n.genderLabel,
        options: Gender.allCases.map { $0.displayName },  // ["Male", "Female"]
        errorMessage: genderErrorMessage,
        accessibilityIDPrefix: "animalDescription.gender"
    )
}
```

---

### LocationCoordinateView.Model

**Purpose**: Model for latitude/longitude coordinate input with GPS capture button. Composes two `ValidatedTextField.Model` instances.

**Location**: Defined as extension in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/LocationCoordinateView.swift`

**Definition**:

```swift
extension LocationCoordinateView {
    struct Model {
        let latitudeField: ValidatedTextField.Model      // Composed ValidatedTextField
        let longitudeField: ValidatedTextField.Model     // Composed ValidatedTextField
        let gpsButtonTitle: String
        let gpsButtonAccessibilityID: String
        let helperText: String?  // Optional helper text (e.g., "GPS capture successful")
    }
}
```

**Usage Note**: ViewModel creates two ValidatedTextField models for composition:

```swift
// In ViewModel
var locationCoordinateModel: LocationCoordinateView.Model {
    LocationCoordinateView.Model(
        latitudeField: ValidatedTextField.Model(
            label: L10n.latitudeLabel,
            placeholder: "e.g., 52.2297",
            errorMessage: latitudeErrorMessage,
            isDisabled: false,
            keyboardType: .decimalPad,
            accessibilityID: "animalDescription.latitudeTextField.input"
        ),
        longitudeField: ValidatedTextField.Model(
            label: L10n.longitudeLabel,
            placeholder: "e.g., 21.0122",
            errorMessage: longitudeErrorMessage,
            isDisabled: false,
            keyboardType: .decimalPad,
            accessibilityID: "animalDescription.longitudeTextField.input"
        ),
        gpsButtonTitle: L10n.requestGPSButton,
        gpsButtonAccessibilityID: "animalDescription.requestGPSButton.tap",
        helperText: gpsHelperText
    )
}
```

---

### TextAreaView.Model

**Purpose**: Model for generic multi-line text area with character counter.

**Location**: Defined as extension in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/TextAreaView.swift`

**Definition**:

```swift
extension TextAreaView {
    struct Model {
        let label: String
        let placeholder: String
        let maxLength: Int
        let characterCountText: String      // Formatted by ViewModel (e.g., "123/500")
        let characterCountColor: Color      // Computed by ViewModel
        let accessibilityID: String
    }
}
```

---

## 4. Session Integration

### MissingPetFlowSession

**Purpose**: Session container for Missing Pet flow (already exists per spec 017).

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Session/MissingPetFlowSession.swift`

**Expected Structure** (Step 3 integration):

```swift
class MissingPetFlowSession {
    // Existing properties from spec 017
    var chipNumber: String?
    var animalPhoto: UIImage?
    
    // NEW: Step 3 data
    var animalDescription: AnimalDescriptionDetails?
    
    // Existing: Step 4 data (contact details)
    var contactDetails: ContactDetails?
    
    // Flow state tracking
    var completedSteps: Set<Int> = []
}
```

**Notes**:
- Session is reference type (class) shared across all flow steps
- Constructor-injected to ViewModel per constitution manual DI pattern
- Coordinator manages session lifetime

---

## 5. State Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                  Animal Description Screen (Step 3)             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                 ┌─────────────────────────┐
                 │   Initial State         │
                 │  - Date: today          │
                 │  - All fields empty     │
                 │  - No validation errors │
                 └─────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
              ▼               ▼               ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │ User fills  │  │ User taps   │  │ User taps   │
    │ form fields │  │ GPS button  │  │ Back arrow  │
    └─────────────┘  └─────────────┘  └─────────────┘
         │                   │                  │
         │                   ▼                  ▼
         │         ┌──────────────────┐  Navigate to Step 2
         │         │ Permission check │  (session persists)
         │         └──────────────────┘
         │                   │
         │         ┌─────────┴─────────┐
         │         ▼                   ▼
         │  ┌────────────┐      ┌──────────────┐
         │  │ Authorized │      │ Denied/      │
         │  │ → Fetch    │      │ Restricted   │
         │  │   location │      │ → Show alert │
         │  └────────────┘      └──────────────┘
         │         │                   │
         │         ▼                   ▼
         │  ┌────────────┐      ┌──────────────┐
         │  │ Populate   │      │ User chooses │
         │  │ Lat/Long   │      │ Cancel or    │
         │  │ fields     │      │ Go Settings  │
         │  └────────────┘      └──────────────┘
         │
         ▼
    ┌─────────────┐
    │ User taps   │
    │ Continue    │
    └─────────────┘
         │
         ▼
    ┌──────────────────┐
    │ Validate all     │
    │ required fields  │
    └──────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌──────┐  ┌───────┐
│Valid │  │Invalid│
└──────┘  └───────┘
    │         │
    │         ▼
    │  ┌──────────────────┐
    │  │ Show toast +     │
    │  │ inline errors    │
    │  │ Stay on screen   │
    │  └──────────────────┘
    │
    ▼
┌──────────────────┐
│ Update session   │
│ Navigate Step 4  │
└──────────────────┘
```

---

## 6. Data Flow Summary

### On Screen Load
1. ViewModel initializes with session container + LocationService
2. If session.animalDescription exists → populate fields (navigation back from Step 4)
3. Else → initialize with defaults (date: today, all fields empty)

### On User Input
1. User edits fields → `@Published` properties update in ViewModel
2. Species change → ViewModel clears race field (automatic behavior)
3. Description text → ViewModel enforces 500-character limit

### On GPS Button Tap
1. ViewModel checks permission status via LocationService
2. If .notDetermined → request permission (system alert shown)
3. If authorized → fetch location, populate Lat/Long fields
4. If denied/restricted → show custom permission alert with Cancel/Go Settings options

### On Continue Button Tap
1. ViewModel validates all required fields (date, species, race, gender)
2. ViewModel validates optional fields if provided (age 0-40, coordinates in range)
3. If invalid → show toast, apply inline errors, stay on screen
4. If valid → update session.animalDescription, call `onContinue()` closure (coordinator navigates to Step 4)

### On Back Button Tap
1. ViewModel does NOT update session (preserves previous Step 3 data if any)
2. Call `onBack()` closure (coordinator navigates to Step 2)

---

## 7. Testing Considerations

### Unit Tests (ViewModel)
- Test validation logic for all required and optional fields
- Test coordinate validation ranges (edge cases: -90, 90, -180, 180, invalid strings)
- Test species change clears race field
- Test GPS permission flow (mock LocationService responses)
- Test session update on valid Continue tap
- Test coordinator callback invocations

### E2E Tests
- Test complete form submission flow (fill all required fields → Continue → navigate Step 4)
- Test validation errors (empty required fields → Continue → toast + inline errors shown)
- Test GPS capture flow (tap GPS button → grant permission → fields populated)
- Test GPS denied flow (tap GPS button → deny → custom alert → Cancel/Go Settings)
- Test navigation back (tap Back → return to Step 2 with session preserved)

---

## Summary

This data model specification defines:
- **Domain models**: AnimalDescriptionDetails (session data), SpeciesTaxonomyOption, Gender
- **Validation models**: ValidationError, CoordinateValidationResult
- **Component models**: Model structs for all reusable form components
- **Session integration**: MissingPetFlowSession with animalDescription property
- **State diagram**: Visual flow from initial state → user input → validation → navigation
- **Data flow**: Detailed sequences for user interactions (input, GPS capture, validation, navigation)

All models follow iOS MVVM-C architecture with constitution compliance (manual DI, Swift Concurrency, SwiftGen localization).

