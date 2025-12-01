# Data Model: Android Backend API Integration

**Feature**: 029-android-api-integration  
**Date**: 2025-11-28

## Overview

This document defines the mapping between backend API responses (DTOs) and existing Android domain models. The goal is to replace mock data with real server data while preserving the existing domain model structure.

## API Response Structure

### GET /api/v1/announcements (List)

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "petName": "Max",
      "species": "Golden Retriever",
      "breed": "Purebred",
      "sex": "MALE",
      "age": 5,
      "description": "Friendly dog with brown fur",
      "microchipNumber": "123456789012345",
      "locationLatitude": 40.785091,
      "locationLongitude": -73.968285,
      "lastSeenDate": "2025-11-20",
      "email": "john@example.com",
      "phone": "+1 555 123 4567",
      "photoUrl": "https://example.com/photo.jpg",
      "status": "MISSING",
      "reward": "500 USD",
      "createdAt": "2025-11-24T12:34:56.789Z",
      "updatedAt": "2025-11-24T12:34:56.789Z"
    }
  ]
}
```

### GET /api/v1/announcements/:id (Single)

Same structure as list item, but without `data` wrapper.

## DTO Definitions

### AnnouncementsResponseDto

```kotlin
@Serializable
data class AnnouncementsResponseDto(
    val data: List<AnnouncementDto>
)
```

### AnnouncementDto

```kotlin
@Serializable
data class AnnouncementDto(
    val id: String,
    val petName: String? = null,
    val species: String,
    val breed: String? = null,
    val sex: String,
    val age: Int? = null,
    val description: String? = null,
    val microchipNumber: String? = null,
    val locationLatitude: Double? = null,
    val locationLongitude: Double? = null,
    val lastSeenDate: String,
    val email: String? = null,
    val phone: String? = null,
    val photoUrl: String,
    val status: String,
    val reward: String? = null,
    val createdAt: String,
    val updatedAt: String
    // NOTE: managementPassword is EXCLUDED (FR-009)
)
```

## Domain Model (Existing)

### Animal

```kotlin
data class Animal(
    val id: String,
    val name: String,
    val photoUrl: String,
    val location: Location,
    val species: AnimalSpecies,
    val breed: String,
    val gender: AnimalGender,
    val status: AnimalStatus,
    val lastSeenDate: String,
    val description: String,
    val email: String?,
    val phone: String?,
    val microchipNumber: String? = null,
    val rewardAmount: String? = null,
    val approximateAge: String? = null
)
```

### Location

```kotlin
data class Location(
    val city: String,
    val radiusKm: Int,
    val latitude: Double? = null,
    val longitude: Double? = null
)
```

### Enums

```kotlin
enum class AnimalStatus(val displayName: String, val badgeColor: String) {
    MISSING("MISSING", "#FF0000"),
    FOUND("FOUND", "#0074FF"),
    CLOSED("CLOSED", "#93A2B4")
}

enum class AnimalGender { MALE, FEMALE, UNKNOWN }

enum class AnimalSpecies { DOG, CAT, BIRD, OTHER }
```

## Field Mapping Table

| API Field (DTO) | Domain Field | Transformation | Notes |
|-----------------|--------------|----------------|-------|
| `id` | `id` | Direct copy | Required |
| `petName` | `name` | Null → "Unknown" | Fallback for display |
| `species` | `species` | String → `AnimalSpecies` | Parse or default to `OTHER` |
| `breed` | `breed` | Null → "" | Empty string fallback |
| `sex` | `gender` | String → `AnimalGender` | MALE/FEMALE/UNKNOWN |
| `age` | `approximateAge` | Int → String | e.g., `5` → "5 years" |
| `description` | `description` | Null → "" | Empty string fallback |
| `microchipNumber` | `microchipNumber` | Direct copy | Optional |
| `locationLatitude` | `location.latitude` | Direct copy | Optional |
| `locationLongitude` | `location.longitude` | Direct copy | Optional |
| `lastSeenDate` | `lastSeenDate` | Direct copy | Format: YYYY-MM-DD |
| `email` | `email` | Direct copy | Optional |
| `phone` | `phone` | Direct copy | Optional |
| `photoUrl` | `photoUrl` | Direct copy | Required |
| `status` | `status` | String → `AnimalStatus` | **Unknown → MISSING** (FR-010) |
| `reward` | `rewardAmount` | Direct copy | Optional |
| `createdAt` | N/A | Not mapped | Not needed in domain |
| `updatedAt` | N/A | Not mapped | Not needed in domain |
| `managementPassword` | N/A | **NEVER MAPPED** | Security requirement (FR-009) |

## Mapping Rules

### Status Coercion (FR-010)

```kotlin
fun String.toAnimalStatus(): AnimalStatus = when (this.uppercase()) {
    "MISSING" -> AnimalStatus.MISSING
    "FOUND" -> AnimalStatus.FOUND
    "CLOSED" -> AnimalStatus.CLOSED
    else -> AnimalStatus.MISSING // Unknown statuses default to MISSING
}
```

### Gender Mapping

```kotlin
fun String.toAnimalGender(): AnimalGender = when (this.uppercase()) {
    "MALE" -> AnimalGender.MALE
    "FEMALE" -> AnimalGender.FEMALE
    else -> AnimalGender.UNKNOWN
}
```

### Species Mapping

```kotlin
fun String.toAnimalSpecies(): AnimalSpecies {
    val speciesLower = this.lowercase()
    return when {
        speciesLower.contains("dog") || speciesLower.contains("retriever") || 
        speciesLower.contains("shepherd") || speciesLower.contains("terrier") -> AnimalSpecies.DOG
        speciesLower.contains("cat") || speciesLower.contains("persian") ||
        speciesLower.contains("maine") || speciesLower.contains("siamese") -> AnimalSpecies.CAT
        speciesLower.contains("bird") || speciesLower.contains("parrot") ||
        speciesLower.contains("canary") -> AnimalSpecies.BIRD
        else -> AnimalSpecies.OTHER
    }
}
```

### Location Construction

```kotlin
fun AnnouncementDto.toLocation(): Location = Location(
    city = "Unknown", // API doesn't provide city name, could be reverse-geocoded later
    radiusKm = 5, // Default radius
    latitude = locationLatitude,
    longitude = locationLongitude
)
```

### Age Formatting

```kotlin
fun Int?.toAgeString(): String? = this?.let { "$it years" }
```

## Complete Mapper

```kotlin
fun AnnouncementDto.toDomain(): Animal = Animal(
    id = id,
    name = petName ?: "Unknown",
    photoUrl = photoUrl,
    location = toLocation(),
    species = species.toAnimalSpecies(),
    breed = breed ?: "",
    gender = sex.toAnimalGender(),
    status = status.toAnimalStatus(),
    lastSeenDate = lastSeenDate,
    description = description ?: "",
    email = email,
    phone = phone,
    microchipNumber = microchipNumber,
    rewardAmount = reward,
    approximateAge = age.toAgeString()
)
```

## Validation Notes

1. **Required fields**: `id`, `species`, `sex`, `lastSeenDate`, `photoUrl`, `status` - API guarantees these
2. **Optional fields**: All others may be null and must have fallback handling
3. **Security**: `managementPassword` is excluded from DTO definition entirely (not just ignored)
4. **Unknown enums**: Default to safe values (MISSING, UNKNOWN, OTHER) rather than throwing


