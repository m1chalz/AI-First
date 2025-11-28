import SwiftUI

/// Generic radio button selector component with typed values.
/// Uses Model pattern for pure presentation without @Published properties.
/// T can be any type (enum, struct, etc.) paired with display string.
/// T must be Equatable to find selected option in list.
struct SelectorView<T: Equatable>: View {
    let model: Model
    @Binding var selectedValue: T?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Radio buttons
            HStack(spacing: 8) {
                ForEach(model.options.indices, id: \.self) { index in
                    let option = model.options[index]
                    Button(action: { selectedValue = option.value }) {
                        HStack(spacing: 12) {
                            Image(systemName: selectedValue == option.value ? "record.circle" : "circle")
                                .font(.system(size: 24))
                                .foregroundColor(Color(hex: "#616161"))
                            Text(option.displayName)
                                .font(.custom("Hind-Regular", size: 16))
                                .foregroundColor(Color(hex: "#545F71"))
                        }
                        .padding(.horizontal, 8.667)
                        .padding(.vertical, 0.667)
                        .frame(height: 57.333)
                        .frame(maxWidth: .infinity)
                        .overlay(
                            RoundedRectangle(cornerRadius: 4)
                                .stroke(Color(hex: "#E5E9EC"), lineWidth: 0.667)
                        )
                    }
                    .buttonStyle(PlainButtonStyle())
                    .accessibilityIdentifier("\(model.accessibilityIDPrefix).\(option.displayName.lowercased()).tap")
                }
            }
            
            // Error message
            if let error = model.errorMessage {
                Text(error)
                    .font(.custom("Hind-Regular", size: 12))
                    .foregroundColor(.red)
            }
        }
    }
}

extension SelectorView {
    struct Model {
        let label: String
        let options: [(value: T, displayName: String)]
        let errorMessage: String?
        let accessibilityIDPrefix: String
    }
}

