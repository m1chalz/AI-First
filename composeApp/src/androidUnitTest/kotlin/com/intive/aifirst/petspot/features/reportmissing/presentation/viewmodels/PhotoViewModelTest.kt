package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.features.reportmissing.domain.usecases.ExtractPhotoMetadataUseCase
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoStatus
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for PhotoViewModel.
 * Tests screen-specific MVI behavior with hybrid pattern: FlowState + callbacks.
 * Follows Given-When-Then structure per project constitution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PhotoViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var extractPhotoMetadataUseCase: ExtractPhotoMetadataUseCase
    private lateinit var flowState: ReportMissingFlowState

    // Track callback invocations
    private var navigateToDescriptionCalled = false
    private var navigateBackCalled = false

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        flowState = ReportMissingFlowState()
        navigateToDescriptionCalled = false
        navigateBackCalled = false

        // Use case with fake repository for testing
        extractPhotoMetadataUseCase =
            ExtractPhotoMetadataUseCase(
                repository = FakePhotoMetadataRepository(),
            )
    }

    /** Fake repository for testing - returns predictable results based on URI */
    private class FakePhotoMetadataRepository :
        com.intive.aifirst.petspot.features.reportmissing.domain.repositories.PhotoMetadataRepository {
        override suspend fun extractMetadata(uri: String): Pair<String, Long> =
            when (uri) {
                "content://photo/1" -> Pair("dog.jpg", 1024L)
                "content://photo/error" -> throw IllegalStateException("Failed to extract metadata")
                else -> Pair("photo.jpg", 2048L)
            }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        PhotoViewModel(
            extractPhotoMetadataUseCase = extractPhotoMetadataUseCase,
            flowState = flowState,
            onNavigateToDescription = { navigateToDescriptionCalled = true },
            onNavigateBack = { navigateBackCalled = true },
        )

    // ========================================
    // Initial State Tests
    // ========================================

    @Test
    fun `initial state should be EMPTY when flow state has no photo`() =
        runTest {
            // Given - flow state with no photo

            // When
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(PhotoStatus.EMPTY, currentState.status)
                assertFalse(currentState.hasPhoto)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial state should load photo from flow state when available`() =
        runTest {
            // Given - flow state with existing photo
            flowState.updatePhoto("content://photo/1", "dog.jpg", 1024)

            // When
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("content://photo/1", currentState.uri)
                assertEquals("dog.jpg", currentState.filename)
                assertEquals(1024L, currentState.sizeBytes)
                assertEquals(PhotoStatus.CONFIRMED, currentState.status)
                assertTrue(currentState.hasPhoto)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // OpenPhotoPicker Intent Tests
    // ========================================

    @Test
    fun `handleIntent OpenPhotoPicker should emit LaunchPhotoPicker effect`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.handleIntent(ReportMissingIntent.OpenPhotoPicker)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertEquals(PhotoEffect.LaunchPhotoPicker, effect)
            }
        }

    // ========================================
    // PhotoSelected Intent Tests
    // ========================================

    @Test
    fun `handleIntent PhotoSelected should set LOADING then CONFIRMED after metadata extraction`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.state.test {
                // Initial state
                val initialState = awaitItem()
                assertEquals(PhotoStatus.EMPTY, initialState.status)

                // Dispatch PhotoSelected
                viewModel.handleIntent(ReportMissingIntent.PhotoSelected("content://photo/1"))

                // Then - should transition to LOADING
                val loadingState = awaitItem()
                assertEquals(PhotoStatus.LOADING, loadingState.status)
                assertEquals("content://photo/1", loadingState.uri)

                // Advance coroutines to complete metadata extraction
                advanceUntilIdle()

                // Then - should transition to CONFIRMED after metadata extraction
                val confirmedState = awaitItem()
                assertEquals(PhotoStatus.CONFIRMED, confirmedState.status)
                assertEquals("content://photo/1", confirmedState.uri)
                assertEquals("dog.jpg", confirmedState.filename)
                assertEquals(1024L, confirmedState.sizeBytes)
                assertTrue(confirmedState.hasPhoto)
            }
        }

    @Test
    fun `handleIntent PhotoSelected with invalid URI should set LOADING then EMPTY on failure`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.state.test {
                skipItems(1) // Skip initial state

                viewModel.handleIntent(ReportMissingIntent.PhotoSelected("content://photo/error"))

                // Then - should transition to LOADING
                val loadingState = awaitItem()
                assertEquals(PhotoStatus.LOADING, loadingState.status)

                // Advance coroutines to complete metadata extraction (will fail)
                advanceUntilIdle()

                // Then - should transition back to EMPTY on failure
                val emptyState = awaitItem()
                assertEquals(PhotoStatus.EMPTY, emptyState.status)
                assertFalse(emptyState.hasPhoto)
            }
        }

    // ========================================
    // RemovePhoto Intent Tests
    // ========================================

    @Test
    fun `handleIntent RemovePhoto should clear state and flow state`() =
        runTest {
            // Given - viewModel with confirmed photo
            val viewModel = createViewModel()
            viewModel.handleIntent(ReportMissingIntent.PhotoSelected("content://photo/1"))
            advanceUntilIdle()

            // Verify photo is confirmed
            assertTrue(viewModel.state.value.hasPhoto)

            // When
            viewModel.handleIntent(ReportMissingIntent.RemovePhoto)
            advanceUntilIdle()

            // Then - state should be empty
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(PhotoStatus.EMPTY, currentState.status)
                assertFalse(currentState.hasPhoto)
                cancelAndIgnoreRemainingEvents()
            }

            // And flow state should be cleared
            assertEquals(null, flowState.data.value.photoUri)
            assertEquals(null, flowState.data.value.photoFilename)
            assertEquals(0, flowState.data.value.photoSizeBytes)
        }

    // ========================================
    // NavigateNext Intent Tests
    // ========================================

    @Test
    fun `handleIntent NavigateNext with photo should save to flow state and navigate`() =
        runTest {
            // Given - viewModel with confirmed photo
            val viewModel = createViewModel()
            viewModel.handleIntent(ReportMissingIntent.PhotoSelected("content://photo/1"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ReportMissingIntent.NavigateNext)
            advanceUntilIdle()

            // Then - flow state should be updated
            assertEquals("content://photo/1", flowState.data.value.photoUri)
            assertEquals("dog.jpg", flowState.data.value.photoFilename)
            assertEquals(1024L, flowState.data.value.photoSizeBytes)

            // And navigation callback should be triggered
            assertTrue(navigateToDescriptionCalled)
        }

    @Test
    fun `handleIntent NavigateNext without photo should emit ShowPhotoMandatoryToast effect`() =
        runTest {
            // Given - no photo selected
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.handleIntent(ReportMissingIntent.NavigateNext)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertEquals(PhotoEffect.ShowPhotoMandatoryToast, effect)
            }

            // And navigation should NOT be triggered
            assertFalse(navigateToDescriptionCalled)
        }

    // ========================================
    // NavigateBack Intent Tests
    // ========================================

    @Test
    fun `handleIntent NavigateBack should save photo if present and trigger callback`() =
        runTest {
            // Given - viewModel with confirmed photo
            val viewModel = createViewModel()
            viewModel.handleIntent(ReportMissingIntent.PhotoSelected("content://photo/1"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ReportMissingIntent.NavigateBack)
            advanceUntilIdle()

            // Then - flow state should be updated
            assertEquals("content://photo/1", flowState.data.value.photoUri)

            // And navigation callback should be triggered
            assertTrue(navigateBackCalled)
        }

    @Test
    fun `handleIntent NavigateBack without photo should trigger callback without saving`() =
        runTest {
            // Given - no photo selected
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ReportMissingIntent.NavigateBack)
            advanceUntilIdle()

            // Then - flow state should remain empty
            assertEquals(null, flowState.data.value.photoUri)

            // And navigation callback should be triggered
            assertTrue(navigateBackCalled)
        }
}
