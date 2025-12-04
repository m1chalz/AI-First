package com.intive.aifirst.petspot.features.reportmissing.domain.repositories

import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreateAnnouncementRequest
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreatedAnnouncement

/**
 * Repository interface for announcement operations.
 * Provides 2-step submission: create announcement, then upload photo.
 */
interface AnnouncementRepository {
    /**
     * Creates a new announcement.
     * @param request Announcement data including contact info and pet details
     * @return Result containing CreatedAnnouncement with id and managementPassword
     */
    suspend fun createAnnouncement(request: CreateAnnouncementRequest): Result<CreatedAnnouncement>

    /**
     * Uploads photo for an existing announcement.
     * @param announcementId UUID from createAnnouncement response
     * @param managementPassword 6-digit code from createAnnouncement response
     * @param photoUri Content URI of the photo to upload
     * @return Result indicating success or failure
     */
    suspend fun uploadPhoto(
        announcementId: String,
        managementPassword: String,
        photoUri: String,
    ): Result<Unit>
}

