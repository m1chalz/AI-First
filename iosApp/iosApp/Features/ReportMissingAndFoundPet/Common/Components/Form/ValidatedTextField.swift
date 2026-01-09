import SwiftUI

/// Reusable text field component with validation error display.
/// Uses Model pattern for pure presentation without @Published properties.
struct ValidatedTextField: View {
    let model: Model
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(model.isDisabled ? Color(hex: "#93A2B4") : Color(hex: "#364153"))
            
            // Text field
            TextField(model.placeholder, text: $text)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .frame(height: 41.323)
                .background(model.isDisabled ? Color(hex: "#F9F9FA").opacity(0.5) : Color.white)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(
                            model.errorMessage != nil ? Color(hex: "#FB2C36") : Color(hex: "#D1D5DC"),
                            lineWidth: 0.667
                        )
                )
                .keyboardType(model.keyboardType)
                .disabled(model.isDisabled)
                .accessibilityIdentifier(model.accessibilityID)
                .onChange(of: text) { _, newValue in
                    // Enforce character limit if maxLength is set
                    if let maxLength = model.maxLength, newValue.count > maxLength {
                        text = String(newValue.prefix(maxLength))
                    }
                }
            
            // Error message
            if let error = model.errorMessage {
                Text(error)
                    .font(.custom("Hind-Regular", size: 12))
                    .foregroundColor(Color(hex: "#FB2C36"))
            }
        }
    }
}
