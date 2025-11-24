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

/// Reusable component displaying a label-value pair in a horizontal layout
struct LabelValueRow: View {
    let model: LabelValueRowModel
    
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            // Label (leading, secondary style)
            Text(model.label)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            // Value (trailing, primary style)
            if let onTap = model.onTap {
                Button(action: onTap) {
                    valueText
                        .foregroundColor(.blue)
                }
            } else {
                valueText
                    .foregroundColor(.primary)
            }
        }
        .padding(.vertical, 8)
    }
    
    private var valueText: some View {
        Text(processedValue)
            .font(.subheadline)
            .frame(maxWidth: .infinity, alignment: .trailing)
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

