import Foundation
import SwiftUI

/// ViewModel for Pet Details screen (Step 2/3 of Found Pet flow).
/// Manages form data, validation, and coordinator callbacks.
/// Combines former ChipNumber + AnimalDescription - adds collar data (microchip) field.
@MainActor
class FoundPetAnimalDescriptionViewModel: ObservableObject {
    // MARK: - Published Properties (Form Data)
    
    /// Date when animal disappeared (required, defaults to today)
    @Published var disappearanceDate: Date = Date()
    
    /// Selected species (nil if not selected)
    @Published var selectedSpecies: AnimalSpecies?
    
    /// Breed/race text input (required, enabled only after species selected)
    @Published var race: String = ""
    
    /// Selected gender (nil if not selected)
    @Published var selectedGender: AnimalGender?
    
    /// Latitude text input (optional, -90 to 90 range)
    @Published var latitude: String = ""
    
    /// Longitude text input (optional, -180 to 180 range)
    @Published var longitude: String = ""
    
    /// Additional description text (optional, max 500 characters)
    @Published var additionalDescription: String = ""
    
    /// Collar data / microchip number (optional, digits only, max 15)
    /// Stored as digits-only string (no dashes)
    @Published var collarData: String = ""
    
    // MARK: - Published Properties (Validation Errors)
    
    @Published var speciesErrorMessage: String?
    @Published var raceErrorMessage: String?
    @Published var genderErrorMessage: String?
    @Published var latitudeErrorMessage: String?
    @Published var longitudeErrorMessage: String?
    
    // MARK: - Published Properties (UI State)
    
    @Published var showToast = false
    @Published var toastMessage = ""
    @Published var showPermissionDeniedAlert = false
    @Published var gpsHelperText: String?
    
    // MARK: - Coordinator Callbacks
    
    var onContinue: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Dependencies
    
    private let flowState: FoundPetReportFlowState
    private let locationHandler: LocationPermissionHandler
    private let toastScheduler: ToastSchedulerProtocol
    
    // MARK: - Initialization
    
    init(
        flowState: FoundPetReportFlowState,
        locationHandler: LocationPermissionHandler,
        toastScheduler: ToastSchedulerProtocol
    ) {
        self.flowState = flowState
        self.locationHandler = locationHandler
        self.toastScheduler = toastScheduler
        
        // Load existing data from flow state if present (returning from Step 4)
        if let existingDate = flowState.disappearanceDate {
            self.disappearanceDate = existingDate
        }
        
        if let existingSpecies = flowState.animalSpecies {
            self.selectedSpecies = existingSpecies
        }
        
        if let existingRace = flowState.animalRace {
            self.race = existingRace
        }
        
        if let existingGender = flowState.animalGender {
            self.selectedGender = existingGender
        }
        
        if let existingLat = flowState.animalLatitude {
            self.latitude = String(format: "%.5f", existingLat)
        }
        
        if let existingLong = flowState.animalLongitude {
            self.longitude = String(format: "%.5f", existingLong)
        }
        
        if let existingDesc = flowState.animalAdditionalDescription {
            self.additionalDescription = existingDesc
        }
        
        // Load collar data (microchip) from flow state
        if let existingChip = flowState.chipNumber {
            self.collarData = existingChip
        }
    }
    
    // MARK: - Computed Properties
    
    /// Formatted collar data for display (00000-00000-00000)
    var formattedCollarData: String {
        MicrochipNumberFormatter.format(collarData)
    }
    
    // MARK: - Computed Properties (Component Models)
    
    /// Model for date input
    var dateInputModel: DateInputView.Model {
        DateInputView.Model(
            label: L10n.AnimalDescription.dateLabel,
            dateRange: ...Date(), // Limit to today or past
            errorMessage: nil, // Date is always valid
            accessibilityID: "animalDescription.datePicker.tap"
        )
    }
    
    /// Model for species dropdown
    var speciesDropdownModel: DropdownView<AnimalSpecies>.Model {
        DropdownView.Model(
            label: L10n.AnimalDescription.speciesLabel,
            placeholder: L10n.AnimalDescription.speciesPlaceholder,
            options: AnimalSpecies.allCases.map { (value: $0, displayName: $0.displayName) },
            errorMessage: speciesErrorMessage,
            accessibilityID: "animalDescription.speciesDropdown.tap"
        )
    }
    
