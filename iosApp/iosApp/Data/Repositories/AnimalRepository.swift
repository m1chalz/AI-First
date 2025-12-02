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
    private let announcementMapper: AnnouncementMapper
    
    init(
        urlSession: URLSession = .shared,
        apiDecoder: APIDecoder = APIDecoder(),
        animalMapper: AnimalMapper = AnimalMapper(),
        petDetailsMapper: PetDetailsMapper = PetDetailsMapper(),
        announcementMapper: AnnouncementMapper = AnnouncementMapper()
    ) {
        self.urlSession = urlSession
        self.apiDecoder = apiDecoder
        self.animalMapper = animalMapper
        self.petDetailsMapper = petDetailsMapper
        self.announcementMapper = announcementMapper
    }
    
    // MARK: - AnimalRepositoryProtocol Implementation
    
    /// Fetches animal announcements from backend API with optional location filtering.
    /// Deduplicates results and skips invalid items gracefully.
    /// - Parameter location: Optional coordinate for location-based filtering (nil = all announcements)
    /// - Returns: Array of valid Animal models
    /// - Throws: RepositoryError if network or parsing fails
    func getAnimals(near location: Coordinate?, range: Int = 100) async throws -> [Animal] {
        var urlComponents = URLComponents(string: "\(APIConfig.fullBaseURL)/announcements")!
        
        // Add location query parameters if provided
        if let location = location {
            urlComponents.queryItems = [
                URLQueryItem(name: "lat", value: String(location.latitude)),
                URLQueryItem(name: "lng", value: String(location.longitude)),
                URLQueryItem(name: "range", value: String(range))
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
    
    /// Creates a new missing pet announcement via POST /api/v1/announcements.
    /// Converts domain model to DTO, sends JSON request, parses response, converts back to domain.
    /// - Parameter data: Domain model with announcement details
    /// - Returns: AnnouncementResult with id and managementPassword
    /// - Throws: RepositoryError on network failure or backend error
    func createAnnouncement(data: CreateAnnouncementData) async throws -> AnnouncementResult {
        guard let url = URL(string: "\(APIConfig.fullBaseURL)/announcements") else {
            throw RepositoryError.invalidURL
        }
        
        // Convert domain model to DTO
        let requestDTO = announcementMapper.toDTO(data)
        
        // Encode DTO to JSON
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        guard let body = try? encoder.encode(requestDTO) else {
            throw RepositoryError.encodingFailed
        }
        
        // Build HTTP request
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        urlRequest.httpBody = body
        
        do {
            let (responseData, response) = try await urlSession.data(for: urlRequest)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 201 else {
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            // Decode response DTO
            let decoder = JSONDecoder()
            decoder.keyDecodingStrategy = .convertFromSnakeCase
            let responseDTO = try decoder.decode(AnnouncementResponseDTO.self, from: responseData)
            
            // Convert DTO to domain model
            return announcementMapper.toDomain(responseDTO)
            
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
    
    /// Uploads photo for existing announcement via POST /api/v1/announcements/:id/photos.
    /// Loads photo data from disk cache, builds multipart form-data, sends with Basic auth.
    /// - Parameter announcementId: Announcement identifier from createAnnouncement
    /// - Parameter photo: Photo metadata with cachedURL for file loading
    /// - Parameter managementPassword: Password for Basic auth
    /// - Throws: RepositoryError on file I/O failure, network failure, or auth failure
    func uploadPhoto(announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String) async throws {
        guard let url = URL(string: "\(APIConfig.fullBaseURL)/announcements/\(announcementId)/photos") else {
            throw RepositoryError.invalidURL
        }
        
        // Load photo data from disk cache
        guard let photoData = try? Data(contentsOf: photo.cachedURL) else {
            print("Error: Failed to load photo from \(photo.cachedURL)")
            throw RepositoryError.fileIOError
        }
        
        // Build multipart form-data body
        let boundary = "Boundary-\(UUID().uuidString)"
        var body = Data()
        
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"photo\"; filename=\"pet.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(photo.mimeType)\r\n\r\n".data(using: .utf8)!)
        body.append(photoData)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        
        // Build HTTP request with Basic auth
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        let credentials = "\(announcementId):\(managementPassword)"
        let base64Credentials = credentials.data(using: .utf8)!.base64EncodedString()
        urlRequest.setValue("Basic \(base64Credentials)", forHTTPHeaderField: "Authorization")
        
        urlRequest.httpBody = body
        
        do {
            let (_, response) = try await urlSession.data(for: urlRequest)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 201 else {
                if httpResponse.statusCode == 401 {
                    throw RepositoryError.unauthorized
                }
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            // Success - no response body needed
            
        } catch let error as RepositoryError {
            throw error
        } catch {
            print("Network error: \(error)")
            throw RepositoryError.networkError(error)
        }
    }
}

