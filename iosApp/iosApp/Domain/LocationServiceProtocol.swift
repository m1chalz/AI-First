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
    /// - Returns: Coordinate with latitude and longitude, or nil if unavailable
    /// - Note: Returns nil for any failure (permission denied, GPS unavailable, timeout, etc.)
    ///         App uses silent fallback - queries server without coordinates when nil
    func requestLocation() async -> Coordinate?
}

