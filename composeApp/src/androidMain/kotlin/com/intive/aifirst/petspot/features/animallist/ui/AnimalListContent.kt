@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListUiState

/**
 * Stateless UI for the animal list screen.
 * Receives all state and actions via parameters to enable previews and testing.
 *
 * Design matches Figma specifications ("Missing animals list app" node-id=297-7556):
 * - Title: "PetSpot" left-aligned, Hind 32sp, color rgba(0,0,0,0.8)
 * - List padding: 23dp horizontal
 * - Spacer between title and list: 24dp
 * - Floating button: pill-shaped (22dp radius), blue (#155DFC), shadow
 */
@Composable
fun AnimalListContent(
    state: AnimalListUiState,
    onReportMissing: () -> Unit = {},
    onAnimalClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 44.dp),
        ) {
            // Title: "PetSpot" left-aligned
            Text(
                text = "PetSpot",
                fontFamily = FontFamily.SansSerif,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000).copy(alpha = 0.8f),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content area with loading/error/empty/list states
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) {
                when {
                    state.isLoading -> LoadingIndicator()
                    state.error != null ->
                        ErrorState(
                            message = state.error,
                            onRetry = onRetry,
                        )

                    state.isEmpty -> EmptyState()
                    else ->
                        AnimalList(
                            animals = state.animals,
                            onAnimalClick = onAnimalClick,
                        )
                }
            }
        }

        // Floating button at bottom right
        FloatingReportButton(
            onClick = onReportMissing,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 23.dp)
                    .testTag("animalList.reportButton"),
        )
    }
}

@Composable
private fun FloatingReportButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF155DFC),
                contentColor = Color.White,
            ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(horizontal = 21.dp, vertical = 14.dp),
    ) {
        Text(
            text = "Report a Missing Animal",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "🐾",
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun BoxScope.LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun BoxScope.ErrorState(
    message: String,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .align(Alignment.Center)
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) {
            Text(text = "Try again")
        }
    }
}

@Composable
private fun AnimalList(
    animals: List<Animal>,
    onAnimalClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .testTag("animalList.list"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = animals,
            key = { animal -> animal.id },
        ) { animal ->
            AnimalCard(
                animal = animal,
                onClick = { onAnimalClick(animal.id) },
            )
        }
    }
}

private class AnimalListStatePreviewProvider :
    PreviewParameterProvider<AnimalListUiState> {
    private val warsaw = Location(city = "Central Park", radiusKm = 2)
    private val krakow = Location(city = "Market Square", radiusKm = 3)

    private val animals =
        listOf(
            Animal(
                id = "1",
                name = "Luna",
                photoUrl = "",
                location = warsaw,
                species = AnimalSpecies.DOG,
                breed = "Golden Retriever",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "18/11/2025",
                description = "Energetic dog wearing a red collar.",
                email = "owner@example.com",
                phone = "+48 111 222 333",
            ),
            Animal(
                id = "2",
                name = "Milo",
                photoUrl = "",
                location = krakow,
                species = AnimalSpecies.CAT,
                breed = "Siamese",
                gender = AnimalGender.MALE,
                status = AnimalStatus.FOUND,
                lastSeenDate = "17/11/2025",
                description = "Large fluffy cat with green eyes.",
                email = null,
                phone = "+48 999 888 777",
            ),
            Animal(
                id = "3",
                name = "Max",
                photoUrl = "",
                location = warsaw,
                species = AnimalSpecies.DOG,
                breed = "German Shepherd",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "15/11/2025",
                description = "Alert dog with brown and black markings.",
                email = "max.owner@example.com",
                phone = "+48 555 666 777",
            ),
        )

    override val values: Sequence<AnimalListUiState> =
        sequenceOf(
            AnimalListUiState(isLoading = true),
            AnimalListUiState(animals = emptyList()),
            AnimalListUiState(error = "Failed to load animals"),
            AnimalListUiState(animals = animals),
        )
}

@Preview(name = "Animal list", showBackground = true)
@Composable
private fun AnimalListContentPreview(
    @PreviewParameter(AnimalListStatePreviewProvider::class) state: AnimalListUiState,
) {
    MaterialTheme {
        AnimalListContent(state = state)
    }
}
