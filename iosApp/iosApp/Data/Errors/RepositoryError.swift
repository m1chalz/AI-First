import Foundation

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

