import Foundation

// MARK: - Enum DTO Mappers

/// Extension for converting backend DTO strings to domain enums
/// Located in Data layer to prevent Domain layer from knowing about Data layer conversion needs
extension AnimalSpecies {
    /// Failable initializer from backend API string (case-insensitive)
    /// Used for DTO→Domain conversion in repository
    init?(fromDTO string: String) {
        let uppercased = string.uppercased()
        switch uppercased {
        case "DOG":
            self = .dog
        case "CAT":
            self = .cat
        case "BIRD":
            self = .bird
        case "RABBIT":
            self = .rabbit
        case "RODENT":
            self = .rodent
        case "REPTILE":
            self = .reptile
        case "OTHER":
            self = .other
        default:
            return nil
        }
    }
}

extension AnimalGender {
    /// Failable initializer from backend API string (case-insensitive)
    /// Used for DTO→Domain conversion in repository
    init?(fromDTO string: String) {
        let uppercased = string.uppercased()
        switch uppercased {
        case "MALE":
            self = .male
        case "FEMALE":
            self = .female
        case "UNKNOWN":
            self = .unknown
        default:
            return nil
        }
    }
}

// MARK: - Domain Model Mappers

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
        
        // Parse age
        let approximateAge = dto.age.map { "\($0) years" }
        
        self.init(
            id: dto.id,
            petName: dto.petName,
            photoUrl: dto.photoUrl,
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
            approximateAge: approximateAge,
            reward: dto.reward,
            createdAt: dto.createdAt,
            updatedAt: dto.updatedAt
        )
    }
}

