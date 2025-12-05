package com.intive.aifirst.petspot.composeapp.domain.repositories

import com.intive.aifirst.petspot.composeapp.domain.models.Animal

/**
 * Repository interface for animal data operations.
 * Defines contract for fetching animal data from various sources.
 *
 * Platform-specific implementations:
 * - Mock implementation: Returns hardcoded test data (current phase)
 * - Real implementation: Fetches from REST API (future phase)
 *
 * All operations are suspend functions using Kotlin Coroutines.
 * Throws exceptions on failure for natural error handling across platforms.
 */
interface AnimalRepository {
    /**
     * Retrieves all animals from the data source.
     * Optionally filters by location when lat/lng are provided.
     *
     * @param lat Optional latitude for location-based filtering
     * @param lng Optional longitude for location-based filtering
     * @param range Optional search radius in kilometers (backend defaults to 5km if not provided)
     * @return List of animals
     * @throws Exception if data fetch fails
     */
    suspend fun getAnimals(
        lat: Double? = null,
        lng: Double? = null,
        range: Int? = null,
    ): List<Animal>

    /**
     * Retrieves a single animal by its unique identifier.
     * Used for pet details screen to display comprehensive animal information.
     *
     * @param id Unique identifier of the animal
     * @return Animal entity with all details
     * @throws NoSuchElementException if animal not found
     */
    suspend fun getAnimalById(id: String): Animal
}
