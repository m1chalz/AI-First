import SwiftUI

/**
 * Reusable floating action button component.
 * Used for primary/secondary actions floating above content.
 *
 * Supports two styles per Figma design (node 71:7472):
 * - Primary: Dark background (#2D2D2D), white text, larger padding
 * - Secondary: Light background (#EFF4F8), dark text, smaller padding
 *
 * - Parameter title: Button label text
 * - Parameter style: Visual style (primary or secondary)
 * - Parameter action: Callback when button is tapped
 */
struct FloatingActionButton: View {
    enum Style {
        case primary
        case secondary
        
        var backgroundColor: Color {
            switch self {
            case .primary: return Color(hex: "#2D2D2D")
            case .secondary: return Color(hex: "#EFF4F8")
            }
        }
        
        var foregroundColor: Color {
            switch self {
            case .primary: return .white
            case .secondary: return Color(hex: "#2D2D2D")
            }
        }
        
        var horizontalPadding: CGFloat {
            switch self {
            case .primary: return 21
            case .secondary: return 10
            }
        }
        
        var verticalPadding: CGFloat {
            switch self {
            case .primary: return 21
            case .secondary: return 10
            }
        }
    }
    
    let title: String
    let style: Style
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 14))
                .foregroundColor(style.foregroundColor)
                .padding(.horizontal, style.horizontalPadding)
                .padding(.vertical, style.verticalPadding)
                .background(style.backgroundColor)
                .cornerRadius(16)
                .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 4)
        }
    }
}

// MARK: - Preview

#Preview {
    ZStack {
        Color.gray.opacity(0.2)
            .ignoresSafeArea()
        
        VStack(spacing: 30) {
            FloatingActionButton(
                title: "Report a Missing Animal",
                style: .primary,
                action: { print("Primary tapped") }
            )
            
            FloatingActionButton(
                title: "Report Found Animal",
                style: .secondary,
                action: { print("Secondary tapped") }
            )
        }
    }
}

