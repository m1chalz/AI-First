package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.FlowStep
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
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
import kotlin.test.assertTrue

/**
 * Unit tests for ReportMissingViewModel.
 * Tests intent handling, state transitions, and effect emissions using Turbine.
 * Follows Given-When-Then structure per project constitution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReportMissingViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========================================
    // State Update Tests
    // ========================================

    @Test
    fun `dispatchIntent UpdateChipNumber should update chip number in state`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()

            // When
            viewModel.dispatchIntent(ReportMissingIntent.UpdateChipNumber("123456789"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("123456789", currentState.chipNumber, "Chip number should be updated")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent UpdatePhotoUri should update photo URI in state`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()

            // When
            viewModel.dispatchIntent(ReportMissingIntent.UpdatePhotoUri("content://photo/1"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    "content://photo/1",
                    currentState.photoAttachment.uri,
                    "Photo URI should be updated",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent UpdateDescription should update description in state`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()

            // When
            viewModel.dispatchIntent(ReportMissingIntent.UpdateDescription("Small brown dog"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Small brown dog", currentState.description, "Description should be updated")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent UpdateContactEmail should update contact email in state`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()

            // When
            viewModel.dispatchIntent(ReportMissingIntent.UpdateContactEmail("owner@example.com"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    "owner@example.com",
                    currentState.contactEmail,
                    "Contact email should be updated",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent UpdateContactPhone should update contact phone in state`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()

            // When
            viewModel.dispatchIntent(ReportMissingIntent.UpdateContactPhone("+1234567890"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("+1234567890", currentState.contactPhone, "Contact phone should be updated")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // Navigation Effect Tests
    // ========================================

    @Test
    fun `dispatchIntent NavigateNext should emit NavigateToStep effect with next step`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()
            advanceUntilIdle()

            // When - on CHIP_NUMBER step
            viewModel.effects.test {
                viewModel.dispatchIntent(ReportMissingIntent.NavigateNext)
                advanceUntilIdle()

                // Then - should emit NavigateToStep(PHOTO)
                val effect = awaitItem()
                assertTrue(
                    effect is ReportMissingEffect.NavigateToStep,
                    "Should emit NavigateToStep effect",
                )
                assertEquals(
                    FlowStep.PHOTO,
                    (effect as ReportMissingEffect.NavigateToStep).step,
                    "Next step from CHIP_NUMBER should be PHOTO",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent NavigateBack should emit NavigateBack effect`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.dispatchIntent(ReportMissingIntent.NavigateBack)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(
                    effect is ReportMissingEffect.NavigateBack,
                    "Should emit NavigateBack effect",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent Submit should emit ExitFlow effect to exit flow`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.dispatchIntent(ReportMissingIntent.Submit)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(
                    effect is ReportMissingEffect.ExitFlow,
                    "Should emit ExitFlow effect on submit (exits nested graph)",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // Step Navigation Flow Tests
    // ========================================

    @Test
    fun `NavigateNext from PHOTO step should emit NavigateToStep DESCRIPTION`() =
        runTest {
            // Given - ViewModel with state on PHOTO step
            val viewModel = ReportMissingViewModel()
            viewModel.updateCurrentStep(FlowStep.PHOTO)
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.dispatchIntent(ReportMissingIntent.NavigateNext)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is ReportMissingEffect.NavigateToStep)
                assertEquals(
                    FlowStep.DESCRIPTION,
                    (effect as ReportMissingEffect.NavigateToStep).step,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `NavigateNext from CONTACT_DETAILS step should emit NavigateToStep SUMMARY`() =
        runTest {
            // Given - ViewModel with state on CONTACT_DETAILS step
            val viewModel = ReportMissingViewModel()
            viewModel.updateCurrentStep(FlowStep.CONTACT_DETAILS)
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.dispatchIntent(ReportMissingIntent.NavigateNext)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is ReportMissingEffect.NavigateToStep)
                assertEquals(FlowStep.SUMMARY, (effect as ReportMissingEffect.NavigateToStep).step)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `NavigateNext from SUMMARY step should not emit any effect`() =
        runTest {
            // Given - ViewModel with state on SUMMARY step (last step)
            val viewModel = ReportMissingViewModel()
            viewModel.updateCurrentStep(FlowStep.SUMMARY)
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.dispatchIntent(ReportMissingIntent.NavigateNext)
                advanceUntilIdle()

                // Then - No effect should be emitted (already at last step)
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // updateCurrentStep Tests
    // ========================================

    @Test
    fun `updateCurrentStep should update current step in state`() =
        runTest {
            // Given
            val viewModel = ReportMissingViewModel()

            // When
            viewModel.updateCurrentStep(FlowStep.DESCRIPTION)
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    FlowStep.DESCRIPTION,
                    currentState.currentStep,
                    "Current step should be updated",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // Initial State Tests
    // ========================================

    @Test
    fun `initial state should have CHIP_NUMBER as current step`() =
        runTest {
            // Given / When
            val viewModel = ReportMissingViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    FlowStep.CHIP_NUMBER,
                    currentState.currentStep,
                    "Initial step should be CHIP_NUMBER",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
}
