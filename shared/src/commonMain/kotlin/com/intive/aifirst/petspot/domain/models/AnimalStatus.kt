package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Status of an animal in the system.
 * Determines badge color and text displayed in list.
 *
 * @property displayName Human-readable status label
 * @property badgeColor Hex color for status badge
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AnimalStatus(
    val displayName: String,
    // Hex color
    val badgeColor: String,
) {
    ACTIVE("Active", "#FF0000"), // Red badge - actively missing/searching
    FOUND("Found", "#0074FF"), // Blue badge - animal has been found
    CLOSED("Closed", "#93A2B4"), // Gray badge - case closed/resolved
}
