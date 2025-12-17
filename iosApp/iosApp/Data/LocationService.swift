import CoreLocation

/// Actor-based location service implementation using CoreLocation framework.
///
/// Wraps CLLocationManager with Swift Concurrency (async/await) interface.
/// Thread-safe actor ensures serial access to location manager state.
actor LocationService: NSObject, CLLocationManagerDelegate, LocationServiceProtocol {
    private let locationManager = CLLocationManager()
    private var permissionContinuation: CheckedContinuation<LocationPermissionStatus, Never>?
    private var locationContinuation: CheckedContinuation<Coordinate?, Never>?
    
    // MARK: - Authorization Status Stream (Broadcast Pattern)
    
    /// Active stream continuations - each subscriber gets own continuation.
    /// Using UUID keys for safe add/remove without Hashable requirement on Continuation.
    private var statusContinuations: [UUID: AsyncStream<LocationPermissionStatus>.Continuation] = [:]
    
    /// Creates new stream for authorization status changes.
    /// Each caller gets independent stream - all subscribers receive all values (broadcast).
    /// Stream lives until subscriber's Task is cancelled or stream is deallocated.
    nonisolated var authorizationStatusStream: AsyncStream<LocationPermissionStatus> {
        let id = UUID()
        return AsyncStream { continuation in
            // Register continuation when stream is created
            let setupTask = Task { await self.addStatusContinuation(id: id, continuation: continuation) }
            
            // Cleanup when stream ends (Task cancelled, finish(), or deallocation)
            continuation.onTermination = { _ in
                setupTask.cancel()
                Task { await self.removeStatusContinuation(id: id) }
            }
        }
    }
    
    private func addStatusContinuation(id: UUID, continuation: AsyncStream<LocationPermissionStatus>.Continuation) {
        statusContinuations[id] = continuation
    }
    
    private func removeStatusContinuation(id: UUID) {
        statusContinuations.removeValue(forKey: id)
    }
    
    /// Broadcasts status to all active stream subscribers.
    private func broadcastStatus(_ status: LocationPermissionStatus) {
        for continuation in statusContinuations.values {
            continuation.yield(status)
        }
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
        
        // Broadcast to all stream subscribers + resume one-shot continuation
        Task {
            await self.broadcastStatus(status)
            await self.resumePermissionContinuation(with: status)
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

