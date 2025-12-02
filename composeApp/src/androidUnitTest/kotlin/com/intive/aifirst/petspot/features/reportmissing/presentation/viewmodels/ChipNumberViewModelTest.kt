package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
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
 * Unit tests for ChipNumberViewModel.
 * Tests screen-specific MVI behavior with hybrid pattern: FlowState + callbacks.
 * Follows Given-When-Then structure per project constitution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChipNumberViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var flowState: ReportMissingFlowState

    // Track callback invocations
    private var navigateToPhotoCalled = false
    private var exitFlowCalled = false

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        flowState = ReportMissingFlowState()
        navigateToPhotoCalled = false
        exitFlowCalled = false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        ChipNumberViewModel(
            flowState = flowState,
            onNavigateToPhoto = { navigateToPhotoCalled = true },
            onExitFlow = { exitFlowCalled = true },
        )

    // ========================================
    // Initial State Tests
    // ========================================

    @Test
    fun `initial state should have empty chip number when flow state is empty`() =
        runTest {
            // Given - flow state with empty data

            // When
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("", currentState.chipNumber, "Initial chip number should be empty")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial state should load chip number from flow state when available`() =
        runTest {
            // Given - flow state with existing chip number
            flowState.updateChipNumber("123456789012345")

            // When
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    "123456789012345",
                    currentState.chipNumber,
                    "Should load chip number from flow state",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateChipNumber Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateChipNumber should update local state with digits only`() =
        runTest {
            // Given
            val viewModel = createViewModel()
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
            val viewModel = createViewModel()
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
            val viewModel = createViewModel()
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
            val viewModel = createViewModel()
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
    // ContinueClicked Intent Tests
    // ========================================

    @Test
    fun `handleIntent ContinueClicked should save chip number to flow state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789012345"))
            advanceUntilIdle()

            // Verify local state is updated first
            assertEquals("123456789012345", viewModel.state.value.chipNumber)

            // When
            viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then - verify flow state was updated
            assertEquals(
                "123456789012345",
                flowState.data.value.chipNumber,
                "Should save chip number to flow state",
            )
        }

    @Test
    fun `handleIntent ContinueClicked should trigger onNavigateToPhoto callback`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then
            assertTrue(navigateToPhotoCalled, "Should trigger onNavigateToPhoto callback")
        }

    @Test
    fun `handleIntent ContinueClicked with empty chip number should still navigate`() =
        runTest {
            // Given - no chip number entered (optional field)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then - should still trigger navigation callback
            assertTrue(navigateToPhotoCalled, "Should navigate even with empty chip number")
        }

    @Test
    fun `handleIntent ContinueClicked with empty chip number should save empty string to flow state`() =
        runTest {
            // Given - no chip number entered (optional field per FR-013)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Verify local state is empty
            assertEquals("", viewModel.state.value.chipNumber)

            // When
            viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then - empty string should be saved to flow state
            assertEquals(
                "",
                flowState.data.value.chipNumber,
                "Empty chip number should be saved to flow state",
            )
        }

    // ========================================
    // BackClicked Tests
    // ========================================

    @Test
    fun `handleIntent BackClicked should trigger onExitFlow callback`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
            advanceUntilIdle()

            // Then
            assertTrue(exitFlowCalled, "Should trigger onExitFlow callback")
        }

    @Test
    fun `handleIntent BackClicked should NOT save chip number to flow state`() =
        runTest {
            // Given - local chip number entered but not saved
            val viewModel = createViewModel()
            viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789"))
            advanceUntilIdle()

            // When - back clicked without Continue
            viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
            advanceUntilIdle()

            // Then - flow state should remain empty
            assertEquals(
                "",
                flowState.data.value.chipNumber,
                "Should NOT save chip number on back navigation",
            )
        }

    @Test
    fun `handleIntent BackClicked should NOT trigger onNavigateToPhoto callback`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
            advanceUntilIdle()

            // Then
            assertFalse(navigateToPhotoCalled, "Should NOT trigger onNavigateToPhoto on back")
            assertTrue(exitFlowCalled, "Should trigger onExitFlow instead")
        }
}
