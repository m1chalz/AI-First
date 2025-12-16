import Foundation

/// API configuration for PetSpot backend
enum APIConfig {
    /// Base URL for backend API server
    /// Development: Use 127.0.0.1 for iOS simulator (more reliable than localhost)
    /// Production: Update this constant for production environment
    static let baseURL = "http://127.0.0.1:3000"
    
    /// API version prefix
    static let apiVersion = "/api/v1"
    
    /// Full base URL with API version
    static var fullBaseURL: String {
        return baseURL + apiVersion
    }
}

