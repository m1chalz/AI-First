import Foundation
import CoreLocation

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

