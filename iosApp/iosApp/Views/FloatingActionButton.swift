import SwiftUI

/**
 * Reusable floating action button component.
 * Used for primary/secondary actions floating above content.
 *
 * Supports two styles per Figma design (node 974:4667):
 * - Primary: Blue gradient, white text (for affirmative actions like "Found Pet")
 * - Secondary: Red/orange gradient, white text (for alert actions like "Lost Pet")
 *
 * Supports SF Symbols and asset images via `IconSource`.
 * Icon position configurable via `IconPosition` (left/right of text).
 *
 * - Parameter model: Button presentation data (FloatingActionButtonModel)
 * - Parameter action: Callback when button is tapped
 */
struct FloatingActionButton: View {
    
    // MARK: - Style
    
    enum Style {
        /// Blue gradient - for affirmative/primary actions (e.g., "Found Pet")
        case primary
        /// Red/orange gradient - for alert/urgent actions (e.g., "Lost Pet")
        case secondary
        
        var background: some ShapeStyle {
            switch self {
            case .primary:
                return LinearGradient(
                    colors: [Color(hex: "#155DFC"), Color(hex: "#0D47C7")],
                    startPoint: .top,
                    endPoint: .bottom
                )
            case .secondary:
                return LinearGradient(
                    colors: [Color(hex: "#FB2C36"), Color(hex: "#E8242D")],
                    startPoint: .top,
                    endPoint: .bottom
                )
            }
        }
        
        var foregroundColor: Color { .white }
        
        var horizontalPadding: CGFloat { 21 }
        var verticalPadding: CGFloat { 14 }
    }
    
    // MARK: - Icon Position
    
    enum IconPosition {
        /// Icon displayed before text (default)
        case left
        /// Icon displayed after text
        case right
    }
    
    // MARK: - Icon Source
    
    enum IconSource {
        /// Asset catalog image (existing use case)
        case asset(String)
        /// SF Symbol (system icons)
        case sfSymbol(String)
        
        var image: Image {
            switch self {
            case .asset(let name):
                return Image(name).renderingMode(.template)
            case .sfSymbol(let name):
                return Image(systemName: name)
            }
        }
    }
    
    // MARK: - Properties
    
    let model: FloatingActionButtonModel
    let action: () -> Void
    
    // MARK: - Body
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                // Left icon
                if model.iconPosition == .left, let iconSource = model.iconSource {
                    iconView(for: iconSource)
                }
                
                Text(model.title)
                    .font(.system(size: 14, weight: .semibold))
                
                // Right icon
                if model.iconPosition == .right, let iconSource = model.iconSource {
                    iconView(for: iconSource)
                }
            }
            .foregroundColor(model.style.foregroundColor)
            .frame(maxWidth: model.expandsHorizontally ? .infinity : nil)
            .padding(.horizontal, model.style.horizontalPadding)
            .padding(.vertical, model.style.verticalPadding)
            .background(model.style.background)
            .cornerRadius(16)
            .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 4)
        }
    }
    
    // MARK: - Icon View
    
    @ViewBuilder
    private func iconView(for source: IconSource) -> some View {
        source.image
            .resizable()
            .scaledToFit()
            .frame(width: 16, height: 16)
    }
}

// MARK: - Preview

#Preview {
    ZStack {
        Color.gray.opacity(0.2)
            .ignoresSafeArea()
        
        VStack(spacing: 30) {
            // Primary style (blue gradient) - Found Pet
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: "Found Pet",
                    style: .primary,
                    iconSource: .sfSymbol("checkmark")
                ),
                action: { print("Found Pet tapped") }
            )
            
            // Secondary style (red gradient) - Lost Pet
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: "Lost Pet",
                    style: .secondary,
                    iconSource: .sfSymbol("exclamationmark.triangle")
                ),
                action: { print("Lost Pet tapped") }
            )
            
            // Asset icon example
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: "Report a Missing Animal",
                    style: .primary,
                    iconSource: .asset("ic_report_missing_animal"),
                    iconPosition: .right
                ),
                action: { print("Report tapped") }
            )
            
            // No icon example
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: "Simple Button",
                    style: .secondary
                ),
                action: { print("Simple tapped") }
            )
        }
    }
}
