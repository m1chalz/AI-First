import Foundation
@testable import PetSpot

/// Fake implementation of AnnouncementRepositoryProtocol for unit testing.
/// Supports testing of both announcement list loading and announcement creation.
///
/// Allows controlling success/failure scenarios:
/// - getAnnouncements: Returns mock announcements or throws error
/// - getPetDetails: Returns mock pet details or throws error
/// - createAnnouncement: Tracks calls and returns mock result
/// - uploadPhoto: Tracks calls and throws on demand
class FakeAnnouncementRepository: AnnouncementRepositoryProtocol {
    // MARK: - getAnnouncements Configuration
    
    let animalCount: Int
    var shouldFail: Bool
    let error: Error
    
    /// Tracks how many times getAnnouncements was called (for test assertions)
    private(set) var getAnnouncementsCallCount = 0
    
    /// For location testing - tracks location parameter passed
    var lastLocationParameter: Coordinate?
    
    /// For location testing - stubbed announcements to return
    var stubbedAnnouncements: [Announcement] = []
    
    /// For testing async cancellation - simulates slow network
    var delayDuration: TimeInterval = 0
    
    // MARK: - getPetDetails Configuration
    
    /// Tracks how many times getPetDetails was called
    private(set) var getPetDetailsCallCount = 0
    
    /// Mock pet details to return
    var mockPetDetails: PetDetails?
    
    // MARK: - createAnnouncement Configuration
    
    var shouldThrowOnCreateAnnouncement = false
    var createAnnouncementThrowsError: Error?
    
    var mockAnnouncementResult = AnnouncementResult(
        id: "test-announcement-id",
        managementPassword: "123456"
    )
    
    // MARK: - createAnnouncement Call Tracking
    
    var createAnnouncementCalled = false
    var lastCreateAnnouncementData: CreateAnnouncementData?
    
    // MARK: - uploadPhoto Configuration
    
    var shouldThrowOnUploadPhoto = false
    var uploadPhotoThrowsError: Error?
    
    // MARK: - uploadPhoto Call Tracking
    
    var uploadPhotoCalled = false
    var lastUploadPhotoAnnouncementId: String?
    var lastUploadPhotoMetadata: PhotoAttachmentMetadata?
    var lastUploadPhotoPassword: String?
    
    // MARK: - Initialization
    
    /// Creates a fake repository with configurable behavior.
    ///
    /// - Parameters:
    ///   - animalCount: Number of mock announcements to return (default: 16)
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
    
    // MARK: - AnnouncementRepositoryProtocol Implementation
    
    func getAnnouncements(near location: Coordinate?, range: Int) async throws -> [Announcement] {
        getAnnouncementsCallCount += 1
        lastLocationParameter = location
        
        // Simulate network delay if configured (for testing task cancellation)
        if delayDuration > 0 {
            try await Task.sleep(for: .seconds(delayDuration))
        }
        
        if shouldFail {
            throw error
        }
        
        if !stubbedAnnouncements.isEmpty {
            return stubbedAnnouncements
        }
        
        return generateMockAnnouncements(count: animalCount)
    }
    
    func getPetDetails(id: String) async throws -> PetDetails {
        getPetDetailsCallCount += 1
        
        if shouldFail {
            throw error
        }
        
        if let mockPetDetails = mockPetDetails {
            return mockPetDetails
        }
        
        guard let petDetails = generateMockPetDetails(id: id) else {
            throw NSError(
                domain: "FakeAnnouncementRepository",
                code: 404,
                userInfo: [NSLocalizedDescriptionKey: "Pet not found"]
            )
        }
        
        return petDetails
    }
    
    func createAnnouncement(data: CreateAnnouncementData) async throws -> AnnouncementResult {
        createAnnouncementCalled = true
        lastCreateAnnouncementData = data
        
        if shouldThrowOnCreateAnnouncement {
            throw createAnnouncementThrowsError ?? RepositoryError.networkError(NSError(domain: "Test", code: -1))
        }
        
        return mockAnnouncementResult
    }
    
    func uploadPhoto(announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String) async throws {
        uploadPhotoCalled = true
        lastUploadPhotoAnnouncementId = announcementId
        lastUploadPhotoMetadata = photo
        lastUploadPhotoPassword = managementPassword
        
        if shouldThrowOnUploadPhoto {
            throw uploadPhotoThrowsError ?? RepositoryError.networkError(NSError(domain: "Test", code: -1))
        }
    }
    
    // MARK: - Mock Data Generation
    
