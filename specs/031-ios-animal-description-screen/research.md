# Research: Animal Description Screen (iOS)

**Feature**: 031-ios-animal-description-screen  
**Date**: November 28, 2025  
**Purpose**: Technical research for iOS Animal Description screen implementation

---

## 1. Reusable Form Components with Validation (SwiftUI)

### Decision
Create reusable SwiftUI components following the **Model pattern** (not ViewModel pattern) as defined in constitution for simple views with static data.

### Rationale
- **Model pattern** appropriate for stateless form components that receive data via initializer parameters
- Each component defines `struct Model` in extension to component struct
- Components remain pure presentation (no `@Published` properties, no business logic)
- Parent ViewModel manages validation state and error messages
- Components display validation errors passed from ViewModel

### Component Design Pattern

```swift
struct ValidatedTextField: View {
    let model: Model
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
            TextField(model.placeholder, text: $text)
                .accessibilityIdentifier(model.accessibilityID)
                .disabled(model.isDisabled)
            
            if let error = model.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
    }
}

extension ValidatedTextField {
    struct Model {
        let label: String
        let placeholder: String
        let errorMessage: String?
        let isDisabled: Bool
        let accessibilityID: String
    }
}
```

### Generic DropdownView Pattern

**Decision**: Use generic `DropdownView` component accepting `[String]` options for maximum reusability.

**Rationale**:
- More flexible than type-specific dropdowns (can be reused for species, status, or any string-based options)
- ViewModel maps domain models (e.g., `SpeciesTaxonomyOption`) to display names when creating model
- Component remains pure presentation without knowledge of domain types

**Implementation**:

```swift
struct DropdownView: View {
    let model: Model
    @Binding var selectedIndex: Int?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
            Picker(model.placeholder, selection: $selectedIndex) {
                ForEach(model.options.indices, id: \.self) { index in
                    Text(model.options[index]).tag(index as Int?)
                }
            }
            .accessibilityIdentifier(model.accessibilityID)
            
            if let error = model.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
    }
}

extension DropdownView {
    struct Model {
        let label: String
        let placeholder: String
        let options: [String]           // Generic string array
        let errorMessage: String?
        let accessibilityID: String
    }
}
```

**Usage Pattern** (ViewModel maps domain models to strings):

```swift
// In ViewModel
var speciesDropdownModel: DropdownView.Model {
    DropdownView.Model(
        label: L10n.speciesLabel,
        placeholder: L10n.speciesPlaceholder,
        options: SpeciesTaxonomy.options.map { $0.displayName },  // Map to [String]
        errorMessage: speciesErrorMessage,
        accessibilityID: "animalDescription.speciesDropdown.tap"
    )
}
```

### Generic SelectorView Pattern (Radio Buttons)

**Decision**: Use generic `SelectorView` component accepting `[String]` options for radio button selection.

**Rationale**:
- Similar to DropdownView but displays options as radio buttons (horizontal or vertical layout)
- Can be reused for gender, status, priority, or any mutually-exclusive choice
- ViewModel maps enum cases to display names

**Implementation**:

```swift
struct SelectorView: View {
    let model: Model
    @Binding var selectedIndex: Int?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(model.label)
            
            HStack(spacing: 16) {
                ForEach(model.options.indices, id: \.self) { index in
                    Button(action: { selectedIndex = index }) {
                        HStack {
                            Image(systemName: selectedIndex == index ? "circle.fill" : "circle")
                            Text(model.options[index])
                        }
                    }
                    .accessibilityIdentifier("\(model.accessibilityIDPrefix).\(index)")
                }
            }
            
            if let error = model.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
    }
}

extension SelectorView {
    struct Model {
        let label: String
        let options: [String]               // Generic string array
        let errorMessage: String?
        let accessibilityIDPrefix: String   // e.g., "animalDescription.gender"
    }
}
```

**Usage Pattern**:

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

// In View
SelectorView(
    model: viewModel.genderSelectorModel,
    selectedIndex: $viewModel.selectedGenderIndex
)
.onChange(of: viewModel.selectedGenderIndex) { newIndex in
    if let index = newIndex {
        viewModel.selectGender(Gender.allCases[index])
    }
}
```

### Component Composition: LocationCoordinateView

**Decision**: `LocationCoordinateView` composes two `ValidatedTextField` instances instead of duplicating field logic.

**Rationale**:
- DRY principle - reuse existing `ValidatedTextField` for both lat/long fields
- Simpler component - only adds GPS button + helper text on top of existing field components
- Model composition - `LocationCoordinateView.Model` contains two `ValidatedTextField.Model` instances
- Easier to maintain - changes to text field validation automatically apply to coordinate fields

**Implementation**:

```swift
struct LocationCoordinateView: View {
    let model: Model
    @Binding var latitude: String
    @Binding var longitude: String
    let onGPSButtonTap: () async -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Composed ValidatedTextField for latitude
            ValidatedTextField(model: model.latitudeField, text: $latitude)
            
