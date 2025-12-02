import Foundation

/// Repository protocol for animal data operations.
/// Defines contract for fetching animal data from various sources.
///
/// Platform-specific implementations:
/// - Mock implementation: Returns hardcoded test data (current phase)
/// - Real implementation: Fetches from REST API (future phase)
///
/// All operations are async functions using Swift Concurrency.
/// Throws errors on failure for natural error handling.
protocol AnimalRepositoryProtocol {
    /// Retrieves all animals from the data source with optional location-based filtering.
    /// Mock implementation returns fixed list of animals (location parameter ignored).
    /// Real implementation will support pagination and filtering by proximity when location provided.
    ///
    /// - Parameter location: Optional user location for proximity filtering (nil = no filtering)
    /// - Parameter range: Search radius in kilometers
    /// - Returns: Array of animals (may be empty if no animals found)
    /// - Throws: Error if data fetch fails
    func getAnimals(near location: Coordinate?, range: Int) async throws -> [Animal]
    
    /// Retrieves detailed information for a specific pet by ID.
    /// Mock implementation returns hardcoded pet details.
    /// Real implementation will call GET /api/v1/announcements/:id endpoint.
    ///
    /// - Parameter id: Unique pet identifier
    /// - Returns: Pet details
    /// - Throws: Error if pet not found or data fetch fails
    func getPetDetails(id: String) async throws -> PetDetails
}

// MARK: - Protocol Extension (Default Values)

extension AnimalRepositoryProtocol {
    /// Convenience method with default range value (100km).
    /// Calls full method with default range parameter.
    func getAnimals(near location: Coordinate?) async throws -> [Animal] {
        return try await getAnimals(near: location, range: 100)
    }
}

