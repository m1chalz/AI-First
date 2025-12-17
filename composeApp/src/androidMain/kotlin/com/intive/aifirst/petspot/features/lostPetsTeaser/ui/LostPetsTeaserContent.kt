package com.intive.aifirst.petspot.features.lostPetsTeaser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.features.animallist.ui.AnimalCard
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserUiState
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ReportMissingColors
import com.intive.aifirst.petspot.ui.components.ContentLoading

/**
 * Stateless content composable for Lost Pets Teaser.
 * Pure presentation layer - renders state without any business logic.
 *
 * Displays loading, error, empty, or success states with pet cards.
 * Callbacks are defaulted to no-ops for preview compatibility.
 */
@Composable
fun LostPetsTeaserContent(
    state: LostPetsTeaserUiState,
    modifier: Modifier = Modifier,
    onPetClicked: (String) -> Unit = {},
    onViewAllClicked: () -> Unit = {},
    onRetryClicked: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .testTag("lostPetsTeaser.container"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Section Header
        Text(
            text = "Recently Lost Pets",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF101828),
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        when {
            // Loading State
            state.isLoading -> {
                ContentLoading(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                    color = ReportMissingColors.PrimaryBlue,
                    testTag = "lostPetsTeaser.loading",
                )
            }

            // Error State
            state.error != null -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .testTag("lostPetsTeaser.error"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Unable to load lost pets",
                        fontSize = 14.sp,
                        color = Color(0xFF6A7282),
                        textAlign = TextAlign.Center,
                    )
                    TextButton(
                        onClick = onRetryClicked,
                        modifier = Modifier.testTag("lostPetsTeaser.retryButton"),
                    ) {
                        Text(
                            text = "Tap to retry",
                            color = ReportMissingColors.PrimaryBlue,
                        )
                    }
                }
            }

            // Empty State
            state.isEmpty -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                            .testTag("lostPetsTeaser.emptyState"),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No lost pets reported recently",
                        fontSize = 14.sp,
                        color = Color(0xFF6A7282),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Success State - Pet Cards
            else -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.animals.forEach { animal ->
                        AnimalCard(
                            animal = animal,
                            onClick = { onPetClicked(animal.id) },
                            modifier = Modifier.testTag("lostPetsTeaser.petCard"),
                        )
                    }
                }

                // View All Button
                Button(
                    onClick = onViewAllClicked,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .testTag("lostPetsTeaser.viewAllButton"),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ReportMissingColors.PrimaryBlue,
                        ),
                ) {
                    Text(
                        text = "View All Lost Pets",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

// ========================================
// Preview Support
// ========================================

private class LostPetsTeaserUiStateProvider : PreviewParameterProvider<LostPetsTeaserUiState> {
    override val values: Sequence<LostPetsTeaserUiState> =
        sequenceOf(
            // Loading
            LostPetsTeaserUiState(isLoading = true),
            // Success with 5 pets
            LostPetsTeaserUiState(animals = sampleAnimals(5)),
            // Success with 2 pets
            LostPetsTeaserUiState(animals = sampleAnimals(2)),
            // Empty
            LostPetsTeaserUiState(animals = emptyList()),
            // Error
            LostPetsTeaserUiState(error = "Network error"),
        )

    private fun sampleAnimals(count: Int): List<Animal> =
        (1..count).map { index ->
            Animal(
                id = "pet-$index",
                name = "Pet $index",
                photoUrl = "",
                location = Location(latitude = 52.2297, longitude = 21.0122),
                species = if (index % 2 == 0) "Cat" else "Dog",
                breed = if (index % 2 == 0) "Persian" else "Labrador",
                gender = if (index % 2 == 0) AnimalGender.FEMALE else AnimalGender.MALE,
                status = AnimalStatus.MISSING,
                lastSeenDate = "1$index/12/2025",
                description = "Description for pet $index",
                email = "owner$index@example.com",
                phone = "+48 111 222 $index$index$index",
            )
        }
}

@Preview(name = "Lost Pets Teaser - All States")
@Composable
private fun LostPetsTeaserContentPreview(
    @PreviewParameter(LostPetsTeaserUiStateProvider::class) state: LostPetsTeaserUiState,
) {
    MaterialTheme {
        Surface {
            LostPetsTeaserContent(
                state = state,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }
    }
}