            // Composed ValidatedTextField for longitude
            ValidatedTextField(model: model.longitudeField, text: $longitude)
            
            // GPS capture button
            Button(action: { Task { await onGPSButtonTap() } }) {
                HStack {
                    Image(systemName: "location.circle")
                    Text(model.gpsButtonTitle)
                }
            }
            .accessibilityIdentifier(model.gpsButtonAccessibilityID)
            
            // Optional helper text (e.g., "GPS capture successful")
            if let helperText = model.helperText {
                Text(helperText)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
    }
}

extension LocationCoordinateView {
    struct Model {
        let latitudeField: ValidatedTextField.Model
        let longitudeField: ValidatedTextField.Model
        let gpsButtonTitle: String
        let gpsButtonAccessibilityID: String
        let helperText: String?
    }
}
```

**Usage Pattern**:

```swift
// In ViewModel - compose two ValidatedTextField models
var locationCoordinateModel: LocationCoordinateView.Model {
    LocationCoordinateView.Model(
        latitudeField: ValidatedTextField.Model(
            label: L10n.latitudeLabel,
            placeholder: "52.2297",
            errorMessage: latitudeErrorMessage,
            isDisabled: false,
            keyboardType: .decimalPad,
            accessibilityID: "animalDescription.latitudeTextField.input"
        ),
        longitudeField: ValidatedTextField.Model(
            label: L10n.longitudeLabel,
            placeholder: "21.0122",
            errorMessage: longitudeErrorMessage,
            isDisabled: false,
            keyboardType: .decimalPad,
            accessibilityID: "animalDescription.longitudeTextField.input"
        ),
        gpsButtonTitle: L10n.requestGPS,
        gpsButtonAccessibilityID: "animalDescription.requestGPSButton.tap",
        helperText: gpsHelperText  // e.g., "Location captured successfully"
    )
}

// In View
LocationCoordinateView(
    model: viewModel.locationCoordinateModel,
    latitude: $viewModel.latitude,
    longitude: $viewModel.longitude,
    onGPSButtonTap: { await viewModel.requestGPSPosition() }
)
```

### References
- Constitution: Principle XI (iOS MVVM-C Architecture) - Model pattern for simple views
- AnimalListViewModel.swift: Reference implementation of ViewModel managing state

---

## 2. Character Counter for Text Areas

### Decision
Implement character counter using custom `TextEditor` wrapper (multi-line) with live character count display and limit enforcement.

### Rationale
- SwiftUI `TextEditor` doesn't have built-in character limit
- Custom wrapper provides real-time character count feedback
- Constitution: All formatting logic in ViewModel/Model, not in views
- Component displays count string computed by ViewModel

### Implementation Pattern

```swift
struct TextAreaView: View {
    let model: Model
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
            
            TextEditor(text: $text)
                .frame(height: 120)
                .border(Color.gray, width: 1)
                .accessibilityIdentifier(model.accessibilityID)
                .onChange(of: text) { newValue in
                    // Enforce character limit (view concern, not formatting)
                    if newValue.count > model.maxLength {
                        text = String(newValue.prefix(model.maxLength))
                    }
                }
            
            // Character counter (formatted string from ViewModel)
            Text(model.characterCountText)
                .font(.caption)
                .foregroundColor(model.characterCountColor)
        }
    }
}

extension TextAreaView {
    struct Model {
        let label: String
        let placeholder: String
        let maxLength: Int
        let characterCountText: String      // e.g., "123/500" (formatted in ViewModel)
        let characterCountColor: Color      // Computed in ViewModel based on limit
        let accessibilityID: String
    }
}
```

### Alternative Considered
- TextEditor with no wrapper → Rejected: No built-in character limit support
- Real-time validation → Rejected: Spec mandates submit-based validation (FR-012)

---

## 3. Coordinate Validation Patterns

### Decision
Validate latitude/longitude ranges on submit using dedicated validation methods in ViewModel.

### Rationale
- Latitude: -90 to 90 (inclusive)
- Longitude: -180 to 180 (inclusive)
- Empty/nil coordinates allowed per spec (optional field)
- Validation triggered on Continue button tap (submit-based per spec clarifications)
- Invalid values show inline error messages and block navigation

### Validation Logic

```swift
func validateCoordinates(lat: String?, long: String?) -> CoordinateValidationResult {
    // Both empty = valid (optional field)
    guard let latStr = lat, let longStr = long, !latStr.isEmpty, !longStr.isEmpty else {
        return .valid
    }
    
    // Parse to Double
    guard let latValue = Double(latStr), let longValue = Double(longStr) else {
        return .invalid(latError: "Invalid format", longError: "Invalid format")
    }
    
    // Range validation
    let latValid = (-90...90).contains(latValue)
    let longValid = (-180...180).contains(longValue)
    
    if !latValid && !longValid {
        return .invalid(latError: "Must be between -90 and 90", longError: "Must be between -180 and 180")
    } else if !latValid {
        return .invalid(latError: "Must be between -90 and 90", longError: nil)
    } else if !longValid {
        return .invalid(latError: nil, longError: "Must be between -180 and 180")
    }
    
    return .valid
}

