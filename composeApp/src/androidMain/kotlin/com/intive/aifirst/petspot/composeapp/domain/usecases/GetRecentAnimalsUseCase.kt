package com.intive.aifirst.petspot.composeapp.domain.usecases

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository

/**
 * Retrieves recent lost (MISSING status) animals for the home teaser.
 * Performs client-side filtering, sorting, and limiting.
 */
class GetRecentAnimalsUseCase(
    private val repository: AnimalRepository,
) {
    /**
     * Fetches animals and filters to show only MISSING status,
     * sorted by lastSeenDate (newest first), limited to specified count.
     *
     * @param limit Maximum number of animals to return (default: 5)
     * @return List of recent lost animals
     * @throws Exception if data fetch fails
     */
    suspend operator fun invoke(limit: Int = 5): List<Animal> =
        repository.getAnimals()
            .filter { it.status == AnimalStatus.MISSING }
            .sortedByDescending { it.lastSeenDate } // YYYY-MM-DD is lexicographically sortable
            .take(limit)
}
