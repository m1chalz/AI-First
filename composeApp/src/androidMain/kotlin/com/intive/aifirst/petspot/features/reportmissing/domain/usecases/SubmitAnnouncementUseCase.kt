package com.intive.aifirst.petspot.features.reportmissing.domain.usecases

import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreateAnnouncementRequest
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.AnnouncementRepository
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.FlowData
import java.time.format.DateTimeFormatter

/**
 * Use case that orchestrates 2-step announcement submission.
 *
 * Step 1: POST /api/v1/announcements → get id + managementPassword
 * Step 2: POST /api/v1/announcements/:id/photos with Basic auth
 *
 * Both steps must succeed for submission to be considered complete.
 *
 * @return managementPassword on success (for display on summary screen)
 */
class SubmitAnnouncementUseCase(
    private val repository: AnnouncementRepository,
) {
    suspend operator fun invoke(flowData: FlowData): Result<String> {
        // Build request from flow data
        val request = buildRequest(flowData)

        // Step 1: Create announcement
        val createResult = repository.createAnnouncement(request)
        if (createResult.isFailure) {
            return Result.failure(createResult.exceptionOrNull() ?: Exception("Unknown error"))
        }

        val createdAnnouncement = createResult.getOrThrow()

        // Step 2: Upload photo
        val photoUri = flowData.photoUri
        if (photoUri != null) {
            val uploadResult =
                repository.uploadPhoto(
                    announcementId = createdAnnouncement.id,
                    managementPassword = createdAnnouncement.managementPassword,
                    photoUri = photoUri,
                )
            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Photo upload failed"))
            }
        }

        // Return managementPassword for user reference
        return Result.success(createdAnnouncement.managementPassword)
    }

    private fun buildRequest(flowData: FlowData): CreateAnnouncementRequest {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val lastSeenDate = flowData.disappearanceDate?.format(dateFormatter) ?: ""

        return CreateAnnouncementRequest(
            species = flowData.animalSpecies.uppercase(),
            sex = flowData.animalGender?.name ?: "UNKNOWN",
            lastSeenDate = lastSeenDate,
            locationLatitude = flowData.latitude ?: 0.0,
            locationLongitude = flowData.longitude ?: 0.0,
            email = flowData.contactEmail,
            phone = flowData.contactPhone,
            status = "MISSING",
            petName = flowData.petName.ifBlank { null },
            breed = flowData.animalRace.ifBlank { null },
            age = flowData.animalAge,
            microchipNumber = flowData.chipNumber.ifBlank { null },
            description = flowData.additionalDescription.ifBlank { null },
            reward = flowData.rewardDescription.ifBlank { null },
        )
    }
}
