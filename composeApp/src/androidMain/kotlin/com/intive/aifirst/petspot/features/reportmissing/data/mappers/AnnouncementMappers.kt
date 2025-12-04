package com.intive.aifirst.petspot.features.reportmissing.data.mappers

import com.intive.aifirst.petspot.features.reportmissing.data.dto.CreateAnnouncementRequestDto
import com.intive.aifirst.petspot.features.reportmissing.data.dto.CreateAnnouncementResponseDto
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreateAnnouncementRequest
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreatedAnnouncement

/**
 * Maps domain request to DTO for API call.
 */
fun CreateAnnouncementRequest.toDto() = CreateAnnouncementRequestDto(
    species = species,
    sex = sex,
    lastSeenDate = lastSeenDate,
    locationLatitude = locationLatitude,
    locationLongitude = locationLongitude,
    email = email,
    phone = phone,
    status = status,
    petName = petName,
    breed = breed,
    age = age,
    microchipNumber = microchipNumber,
    description = description,
    reward = reward,
)

/**
 * Maps API response DTO to domain model.
 */
fun CreateAnnouncementResponseDto.toDomain() = CreatedAnnouncement(
    id = id,
    managementPassword = managementPassword,
)

