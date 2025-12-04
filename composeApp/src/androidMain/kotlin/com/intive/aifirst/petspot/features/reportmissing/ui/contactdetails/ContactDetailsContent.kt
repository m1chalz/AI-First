package com.intive.aifirst.petspot.features.reportmissing.ui.contactdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUiState
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ContinueButton
import com.intive.aifirst.petspot.features.reportmissing.ui.components.FormField
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ReportMissingColors
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StepHeader
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StyledOutlinedTextField
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes

/** Maximum digits allowed in phone number (matches validator) */
private const val MAX_PHONE_DIGITS = 11

/**
 * Stateless content composable for Contact Details screen (Step 4/4).
 * Displays phone, email, and optional reward fields with validation errors.
 *
 * Follows the same edge-to-edge pattern as ChipNumberContent and PhotoContent:
 * - statusBarsPadding() on main Column
 * - navigationBarsPadding() on Continue button
 *
 * @param state Current UI state
 * @param modifier Modifier for the component
 * @param onPhoneChange Callback when phone changes
 * @param onEmailChange Callback when email changes
 * @param onRewardChange Callback when reward changes
 * @param onBackClick Callback when back button is clicked
 * @param onContinueClick Callback when continue button is clicked
 */
@Composable
fun ContactDetailsContent(
    state: OwnerDetailsUiState,
    modifier: Modifier = Modifier,
    onPhoneChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onRewardChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("ownersDetails.content"),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = "Owner's details",
            currentStep = 4,
            onBackClick = if (state.isSubmitting) { {} } else onBackClick,
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your contact info",
                style = MaterialTheme.typography.headlineSmall,
                color = ReportMissingColors.TitleColor,
                modifier = Modifier.testTag("ownersDetails.title"),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add your contact information's and potential reward.",
                style = MaterialTheme.typography.bodyMedium,
                color = ReportMissingColors.SubtitleColor,
                modifier = Modifier.testTag("ownersDetails.subtitle"),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Phone field with input filtering
            FormField(
                label = "Phone number",
                errorMessage = state.phoneError,
                modifier = Modifier.testTag("ownersDetails.phoneInput"),
            ) {
                StyledOutlinedTextField(
                    value = state.phone,
                    onValueChange = { newValue ->
                        // Filter to only allow valid phone characters: digits, +, spaces, dashes, parentheses
                        val filtered = newValue.filter { it.isDigit() || it in "+- ()" }
                        // Count digits only (excluding formatting characters)
                        val digitCount = filtered.count { it.isDigit() }
                        // Allow change only if digit count <= 11
                        if (digitCount <= MAX_PHONE_DIGITS) {
                            onPhoneChange(filtered)
                        }
                    },
                    placeholder = "Enter phone number...",
                    isError = state.phoneError != null,
                    enabled = !state.isSubmitting,
                    keyboardType = KeyboardType.Phone,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            FormField(
                label = "Email",
                errorMessage = state.emailError,
                modifier = Modifier.testTag("ownersDetails.emailInput"),
            ) {
                StyledOutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = "username@example.com",
                    isError = state.emailError != null,
                    enabled = !state.isSubmitting,
                    keyboardType = KeyboardType.Email,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reward field (optional)
            FormField(
                label = "Reward for the finder (optional)",
                modifier = Modifier.testTag("ownersDetails.rewardInput"),
            ) {
                StyledOutlinedTextField(
                    value = state.reward,
                    onValueChange = onRewardChange,
                    placeholder = "Enter amount...",
                    enabled = !state.isSubmitting,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Continue button (shared component with loading state support)
        ContinueButton(
            onClick = onContinueClick,
            enabled = state.canSubmit,
            isLoading = state.isSubmitting,
            testTag = "ownersDetails.continueButton",
        )
    }
}

/**
 * Preview parameter provider for ContactDetailsContent.
 */
class OwnerDetailsUiStateProvider : PreviewParameterProvider<OwnerDetailsUiState> {
    override val values = sequenceOf(
        // Initial state
        OwnerDetailsUiState(),
        // With values entered
        OwnerDetailsUiState(
            phone = "+48 123 456 789",
            email = "owner@example.com",
            reward = "$250 gift card",
        ),
        // With validation errors
        OwnerDetailsUiState(
            phone = "123",
            email = "invalid",
            phoneError = "Enter at least 7 digits",
            emailError = "Enter a valid email address",
        ),
        // Loading state
        OwnerDetailsUiState(
            phone = "+48 123 456 789",
            email = "owner@example.com",
            isSubmitting = true,
        ),
    )
}

@Preview(name = "Contact Details Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun ContactDetailsContentPreview(
    @PreviewParameter(OwnerDetailsUiStateProvider::class) state: OwnerDetailsUiState,
) {
    MaterialTheme {
        ContactDetailsContent(state = state)
    }
}
