package com.intive.aifirst.petspot.composeapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Animal gender/sex.
 * Displayed as icon on web version (per Figma spec).
 */
@Serializable
enum class AnimalGender(val displayName: String) {
    @SerialName("MALE")
    MALE("Male"),

    @SerialName("FEMALE")
    FEMALE("Female"),

    @SerialName("UNKNOWN")
    UNKNOWN("Unknown"),
}
