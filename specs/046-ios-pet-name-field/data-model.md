# Data Model: iOS - Add Pet Name Field to Animal Details Screen

**Feature**: 046-ios-pet-name-field  
**Date**: December 4, 2025

## Entities

### ReportMissingPetFlowState (MODIFIED)

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`

**Purpose**: Holds multi-step form data for the "Report Missing Pet" flow across all screens (Location → Owner Details → Animal Details → Summary).

**Modification**: Add optional `petName` property to store the animal name entered by the user.

**Structure**:
```swift
struct ReportMissingPetFlowState {
    // Existing properties (not modified)
    var location: Location?
    var ownerName: String?
    var ownerPhone: String?
    var species: AnimalSpecies?
    var breed: String?
    var sex: AnimalSex?
    var age: Int?
    var dateOfDisappearance: Date?
    var description: String?
    var photoData: Data?
    
    // NEW PROPERTY
    var petName: String?  // Animal name (optional, trimmed before API submission)
}
```

**Relationships**: None (simple value type)

**Validation Rules**:
- Optional field (can be `nil` or empty)
- No client-side character limit enforcement
- Backend validates maximum length (client delegates validation to server)

**State Transitions**:
- Initial state: `petName = nil`
- User enters text → `petName` updated with user input
- User navigates back/forward → `petName` persists in flow state
- Submit to API → trim whitespace; if result is empty/whitespace-only, send `nil` or omit field

---

### AnimalDescriptionViewModel (MODIFIED)

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModel.swift`

**Purpose**: Manages presentation state and user interactions for the Animal Details screen (step 3/4 of missing pet flow).

**Modification**: Add `@Published var petName: String` property and computed property for TextField model (if applicable).

**Structure**:
```swift
@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    // Existing properties (not modified)
    @Published var species: AnimalSpecies?
    @Published var breed: String = ""
    @Published var sex: AnimalSex?
    @Published var age: String = ""
    @Published var dateOfDisappearance: Date?
    @Published var description: String = ""
    
    // NEW PROPERTY
    @Published var petName: String = ""  // Two-way binding for TextField
    
    // Existing dependencies
    private let flowState: ReportMissingPetFlowState
    var onContinue: (() -> Void)?
    
    // Existing methods (not modified)
    init(flowState: ReportMissingPetFlowState) { ... }
    func continueToNextStep() { ... }
    func updateFlowState() { ... }
    
    // NEW COMPUTED PROPERTY (if TextField.Model pattern exists)
    var petNameTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            text: $petName,
            placeholder: L10n.animalNamePlaceholder,  // "Animal name (optional)"
            accessibilityIdentifier: "animalDescription.animalNameTextField.input"
        )
    }
}
```

**Relationships**:
- Reads from `flowState.petName` on initialization
- Updates `flowState.petName` when user taps "Continue"

**Validation Rules**:
- No validation in ViewModel (accepts all input)
- Empty string allowed (represents "no pet name")

**State Transitions**:
- On init: Load `flowState.petName ?? ""` into `petName` property
- On user edit: SwiftUI two-way binding updates `petName` property
- On continue: Update `flowState.petName` with trimmed value (or `nil` if empty/whitespace-only)

---

### API Request Body (REFERENCE - Backend Model)

**Endpoint**: `POST /api/announcements`

**Purpose**: Create a new missing pet announcement with optional pet name field.

**Existing Structure** (for reference):
```json
{
  "location": {
    "latitude": 52.2297,
    "longitude": 21.0122
  },
  "ownerName": "John Doe",
  "ownerPhone": "+48 123 456 789",
  "species": "dog",
  "breed": "Labrador",
  "sex": "male",
  "age": 5,
  "dateOfDisappearance": "2025-12-01",
  "description": "Black Labrador, very friendly",
  "photo": "base64-encoded-image-data",
  "petName": "Max"  // NEW FIELD (optional)
}
```

**Field Details**:
- `petName`: Optional string field
- Sent only when non-empty after trimming
- Backend validates maximum length and returns standard error if exceeded
- If empty/whitespace-only after trimming: send `null` or omit field from JSON

---

## Data Flow

### User Enters Pet Name

