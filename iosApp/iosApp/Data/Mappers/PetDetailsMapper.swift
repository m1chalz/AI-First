import Foundation

// MARK: - Helper Functions

/// Converts relative photo URL to absolute URL by prepending base URL
/// - Parameter photoUrl: Photo URL from backend (may be relative or absolute)
/// - Returns: Absolute URL string
private func resolvePhotoURL(_ photoUrl: String) -> String {
    // If URL starts with /, it's relative - prepend base URL
    if photoUrl.starts(with: "/") {
        return APIConfig.baseURL + photoUrl
    }
    // Otherwise it's already absolute
    return photoUrl
}

// MARK: - Domain Model Mappers

/// Extension for converting PetDetailsDTO to PetDetails domain model
extension PetDetails {
    /// Failable initializer - returns nil if DTO contains invalid data
    init?(fromDTO dto: PetDetailsDTO) {
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
        
        self.init(
            id: dto.id,
            petName: dto.petName,
            photoUrl: resolvePhotoURL(dto.photoUrl),
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

