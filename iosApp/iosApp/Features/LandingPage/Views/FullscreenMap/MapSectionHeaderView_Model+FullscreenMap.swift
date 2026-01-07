import Foundation

// MARK: - Fullscreen Map Factory

extension MapSectionHeaderView.Model {
    /// Creates legend-only model for fullscreen map (no title).
    /// Title is provided by navigation bar ("Pet Locations").
    ///
    /// **Legend items**:
    /// - Missing (red): Indicates missing animals on map
    /// - Found (blue): Indicates found animals on map
    ///
    /// **Accessibility**: Uses `fullscreenMap.legend` prefix for identifiers
    ///
    /// - Returns: Configured MapSectionHeaderView.Model for fullscreen map
    static func fullscreenMap() -> MapSectionHeaderView.Model {
        MapSectionHeaderView.Model(
            title: nil, // Title provided by navigation bar
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
            titleAccessibilityId: nil,
            legendAccessibilityIdPrefix: "fullscreenMap.legend"
        )
    }
}