enum CoordinateValidationResult {
    case valid
    case invalid(latError: String?, longError: String?)
}
```

### References
- Spec FR-010: Lat/Long validation rules
- Spec clarifications (Session 2025-11-28): Validation on submit

---

## 4. Species Taxonomy Bundling Strategy

### Decision
Bundle curated species list as static Swift data structure (enum or struct array) in `/iosApp/iosApp/Data/SpeciesTaxonomy.swift`.

### Rationale
- Spec requirement: Offline-capable, no runtime taxonomy service
- Constitution: Avoid micro-dependencies, minimize external dependencies
- Swift native data structures = zero dependencies, compile-time safety
- Easy to update via code changes (no JSON parsing overhead)

### Implementation Pattern

```swift
// Data/SpeciesTaxonomy.swift
struct SpeciesTaxonomyOption: Identifiable, Equatable {
    let id: String
    let displayName: String
}

struct SpeciesTaxonomy {
    static let options: [SpeciesTaxonomyOption] = [
        SpeciesTaxonomyOption(id: "dog", displayName: "Dog"),
        SpeciesTaxonomyOption(id: "cat", displayName: "Cat"),
        SpeciesTaxonomyOption(id: "bird", displayName: "Bird"),
        SpeciesTaxonomyOption(id: "rabbit", displayName: "Rabbit"),
        SpeciesTaxonomyOption(id: "other", displayName: "Other")
    ]
}
```

### Alternative Considered
- JSON file in bundle → Rejected: Adds parsing overhead, more complex error handling
- Plist file → Rejected: Less type-safe than Swift structs
- Remote API → Rejected: Spec mandates offline capability

---

## 5. Submit-Based Validation Approach

### Decision
Validate all required fields on Continue button tap. Display toast message for validation errors and show inline error text under invalid fields.

### Rationale
- Spec clarifications (Session 2025-11-28): "When should field validation occur? → A: On submit"
- Spec FR-012: Continue button always enabled, validates on tap
- Toast message explains overall validation failure
- Inline error messages identify specific invalid fields
- Continue button remains enabled (no disabled state)

### Validation Flow

```swift
func onContinueTapped() {
    // Reset previous errors
    clearValidationErrors()
    
    // Validate all required fields
    let validationErrors = validateAllFields()
    
    if validationErrors.isEmpty {
        // All valid → update session and navigate
        updateSession()
        onContinue?()  // Coordinator callback
    } else {
        // Show toast + inline errors
        showToast(message: "Please correct the highlighted fields")
        applyValidationErrors(validationErrors)
    }
}

func validateAllFields() -> [ValidationError] {
    var errors: [ValidationError] = []
    
    // Required fields
    if disappearanceDate == nil {
        errors.append(.missingDate)
    }
    if selectedSpecies == nil {
        errors.append(.missingSpecies)
    }
    if race.isEmpty {
        errors.append(.missingRace)
    }
    if selectedGender == nil {
        errors.append(.missingGender)
    }
    
    // Coordinate validation (if provided)
    switch validateCoordinates(lat: latitude, long: longitude) {
    case .valid:
        break
    case .invalid(let latError, let longError):
        if let latError = latError {
            errors.append(.invalidLatitude(latError))
        }
        if let longError = longError {
            errors.append(.invalidLongitude(longError))
        }
    }
    
    return errors
}
```

### Alternative Considered
- Real-time validation → Rejected: Spec clarifications mandate submit-based validation
- Disabled Continue button → Rejected: FR-012 mandates always-enabled Continue button

---

## 6. Session State Mutation Patterns

### Decision
Use in-memory session container injected to ViewModel via constructor. ViewModel updates session properties directly on successful validation.

### Rationale
- Spec clarifications (Session 2025-11-28): "How long should session data persist? → A: Until flow completion or cancellation (in-memory session via constructor-injected container)"
- Spec FR-013: Session container constructor-injected to ViewModel
- Session = reference type (class), mutations visible to coordinator and other steps
- Session lifetime managed by coordinator, not ViewModel

### Session Update Pattern

```swift
class AnimalDescriptionViewModel: ObservableObject {
    private let session: MissingPetFlowSession  // Constructor-injected
    
    init(session: MissingPetFlowSession, locationService: LocationServiceProtocol) {
        self.session = session
        self.locationService = locationService
    }
    
