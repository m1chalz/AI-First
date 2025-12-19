import SwiftUI

/// Reusable hero panel view displaying title and two action buttons.
/// Can be used on any screen requiring a prominent call-to-action section.
///
/// **Design**: Blue-to-purple gradient background with left-aligned title and buttons.
struct HeroPanelView: View {
    let model: Model
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Title (left-aligned like buttons)
            Text(model.title)
                .font(.system(size: 20, weight: .semibold))
                .foregroundColor(Color(hex: "#101828"))
                .accessibilityIdentifier(model.titleAccessibilityId)
            
            // Buttons row - width controlled by FloatingActionButtonModel.expandsHorizontally
            HStack(spacing: 12) {
                // Left button
                FloatingActionButton(
                    model: model.leftButton,
                    action: model.onLeftButtonTap
                )
                .accessibilityIdentifier(model.leftButtonAccessibilityId)
                
                // Right button
                FloatingActionButton(
                    model: model.rightButton,
                    action: model.onRightButtonTap
                )
                .accessibilityIdentifier(model.rightButtonAccessibilityId)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 16)
        .padding(.vertical, 24)
        .background(
            LinearGradient(
                gradient: Gradient(colors: [
                    Color(hex: "#EFF6FF"),
                    Color(hex: "#E0E7FF")
                ]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
    }
}

// MARK: - Preview

#Preview("Hero Panel") {
    VStack {
        HeroPanelView(
            model: HeroPanelView.Model(
                title: "Quick Actions",
                leftButton: FloatingActionButtonModel(
                    title: "Option A",
                    style: .secondary,
                    iconSource: .sfSymbol("star")
                ),
                rightButton: FloatingActionButtonModel(
                    title: "Option B",
                    style: .primary,
                    iconSource: .sfSymbol("heart")
                ),
                onLeftButtonTap: { print("A tapped") },
                onRightButtonTap: { print("B tapped") },
                titleAccessibilityId: "preview.hero.title",
                leftButtonAccessibilityId: "preview.hero.optionA",
                rightButtonAccessibilityId: "preview.hero.optionB"
            )
        )
        Spacer()
    }
}
