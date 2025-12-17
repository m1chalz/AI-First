package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetRecentAnimalsUseCase
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect.NavigateToLostPetsList
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect.NavigateToPetDetails
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.LoadData
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.PetClicked
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.RefreshData
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.ViewAllClicked
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserReducer
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Lost Pets Teaser component following MVI architecture.
 * Self-contained and reusable - can be embedded in any screen.
 *
 * State flow:
 * Intent → ViewModel → UseCase → Reducer → State → UI
 *
 * Effects (one-off events like navigation) emitted via Channel.
 */
class LostPetsTeaserViewModel(
    private val getRecentAnimalsUseCase: GetRecentAnimalsUseCase,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(LostPetsTeaserUiState.Initial)
    val state: StateFlow<LostPetsTeaserUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = Channel<LostPetsTeaserEffect>(capacity = Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // Track if data has been loaded (per spec: load once, no auto-refresh)
    private var hasLoadedData = false

    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: LostPetsTeaserIntent) {
        when (intent) {
            is LoadData -> handleLoadData()
            is RefreshData -> loadAnimals()
            is PetClicked -> handlePetClicked(intent.petId)
            is ViewAllClicked -> handleViewAllClicked()
        }
    }

    /** Loads data only on first call (per spec Data Freshness). */
    private fun handleLoadData() {
        if (hasLoadedData) return
        hasLoadedData = true
        loadAnimals()
    }

    /**
     * Handles PetClicked intent: emits navigation effect.
     */
    private fun handlePetClicked(petId: String) {
        viewModelScope.launch {
            _effects.send(NavigateToPetDetails(petId))
        }
    }

    /**
     * Handles ViewAllClicked intent: emits navigation effect.
     */
    private fun handleViewAllClicked() {
        viewModelScope.launch {
            _effects.send(NavigateToLostPetsList)
        }
    }

    /**
     * Loads animals from use case and updates state via reducer.
     */
    private fun loadAnimals() {
        viewModelScope.launch {
            _state.value = LostPetsTeaserReducer.loading(_state.value)

            _state.value =
                runCatching { getRecentAnimalsUseCase() }
                    .fold(
                        onSuccess = { animals ->
                            LostPetsTeaserReducer.success(_state.value, animals)
                        },
                        onFailure = { exception ->
                            LostPetsTeaserReducer.error(
                                _state.value,
                                exception.message ?: "Failed to load lost pets",
                            )
                        },
                    )
        }
    }
}
