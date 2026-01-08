import Foundation
import CoreLocation
import SwiftUI

/// Lightweight pin representation for map display and annotation callout.
/// Derived from `Announcement` with fields needed for rendering and callout display.
///
/// **Conformances**:
/// - `Identifiable`: Enables SwiftUI `ForEach` and optimized diffing
/// - `Equatable`: Enables selection comparison for callout toggle
///
/// **Pin colors**:
/// - Red: Missing pets (status == .active)
/// - Blue: Found pets (status == .found)
struct MapPin: Identifiable, Equatable {
    /// Unique identifier from announcement
    let id: String
    
    /// Pin position on map (latitude/longitude)
    let coordinate: CLLocationCoordinate2D
    
    /// Animal species for pin icon and callout display
    let species: AnimalSpecies
    
    /// Announcement status - determines pin color (red for missing, blue for found)
    let status: AnnouncementStatus
    
    // MARK: - Callout Data Fields
    
    /// Pet name (nullable - may not be provided by reporter)
    let petName: String?
    
    /// Photo URL or empty string if not available
    let photoUrl: String
    
    /// Breed name (nullable - may not be specified)
    let breed: String?
    
    /// Date when animal was last seen (format: yyyy-MM-dd)
    let lastSeenDate: String
    
    /// Owner's email for contact (nullable)
    let ownerEmail: String?
    
    /// Owner's phone for contact (nullable)
    let ownerPhone: String?
    
    /// Additional description of the pet (nullable)
    let petDescription: String?
    
    /// Converts announcement status to TeardropPin display mode.
    var displayMode: TeardropPin.Mode {
        switch status {
        case .active:
            return .active
        case .found:
            return .found
        case .closed:
            return .closed
        }
    }
    
    /// Memberwise initializer for testing and preview purposes.
    init(
        id: String,
        coordinate: CLLocationCoordinate2D,
        species: AnimalSpecies,
        status: AnnouncementStatus,
        petName: String?,
        photoUrl: String,
        breed: String?,
        lastSeenDate: String,
        ownerEmail: String?,
        ownerPhone: String?,
        petDescription: String?
    ) {
        self.id = id
        self.coordinate = coordinate
        self.species = species
        self.status = status
        self.petName = petName
        self.photoUrl = photoUrl
        self.breed = breed
        self.lastSeenDate = lastSeenDate
        self.ownerEmail = ownerEmail
        self.ownerPhone = ownerPhone
        self.petDescription = petDescription
    }
    
    /// Creates MapPin from Announcement domain model.
    /// Maps all fields needed for pin display and annotation callout.
    /// - Parameter announcement: Source announcement with location data
    init(from announcement: Announcement) {
        self.id = announcement.id
        self.coordinate = CLLocationCoordinate2D(
            latitude: announcement.coordinate.latitude,
            longitude: announcement.coordinate.longitude
        )
        self.species = announcement.species
        self.status = announcement.status
        
        // Callout data fields
        self.petName = announcement.name
        // Treat empty photoUrl as empty string (Model will convert to nil for placeholder)
        self.photoUrl = announcement.photoUrl
        self.breed = announcement.breed
        self.lastSeenDate = announcement.lastSeenDate
        self.ownerEmail = announcement.email
        self.ownerPhone = announcement.phone
        self.petDescription = announcement.description
    }
    
    // MARK: - Equatable
    
    static func == (lhs: MapPin, rhs: MapPin) -> Bool {
        lhs.id == rhs.id &&
        lhs.coordinate.latitude == rhs.coordinate.latitude &&
        lhs.coordinate.longitude == rhs.coordinate.longitude &&
        lhs.species == rhs.species &&
        lhs.status == rhs.status &&
        lhs.petName == rhs.petName &&
        lhs.photoUrl == rhs.photoUrl &&
        lhs.breed == rhs.breed &&
        lhs.lastSeenDate == rhs.lastSeenDate &&
        lhs.ownerEmail == rhs.ownerEmail &&
        lhs.ownerPhone == rhs.ownerPhone &&
        lhs.petDescription == rhs.petDescription
    }
}

