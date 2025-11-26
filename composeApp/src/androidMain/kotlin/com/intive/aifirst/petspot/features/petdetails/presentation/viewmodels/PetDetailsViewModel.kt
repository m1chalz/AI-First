package com.intive.aifirst.petspot.features.petdetails.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.domain.usecases.GetAnimalByIdUseCase
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsEffect
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsIntent
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsReducer
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Pet Details screen following MVI architecture.
 * Manages UI state, processes user intents, and emits one-off effects.
 *
 * State flow:
 * Intent → ViewModel → UseCase → Reducer → State → UI
 */
class PetDetailsViewModel(
    private val getAnimalByIdUseCase: GetAnimalByIdUseCase
) : ViewModel() {
    
    // State
    private val _state = MutableStateFlow(PetDetailsUiState.Initial)
    val state: StateFlow<PetDetailsUiState> = _state.asStateFlow()
    
    // Effects (one-off events)
    private val _effects = MutableSharedFlow<PetDetailsEffect>()
    val effects: SharedFlow<PetDetailsEffect> = _effects.asSharedFlow()
    
    // Store current pet ID for retry functionality
    private var currentPetId: String? = null
    
    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: PetDetailsIntent) {
        when (intent) {
            is PetDetailsIntent.LoadPet -> handleLoadPet(intent.id)
            is PetDetailsIntent.NavigateBack -> handleNavigateBack()
            is PetDetailsIntent.ShowOnMap -> handleShowOnMap()
            is PetDetailsIntent.RetryLoad -> handleRetryLoad()
        }
    }
    
    /**
     * Handles LoadPet intent: loads animal details from repository.
     */
    private fun handleLoadPet(id: String) {
        currentPetId = id
        viewModelScope.launch {
            // Set loading state
            _state.value = PetDetailsReducer.loading(_state.value)
            
            // Call use case
            val result = runCatching { getAnimalByIdUseCase(id) }
            
            // Reduce result to new state
            _state.value = PetDetailsReducer.reduce(_state.value, result)
        }
    }
    
    /**
     * Handles NavigateBack intent: emits navigation effect.
     */
    private fun handleNavigateBack() {
        viewModelScope.launch {
            _effects.emit(PetDetailsEffect.NavigateBack)
        }
    }
    
    /**
     * Handles ShowOnMap intent: emits map effect with location if available.
     */
    private fun handleShowOnMap() {
        viewModelScope.launch {
            val location = _state.value.pet?.location
            if (location != null && location.latitude != null && location.longitude != null) {
                _effects.emit(PetDetailsEffect.ShowMap(location))
            } else {
                _effects.emit(PetDetailsEffect.MapNotAvailable)
            }
        }
    }
    
    /**
     * Handles RetryLoad intent: reloads pet data using stored ID.
     */
    private fun handleRetryLoad() {
        currentPetId?.let { id ->
            handleLoadPet(id)
        }
    }
}

