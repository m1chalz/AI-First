package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.domain.usecases.SubmitAnnouncementUseCase
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
 * - Orchestrates 2-step submission via SubmitAnnouncementUseCase
 * - Syncs data with ReportMissingFlowState for cross-screen persistence
 * - Emits one-off effects (navigation, snackbars)
 */
class OwnerDetailsViewModel(
    private val flowState: ReportMissingFlowState,
    private val submitAnnouncementUseCase: SubmitAnnouncementUseCase? = null,
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
        // Prevent double submission
        if (_state.value.isSubmitting) return

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

        // Validation passed - start submission
        submitAnnouncement()
    }

    private fun submitAnnouncement() {
        // Guard: no use case means we're in test mode without submission
        val useCase = submitAnnouncementUseCase ?: return

        _state.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val result = useCase(flowState.data.value)

            _state.update { it.copy(isSubmitting = false) }

            result.fold(
                onSuccess = { managementPassword ->
                    _effects.emit(OwnerDetailsUiEffect.NavigateToSummary(managementPassword))
                },
                onFailure = {
                    _effects.emit(
                        OwnerDetailsUiEffect.ShowSnackbar(
                            message = "Something went wrong. Please try again.",
                            actionLabel = "Retry",
                        ),
                    )
                },
            )
        }
    }

    private fun handleBackClicked() {
        viewModelScope.launch {
            _effects.emit(OwnerDetailsUiEffect.NavigateBack)
        }
    }

    private fun handleRetryClicked() {
        // Retry submission directly (validation already passed)
        submitAnnouncement()
    }
}

