import SwiftUI
import MapKit

/// Fullscreen interactive map view with legend, pins, and annotation callouts.
///
/// **Layout**: Legend header at top, interactive map below with pins.
/// - Legend shows Missing (red) and Found (blue) markers
/// - Map supports all gestures: pinch zoom, pan, double-tap zoom
/// - Pins display as teardrop-shaped markers (red for missing, blue for found)
/// - Tapping a pin shows annotation callout with pet details (FR-001)
///
/// **Callout Behavior** (FR-010, FR-011, FR-012):
/// - Tap pin → callout appears
/// - Tap same pin → callout toggles off
/// - Tap different pin → callout switches
/// - Tap map background → callout dismisses
///
/// **Accessibility identifiers**:
/// - `fullscreenMap.container` - Root container
/// - `fullscreenMap.map` - Interactive map
/// - `fullscreenMap.legend.missing` - Missing legend item
/// - `fullscreenMap.legend.found` - Found legend item
/// - `fullscreenMap.pin.{id}` - Individual pin markers
/// - `fullscreenMap.annotation.{id}` - Annotation callouts
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Legend header above map (reuse existing component)
            MapSectionHeaderView(model: viewModel.legendModel)
            
            // Interactive map with pins and callouts
            Map(initialPosition: .region(viewModel.mapRegion)) {
                ForEach(viewModel.pins) { pin in
                    // T025: anchor: .bottom - coordinate points to pin tip
                    Annotation("", coordinate: pin.coordinate, anchor: .bottom) {
                        // T022: ZStack with callout above pin
                        ZStack(alignment: .bottom) {
                            // Teardrop pin marker - always visible (rendered first = below)
                            TeardropPin(color: pin.pinColor, icon: pin.pinIcon)
                                .accessibilityIdentifier("fullscreenMap.pin.\(pin.id)")
                            
                            // Callout appears ABOVE pin when selected (FR-001)
                            // Rendered second = on top, so arrow is visible above pin
                            if viewModel.selectedPinId == pin.id {
                                AnnotationCalloutView(model: viewModel.calloutModel(for: pin))
                                    // Position arrow to point at ~1/3 from top of teardrop pin
                                    .offset(y: -22)
                            }
                        }
                        // T023: Tap anywhere in annotation (pin or callout) to select/toggle
                        // Using contentShape + highPriorityGesture ensures pin tap wins over map tap
                        .contentShape(Rectangle())
                        .highPriorityGesture(
                            TapGesture()
                                .onEnded {
                                    viewModel.selectPin(pin.id)
                                }
                        )
                    }
                }
            }
            .accessibilityIdentifier("fullscreenMap.map")
            // T024: Tap map to dismiss callout (FR-010)
            .onTapGesture {
                viewModel.deselectPin()
            }
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
