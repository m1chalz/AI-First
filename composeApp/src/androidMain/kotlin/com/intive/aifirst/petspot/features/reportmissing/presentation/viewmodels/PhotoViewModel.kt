package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.domain.usecases.ExtractPhotoMetadataUseCase
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoAttachmentState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoStatus
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Photo screen (Step 2/4) following MVI architecture.
 * Uses hybrid pattern: shared state holder for data, callbacks for navigation.
 *
 * @param extractPhotoMetadataUseCase Use case for extracting photo metadata
 * @param flowState Shared state holder for flow data persistence
 * @param onNavigateToDescription Callback when user taps Continue (navigation event)
 * @param onNavigateBack Callback when user taps Back (navigation event)
 */
class PhotoViewModel(
    private val extractPhotoMetadataUseCase: ExtractPhotoMetadataUseCase,
    private val flowState: ReportMissingFlowState,
    private val onNavigateToDescription: () -> Unit,
    private val onNavigateBack: () -> Unit,
) : ViewModel() {
    // Screen-specific state
    private val _state = MutableStateFlow(PhotoAttachmentState.Empty)
    val state: StateFlow<PhotoAttachmentState> = _state.asStateFlow()

    // One-off effects (photo picker launch, toasts)
    private val _effects = MutableSharedFlow<PhotoEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<PhotoEffect> = _effects.asSharedFlow()

    init {
        // Initialize from shared flow state (for data persistence on back navigation)
        viewModelScope.launch {
            val flowData = flowState.data.value
            if (flowData.photoUri != null) {
                _state.value =
                    PhotoAttachmentState(
                        uri = flowData.photoUri,
                        filename = flowData.photoFilename,
                        sizeBytes = flowData.photoSizeBytes,
                        status = PhotoStatus.CONFIRMED,
                    )
            }
        }
    }

    /**
     * Processes user intents and updates state or triggers effects.
     */
    fun handleIntent(intent: ReportMissingIntent) {
        when (intent) {
            is ReportMissingIntent.OpenPhotoPicker -> handleOpenPhotoPicker()
            is ReportMissingIntent.PhotoSelected -> handlePhotoSelected(intent.uri)
            is ReportMissingIntent.PhotoMetadataLoaded ->
                handlePhotoMetadataLoaded(
                    intent.uri,
                    intent.filename,
                    intent.sizeBytes,
                )
            is ReportMissingIntent.PhotoLoadFailed -> handlePhotoLoadFailed()
            is ReportMissingIntent.RemovePhoto -> handleRemovePhoto()
            is ReportMissingIntent.PhotoPickerCancelled -> handlePhotoPickerCancelled()
            is ReportMissingIntent.NavigateNext -> handleContinueClicked()
            is ReportMissingIntent.NavigateBack -> handleBackClicked()
            else -> {
                // Ignore other intents
            }
        }
    }

    /**
     * Emits effect to launch photo picker.
     */
    private fun handleOpenPhotoPicker() {
        viewModelScope.launch {
            _effects.emit(PhotoEffect.LaunchPhotoPicker)
        }
    }

    /**
     * Sets loading state and extracts metadata from URI.
     */
    private fun handlePhotoSelected(uri: String) {
        // Set loading state
        _state.value =
            PhotoAttachmentState(
                uri = uri,
                status = PhotoStatus.LOADING,
            )

        // Extract metadata in background
        viewModelScope.launch {
            try {
                val (filename, sizeBytes) = extractMetadata(uri)
                handlePhotoMetadataLoaded(uri, filename, sizeBytes)
            } catch (e: Exception) {
                handlePhotoLoadFailed()
            }
        }
    }

    /**
     * Updates state to CONFIRMED with photo metadata.
     */
    private fun handlePhotoMetadataLoaded(
        uri: String,
        filename: String,
        sizeBytes: Long,
    ) {
        _state.value =
            PhotoAttachmentState(
                uri = uri,
                filename = filename,
                sizeBytes = sizeBytes,
                status = PhotoStatus.CONFIRMED,
            )
    }

    /**
     * Resets state to EMPTY on load failure and shows toast.
     */
    private fun handlePhotoLoadFailed() {
        _state.value = PhotoAttachmentState.Empty
        viewModelScope.launch {
            _effects.emit(PhotoEffect.ShowMetadataFailedToast)
        }
    }

    /**
     * Clears photo selection.
     */
    private fun handleRemovePhoto() {
        _state.value = PhotoAttachmentState.Empty
        flowState.clearPhoto()
    }

    /**
     * No-op when picker is cancelled.
     */
    private fun handlePhotoPickerCancelled() {
        // Keep current state
    }

    /**
     * Validates photo selection and navigates to next step.
     * Shows toast if no photo selected.
     */
    private fun handleContinueClicked() {
        if (_state.value.hasPhoto) {
            savePhotoToFlowState()
            onNavigateToDescription()
        } else {
            viewModelScope.launch {
                _effects.emit(PhotoEffect.ShowPhotoMandatoryToast)
            }
        }
    }

    /**
     * Navigates back without validation (photo can be empty).
     */
    private fun handleBackClicked() {
        if (_state.value.hasPhoto) {
            savePhotoToFlowState()
        }
        onNavigateBack()
    }

    /**
     * Persists current photo state to shared flow state for cross-screen data retention.
     */
    private fun savePhotoToFlowState() {
        with(_state.value) {
            flowState.updatePhoto(uri, filename, sizeBytes)
        }
    }

    /**
     * Extracts filename and size from content URI using use case.
     */
    private suspend fun extractMetadata(uri: String): Pair<String, Long> {
        return extractPhotoMetadataUseCase(uri)
    }
}
