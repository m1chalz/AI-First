import SwiftUI

/// Generic radio button selector component accepting [String] options.
/// Uses Model pattern for pure presentation without @Published properties.
struct SelectorView: View {
    let model: Model
    @Binding var selectedIndex: Int?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Radio buttons
            HStack(spacing: 8) {
                ForEach(model.options.indices, id: \.self) { index in
                    Button(action: { selectedIndex = index }) {
                        HStack(spacing: 12) {
                            Image(systemName: selectedIndex == index ? "record.circle" : "circle")
                                .font(.system(size: 24))
                                .foregroundColor(Color(hex: "#616161"))
                            Text(model.options[index])
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
                    .accessibilityIdentifier("\(model.accessibilityIDPrefix).\(model.options[index].lowercased()).tap")
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
        let options: [String]
        let errorMessage: String?
        let accessibilityIDPrefix: String
    }
}

