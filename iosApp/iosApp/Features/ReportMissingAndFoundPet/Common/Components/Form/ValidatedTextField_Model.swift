import UIKit

extension ValidatedTextField {
    struct Model {
        let label: String
        let placeholder: String
        let errorMessage: String?
        let isDisabled: Bool
        let keyboardType: UIKeyboardType
        let maxLength: Int?
        let accessibilityID: String
        
        init(
            label: String,
            placeholder: String = "",
            errorMessage: String? = nil,
            isDisabled: Bool = false,
            keyboardType: UIKeyboardType = .default,
            maxLength: Int? = nil,
            accessibilityID: String
        ) {
            self.label = label
            self.placeholder = placeholder
            self.errorMessage = errorMessage
            self.isDisabled = isDisabled
            self.keyboardType = keyboardType
            self.maxLength = maxLength
            self.accessibilityID = accessibilityID
        }
    }
}

