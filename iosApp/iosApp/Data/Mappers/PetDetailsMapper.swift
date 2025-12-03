import Foundation

/// Mapper for converting PetDetailsDTO to PetDetails domain model
struct PetDetailsMapper {
    private let photoURLMapper: PhotoURLMapper
    
    init(photoURLMapper: PhotoURLMapper = PhotoURLMapper()) {
        self.photoURLMapper = photoURLMapper
    }
    
    /// Converts PetDetailsDTO to PetDetails
    /// - Parameter dto: DTO from backend API
    /// - Returns: PetDetails domain model, or nil if DTO contains invalid data
    func map(_ dto: PetDetailsDTO) -> PetDetails? {
        return PetDetails(
            id: dto.id,
            petName: dto.petName,
            photoUrl: photoURLMapper.resolve(dto.photoUrl),
            status: dto.status.toDomain,
            lastSeenDate: dto.lastSeenDate,
            species: dto.species.toDomain,
            gender: dto.sex?.toDomain ?? .unknown,
            description: dto.description,
            phone: dto.phone,
            email: dto.email,
            breed: dto.breed,
            latitude: dto.locationLatitude,
            longitude: dto.locationLongitude,
            microchipNumber: dto.microchipNumber,
            approximateAge: dto.age,
            reward: dto.reward,
            createdAt: dto.createdAt,
            updatedAt: dto.updatedAt
        )
    }
}
