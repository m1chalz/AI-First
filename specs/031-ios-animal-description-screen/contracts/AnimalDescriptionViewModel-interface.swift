// INTERFACE CONTRACT (reference documentation, not compiled)
// Actual implementation: /iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/AnimalDescriptionViewModel.swift

import Foundation
import SwiftUI

/// ViewModel for Animal Description screen (Step 3 of Missing Pet flow).
/// Follows iOS MVVM-C architecture with ObservableObject pattern.
@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    
    // MARK: - Published State (Reactive UI Updates)
    
    /// Date of animal disappearance (required, defaults to today).
    @Published var disappearanceDate: Date
    
    /// Selected species from curated list (required).
    @Published var selectedSpecies: SpeciesTaxonomyOption?
    
    /// Animal breed/race text input (required, cleared when species changes).
    @Published var race: String
    
    /// Selected gender (required).
    @Published var selectedGender: Gender?
    
    /// Animal age in years (optional, 0-40 if provided).
    @Published var age: String
    
    /// Latitude coordinate string (optional, -90 to 90 if provided).
    @Published var latitude: String
    
    /// Longitude coordinate string (optional, -180 to 180 if provided).
    @Published var longitude: String
    
    /// Additional description text (optional, max 500 characters).
    @Published var additionalDescription: String
    
    /// Controls display of GPS permission denied alert.
    @Published var showPermissionDeniedAlert: Bool
    
    /// Controls display of toast message for validation errors.
    @Published var showToast: Bool
    
    /// Toast message text (displayed when showToast = true).
    @Published var toastMessage: String
    
    // MARK: - Validation Error State
    
    /// Error message for date field (nil if valid).
    @Published var dateErrorMessage: String?
    
    /// Error message for species field (nil if valid).
    @Published var speciesErrorMessage: String?
    
    /// Error message for race field (nil if valid).
    @Published var raceErrorMessage: String?
    
    /// Error message for gender field (nil if valid).
    @Published var genderErrorMessage: String?
    
    /// Error message for age field (nil if valid).
    @Published var ageErrorMessage: String?
    
    /// Error message for latitude field (nil if valid).
    @Published var latitudeErrorMessage: String?
    
    /// Error message for longitude field (nil if valid).
    @Published var longitudeErrorMessage: String?
    
    // MARK: - Computed Properties (for Component Models)
    
    /// Character counter text for description field (formatted string).
    var characterCountText: String { get }
    
    /// Character counter color for description field.
    var characterCountColor: Color { get }
    
    /// Available species options from curated taxonomy.
    var speciesOptions: [SpeciesTaxonomyOption] { get }
    
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
    func selectSpecies(_ species: SpeciesTaxonomyOption)
    
    /// Handles gender selection change.
    /// - Parameter gender: Newly selected gender option.
    func selectGender(_ gender: Gender)
    
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
    /// Returns array of validation errors (empty if all valid).
    private func validateAllFields() -> [ValidationError]
    
    /// Validates latitude and longitude coordinate ranges.
    /// Returns CoordinateValidationResult with specific field errors.
    private func validateCoordinates() -> CoordinateValidationResult
    
    /// Updates session container with current form data.
    /// Called only when validation passes and user taps Continue.
    private func updateSession()
    
    /// Clears all validation error messages.
    /// Called at start of validation cycle.
    private func clearValidationErrors()
    
    /// Applies validation errors to respective field error properties.
    /// - Parameter errors: Array of validation errors from validateAllFields().
    private func applyValidationErrors(_ errors: [ValidationError])
    
    /// Fetches user location from LocationService.
    /// Populates latitude and longitude fields with formatted values.
    private func fetchLocation() async
}

