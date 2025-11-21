package com.intive.aifirst.petspot.features.animallist.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.domain.usecases.GetAnimalsUseCase
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListReducer
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Animal List screen following MVI architecture.
 * Manages UI state, processes user intents, and emits one-off effects.
 *
 * State flow:
 * Intent → ViewModel → UseCase → Reducer → State → UI
 *
 * Effects (one-off events like navigation) emitted via SharedFlow.
 */
class AnimalListViewModel(
    private val getAnimalsUseCase: GetAnimalsUseCase,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(AnimalListUiState.Initial)
    val state: StateFlow<AnimalListUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = MutableSharedFlow<AnimalListEffect>()
    val effects: SharedFlow<AnimalListEffect> = _effects.asSharedFlow()

    init {
        // Load animals on ViewModel creation
        dispatchIntent(AnimalListIntent.Refresh)
    }

    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: AnimalListIntent) {
        when (intent) {
            is AnimalListIntent.Refresh -> handleRefresh()
            is AnimalListIntent.SelectAnimal -> handleSelectAnimal(intent.id)
            is AnimalListIntent.ReportMissing -> handleReportMissing()
            is AnimalListIntent.ReportFound -> handleReportFound()
        }
    }

    /**
     * Handles Refresh intent: loads animals from repository.
     */
    private fun handleRefresh() {
        viewModelScope.launch {
            // Set loading state
            _state.value = AnimalListReducer.loading(_state.value)

            // Call use case
            val result = runCatching { getAnimalsUseCase() }

            // Reduce result to new state
            _state.value = AnimalListReducer.reduce(_state.value, result)
        }
    }

    /**
     * Handles SelectAnimal intent: emits navigation effect.
     */
    private fun handleSelectAnimal(animalId: String) {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.NavigateToDetails(animalId))
        }
    }

    /**
     * Handles ReportMissing intent: emits navigation effect.
     */
    private fun handleReportMissing() {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.NavigateToReportMissing)
        }
    }

    /**
     * Handles ReportFound intent: emits navigation effect.
     */
    private fun handleReportFound() {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.NavigateToReportFound)
        }
    }
}
