package com.intive.aifirst.petspot.composeapp.domain.usecases

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository

/**
 * Retrieves list of animals from repository.
 * Optionally filters by location when coordinates are provided.
 */
class GetAnimalsUseCase(
    private val repository: AnimalRepository,
) {
    /**
     * Fetches animals from the repository.
     * When location is provided, returns animals near the specified coordinates.
     *
     * @param lat Optional latitude for location-based filtering
     * @param lng Optional longitude for location-based filtering
     * @param range Optional search radius in kilometers (backend defaults to 5km if not provided)
     * @return List of animals
     * @throws Exception if data fetch fails
     */
    suspend operator fun invoke(
        lat: Double? = null,
        lng: Double? = null,
        range: Int? = null,
    ): List<Animal> = repository.getAnimals(lat, lng, range)
}
