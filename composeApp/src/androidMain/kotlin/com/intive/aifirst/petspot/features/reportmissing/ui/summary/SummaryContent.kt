package com.intive.aifirst.petspot.features.reportmissing.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUiState
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes

/**
 * Design constants matching Figma node 297-8193.
 */
private object SummaryDesign {
    // Colors
    val TitleColor = Color(0xCC000000) // rgba(0,0,0,0.8)
    val BodyColor = Color(0xFF545F71)
    val GradientStart = Color(0xFF5C33FF)
    val GradientEnd = Color(0xFFF84BA1)
    val GlowColor = Color(0x33FB64B6) // 20% alpha
    val CloseButtonColor = Color(0xFF155DFC)

    // Spacing
    val HorizontalPadding = 22.dp
    val TitleTopPadding = 104.dp // From Figma py-[104px]
    val SectionSpacing = 24.dp
    val ParagraphSpacing = 0.dp // Paragraphs are in same container
    val PasswordTopSpacing = 16.dp // gap-[16px] from parent Frame between body and password
    val ButtonBottomPadding = 16.dp

    // Password container
    val PasswordContainerHeight = 90.dp
    val PasswordContainerRadius = 10.dp
    val PasswordFontSize = 60.sp
    val PasswordLetterSpacing = (-1.5).sp

    // Close button
    val CloseButtonHeight = 52.dp
    val CloseButtonRadius = 10.dp
    val CloseButtonFontSize = 18.sp
}

/**
 * Body copy text from Figma (FR-003).
 */
private object SummaryCopy {
    const val TITLE = "Report created"
    const val BODY_PARAGRAPH_1 =
        "Your report has been created, and your missing animal has been added to the database. " +
            "If your pet is found, you will receive a notification immediately."
    const val BODY_PARAGRAPH_2 =
        "If you wish to remove your report from the database, use the code provided below in the " +
            "removal form. This code has also been sent to your email address"
    const val CLOSE_BUTTON = "Close"
}

/**
 * Stateless content composable for Summary/Report Created Confirmation screen.
 * Displays success confirmation messaging, password container with gradient, and Close button.
 *
 * NOTE: This screen does NOT have a TopAppBar (FR-011) - only Close button at bottom.
 * NOTE: Android 13+ shows system clipboard confirmation, so no custom Snackbar is needed.
 *
 * @param state Current UI state containing management password
 * @param modifier Modifier for the component
 * @param onPasswordContainerClick Callback when password container is tapped (copy to clipboard)
 * @param onCloseClick Callback when Close button is clicked
 */
@Composable
fun SummaryContent(
    state: SummaryUiState,
    modifier: Modifier = Modifier,
    onPasswordContainerClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("summary.content"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = SummaryDesign.HorizontalPadding),
            ) {
                Spacer(modifier = Modifier.height(SummaryDesign.TitleTopPadding))

                // Title: "Report created"
                Text(
                    text = SummaryCopy.TITLE,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = SummaryDesign.TitleColor,
                    modifier = Modifier.testTag("summary.title"),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Body paragraph 1 + 2 (combined as in Figma)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = SummaryCopy.BODY_PARAGRAPH_1,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                        ),
                        color = SummaryDesign.BodyColor,
                        modifier = Modifier.testTag("summary.bodyParagraph1"),
                    )
                    Text(
                        text = SummaryCopy.BODY_PARAGRAPH_2,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                        ),
                        color = SummaryDesign.BodyColor,
                        modifier = Modifier.testTag("summary.bodyParagraph2"),
                    )
                }

                Spacer(modifier = Modifier.height(SummaryDesign.PasswordTopSpacing))

                // Password container with gradient background
                PasswordContainer(
                    password = state.managementPassword,
                    onClick = onPasswordContainerClick,
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Close button at bottom (outside scrolling area)
            Button(
                onClick = onCloseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .height(SummaryDesign.CloseButtonHeight)
                    .testTag("summary.closeButton"),
                shape = RoundedCornerShape(SummaryDesign.CloseButtonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SummaryDesign.CloseButtonColor,
                ),
            ) {
                Text(
                    text = SummaryCopy.CLOSE_BUTTON,
                    fontSize = SummaryDesign.CloseButtonFontSize,
                    lineHeight = 28.sp,
                )
            }
        }
    }
}

/**
 * Password container with gradient background and glow effect.
 * Displays the management password/code in large white digits.
 */
@Composable
private fun PasswordContainer(
    password: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(SummaryDesign.PasswordContainerHeight)
            .clip(RoundedCornerShape(SummaryDesign.PasswordContainerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SummaryDesign.GradientStart,
                        SummaryDesign.GradientEnd,
                    ),
                    start = Offset(0.0f, Float.POSITIVE_INFINITY),
                    end = Offset(Float.POSITIVE_INFINITY, 0.0f),
                ),
            )
            .clickable(onClick = onClick)
            .testTag("summary.passwordContainer"),
        contentAlignment = Alignment.Center,
    ) {
        // Glow effect overlay (positioned at left side as in Figma)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    alpha = 0.2f
                }
                .blur(24.dp)
                .background(
                    color = Color(0xFFFB64B6),
                    shape = RoundedCornerShape(percent = 50),
                ),
        )

        // Password text
        Text(
            text = password,
            style = TextStyle(
                fontSize = SummaryDesign.PasswordFontSize,
                letterSpacing = SummaryDesign.PasswordLetterSpacing,
                fontFamily = FontFamily.SansSerif, // Arial equivalent
                fontWeight = FontWeight.Normal,
                lineHeight = 60.sp,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("summary.passwordText"),
        )
    }
}

/**
 * Preview parameter provider for SummaryUiState.
 */
class SummaryUiStateProvider : PreviewParameterProvider<SummaryUiState> {
    override val values: Sequence<SummaryUiState> = sequenceOf(
        // With password (typical case)
        SummaryUiState(managementPassword = "5216577"),
        // Empty password (null from flowState mapped to empty string)
        SummaryUiState.Initial,
    )
}

@Preview(name = "Summary Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun SummaryContentPreview(
    @PreviewParameter(SummaryUiStateProvider::class) state: SummaryUiState,
) {
    MaterialTheme {
        SummaryContent(state = state)
    }
}
