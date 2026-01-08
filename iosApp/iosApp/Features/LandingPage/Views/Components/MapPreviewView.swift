import SwiftUI
import MapKit

/// Static map preview component for landing page.
///
/// Displays a non-interactive map centered on user's location, or shows
/// a permission prompt if location access is not granted.
///
/// **States**:
/// - `.loading`: Shows spinner while location is being determined
/// - `.map`: Shows SwiftUI Map with disabled interactions (tap handled by overlay)
/// - `.permissionRequired`: Shows location icon, message, and "Go to Settings" button
///
/// **Interactions**:
/// - Map is static (no pan/zoom) - `interactionModes: []`
/// - Tap gesture handled via transparent overlay (triggers `onTap` closure)
///
/// **Accessibility**: Uses `landingPage.mapPreview` and `landingPage.mapPreview.settings` identifiers.
struct MapPreviewView: View {
    let model: Model
    
    var body: some View {
        contentView
            .frame(maxWidth: .infinity)
            .background(Color(.systemGray6))
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .accessibilityIdentifier("landingPage.mapPreview")
    }
    
    @ViewBuilder
    private var contentView: some View {
        switch model {
        case .loading:
            loadingView
            
        case .map(let region, let pins, let onTap):
            mapView(region: region, pins: pins, onTap: onTap)
            
        case .permissionRequired(let message, let onGoToSettings):
            permissionView(message: message, onGoToSettings: onGoToSettings)
        }
    }
    
    // MARK: - State Views
    
    private var loadingView: some View {
        ProgressView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
    
    private func mapView(
        region: MKCoordinateRegion,
        pins: [PinModel],
        onTap: @escaping () -> Void
    ) -> some View {
        ZStack {
            Map(initialPosition: .region(region), interactionModes: []) {
                ForEach(pins) { pin in
                    Annotation("", coordinate: pin.clLocationCoordinate, anchor: .bottom) {
                        // Teardrop pin marker - red for missing, blue for found
                        TeardropPin(color: pin.pinColor, icon: pin.pinIcon)
                    }
                }
            }
            .disabled(true)
            .allowsHitTesting(false)
            
            // Centered tap hint bubble
            tapHintBubble
        }
        .overlay {
            Color.clear
                .contentShape(Rectangle())
                .onTapGesture { onTap() }
        }
    }
    
    private var tapHintBubble: some View {
        Text(L10n.MapPreview.tapHint)
            .font(.system(size: 12, weight: .medium))
            .foregroundColor(.primary)
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(
                Capsule()
                    .fill(Color(.systemBackground))
                    .shadow(color: .black.opacity(0.2), radius: 4, x: 0, y: 2)
            )
    }
    
    private func permissionView(message: String, onGoToSettings: @escaping () -> Void) -> some View {
        VStack(spacing: 12) {
            Image(systemName: "location.slash.fill")
                .font(.system(size: 32))
                .foregroundColor(.secondary)
            
            Text(message)
                .font(.system(size: 14))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
            
            Button(L10n.MapPreview.Permission.settingsButton) {
                onGoToSettings()
            }
            .font(.system(size: 16, weight: .medium))
            .accessibilityIdentifier("landingPage.mapPreview.settings")
        }
        .padding()
    }
}

// MARK: - Preview

#Preview("Loading") {
    MapPreviewView(model: .loading)
        .aspectRatio(16/9, contentMode: .fit)
        .padding()
}

#Preview("Map") {
    MapPreviewView(
        model: .map(
            region: MKCoordinateRegion(
                center: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
                latitudinalMeters: 20_000,
                longitudinalMeters: 20_000
            ),
            pins: [
                MapPreviewView.PinModel(
                    id: "1",
                    coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
                    status: .active
                ),
                MapPreviewView.PinModel(
                    id: "2",
                    coordinate: Coordinate(latitude: 52.2350, longitude: 21.0200),
                    status: .found
                ),
                MapPreviewView.PinModel(
                    id: "3",
                    coordinate: Coordinate(latitude: 52.2200, longitude: 21.0050),
                    status: .active
                )
            ],
            onTap: { print("Map tapped") }
        )
    )
    .aspectRatio(16/9, contentMode: .fit)
    .padding()
}

#Preview("Permission Required") {
    MapPreviewView(
        model: .permissionRequired(
            message: "Enable location to see nearby area.",
            onGoToSettings: { print("Go to Settings tapped") }
        )
    )
    .aspectRatio(16/9, contentMode: .fit)
    .padding()
}

