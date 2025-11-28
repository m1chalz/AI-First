package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location

/**
 * Hero image section showing pet photo with close button, status badge, and reward badge.
 * Per Figma design: photo with overlays for navigation and badges.
 * Handles edge-to-edge display with proper status bar insets.
 */
@Composable
fun PetPhotoSection(
    pet: Animal,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                // Matches Figma design proportions
                .aspectRatio(16f / 10f),
    ) {
        // Pet Photo with error fallback per FR-001
        if (pet.photoUrl.startsWith("placeholder") || pet.photoUrl.isBlank()) {
            // Show "Image not available" placeholder per spec
            ImageNotAvailablePlaceholder(
                modifier =
                    Modifier
                        .matchParentSize()
                        .testTag("petDetails.photo"),
            )
        } else {
            SubcomposeAsyncImage(
                model = pet.photoUrl,
                contentDescription = "Photo of ${pet.name}",
                modifier =
                    Modifier
                        .matchParentSize()
                        .testTag("petDetails.photo"),
                contentScale = ContentScale.Crop,
                loading = {
                    ImageNotAvailablePlaceholder()
                },
                error = {
                    ImageNotAvailablePlaceholder()
                },
                success = {
                    SubcomposeAsyncImageContent()
                },
            )
        }

        // Close Button (top left) - Per Figma design, respects status bar
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = statusBarPadding + 18.dp),
        ) {
            PetDetailsCloseButton(onCloseClick = onCloseClick)
        }

        // Status Badge (top right) - Per Figma design, respects status bar
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = statusBarPadding + 18.dp),
        ) {
            StatusBadge(status = pet.status)
        }

        // Reward Badge (bottom left) - Per Figma design
        pet.rewardAmount?.let { reward ->
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 18.dp),
            ) {
                RewardBadge(reward = reward)
            }
        }
    }
}

/**
 * Gray placeholder with "Image not available" text per FR-001 spec.
 */
@Composable
private fun ImageNotAvailablePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFE5E5E5)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Image not available",
            color = Color(0xFF6A7282),
            fontSize = 16.sp,
        )
    }
}

@Preview(name = "Pet Photo Section")
@Composable
private fun PetPhotoSectionPreview() {
    PetPhotoSection(
        pet =
            Animal(
                id = "1",
                name = "Luna",
                photoUrl = "",
                location = Location(latitude = 52.2297, longitude = 21.0122),
                species = "Dog",
                breed = "Golden Retriever",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.MISSING,
                lastSeenDate = "18/11/2025",
                description = "Friendly dog",
                email = "owner@example.com",
                phone = "+48 111 222 333",
                rewardAmount = "500 PLN",
            ),
        onCloseClick = {},
    )
}
