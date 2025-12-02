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
        // Parse species
        guard let species = AnimalSpecies(fromDTO: dto.species) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id)")
            return nil
        }
        
        // Parse status - map MISSING to ACTIVE
        let statusString = dto.status.uppercased() == "MISSING" ? "ACTIVE" : dto.status.uppercased()
        guard let status = AnimalStatus(rawValue: statusString) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id)")
            return nil
        }
        
        // Parse gender (if sex is missing, default to unknown)
        let gender = dto.sex.flatMap({ AnimalGender(fromDTO: $0) }) ?? .unknown
        
        return PetDetails(
            id: dto.id,
            petName: dto.petName,
            photoUrl: photoURLMapper.resolve(dto.photoUrl),
            status: status,
            lastSeenDate: dto.lastSeenDate,
            species: species,
            gender: gender,
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
