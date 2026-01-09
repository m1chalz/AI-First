import Foundation
import MapKit
import CoreLocation

/// Extension providing radius calculation for MKCoordinateRegion.
/// Used to convert visible map area to API query range parameter.
extension MKCoordinateRegion {
    /// Calculates radius in kilometers covering the visible region.
    /// Uses diagonal distance (corner to corner) for complete coverage.
    ///
    /// **Calculation**: Distance from top-left to bottom-right corner, divided by 2.
    /// This ensures all visible pins are within the search radius.
    var radiusInKilometers: Int {
        let topLeft = CLLocation(
            latitude: center.latitude + span.latitudeDelta / 2,
            longitude: center.longitude - span.longitudeDelta / 2
        )
        let bottomRight = CLLocation(
            latitude: center.latitude - span.latitudeDelta / 2,
            longitude: center.longitude + span.longitudeDelta / 2
        )
        
        let diagonalMeters = topLeft.distance(from: bottomRight)
        let radiusMeters = diagonalMeters / 2
        
        return max(1, Int(radiusMeters / 1000))
    }
}

