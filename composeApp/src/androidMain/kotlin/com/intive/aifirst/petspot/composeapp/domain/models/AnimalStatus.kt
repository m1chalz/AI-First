package com.intive.aifirst.petspot.composeapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Status of an animal in the system.
 * Determines badge color and text displayed in list.
 *
 * @property displayName Human-readable status label
 * @property badgeColor Hex color for status badge
 */
@Serializable
enum class AnimalStatus(
    val displayName: String,
    // Hex color
    val badgeColor: String,
) {
    @SerialName("MISSING")
    MISSING("MISSING", "#FF0000"), // Red badge - actively missing/searching

    @SerialName("FOUND")
    FOUND("FOUND", "#0074FF"), // Blue badge - animal has been found

    @SerialName("CLOSED")
    CLOSED("CLOSED", "#93A2B4"), // Gray badge - case closed/resolved
}
