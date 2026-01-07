import SwiftUI
import MapKit

/// Fullscreen interactive map view with legend.
///
/// **Layout**: Legend header at top, interactive map below.
/// - Legend shows Missing (red) and Found (blue) markers
/// - Map supports all gestures: pinch zoom, pan, double-tap zoom
///
/// **Accessibility identifiers**:
/// - `fullscreenMap.container` - Root container
/// - `fullscreenMap.map` - Interactive map
/// - `fullscreenMap.legend.missing` - Missing legend item
/// - `fullscreenMap.legend.found` - Found legend item
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Legend header above map (reuse existing component)
            MapSectionHeaderView(model: viewModel.legendModel)
            
            // Interactive map - all gestures enabled by default
            Map(initialPosition: .region(viewModel.mapRegion))
                .accessibilityIdentifier("fullscreenMap.map")
        }
        .accessibilityIdentifier("fullscreenMap.container")
    }
}
