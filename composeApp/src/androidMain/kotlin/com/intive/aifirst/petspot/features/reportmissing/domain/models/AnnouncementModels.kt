package com.intive.aifirst.petspot.features.reportmissing.domain.models

/**
 * Domain model for announcement creation request.
 * Represents the data needed to create a missing pet announcement.
 */
data class CreateAnnouncementRequest(
    val species: String,
    val sex: String,
    val lastSeenDate: String,
    val locationLatitude: Double,
    val locationLongitude: Double,
    val email: String,
    val phone: String,
    val status: String = "MISSING",
    val microchipNumber: String? = null,
    val description: String? = null,
    val reward: String? = null,
)

/**
 * Domain model for created announcement.
 * Contains id and managementPassword needed for photo upload and user reference.
 */
data class CreatedAnnouncement(
    val id: String,
    val managementPassword: String,
)

