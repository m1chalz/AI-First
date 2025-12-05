package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import app.cash.turbine.test
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [SummaryViewModel].
 *
 * Tests cover:
 * - US1: Initial state creation with password from flowState
 * - US2: Copy password to clipboard and show Snackbar
 * - US3: Close flow (DismissFlow effect)
 *
 * Note: ClipboardManager is tested with null to avoid Android SDK dependencies.
 * Actual clipboard functionality is verified via E2E tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SummaryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var flowState: ReportMissingFlowState
    private lateinit var viewModel: SummaryViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        flowState = ReportMissingFlowState()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(clipboardManager: ClipboardManager? = null): SummaryViewModel {
        return SummaryViewModel(flowState, clipboardManager)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // US1: Initial State Tests - Password from FlowState
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `should initialize with password from flowState`() = runTest {
        // Given
        val password = "5216577"
        flowState.updateManagementPassword(password)

        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(password, viewModel.state.value.managementPassword)
    }

    @Test
    fun `should initialize with empty string when flowState password is null`() = runTest {
        // Given - flowState has no password set (null)

        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("", viewModel.state.value.managementPassword)
    }

    @Test
    fun `should preserve initial state values`() = runTest {
        // Given
        flowState.updateManagementPassword("1234567")

        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - verify state is properly initialized
        assertEquals("1234567", viewModel.state.value.managementPassword)
    }

    @Test
    fun `should initialize with 7-digit password from flowState`() = runTest {
        // Given - 7 digit password as shown in Figma
        val password = "5216577"
        flowState.updateManagementPassword(password)

        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(7, viewModel.state.value.managementPassword.length)
        assertEquals(password, viewModel.state.value.managementPassword)
    }

    @Test
    fun `should initialize with 6-digit password from flowState`() = runTest {
        // Given - 6 digit password
        val password = "123456"
        flowState.updateManagementPassword(password)

        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(6, viewModel.state.value.managementPassword.length)
        assertEquals(password, viewModel.state.value.managementPassword)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // US2: Copy Password Tests
    // Note: Android 13+ shows system clipboard confirmation, no custom Snackbar needed
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `CopyPasswordClicked should not crash with null clipboardManager`() = runTest {
        // Given
        flowState.updateManagementPassword("5216577")
        viewModel = createViewModel(clipboardManager = null)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - should not crash
        viewModel.dispatchIntent(SummaryUserIntent.CopyPasswordClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - state should remain unchanged
        assertEquals("5216577", viewModel.state.value.managementPassword)
    }

    @Test
    fun `state should remain unchanged after CopyPasswordClicked`() = runTest {
        // Given
        val password = "5216577"
        flowState.updateManagementPassword(password)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val stateBefore = viewModel.state.value

        // When
        viewModel.dispatchIntent(SummaryUserIntent.CopyPasswordClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - state unchanged (feedback is via effect only)
        assertEquals(stateBefore, viewModel.state.value)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // US3: Close Flow Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `CloseClicked should emit DismissFlow effect`() = runTest {
        // Given
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When/Then
        viewModel.effects.test {
            viewModel.dispatchIntent(SummaryUserIntent.CloseClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is SummaryUiEffect.DismissFlow)
        }
    }

    @Test
    fun `CloseClicked should emit DismissFlow regardless of password state`() = runTest {
        // Given - password set
        flowState.updateManagementPassword("5216577")
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When/Then
        viewModel.effects.test {
            viewModel.dispatchIntent(SummaryUserIntent.CloseClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is SummaryUiEffect.DismissFlow)
        }
    }

    @Test
    fun `CloseClicked should emit DismissFlow when password is empty`() = runTest {
        // Given - no password (empty state)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When/Then
        viewModel.effects.test {
            viewModel.dispatchIntent(SummaryUserIntent.CloseClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is SummaryUiEffect.DismissFlow)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Edge Case Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `should handle multiple CopyPasswordClicked intents without crash`() = runTest {
        // Given
        flowState.updateManagementPassword("5216577")
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When - multiple copies should not crash
        viewModel.dispatchIntent(SummaryUserIntent.CopyPasswordClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.dispatchIntent(SummaryUserIntent.CopyPasswordClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - state should remain unchanged
        assertEquals("5216577", viewModel.state.value.managementPassword)
    }

    @Test
    fun `should emit DismissFlow after copy and close sequence`() = runTest {
        // Given
        flowState.updateManagementPassword("5216577")
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When - copy (no effect) then close
        viewModel.dispatchIntent(SummaryUserIntent.CopyPasswordClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.dispatchIntent(SummaryUserIntent.CloseClicked)
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is SummaryUiEffect.DismissFlow)
        }
    }

    @Test
    fun `password in state should match exactly what was set in flowState`() = runTest {
        // Given
        val passwords = listOf("000000", "999999", "123456", "7654321")

        for (password in passwords) {
            // When
            flowState.updateManagementPassword(password)
            val vm = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertEquals(
                password,
                vm.state.value.managementPassword,
                "Password '$password' should match exactly",
            )
        }
    }
}
