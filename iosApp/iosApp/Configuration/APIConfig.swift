import Foundation

/// API configuration for PetSpot backend
enum APIConfig {
    /// Base URL for backend API server
    /// Development: Local server on localhost:3000
    /// Production: Update this constant for production environment
    static let baseURL = "http://localhost:3000"
    
    /// API version prefix
    static let apiVersion = "/api/v1"
    
    /// Full base URL with API version
    static var fullBaseURL: String {
        return baseURL + apiVersion
    }
}

