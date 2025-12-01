import Foundation

// MARK: - RepositoryError

/// Errors that can occur during repository operations
enum RepositoryError: Error, LocalizedError {
    case invalidURL
    case invalidResponse
    case httpError(statusCode: Int)
    case networkError(Error)
    case decodingFailed(Error)
    case notFound
    case invalidData
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL configuration"
        case .invalidResponse:
            return "Invalid server response"
        case .httpError(let statusCode):
            return "Server error: \(statusCode)"
        case .networkError:
            return "Network connection failed"
        case .decodingFailed:
            return "Failed to parse server response"
        case .notFound:
            return "Announcement not found"
        case .invalidData:
            return "Invalid data received from server"
        }
    }
}

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
    
    func getAnimals(near location: UserLocation?) async throws -> [Animal] {
        var urlComponents = URLComponents(string: "\(APIConfig.fullBaseURL)/announcements")!
        
        // Add optional location query parameters
        if let userLocation = location {
            urlComponents.queryItems = [
                URLQueryItem(name: "lat", value: String(userLocation.latitude)),
                URLQueryItem(name: "lng", value: String(userLocation.longitude))
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
                return Animal(from: dto)
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
            guard let details = PetDetails(from: dto) else {
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

// MARK: - JSONDecoder Extension

extension JSONDecoder {
    /// Decoder configured for PetSpot API date formats
    static var apiDecoder: JSONDecoder {
        let decoder = JSONDecoder()
        // Date decoding is handled in domain model extensions
        // DTOs receive date strings and convert them in failable initializers
        return decoder
    }
}

// MARK: - DTOs (Private)

/// Response wrapper for announcements list endpoint
private struct AnnouncementsListResponse: Codable {
    let data: [AnnouncementDTO]
}

/// DTO for single announcement from list endpoint
private struct AnnouncementDTO: Codable {
    let id: String
    let petName: String
    let species: String
    let status: String
    let photoUrl: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let breed: String?
    let sex: String?
    let age: Int?
    let description: String
    let phone: String
    let email: String?
}

/// DTO for pet details from details endpoint
private struct PetDetailsDTO: Codable {
    let id: String
    let petName: String
    let species: String
    let status: String
    let photoUrl: String
    let lastSeenDate: String
    let locationLatitude: Double
    let locationLongitude: Double
    let breed: String?
    let sex: String?
    let age: Int?
    let microchipNumber: String?
    let email: String?
    let phone: String
    let reward: String?
    let description: String
    let createdAt: String
    let updatedAt: String
}

// MARK: - Domain Model Extensions

extension Animal {
    /// Failable initializer - returns nil if DTO contains invalid data
    /// Allows graceful handling of invalid items in list (skip instead of crash)
    fileprivate init?(from dto: AnnouncementDTO) {
        // Parse species
        guard let species = AnimalSpecies(fromString: dto.species) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id), skipping item")
            return nil
        }
        
        // Parse status - map MISSING to ACTIVE
        let statusString = dto.status.uppercased() == "MISSING" ? "ACTIVE" : dto.status.uppercased()
        guard let status = AnimalStatus(rawValue: statusString) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id), skipping item")
            return nil
        }
        
        // Parse gender (if sex is missing, default to unknown)
        let gender = dto.sex.flatMap({ AnimalGender(fromString: $0) }) ?? .unknown
        
        self.init(
            id: dto.id,
            name: dto.petName,
            photoUrl: dto.photoUrl,
            coordinate: Coordinate(latitude: dto.locationLatitude, longitude: dto.locationLongitude),
            species: species,
            breed: dto.breed ?? "Unknown",
            gender: gender,
            status: status,
            lastSeenDate: dto.lastSeenDate,
            description: dto.description,
            email: dto.email,
            phone: dto.phone
        )
    }
}

extension PetDetails {
    /// Failable initializer - returns nil if DTO contains invalid data
    fileprivate init?(from dto: PetDetailsDTO) {
        // Parse species
        guard let species = AnimalSpecies(fromString: dto.species) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id)")
            return nil
        }
        
        // Parse status - map MISSING to ACTIVE
        let statusString = dto.status.uppercased() == "MISSING" ? "ACTIVE" : dto.status.uppercased()
        guard let status = AnimalStatus(rawValue: statusString) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id)")
            return nil
        }
        
        // Parse gender (if sex is missing, default to unknown)
        let gender = dto.sex.flatMap({ AnimalGender(fromString: $0) }) ?? .unknown
        
        // Parse age
        let approximateAge = dto.age.map { "\($0) years" }
        
        self.init(
            id: dto.id,
            petName: dto.petName,
            photoUrl: dto.photoUrl,
            status: status,
            lastSeenDate: dto.lastSeenDate,
            species: species,
            gender: gender,
            description: dto.description,
            phone: dto.phone,
            email: dto.email,
            breed: dto.breed,
            latitude: dto.locationLatitude,
            longitude: dto.locationLongitude,
            microchipNumber: dto.microchipNumber,
            approximateAge: approximateAge,
            reward: dto.reward,
            createdAt: dto.createdAt,
            updatedAt: dto.updatedAt
        )
    }
}
