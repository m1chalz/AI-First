import Foundation
@testable import PetSpot

/// Fake location service for unit testing.
/// Implements LocationServiceProtocol for controlled test scenarios.
actor FakeLocationService: LocationServiceProtocol {
    
    // Configurable behavior
    var stubbedAuthorizationStatus: LocationPermissionStatus = .notDetermined
    var stubbedLocation: UserLocation?
    var shouldFailLocationRequest: Bool = false
    
    // Call tracking for assertions
    private(set) var requestAuthorizationCallCount = 0
    private(set) var requestLocationCallCount = 0
    
    var authorizationStatus: LocationPermissionStatus {
        get async {
            return stubbedAuthorizationStatus
        }
    }
    
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
        requestAuthorizationCallCount += 1
        return stubbedAuthorizationStatus
    }
    
    func requestLocation() async -> UserLocation? {
        requestLocationCallCount += 1
        
        if shouldFailLocationRequest {
            return nil
        }
        
        return stubbedLocation
    }
}

