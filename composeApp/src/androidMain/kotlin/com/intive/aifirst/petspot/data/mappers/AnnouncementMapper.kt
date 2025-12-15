package com.intive.aifirst.petspot.data.mappers

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.data.api.dto.AnnouncementDto

/**
 * Extension function to convert API DTO to domain model.
 * Handles null fallbacks for optional fields per FR-007.
 * Status and gender are deserialized directly as enums.
 *
 * @param baseUrl Base URL for constructing full image URLs from relative paths
 */
fun AnnouncementDto.toDomain(baseUrl: String): Animal =
    Animal(
        id = id,
        name = petName ?: "Unknown",
        photoUrl = buildFullPhotoUrl(photoUrl, baseUrl),
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
 * Constructs full photo URL from relative path.
 * Handles both relative paths (/images/...) and already-full URLs (https://...).
 */
private fun buildFullPhotoUrl(
    photoUrl: String,
    baseUrl: String,
): String =
    if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
        photoUrl // Already a full URL
    } else {
        "$baseUrl$photoUrl" // Prepend base URL to relative path
    }

/**
 * Creates Location domain object from DTO coordinates.
 */
private fun AnnouncementDto.toLocation(): Location =
    Location(
        latitude = locationLatitude,
        longitude = locationLongitude,
    )
