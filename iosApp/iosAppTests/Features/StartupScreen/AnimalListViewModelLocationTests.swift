import XCTest
@testable import iosApp

/**
 * Unit tests for AnimalListViewModel location permission and fetching logic.
 * Tests User Story 1: Location-Aware Content for Authorized Users.
 * Uses FakeLocationService and FakeAnimalRepository for isolation.
 * Follows Given-When-Then structure per constitution.
 */
@MainActor
final class AnimalListViewModelLocationTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    private var fakeLocationService: FakeLocationService!
    private var fakeRepository: FakeAnimalRepository!
    private var viewModel: AnimalListViewModel!
    
    // MARK: - Setup / Teardown
    
    override func setUp() async throws {
        try await super.setUp()
        fakeLocationService = FakeLocationService()
        fakeRepository = FakeAnimalRepository()
    }
    
    override func tearDown() async throws {
        viewModel = nil
        fakeRepository = nil
        fakeLocationService = nil
        try await super.tearDown()
    }
    
    // MARK: - T019: loadAnimals fetches location when authorized
    
    func test_loadAnimals_whenLocationPermissionGranted_shouldFetchLocation() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(UserLocation(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnimals = []
        
        viewModel = AnimalListViewModel(
            repository: fakeRepository,
            locationService: fakeLocationService
        )
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let locationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertTrue(locationCalled, "Should request location when permission granted")
    }
    
    // MARK: - T020: loadAnimals queries with coordinates when location available
    
    func test_loadAnimals_whenLocationAvailable_shouldQueryWithCoordinates() async {
        // Given
        let expectedLocation = UserLocation(latitude: 52.2297, longitude: 21.0122)
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(expectedLocation)
        fakeRepository.stubbedAnimals = []
        
        viewModel = AnimalListViewModel(
            repository: fakeRepository,
            locationService: fakeLocationService
        )
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNotNil(passedLocation, "Should pass location to repository")
        XCTAssertEqual(passedLocation?.latitude, expectedLocation.latitude, accuracy: 0.0001)
        XCTAssertEqual(passedLocation?.longitude, expectedLocation.longitude, accuracy: 0.0001)
    }
    
    // MARK: - T021: loadAnimals queries without coordinates when location fetch fails
    
    func test_loadAnimals_whenLocationFetchFails_shouldQueryWithoutCoordinates() async {
        // Given
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(nil) // Simulate location fetch failure
        fakeRepository.stubbedAnimals = []
        
        viewModel = AnimalListViewModel(
            repository: fakeRepository,
            locationService: fakeLocationService
        )
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNil(passedLocation, "Should pass nil location when fetch fails")
    }
    
    func test_loadAnimals_whenPermissionDenied_shouldNotFetchLocation() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnimals = []
        
        viewModel = AnimalListViewModel(
            repository: fakeRepository,
            locationService: fakeLocationService
        )
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let locationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertFalse(locationCalled, "Should not request location when permission denied")
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when denied")
    }
    
    func test_loadAnimals_whenPermissionNotDetermined_shouldQueryWithoutLocation() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await setLocationServiceLocation(nil)
        fakeRepository.stubbedAnimals = []
        
        viewModel = AnimalListViewModel(
            repository: fakeRepository,
            locationService: fakeLocationService
        )
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when status notDetermined")
    }
    
    // MARK: - Helper Methods
    
    private func setLocationServiceStatus(_ status: LocationPermissionStatus) async {
        await fakeLocationService.setStatus(status)
    }
    
    private func setLocationServiceLocation(_ location: UserLocation?) async {
        await fakeLocationService.setLocation(location)
    }
}

// MARK: - Fake Animal Repository

/**
 * Fake repository for testing AnimalListViewModel location logic.
 * Tracks location parameter passed to getAnimals().
 */
class FakeAnimalRepository: AnimalRepositoryProtocol {
    var stubbedAnimals: [Animal] = []
    var lastLocationParameter: UserLocation?
    var shouldThrowError = false
    
    func getAnimals(near location: UserLocation?) async throws -> [Animal] {
        lastLocationParameter = location
        
        if shouldThrowError {
            throw NSError(domain: "FakeRepository", code: -1, userInfo: nil)
        }
        
        return stubbedAnimals
    }
    
    func getPetDetails(id: String) async throws -> PetDetails {
        throw NSError(domain: "FakeRepository", code: -1, userInfo: [NSLocalizedDescriptionKey: "Not implemented"])
    }
}

// MARK: - FakeLocationService Extensions

extension FakeLocationService {
    func setStatus(_ status: LocationPermissionStatus) {
        stubbedAuthorizationStatus = status
    }
    
    func setLocation(_ location: UserLocation?) {
        stubbedLocation = location
    }
}

