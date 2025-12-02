package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import com.intive.aifirst.petspot.features.reportmissing.util.MicrochipNumberFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Chip Number screen (Step 1/4) following MVI architecture.
 * Uses hybrid pattern: shared state holder for data, callbacks for navigation.
 *
 * @param flowState Shared state holder for flow data persistence
 * @param onNavigateToPhoto Callback when user taps Continue (navigation event)
 * @param onExitFlow Callback when user taps Back (navigation event)
 */
class ChipNumberViewModel(
    private val flowState: ReportMissingFlowState,
    private val onNavigateToPhoto: () -> Unit,
    private val onExitFlow: () -> Unit,
) : ViewModel() {

    // Screen-specific state
    private val _state = MutableStateFlow(ChipNumberUiState.Initial)
    val state: StateFlow<ChipNumberUiState> = _state.asStateFlow()

    init {
        // Initialize from shared flow state (for data persistence on back navigation)
        viewModelScope.launch {
            val chipNumber = flowState.data.value.chipNumber
            _state.value = ChipNumberUiState(chipNumber = chipNumber)
        }
    }

    /**
     * Processes user intents and updates state or triggers navigation.
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
     * Saves chip number to shared flow state and triggers navigation callback.
     */
    private fun handleContinueClicked() {
        // Save to shared state
        flowState.updateChipNumber(_state.value.chipNumber)
        // Trigger navigation via callback
        onNavigateToPhoto()
    }

    /**
     * Exits flow without saving chip number.
     * Local state is discarded; shared state remains unchanged.
     */
    private fun handleBackClicked() {
        // Do NOT save to shared state - just trigger navigation
        onExitFlow()
    }
}