```
User types in TextField
  ↓ (SwiftUI two-way binding)
AnimalDescriptionViewModel.petName = "Max"
  ↓ (user taps "Continue")
ViewModel.updateFlowState()
  ↓ (trim whitespace, convert empty to nil)
flowState.petName = "Max"
  ↓ (user completes flow and submits)
API Request: { ..., "petName": "Max" }
```

### User Leaves Pet Name Empty

```
User leaves TextField empty
  ↓
AnimalDescriptionViewModel.petName = ""
  ↓ (user taps "Continue")
ViewModel.updateFlowState()
  ↓ (empty string trimmed, converted to nil)
flowState.petName = nil
  ↓ (user completes flow and submits)
API Request: { ... } (petName field omitted or null)
```

### User Navigates Back and Forward

```
User enters "Max" on Animal Details screen
  ↓
flowState.petName = "Max"
  ↓ (user taps "Back" to Owner Details)
flowState preserved in memory
  ↓ (user taps "Continue" back to Animal Details)
ViewModel.init(flowState: flowState)
  ↓ (load persisted value)
AnimalDescriptionViewModel.petName = "Max" (restored)
```

---

## Type Mappings

| iOS Swift Type | Flow State Type | API JSON Type | Notes |
|----------------|-----------------|---------------|-------|
| `@Published var petName: String` | `var petName: String?` | `"petName": string \| null` | ViewModel uses non-optional for TextField binding; flow state and API use optional |
| Empty string `""` | `nil` | Field omitted or `null` | Empty/whitespace-only converted to nil before API submission |
| User input `"Max"` | `"Max"` (trimmed) | `"Max"` | Whitespace trimmed before storing in flow state |

---

## Validation Matrix

| Validation Rule | Client (iOS) | Backend (API) |
|-----------------|--------------|---------------|
| Required field? | No (optional) | No (optional) |
| Character limit | No limit | Maximum length validated (returns error if exceeded) |
| Input filtering (emoji, special chars) | No filtering (accept all Unicode) | No filtering (accept all Unicode) |
| Whitespace handling | Trim before API submission | N/A (receives trimmed value) |
| Empty value handling | Convert to `nil` or omit field | Accept `null` or missing field |

---

## Testing Considerations

### Unit Test Scenarios (iOS)

1. **Given** user enters "Max" **When** ViewModel updates flow state **Then** `flowState.petName == "Max"`
2. **Given** user enters "  Max  " (with whitespace) **When** ViewModel updates flow state **Then** `flowState.petName == "Max"` (trimmed)
3. **Given** user enters "   " (whitespace-only) **When** ViewModel updates flow state **Then** `flowState.petName == nil`
4. **Given** user leaves field empty **When** ViewModel updates flow state **Then** `flowState.petName == nil`
5. **Given** `flowState.petName == "Max"` **When** ViewModel initializes **Then** `viewModel.petName == "Max"` (restored)
6. **Given** API request payload **When** `flowState.petName == "Max"` **Then** payload includes `"petName": "Max"`
7. **Given** API request payload **When** `flowState.petName == nil` **Then** payload omits `petName` or includes `"petName": null`

### E2E Test Scenarios (Mobile)

1. **Given** user is on Animal Details screen **When** user enters "Max" in animal name field **Then** field displays "Max"
2. **Given** user entered "Max" **When** user taps "Continue" and completes flow **Then** created announcement includes pet name "Max"
3. **Given** user leaves animal name empty **When** user taps "Continue" and completes flow **Then** created announcement has no pet name
4. **Given** user entered "Max" **When** user navigates back and forward **Then** "Max" is still displayed in field

---

## Migration Notes

No data migration required:
- Existing `ReportMissingPetFlowState` instances in memory (if any) will have `petName = nil` (default for optional properties)
- Existing announcements in backend database are unaffected (backend already supports optional `petName` field)
- No schema changes needed (iOS models are not persisted to local database)

---

## References

- Feature Spec: `/specs/046-ios-pet-name-field/spec.md`
- Research: `/specs/046-ios-pet-name-field/research.md`
- Backend API Spec: (assumed existing - `POST /api/announcements` accepts optional `petName` field)

