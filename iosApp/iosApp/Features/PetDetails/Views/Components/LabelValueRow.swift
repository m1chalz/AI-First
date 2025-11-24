import SwiftUI

/// Model for configuring a label-value row component
struct LabelValueRowModel: Equatable {
    /// Label text (e.g., "Date of Disappearance")
    let label: String
    
    /// Value text (e.g., "Nov 18, 2025")
    let value: String
    
    /// Optional processor to format value before display
    let valueProcessor: ((String) -> String)?
    
    /// Optional tap handler for interactive values (e.g., phone, email)
    let onTap: (() -> Void)?
    
    init(
        label: String,
        value: String,
        valueProcessor: ((String) -> String)? = nil,
        onTap: (() -> Void)? = nil
    ) {
        self.label = label
        self.value = value
        self.valueProcessor = valueProcessor
        self.onTap = onTap
    }
    
    // Equatable conformance (ignore closures)
    static func == (lhs: LabelValueRowModel, rhs: LabelValueRowModel) -> Bool {
        lhs.label == rhs.label && lhs.value == rhs.value
    }
}

/// Reusable component displaying a label-value pair in a vertical layout (label on top, value below)
struct LabelValueRow: View {
    let model: LabelValueRowModel
    
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
        Text(processedValue)
            .font(.system(size: 16))
            .frame(maxWidth: .infinity, alignment: .leading)
    }
    
    private var processedValue: String {
        if let processor = model.valueProcessor {
            return processor(model.value)
        }
        return model.value
    }
}

// MARK: - Previews

#if DEBUG
struct LabelValueRow_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 0) {
            // Simple row
            LabelValueRow(model: LabelValueRowModel(
                label: "Species",
                value: "Dog"
            ))
            
            Divider()
            
            // Row with value processor
            LabelValueRow(model: LabelValueRowModel(
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
            LabelValueRow(model: LabelValueRowModel(
                label: "Phone",
                value: "+48 123 456 789",
                onTap: {
                    print("Phone tapped")
                }
            ))
            
            Divider()
            
            // Row with fallback value
            LabelValueRow(model: LabelValueRowModel(
                label: "Email",
                value: "—"
            ))
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif

