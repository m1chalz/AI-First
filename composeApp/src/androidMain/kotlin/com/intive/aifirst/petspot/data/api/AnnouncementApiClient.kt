package com.intive.aifirst.petspot.data.api

import com.intive.aifirst.petspot.data.api.dto.AnnouncementDto
import com.intive.aifirst.petspot.data.api.dto.AnnouncementsResponseDto
import com.intive.aifirst.petspot.features.reportmissing.data.dto.CreateAnnouncementRequestDto
import com.intive.aifirst.petspot.features.reportmissing.data.dto.CreateAnnouncementResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

/**
 * HTTP client wrapper for the announcements REST API.
 * Provides type-safe methods for all announcement operations.
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
    suspend fun getAnnouncements(): AnnouncementsResponseDto =
        httpClient.get("$baseUrl/api/v1/announcements").body()

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

    /**
     * Creates a new announcement (Step 1 of 2-step submission).
     *
     * @param request Announcement data DTO including contact info and pet details
     * @return Response DTO containing id and managementPassword
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors
     * @throws java.io.IOException on network failures
     */
    suspend fun createAnnouncement(request: CreateAnnouncementRequestDto): CreateAnnouncementResponseDto =
        httpClient.post("$baseUrl/api/v1/announcements") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /**
     * Uploads photo for an existing announcement (Step 2 of 2-step submission).
     *
     * @param announcementId UUID from createAnnouncement response
     * @param authHeader Basic auth header value (e.g., "Basic base64(id:password)")
     * @param photoBytes Raw photo bytes to upload
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors (401 for invalid auth)
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors
     * @throws java.io.IOException on network failures
     */
    suspend fun uploadPhoto(
        announcementId: String,
        authHeader: String,
        photoBytes: ByteArray,
    ) {
        httpClient.post("$baseUrl/api/v1/announcements/$announcementId/photos") {
            header(HttpHeaders.Authorization, authHeader)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "photo",
                            photoBytes,
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/*")
                                append(HttpHeaders.ContentDisposition, "filename=\"pet.jpg\"")
                            },
                        )
                    },
                ),
            )
        }
    }
}