    /// Generates mock announcement data for testing.
    /// Matches structure of production mock data for consistency.
    private func generateMockAnnouncements(count: Int) -> [Announcement] {
        guard count > 0 else { return [] }
        
        let mockAnnouncements: [Announcement] = [
            Announcement(
                id: "1",
                name: "Fluffy",
                photoUrl: "placeholder_cat",
                coordinate: Coordinate(latitude: 52.1764, longitude: 20.8132),
                species: .cat,
                breed: "Maine Coon",
                gender: .male,
                status: .active,
                lastSeenDate: "18/11/2025",
                description: "Friendly orange tabby cat, last seen near the park.",
                email: "john.doe@example.com",
                phone: "+48 123 456 789"
            ),
            Announcement(
                id: "2",
                name: "Rex",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
                species: .dog,
                breed: "German Shepherd",
                gender: .female,
                status: .active,
                lastSeenDate: "17/11/2025",
                description: "Large black and tan dog, wearing red collar.",
                email: "anna.smith@example.com",
                phone: nil
            ),
            Announcement(
                id: "3",
                name: "Bella",
                photoUrl: "placeholder_cat",
                coordinate: Coordinate(latitude: 50.0647, longitude: 19.9450),
                species: .cat,
                breed: "Siamese",
                gender: .female,
                status: .found,
                lastSeenDate: "19/11/2025",
                description: "Blue-eyed white cat found near train station.",
                email: nil,
                phone: "+48 987 654 321"
            ),
            Announcement(
                id: "4",
                name: "Buddy",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 51.1079, longitude: 17.0385),
                species: .dog,
                breed: "Labrador Retriever",
                gender: .male,
                status: .active,
                lastSeenDate: "16/11/2025",
                description: "Yellow lab, very friendly, responds to 'Buddy'.",
                email: "mike@example.com",
                phone: "+48 111 222 333"
            ),
            Announcement(
                id: "5",
                name: "Tweety",
                photoUrl: "placeholder_bird",
                coordinate: Coordinate(latitude: 54.3520, longitude: 18.6466),
                species: .bird,
                breed: "Cockatiel",
                gender: .unknown,
                status: .active,
                lastSeenDate: "15/11/2025",
                description: "Gray and yellow bird, escaped from balcony.",
                email: "sarah@example.com",
                phone: nil
            ),
            Announcement(
                id: "6",
                name: "Snowball",
                photoUrl: "placeholder_cat",
                coordinate: Coordinate(latitude: 52.4064, longitude: 16.9252),
                species: .cat,
                breed: "Persian",
                gender: .female,
                status: .active,
                lastSeenDate: "14/11/2025",
                description: "White long-haired cat, very shy.",
                email: nil,
                phone: nil
            ),
            Announcement(
                id: "7",
                name: "Snoopy",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 51.7592, longitude: 19.4560),
                species: .dog,
                breed: "Beagle",
                gender: .male,
                status: .found,
                lastSeenDate: "20/11/2025",
                description: "Tri-color beagle found wandering near shopping center.",
                email: "finder@example.com",
                phone: "+48 555 666 777"
            ),
            Announcement(
                id: "8",
                name: "Thumper",
                photoUrl: "placeholder_rabbit",
                coordinate: Coordinate(latitude: 50.2649, longitude: 19.0238),
                species: .rabbit,
                breed: "Dwarf Rabbit",
                gender: .female,
                status: .closed,
                lastSeenDate: "13/11/2025",
                description: "Small gray rabbit, reunited with owner.",
                email: "owner@example.com",
                phone: "+48 444 333 222"
            ),
            Announcement(
                id: "9",
                name: "Shadow",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 53.4285, longitude: 14.5528),
                species: .dog,
                breed: "Husky",
                gender: .male,
                status: .active,
                lastSeenDate: "12/11/2025",
                description: "Blue-eyed Siberian Husky, very energetic.",
                email: nil,
                phone: "+48 888 999 000"
            ),
            Announcement(
                id: "10",
                name: "Whiskers",
                photoUrl: "placeholder_cat",
                coordinate: Coordinate(latitude: 53.1325, longitude: 23.1688),
                species: .cat,
                breed: "British Shorthair",
                gender: .male,
                status: .active,
                lastSeenDate: "11/11/2025",
                description: "Gray tabby cat with green eyes.",
                email: "cat.owner@example.com",
                phone: nil
            ),
            Announcement(
                id: "11",
                name: "Luna",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 51.2465, longitude: 22.5684),
                species: .dog,
                breed: "Golden Retriever",
                gender: .female,
                status: .active,
                lastSeenDate: "10/11/2025",
                description: "Golden retriever puppy, very playful.",
                email: "luna.owner@example.com",
                phone: "+48 222 333 444"
            ),
            Announcement(
                id: "12",
                name: "Charlie",
                photoUrl: "placeholder_cat",
                coordinate: Coordinate(latitude: 50.0412, longitude: 21.9991),
                species: .cat,
                breed: "Ragdoll",
                gender: .male,
                status: .found,
                lastSeenDate: "21/11/2025",
                description: "Blue-eyed ragdoll cat, found in garage.",
                email: "finder123@example.com",
                phone: nil
            ),
            Announcement(
                id: "13",
                name: "Max",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 53.0138, longitude: 18.5984),
                species: .dog,
                breed: "Dachshund",
                gender: .male,
                status: .active,
                lastSeenDate: "09/11/2025",
                description: "Small brown dachshund, wears blue collar.",
                email: "max.family@example.com",
                phone: "+48 333 444 555"
            ),
            Announcement(
                id: "14",
                name: "Milo",
                photoUrl: "placeholder_bird",
                coordinate: Coordinate(latitude: 50.2945, longitude: 18.6714),
                species: .bird,
                breed: "Parrot",
                gender: .unknown,
                status: .active,
                lastSeenDate: "08/11/2025",
                description: "Green parrot, can say 'Hello'.",
                email: nil,
                phone: "+48 666 777 888"
            ),
            Announcement(
                id: "15",
                name: "Daisy",
                photoUrl: "placeholder_cat",
                coordinate: Coordinate(latitude: 53.1235, longitude: 18.0084),
                species: .cat,
                breed: "Bengal",
                gender: .female,
                status: .active,
                lastSeenDate: "07/11/2025",
                description: "Spotted bengal cat, very active.",
                email: "daisy.home@example.com",
                phone: "+48 777 888 999"
            ),
            Announcement(
                id: "16",
                name: "Rocky",
                photoUrl: "placeholder_dog",
                coordinate: Coordinate(latitude: 53.7784, longitude: 20.4801),
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
        
        // Return requested number of announcements (cycle through if count > mockAnnouncements.count)
        return (0..<count).map { i in mockAnnouncements[i % mockAnnouncements.count] }
    }
    
    private func generateMockPetDetails(id: String) -> PetDetails? {
        switch id {
        case "11111111-1111-1111-1111-111111111111", "1", "preview-id":
            return PetDetails(
                id: id,
                petName: "Fredi Kamionka Gmina Burzenin",
                photoUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
                status: .active,
                lastSeenDate: "2025-11-18",
                species: .dog,
                gender: .male,
                description: "Zaginął piesek York wabi się Fredi Kamionka gmina burzenin",
                phone: "+48 123 456 789",
                email: "spotterka@example.pl",
                breed: "York",
                latitude: 51.5000,
                longitude: 18.5000,
                microchipNumber: "616-093-400-123",
                approximateAge: 3,
                reward: "500 PLN",
                createdAt: "2025-11-19T15:47:14.000Z",
                updatedAt: "2025-11-19T15:47:14.000Z"
            )
            
        case "22222222-2222-2222-2222-222222222222", "2":
            return PetDetails(
                id: id,
                petName: "Luna",
                photoUrl: "https://images.dog.ceo/breeds/saluki/n02091831_6640.jpg",
                status: .active,
                lastSeenDate: "2025-11-20",
                species: .cat,
                gender: .female,
                description: "Beautiful black cat with white paws, very friendly.",
                phone: "+48 987 654 321",
                email: nil,
                breed: "Mixed",
                latitude: 52.2297,
                longitude: 21.0122,
                microchipNumber: "616-093-400-456",
                approximateAge: 2,
                reward: nil,
                createdAt: "2025-11-20T10:30:00.000Z",
                updatedAt: "2025-11-20T10:30:00.000Z"
            )
            
        case "33333333-3333-3333-3333-333333333333", "3":
            return PetDetails(
                id: id,
                petName: "Piorun",
                photoUrl: nil,
                status: .active,
                lastSeenDate: "2025-11-21",
                species: .bird,
                gender: .unknown,
                description: "Green parrot, escaped from cage, can say 'Hello' and 'Goodbye'.",
                phone: "+48 555 123 456",
                email: nil,
                breed: nil,
                latitude: 50.0647,
                longitude: 19.9450,
                microchipNumber: nil,
                approximateAge: nil,
                reward: nil,
                createdAt: "2025-11-21T14:15:00.000Z",
                updatedAt: "2025-11-21T14:15:00.000Z"
            )
            
        case "44444444-4444-4444-4444-444444444444", "4":
            return PetDetails(
                id: id,
                petName: "Burek",
                photoUrl: "https://images.dog.ceo/breeds/shepherd-german/n02106662_10908.jpg",
                status: .found,
                lastSeenDate: "2025-11-15",
                species: .dog,
                gender: .male,
                description: "Large German Shepherd found near the park, very friendly.",
                phone: "+48 111 222 333",
                email: "finder@example.com",
                breed: "German Shepherd",
                latitude: 52.4064,
                longitude: 16.9252,
                microchipNumber: "616-093-400-789",
                approximateAge: 5,
                reward: "200 PLN",
                createdAt: "2025-11-15T08:00:00.000Z",
                updatedAt: "2025-11-15T08:00:00.000Z"
            )
            
        default:
            return nil
        }
    }
}

