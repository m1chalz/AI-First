import SwiftUI

/// Generic dropdown component accepting [String] options for maximum reusability.
/// Uses Model pattern for pure presentation without @Published properties.
struct DropdownView: View {
    let model: Model
    @Binding var selectedIndex: Int?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Dropdown menu
            Menu {
                ForEach(model.options.indices, id: \.self) { index in
                    Button(model.options[index]) {
                        selectedIndex = index
                    }
                }
            } label: {
                HStack(spacing: 0) {
                    // Selected value or placeholder
                    Text(selectedIndex.map { model.options[$0] } ?? model.placeholder)
                        .font(.custom("Hind-Regular", size: 16))
                        .foregroundColor(selectedIndex == nil ? Color(hex: "#0A0A0A").opacity(0.5) : Color(hex: "#364153"))
                    
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
}

extension DropdownView {
    struct Model {
        let label: String
        let placeholder: String
        let options: [String]
        let errorMessage: String?
        let accessibilityID: String
    }
}

