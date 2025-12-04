package com.intive.aifirst.petspot.features.reportmissing.data.repositories

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import com.intive.aifirst.petspot.features.reportmissing.data.mappers.toDto
import com.intive.aifirst.petspot.features.reportmissing.data.mappers.toDomain
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreateAnnouncementRequest
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreatedAnnouncement
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.AnnouncementRepository
import java.io.IOException

/**
 * Implementation of AnnouncementRepository using AnnouncementApiClient.
 * Handles 2-step submission: announcement creation + photo upload.
 * Maps between domain models and DTOs.
 */
class AnnouncementRepositoryImpl(
    private val apiClient: AnnouncementApiClient,
    private val contentResolver: ContentResolver,
) : AnnouncementRepository {
    override suspend fun createAnnouncement(request: CreateAnnouncementRequest): Result<CreatedAnnouncement> =
        try {
            val responseDto = apiClient.createAnnouncement(request.toDto())
            Result.success(responseDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun uploadPhoto(
        announcementId: String,
        managementPassword: String,
        photoUri: String,
    ): Result<Unit> =
        try {
            // Build Basic auth header
            val credentials = "$announcementId:$managementPassword"
            val basicAuth =
                "Basic " +
                    Base64.encodeToString(
                        credentials.toByteArray(),
                        Base64.NO_WRAP,
                    )

            // Read photo from URI using injected ContentResolver
            val uri = Uri.parse(photoUri)
            val inputStream =
                contentResolver.openInputStream(uri)
                    ?: return Result.failure(IOException("Cannot open photo URI"))
            val bytes = inputStream.readBytes()
            inputStream.close()

            // Delegate HTTP call to API client
            apiClient.uploadPhoto(announcementId, basicAuth, bytes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}

