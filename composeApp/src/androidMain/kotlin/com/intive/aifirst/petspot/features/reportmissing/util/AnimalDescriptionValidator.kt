package com.intive.aifirst.petspot.features.reportmissing.util

import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiState

/**
 * Result of validating the animal description form.
 *
 * @property isValid True if all required fields are valid
 * @property speciesError Error message for species field (null if valid)
 * @property raceError Error message for race field (null if valid)
 * @property genderError Error message for gender field (null if valid)
 * @property ageError Error message for age field (null if valid)
 * @property latitudeError Error message for latitude field (null if valid)
 * @property longitudeError Error message for longitude field (null if valid)
 */
data class ValidationResult(
    val isValid: Boolean,
    val speciesError: String? = null,
    val raceError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val latitudeError: String? = null,
    val longitudeError: String? = null,
)

/**
 * Validates animal description form fields.
 * Validation occurs on submit per spec requirement.
 */
object AnimalDescriptionValidator {
    private const val MIN_AGE = 0
    private const val MAX_AGE = 40
    private const val MIN_LATITUDE = -90.0
    private const val MAX_LATITUDE = 90.0
    private const val MIN_LONGITUDE = -180.0
    private const val MAX_LONGITUDE = 180.0

    /**
     * Validates all form fields and returns a comprehensive result.
     *
     * Required fields: species, race (if species selected), gender
     * Optional fields: age (0-40 if provided), latitude (-90 to 90), longitude (-180 to 180)
     */
    fun validate(state: AnimalDescriptionUiState): ValidationResult {
        val speciesError = validateSpecies(state.animalSpecies)
        val raceError = validateRace(state.animalRace, state.animalSpecies)
        val genderError = validateGender(state.animalGender)
        val ageError = validateAge(state.animalAge)
        val latitudeError = validateLatitude(state.latitude)
        val longitudeError = validateLongitude(state.longitude)

        return ValidationResult(
            isValid =
                speciesError == null &&
                    raceError == null &&
                    genderError == null &&
                    ageError == null &&
                    latitudeError == null &&
                    longitudeError == null,
            speciesError = speciesError,
            raceError = raceError,
            genderError = genderError,
            ageError = ageError,
            latitudeError = latitudeError,
            longitudeError = longitudeError,
        )
    }

    private fun validateSpecies(species: String): String? = if (species.isBlank()) "This field cannot be empty" else null

    private fun validateRace(
        race: String,
        species: String,
    ): String? = if (species.isNotBlank() && race.isBlank()) "This field cannot be empty" else null

    private fun validateGender(gender: Any?): String? = if (gender == null) "This field cannot be empty" else null

    private fun validateAge(age: String): String? {
        if (age.isBlank()) return null

        val ageInt = age.toIntOrNull()
        return when {
            ageInt == null -> "Age must be a number"
            ageInt < MIN_AGE -> "Age cannot be negative"
            ageInt > MAX_AGE -> "Age must be $MAX_AGE or less"
            else -> null
        }
    }

    private fun validateLatitude(latitude: String): String? {
        if (latitude.isBlank()) return null

        val latDouble = latitude.toDoubleOrNull()
        return when {
            latDouble == null -> "Invalid latitude format"
            latDouble !in MIN_LATITUDE..MAX_LATITUDE ->
                "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE"
            else -> null
        }
    }

    private fun validateLongitude(longitude: String): String? {
        if (longitude.isBlank()) return null

        val lonDouble = longitude.toDoubleOrNull()
        return when {
            lonDouble == null -> "Invalid longitude format"
            lonDouble !in MIN_LONGITUDE..MAX_LONGITUDE ->
                "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE"
            else -> null
        }
    }
}
