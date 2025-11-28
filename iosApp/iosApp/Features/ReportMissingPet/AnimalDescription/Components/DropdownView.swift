import SwiftUI

/// Generic dropdown component with typed values.
/// Uses Model pattern for pure presentation without @Published properties.
/// T can be any type (enum, struct, etc.) paired with display string.
/// T must be Equatable to find selected option in list.
struct DropdownView<T: Equatable>: View {
    let model: Model
    @Binding var selectedValue: T?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Dropdown menu
            Menu {
                ForEach(model.options.indices, id: \.self) { index in
                    Button(model.options[index].displayName) {
                        selectedValue = model.options[index].value
                    }
                }
            } label: {
                HStack(spacing: 0) {
                    // Selected value or placeholder
                    Text(selectedDisplayName ?? model.placeholder)
                        .font(.custom("Hind-Regular", size: 16))
                        .foregroundColor(selectedValue == nil ? Color(hex: "#0A0A0A").opacity(0.5) : Color(hex: "#364153"))
                    
                    Spacer()
                    
                    // Chevron icon
                    Image(systemName: "chevron.down")
                        .font(.system(size: 20))
                        .foregroundColor(Color(hex: "#364153"))
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .frame(height: 41.323)
                .background(Color(hex: "#F3F3F5"))
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color(hex: "#D1D5DC"), lineWidth: 0.667)
                )
            }
            .accessibilityIdentifier(model.accessibilityID)
            
            // Error message
            if let error = model.errorMessage {
                Text(error)
                    .font(.custom("Hind-Regular", size: 12))
                    .foregroundColor(.red)
            }
        }
    }
    
    /// Display name for currently selected value
    private var selectedDisplayName: String? {
        guard let value = selectedValue else { return nil }
        return model.options.first { $0.value == value }?.displayName
    }
}

extension DropdownView {
    struct Model {
        let label: String
        let placeholder: String
        let options: [(value: T, displayName: String)]
        let errorMessage: String?
        let accessibilityID: String
    }
}

