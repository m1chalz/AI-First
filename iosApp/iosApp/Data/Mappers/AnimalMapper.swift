import Foundation

/// Extension for converting AnnouncementDTO to Animal domain model
extension Animal {
    /// Failable initializer - returns nil if DTO contains invalid data
    /// Allows graceful handling of invalid items in list (skip instead of crash)
    init?(fromDTO dto: AnnouncementDTO) {
        // Parse species
        guard let species = AnimalSpecies(fromDTO: dto.species) else {
            print("Warning: Invalid species '\(dto.species)' for announcement \(dto.id), skipping item")
            return nil
        }
        
        // Parse status - map MISSING to ACTIVE
        let statusString = dto.status.uppercased() == "MISSING" ? "ACTIVE" : dto.status.uppercased()
        guard let status = AnimalStatus(rawValue: statusString) else {
            print("Warning: Invalid status '\(dto.status)' for announcement \(dto.id), skipping item")
            return nil
        }
        
        // Parse gender (if sex is missing, default to unknown)
        let gender = dto.sex.flatMap({ AnimalGender(fromDTO: $0) }) ?? .unknown
        
        self.init(
            id: dto.id,
            name: dto.petName,
            photoUrl: dto.photoUrl,
            coordinate: Coordinate(latitude: dto.locationLatitude, longitude: dto.locationLongitude),
            species: species,
            breed: dto.breed,
            gender: gender,
            status: status,
            lastSeenDate: dto.lastSeenDate,
            description: dto.description,
            email: dto.email,
            phone: dto.phone
        )
    }
}

