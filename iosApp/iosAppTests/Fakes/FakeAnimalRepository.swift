import Foundation
@testable import PetSpot

/// Fake repository implementation for unit testing.
/// Implements AnimalRepository protocol for controlled test scenarios.
///
/// Allows controlling success/failure scenarios for testing:
/// - Success: Returns mock animals
/// - Failure: Throws configurable error
/// - Empty: Returns empty list when animalCount = 0
class FakeAnimalRepository: AnimalRepository {
    private let animalCount: Int
    private let shouldFail: Bool
    private let error: Error
    
    /// Tracks how many times getAnimals was called (for test assertions)
    private(set) var getAnimalsCallCount = 0
    
    /// Creates a fake repository with configurable behavior.
    ///
    /// - Parameters:
    ///   - animalCount: Number of mock animals to return (default: 16)
    ///   - shouldFail: Whether to throw error instead of returning data (default: false)
    ///   - error: Error to throw when shouldFail is true (default: generic error)
    init(
        animalCount: Int = 16,
        shouldFail: Bool = false,
        error: Error = NSError(domain: "FakeRepository", code: 1, userInfo: [NSLocalizedDescriptionKey: "Fake repository error"])
    ) {
        self.animalCount = animalCount
        self.shouldFail = shouldFail
        self.error = error
    }
    
    func getAnimals() async throws -> [Animal] {
        getAnimalsCallCount += 1
        
        if shouldFail {
            throw error
        }
        
        return generateMockAnimals(count: animalCount)
    }
    
    /// Generates mock animal data for testing.
    /// Matches structure of production mock data for consistency.
    private func generateMockAnimals(count: Int) -> [Animal] {
        guard count > 0 else { return [] }
        
        let mockAnimals: [Animal] = [
            Animal(
                id: "1",
                name: "Fluffy",
                photoUrl: "placeholder_cat",
                location: Location(city: "Pruszkow", radiusKm: 5),
                species: .cat,
                breed: "Maine Coon",
                gender: .male,
                status: .active,
                lastSeenDate: "18/11/2025",
                description: "Friendly orange tabby cat, last seen near the park.",
                email: "owner@example.com",
                phone: "+48123456789"
            ),
            Animal(
                id: "2",
                name: "Buddy",
                photoUrl: "placeholder_dog",
                location: Location(city: "Warsaw", radiusKm: 10),
                species: .dog,
                breed: "Golden Retriever",
                gender: .male,
                status: .found,
                lastSeenDate: "19/11/2025",
                description: "Golden retriever found in Lazienki Park.",
                email: nil,
                phone: nil
            ),
            // Add more mock animals as needed for comprehensive testing
        ]
        
        // Return requested number of animals (cycle through if count > mockAnimals.count)
        return (0..<count).map { i in mockAnimals[i % mockAnimals.count] }
    }
}

