package com.intive.aifirst.petspot.composeapp.domain.models

/**
 * Animal gender/sex.
 * Displayed as icon on web version (per Figma spec).
 */
enum class AnimalGender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("Unknown")
}

