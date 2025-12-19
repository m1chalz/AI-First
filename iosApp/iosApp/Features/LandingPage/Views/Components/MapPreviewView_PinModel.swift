import Foundation
import CoreLocation

extension MapPreviewView {
    /// Lightweight model for map pin placement.
    /// Represents a single announcement location on the map preview.
    struct PinModel: Identifiable, Equatable {
        let id: String
        let coordinate: Coordinate
        
        var clLocationCoordinate: CLLocationCoordinate2D {
            CLLocationCoordinate2D(
                latitude: coordinate.latitude,
                longitude: coordinate.longitude
            )
        }
    }
}