    /// Model for race text field (disabled until species selected)
    var raceTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.AnimalDescription.raceLabel,
            placeholder: L10n.AnimalDescription.racePlaceholder,
            errorMessage: raceErrorMessage,
            isDisabled: selectedSpecies == nil,
            keyboardType: .default,
            accessibilityID: "animalDescription.raceTextField.input"
        )
    }
    
    /// Model for gender selector
    var genderSelectorModel: SelectorView<AnimalGender>.Model {
        SelectorView.Model(
            label: L10n.AnimalDescription.genderLabel,
            options: [
                (value: .male, displayName: AnimalGender.male.displayName),
                (value: .female, displayName: AnimalGender.female.displayName)
            ],
            errorMessage: genderErrorMessage,
            accessibilityIDPrefix: "animalDescription.gender"
        )
    }
    
    /// Model for coordinate input (two fields side by side)
    var coordinateInputModel: CoordinateInputView.Model {
        CoordinateInputView.Model(
            label: L10n.AnimalDescription.coordinatesLabel,
            latitudePlaceholder: L10n.AnimalDescription.coordinatePlaceholder,
            longitudePlaceholder: L10n.AnimalDescription.coordinatePlaceholder,
            latitudeError: latitudeErrorMessage,
            longitudeError: longitudeErrorMessage,
            latitudeAccessibilityID: "animalDescription.latitudeTextField.input",
            longitudeAccessibilityID: "animalDescription.longitudeTextField.input"
        )
    }
    
    /// Model for description text area (optional, max 500 characters)
    var descriptionTextAreaModel: TextAreaView.Model {
        TextAreaView.Model(
            label: L10n.AnimalDescription.descriptionLabel,
            placeholder: L10n.AnimalDescription.descriptionPlaceholder,
            maxLength: 500,
            characterCountText: characterCountText,
            accessibilityID: "animalDescription.descriptionTextArea.input"
        )
    }
    
    /// Character count text for description field (e.g., "123/500")
    var characterCountText: String {
        return "\(additionalDescription.count)/500"
    }
    
    /// Model for collar data text field (optional, digits only)
    var collarDataTextFieldModel: ValidatedTextField.Model {
        ValidatedTextField.Model(
            label: L10n.ReportFoundPet.PetDetails.collarDataLabel,
            placeholder: L10n.ReportFoundPet.PetDetails.collarDataPlaceholder,
            errorMessage: nil, // Collar data is optional - no validation errors
            isDisabled: false,
            keyboardType: .numberPad,
            accessibilityID: "reportFoundPet.petDetails.collarData.input"
        )
    }
    
    // MARK: - User Actions
    
    /// Updates collar data with digit filtering and 15-digit limit
    func updateCollarData(_ newValue: String) {
        let digits = MicrochipNumberFormatter.extractDigits(newValue)
        collarData = String(digits.prefix(MicrochipNumberFormatter.maxDigits))
    }
    
    /// Called when species selection changes
    func handleSpeciesChange() {
        // Clear error when value selected
        speciesErrorMessage = nil
        // Clear race field when species changes (per spec)
        race = ""
        raceErrorMessage = nil
    }
    
    /// Called when race text changes
    func handleRaceChange(_ newValue: String) {
        // Clear error when user types
        if !newValue.isEmpty {
            raceErrorMessage = nil
        }
    }
    
    /// Called when gender selection changes
    func handleGenderChange() {
        // Clear error when value selected
        genderErrorMessage = nil
    }
    
    /// Called when user taps Continue button
    func onContinueTapped() {
        clearValidationErrors()
        
        let errors = validateAllFields()
        
        if errors.isEmpty {
            // All valid → update flow state and navigate
            updateFlowState()
            onContinue?()
        } else {
            // Show toast and inline errors with animation
            showValidationToast()
            applyValidationErrors(errors)
        }
    }
    
    /// Called when user taps Back button
    func onBackTapped() {
        // Do NOT update flow state when going back
        onBack?()
    }
    
    /// Called when user taps GPS button (async)
    func requestGPSPosition() async {
        let result = await locationHandler.requestLocationWithPermissions()
        
        // Show alert for denied/restricted (per-action policy - always show)
        if result.status.shouldShowCustomPopup {
            showPermissionDeniedAlert = true
        }
        
        // Populate fields if location fetched successfully
        if let location = result.location {
            latitude = String(format: "%.5f", location.latitude)
            longitude = String(format: "%.5f", location.longitude)
            gpsHelperText = L10n.AnimalDescription.gpsHelperText
        } else if result.status.isAuthorized {
            // Authorized but location fetch failed
            gpsHelperText = "Failed to get location"
        }
    }
    
    // MARK: - Private Helpers
    
    /// Validates all required and optional fields
    private func validateAllFields() -> [ValidationError] {
        var errors: [ValidationError] = []
        
        // Date is always valid (DatePicker blocks future dates, default is today)
        
        // Species validation (required)
        if selectedSpecies == nil {
            errors.append(.missingSpecies)
        }
        
        // Race validation - optional, no validation needed
        
        // Gender validation (required)
        if selectedGender == nil {
            errors.append(.missingGender)
        }
        
        // Coordinate validation (optional, but if provided must be in range)
        let coordinateValidation = validateCoordinates()
        if case .invalid(let latError, let longError) = coordinateValidation {
            if let latError = latError {
                errors.append(.invalidLatitude(latError))
            }
            if let longError = longError {
                errors.append(.invalidLongitude(longError))
            }
        }
        
        return errors
    }
    
    /// Validates latitude and longitude ranges
    private func validateCoordinates() -> CoordinateValidationResult {
        // Both required - must be filled and valid
        let latTrimmed = latitude.trimmingCharacters(in: .whitespacesAndNewlines)
        let longTrimmed = longitude.trimmingCharacters(in: .whitespacesAndNewlines)
        
        var latError: String?
        var longError: String?
        
        // Validate latitude
        if latTrimmed.isEmpty {
            latError = L10n.AnimalDescription.Error.missingLatitude
        } else if let latValue = Double(latTrimmed) {
            if !(-90...90).contains(latValue) {
                latError = L10n.AnimalDescription.Error.invalidLatitude
            }
        } else {
            latError = L10n.AnimalDescription.Error.invalidCoordinateFormat
        }
        
        // Validate longitude
        if longTrimmed.isEmpty {
            longError = L10n.AnimalDescription.Error.missingLongitude
        } else if let longValue = Double(longTrimmed) {
            if !(-180...180).contains(longValue) {
                longError = L10n.AnimalDescription.Error.invalidLongitude
            }
        } else {
            longError = L10n.AnimalDescription.Error.invalidCoordinateFormat
        }
        
        // Return result
        if latError != nil || longError != nil {
            return .invalid(latError: latError, longError: longError)
        }
        
        return .valid
    }
    
    /// Clears all validation error messages
    private func clearValidationErrors() {
        speciesErrorMessage = nil
        raceErrorMessage = nil
        genderErrorMessage = nil
        latitudeErrorMessage = nil
        longitudeErrorMessage = nil
        toastScheduler.cancel()
        showToast = false
        toastMessage = ""
    }
    
    /// Shows validation error toast with auto-dismiss
    private func showValidationToast() {
        toastScheduler.cancel()
        toastMessage = L10n.AnimalDescription.Toast.validationErrors
        showToast = true
        
        toastScheduler.schedule(duration: 3.0) { [weak self] in
            Task { @MainActor in
                self?.showToast = false
            }
        }
    }
    
    /// Applies validation errors to corresponding fields
    private func applyValidationErrors(_ errors: [ValidationError]) {
        for error in errors {
            switch error.field {
            case .species:
                speciesErrorMessage = error.message
            case .race:
                raceErrorMessage = error.message
            case .gender:
                genderErrorMessage = error.message
            case .latitude:
                latitudeErrorMessage = error.message
            case .longitude:
                longitudeErrorMessage = error.message
            default:
                break
            }
        }
    }
    
    /// Updates flow state with current form data (all fields: required + optional)
    private func updateFlowState() {
        flowState.disappearanceDate = disappearanceDate
        flowState.animalSpecies = selectedSpecies
        flowState.animalRace = race.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : race.trimmingCharacters(in: .whitespacesAndNewlines)
        flowState.animalGender = selectedGender
        
        flowState.animalLatitude = latitude.isEmpty ? nil : Double(latitude)
        flowState.animalLongitude = longitude.isEmpty ? nil : Double(longitude)
        flowState.animalAdditionalDescription = additionalDescription.isEmpty ? nil : additionalDescription
        
        // Collar data (microchip) - store digits-only
        flowState.chipNumber = collarData.isEmpty ? nil : collarData
    }

    deinit {
        toastScheduler.cancel()
        print("deinit FoundPetAnimalDescriptionViewModel")
    }
}

