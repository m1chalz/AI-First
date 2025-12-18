import Foundation
@testable import PetSpot

/**
 * Fake implementation of LocationServiceProtocol for unit testing.
 * Allows tests to control permission status and location responses.
 * Thread-safe actor for Swift Concurrency compatibility.
 */
actor FakeLocationService: LocationServiceProtocol {
    // MARK: - Test Configuration
    
    /// Stubbed authorization status returned by authorizationStatus property
    var stubbedAuthorizationStatus: LocationPermissionStatus = .notDetermined
    
    /// Stubbed location returned by requestLocation() (nil = unavailable)
    var stubbedLocation: Coordinate?
    
    /// Status returned after requestWhenInUseAuthorization() call
    var stubbedAuthorizationAfterRequest: LocationPermissionStatus = .authorizedWhenInUse
    
    // MARK: - Test Spy Properties
    
    /// Tracks if requestWhenInUseAuthorization() was called
    var requestAuthorizationCalled = false
    
    /// Tracks if requestLocation() was called
    var requestLocationCalled = false
    
    // MARK: - Authorization Status Stream (required by protocol)
    
    /// Empty stream - handler tests use foreground notifications, not stream
    private let (statusStream, _) = AsyncStream<LocationPermissionStatus>.makeStream()
    
    nonisolated var authorizationStatusStream: AsyncStream<LocationPermissionStatus> {
        statusStream
    }
    
    // MARK: - LocationServiceProtocol Implementation
    
    var authorizationStatus: LocationPermissionStatus {
        get async {
            stubbedAuthorizationStatus
        }
    }
    
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
        requestAuthorizationCalled = true
        return stubbedAuthorizationAfterRequest
    }
    
    func requestLocation() async -> Coordinate? {
        requestLocationCalled = true
        return stubbedLocation
    }
    
    // MARK: - Test Helpers
    
    /// Resets spy flags (call between tests)
    func reset() {
        requestAuthorizationCalled = false
        requestLocationCalled = false
    }
}

