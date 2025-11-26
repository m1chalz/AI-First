package com.intive.aifirst.petspot.composeapp.domain.models

/**
 * Animal species types supported by the system.
 * Determines icon/image display and filtering options.
 */
enum class AnimalSpecies(val displayName: String) {
    DOG("Dog"),
    CAT("Cat"),
    BIRD("Bird"),
    RABBIT("Rabbit"),
    OTHER("Other"),
}
