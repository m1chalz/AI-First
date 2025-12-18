import Foundation

/// Mapper for converting AnnouncementDTO to Announcement domain model.
/// Validates data integrity and returns nil for malformed DTOs (FR-016).
struct AnnouncementMapper {
    private let photoURLMapper: PhotoURLMapper
    
    init(photoURLMapper: PhotoURLMapper = PhotoURLMapper()) {
        self.photoURLMapper = photoURLMapper
    }
    
    /// Converts AnnouncementDTO to Announcement with validation.
    ///
    /// **Validation Rules (FR-016)**:
    /// - ID must be non-empty
    /// - Latitude must be in range [-90, 90]
    /// - Longitude must be in range [-180, 180]
    ///
    /// - Parameter dto: DTO from backend API
    /// - Returns: Announcement domain model, or nil if DTO contains invalid data
    func map(_ dto: AnnouncementDTO) -> Announcement? {
        // FR-016: Validate required fields
        guard isValidId(dto.id) else {
            return nil
        }
        
        // FR-016: Validate coordinates
        guard isValidCoordinate(latitude: dto.locationLatitude, longitude: dto.locationLongitude) else {
            return nil
        }
        
        return Announcement(
            id: dto.id,
            name: dto.petName,
            photoUrl: photoURLMapper.resolve(dto.photoUrl),
            coordinate: Coordinate(latitude: dto.locationLatitude, longitude: dto.locationLongitude),
            species: dto.species.toDomain,
            breed: dto.breed,
            gender: dto.sex?.toDomain ?? .unknown,
            status: dto.status.toDomain,
            lastSeenDate: dto.lastSeenDate,
            description: dto.description,
            email: dto.email,
            phone: dto.phone
        )
    }
    
    // MARK: - Validation Helpers
    
    /// Validates that ID is non-empty.
    private func isValidId(_ id: String) -> Bool {
        !id.isEmpty
    }
    
    /// Validates that coordinates are within valid ranges.
    /// NaN values fail range checks automatically (NaN comparisons return false).
    private func isValidCoordinate(latitude: Double, longitude: Double) -> Bool {
        let validLatitude = latitude >= -90 && latitude <= 90
        let validLongitude = longitude >= -180 && longitude <= 180
        return validLatitude && validLongitude
    }
}
