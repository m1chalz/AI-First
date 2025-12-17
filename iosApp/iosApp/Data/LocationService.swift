import CoreLocation

/// Actor-based location service implementation using CoreLocation framework.
///
/// Wraps CLLocationManager with Swift Concurrency (async/await) interface.
/// Thread-safe actor ensures serial access to location manager state.
actor LocationService: NSObject, CLLocationManagerDelegate, LocationServiceProtocol {
    private let locationManager = CLLocationManager()
    private var permissionContinuation: CheckedContinuation<LocationPermissionStatus, Never>?
    private var locationContinuation: CheckedContinuation<Coordinate?, Never>?
    
    // MARK: - Authorization Status Stream
    
    /// Stream and continuation created together using makeStream() (iOS 17+)
    private let (statusStream, statusStreamContinuation) = AsyncStream<LocationPermissionStatus>.makeStream()
    
    nonisolated var authorizationStatusStream: AsyncStream<LocationPermissionStatus> {
        statusStream
    }
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
    }
    
    var authorizationStatus: LocationPermissionStatus {
        get async {
            LocationPermissionStatus(from: locationManager.authorizationStatus)
        }
    }
    
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
        let current = LocationPermissionStatus(from: locationManager.authorizationStatus)
        
        // If status already known → return immediately
        guard !current.isAuthorized else {
            return current  // Already authorized
        }
        
        guard current == .notDetermined else {
            return current  // Already denied/restricted
        }
        
        return await withCheckedContinuation { continuation in
            // Prevent overwriting existing continuation
            if permissionContinuation != nil {
                continuation.resume(returning: current)
                return
            }
            
            permissionContinuation = continuation
            locationManager.requestWhenInUseAuthorization()
        }
    }
    
    func requestLocation() async -> Coordinate? {
        let status = locationManager.authorizationStatus
        
        guard status == .authorizedWhenInUse || status == .authorizedAlways else {
            return nil  // Permission not granted
        }
        
        return await withCheckedContinuation { continuation in
            // Prevent overwriting existing continuation
            if locationContinuation != nil {
                continuation.resume(returning: nil)
                return
            }
            
            locationContinuation = continuation
            locationManager.requestLocation()
        }
    }
    
    // MARK: - CLLocationManagerDelegate
    
    nonisolated func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        let status = LocationPermissionStatus(from: manager.authorizationStatus)
        
        // Emit to stream for real-time observers (nonisolated - continuation is Sendable)
        statusStreamContinuation.yield(status)
        
        // Resume one-shot continuation for requestWhenInUseAuthorization()
        Task {
            await resumePermissionContinuation(with: status)
        }
    }
    
    nonisolated func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        Task {
            if let location = locations.first {
                let coordinate = Coordinate(
                    latitude: location.coordinate.latitude,
                    longitude: location.coordinate.longitude
                )
                await resumeLocationContinuation(with: coordinate)
            } else {
                await resumeLocationContinuation(with: nil)
            }
        }
    }
    
    nonisolated func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        Task {
            await resumeLocationContinuation(with: nil)
        }
    }
    
    // MARK: - Private Helpers
    
    private func resumePermissionContinuation(with status: LocationPermissionStatus) {
        permissionContinuation?.resume(returning: status)
        permissionContinuation = nil
    }
    
    private func resumeLocationContinuation(with location: Coordinate?) {
        locationContinuation?.resume(returning: location)
        locationContinuation = nil
    }
}

