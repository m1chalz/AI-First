package com.intive.aifirst.petspot.features.fullscreenmap.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapError
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapUiState

/**
 * Preview parameter provider for FullscreenMapUiState.
 * Provides different states for preview rendering.
 */
class FullscreenMapStateProvider : PreviewParameterProvider<FullscreenMapUiState> {
    private val testAnimal =
        Animal(
            id = "1",
            name = "Max",
            photoUrl = "https://example.com/max.jpg",
            location = Location(latitude = 52.2297, longitude = 21.0122),
            species = "Dog",
            breed = "Labrador",
            gender = AnimalGender.MALE,
            status = AnimalStatus.MISSING,
            lastSeenDate = "2026-01-08",
            description = "Friendly golden labrador, responds to whistles.",
            email = "owner@example.com",
            phone = "+48 123 456 789",
            microchipNumber = null,
            rewardAmount = "500 PLN",
            age = 3,
        )

    private val foundAnimal =
        Animal(
            id = "2",
            name = "Bella",
            photoUrl = "https://example.com/bella.jpg",
            location = Location(latitude = 52.2400, longitude = 21.0200),
            species = "Cat",
            breed = "Siamese",
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.FOUND,
            lastSeenDate = "2026-01-07",
            description = "Found near the park. Very friendly.",
            email = "finder@example.com",
            phone = null,
            microchipNumber = "123456789012345",
            rewardAmount = null,
            age = 2,
        )

    override val values: Sequence<FullscreenMapUiState> =
        sequenceOf(
            // Initial state
            FullscreenMapUiState.Initial,
            // Loading state
            FullscreenMapUiState(
                isLoadingAnimals = true,
                userLocation = LocationCoordinates(52.2297, 21.0122),
            ),
            // Success state with animals
            FullscreenMapUiState(
                userLocation = LocationCoordinates(52.2297, 21.0122),
                animals = listOf(testAnimal, foundAnimal),
            ),
            // Error state
            FullscreenMapUiState(
                userLocation = LocationCoordinates(52.2297, 21.0122),
                error = FullscreenMapError.NetworkError,
            ),
            // Popup visible state
            FullscreenMapUiState(
                userLocation = LocationCoordinates(52.2297, 21.0122),
                animals = listOf(testAnimal, foundAnimal),
                selectedAnimal = testAnimal,
            ),
        )
}
