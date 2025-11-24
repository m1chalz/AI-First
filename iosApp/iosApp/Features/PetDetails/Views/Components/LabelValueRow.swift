import SwiftUI

/// Reusable component displaying a label-value pair in a vertical layout (label on top, value below)
struct LabelValueRow: View {
    let model: Model
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            // Label (top, secondary color)
            Text(model.label)
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#6a7282"))
            
            // Value (bottom, primary color or interactive)
            if let onTap = model.onTap {
                Button(action: onTap) {
                    valueText
                        .foregroundColor(Color(hex: "#101828"))
                }
            } else {
                valueText
                    .foregroundColor(Color(hex: "#101828"))
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
    
    private var valueText: some View {
        Text(model.value)
            .font(.system(size: 16))
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

// MARK: - Previews

#if DEBUG
struct LabelValueRow_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 0) {
            // Simple row
            LabelValueRow(model: .init(
                label: "Species",
                value: "Dog"
            ))
            
            Divider()
            
            // Row with value processor
            LabelValueRow(model: .init(
                label: "Sex",
                value: "MALE",
                valueProcessor: { gender in
                    switch gender {
                    case "MALE": return "♂ Male"
                    case "FEMALE": return "♀ Female"
                    default: return "? Unknown"
                    }
                }
            ))
            
            Divider()
            
            // Interactive row (tappable)
            LabelValueRow(model: .init(
                label: "Phone",
                value: "+48 123 456 789",
                onTap: {
                    print("Phone tapped")
                }
            ))
            
            Divider()
            
            // Row with fallback value
            LabelValueRow(model: .init(
                label: "Email",
                value: "—"
            ))
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif

