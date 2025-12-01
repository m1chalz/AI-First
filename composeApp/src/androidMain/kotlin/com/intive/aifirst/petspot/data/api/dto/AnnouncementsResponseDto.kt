package com.intive.aifirst.petspot.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO representing the response wrapper for `GET /api/v1/announcements`.
 * Contains a list of announcements in the `data` field.
 */
@Serializable
data class AnnouncementsResponseDto(
    @SerialName("data")
    val data: List<AnnouncementDto>,
)
