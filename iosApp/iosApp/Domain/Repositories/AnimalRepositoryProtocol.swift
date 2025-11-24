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
    /// Retrieves all animals from the data source.
    /// Mock implementation returns fixed list of animals.
    /// Real implementation will support pagination and filtering.
    ///
    /// - Returns: Array of animals
    /// - Throws: Error if data fetch fails
    func getAnimals() async throws -> [Animal]
    
    /// Retrieves detailed information for a specific pet by ID.
    /// Mock implementation returns hardcoded pet details.
    /// Real implementation will call GET /api/v1/announcements/:id endpoint.
    ///
    /// - Parameter id: Unique pet identifier
    /// - Returns: Pet details
    /// - Throws: Error if pet not found or data fetch fails
    func getPetDetails(id: String) async throws -> PetDetails
}

