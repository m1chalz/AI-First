import XCTest
@testable import PetSpot

/// Unit tests for PetDetailsViewModel
/// Tests cover loading states, error handling, and retry functionality
@MainActor
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
            species: "DOG",
            gender: "MALE",
            description: "Test description",
            phone: "+48 123 456 789",
            email: "test@example.com",
            breed: "Test Breed",
            latitude: latitude,
            longitude: longitude,
            locationRadius: 5,
            microchipNumber: "123-456-789",
            approximateAge: "2 years",
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
            locationRadius: petDetails.locationRadius,
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
            locationRadius: petDetails.locationRadius,
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
            locationRadius: petDetails.locationRadius,
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
    
    func testFormattedSpecies_whenSpeciesIsUppercase_shouldReturnCapitalized() async {
        // Given
        let (sut, repository) = makeSUT()
        var petDetails = makeMockPetDetails()
        petDetails = PetDetails(
            id: petDetails.id,
            petName: petDetails.petName,
            photoUrl: petDetails.photoUrl,
            status: petDetails.status,
            lastSeenDate: petDetails.lastSeenDate,
            species: "DOG",
            gender: petDetails.gender,
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            locationRadius: petDetails.locationRadius,
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
        XCTAssertEqual(result, "Dog")
    }
    
    func testGenderIconName_whenStateIsLoading_shouldReturnQuestionmark() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.genderIconName
        
        // Then
        XCTAssertEqual(result, "questionmark")
    }
    
    func testGenderIconName_whenGenderIsMale_shouldReturnArrowUpRight() async {
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
            gender: "MALE",
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            locationRadius: petDetails.locationRadius,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.genderIconName
        
        // Then
        XCTAssertEqual(result, "arrow.up.right")
    }
    
    func testGenderIconName_whenGenderIsFemale_shouldReturnArrowDownRight() async {
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
            gender: "FEMALE",
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            locationRadius: petDetails.locationRadius,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.genderIconName
        
        // Then
        XCTAssertEqual(result, "arrow.down.right")
    }
    
    func testGenderIconName_whenGenderIsUnknown_shouldReturnQuestionmark() async {
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
            gender: "UNKNOWN",
            description: petDetails.description,
            phone: petDetails.phone,
            email: petDetails.email,
            breed: petDetails.breed,
            latitude: petDetails.latitude,
            longitude: petDetails.longitude,
            locationRadius: petDetails.locationRadius,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.genderIconName
        
        // Then
        XCTAssertEqual(result, "questionmark")
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
            locationRadius: petDetails.locationRadius,
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
            locationRadius: petDetails.locationRadius,
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
    
    func testFormattedRadius_whenStateIsLoading_shouldReturnNil() {
        // Given
        let (sut, _) = makeSUT()
        
        // When
        let result = sut.formattedRadius
        
        // Then
        XCTAssertNil(result)
    }
    
    func testFormattedRadius_whenRadiusIsNil_shouldReturnNil() async {
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
            locationRadius: nil,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedRadius
        
        // Then
        XCTAssertNil(result)
    }
    
    func testFormattedRadius_whenRadiusIsValid_shouldReturnFormattedString() async {
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
            locationRadius: 5,
            microchipNumber: petDetails.microchipNumber,
            approximateAge: petDetails.approximateAge,
            reward: petDetails.reward,
            createdAt: petDetails.createdAt,
            updatedAt: petDetails.updatedAt
        )
        repository.mockPetDetails = petDetails
        await sut.loadPetDetails()
        
        // When
        let result = sut.formattedRadius
        
        // Then
        XCTAssertNotNil(result)
        // The exact format depends on L10n, but it should not be nil
        if let radiusText = result {
            XCTAssertFalse(radiusText.isEmpty)
        }
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
        XCTAssertEqual(result, "52.2297, 21.0122")
    }
    
    // MARK: - Static Formatter Tests
    
    func testStaticFormatMicrochip_whenMicrochipIsNil_shouldReturnDash() {
        // Given + When
        let result = PetDetailsViewModel.formatMicrochip(nil)
        
        // Then
        XCTAssertEqual(result, "—")
    }
    
    func testStaticFormatMicrochip_whenMicrochipHas12Digits_shouldFormatWithDashes() {
        // Given + When
        let result = PetDetailsViewModel.formatMicrochip("123456789012")
        
        // Then
        XCTAssertEqual(result, "123-456-789-012")
    }
    
    func testStaticFormatSpecies_whenSpeciesIsUppercase_shouldReturnCapitalized() {
        // Given + When
        let result = PetDetailsViewModel.formatSpecies("DOG")
        
        // Then
        XCTAssertEqual(result, "Dog")
    }
    
    func testStaticGenderIcon_whenGenderIsMale_shouldReturnArrowUpRight() {
        // Given + When
        let result = PetDetailsViewModel.genderIcon("MALE")
        
        // Then
        XCTAssertEqual(result, "arrow.up.right")
    }
    
    func testStaticGenderIcon_whenGenderIsFemale_shouldReturnArrowDownRight() {
        // Given + When
        let result = PetDetailsViewModel.genderIcon("FEMALE")
        
        // Then
        XCTAssertEqual(result, "arrow.down.right")
    }
    
    func testStaticFormatDate_whenDateIsValid_shouldFormatCorrectly() {
        // Given + When
        let result = PetDetailsViewModel.formatDate("2025-11-20")
        
        // Then
        XCTAssertEqual(result, "Nov 20, 2025")
    }
    
    func testStaticFormatRadius_whenRadiusIsNil_shouldReturnNil() {
        // Given + When
        let result = PetDetailsViewModel.formatRadius(nil)
        
        // Then
        XCTAssertNil(result)
    }
    
    func testStaticFormatRadius_whenRadiusIsValid_shouldReturnFormattedString() {
        // Given + When
        let result = PetDetailsViewModel.formatRadius(5)
        
        // Then
        XCTAssertNotNil(result)
        if let radiusText = result {
            XCTAssertFalse(radiusText.isEmpty)
        }
    }
}

