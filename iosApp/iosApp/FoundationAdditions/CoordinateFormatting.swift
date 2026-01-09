import Foundation
import CoreLocation

/// Shared coordinate formatting utilities for consistent location display across the app.
enum CoordinateFormatting {
    
    /// Formats coordinates to display format with cardinal directions.
    /// - Parameter coordinate: Geographic coordinate (latitude/longitude)
    /// - Returns: Formatted as "52.2297° N, 21.0122° E"
    ///
    /// **Usage**:
    /// ```swift
    /// let coord = CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122)
    /// CoordinateFormatting.formatCoordinates(coord) // "52.2297° N, 21.0122° E"
    /// ```
    static func formatCoordinates(_ coordinate: CLLocationCoordinate2D) -> String {
        formatCoordinates(latitude: coordinate.latitude, longitude: coordinate.longitude)
    }
    
    /// Formats coordinates to display format with cardinal directions.
    /// - Parameters:
    ///   - latitude: Latitude coordinate (-90 to 90)
    ///   - longitude: Longitude coordinate (-180 to 180)
    /// - Returns: Formatted as "52.2297° N, 21.0122° E"
    static func formatCoordinates(latitude: Double, longitude: Double) -> String {
        let latDirection = latitude >= 0 ? "N" : "S"
        let lonDirection = longitude >= 0 ? "E" : "W"
        let lat = abs(latitude)
        let lon = abs(longitude)
        return String(format: "%.4f° %@, %.4f° %@", lat, latDirection, lon, lonDirection)
    }
}

