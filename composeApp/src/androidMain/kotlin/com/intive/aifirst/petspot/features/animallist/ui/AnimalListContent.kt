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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.AnimalGender
import com.intive.aifirst.petspot.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.domain.models.AnimalStatus
import com.intive.aifirst.petspot.domain.models.Location
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListUiState

/**
 * Stateless UI for the animal list screen.
 * Receives all state and actions via parameters to enable previews and testing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListContent(
    state: AnimalListUiState,
    onReportMissing: () -> Unit = {},
    onAnimalClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AnimalListTopBar() },
        bottomBar = { ReportMissingBottomBar(onReportMissing = onReportMissing) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalListTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Missing animals list",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    )
}

@Composable
private fun ReportMissingBottomBar(
    onReportMissing: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Button(
            onClick = onReportMissing,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("animalList.reportMissingButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = "Report a Missing Animal",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            )
        }
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(modifier = Modifier.height(56.dp)) }

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
    private val warsaw = Location(city = "Warsaw", radiusKm = 15)
    private val krakow = Location(city = "Kraków", radiusKm = 10)

    private val animals =
        listOf(
            Animal(
                id = "1",
                name = "Luna",
                photoUrl = "",
                location = warsaw,
                species = AnimalSpecies.DOG,
                breed = "Border Collie",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "12/11/2025",
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
                breed = "Maine Coon",
                gender = AnimalGender.MALE,
                status = AnimalStatus.FOUND,
                lastSeenDate = "10/11/2025",
                description = "Large fluffy cat with green eyes.",
                email = null,
                phone = "+48 999 888 777",
            ),
        )

    override val values: Sequence<AnimalListUiState> =
        sequenceOf(
            AnimalListUiState(isLoading = true),
            AnimalListUiState(error = "Network timeout"),
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
