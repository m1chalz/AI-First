package com.intive.aifirst.petspot.data

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import com.intive.aifirst.petspot.data.mappers.toDomain

/**
 * Repository implementation that fetches pet announcements from the backend API.
 *
 * @property apiClient HTTP client for backend API communication
 * @property baseUrl Base URL for constructing full image URLs from relative paths
 */
class AnimalRepositoryImpl(
    private val apiClient: AnnouncementApiClient,
    private val baseUrl: String,
) : AnimalRepository {
    /**
     * Retrieves all pet announcements from the backend API.
     *
     * @return List of animals from backend
     * @throws Exception on network or API errors
     */
    override suspend fun getAnimals(): List<Animal> {
        val response = apiClient.getAnnouncements()
        return response.data.map { it.toDomain(baseUrl) }
    }

    /**
     * Retrieves a single pet announcement by ID from the backend API.
     *
     * @param id Unique identifier of the announcement
     * @return Animal entity
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors (including 404)
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors
     * @throws java.io.IOException on network failures
     */
    override suspend fun getAnimalById(id: String): Animal {
        val response = apiClient.getAnnouncementById(id)
        return response.toDomain(baseUrl)
    }
}
