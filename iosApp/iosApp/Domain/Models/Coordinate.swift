import Foundation
import MapKit

/// Geographic coordinate (latitude and longitude)
struct Coordinate: Codable, Equatable {
    let latitude: Double
    let longitude: Double
    
    /// Creates a coordinate with latitude and longitude
    /// - Parameters:
    ///   - latitude: Latitude in degrees (-90 to 90)
    ///   - longitude: Longitude in degrees (-180 to 180)
    init(latitude: Double, longitude: Double) {
        self.latitude = latitude
        self.longitude = longitude
    }
}

// MARK: - Map Region Helper

extension Coordinate {
    /// Creates map region with specified radius centered on this coordinate.
    /// - Parameter radiusMeters: Radius in meters (default: 10,000 = 10 km)
    /// - Returns: MKCoordinateRegion spanning 2x radius (diameter)
    func mapRegion(radiusMeters: Double = 10_000) -> MKCoordinateRegion {
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
            latitudinalMeters: radiusMeters * 2,
            longitudinalMeters: radiusMeters * 2
        )
    }
}

