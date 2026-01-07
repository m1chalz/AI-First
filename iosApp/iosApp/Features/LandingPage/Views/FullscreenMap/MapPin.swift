import Foundation
import CoreLocation
import SwiftUI

/// Lightweight pin representation for map display.
/// Derived from `Announcement` with only fields needed for rendering.
///
/// **Conformances**:
/// - `Identifiable`: Enables SwiftUI `ForEach` and optimized diffing
/// - `Equatable`: Enables selection comparison for future callout toggle
///
/// **Pin colors**:
/// - Red: Missing pets (status == .active)
/// - Blue: Found pets (status == .found)
///
/// **Future extension**: When tap interaction is implemented, this model
/// will be extended with additional fields for callout content (see Figma node `1192:5893`).
struct MapPin: Identifiable, Equatable {
    /// Unique identifier from announcement
    let id: String
    
    /// Pin position on map (latitude/longitude)
    let coordinate: CLLocationCoordinate2D
    
    /// Animal species for future pin icon variation
    let species: AnimalSpecies
    
    /// Announcement status - determines pin color (red for missing, blue for found)
    let status: AnnouncementStatus
    
    /// Pin color based on announcement status
    var pinColor: Color {
        switch status {
        case .active:
            return .red
        case .found:
            return .blue
        case .closed:
            return .gray
        }
    }
    
    /// Creates MapPin from Announcement domain model.
    /// - Parameter announcement: Source announcement with location data
    init(from announcement: Announcement) {
        self.id = announcement.id
        self.coordinate = CLLocationCoordinate2D(
            latitude: announcement.coordinate.latitude,
            longitude: announcement.coordinate.longitude
        )
        self.species = announcement.species
        self.status = announcement.status
    }
    
    // MARK: - Equatable
    
    static func == (lhs: MapPin, rhs: MapPin) -> Bool {
        lhs.id == rhs.id &&
        lhs.coordinate.latitude == rhs.coordinate.latitude &&
        lhs.coordinate.longitude == rhs.coordinate.longitude &&
        lhs.species == rhs.species &&
        lhs.status == rhs.status
    }
}

