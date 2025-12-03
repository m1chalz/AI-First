import Foundation

/// Mapper for converting AnnouncementDTO to Announcement domain model
struct AnnouncementMapper {
    private let photoURLMapper: PhotoURLMapper
    
    init(photoURLMapper: PhotoURLMapper = PhotoURLMapper()) {
        self.photoURLMapper = photoURLMapper
    }
    
    /// Converts AnnouncementDTO to Announcement
    /// - Parameter dto: DTO from backend API
    /// - Returns: Announcement domain model, or nil if DTO contains invalid data
    func map(_ dto: AnnouncementDTO) -> Announcement? {
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
}
