package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Animal gender/sex.
 * Displayed as icon on web version (per Figma spec).
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AnimalGender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("Unknown"),
}
