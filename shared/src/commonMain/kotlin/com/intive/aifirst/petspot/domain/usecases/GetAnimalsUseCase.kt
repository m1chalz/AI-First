package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.repositories.AnimalRepository

/**
 * Retrieves list of animals from repository.
 * Returns mock data until backend integration is implemented.
 */
class GetAnimalsUseCase(
    private val repository: AnimalRepository,
) {
    /**
     * Fetches all animals from the repository.
     * Delegates to repository implementation which may be mock or real API.
     *
     * @return List of animals
     * @throws Exception if data fetch fails
     */
    suspend operator fun invoke(): List<Animal> = repository.getAnimals()
}
