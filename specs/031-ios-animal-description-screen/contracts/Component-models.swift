// COMPONENT MODEL CONTRACTS (reference documentation, not compiled)
// Actual implementations: /iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/Components/*.swift

import SwiftUI

// MARK: - ValidatedTextField.Model

extension ValidatedTextField {
    /// Model for reusable text field component with validation error display.
    struct Model {
        /// Label displayed above text field.
        let label: String
        
        /// Placeholder text shown when field is empty.
        let placeholder: String
        
        /// Error message displayed below field (nil if no error).
        let errorMessage: String?
        
        /// Whether field is disabled for user input.
        let isDisabled: Bool
        
        /// Keyboard type for text input.
        let keyboardType: UIKeyboardType
        
        /// Accessibility identifier for UI testing.
        let accessibilityID: String
        
        init(
            label: String,
            placeholder: String = "",
            errorMessage: String? = nil,
            isDisabled: Bool = false,
            keyboardType: UIKeyboardType = .default,
            accessibilityID: String
        )
    }
}

// MARK: - SpeciesDropdown.Model

extension SpeciesDropdown {
    /// Model for species dropdown component with validation error display.
    struct Model {
        /// Label displayed above dropdown.
        let label: String
        
        /// Placeholder text shown when no selection made.
        let placeholder: String
        
        /// Available species options from curated taxonomy.
        let options: [SpeciesTaxonomyOption]
        
        /// Error message displayed below dropdown (nil if no error).
        let errorMessage: String?
        
        /// Accessibility identifier for UI testing.
        let accessibilityID: String
    }
}

// MARK: - GenderSelector.Model

extension GenderSelector {
    /// Model for male/female radio button component.
    struct Model {
        /// Label displayed above gender options.
        let label: String
        
        /// Error message displayed below options (nil if no error).
        let errorMessage: String?
        
        /// Accessibility identifier prefix (e.g., "animalDescription.gender").
        /// Component appends ".male" and ".female" for individual buttons.
        let accessibilityIDPrefix: String
    }
}

// MARK: - LocationCoordinateFields.Model

extension LocationCoordinateFields {
    /// Model for latitude/longitude input fields with GPS capture button.
    struct Model {
        /// Label for latitude text field.
        let latitudeLabel: String
        
        /// Label for longitude text field.
        let longitudeLabel: String
        
        /// Error message for latitude field (nil if no error).
        let latitudeError: String?
        
        /// Error message for longitude field (nil if no error).
        let longitudeError: String?
        
        /// Title for GPS capture button.
        let gpsButtonTitle: String
        
        /// Accessibility identifier for GPS button.
        let gpsButtonAccessibilityID: String
        
        /// Accessibility identifier for latitude field.
        let latitudeAccessibilityID: String
        
        /// Accessibility identifier for longitude field.
        let longitudeAccessibilityID: String
        
        /// Optional helper text displayed below fields (e.g., "GPS capture successful").
        let helperText: String?
    }
}

// MARK: - DescriptionTextArea.Model

extension DescriptionTextArea {
    /// Model for multi-line text area with character counter.
    struct Model {
        /// Label displayed above text area.
        let label: String
        
        /// Placeholder text shown when area is empty.
        let placeholder: String
        
        /// Maximum character limit for text input.
        let maxLength: Int
        
        /// Character counter text (formatted by ViewModel, e.g., "123/500").
        let characterCountText: String
        
        /// Character counter color (computed by ViewModel based on proximity to limit).
        let characterCountColor: Color
        
        /// Accessibility identifier for UI testing.
        let accessibilityID: String
    }
}

// MARK: - DatePickerField.Model

extension DatePickerField {
    /// Model for date picker component with label.
    struct Model {
        /// Label displayed above date picker.
        let label: String
        
        /// Date range restriction (e.g., ...Date() for today or past).
        let dateRange: PartialRangeThrough<Date>
        
        /// Accessibility identifier for UI testing.
        let accessibilityID: String
    }
}

