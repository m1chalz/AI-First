package com.intive.aifirst.petspot.features.reportmissing.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for POST /api/v1/announcements response.
 * Contains id and managementPassword needed for photo upload.
 */
@Serializable
data class CreateAnnouncementResponseDto(
    @SerialName("id") val id: String,
    @SerialName("managementPassword") val managementPassword: String,
)

