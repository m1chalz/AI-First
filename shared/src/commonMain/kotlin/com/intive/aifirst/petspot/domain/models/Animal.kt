package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents an animal in the PetSpot system.
 * Contains all information needed for list display and detail views.
 * Shared across all platforms via Kotlin Multiplatform.
 *
 * @property id Unique identifier (UUID or database ID)
 * @property name Name of the animal (e.g., "Buddy", "Mittens")
 * @property photoUrl URL or placeholder identifier for animal photo
 * @property location Geographic location with radius for search area
 * @property species Animal species (Dog, Cat, Bird, etc.)
 * @property breed Specific breed name (e.g., "Maine Coon", "German Shepherd")
 * @property gender Biological sex (Male, Female, Unknown)
 * @property status Current status (Active, Found, Closed)
 * @property lastSeenDate Date when animal was last seen (for Active status) or found (for Found status)
 * @property description Detailed text description (visible on web, truncated on mobile)
 * @property email Contact email of the person who reported/owns the animal (optional)
 * @property phone Contact phone number of the person who reported/owns the animal (optional)
 * @property microchipNumber Microchip number for pet identification (optional)
 * @property rewardAmount Reward amount as string, displayed as-is (optional)
 * @property approximateAge Approximate age description (optional)
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Animal(
    val id: String,
    val name: String,
    val photoUrl: String,  // URL or placeholder identifier
    val location: Location,
    val species: AnimalSpecies,
    val breed: String,
    val gender: AnimalGender,
    val status: AnimalStatus,
    val lastSeenDate: String,  // Format: DD/MM/YYYY (as specified in spec)
    val description: String,
    val email: String?,  // Optional contact email
    val phone: String?,  // Optional contact phone
    val microchipNumber: String? = null,  // Optional microchip number (format: 000-000-000-000)
    val rewardAmount: String? = null,     // Optional reward amount (displayed as-is)
    val approximateAge: String? = null    // Optional approximate age description
)

