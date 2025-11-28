import XCTest
@testable import PetSpot

final class LocationPermissionHandlerTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    class FakeLocationService: LocationServiceProtocol {
        var stubbedAuthorizationStatus: LocationPermissionStatus = .notDetermined
        var stubbedLocation: UserLocation?
        var requestAuthorizationCalled = false
        var requestLocationCalled = false
        
        var authorizationStatus: LocationPermissionStatus {
            get async { stubbedAuthorizationStatus }
        }
        
        func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
            requestAuthorizationCalled = true
            return stubbedAuthorizationStatus
        }
        
        func requestLocation() async -> UserLocation? {
            requestLocationCalled = true
            return stubbedLocation
        }
    }
    
    // MARK: - Tests for requestLocationWithPermissions()
    
    func testRequestLocationWithPermissions_whenAuthorized_returnsLocation() async {
        // Given - location service with authorized status and sample location
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .authorizedWhenInUse
        fakeService.stubbedLocation = UserLocation(latitude: 52.2297, longitude: 21.0122)
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()  // Isolated instance for tests
        )
        
        // When - request location with permissions
        let result = await handler.requestLocationWithPermissions()
        
        // Then - returns location with authorized status
        XCTAssertEqual(result.status, .authorizedWhenInUse)
        XCTAssertEqual(result.location?.latitude, 52.2297)
        XCTAssertEqual(result.location?.longitude, 21.0122)
        XCTAssertTrue(fakeService.requestLocationCalled, "Should fetch location when authorized")
        XCTAssertFalse(fakeService.requestAuthorizationCalled, "Should not request auth when already authorized")
    }
    
    func testRequestLocationWithPermissions_whenNotDetermined_requestsPermission() async {
        // Given - location service with not determined status
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .notDetermined
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // When - request location with permissions
        _ = await handler.requestLocationWithPermissions()
        
        // Then - requests authorization
        XCTAssertTrue(fakeService.requestAuthorizationCalled, "Should request authorization when not determined")
    }
    
    func testRequestLocationWithPermissions_whenDenied_returnsNilLocation() async {
        // Given - location service with denied status
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .denied
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // When - request location with permissions
        let result = await handler.requestLocationWithPermissions()
        
        // Then - returns nil location with denied status
        XCTAssertEqual(result.status, .denied)
        XCTAssertNil(result.location, "Should not return location when denied")
        XCTAssertFalse(fakeService.requestLocationCalled, "Should not fetch location when denied")
    }
    
    func testRequestLocationWithPermissions_whenRestricted_returnsNilLocation() async {
        // Given - location service with restricted status
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .restricted
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // When - request location with permissions
        let result = await handler.requestLocationWithPermissions()
        
        // Then - returns nil location with restricted status
        XCTAssertEqual(result.status, .restricted)
        XCTAssertNil(result.location, "Should not return location when restricted")
        XCTAssertFalse(fakeService.requestLocationCalled, "Should not fetch location when restricted")
    }
    
    func testRequestLocationWithPermissions_whenAuthorizedAlways_returnsLocation() async {
        // Given - location service with always authorized status
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .authorizedAlways
        fakeService.stubbedLocation = UserLocation(latitude: 40.7128, longitude: -74.0060)
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // When - request location with permissions
        let result = await handler.requestLocationWithPermissions()
        
        // Then - returns location with authorized status
        XCTAssertEqual(result.status, .authorizedAlways)
        XCTAssertEqual(result.location?.latitude, 40.7128)
        XCTAssertEqual(result.location?.longitude, -74.0060)
        XCTAssertTrue(fakeService.requestLocationCalled, "Should fetch location when always authorized")
    }
    
    // MARK: - Tests for foreground observer
    
    func testStartObservingForeground_whenNotificationPosted_triggersCallback() async {
        // Given - handler with fake service and isolated notification center
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .authorizedWhenInUse
        
        let testNotificationCenter = NotificationCenter()  // Isolated instance
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: testNotificationCenter
        )
        
        var callbackInvoked = false
        var capturedStatus: LocationPermissionStatus?
        var capturedDidBecomeAuthorized: Bool?
        
        handler.startObservingForeground { status, didBecomeAuthorized in
            callbackInvoked = true
            capturedStatus = status
            capturedDidBecomeAuthorized = didBecomeAuthorized
        }
        
        // When - post foreground notification
        testNotificationCenter.post(
            name: UIApplication.willEnterForegroundNotification,
            object: nil
        )
        
        // Wait for async task to complete
        try? await Task.sleep(nanoseconds: 100_000_000)  // 0.1s
        
        // Then - callback invoked with current status and change flag
        XCTAssertTrue(callbackInvoked, "Callback should be invoked when notification posted")
        XCTAssertEqual(capturedStatus, .authorizedWhenInUse)
        XCTAssertTrue(capturedDidBecomeAuthorized == true, "Should detect change from unknown to authorized")
    }
    
    func testStartObservingForeground_whenCalledTwice_registersOnlyOnce() async {
        // Given - handler with observer started
        let fakeService = FakeLocationService()
        let testNotificationCenter = NotificationCenter()
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: testNotificationCenter
        )
        
        var callbackCount = 0
        let callback: (LocationPermissionStatus, Bool) -> Void = { _, _ in
            callbackCount += 1
        }
        
        handler.startObservingForeground(onStatusChange: callback)
        
        // When - start observing again with different callback
        handler.startObservingForeground { _, _ in
            XCTFail("Second callback should not be registered")
        }
        
        testNotificationCenter.post(name: UIApplication.willEnterForegroundNotification, object: nil)
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - only first callback invoked (prevents double registration)
        XCTAssertEqual(callbackCount, 1, "Should prevent double registration")
    }
    
    func testStopObservingForeground_stopsReceivingNotifications() async {
        // Given - handler with observer started
        let fakeService = FakeLocationService()
        let testNotificationCenter = NotificationCenter()
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: testNotificationCenter
        )
        
        var callbackCount = 0
        handler.startObservingForeground { _, _ in
            callbackCount += 1
        }
        
        // When - post notification, then stop, then post again
        testNotificationCenter.post(name: UIApplication.willEnterForegroundNotification, object: nil)
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        handler.stopObservingForeground()
        
        testNotificationCenter.post(name: UIApplication.willEnterForegroundNotification, object: nil)
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - callback called only once (before stop)
        XCTAssertEqual(callbackCount, 1, "Should stop receiving notifications after stopObservingForeground()")
    }
    
    func testDeinit_removesObserver() async {
        // Given - handler with observer started
        let fakeService = FakeLocationService()
        let testNotificationCenter = NotificationCenter()
        var handler: LocationPermissionHandler? = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: testNotificationCenter
        )
        
        var callbackCount = 0
        handler?.startObservingForeground { _, _ in
            callbackCount += 1
        }
        
        // When - handler is deallocated
        handler = nil
        
        testNotificationCenter.post(name: UIApplication.willEnterForegroundNotification, object: nil)
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - callback not invoked (observer removed in deinit)
        XCTAssertEqual(callbackCount, 0, "Should remove observer in deinit")
    }
    
    // MARK: - Tests for checkPermissionStatusChange()
    
    func testCheckPermissionStatusChange_whenNoLastKnownStatus_returnsCurrentStatusAndFalse() async {
        // Given - handler with authorized status, no previous status known
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .authorizedWhenInUse
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // When - check permission status change (first time)
        let result = await handler.checkPermissionStatusChange()
        
        // Then - returns current status with didBecomeAuthorized=true (from unknown → authorized)
        XCTAssertEqual(result.status, .authorizedWhenInUse)
        XCTAssertTrue(result.didBecomeAuthorized, "Should be true when changing from unknown to authorized")
    }
    
    func testCheckPermissionStatusChange_whenChangedFromDeniedToAuthorized_returnsTrue() async {
        // Given - handler with denied status initially
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .denied
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // Establish last known status as denied
        _ = await handler.requestLocationWithPermissions()
        
        // When - status changes to authorized (user went to Settings)
        fakeService.stubbedAuthorizationStatus = .authorizedWhenInUse
        let result = await handler.checkPermissionStatusChange()
        
        // Then - detects change from denied → authorized
        XCTAssertEqual(result.status, .authorizedWhenInUse)
        XCTAssertTrue(result.didBecomeAuthorized, "Should detect change from denied to authorized")
    }
    
    func testCheckPermissionStatusChange_whenStatusUnchangedAuthorized_returnsFalse() async {
        // Given - handler with authorized status
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .authorizedWhenInUse
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // Establish last known status as authorized
        _ = await handler.requestLocationWithPermissions()
        
        // When - check status again (unchanged)
        let result = await handler.checkPermissionStatusChange()
        
        // Then - no change detected
        XCTAssertEqual(result.status, .authorizedWhenInUse)
        XCTAssertFalse(result.didBecomeAuthorized, "Should not detect change when status unchanged")
    }
    
    func testCheckPermissionStatusChange_whenChangedFromAuthorizedToDenied_returnsFalse() async {
        // Given - handler with authorized status initially
        let fakeService = FakeLocationService()
        fakeService.stubbedAuthorizationStatus = .authorizedWhenInUse
        
        let handler = LocationPermissionHandler(
            locationService: fakeService,
            notificationCenter: NotificationCenter()
        )
        
        // Establish last known status as authorized
        _ = await handler.requestLocationWithPermissions()
        
        // When - status changes to denied (user revoked permission)
        fakeService.stubbedAuthorizationStatus = .denied
        let result = await handler.checkPermissionStatusChange()
        
        // Then - detects change but didBecomeAuthorized is false (went opposite direction)
        XCTAssertEqual(result.status, .denied)
        XCTAssertFalse(result.didBecomeAuthorized, "Should be false when changing from authorized to denied")
    }
}

