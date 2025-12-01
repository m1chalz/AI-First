package com.intive.aifirst.petspot.data.mappers

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.data.api.dto.AnnouncementDto

/**
 * Extension function to convert API DTO to domain model.
 * Handles null fallbacks for optional fields per FR-007.
 * Status and gender are deserialized directly as enums.
 */
fun AnnouncementDto.toDomain(): Animal =
    Animal(
        id = id,
        name = petName ?: "Unknown",
        photoUrl = photoUrl,
        location = toLocation(),
        species = species,
        breed = breed ?: "",
        gender = sex,
        status = status,
        lastSeenDate = lastSeenDate,
        description = description ?: "",
        email = email,
        phone = phone,
        microchipNumber = microchipNumber,
        rewardAmount = reward,
        age = age,
    )

/**
 * Creates Location domain object from DTO coordinates.
 */
private fun AnnouncementDto.toLocation(): Location =
    Location(
        latitude = locationLatitude,
        longitude = locationLongitude,
    )
