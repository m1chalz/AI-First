package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.repositories.AnimalRepository

/**
 * Use case for retrieving a single animal by its ID.
 * Returns Result wrapper for safe error handling in ViewModel.
 */
class GetAnimalByIdUseCase(
    private val repository: AnimalRepository
) {
    /**
     * Fetches animal details by ID from repository.
     *
     * @param id Unique identifier of the animal
     * @return Animal entity
     * @throws NoSuchElementException if animal not found
     */
    suspend operator fun invoke(id: String): Animal =
        repository.getAnimalById(id)
}

