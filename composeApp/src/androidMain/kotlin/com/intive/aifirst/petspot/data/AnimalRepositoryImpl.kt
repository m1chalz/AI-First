package com.intive.aifirst.petspot.data

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import kotlinx.coroutines.delay

/**
 * Repository implementation with mocked data for UI development.
 * Returns hardcoded list of 16 animals for testing UI flows.
 * Simulates network delay to test loading states.
 * Will be replaced with RemoteAnimalRepository when backend is ready.
 */
class AnimalRepositoryImpl : AnimalRepository {
    /** Simulated network delay in milliseconds */
    private val networkDelayMs: Long = 500

    /**
     * Retrieves mock animal data after simulated delay.
     * Uses MockAnimalData as single source of truth for consistency across platforms.
     *
     * @return List of 16 mock animals
     */
    override suspend fun getAnimals(): List<Animal> {
        // Simulate network delay
        delay(networkDelayMs)

        // Return mock data from shared test fixtures
        return MockAnimalData.generateMockAnimals()
    }
}
