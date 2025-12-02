package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
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
 * Unit tests for ChipNumberViewModel.
 * Tests screen-specific MVI behavior: initial state, intent handling, and effect emissions.
 * Follows Given-When-Then structure per project constitution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChipNumberViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sharedViewModel: ReportMissingViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sharedViewModel = ReportMissingViewModel()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========================================
    // T010: Initial State Tests
    // ========================================

    @Test
    fun `initial state should have empty chip number when shared state is empty`() =
        runTest {
            // Given - shared ViewModel with empty state

            // When
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("", currentState.chipNumber, "Initial chip number should be empty")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial state should load chip number from shared state when available`() =
        runTest {
            // Given - shared ViewModel with existing chip number
            sharedViewModel.dispatchIntent(
                com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent.UpdateChipNumber(
                    "123456789012345"
                )
            )
            advanceUntilIdle()

            // When
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    "123456789012345",
                    currentState.chipNumber,
                    "Should load chip number from shared state"
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // T011: UpdateChipNumber Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateChipNumber should update local state with digits only`() =
        runTest {
            // Given
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // When - input with non-digits
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123-456-789"))
            advanceUntilIdle()

            // Then - should extract digits only
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("123456789", currentState.chipNumber, "Should extract digits only")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateChipNumber should limit to 15 digits`() =
        runTest {
            // Given
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // When - input exceeds 15 digits
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("12345678901234567890"))
            advanceUntilIdle()

            // Then - should limit to 15 digits
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(15, currentState.chipNumber.length, "Should limit to 15 digits")
                assertEquals("123456789012345", currentState.chipNumber)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateChipNumber should accept partial input`() =
        runTest {
            // Given
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // When - partial input
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("12345"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("12345", currentState.chipNumber)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateChipNumber should handle empty input`() =
        runTest {
            // Given - ViewModel with existing chip number
            val viewModel = ChipNumberViewModel(sharedViewModel)
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("12345"))
            advanceUntilIdle()

            // When - clear input
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber(""))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("", currentState.chipNumber, "Should accept empty input")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // T012: ContinueClicked Intent Tests
    // ========================================

    @Test
    fun `handleIntent ContinueClicked should save chip number to shared state`() =
        runTest {
            // Given
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789012345"))
            advanceUntilIdle()

            // Verify local state is updated first
            assertEquals("123456789012345", viewModel.state.value.chipNumber)

            // When
            viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Allow all coroutines to complete
            testScheduler.advanceUntilIdle()

            // Then - verify shared state was updated
            assertEquals(
                "123456789012345",
                sharedViewModel.state.value.chipNumber,
                "Should save chip number to shared state"
            )
        }

    @Test
    fun `handleIntent ContinueClicked should emit NavigateToPhoto effect`() =
        runTest {
            // Given
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(
                    effect is ChipNumberUiEffect.NavigateToPhoto,
                    "Should emit NavigateToPhoto effect"
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent ContinueClicked with empty chip number should still navigate`() =
        runTest {
            // Given - no chip number entered (optional field)
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
                advanceUntilIdle()

                // Then - should still emit navigation effect
                val effect = awaitItem()
                assertTrue(
                    effect is ChipNumberUiEffect.NavigateToPhoto,
                    "Should navigate even with empty chip number"
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // BackClicked Tests (covered in Phase 5)
    // ========================================

    @Test
    fun `handleIntent BackClicked should emit NavigateBack effect`() =
        runTest {
            // Given
            val viewModel = ChipNumberViewModel(sharedViewModel)
            advanceUntilIdle()

            // When
            viewModel.effects.test {
                viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(
                    effect is ChipNumberUiEffect.NavigateBack,
                    "Should emit NavigateBack effect"
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent BackClicked should NOT save chip number to shared state`() =
        runTest {
            // Given - local chip number entered but not saved
            val viewModel = ChipNumberViewModel(sharedViewModel)
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789"))
            advanceUntilIdle()

            // When - back clicked without Continue
            viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
            advanceUntilIdle()

            // Then - shared state should remain empty
            sharedViewModel.state.test {
                val sharedState = awaitItem()
                assertEquals(
                    "",
                    sharedState.chipNumber,
                    "Should NOT save chip number on back navigation"
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
}

