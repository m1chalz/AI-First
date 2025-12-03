package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import java.time.LocalDate

/**
 * UI state for the Animal Description screen (Step 3/4).
 * Immutable data class following MVI architecture.
 */
data class AnimalDescriptionUiState(
    // Form fields (in display order)
    // Note: null means "use today's date" - ViewModel sets initial value to avoid API level issue
    val disappearanceDate: LocalDate? = null,
    val petName: String = "",
    val animalSpecies: String = "",
    val animalRace: String = "",
    val animalGender: AnimalGender? = null,
    val animalAge: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val additionalDescription: String = "",
    // Validation errors (null = no error)
    val speciesError: String? = null,
    val raceError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val latitudeError: String? = null,
    val longitudeError: String? = null,
    // Loading states
    val isGpsLoading: Boolean = false,
    val gpsSuccessMessage: String? = null,
    // Date picker dialog state
    val isDatePickerVisible: Boolean = false,
) {
    companion object {
        val Initial = AnimalDescriptionUiState()
    }

    /** True when species is selected and race field should be enabled */
    val isRaceFieldEnabled: Boolean
        get() = animalSpecies.isNotBlank()

    /** Character count for description field */
    val descriptionCharCount: Int
        get() = additionalDescription.length

    val descriptionMaxChars: Int = 500
}