    func updateSession() {
        session.animalDescription = AnimalDescriptionDetails(
            disappearanceDate: disappearanceDate,
            species: selectedSpecies,
            race: race,
            gender: selectedGender,
            age: age.flatMap(Int.init),
            latitude: latitude.flatMap(Double.init),
            longitude: longitude.flatMap(Double.init),
            additionalDescription: additionalDescription
        )
    }
}
```

### References
- Spec 017: Missing Pet flow session container (already implemented)
- Constitution Principle IV: Manual DI with constructor injection (iOS)

---

## 7. Native DatePicker (No Custom Component)

### Decision
Use native SwiftUI `DatePicker` directly in main view (no custom wrapper component).

### Rationale
- **Built-in date range restriction**: `in: ...Date()` parameter proactively blocks future dates
- **No validation needed**: Future dates cannot be selected, so no error state required
- **Native iOS UX**: System date picker is familiar to users, follows iOS design guidelines
- **Accessibility built-in**: VoiceOver and Dynamic Type support out of the box
- **Less code**: No need for custom Model struct, wrapper component, or validation logic
- **Spec compliance**: Edge case requirement "proactively blocks selection of any future dates" is satisfied by native picker

### Implementation Pattern

```swift
// Directly in AnimalDescriptionView (no custom component)
VStack(alignment: .leading, spacing: 4) {
    Text(L10n.dateOfDisappearanceLabel)
    DatePicker(
        "",  // Empty label (custom label above)
        selection: $viewModel.disappearanceDate,
        in: ...Date(),  // Limit to today or past
        displayedComponents: .date
    )
    .datePickerStyle(.compact)  // or .graphical / .wheel
    .labelsHidden()
    .accessibilityIdentifier("animalDescription.datePicker.tap")
}
```

### Alternative Considered
- Custom DatePickerField wrapper → Rejected: Adds unnecessary abstraction, no benefit over native picker
- Manual date validation → Rejected: Proactive blocking is better UX, native picker handles it
- Third-party date picker → Rejected: Native picker is sufficient and zero-dependency

---

## 8. GPS Permission Flow Integration

### Decision
Reuse existing `LocationService` (actor-based Swift Concurrency) from Animal List feature. Handle permission states in ViewModel following same pattern as `AnimalListViewModel`.

### Rationale
- LocationService already exists: `/iosApp/iosApp/Data/LocationService.swift`
- Proven pattern in AnimalListViewModel: async permission request, status tracking, permission denied alert
- Constitution: Swift Concurrency (async/await) mandatory for iOS
- Actor isolation ensures thread safety

### Permission Handling Pattern

```swift
@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    @Published var showPermissionDeniedAlert = false
    
    private let locationService: LocationServiceProtocol
    
    func requestGPSPosition() async {
        // Check current status
        let status = await locationService.authorizationStatus
        
        // Request if not determined (system alert shown automatically)
        if status == .notDetermined {
            let newStatus = await locationService.requestWhenInUseAuthorization()
            if newStatus.isAuthorized {
                await fetchLocation()
            } else {
                showPermissionDeniedAlert = true
            }
        } else if status.isAuthorized {
            await fetchLocation()
        } else {
            // Denied/restricted → show custom alert
            showPermissionDeniedAlert = true
        }
    }
    
    func fetchLocation() async {
        guard let location = await locationService.requestLocation() else {
            // Location fetch failed (show error toast or helper text)
            return
        }
        
        // Update latitude/longitude fields
        latitude = String(format: "%.5f", location.latitude)
        longitude = String(format: "%.5f", location.longitude)
    }
}
```

### References
- AnimalListViewModel.swift: Lines 150-188 (loadAnimals method with permission handling)
- LocationService.swift: Actor-based implementation with Swift Concurrency
- Spec FR-009: GPS permission flow requirement

---

## Summary

All research tasks completed. Key decisions:

1. **Form Components**: Model pattern (not ViewModel), validation errors passed from parent ViewModel
2. **Character Counter**: Custom TextEditor wrapper with live count, limit enforcement in view layer
3. **Coordinate Validation**: Submit-based validation with inline errors, ranges: lat [-90, 90], long [-180, 180]
4. **Species Taxonomy**: Static Swift struct array bundled in app (no JSON, no API)
5. **Submit Validation**: Always-enabled Continue button, validate on tap, toast + inline errors
6. **Session State**: Constructor-injected reference type (class), direct property mutation in ViewModel
7. **Date Picker**: SwiftUI DatePicker with `in: ...Date()` to block future dates
8. **GPS Permission**: Reuse existing LocationService, follow AnimalListViewModel pattern

No NEEDS CLARIFICATION remaining. Ready to proceed to Phase 1 (data-model.md, contracts/).

