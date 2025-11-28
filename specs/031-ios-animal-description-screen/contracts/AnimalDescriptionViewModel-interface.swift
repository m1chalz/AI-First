// INTERFACE CONTRACT (reference documentation, not compiled)
// Actual implementation: /iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionViewModel.swift

import Foundation
import SwiftUI

/// ViewModel for Animal Description screen (Step 3 of Missing Pet flow).
/// Follows iOS MVVM-C architecture with ObservableObject pattern.
@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    
    // MARK: - Published State (Grouped for Clarity)
    
    /// Form data state (all field values).
    @Published var formData: FormData
    
    /// Validation errors state (all field error messages).
    @Published var validationErrors: ValidationErrors
    
    /// UI state (alerts, toasts, loading indicators).
    @Published var uiState: UIState
    
    // MARK: - Nested State Structures
    
    /// Form field values.
    struct FormData: Equatable {
        var disappearanceDate: Date
        var selectedSpecies: SpeciesTaxonomyOption?
        var race: String
        var selectedGender: Gender?
        var age: String
        var location: LocationData
        var additionalDescription: String
        
        struct LocationData: Equatable {
            var latitude: String
            var longitude: String
        }
        
        static let initial = FormData(
            disappearanceDate: Date(),
            selectedSpecies: nil,
            race: "",
            selectedGender: nil,
            age: "",
            location: LocationData(latitude: "", longitude: ""),
            additionalDescription: ""
        )
    }
    
    /// Validation error messages (nil = no error).
    struct ValidationErrors: Equatable {
        var species: String?
        var race: String?
        var gender: String?
        var age: String?
        var latitude: String?
        var longitude: String?
        
        static let clear = ValidationErrors()
    }
    
    /// UI-related state (alerts, toasts, etc).
    struct UIState: Equatable {
        var showPermissionDeniedAlert: Bool
        var showToast: Bool
        var toastMessage: String
        var gpsHelperText: String?
        
        static let initial = UIState(
            showPermissionDeniedAlert: false,
            showToast: false,
            toastMessage: "",
            gpsHelperText: nil
        )
    }
    
    // MARK: - Computed Properties (for Component Models)
    
    /// Model for race text field (computed from current state + validation errors).
    var raceTextFieldModel: ValidatedTextField.Model { get }
    
    /// Model for age text field (computed from current state + validation errors).
    var ageTextFieldModel: ValidatedTextField.Model { get }
    
    /// Model for species dropdown (computed from current state + validation errors).
    var speciesDropdownModel: DropdownView.Model { get }
    
    /// Model for gender selector (computed from current state + validation errors).
    var genderSelectorModel: SelectorView.Model { get }
    
    /// Model for location coordinate fields (computed from current state + validation errors).
    var locationCoordinateModel: LocationCoordinateView.Model { get }
    
    /// Model for description text area (computed from current state + character count).
    var descriptionTextAreaModel: TextAreaView.Model { get }
    
    /// Character counter text for description field (formatted string).
    var characterCountText: String { get }
    
    /// Character counter color for description field.
    var characterCountColor: Color { get }
    
    // MARK: - Internal Data Access
    
    /// Available species options from curated taxonomy (internal use).
    /// Used to map selected index back to SpeciesTaxonomyOption.
    var speciesOptions: [SpeciesTaxonomyOption] { get }  // ← Internal, not for view
    
    /// Available gender options (internal use).
    /// Used to map selected index back to Gender enum.
    var genderOptions: [Gender] { get }  // ← Gender.allCases
    
    // MARK: - Coordinator Callbacks (Navigation)
    
    /// Called when user taps Continue button and validation passes.
    var onContinue: (() -> Void)?
    
    /// Called when user taps Back button in header.
    var onBack: (() -> Void)?
    
    /// Called when user taps "Go to Settings" in permission alert.
    var onOpenAppSettings: (() -> Void)?
    
    // MARK: - Initialization (Manual DI)
    
    /// Initializes ViewModel with dependencies.
    /// - Parameters:
    ///   - session: Missing Pet flow session container (reference type, constructor-injected).
    ///   - locationService: Location service for GPS coordinate capture (constructor-injected).
    init(session: MissingPetFlowSession, locationService: LocationServiceProtocol)
    
    // MARK: - User Actions
    
    /// Handles species selection change.
    /// Automatically clears race field when species changes.
    /// - Parameter species: Newly selected species option.
    func selectSpecies(_ species: SpeciesTaxonomyOption) {
        formData.selectedSpecies = species
        formData.race = ""  // Clear race when species changes
        validationErrors.race = nil
    }
    
    /// Handles gender selection change.
    /// - Parameter gender: Newly selected gender option.
    func selectGender(_ gender: Gender) {
        formData.selectedGender = gender
        validationErrors.gender = nil
    }
    
    /// Handles "Request GPS position" button tap.
    /// Requests location permission if needed, fetches location, and populates Lat/Long fields.
    /// Shows permission denied alert if permission is denied/restricted.
    func requestGPSPosition() async
    
    /// Handles "Go to Settings" button tap in permission alert.
    /// Delegates to coordinator via onOpenAppSettings closure.
    func openSettings()
    
    /// Handles "Continue" button tap.
    /// Validates all fields on submit, shows errors if invalid, or navigates to Step 4 if valid.
    func onContinueTapped()
    
    /// Handles "Back" button tap.
    /// Calls coordinator callback without updating session.
    func onBackTapped()
    
    // MARK: - Private Methods (implementation details omitted)
    
    /// Validates all required fields (date, species, race, gender).
    /// Also validates optional fields if provided (age range, coordinate ranges).
    /// Returns array of validation errors (empty if all valid).
    /// - Returns: Array of ValidationError cases
    private func validateAllFields() -> [ValidationError]
    
    /// Validates latitude and longitude coordinate ranges.
    /// Returns CoordinateValidationResult with specific field errors.
    private func validateCoordinates() -> CoordinateValidationResult
    
    /// Updates session container with current form data.
    /// Called only when validation passes and user taps Continue.
    private func updateSession()
    
    /// Clears all validation error messages (resets validationErrors to .clear).
    /// Called at start of validation cycle (before validateAllFields).
    /// Triggers computed model properties to recompute without errors.
    private func clearValidationErrors() {
        validationErrors = .clear
    }
    
    /// Applies validation errors to validationErrors struct.
    /// Setting properties triggers view updates (computed models recompute).
    /// - Parameter errors: Array of validation errors from validateAllFields()
    /// Example: .missingRace → sets validationErrors.race = "Please enter race"
    private func applyValidationErrors(_ errors: [ValidationError])
    
    /// Fetches user location from LocationService.
    /// Populates latitude and longitude fields with formatted values.
    private func fetchLocation() async
}

