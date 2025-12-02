import Foundation

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

