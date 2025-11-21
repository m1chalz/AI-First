package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Animal species types supported by the system.
 * Determines icon/image display and filtering options.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AnimalSpecies(val displayName: String) {
    DOG("Dog"),
    CAT("Cat"),
    BIRD("Bird"),
    RABBIT("Rabbit"),
    OTHER("Other")
}

