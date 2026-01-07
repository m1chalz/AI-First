import Foundation

// MARK: - Landing Page Factory

extension MapSectionHeaderView.Model {
    /// Creates map section header model for Landing Page.
    /// Shows "Map View" title with Missing (red) and Found (blue) legend items.
    /// - Returns: Configured MapSectionHeaderView.Model for landing page
    static func landingPage() -> MapSectionHeaderView.Model {
        MapSectionHeaderView.Model(
            title: L10n.MapSection.title,
            legendItems: [
                MapSectionHeaderView.LegendItem(
                    id: "missing",
                    colorHex: "#FF0000",
                    label: L10n.MapSection.Legend.missing
                ),
                MapSectionHeaderView.LegendItem(
                    id: "found",
                    colorHex: "#0074FF",
                    label: L10n.MapSection.Legend.found
                )
            ],
            titleAccessibilityId: "landingPage.mapSection.title",
            legendAccessibilityIdPrefix: "landingPage.mapSection.legend"
        )
    }
}

