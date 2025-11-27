import Foundation

/// Extension to AnimalRepositoryProtocol for location-aware animal queries.
///
/// Adds support for optional location-based filtering when fetching animals.
/// Backend API already supports lat/lon query parameters (optional).
protocol AnimalRepositoryProtocol {
    /// Fetches animals from server with optional location-based filtering.
    ///
    /// When location is provided, server returns animals sorted by proximity.
    /// When location is nil, server returns all animals (no filtering).
    ///
    /// - Parameter location: Optional user location for proximity filtering
    /// - Returns: Array of Animal entities (may be empty if no animals found)
    /// - Throws: NetworkError or parsing errors from repository implementation
    ///
    /// - Note: Existing backend API endpoint: `GET /api/pets?lat=X&lon=Y`
    ///   (lat/lon parameters are optional, server handles missing parameters gracefully)
    func fetchAnimals(near location: UserLocation?) async throws -> [Animal]
}

// MARK: - Implementation Notes

/*
 Implementation example for existing AnimalRepository:
 
 class AnimalRepository: AnimalRepositoryProtocol {
     func fetchAnimals(near location: UserLocation?) async throws -> [Animal] {
         var queryItems: [URLQueryItem] = []
         
         if let location = location {
             queryItems.append(URLQueryItem(name: "lat", value: "\(location.latitude)"))
             queryItems.append(URLQueryItem(name: "lon", value: "\(location.longitude)"))
         }
         
         var urlComponents = URLComponents(string: "\(baseURL)/api/pets")
         urlComponents?.queryItems = queryItems.isEmpty ? nil : queryItems
         
         guard let url = urlComponents?.url else {
             throw NetworkError.invalidURL
         }
         
         // Existing HTTP client fetch logic...
     }
 }
 */

