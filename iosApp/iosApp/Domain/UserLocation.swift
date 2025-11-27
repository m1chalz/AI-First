import Foundation

/// Domain model for user geographic location.
struct UserLocation: Equatable {
    let latitude: Double
    let longitude: Double
    let timestamp: Date
    
    init(latitude: Double, longitude: Double, timestamp: Date = Date()) {
        self.latitude = latitude
        self.longitude = longitude
        self.timestamp = timestamp
    }
}

