import Foundation

/// Represents a geographic location with search radius.
/// Domain model for animal location data.
struct Location {
    /// City or area name
    let city: String
    
    /// Search radius in kilometers
    let radiusKm: Int
}

