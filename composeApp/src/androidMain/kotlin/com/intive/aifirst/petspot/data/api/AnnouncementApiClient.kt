package com.intive.aifirst.petspot.data.api

import com.intive.aifirst.petspot.data.api.dto.AnnouncementDto
import com.intive.aifirst.petspot.data.api.dto.AnnouncementsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * HTTP client wrapper for the announcements REST API.
 * Provides type-safe methods to fetch pet announcements from the backend.
 *
 * @property httpClient Configured Ktor HttpClient instance
 * @property baseUrl Base URL for API endpoints (e.g., "http://10.0.2.2:3000")
 */
class AnnouncementApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    /**
     * Fetches all pet announcements from the API.
     *
     * @return Response containing list of announcements
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors
     * @throws java.io.IOException on network failures
     */
    suspend fun getAnnouncements(): AnnouncementsResponseDto = httpClient.get("$baseUrl/api/v1/announcements").body()

    /**
     * Fetches a single pet announcement by ID.
     *
     * @param id Unique identifier of the announcement
     * @return Single announcement DTO
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors (including 404)
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors
     * @throws java.io.IOException on network failures
     */
    suspend fun getAnnouncementById(id: String): AnnouncementDto =
        httpClient.get("$baseUrl/api/v1/announcements/$id").body()
}
