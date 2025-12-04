package com.intive.aifirst.petspot.features.reportmissing.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for POST /api/v1/announcements request body.
 * Used in Step 1 of 2-step announcement submission.
 */
@Serializable
data class CreateAnnouncementRequestDto(
    @SerialName("species") val species: String,
    @SerialName("sex") val sex: String,
    @SerialName("lastSeenDate") val lastSeenDate: String,
    @SerialName("locationLatitude") val locationLatitude: Double,
    @SerialName("locationLongitude") val locationLongitude: Double,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String,
    @SerialName("status") val status: String = "MISSING",
    @SerialName("petName") val petName: String? = null,
    @SerialName("breed") val breed: String? = null,
    @SerialName("age") val age: Int? = null,
    @SerialName("microchipNumber") val microchipNumber: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("reward") val reward: String? = null,
)

