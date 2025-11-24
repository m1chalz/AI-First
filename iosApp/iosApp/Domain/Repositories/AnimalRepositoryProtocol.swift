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
}

