import SwiftUI

/// Custom teardrop-shaped map pin marker.
///
/// Renders a classic "teardrop" pin shape commonly used in map applications.
/// The pin consists of a circular top with a pointed bottom, creating the
/// iconic map marker silhouette.
///
/// **Usage**:
/// ```swift
/// TeardropPin(color: .red, icon: "exclamationmark")  // Missing pet
/// TeardropPin(color: .blue, icon: "checkmark")       // Found pet
/// TeardropPin(color: .gray)                          // No icon
/// ```
///
/// **Sizing**: Default size is 28x36 points. Use `.frame()` to adjust.
struct TeardropPin: View {
    let color: Color
    let icon: String?
    
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

#Preview("Red Pin (Missing)") {
    TeardropPin(color: .red, icon: "exclamationmark")
        .padding()
}

#Preview("Blue Pin (Found)") {
    TeardropPin(color: .blue, icon: "checkmark")
        .padding()
}

#Preview("Size Comparison") {
    HStack(spacing: 20) {
        TeardropPin(color: .red, icon: "exclamationmark")
        TeardropPin(color: .blue, icon: "checkmark")
        TeardropPin(color: .green)
        TeardropPin(color: .orange)
    }
    .padding()
}

#Preview("On Map Background") {
    ZStack {
        Color.gray.opacity(0.3)
        
        VStack(spacing: 40) {
            HStack(spacing: 30) {
                TeardropPin(color: .red, icon: "exclamationmark")
                TeardropPin(color: .blue, icon: "checkmark")
            }
            
            Text("Missing          Found")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
    }
    .frame(width: 200, height: 150)
}

