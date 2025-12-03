package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import com.intive.aifirst.petspot.features.reportmissing.util.AnimalDescriptionValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for Animal Description screen (Step 3/4) following MVI architecture.
 * Uses hybrid pattern: shared state holder for data, callbacks for navigation.
 *
 * @param flowState Shared state holder for flow data persistence
 * @param onNavigateToContactDetails Callback when user taps Continue (navigation event)
 * @param onNavigateBack Callback when user taps Back (navigation event)
 */
class AnimalDescriptionViewModel(
    private val flowState: ReportMissingFlowState,
    private val onNavigateToContactDetails: () -> Unit,
    private val onNavigateBack: () -> Unit,
) : ViewModel() {
    private val _state = MutableStateFlow(AnimalDescriptionUiState.Initial)
    val state: StateFlow<AnimalDescriptionUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AnimalDescriptionUiEffect>()
    val effects: SharedFlow<AnimalDescriptionUiEffect> = _effects.asSharedFlow()

    init {
        // Initialize from shared flow state (for data persistence on back navigation)
        viewModelScope.launch {
            val data = flowState.data.value
            _state.value = AnimalDescriptionUiState(
                disappearanceDate = data.disappearanceDate ?: LocalDate.now(),
                petName = data.petName,
                animalSpecies = data.animalSpecies,
                animalRace = data.animalRace,
                animalGender = data.animalGender,
                animalAge = data.animalAge?.toString() ?: "",
                latitude = data.latitude?.toString() ?: "",
                longitude = data.longitude?.toString() ?: "",
                additionalDescription = data.additionalDescription,
            )
        }
    }

    /** Processes user intents and updates state or triggers effects. */
    fun handleIntent(intent: AnimalDescriptionUserIntent) {
        when (intent) {
            is AnimalDescriptionUserIntent.UpdatePetName -> handleUpdatePetName(intent.name)
            is AnimalDescriptionUserIntent.UpdateDate -> handleUpdateDate(intent.date)
            is AnimalDescriptionUserIntent.UpdateSpecies -> handleUpdateSpecies(intent.species)
            is AnimalDescriptionUserIntent.UpdateRace -> handleUpdateRace(intent.race)
            is AnimalDescriptionUserIntent.UpdateGender -> handleUpdateGender(intent.gender)
            is AnimalDescriptionUserIntent.UpdateAge -> handleUpdateAge(intent.age)
            is AnimalDescriptionUserIntent.UpdateLatitude -> handleUpdateLatitude(intent.latitude)
            is AnimalDescriptionUserIntent.UpdateLongitude -> handleUpdateLongitude(intent.longitude)
            is AnimalDescriptionUserIntent.UpdateDescription -> handleUpdateDescription(intent.description)
            is AnimalDescriptionUserIntent.RequestGpsPosition -> handleRequestGpsPosition()
            is AnimalDescriptionUserIntent.ContinueClicked -> handleContinueClicked()
            is AnimalDescriptionUserIntent.BackClicked -> handleBackClicked()
            is AnimalDescriptionUserIntent.OpenDatePicker -> handleOpenDatePicker()
            is AnimalDescriptionUserIntent.DismissDatePicker -> handleDismissDatePicker()
        }
    }

    private fun handleUpdatePetName(name: String) {
        _state.value = _state.value.copy(petName = name)
    }

    private fun handleUpdateDate(date: LocalDate) {
        _state.value = _state.value.copy(
            disappearanceDate = date,
            isDatePickerVisible = false,
        )
    }

    private fun handleUpdateSpecies(species: String) {
        val currentState = _state.value
        // Clear race when species changes (race depends on species)
        val newRace = if (species != currentState.animalSpecies) "" else currentState.animalRace
        _state.value = currentState.copy(
            animalSpecies = species,
            animalRace = newRace,
            speciesError = null,
        )
    }

    private fun handleUpdateRace(race: String) {
        _state.value = _state.value.copy(
            animalRace = race,
            raceError = null,
        )
    }

    private fun handleUpdateGender(gender: AnimalGender) {
        _state.value = _state.value.copy(
            animalGender = gender,
            genderError = null,
        )
    }

    private fun handleUpdateAge(age: String) {
        // Only allow digits
        val digits = age.filter { it.isDigit() }
        _state.value = _state.value.copy(
            animalAge = digits,
            ageError = null,
        )
    }

    private fun handleUpdateLatitude(latitude: String) {
        _state.value = _state.value.copy(
            latitude = latitude,
            latitudeError = null,
        )
    }

    private fun handleUpdateLongitude(longitude: String) {
        _state.value = _state.value.copy(
            longitude = longitude,
            longitudeError = null,
        )
    }

    private fun handleUpdateDescription(description: String) {
        // Enforce max character limit
        val truncated = description.take(_state.value.descriptionMaxChars)
        _state.value = _state.value.copy(additionalDescription = truncated)
    }

    private fun handleRequestGpsPosition() {
        // GPS handling will be implemented in Phase 4 (US2)
        // For now, just show loading state briefly
        _state.value = _state.value.copy(isGpsLoading = true)
        viewModelScope.launch {
            // Placeholder - will be replaced with actual GPS request in Phase 4
            _state.value = _state.value.copy(isGpsLoading = false)
        }
    }

    private fun handleOpenDatePicker() {
        _state.value = _state.value.copy(isDatePickerVisible = true)
    }

    private fun handleDismissDatePicker() {
        _state.value = _state.value.copy(isDatePickerVisible = false)
    }

    private fun handleContinueClicked() {
        val validation = AnimalDescriptionValidator.validate(_state.value)

        if (!validation.isValid) {
            // Update state with validation errors
            _state.value = _state.value.copy(
                speciesError = validation.speciesError,
                raceError = validation.raceError,
                genderError = validation.genderError,
                ageError = validation.ageError,
                latitudeError = validation.latitudeError,
                longitudeError = validation.longitudeError,
            )
            // Show validation error snackbar
            viewModelScope.launch {
                _effects.emit(AnimalDescriptionUiEffect.ShowSnackbar("Please correct the errors"))
            }
            return
        }

        // Save to shared flow state
        saveToFlowState()

        // Navigate to next screen
        onNavigateToContactDetails()
    }

    private fun handleBackClicked() {
        // Save current state to flow (preserve on back navigation)
        saveToFlowState()
        onNavigateBack()
    }

    private fun saveToFlowState() {
        val currentState = _state.value
        flowState.updateAnimalDescription(
            disappearanceDate = currentState.disappearanceDate,
            petName = currentState.petName,
            animalSpecies = currentState.animalSpecies,
            animalRace = currentState.animalRace,
            animalGender = currentState.animalGender,
            animalAge = currentState.animalAge.toIntOrNull(),
            latitude = currentState.latitude.toDoubleOrNull(),
            longitude = currentState.longitude.toDoubleOrNull(),
            additionalDescription = currentState.additionalDescription,
        )
    }
}

