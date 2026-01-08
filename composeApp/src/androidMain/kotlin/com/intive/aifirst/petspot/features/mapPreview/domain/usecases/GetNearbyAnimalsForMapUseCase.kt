package com.intive.aifirst.petspot.features.mapPreview.domain.usecases

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.domain.models.LocationCoordinates

/**
 * Use case for fetching nearby animals for map preview.
 * Wraps existing AnimalRepository with 10km default radius.
 */
class GetNearbyAnimalsForMapUseCase(
    private val animalRepository: AnimalRepository,
) {
    /**
     * Fetches animals near the given location.
     *
     * @param location User's current location
     * @param radiusKm Search radius in kilometers (default: 10km per spec)
     * @return Result containing list of animals (may be empty)
     */
    suspend operator fun invoke(
        location: LocationCoordinates,
        radiusKm: Int = DEFAULT_RADIUS_KM,
    ): Result<List<Animal>> =
        runCatching {
            animalRepository.getAnimals(
                lat = location.latitude,
                lng = location.longitude,
                range = radiusKm,
            )
        }

    companion object {
        /** Default search radius: 10 km per spec */
        const val DEFAULT_RADIUS_KM = 10
    }
}
