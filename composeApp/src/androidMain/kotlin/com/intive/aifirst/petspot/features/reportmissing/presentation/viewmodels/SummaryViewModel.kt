package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Summary/Report Created Confirmation screen following MVI architecture.
 *
 * Responsibilities:
 * - Reads management password from ReportMissingFlowState on init
 * - Handles copy to clipboard via CopyPasswordClicked intent
 * - Handles flow dismissal via CloseClicked intent
 * - Emits one-off effects (ShowSnackbar, DismissFlow)
 */
class SummaryViewModel(
    private val flowState: ReportMissingFlowState,
    private val clipboardManager: ClipboardManager?,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(SummaryUiState.Initial)
    val state: StateFlow<SummaryUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = MutableSharedFlow<SummaryUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<SummaryUiEffect> = _effects.asSharedFlow()

    init {
        // Initialize from flow state
        viewModelScope.launch {
            val flowData = flowState.data.value
            _state.update {
                it.copy(
                    managementPassword = flowData.managementPassword ?: "",
                )
            }
        }
    }

    /**
     * Processes user intents and updates state or triggers effects.
     */
    fun dispatchIntent(intent: SummaryUserIntent) {
        when (intent) {
            is SummaryUserIntent.CopyPasswordClicked -> handleCopyPasswordClicked()
            is SummaryUserIntent.CloseClicked -> handleCloseClicked()
        }
    }

    private fun handleCopyPasswordClicked() {
        val password = _state.value.managementPassword
        copyToClipboard(password)
        // Note: Android 13+ shows system clipboard confirmation, no custom Snackbar needed
    }

    private fun handleCloseClicked() {
        viewModelScope.launch {
            _effects.emit(SummaryUiEffect.DismissFlow)
        }
    }

    private fun copyToClipboard(text: String) {
        clipboardManager?.let { manager ->
            val clip = ClipData.newPlainText(CLIPBOARD_LABEL, text)
            manager.setPrimaryClip(clip)
        }
    }

    companion object {
        const val CLIPBOARD_LABEL = "Management Code"
    }
}
