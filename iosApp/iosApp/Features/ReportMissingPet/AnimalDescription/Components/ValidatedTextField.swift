import SwiftUI

/// Reusable text field component with validation error display.
/// Uses Model pattern for pure presentation without @Published properties.
struct ValidatedTextField: View {
    let model: Model
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
                .font(.headline)
            
            TextField(model.placeholder, text: $text)
                .textFieldStyle(.roundedBorder)
                .keyboardType(model.keyboardType)
                .disabled(model.isDisabled)
                .accessibilityIdentifier(model.accessibilityID)
            
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

