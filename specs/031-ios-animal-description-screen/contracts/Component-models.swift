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

// MARK: - DropdownView.Model

extension DropdownView {
    /// Model for generic dropdown component with validation error display.
    struct Model {
        /// Label displayed above dropdown.
        let label: String
        
        /// Placeholder text shown when no selection made.
        let placeholder: String
        
        /// Available options for selection.
        let options: [String]
        
        /// Error message displayed below dropdown (nil if no error).
        let errorMessage: String?
        
        /// Accessibility identifier for UI testing.
        let accessibilityID: String
    }
}

// MARK: - SelectorView.Model

extension SelectorView {
    /// Model for generic radio button selector component.
    struct Model {
        /// Label displayed above selector options.
        let label: String
        
        /// Available options for selection (displayed as radio buttons).
        let options: [String]
        
        /// Error message displayed below options (nil if no error).
        let errorMessage: String?
        
        /// Accessibility identifier prefix (e.g., "animalDescription.gender").
        /// Component appends option index for individual buttons (e.g., ".0", ".1").
        let accessibilityIDPrefix: String
    }
}

// MARK: - LocationCoordinateView.Model

extension LocationCoordinateView {
    /// Model for latitude/longitude coordinate input with GPS capture.
    /// Composes two ValidatedTextField models + GPS button properties.
    struct Model {
        /// Model for latitude text field.
        let latitudeField: ValidatedTextField.Model
        
        /// Model for longitude text field.
        let longitudeField: ValidatedTextField.Model
        
        /// Title for GPS capture button.
        let gpsButtonTitle: String
        
        /// Accessibility identifier for GPS button.
        let gpsButtonAccessibilityID: String
        
        /// Optional helper text displayed below fields (e.g., "GPS capture successful").
        let helperText: String?
    }
}

// MARK: - TextAreaView.Model

extension TextAreaView {
    /// Model for generic multi-line text area with character counter.
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

// NOTE: DatePicker uses native SwiftUI DatePicker (inline, no custom component needed)
// Usage example:
//   DatePicker("", selection: $date, in: ...Date(), displayedComponents: .date)
//     .labelsHidden()
//     .accessibilityIdentifier("screen.datePicker.tap")

