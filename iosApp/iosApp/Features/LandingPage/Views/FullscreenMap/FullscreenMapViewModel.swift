import Foundation
import MapKit

/// ViewModel for fullscreen map screen.
/// Receives user location via constructor (already available from landing page).
///
/// **Design**: Simple data holder - no async loading needed.
/// Location is passed from landing page where it was already fetched.
///
/// **Properties**:
/// - `mapRegion`: Pre-calculated from user location for city-level zoom (~10km radius)
/// - `legendModel`: Legend-only configuration (no title - navigation bar provides context)
@MainActor
class FullscreenMapViewModel: ObservableObject {
    /// Map region centered on user's location with city-level zoom (~10km radius).
    let mapRegion: MKCoordinateRegion
    
    /// Legend configuration for fullscreen map (reuses existing component).
    /// Shows Missing (red) and Found (blue) legend items without title.
    let legendModel: MapSectionHeaderView.Model
    
    /// Creates ViewModel with pre-fetched user location.
    ///
    /// - Parameter userLocation: User's current location (from landing page)
    init(userLocation: Coordinate) {
        self.mapRegion = userLocation.mapRegion()
        self.legendModel = .fullscreenMap()
    }
}
