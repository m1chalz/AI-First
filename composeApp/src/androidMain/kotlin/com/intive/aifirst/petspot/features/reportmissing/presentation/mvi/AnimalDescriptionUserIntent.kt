package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import java.time.LocalDate

/**
 * User intents for the Animal Description screen (Step 3/4).
 * Sealed interface following MVI architecture.
 */
sealed interface AnimalDescriptionUserIntent {
    data class UpdatePetName(val name: String) : AnimalDescriptionUserIntent

    data class UpdateDate(val date: LocalDate) : AnimalDescriptionUserIntent

    data class UpdateSpecies(val species: String) : AnimalDescriptionUserIntent

    data class UpdateRace(val race: String) : AnimalDescriptionUserIntent

    data class UpdateGender(val gender: AnimalGender) : AnimalDescriptionUserIntent

    data class UpdateAge(val age: String) : AnimalDescriptionUserIntent

    data object RequestGpsPosition : AnimalDescriptionUserIntent

    data class UpdateLatitude(val latitude: String) : AnimalDescriptionUserIntent

    data class UpdateLongitude(val longitude: String) : AnimalDescriptionUserIntent

    data class UpdateDescription(val description: String) : AnimalDescriptionUserIntent

    data object ContinueClicked : AnimalDescriptionUserIntent

    data object BackClicked : AnimalDescriptionUserIntent

    data object OpenDatePicker : AnimalDescriptionUserIntent

    data object DismissDatePicker : AnimalDescriptionUserIntent
}
