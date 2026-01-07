import SwiftUI

/// Header component for the map section displaying title and legend.
///
/// **Layout**: Title on top, legend items below in a horizontal row.
/// - Title font matches "Recent Reports" (18pt semibold)
/// - Legend items font matches species in cards (16pt regular)
/// - Colored circles indicate status
struct MapSectionHeaderView: View {
    let model: Model
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            // Section title - only show if provided
            if let title = model.title {
                Text(title)
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(Color(hex: "#101828"))
                    .accessibilityIdentifier(model.titleAccessibilityId ?? "")
            }
            
            // Legend items below title (or standalone if no title)
            HStack(spacing: 16) {
                ForEach(model.legendItems) { item in
                    legendItemView(item)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
    
    private func legendItemView(_ item: LegendItem) -> some View {
        HStack(spacing: 6) {
            Circle()
                .fill(item.color)
                .frame(width: 8, height: 8)
            
            Text(item.label)
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#2D2D2D"))
        }
        .accessibilityIdentifier("\(model.legendAccessibilityIdPrefix).\(item.id)")
    }
}

// MARK: - Preview

#Preview("Map Section Header - Landing Page") {
    VStack(spacing: 16) {
        MapSectionHeaderView(model: .landingPage())
        
        // Simulated map placeholder
        RoundedRectangle(cornerRadius: 12)
            .fill(Color(.systemGray5))
            .frame(height: 200)
            .padding(.horizontal, 16)
    }
}
