import SwiftUI

/// Custom teardrop-shaped map pin marker.
///
/// Renders a classic "teardrop" pin shape commonly used in map applications.
/// The pin consists of a circular top with a pointed bottom, creating the
/// iconic map marker silhouette.
///
/// **Usage**:
/// ```swift
/// TeardropPin(mode: .active)                         // Red pin with exclamationmark
/// TeardropPin(mode: .found)                          // Blue pin with checkmark
/// TeardropPin(mode: .closed)                         // Gray pin with xmark
/// TeardropPin(mode: .custom(color: "#FF6600", icon: "star.fill"))  // Custom
/// TeardropPin(color: .red, icon: "exclamationmark")  // Legacy: Missing pet
/// ```
///
/// **Sizing**: Default size is 28x36 points. Use `.frame()` to adjust.
struct TeardropPin: View {
    
    // MARK: - Mode
    
    /// Pin display mode with predefined or custom appearance.
    public enum Mode: Equatable {
        /// Missing pet - red pin with exclamation mark
        case active
        /// Found pet - blue pin with checkmark
        case found
        /// Closed announcement - gray pin with X mark
        case closed
        /// Custom appearance with hex color and SF Symbol icon name
        case custom(color: String, icon: String)
        
        /// Resolved color for this mode
        var color: Color {
            switch self {
            case .active:
                return .red
            case .found:
                return .blue
            case .closed:
                return .gray
            case .custom(let hexColor, _):
                return Color(hex: hexColor)
            }
        }
        
        /// Resolved icon for this mode
        var icon: String? {
            switch self {
            case .active:
                return "exclamationmark"
            case .found:
                return "checkmark"
            case .closed:
                return "xmark"
            case .custom(_, let iconName):
                return iconName
            }
        }
    }
    
    // MARK: - Properties
    
    let color: Color
    let icon: String?
    
    // MARK: - Initializers
    
    /// Creates a pin with a predefined or custom mode.
    init(mode: Mode) {
        self.color = mode.color
        self.icon = mode.icon
    }
    
    /// Creates a pin with explicit color and optional icon (legacy initializer).
    init(color: Color, icon: String? = nil) {
        self.color = color
        self.icon = icon
    }
    
    var body: some View {
        ZStack {
            TeardropShape()
                .fill(color)
                .overlay(
                    TeardropShape()
                        .stroke(Color.white, lineWidth: 3)
                )
            
            // Icon centered in the circular part (top 60% of pin)
            if let icon {
                Image(systemName: icon)
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.white)
                    .offset(y: -4) // Move up into circular part
            }
        }
        .frame(width: 28, height: 36)
        .shadow(color: .black.opacity(0.3), radius: 2, x: 0, y: 2)
    }
}

/// Custom Shape that draws a teardrop/map pin silhouette.
///
/// The shape is drawn with the pointed tip at the bottom (anchor point for map placement).
/// Proportions: circular top tapering to a point at bottom.
struct TeardropShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        
        let width = rect.width
        let height = rect.height
        
        // Circle takes up roughly top 60% of height
        let circleRadius = width / 2
        let circleCenter = CGPoint(x: rect.midX, y: circleRadius)
        
        // Bottom point (tip of the pin)
        let bottomPoint = CGPoint(x: rect.midX, y: height)
        
        // Start at bottom point
        path.move(to: bottomPoint)
        
        // Draw left curve from bottom to circle
        path.addCurve(
            to: CGPoint(x: circleCenter.x - circleRadius, y: circleCenter.y),
            control1: CGPoint(x: rect.midX - width * 0.1, y: height * 0.75),
            control2: CGPoint(x: circleCenter.x - circleRadius, y: circleCenter.y + circleRadius * 0.6)
        )
        
        // Draw arc around top (full semicircle + more)
        path.addArc(
            center: circleCenter,
            radius: circleRadius,
            startAngle: .degrees(180),
            endAngle: .degrees(0),
            clockwise: false
        )
        
        // Draw right curve from circle back to bottom
        path.addCurve(
            to: bottomPoint,
            control1: CGPoint(x: circleCenter.x + circleRadius, y: circleCenter.y + circleRadius * 0.6),
            control2: CGPoint(x: rect.midX + width * 0.1, y: height * 0.75)
        )
        
        path.closeSubpath()
        
        return path
    }
}

// MARK: - Previews

#Preview("Active Pin (Missing)") {
    TeardropPin(mode: .active)
        .padding()
}

#Preview("Found Pin") {
    TeardropPin(mode: .found)
        .padding()
}

#Preview("Closed Pin") {
    TeardropPin(mode: .closed)
        .padding()
}

#Preview("Custom Pin") {
    TeardropPin(mode: .custom(color: "#FF6600", icon: "star.fill"))
        .padding()
}

#Preview("All Modes") {
    HStack(spacing: 20) {
        TeardropPin(mode: .active)
        TeardropPin(mode: .found)
        TeardropPin(mode: .closed)
        TeardropPin(mode: .custom(color: "#FF6600", icon: "star.fill"))
    }
    .padding()
}

#Preview("On Map Background") {
    ZStack {
        Color.gray.opacity(0.3)
        
        VStack(spacing: 40) {
            HStack(spacing: 30) {
                TeardropPin(mode: .active)
                TeardropPin(mode: .found)
            }
            
            Text("Missing          Found")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
    }
    .frame(width: 200, height: 150)
}

