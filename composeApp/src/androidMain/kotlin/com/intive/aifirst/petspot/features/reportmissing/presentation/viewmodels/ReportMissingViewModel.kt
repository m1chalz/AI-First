package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.FlowStep
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingReducer
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Missing Pet Report flow following MVI architecture.
 * Manages UI state, processes user intents, and emits one-off effects.
 * Shared across all 5 screens via nav graph scoping.
 *
 * State flow:
 * Intent → ViewModel → Reducer → State → UI
 *
 * Effects (one-off events like navigation) emitted via SharedFlow.
 */
class ReportMissingViewModel : ViewModel() {
    // State
    private val _state = MutableStateFlow(ReportMissingUiState.Initial)
    val state: StateFlow<ReportMissingUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = MutableSharedFlow<ReportMissingEffect>()
    val effects: SharedFlow<ReportMissingEffect> = _effects.asSharedFlow()

    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: ReportMissingIntent) {
        when (intent) {
            is ReportMissingIntent.NavigateNext -> handleNavigateNext()
            is ReportMissingIntent.NavigateBack -> handleNavigateBack()
            is ReportMissingIntent.Submit -> handleSubmit()
            else -> {
                // State-modifying intents are processed by reducer
                _state.value = ReportMissingReducer.reduce(_state.value, intent)
            }
        }
    }

    /**
     * Handles NavigateNext intent: emits navigation effect to next step.
     */
    private fun handleNavigateNext() {
        viewModelScope.launch {
            val nextStep = ReportMissingReducer.nextStep(_state.value.currentStep)
            if (nextStep != null) {
                _effects.emit(ReportMissingEffect.NavigateToStep(nextStep))
            }
        }
    }

    /**
     * Handles NavigateBack intent: emits navigation effect.
     * Navigation Component handles step-aware behavior automatically.
     */
    private fun handleNavigateBack() {
        viewModelScope.launch {
            _effects.emit(ReportMissingEffect.NavigateBack)
        }
    }

    /**
     * Handles Submit intent: placeholder for future backend integration.
     * For now, emits NavigateBack to exit the wizard (popBackStack exits nested graph).
     */
    private fun handleSubmit() {
        viewModelScope.launch {
            // TODO: Add backend submission in future feature
            _effects.emit(ReportMissingEffect.NavigateBack)
        }
    }

    /**
     * Updates the current step in state.
     * Called when navigation occurs to keep state in sync.
     */
    fun updateCurrentStep(step: FlowStep) {
        _state.value = ReportMissingReducer.updateStep(_state.value, step)
    }
}
