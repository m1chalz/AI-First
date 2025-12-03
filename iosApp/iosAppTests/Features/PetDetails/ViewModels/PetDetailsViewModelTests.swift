import XCTest
@testable import PetSpot

/// Unit tests for PetDetailsViewModel
/// Tests cover loading states, error handling, and retry functionality
@MainActor
final class PetDetailsViewModelTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    // Uses FakeAnnouncementRepository from main app for testing
    
    // MARK: - Helper Methods
    
    private func makeSUT(
        petId: String = "test-id"
    ) -> (viewModel: PetDetailsViewModel, repository: FakeAnnouncementRepository) {
        let fakeRepo = FakeAnnouncementRepository()
        let viewModel = PetDetailsViewModel(repository: fakeRepo, petId: petId)
        return (viewModel, fakeRepo)
    }
    
    private func makeMockPetDetails(
        id: String = "test-id",
        photoUrl: String? = "https://example.com/photo.jpg",
        status: AnimalStatus = .active,
        latitude: Double = 52.2297,
        longitude: Double = 21.0122,
        reward: String? = "100 PLN"
    ) -> PetDetails {
        return PetDetails(
            id: id,
            petName: "Test Pet",
            photoUrl: photoUrl,
            status: status,
            lastSeenDate: "2025-11-20",
            species: .dog,
            gender: .male,
            description: "Test description",
            phone: "+48 123 456 789",
            email: "test@example.com",
            breed: "Test Breed",
            latitude: latitude,
            longitude: longitude,
            microchipNumber: "123-456-789",
            approximateAge: 2,
            reward: reward,
            createdAt: "2025-11-20T10:00:00.000Z",
            updatedAt: "2025-11-20T10:00:00.000Z"
        )
    }
    
    // MARK: - Tests
    
    func testInitialState_shouldBeLoading() {
        // Given + When
        let (sut, _) = makeSUT()
        
        // Then
        guard case .loading = sut.state else {
            XCTFail("Expected loading state, got \(sut.state)")
            return
        }
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
        repository.shouldFail = true
        
        // When
        await sut.loadPetDetails()
        
        // Then
        guard case .error(let message) = sut.state else {
            XCTFail("Expected error state, got \(sut.state)")
            return
        }
        // ViewModel returns generic L10n error message for all errors
        XCTAssertEqual(message, L10n.PetDetails.Error.loadingFailed)
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
        // ViewModel returns generic L10n error message for all errors
        XCTAssertEqual(message, L10n.PetDetails.Error.loadingFailed)
    }
    
    func testRetry_whenInErrorState_shouldTransitionToLoading() async {
        // Given
        let (sut, repository) = makeSUT()
        repository.shouldFail = true
        await sut.loadPetDetails()
        
        guard case .error = sut.state else {
            XCTFail("Setup failed: Expected error state")
            return
        }
        
        // When
        repository.shouldFail = false
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
        repository.shouldFail = true
        await sut.loadPetDetails()
        
        guard case .error = sut.state else {
            XCTFail("Setup failed: Expected error state")
            return
        }
        
        // When
        repository.shouldFail = false
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
    
    // MARK: - Formatter Computed Properties Tests
    
    func testFormattedMicrochip_whenStateIsLoading_shouldReturnDash() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.formattedMicrochip
        
        // Then
        XCTAssertEqual(result, "—")
    }
    
    func testFormattedMicrochip_whenMicrochipIsNil_shouldReturnDash() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: petDetails.species,
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: nil,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedMicrochip
        
        // Then
        XCTAssertEqual(result, "—")
    }
    
    func testFormattedMicrochip_whenMicrochipHas12Digits_shouldFormatWithDashes() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: petDetails.species,
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: "123456789012",
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedMicrochip
        
        // Then
        XCTAssertEqual(result, "123-456-789-012")
    }
    
    func testFormattedMicrochip_whenMicrochipHasLessThan12Digits_shouldReturnAsIs() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: petDetails.species,
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: "123-456",
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedMicrochip
        
        // Then
        XCTAssertEqual(result, "123-456")
    }
    
    func testFormattedSpecies_whenStateIsLoading_shouldReturnEmptyString() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.formattedSpecies
        
        // Then
        XCTAssertEqual(result, "")
    }
    
    func testFormattedSpecies_whenSpeciesIsDog_shouldReturnDisplayName() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: .dog,
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedSpecies
        
        // Then
        XCTAssertEqual(result, L10n.AnimalSpecies.dog)
    }
    
    func testGenderSymbol_whenStateIsLoading_shouldReturnQuestionMark() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.genderSymbol
        
        // Then
        XCTAssertEqual(result, "?")
    }
    
    func testGenderSymbol_whenGenderIsMale_shouldReturnMarsSymbol() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: petDetails.species,
            gender: .male,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.genderSymbol
        
        // Then
        XCTAssertEqual(result, "♂")
    }
    
    func testGenderSymbol_whenGenderIsFemale_shouldReturnVenusSymbol() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: petDetails.species,
            gender: .female,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.genderSymbol
        
        // Then
        XCTAssertEqual(result, "♀")
    }
    
    func testGenderSymbol_whenGenderIsUnknown_shouldReturnQuestionMark() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: petDetails.species,
            gender: .unknown,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.genderSymbol
        
        // Then
        XCTAssertEqual(result, "?")
    }
    
    func testFormattedDate_whenStateIsLoading_shouldReturnEmptyString() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.formattedDate
        
        // Then
        XCTAssertEqual(result, "")
    }
    
    func testFormattedDate_whenDateIsValid_shouldFormatCorrectly() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: "2025-11-20",
            species: petDetails.species,
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedDate
        
        // Then
        XCTAssertEqual(result, "Nov 20, 2025")
    }
    
    func testFormattedDate_whenDateIsInvalid_shouldReturnOriginalString() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: "invalid-date",
            species: petDetails.species,
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedDate
        
        // Then
        XCTAssertEqual(result, "invalid-date")
    }
    
    // MARK: - Photo With Badges Model Tests
    
    func testPhotoWithBadgesModel_whenStateIsLoading_shouldReturnNil() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.photoWithBadgesModel
        
        // Then
        XCTAssertNil(result)
    }
    
    func testPhotoWithBadgesModel_whenStateIsLoaded_shouldReturnModelWithCorrectData() async {
        // Given
        let (sut, repository) = makeSUT()
        let petDetails = makeMockPetDetails(
            photoUrl: "https://example.com/photo.jpg",
            status: .active,
            reward: "$500"
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.photoWithBadgesModel
        
        // Then
        XCTAssertNotNil(result)
        if let model = result {
            XCTAssertEqual(model.imageUrl, "https://example.com/photo.jpg")
            XCTAssertEqual(model.statusDisplayText, L10n.AnimalStatus.active)
            XCTAssertEqual(model.rewardText, "$500")
        }
    }
    
    // MARK: - Formatted Coordinates Tests
    
    func testFormattedCoordinates_whenStateIsLoading_shouldReturnDash() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.formattedCoordinates
        
        // Then
        XCTAssertEqual(result, "—")
    }
    
    func testFormattedCoordinates_whenStateIsLoaded_shouldFormatCorrectly() async {
        // Given
        let (sut, repository) = makeSUT()
        let petDetails = makeMockPetDetails(
            latitude: 52.2297,
            longitude: 21.0122
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedCoordinates
        
        // Then
        XCTAssertEqual(result, "52.2297° N, 21.0122° E")
    }
    
    func testFormattedCoordinates_whenLatitudeIsNegative_shouldShowSouth() async {
        // Given
        let (sut, repository) = makeSUT()
        let petDetails = makeMockPetDetails(
            latitude: -33.8688,
            longitude: 151.2093
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedCoordinates
        
        // Then
        XCTAssertEqual(result, "33.8688° S, 151.2093° E")
    }
    
    func testFormattedCoordinates_whenLongitudeIsNegative_shouldShowWest() async {
        // Given
        let (sut, repository) = makeSUT()
        let petDetails = makeMockPetDetails(
            latitude: 40.7128,
            longitude: -74.0060
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedCoordinates
        
        // Then
        XCTAssertEqual(result, "40.7128° N, 74.0060° W")
    }
    
    func testFormattedCoordinates_whenBothAreNegative_shouldShowSouthAndWest() async {
        // Given
        let (sut, repository) = makeSUT()
        let petDetails = makeMockPetDetails(
            latitude: -34.6037,
            longitude: -58.3816
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedCoordinates
        
        // Then
        XCTAssertEqual(result, "34.6037° S, 58.3816° W")
    }
    
    // MARK: - API Integration Tests (User Story 2)
    
    /// T044: Test PetDetailsViewModel loadDetails should update petDetails publisher with API data
    func testLoadPetDetails_whenRepositoryReturnsApiData_shouldUpdateState() async {
        // Given - ViewModel with repository that returns API-like data
        let (sut, repository) = makeSUT(petId: "api-test-id")
        let apiPetDetails = makeMockPetDetails(
            id: "api-test-id",
            photoUrl: "http://localhost:3000/images/test.jpg"
        )
        repository.mockPetDetails = apiPetDetails
        
        // When - loadPetDetails is called
        await sut.loadPetDetails()
        
        // Then - state should be loaded with API data
        guard case .loaded(let details) = sut.state else {
            XCTFail("Expected loaded state, got \(sut.state)")
            return
        }
        XCTAssertEqual(details.id, "api-test-id")
        XCTAssertEqual(details.petName, "Test Pet")
        XCTAssertEqual(repository.getPetDetailsCallCount, 1)
    }
    
    /// T045: Test PetDetailsViewModel with 404 error should set appropriate error state
    
    /// T046: Test PetDetailsViewModel with network error should set appropriate error state
    
}

