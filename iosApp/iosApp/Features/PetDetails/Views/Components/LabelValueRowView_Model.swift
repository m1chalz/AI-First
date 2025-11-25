import Foundation

extension LabelValueRowView {
    /// Model for configuring a label-value row component
    struct Model: Equatable {
        /// Label text (e.g., "Date of Disappearance")
        let label: String
        
        /// Processed value text ready for display (e.g., "Nov 18, 2025")
        let value: String
        
        /// Optional tap handler for interactive values (e.g., phone, email)
        let onTap: (() -> Void)?
        
        init(
            label: String,
            value: String,
            valueProcessor: ((String) -> String)? = nil,
            onTap: (() -> Void)? = nil
        ) {
            self.label = label
            self.value = valueProcessor?(value) ?? value
            self.onTap = onTap
        }
        
        // Equatable conformance (ignore closures)
        static func == (lhs: Model, rhs: Model) -> Bool {
            lhs.label == rhs.label && lhs.value == rhs.value
        }
    }
}

