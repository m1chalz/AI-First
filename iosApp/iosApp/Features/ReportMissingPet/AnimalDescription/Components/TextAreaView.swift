import SwiftUI

/// Multi-line text area component with character counter.
/// Enforces hard character limit by truncating input at maxLength.
struct TextAreaView: View {
    let model: Model
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Text editor with placeholder overlay
            ZStack(alignment: .topLeading) {
                TextEditor(text: $text)
                    .font(.custom("Hind-Regular", size: 16))
                    .foregroundColor(Color(hex: "#364153"))
                    .frame(height: 96)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(Color.white)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(Color(hex: "#D1D5DC"), lineWidth: 0.667)
                    )
                    .accessibilityIdentifier(model.accessibilityID)
                    .onChange(of: text) { _, newValue in
                        // Enforce character limit (hard limit: prevent input at limit, truncate paste)
                        if newValue.count > model.maxLength {
                            text = String(newValue.prefix(model.maxLength))
                        }
                    }
                
                // Placeholder text when empty (TextEditor doesn't have native placeholder)
                if text.isEmpty {
                    Text(model.placeholder)
                        .font(.custom("Hind-Regular", size: 16))
                        .foregroundColor(Color(hex: "#0A0A0A").opacity(0.5))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 16)
                        .allowsHitTesting(false)
                }
            }
            
            // Character counter
            HStack {
                Spacer()
                Text(model.characterCountText)
                    .font(.custom("Hind-Regular", size: 12))
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

