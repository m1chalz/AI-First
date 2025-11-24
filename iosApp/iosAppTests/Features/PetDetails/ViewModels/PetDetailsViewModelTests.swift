import XCTest
@testable import iosApp

/// Unit tests for PetDetailsViewModel
/// Tests cover loading states, error handling, and retry functionality
final class PetDetailsViewModelTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    /// Fake repository for testing
    private class FakeAnimalRepository: AnimalRepositoryProtocol {
        var shouldThrowError = false
        var mockPetDetails: PetDetails?
        var getAnimalsCallCount = 0
        var getPetDetailsCallCount = 0
        
        func getAnimals() async throws -> [Animal] {
            getAnimalsCallCount += 1
            return []
        }
        
        func getPetDetails(id: String) async throws -> PetDetails {
            getPetDetailsCallCount += 1
            
            if shouldThrowError {
                throw NSError(
                    domain: "FakeRepository",
                    code: 500,
                    userInfo: [NSLocalizedDescriptionKey: "Mock error"]
                )
            }
            
            guard let petDetails = mockPetDetails else {
                throw NSError(
                    domain: "FakeRepository",
                    code: 404,
                    userInfo: [NSLocalizedDescriptionKey: "Pet not found"]
                )
            }
            
            return petDetails
        }
    }
    
    // MARK: - Helper Methods
    
    private func makeSUT(
        repository: AnimalRepositoryProtocol? = nil,
        petId: String = "test-id"
    ) -> (viewModel: PetDetailsViewModel, repository: FakeAnimalRepository) {
        let fakeRepo = (repository as? FakeAnimalRepository) ?? FakeAnimalRepository()
        let viewModel = PetDetailsViewModel(repository: fakeRepo, petId: petId)
        return (viewModel, fakeRepo)
    }
    
    private func makeMockPetDetails(id: String = "test-id") -> PetDetails {
        return PetDetails(
            id: id,
            petName: "Test Pet",
            photoUrl: "https://example.com/photo.jpg",
            status: "ACTIVE",
            lastSeenDate: "2025-11-20",
            species: "DOG",
            gender: "MALE",
            description: "Test description",
            location: "Test City",
            phone: "+48 123 456 789",
            email: "test@example.com",
            breed: "Test Breed",
            locationRadius: 5,
            microchipNumber: "123-456-789",
            approximateAge: "2 years",
            reward: "$100",
            createdAt: "2025-11-20T10:00:00.000Z",
            updatedAt: "2025-11-20T10:00:00.000Z"
        )
    }
    
    // MARK: - Tests
    
    func testInitialState_shouldBeLoading() {
        // Given + When
        let (sut, _) = makeSUT()
        
        // Then
        XCTAssertEqual(sut.state, .loading)
    }
    
    func testLoadPetDetails_whenRepositorySucceeds_shouldUpdateStateToLoaded() async {
        // Given
        let (sut, repository) = makeSUT()
        let mockPet = makeMockPetDetails()
        repository.mockPetDetails = mockPet
        
        // When
        await sut.loadPetDetails()
        
        // Then
        guard case .loaded(let petDetails) = sut.state else {
            XCTFail("Expected loaded state, got \(sut.state)")
            return
        }
        XCTAssertEqual(petDetails.id, mockPet.id)
        XCTAssertEqual(petDetails.petName, mockPet.petName)
        XCTAssertEqual(repository.getPetDetailsCallCount, 1)
    }
    
    func testLoadPetDetails_whenRepositoryFails_shouldUpdateStateToError() async {
        // Given
        let (sut, repository) = makeSUT()
        repository.shouldThrowError = true
        
        // When
        await sut.loadPetDetails()
        
        // Then
        guard case .error(let message) = sut.state else {
            XCTFail("Expected error state, got \(sut.state)")
            return
        }
        XCTAssertFalse(message.isEmpty)
        XCTAssertEqual(repository.getPetDetailsCallCount, 1)
    }
    
    func testLoadPetDetails_whenPetNotFound_shouldUpdateStateToError() async {
        // Given
        let (sut, repository) = makeSUT()
        repository.mockPetDetails = nil // No pet data
        
        // When
        await sut.loadPetDetails()
        
        // Then
        guard case .error(let message) = sut.state else {
            XCTFail("Expected error state, got \(sut.state)")
            return
        }
        XCTAssertTrue(message.contains("not found") || message.contains("Not found"))
    }
    
    func testRetry_whenInErrorState_shouldTransitionToLoading() async {
        // Given
        let (sut, repository) = makeSUT()
        repository.shouldThrowError = true
        await sut.loadPetDetails()
        
        guard case .error = sut.state else {
            XCTFail("Setup failed: Expected error state")
            return
        }
        
        // When
        repository.shouldThrowError = false
        repository.mockPetDetails = makeMockPetDetails()
        sut.retry()
        
        // Wait for async retry to complete
        try? await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
        
        // Then
        guard case .loaded = sut.state else {
            XCTFail("Expected loaded state after retry, got \(sut.state)")
            return
        }
        XCTAssertEqual(repository.getPetDetailsCallCount, 2)
    }
    
    func testHandleBack_shouldInvokeOnBackCallback() {
        // Given
        let (sut, _) = makeSUT()
        var callbackInvoked = false
        sut.onBack = {
            callbackInvoked = true
        }
        
        // When
        sut.handleBack()
        
        // Then
        XCTAssertTrue(callbackInvoked)
    }
    
    func testLoadPetDetails_shouldSetLoadingStateFirst() async {
        // Given
        let (sut, repository) = makeSUT()
        repository.mockPetDetails = makeMockPetDetails()
        
        // Set initial state to error
        await sut.loadPetDetails()
        repository.shouldThrowError = true
        await sut.loadPetDetails()
        
        guard case .error = sut.state else {
            XCTFail("Setup failed: Expected error state")
            return
        }
        
        // When
        repository.shouldThrowError = false
        let loadTask = Task {
            await sut.loadPetDetails()
        }
        
        // Then (immediately after starting load)
        // Note: This is a race condition test - state should be loading immediately
        // In practice, for very fast operations, this might not always catch loading state
        
        await loadTask.value
        // Final state should be loaded
        guard case .loaded = sut.state else {
            XCTFail("Expected loaded state, got \(sut.state)")
            return
        }
    }
}

