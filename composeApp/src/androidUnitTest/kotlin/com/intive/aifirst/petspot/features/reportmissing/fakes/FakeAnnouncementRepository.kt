package com.intive.aifirst.petspot.features.reportmissing.fakes

import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreateAnnouncementRequest
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreatedAnnouncement
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.AnnouncementRepository

/**
 * Fake implementation of [AnnouncementRepository] for unit testing.
 * Allows controlling success/failure scenarios for both steps of submission.
 */
class FakeAnnouncementRepository : AnnouncementRepository {
    // Configuration for test scenarios
    var createAnnouncementResult: Result<CreatedAnnouncement> =
        Result.success(CreatedAnnouncement(id = "test-uuid", managementPassword = "123456"))
    var uploadPhotoResult: Result<Unit> = Result.success(Unit)

    // Tracking for verification
    var createAnnouncementCalled = false
    var uploadPhotoCalled = false
    var lastCreateRequest: CreateAnnouncementRequest? = null
    var lastUploadAnnouncementId: String? = null
    var lastUploadPassword: String? = null
    var lastUploadPhotoUri: String? = null

    override suspend fun createAnnouncement(request: CreateAnnouncementRequest): Result<CreatedAnnouncement> {
        createAnnouncementCalled = true
        lastCreateRequest = request
        return createAnnouncementResult
    }

    override suspend fun uploadPhoto(
        announcementId: String,
        managementPassword: String,
        photoUri: String,
    ): Result<Unit> {
        uploadPhotoCalled = true
        lastUploadAnnouncementId = announcementId
        lastUploadPassword = managementPassword
        lastUploadPhotoUri = photoUri
        return uploadPhotoResult
    }

    /** Resets all tracking flags and restores default success results */
    fun reset() {
        createAnnouncementResult = Result.success(CreatedAnnouncement(id = "test-uuid", managementPassword = "123456"))
        uploadPhotoResult = Result.success(Unit)
        createAnnouncementCalled = false
        uploadPhotoCalled = false
        lastCreateRequest = null
        lastUploadAnnouncementId = null
        lastUploadPassword = null
        lastUploadPhotoUri = null
    }
}

