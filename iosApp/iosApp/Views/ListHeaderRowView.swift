import SwiftUI

/// Reusable list header row displaying section title and action button.
/// Can be used above any list requiring a header with navigation action.
///
/// **Design**: Left-aligned title with right-aligned action link.
struct ListHeaderRowView: View {
    let model: Model
    
    var body: some View {
        HStack {
            // Section title
            Text(model.title)
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(Color(hex: "#101828"))
                .accessibilityIdentifier(model.titleAccessibilityId)
            
            Spacer()
            
            // Action button
            Button(action: model.onActionTap) {
                Text(model.actionTitle)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(Color(hex: "#155DFC"))
            }
            .accessibilityIdentifier(model.actionAccessibilityId)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

// MARK: - Preview

#Preview("List Header Row") {
    VStack {
        ListHeaderRowView(
            model: ListHeaderRowView.Model(
                title: "Section Title",
                actionTitle: "View All",
                onActionTap: { print("View All tapped") },
                titleAccessibilityId: "preview.header.title",
                actionAccessibilityId: "preview.header.action"
            )
        )
        
        ForEach(0..<3) { index in
            Text("List item \(index + 1)")
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.gray.opacity(0.1))
        }
        
        Spacer()
    }
}
