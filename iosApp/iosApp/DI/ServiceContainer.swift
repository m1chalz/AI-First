import Foundation

/**
 * Service container for manual dependency injection.
 * Provides centralized registry of services and repositories.
 * Uses lazy initialization for efficient memory usage.
 *
 * Pattern: Constructor injection via ServiceContainer
 * Architecture: iOS MVVM-C with manual DI (no frameworks)
 */
class ServiceContainer {
    /// Shared singleton instance
    static let shared = ServiceContainer()
    
    private init() {}
    
    // MARK: - Services
    
    /// Location service for permission management and coordinate fetching
    lazy var locationService: LocationServiceProtocol = LocationService()
    
    /// Disk cache for photo attachments within Report Missing Pet flow
    lazy var photoAttachmentCache: PhotoAttachmentCacheProtocol = PhotoAttachmentCache()

    /// Location permission handler (shared across ViewModels for consistent permission management)
    lazy var locationPermissionHandler: LocationPermissionHandler =
        LocationPermissionHandler(locationService: locationService)
    
    /// Announcement submission service for 2-step submission orchestration
    lazy var announcementSubmissionService: AnnouncementSubmissionServiceProtocol =
        AnnouncementSubmissionService(repository: announcementRepository)

    // MARK: - Repositories
    
    /// Announcement repository for fetching announcement data
    lazy var announcementRepository: AnnouncementRepositoryProtocol = AnnouncementRepository()
}

