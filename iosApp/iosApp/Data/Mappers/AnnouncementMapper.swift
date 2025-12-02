import Foundation

/// Mapper for converting between DTOs (Data layer) and Domain models
class AnnouncementMapper {
    
    /// Converts DTO from HTTP response to domain model
    func toDomain(_ dto: AnnouncementResponseDTO) -> AnnouncementResult {
        AnnouncementResult(
            id: dto.id,
            managementPassword: dto.managementPassword
        )
    }
    
    /// Converts domain model to DTO for HTTP request
    func toDTO(_ data: CreateAnnouncementData) -> CreateAnnouncementRequestDTO {
        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.formatOptions = [.withFullDate]
        
        return CreateAnnouncementRequestDTO(
            species: AnimalSpeciesDTO(domain: data.species),
            sex: AnimalGenderDTO(domain: data.sex),
            lastSeenDate: dateFormatter.string(from: data.lastSeenDate),
            locationLatitude: data.location.latitude,
            locationLongitude: data.location.longitude,
            email: data.contact.email,
            phone: data.contact.phone,
            status: AnimalStatusDTO(domain: .active),
            microchipNumber: data.microchipNumber,
            description: data.description,
            reward: data.reward
        )
    }
}
