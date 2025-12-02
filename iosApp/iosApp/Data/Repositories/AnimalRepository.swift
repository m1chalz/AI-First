import Foundation

// MARK: - HTTP Repository Implementation

/// HTTP-based implementation of AnimalRepositoryProtocol
/// Consumes backend REST API endpoints:
/// - GET /api/v1/announcements (with optional lat/lng query params)
/// - GET /api/v1/announcements/:id
class AnimalRepository: AnimalRepositoryProtocol {
    private let urlSession: URLSession
    private let apiDecoder: APIDecoder
    private let animalMapper: AnimalMapper
    private let petDetailsMapper: PetDetailsMapper
    
    init(
        urlSession: URLSession = .shared,
        apiDecoder: APIDecoder = APIDecoder(),
        animalMapper: AnimalMapper = AnimalMapper(),
        petDetailsMapper: PetDetailsMapper = PetDetailsMapper()
    ) {
        self.urlSession = urlSession
        self.apiDecoder = apiDecoder
        self.animalMapper = animalMapper
        self.petDetailsMapper = petDetailsMapper
    }
    
    // MARK: - AnimalRepositoryProtocol Implementation
    
    /// Fetches animal announcements from backend API with optional location filtering.
    /// Deduplicates results and skips invalid items gracefully.
    /// - Parameter location: Optional coordinate for location-based filtering (nil = all announcements)
    /// - Returns: Array of valid Animal models
    /// - Throws: RepositoryError if network or parsing fails
    func getAnimals(near location: Coordinate?) async throws -> [Animal] {
        var urlComponents = URLComponents(string: "\(APIConfig.fullBaseURL)/announcements")!
        
        // Add location query parameters if provided
        if let location = location {
            urlComponents.queryItems = [
                URLQueryItem(name: "lat", value: String(location.latitude)),
                URLQueryItem(name: "lng", value: String(location.longitude)),
                URLQueryItem(name: "range", value: "10")
            ]
        }
        
        guard let url = urlComponents.url else {
            throw RepositoryError.invalidURL
        }
        
        do {
            let (data, response) = try await urlSession.data(from: url)
            
            // User Story 3 (T067): Check for cancellation after network call
            // URLSession automatically cancels requests, but checking here saves CPU time on decoding
            try Task.checkCancellation()
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 200 else {
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            let listResponse = try apiDecoder.decode(
                AnnouncementsListResponse.self,
                from: data
            )
            
            // User Story 3 (T067): Check for cancellation before expensive mapping operation
            try Task.checkCancellation()
            
            // Convert DTOs to domain models, skipping invalid items
            let animals = listResponse.data.compactMap { dto -> Animal? in
                return animalMapper.map(dto)
            }
            
            // Deduplicate by ID (keep first occurrence)
            let uniqueAnimals = Dictionary(
                animals.map { ($0.id, $0) },
                uniquingKeysWith: { first, _ in
                    print("Warning: Duplicate announcement ID: \(first.id)")
                    return first
                }
            ).values
            
            return Array(uniqueAnimals)
            
        } catch let error as DecodingError {
            print("JSON decoding error: \(error)")
            throw RepositoryError.decodingFailed(error)
        } catch let error as RepositoryError {
            throw error
        } catch {
            print("Network error: \(error)")
            throw RepositoryError.networkError(error)
        }
    }
    
    /// Fetches detailed pet information from backend API by ID.
    /// - Parameter id: Unique identifier of the pet announcement
    /// - Returns: Complete PetDetails model with all fields
    /// - Throws: RepositoryError.notFound if pet doesn't exist, or other RepositoryError types
    func getPetDetails(id: String) async throws -> PetDetails {
        guard let url = URL(string: "\(APIConfig.fullBaseURL)/announcements/\(id)") else {
            throw RepositoryError.invalidURL
        }
        
        do {
            let (data, response) = try await urlSession.data(from: url)
            
            // User Story 3 (T067): Check for cancellation after network call
            try Task.checkCancellation()
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 200 else {
                if httpResponse.statusCode == 404 {
                    throw RepositoryError.notFound
                }
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            let dto = try apiDecoder.decode(PetDetailsDTO.self, from: data)
            
            // User Story 3 (T067): Check for cancellation before mapping
            try Task.checkCancellation()
            
            // Convert DTO to domain model, throw error if invalid
            guard let details = petDetailsMapper.map(dto) else {
                print("Error: Failed to convert DTO to PetDetails for id: \(id)")
                throw RepositoryError.invalidData
            }
            return details
            
        } catch let error as DecodingError {
            print("JSON decoding error: \(error)")
            throw RepositoryError.decodingFailed(error)
        } catch let error as RepositoryError {
            throw error
        } catch {
            print("Network error: \(error)")
            throw RepositoryError.networkError(error)
        }
    }
}

