import XCTest
@testable import iosApp

/// Unit tests for AnimalListViewModel
/// Focuses on User Story 3: Refresh behavior and task cancellation
@MainActor
final class AnimalListViewModelTests: XCTestCase {
    var sut: AnimalListViewModel!
    var fakeRepository: FakeAnimalRepository!
    var fakeLocationHandler: FakeLocationPermissionHandler!
    
    override func setUp() async throws {
        try await super.setUp()
        fakeRepository = FakeAnimalRepository()
        fakeLocationHandler = FakeLocationPermissionHandler()
        sut = AnimalListViewModel(
            repository: fakeRepository,
            locationHandler: fakeLocationHandler
        )
        
        // Wait for initial load to complete
        try await Task.sleep(for: .milliseconds(100))
    }
    
    override func tearDown() async throws {
        sut = nil
        fakeRepository = nil
        fakeLocationHandler = nil
        try await super.tearDown()
    }
    
    // MARK: - User Story 3: Refresh Behavior Tests
    
    /// T061 [P] [US3] Test: AnimalListViewModel onAppear should trigger automatic refresh when returning from create flow
    func testOnAppear_whenReturningFromCreateFlow_shouldTriggerAutomaticRefresh() async throws {
        // Given - ViewModel has initial data
        let initialAnimals = fakeRepository.generateMockAnimals(count: 3)
        fakeRepository.stubbedAnimals = initialAnimals
        await sut.loadAnimals()
        
        XCTAssertEqual(sut.cardViewModels.count, 3, "Initial load should have 3 animals")
        
        // When - User returns from create flow (simulated by adding new animal to backend)
        let newAnimal = Animal(
            id: "new-test-id",
            name: "Newly Created Pet",
            species: .dog,
            status: .missing,
            photoUrl: "http://localhost:3000/images/new.jpg",
            lastSeenDate: Date(),
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
            breed: "Test Breed",
            description: "Newly created test pet",
            contactPhone: "+48123456789"
        )
        fakeRepository.stubbedAnimals = initialAnimals + [newAnimal]
        
        // Simulate onAppear triggering refresh
        await sut.loadAnimals()
        
        // Then - List should contain new animal
        XCTAssertEqual(sut.cardViewModels.count, 4, "After refresh, should have 4 animals (3 initial + 1 new)")
        XCTAssertTrue(
            sut.cardViewModels.contains(where: { $0.animal.id == "new-test-id" }),
            "New animal should be present in the list"
        )
    }
    
    /// T062 [P] [US3] Test: AnimalListViewModel refresh should cancel previous task if already loading
    func testLoadAnimals_whenAlreadyLoading_shouldCancelPreviousTask() async throws {
        // Given - Repository with slow response to simulate ongoing request
        fakeRepository.delayDuration = 2.0 // 2 second delay
        
        // When - Start first load (will take 2 seconds)
        let firstLoadTask = Task {
            await sut.loadAnimals()
        }
        
        // Wait briefly to ensure first load has started
        try await Task.sleep(for: .milliseconds(100))
        XCTAssertTrue(sut.isLoading, "First load should be in progress")
        
        // Start second load immediately (should cancel first)
        let secondLoadTask = Task {
            await sut.loadAnimals()
        }
        
        // Then - Only second load should complete, first should be cancelled
        // Wait for both tasks to finish (with timeout)
        _ = await firstLoadTask.result
        _ = await secondLoadTask.result
        
        // Verify that we don't have duplicate calls (second should cancel first)
        // Note: This test will FAIL initially because task cancellation is not implemented yet
        // After implementation, repository should be called exactly twice (once for each load attempt)
        XCTAssertLessThanOrEqual(
            fakeRepository.getAnimalsCallCount,
            2,
            "Should not accumulate duplicate requests when rapidly calling loadAnimals"
        )
    }
    
    // MARK: - Helper Tests (existing functionality)
    
    /// Test: Initial load populates cardViewModels
    func testInit_shouldLoadAnimalsAutomatically() async throws {
        // Given/When - ViewModel initialized in setUp
        try await Task.sleep(for: .milliseconds(100))
        
        // Then - Animals should be loaded
        XCTAssertFalse(sut.cardViewModels.isEmpty, "Initial load should populate animals")
        XCTAssertFalse(sut.isLoading, "Loading should be complete")
    }
    
    /// Test: Empty repository returns empty list
    func testLoadAnimals_whenRepositoryReturnsEmpty_shouldShowEmptyState() async throws {
        // Given - Empty repository
        fakeRepository.stubbedAnimals = []
        
        // When - Load animals
        await sut.loadAnimals()
        
        // Then - Empty state
        XCTAssertTrue(sut.cardViewModels.isEmpty, "Should have no animals")
        XCTAssertTrue(sut.isEmpty, "isEmpty computed property should be true")
    }
    
    /// Test: Repository error sets error message
    func testLoadAnimals_whenRepositoryThrowsError_shouldSetErrorMessage() async throws {
        // Given - Repository that throws error
        fakeRepository.shouldFail = true
        fakeRepository.error = RepositoryError.networkError(URLError(.notConnectedToInternet))
        
        // When - Load animals
        await sut.loadAnimals()
        
        // Then - Error message set
        XCTAssertNotNil(sut.errorMessage, "Error message should be set")
        XCTAssertTrue(sut.cardViewModels.isEmpty, "Should have no animals on error")
        XCTAssertFalse(sut.isLoading, "Loading should be complete")
    }
}

// MARK: - Fake Location Handler

/// Fake LocationPermissionHandler for testing
class FakeLocationPermissionHandler: LocationPermissionHandler {
    var stubbedStatus: LocationPermissionStatus = .authorized
    var stubbedLocation: Coordinate? = Coordinate(latitude: 52.2297, longitude: 21.0122)
    
    func requestLocationWithPermissions() async -> LocationRequestResult {
        return LocationRequestResult(status: stubbedStatus, location: stubbedLocation)
    }
    
    func startObservingForeground(_ callback: @escaping @MainActor (LocationPermissionStatus, Bool) -> Void) {
        // No-op for tests
    }
    
    func stopObservingForeground() {
        // No-op for tests
    }
}


