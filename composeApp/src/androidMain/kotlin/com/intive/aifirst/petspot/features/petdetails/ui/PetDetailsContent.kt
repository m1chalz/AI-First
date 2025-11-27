package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location

/**
 * Main content composable for Pet Details screen per Figma design.
 * Layout order: Photo → Info (with contact) → Location → Description
 */
@Composable
fun PetDetailsContent(
    pet: Animal,
    onBackClick: () -> Unit,
    onShowMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .testTag("petDetails.content"),
    ) {
        // Photo section with close button, status badge, and reward badge
        PetPhotoSection(
            pet = pet,
            onCloseClick = onBackClick,
        )

        // Info section (Date, Contact, Name/Microchip, Species/Race, Sex/Age)
        PetInfoSection(pet = pet)

        // Location section with "Show on map" button
        PetLocationSection(
            pet = pet,
            onShowMapClick = onShowMapClick,
        )

        // Description section
        PetDescriptionSection(pet = pet)

        // Bottom padding
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(name = "Pet Details Content", showBackground = true)
@Composable
private fun PetDetailsContentPreview() {
    PetDetailsContent(
        pet =
            Animal(
                id = "1",
                name = "Luna",
                photoUrl = "",
                location = Location(city = "Warsaw", radiusKm = 2, latitude = 52.2297, longitude = 21.0122),
                species = AnimalSpecies.DOG,
                breed = "Golden Retriever",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.MISSING,
                lastSeenDate = "18/11/2025",
                description = "Friendly golden retriever with a red collar. Very energetic and responds to her name.",
                email = "owner@example.com",
                phone = "+48 111 222 333",
                microchipNumber = "123456789012345",
                approximateAge = "3 years",
                rewardAmount = "500 PLN",
            ),
        onBackClick = {},
        onShowMapClick = {},
    )
}
