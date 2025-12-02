package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
import com.intive.aifirst.petspot.features.reportmissing.util.MicrochipNumberFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Chip Number screen (Step 1/4) following MVI architecture.
 * Manages screen-specific state and coordinates with shared flow ViewModel.
 *
 * @param sharedViewModel NavGraph-scoped ViewModel for flow state persistence
 */
class ChipNumberViewModel(
    private val sharedViewModel: ReportMissingViewModel,
) : ViewModel() {

    // Screen-specific state
    private val _state = MutableStateFlow(ChipNumberUiState.Initial)
    val state: StateFlow<ChipNumberUiState> = _state.asStateFlow()

    // One-off navigation effects
    private val _effects = MutableSharedFlow<ChipNumberUiEffect>()
    val effects: SharedFlow<ChipNumberUiEffect> = _effects.asSharedFlow()

    init {
        // Initialize from shared flow state (for data persistence on back navigation)
        viewModelScope.launch {
            val sharedState = sharedViewModel.state.value
            _state.value = ChipNumberUiState(chipNumber = sharedState.chipNumber)
        }
    }

    /**
     * Processes user intents and updates state or emits effects accordingly.
     */
    fun handleIntent(intent: ChipNumberUserIntent) {
        when (intent) {
            is ChipNumberUserIntent.UpdateChipNumber -> handleUpdateChipNumber(intent.value)
            is ChipNumberUserIntent.ContinueClicked -> handleContinueClicked()
            is ChipNumberUserIntent.BackClicked -> handleBackClicked()
        }
    }

    /**
     * Updates local state with extracted digits (max 15).
     * Does NOT save to shared state until Continue is clicked.
     */
    private fun handleUpdateChipNumber(value: String) {
        val digits = MicrochipNumberFormatter.extractDigits(value)
            .take(MicrochipNumberFormatter.MAX_DIGITS)
        _state.value = _state.value.copy(chipNumber = digits)
    }

    /**
     * Saves chip number to shared flow state and navigates to Photo screen.
     */
    private fun handleContinueClicked() {
        viewModelScope.launch {
            // Save to shared state
            sharedViewModel.dispatchIntent(
                ReportMissingIntent.UpdateChipNumber(_state.value.chipNumber)
            )
            // Navigate to next screen
            _effects.emit(ChipNumberUiEffect.NavigateToPhoto)
        }
    }

    /**
     * Navigates back without saving chip number.
     * Local state is discarded; shared state remains unchanged.
     */
    private fun handleBackClicked() {
        viewModelScope.launch {
            // Do NOT save to shared state - just emit navigation effect
            _effects.emit(ChipNumberUiEffect.NavigateBack)
        }
    }
}

