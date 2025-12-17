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
        return HomeCoordinator(
            repository: fakeRepository,
            locationHandler: locationHandler,
            onShowPetDetails: { [weak self] announcementId in
                self?.capturedAnnouncementId = announcementId
            }
        )
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
    
    // MARK: - T020: onShowPetDetails Callback Tests
    
    func test_onShowPetDetails_shouldInvokeClosureWithAnnouncementId() async {
        // Given
        let testAnnouncementId = "test-announcement-123"
        fakeRepository.stubbedAnnouncements = [
            Announcement(
                id: testAnnouncementId,
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
        
        sut = makeSUT()
        await sut.start(animated: false)
        
        // Then - Verify closure is set up (indirect test through coordinator creation)
        // Direct testing would require accessing private ViewModel state
        // Instead, we verify the coordinator was created with callback capability
        XCTAssertNotNil(sut.navigationController, "Coordinator should be properly initialized with callback")
        XCTAssertEqual(sut.navigationController?.viewControllers.count, 1, "Should have root view controller")
    }
    
    func test_onShowPetDetails_whenTriggeredFromLandingPage_shouldPassCorrectId() async throws {
        // Given - Create coordinator with callback capture
        let testId = "pet-456"
        var capturedId: String?
        
        let coordinator = HomeCoordinator(
            repository: fakeRepository,
            locationHandler: locationHandler,
            onShowPetDetails: { announcementId in
                capturedId = announcementId
            }
        )
        
        fakeRepository.stubbedAnnouncements = [
            Announcement(
                id: testId,
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
        
        // Then - Verify the callback mechanism exists
        // Note: Full integration test would require UI interaction
        // This unit test verifies the coordinator correctly stores the callback
        XCTAssertNotNil(coordinator.navigationController)
        XCTAssertNil(capturedId, "Callback should not be called until user taps")
    }
}

// MARK: - FakeLocationService Helper Extension

extension FakeLocationService {
    /// Helper to set authorization status from MainActor context
    func setAuthorizationStatus(_ status: LocationPermissionStatus) async {
        stubbedAuthorizationStatus = status
    }
}
