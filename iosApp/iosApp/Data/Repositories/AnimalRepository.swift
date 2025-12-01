import Foundation

// MARK: - HTTP Repository Implementation

/// HTTP-based implementation of AnimalRepositoryProtocol
/// Consumes backend REST API endpoints:
/// - GET /api/v1/announcements (with optional lat/lng query params)
/// - GET /api/v1/announcements/:id
class AnimalRepository: AnimalRepositoryProtocol {
    private let urlSession: URLSession
    
    init(urlSession: URLSession = .shared) {
        self.urlSession = urlSession
    }
    
    // MARK: - AnimalRepositoryProtocol Implementation
    
    func getAnimals(near location: Coordinate?) async throws -> [Animal] {
        var urlComponents = URLComponents(string: "\(APIConfig.fullBaseURL)/announcements")!
        
        // Add location query parameters if provided
        if let location = location {
            urlComponents.queryItems = [
                URLQueryItem(name: "lat", value: String(location.latitude)),
                URLQueryItem(name: "lng", value: String(location.longitude))
            ]
        }
        
        guard let url = urlComponents.url else {
            throw RepositoryError.invalidURL
        }
        
        do {
            let (data, response) = try await urlSession.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 200 else {
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            let listResponse = try JSONDecoder.apiDecoder.decode(
                AnnouncementsListResponse.self,
                from: data
            )
            
            // Convert DTOs to domain models, skipping invalid items
            let animals = listResponse.data.compactMap { dto -> Animal? in
                return Animal(fromDTO: dto)
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
    
    func getPetDetails(id: String) async throws -> PetDetails {
        guard let url = URL(string: "\(APIConfig.fullBaseURL)/announcements/\(id)") else {
            throw RepositoryError.invalidURL
        }
        
        do {
            let (data, response) = try await urlSession.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse else {
                throw RepositoryError.invalidResponse
            }
            
            guard httpResponse.statusCode == 200 else {
                if httpResponse.statusCode == 404 {
                    throw RepositoryError.notFound
                }
                throw RepositoryError.httpError(statusCode: httpResponse.statusCode)
            }
            
            let dto = try JSONDecoder.apiDecoder.decode(PetDetailsDTO.self, from: data)
            
            // Convert DTO to domain model, throw error if invalid
            guard let details = PetDetails(fromDTO: dto) else {
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

