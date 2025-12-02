import Foundation

/// Mapper for converting between DTOs (Data layer) and Domain models
struct AnnouncementMapper {
    /// Converts DTO from HTTP response to domain model
    static func toDomain(_ dto: AnnouncementResponseDTO) -> AnnouncementResult {
        return AnnouncementResult(
            id: dto.id,
            managementPassword: dto.managementPassword
        )
    }
    
    /// Converts domain model to DTO for HTTP request
    static func toDTO(_ data: CreateAnnouncementData) -> CreateAnnouncementRequestDTO {
        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.formatOptions = [.withFullDate]
        
        return CreateAnnouncementRequestDTO(
            species: data.species,
            sex: data.sex,
            lastSeenDate: dateFormatter.string(from: data.lastSeenDate),
            locationLatitude: data.location.latitude,
            locationLongitude: data.location.longitude,
            email: data.contact.email,
            phone: data.contact.phone,
            status: "MISSING",
            microchipNumber: data.microchipNumber,
            description: data.description,
            reward: data.reward
        )
    }
}

