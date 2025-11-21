package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.AnimalGender
import com.intive.aifirst.petspot.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.domain.models.AnimalStatus
import com.intive.aifirst.petspot.domain.models.Location

/**
 * Composable for displaying a single animal card in the list.
 * Shows animal photo placeholder, species, breed, location, status badge, and date.
 *
 * Design matches Figma specifications:
 * - Card border radius: 4dp
 * - Card shadow: elevation 2dp
 * - Padding: 16dp horizontal
 * - Image placeholder: 63dp circular
 * - Status badge radius: 10dp
 *
 * @param animal Animal entity to display
 * @param onClick Callback when card is tapped
 * @param modifier Modifier for styling
 */
@Composable
fun AnimalCard(
    animal: Animal,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val placeholderInitial =
        animal.name.firstOrNull()?.uppercaseChar()?.toString()
            ?: animal.species.displayName.firstOrNull()?.uppercaseChar()?.toString()
            ?: "?"

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .testTag("animalList.item.${animal.id}"),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Photo placeholder (63dp circular)
            Box(
                modifier =
                    Modifier
                        .size(63.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEEEEE)),
                // Light gray placeholder
                contentAlignment = Alignment.Center,
            ) {
                // TODO: Replace with actual image when assets available
                Text(
                    text = placeholderInitial,
                    fontSize = 24.sp,
                    color = Color(0xFF93A2B4), // Tertiary text color
                )
            }

            // Animal info column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Species | Breed
                Text(
                    text = "${animal.species.displayName} | ${animal.breed}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF2D2D2D), // Primary text color
                )

                // Location
                Text(
                    text = "${animal.location.city}, +${animal.location.radiusKm}km",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF545F71), // Secondary text color
                )

                // Status badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(android.graphics.Color.parseColor(animal.status.badgeColor)),
                ) {
                    Text(
                        text = animal.status.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                // Last seen date
                Text(
                    text = "Last seen: ${animal.lastSeenDate}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF93A2B4), // Tertiary text color
                )
            }
        }
    }
}

private object AnimalCardPreviewData {
    val animal =
        Animal(
            id = "preview-1",
            name = "Luna",
            photoUrl = "",
            location = Location(city = "Warsaw", radiusKm = 15),
            species = AnimalSpecies.DOG,
            breed = "Border Collie",
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.ACTIVE,
            lastSeenDate = "12/11/2025",
            description = "Energetic dog wearing a red collar.",
            email = "owner@example.com",
            phone = "+48 111 222 333",
        )
}

@Preview(name = "Animal card")
@Composable
private fun AnimalCardPreview() {
    MaterialTheme {
        Surface {
            AnimalCard(animal = AnimalCardPreviewData.animal)
        }
    }
}
