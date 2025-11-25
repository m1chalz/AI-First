import Foundation
@testable import PetSpot

/// Fake repository implementation for unit testing.
/// Implements AnimalRepositoryProtocol protocol for controlled test scenarios.
///
/// Allows controlling success/failure scenarios for testing:
/// - Success: Returns mock animals
/// - Failure: Throws configurable error
/// - Empty: Returns empty list when animalCount = 0
class FakeAnimalRepository: AnimalRepositoryProtocol {
    
    private let animalCount: Int
    private let shouldFail: Bool
    private let error: Error
    
    /// Tracks how many times getAnimals was called (for test assertions)
    private(set) var getAnimalsCallCount = 0
    
    /// Tracks how many times getPetDetails was called (for test assertions)
    private(set) var getPetDetailsCallCount = 0
    
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
    
    func getPetDetails(id: String) async throws -> PetDetails {
        getPetDetailsCallCount += 1
        
        if shouldFail {
            throw error
        }
        
        guard let petDetails = generateMockPetDetails(id: id) else {
            throw NSError(
                domain: "FakeAnimalRepository",
                code: 404,
                userInfo: [NSLocalizedDescriptionKey: "Pet not found"]
            )
        }
        
        return petDetails
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
            email: "john.doe@example.com",
            phone: "+48 123 456 789"
        ),
        Animal(
            id: "2",
            name: "Rex",
            photoUrl: "placeholder_dog",
            location: Location(city: "Warsaw", radiusKm: 10),
            species: .dog,
            breed: "German Shepherd",
            gender: .female,
            status: .active,
            lastSeenDate: "17/11/2025",
            description: "Large black and tan dog, wearing red collar.",
            email: "anna.smith@example.com",
            phone: nil
        ),
        Animal(
            id: "3",
            name: "Bella",
            photoUrl: "placeholder_cat",
            location: Location(city: "Krakow", radiusKm: 3),
            species: .cat,
            breed: "Siamese",
            gender: .female,
            status: .found,
            lastSeenDate: "19/11/2025",
            description: "Blue-eyed white cat found near train station.",
            email: nil,
            phone: "+48 987 654 321"
        ),
        Animal(
            id: "4",
            name: "Buddy",
            photoUrl: "placeholder_dog",
            location: Location(city: "Wroclaw", radiusKm: 7),
            species: .dog,
            breed: "Labrador Retriever",
            gender: .male,
            status: .active,
            lastSeenDate: "16/11/2025",
            description: "Yellow lab, very friendly, responds to 'Buddy'.",
            email: "mike@example.com",
            phone: "+48 111 222 333"
        ),
        Animal(
            id: "5",
            name: "Tweety",
            photoUrl: "placeholder_bird",
            location: Location(city: "Gdansk", radiusKm: 15),
            species: .bird,
            breed: "Cockatiel",
            gender: .unknown,
            status: .active,
            lastSeenDate: "15/11/2025",
            description: "Gray and yellow bird, escaped from balcony.",
            email: "sarah@example.com",
            phone: nil
        ),
        Animal(
            id: "6",
            name: "Snowball",
            photoUrl: "placeholder_cat",
            location: Location(city: "Poznan", radiusKm: 8),
            species: .cat,
            breed: "Persian",
            gender: .female,
            status: .active,
            lastSeenDate: "14/11/2025",
            description: "White long-haired cat, very shy.",
            email: nil,
            phone: nil
        ),
        Animal(
            id: "7",
            name: "Snoopy",
            photoUrl: "placeholder_dog",
            location: Location(city: "Lodz", radiusKm: 12),
            species: .dog,
            breed: "Beagle",
            gender: .male,
            status: .found,
            lastSeenDate: "20/11/2025",
            description: "Tri-color beagle found wandering near shopping center.",
            email: "finder@example.com",
            phone: "+48 555 666 777"
        ),
        Animal(
            id: "8",
            name: "Thumper",
            photoUrl: "placeholder_rabbit",
            location: Location(city: "Katowice", radiusKm: 6),
            species: .rabbit,
            breed: "Dwarf Rabbit",
            gender: .female,
            status: .closed,
            lastSeenDate: "13/11/2025",
            description: "Small gray rabbit, reunited with owner.",
            email: "owner@example.com",
            phone: "+48 444 333 222"
        ),
        Animal(
            id: "9",
            name: "Shadow",
            photoUrl: "placeholder_dog",
            location: Location(city: "Szczecin", radiusKm: 20),
            species: .dog,
            breed: "Husky",
            gender: .male,
            status: .active,
            lastSeenDate: "12/11/2025",
            description: "Blue-eyed Siberian Husky, very energetic.",
            email: nil,
            phone: "+48 888 999 000"
        ),
        Animal(
            id: "10",
            name: "Whiskers",
            photoUrl: "placeholder_cat",
            location: Location(city: "Bialystok", radiusKm: 4),
            species: .cat,
            breed: "British Shorthair",
            gender: .male,
            status: .active,
            lastSeenDate: "11/11/2025",
            description: "Gray tabby cat with green eyes.",
            email: "cat.owner@example.com",
            phone: nil
        ),
        Animal(
            id: "11",
            name: "Luna",
            photoUrl: "placeholder_dog",
            location: Location(city: "Lublin", radiusKm: 9),
            species: .dog,
            breed: "Golden Retriever",
            gender: .female,
            status: .active,
            lastSeenDate: "10/11/2025",
            description: "Golden retriever puppy, very playful.",
            email: "luna.owner@example.com",
            phone: "+48 222 333 444"
        ),
        Animal(
            id: "12",
            name: "Charlie",
            photoUrl: "placeholder_cat",
            location: Location(city: "Rzeszow", radiusKm: 5),
            species: .cat,
            breed: "Ragdoll",
            gender: .male,
            status: .found,
            lastSeenDate: "21/11/2025",
            description: "Blue-eyed ragdoll cat, found in garage.",
            email: "finder123@example.com",
            phone: nil
        ),
        Animal(
            id: "13",
            name: "Max",
            photoUrl: "placeholder_dog",
            location: Location(city: "Torun", radiusKm: 11),
            species: .dog,
            breed: "Dachshund",
            gender: .male,
            status: .active,
            lastSeenDate: "09/11/2025",
            description: "Small brown dachshund, wears blue collar.",
            email: "max.family@example.com",
            phone: "+48 333 444 555"
        ),
        Animal(
            id: "14",
            name: "Milo",
            photoUrl: "placeholder_bird",
            location: Location(city: "Gliwice", radiusKm: 6),
            species: .bird,
            breed: "Parrot",
            gender: .unknown,
            status: .active,
            lastSeenDate: "08/11/2025",
            description: "Green parrot, can say 'Hello'.",
            email: nil,
            phone: "+48 666 777 888"
        ),
        Animal(
            id: "15",
            name: "Daisy",
            photoUrl: "placeholder_cat",
            location: Location(city: "Bydgoszcz", radiusKm: 7),
            species: .cat,
            breed: "Bengal",
            gender: .female,
            status: .active,
            lastSeenDate: "07/11/2025",
            description: "Spotted bengal cat, very active.",
            email: "daisy.home@example.com",
            phone: "+48 777 888 999"
        ),
        Animal(
            id: "16",
            name: "Rocky",
            photoUrl: "placeholder_dog",
            location: Location(city: "Olsztyn", radiusKm: 13),
            species: .dog,
            breed: "Rottweiler",
            gender: .male,
            status: .active,
            lastSeenDate: "06/11/2025",
            description: "Large rottweiler, friendly despite size.",
            email: "rocky.owner@example.com",
            phone: nil
        )
    ]
        // Return requested number of animals (cycle through if count > mockAnimals.count)
        return (0..<count).map { i in mockAnimals[i % mockAnimals.count] }
    }
    
    private func generateMockPetDetails(id: String) -> PetDetails? {
        switch id {
        case "11111111-1111-1111-1111-111111111111", "1":
            return PetDetails(
                id: id,
                petName: "Fredi Kamionka Gmina Burzenin",
                photoUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
                status: "ACTIVE",
                lastSeenDate: "2025-11-18",
                species: "DOG",
                gender: "MALE",
                description: "Zaginął piesek York wabi się Fredi Kamionka gmina burzenin",
                location: "Kamionka",
                phone: "+48 123 456 789",
                email: "spotterka@example.pl",
                breed: "York",
                locationRadius: 5,
                microchipNumber: "616-093-400-123",
                approximateAge: "3 years",
                reward: "500 PLN",
                vaccinationId: "VAC-2023-001234",
                createdAt: "2025-11-19T15:47:14.000Z",
                updatedAt: "2025-11-19T15:47:14.000Z"
            )
            
        case "22222222-2222-2222-2222-222222222222", "2":
            return PetDetails(
                id: id,
                petName: "Luna",
                photoUrl: "https://images.dog.ceo/breeds/saluki/n02091831_6640.jpg",
                status: "ACTIVE",
                lastSeenDate: "2025-11-20",
                species: "CAT",
                gender: "FEMALE",
                description: "Beautiful black cat with white paws, very friendly.",
                location: "Warsaw",
                phone: "+48 987 654 321",
                email: nil,
                breed: "Mixed",
                locationRadius: nil,
                microchipNumber: "616-093-400-456",
                approximateAge: "2 years",
                reward: nil,
                vaccinationId: "VAC-2023-004567",
                createdAt: "2025-11-20T10:30:00.000Z",
                updatedAt: "2025-11-20T10:30:00.000Z"
            )
            
        case "33333333-3333-3333-3333-333333333333", "3":
            return PetDetails(
                id: id,
                petName: "Piorun",
                photoUrl: nil,
                status: "ACTIVE",
                lastSeenDate: "2025-11-21",
                species: "BIRD",
                gender: "UNKNOWN",
                description: "Green parrot, escaped from cage, can say 'Hello' and 'Goodbye'.",
                location: "Krakow",
                phone: "+48 555 123 456",
                email: nil,
                breed: nil,
                locationRadius: nil,
                microchipNumber: nil,
                approximateAge: nil,
                reward: nil,
                vaccinationId: nil,
                createdAt: "2025-11-21T14:15:00.000Z",
                updatedAt: "2025-11-21T14:15:00.000Z"
            )
            
        case "44444444-4444-4444-4444-444444444444", "4":
            return PetDetails(
                id: id,
                petName: "Burek",
                photoUrl: "https://images.dog.ceo/breeds/shepherd-german/n02106662_10908.jpg",
                status: "FOUND",
                lastSeenDate: "2025-11-15",
                species: "DOG",
                gender: "MALE",
                description: "Large German Shepherd found near the park, very friendly.",
                location: "Poznan",
                phone: "+48 111 222 333",
                email: "finder@example.com",
                breed: "German Shepherd",
                locationRadius: 10,
                microchipNumber: "616-093-400-789",
                approximateAge: "5 years",
                reward: "200 PLN",
                vaccinationId: "VAC-2023-007890",
                createdAt: "2025-11-15T08:00:00.000Z",
                updatedAt: "2025-11-15T08:00:00.000Z"
            )
            
        default:
            return nil
        }
    }
}
