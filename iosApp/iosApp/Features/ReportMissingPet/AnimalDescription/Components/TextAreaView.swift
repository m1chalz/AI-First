import SwiftUI

/// Multi-line text area component with character counter.
/// Enforces hard character limit by truncating input at maxLength.
struct TextAreaView: View {
    let model: Model
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
                .font(.headline)
            
            TextEditor(text: $text)
                .frame(height: 120)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color.gray, lineWidth: 1)
                )
                .accessibilityIdentifier(model.accessibilityID)
                .onChange(of: text) { newValue in
                    // Enforce character limit (hard limit: prevent input at limit, truncate paste)
                    if newValue.count > model.maxLength {
                        text = String(newValue.prefix(model.maxLength))
                    }
                }
            
            // Placeholder text when empty (TextEditor doesn't have native placeholder)
            if text.isEmpty {
                Text(model.placeholder)
                    .foregroundColor(.secondary)
                    .padding(.horizontal, 5)
                    .padding(.vertical, 8)
                    .allowsHitTesting(false)
            }
            
            // Character counter
            HStack {
                Spacer()
                Text(model.characterCountText)
                    .font(.caption)
                    .foregroundColor(model.characterCountColor)
            }
        }
    }
}

extension TextAreaView {
    struct Model {
        let label: String
        let placeholder: String
        let maxLength: Int
        let characterCountText: String      // e.g., "123/500" (formatted in ViewModel)
        let characterCountColor: Color      // Computed in ViewModel based on limit
        let accessibilityID: String
    }
}

