package com.intive.aifirst.petspot.features.reportmissing.presentation.state

import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * Shared state holder for the Report Missing Pet flow.
 * NOT a ViewModel - just a data container with observable state.
 *
 * Holds data that persists across all 5 screens in the flow.
 * Each screen ViewModel reads/writes to this shared state.
 *
 * Lifecycle: Should be scoped to the NavGraph (created when flow starts, cleared when flow ends).
 */
class ReportMissingFlowState {
    private val _data = MutableStateFlow(FlowData())
    val data: StateFlow<FlowData> = _data.asStateFlow()

    /** Update chip number (Step 1/4) */
    fun updateChipNumber(chipNumber: String) {
        _data.update { it.copy(chipNumber = chipNumber) }
    }

    /** Update photo URI (Step 2/4) */
    fun updatePhotoUri(uri: String?) {
        _data.update { it.copy(photoUri = uri) }
    }

    /** Update photo data (Step 2/4) */
    fun updatePhoto(
        uri: String?,
        filename: String?,
        sizeBytes: Long,
    ) {
        _data.update {
            it.copy(
                photoUri = uri,
                photoFilename = filename,
                photoSizeBytes = sizeBytes,
            )
        }
    }

    /** Clear photo data */
    fun clearPhoto() {
        _data.update {
            it.copy(
                photoUri = null,
                photoFilename = null,
                photoSizeBytes = 0,
            )
        }
    }

    /** Update animal description fields (Step 3/4) */
    fun updateAnimalDescription(
        disappearanceDate: LocalDate?,
        petName: String,
        animalSpecies: String,
        animalRace: String,
        animalGender: AnimalGender?,
        animalAge: Int?,
        latitude: Double?,
        longitude: Double?,
        additionalDescription: String,
    ) {
        _data.update {
            it.copy(
                disappearanceDate = disappearanceDate,
                petName = petName,
                animalSpecies = animalSpecies,
                animalRace = animalRace,
                animalGender = animalGender,
                animalAge = animalAge,
                latitude = latitude,
                longitude = longitude,
                additionalDescription = additionalDescription,
            )
        }
    }

    /** Update contact email (Step 4/4) */
    fun updateContactEmail(email: String) {
        _data.update { it.copy(contactEmail = email) }
    }

    /** Update contact phone (Step 4/4) */
    fun updateContactPhone(phone: String) {
        _data.update { it.copy(contactPhone = phone) }
    }

    /** Clear all flow data (when flow is exited or completed) */
    fun clear() {
        _data.value = FlowData()
    }
}

/**
 * Immutable data class holding all flow data.
 * Each field corresponds to a step in the flow.
 */
data class FlowData(
    // Step 1/4 - Chip Number
    val chipNumber: String = "",
    // Step 2/4 - Photo
    val photoUri: String? = null,
    val photoFilename: String? = null,
    val photoSizeBytes: Long = 0,
    // Step 3/4 - Animal Description
    // Note: null means "use today's date" - ViewModel sets initial value to avoid API level issue
    val disappearanceDate: LocalDate? = null,
    val petName: String = "",
    val animalSpecies: String = "",
    val animalRace: String = "",
    val animalGender: AnimalGender? = null,
    val animalAge: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val additionalDescription: String = "",
    // Step 4/4 - Contact Details
    val contactEmail: String = "",
    val contactPhone: String = "",
)
