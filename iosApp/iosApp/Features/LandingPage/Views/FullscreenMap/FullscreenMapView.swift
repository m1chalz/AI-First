import SwiftUI
import MapKit

/// Fullscreen interactive map view with legend and pins.
///
/// **Layout**: Legend header at top, interactive map below with pins.
/// - Legend shows Missing (red) and Found (blue) markers
/// - Map supports all gestures: pinch zoom, pan, double-tap zoom
/// - Pins display as teardrop-shaped markers (red for missing, blue for found)
///
/// **Accessibility identifiers**:
/// - `fullscreenMap.container` - Root container
/// - `fullscreenMap.map` - Interactive map
/// - `fullscreenMap.legend.missing` - Missing legend item
/// - `fullscreenMap.legend.found` - Found legend item
/// - `fullscreenMap.pin.{id}` - Individual pin markers
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Legend header above map (reuse existing component)
            MapSectionHeaderView(model: viewModel.legendModel)
            
            // Interactive map with pins - all gestures enabled by default
            Map(initialPosition: .region(viewModel.mapRegion)) {
                ForEach(viewModel.pins) { pin in
                    Annotation("", coordinate: pin.coordinate, anchor: .bottom) {
                        // Teardrop pin marker - red for missing, blue for found
                        TeardropPin(color: pin.pinColor)
                            .accessibilityIdentifier("fullscreenMap.pin.\(pin.id)")
                    }
                }
            }
            .accessibilityIdentifier("fullscreenMap.map")
            .task {
                await viewModel.loadPins()
            }
            .onMapCameraChange(frequency: .onEnd) { context in
                Task {
                    await viewModel.handleRegionChange(context.region)
                }
            }
        }
        .accessibilityIdentifier("fullscreenMap.container")
    }
}
