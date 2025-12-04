import Foundation

/// Wrapper for JSON decoding with API-specific configuration.
/// Provides centralized decoder configuration and allows for future extensions
/// like custom error handling, logging, or retry logic.
struct APIDecoder {
    private let decoder: JSONDecoder
    
    init() {
        let decoder = JSONDecoder()
        // Future: Add custom configuration here
        // decoder.dateDecodingStrategy = ...
        // decoder.keyDecodingStrategy = ...
        self.decoder = decoder
    }
    
    /// Decodes a value of the given type from JSON data.
    /// - Parameters:
    ///   - type: The type to decode
    ///   - data: The JSON data to decode
    /// - Returns: Decoded value of type T
    /// - Throws: DecodingError if decoding fails
    func decode<T: Decodable>(_ type: T.Type, from data: Data) throws -> T {
        // Future: Add logging, error mapping, or monitoring here
        return try decoder.decode(type, from: data)
    }
}

