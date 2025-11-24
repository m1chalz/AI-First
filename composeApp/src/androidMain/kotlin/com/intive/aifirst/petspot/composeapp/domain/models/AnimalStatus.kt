package com.intive.aifirst.petspot.composeapp.domain.models

/**
 * Status of an animal in the system.
 * Determines badge color and text displayed in list.
 *
 * @property displayName Human-readable status label
 * @property badgeColor Hex color for status badge
 */
enum class AnimalStatus(
    val displayName: String,
    val badgeColor: String  // Hex color
) {
    ACTIVE("Active", "#FF0000"),    // Red badge - actively missing/searching
    FOUND("Found", "#0074FF"),      // Blue badge - animal has been found
    CLOSED("Closed", "#93A2B4")     // Gray badge - case closed/resolved
}

