package com.intive.aifirst.petspot.data.api.dto

import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO representing a single pet announcement from the API.
 * Maps to `GET /api/v1/announcements` response items and `GET /api/v1/announcements/:id`.
 *
 * Note: managementPassword is intentionally excluded per FR-009 security requirement.
 */
@Serializable
data class AnnouncementDto(
    @SerialName("id")
    val id: String,
    @SerialName("petName")
    val petName: String? = null,
    @SerialName("species")
    val species: String,
    @SerialName("breed")
    val breed: String? = null,
    @SerialName("sex")
    val sex: AnimalGender,
    @SerialName("age")
    val age: Int? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("microchipNumber")
    val microchipNumber: String? = null,
    @SerialName("locationLatitude")
    val locationLatitude: Double? = null,
    @SerialName("locationLongitude")
    val locationLongitude: Double? = null,
    @SerialName("lastSeenDate")
    val lastSeenDate: String,
    @SerialName("email")
    val email: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("photoUrl")
    val photoUrl: String,
    @SerialName("status")
    val status: AnimalStatus,
    @SerialName("reward")
    val reward: String? = null,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)
