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
     * Mock implementation returns fixed list of 16 animals.
     * Real implementation will support pagination and filtering.
     *
     * @return List of animals
     * @throws Exception if data fetch fails
     */
    suspend fun getAnimals(): List<Animal>
}
