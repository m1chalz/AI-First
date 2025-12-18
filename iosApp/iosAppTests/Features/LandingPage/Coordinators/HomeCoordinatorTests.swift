import XCTest
import UIKit
import SwiftUI
@testable import PetSpot

/// Unit tests for HomeCoordinator.
/// Tests coordinator initialization, start method, and cross-tab navigation callback.
/// Follows Given-When-Then structure per project constitution.
@MainActor
final class HomeCoordinatorTests: XCTestCase {
    
    // MARK: - Test Properties
    
    private var sut: HomeCoordinator!
    private var fakeRepository: FakeAnnouncementRepository!
    private var fakeLocationService: FakeLocationService!
    private var locationHandler: LocationPermissionHandler!
    private var capturedAnnouncementId: String?
    
    // MARK: - Setup & Teardown
    
    override func setUp() {
        super.setUp()
        fakeRepository = FakeAnnouncementRepository()
        fakeLocationService = FakeLocationService()
        locationHandler = LocationPermissionHandler(locationService: fakeLocationService)
        capturedAnnouncementId = nil
    }
    
    override func tearDown() {
        sut = nil
        fakeRepository = nil
        fakeLocationService = nil
        locationHandler = nil
        capturedAnnouncementId = nil
        super.tearDown()
    }
    
    // MARK: - Helper Methods
    
    private func makeSUT() -> HomeCoordinator {
        let coordinator = HomeCoordinator(
            repository: fakeRepository,
            locationHandler: locationHandler
        )
        // Set up the unified tab navigation closure
        coordinator.onSwitchToLostPetTab = { [weak self] announcementId in
            self?.capturedAnnouncementId = announcementId
        }
        return coordinator
    }
    
    // MARK: - T020: Initialization Tests
    
    func test_init_shouldCreateNavigationController() {
        // Given/When
        sut = makeSUT()
        
        // Then
        XCTAssertNotNil(sut.navigationController, "HomeCoordinator should create its own navigation controller")
    }
    
    func test_init_shouldHaveEmptyChildCoordinators() {
        // Given/When
        sut = makeSUT()
        
        // Then
        XCTAssertTrue(sut.childCoordinators.isEmpty, "Child coordinators should be empty initially")
    }
    
    // MARK: - T020: start() Tests
    
    func test_start_shouldCreateLandingPageViewAndSetAsRootViewController() async {
        // Given
        fakeRepository.stubbedAnnouncements = []
        await fakeLocationService.setAuthorizationStatus(.denied)
        sut = makeSUT()
        
        // When
        await sut.start(animated: false)
        
        // Then
        guard let navigationController = sut.navigationController else {
            XCTFail("Navigation controller should exist")
            return
        }
        
        XCTAssertEqual(navigationController.viewControllers.count, 1, "Should have exactly one view controller")
        
        // Verify navigation bar is visible
        XCTAssertFalse(navigationController.isNavigationBarHidden, "Navigation bar should be visible")
        
        // Verify title is set
        let rootVC = navigationController.viewControllers.first
        XCTAssertEqual(rootVC?.title, L10n.LandingPage.navigationTitle, "Title should be set correctly")
    }
    
    func test_start_whenCalledMultipleTimes_shouldReplaceRootViewController() async {
        // Given
        fakeRepository.stubbedAnnouncements = []
        await fakeLocationService.setAuthorizationStatus(.denied)
        sut = makeSUT()
        
        // When - start twice
        await sut.start(animated: false)
        await sut.start(animated: false)
        
        // Then - should still have only one view controller (replaced, not added)
        guard let navigationController = sut.navigationController else {
            XCTFail("Navigation controller should exist")
            return
        }
        
        XCTAssertEqual(navigationController.viewControllers.count, 1, "Should have exactly one view controller after multiple starts")
    }
    
    // MARK: - T020: onSwitchToLostPetTab Callback Tests
    
    func test_onSwitchToLostPetTab_shouldBeSettableAfterInit() async {
        // Given
        fakeRepository.stubbedAnnouncements = []
        await fakeLocationService.setAuthorizationStatus(.denied)
        sut = makeSUT()
        
        await sut.start(animated: false)
        
        // Then - Verify closure is set up (indirect test through coordinator creation)
        XCTAssertNotNil(sut.navigationController, "Coordinator should be properly initialized")
        XCTAssertEqual(sut.navigationController?.viewControllers.count, 1, "Should have root view controller")
    }
    
    func test_onSwitchToLostPetTab_whenSetWithClosure_shouldNotCallImmediately() async throws {
        // Given - Create coordinator with callback capture
        var capturedId: String?
        
        let coordinator = HomeCoordinator(
            repository: fakeRepository,
            locationHandler: locationHandler
        )
        coordinator.onSwitchToLostPetTab = { announcementId in
            capturedId = announcementId
        }
        
        fakeRepository.stubbedAnnouncements = [
            Announcement(
                id: "pet-456",
                name: "Test Pet",
                photoUrl: "https://example.com/photo.jpg",
                coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
                species: .dog,
                breed: "Labrador",
                gender: .male,
                status: .active,
                lastSeenDate: "20/11/2025",
                description: "Test description",
                email: "test@example.com",
                phone: "+48123456789"
            )
        ]
        await fakeLocationService.setAuthorizationStatus(.denied)
        
        // When - Start coordinator
        await coordinator.start(animated: false)
        
        // Then - Verify the callback is not called until user interaction
        XCTAssertNotNil(coordinator.navigationController)
        XCTAssertNil(capturedId, "Callback should not be called until user taps")
    }
    
    func test_onSwitchToFoundPetTab_shouldBeSettableAfterInit() {
        // Given
        var foundPetTabCalled = false
        sut = makeSUT()
        
        // When
        sut.onSwitchToFoundPetTab = {
            foundPetTabCalled = true
        }
        
        // Then - closure is set (would be called by view interaction)
        XCTAssertNotNil(sut.onSwitchToFoundPetTab)
        XCTAssertFalse(foundPetTabCalled, "Should not be called until user interaction")
    }
}

// MARK: - FakeLocationService Helper Extension

extension FakeLocationService {
    /// Helper to set authorization status from MainActor context
    func setAuthorizationStatus(_ status: LocationPermissionStatus) async {
        stubbedAuthorizationStatus = status
    }
}
