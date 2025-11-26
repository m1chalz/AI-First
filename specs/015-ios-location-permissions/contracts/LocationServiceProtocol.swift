import Foundation
import CoreLocation

/// Service protocol for location permission management and coordinate fetching.
///
/// Provides async/await interface for iOS location services, abstracting CoreLocation complexity.
/// Implementations MUST handle all permission states and provide graceful fallback.
protocol LocationServiceProtocol {
    /// Current location authorization status.
    ///
    /// Updated reactively when user changes permissions in Settings or responds to system alert.
    /// Observers can use this property to react to permission changes in real-time.
    var authorizationStatus: LocationPermissionStatus { get async }
    
    /// Requests "When In Use" location permission from user.
    ///
    /// Displays iOS system permission alert if status is `.notDetermined`.
    /// Returns immediately if permission already granted or denied.
    ///
    /// - Returns: Updated authorization status after user response
    /// - Note: May take several seconds if user delays responding to alert
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus
    
    /// Fetches current device location coordinates.
    ///
    /// Requires location permission to be granted (`.authorizedWhenInUse` or `.authorizedAlways`).
    /// Uses one-time location request (no continuous monitoring).
    ///
    /// - Returns: UserLocation with coordinates and timestamp, or nil if unavailable
    /// - Note: Returns nil for any failure (permission denied, GPS unavailable, timeout, etc.)
    ///         App uses silent fallback - queries server without coordinates when nil
    func requestLocation() async -> UserLocation?
}

/// Domain representation of location authorization status.
///
/// Abstracts CoreLocation's CLAuthorizationStatus to domain layer.
enum LocationPermissionStatus: Equatable {
    case notDetermined       // User hasn't been asked yet
    case authorizedWhenInUse // Granted "While Using App" permission
    case authorizedAlways    // Granted "Always" permission
    case denied              // User explicitly denied permission
    case restricted          // Permission restricted by system policies
    
    /// Whether location can be fetched (permission granted).
    /// Domain business rule: location is available when authorized.
    var isAuthorized: Bool {
        self == .authorizedWhenInUse || self == .authorizedAlways
    }
    
    /// Converts CoreLocation status to domain model.
    init(from clStatus: CLAuthorizationStatus) {
        switch clStatus {
        case .notDetermined:
            self = .notDetermined
        case .authorizedWhenInUse:
            self = .authorizedWhenInUse
        case .authorizedAlways:
            self = .authorizedAlways
        case .denied:
            self = .denied
        case .restricted:
            self = .restricted
        @unknown default:
            self = .notDetermined
        }
    }
}

/// Domain model for user geographic location.
struct UserLocation: Equatable {
    let latitude: Double
    let longitude: Double
    let timestamp: Date
}

