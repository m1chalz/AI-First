import Foundation

/// Configuration for querying announcement lists with filtering and sorting options.
/// Used by autonomous list component to determine query behavior.
///
/// Immutable value type - create new instance for different configurations.
struct AnnouncementListQuery: Equatable {
    /// Maximum number of results (nil = all items)
    let limit: Int?
    
    /// Sorting order for results
    let sortBy: SortOption
    
    /// User's current location for enabling location coordinate display (nil = no location)
    let location: Coordinate?
    
    /// Sorting options for announcement list
    enum SortOption: Equatable {
        /// Newest announcements first (default)
        case createdAtDescending
        /// Oldest announcements first
        case createdAtAscending
        /// Closest announcements first (requires location)
        case distanceFromUser
    }
    
    // MARK: - Convenience Factory Methods
    
    /// Creates default query: all announcements sorted by creation date (newest first)
    /// - Parameter location: Optional user location for displaying coordinates
    /// - Returns: Query configuration for full announcement list
    static func defaultQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(limit: nil, sortBy: .createdAtDescending, location: location)
    }
    
    /// Creates landing page query: 5 most recent announcements
    /// - Parameter location: Optional user location for displaying coordinates
    /// - Returns: Query configuration for landing page (limited to 5 items)
    static func landingPageQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(limit: 5, sortBy: .createdAtDescending, location: location)
    }
}

