package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import com.intive.aifirst.petspot.features.reportmissing.util.OwnerDetailsValidationResult
import com.intive.aifirst.petspot.features.reportmissing.util.OwnerDetailsValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Owner's Details screen (Step 4/4) following MVI architecture.
 *
 * Responsibilities:
 * - Manages UI state (phone, email, reward, validation errors, submission state)
 * - Validates inputs on ContinueClicked
 * - Syncs data with ReportMissingFlowState for cross-screen persistence
 * - Emits one-off effects (navigation, snackbars)
 */
class OwnerDetailsViewModel(
    private val flowState: ReportMissingFlowState,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(OwnerDetailsUiState())
    val state: StateFlow<OwnerDetailsUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = MutableSharedFlow<OwnerDetailsUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<OwnerDetailsUiEffect> = _effects.asSharedFlow()

    init {
        // Initialize from shared flow state (for data persistence on back navigation)
        viewModelScope.launch {
            val flowData = flowState.data.value
            _state.update {
                it.copy(
                    phone = flowData.contactPhone,
                    email = flowData.contactEmail,
                    reward = flowData.rewardDescription,
                )
            }
        }
    }

    /**
     * Processes user intents and updates state or triggers effects.
     */
    fun dispatchIntent(intent: OwnerDetailsUserIntent) {
        when (intent) {
            is OwnerDetailsUserIntent.UpdatePhone -> handleUpdatePhone(intent.phone)
            is OwnerDetailsUserIntent.UpdateEmail -> handleUpdateEmail(intent.email)
            is OwnerDetailsUserIntent.UpdateReward -> handleUpdateReward(intent.reward)
            is OwnerDetailsUserIntent.ContinueClicked -> handleContinueClicked()
            is OwnerDetailsUserIntent.BackClicked -> handleBackClicked()
            is OwnerDetailsUserIntent.RetryClicked -> handleRetryClicked()
        }
    }

    private fun handleUpdatePhone(phone: String) {
        _state.update { it.copy(phone = phone, phoneError = null) }
        flowState.updateContactPhone(phone)
    }

    private fun handleUpdateEmail(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
        flowState.updateContactEmail(email)
    }

    private fun handleUpdateReward(reward: String) {
        // Truncate at max length
        val truncated = reward.take(_state.value.rewardMaxLength)
        _state.update { it.copy(reward = truncated) }
        flowState.updateRewardDescription(truncated)
    }

    private fun handleContinueClicked() {
        // Validate inputs
        val phoneResult = OwnerDetailsValidator.validatePhone(_state.value.phone)
        val emailResult = OwnerDetailsValidator.validateEmail(_state.value.email)

        val phoneError = (phoneResult as? OwnerDetailsValidationResult.Invalid)?.message
        val emailError = (emailResult as? OwnerDetailsValidationResult.Invalid)?.message

        // Update state with validation errors
        _state.update {
            it.copy(
                phoneError = phoneError,
                emailError = emailError,
            )
        }

        // If validation fails, show snackbar and stop
        if (phoneError != null || emailError != null) {
            viewModelScope.launch {
                _effects.emit(
                    OwnerDetailsUiEffect.ShowSnackbar(
                        message = "Please fix the errors above",
                    ),
                )
            }
            return
        }

        // Validation passed - Phase 4 will add submission logic here
        // For now, just emit navigation effect (placeholder for Phase 4)
        viewModelScope.launch {
            // TODO: Phase 4 will add: call SubmitAnnouncementUseCase here
            // For Phase 3, we just validate - submission will be added in Phase 4
        }
    }

    private fun handleBackClicked() {
        viewModelScope.launch {
            _effects.emit(OwnerDetailsUiEffect.NavigateBack)
        }
    }

    private fun handleRetryClicked() {
        // Phase 5 will implement retry logic
        handleContinueClicked()
    }
}

