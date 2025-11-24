import XCTest
@testable import PetSpot

/**
 * Unit tests for AnimalListViewModel.
 * Tests @Published property updates and coordinator callback invocations.
 * Follows Given-When-Then structure per project constitution.
 */
@MainActor
final class AnimalListViewModelTests: XCTestCase {
    
    // MARK: - Test loadAnimals Success
    
    /**
     * Tests that loadAnimals updates @Published cardViewModels property on success.
     */
    func testLoadAnimals_whenRepositorySucceeds_shouldUpdateCardViewModels() async {
        // Given - ViewModel with fake repository returning animals
        let fakeRepository = FakeAnimalRepository(
            animalCount: 16,
            shouldFail: false
        )
        let viewModel = AnimalListViewModel(repository: fakeRepository)
        
        // When - loadAnimals is called
        await viewModel.loadAnimals()
        
        // Then - cardViewModels should be populated and state updated
        XCTAssertEqual(viewModel.cardViewModels.count, 16, "Should have 16 card ViewModels")
        XCTAssertFalse(viewModel.isLoading, "Should not be loading")
        XCTAssertNil(viewModel.errorMessage, "Should have no error")
        XCTAssertFalse(viewModel.isEmpty, "isEmpty should be false when card ViewModels present")
    }
    
    // MARK: - Test loadAnimals Failure
    
    /**
     * Tests that loadAnimals sets errorMessage on failure.
     */
//    func testLoadAnimals_whenRepositoryFails_shouldSetErrorMessage() async {
//        // Given - Repository configured to fail with default error
//        let fakeRepository = FakeAnimalRepository(
//            animalCount: 0,
//            shouldFail: true,
//            exception: KotlinException(message: "Network error")
//        )
//        let getAnimalsUseCase = GetAnimalsUseCase(repository: fakeRepository)
//        let viewModel = AnimalListViewModel(getAnimalsUseCase: getAnimalsUseCase)
//        
//        // When - loadAnimals is called
//        await viewModel.loadAnimals()
//        
//        // Then - errorMessage should be set, isLoading false, cardViewModels empty
//        XCTAssertFalse(viewModel.isLoading, "Should not be loading")
//        XCTAssertNotNil(viewModel.errorMessage, "Should have error message")
//        XCTAssertTrue(viewModel.cardViewModels.isEmpty, "Should have no card ViewModels on error")
//        XCTAssertFalse(viewModel.isEmpty, "isEmpty should be false when error present")
//    }
    
    // MARK: - Test isEmpty Property
    
    /**
     * Tests isEmpty computed property returns true when no card ViewModels.
     */
    func test_isEmpty_whenNoCardViewModelsAndNotLoadingAndNoError_shouldReturnTrue() {
        // Given - ViewModel with empty cardViewModels list
        let fakeRepository = FakeAnimalRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = AnimalListViewModel(repository: fakeRepository)
        
        // Manually set state to empty (before loadAnimals runs)
        viewModel.cardViewModels = []
        viewModel.isLoading = false
        viewModel.errorMessage = nil
        
        // When - checking isEmpty
        let isEmpty = viewModel.isEmpty
        
        // Then - should be true
        XCTAssertTrue(isEmpty, "isEmpty should be true when no card ViewModels, not loading, and no error")
    }
    
    /**
     * Tests isEmpty computed property returns false when card ViewModels present.
     */
    func test_isEmpty_whenCardViewModelsPresent_shouldReturnFalse() async {
        // Given - ViewModel with card ViewModels loaded
        let fakeRepository = FakeAnimalRepository(
            animalCount: 16,
            shouldFail: false
        )
        let viewModel = AnimalListViewModel(repository: fakeRepository)
        
        // When - card ViewModels are loaded
        await viewModel.loadAnimals()
        
        // Then - isEmpty should be false
        XCTAssertFalse(viewModel.isEmpty, "isEmpty should be false when card ViewModels present")
    }
    
    // MARK: - Test selectAnimal Callback
    
    /**
     * Tests that selectAnimal invokes onAnimalSelected closure.
     */
    func testSelectAnimal_shouldInvokeOnAnimalSelectedClosure() {
        // Given - ViewModel with callback closure
        let fakeRepository = FakeAnimalRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = AnimalListViewModel(repository: fakeRepository)
        
        var capturedAnimalId: String?
        viewModel.onAnimalSelected = { animalId in
            capturedAnimalId = animalId
        }
        
        // When - selectAnimal is called
        viewModel.selectAnimal(id: "test-animal-123")
        
        // Then - closure should be invoked with correct ID
        XCTAssertEqual(capturedAnimalId, "test-animal-123", "Should invoke closure with correct animal ID")
    }
    
    // MARK: - Test reportMissing Callback
    
    /**
     * Tests that reportMissing invokes onReportMissing closure.
     */
    func testReportMissing_shouldInvokeOnReportMissingClosure() {
        // Given - ViewModel with callback closure
        let fakeRepository = FakeAnimalRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = AnimalListViewModel(repository: fakeRepository)
        
        var callbackInvoked = false
        viewModel.onReportMissing = {
            callbackInvoked = true
        }
        
        // When - reportMissing is called
        viewModel.reportMissing()
        
        // Then - closure should be invoked
        XCTAssertTrue(callbackInvoked, "Should invoke onReportMissing closure")
    }
    
    // MARK: - Test reportFound Callback
    
    /**
     * Tests that reportFound invokes onReportFound closure.
     * Note: Not exposed in iOS mobile UI, but included for completeness.
     */
    func testReportFound_shouldInvokeOnReportFoundClosure() {
        // Given - ViewModel with callback closure
        let fakeRepository = FakeAnimalRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = AnimalListViewModel(repository: fakeRepository)
        
        var callbackInvoked = false
        viewModel.onReportFound = {
            callbackInvoked = true
        }
        
        // When - reportFound is called
        viewModel.reportFound()
        
        // Then - closure should be invoked
        XCTAssertTrue(callbackInvoked, "Should invoke onReportFound closure")
    }
}
