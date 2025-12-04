import Foundation

/// Mapper for converting relative photo URLs to absolute URLs
struct PhotoURLMapper {
    private let baseURL: String
    
    init(baseURL: String = APIConfig.baseURL) {
        self.baseURL = baseURL
    }
    
    /// Converts photo URL to absolute URL
    /// - Parameter photoUrl: Photo URL from backend (may be relative or absolute)
    /// - Returns: Absolute URL string
    func resolve(_ photoUrl: String) -> String {
        // If URL starts with /, it's relative - prepend base URL
        if photoUrl.starts(with: "/") {
            return baseURL + photoUrl
        }
        // Otherwise it's already absolute
        return photoUrl
    }
}

