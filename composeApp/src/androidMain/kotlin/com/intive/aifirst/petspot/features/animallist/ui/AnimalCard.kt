@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.intive.aifirst.petspot.R
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location

/**
 * Composable for displaying a single animal card in the list.
 * Shows animal photo placeholder (left), info details (middle), and status/date (right).
 *
 * Design matches Figma specifications ("Missing animals list app" node-id=297-7556):
 * - Card border radius: 14dp
 * - Card border: 1px solid #E5E9EC (no shadow)
 * - Card height: 100dp
 * - Layout: Three-column (photo | info | status/date)
 * - Image placeholder: 64dp circular
 * - Status badge: pill-shaped, color-coded (MISSING=#FF0000, FOUND=#155DFC)
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
                .height(100.dp)
                .border(1.dp, Color(0xFFE5E9EC), RoundedCornerShape(14.dp))
                .testTag("animalList.cardItem"),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // LEFT: Photo placeholder (64dp circular)
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_list_image_default),
                    contentDescription = "",
                )
            }

            // MIDDLE: Info column (location, species, breed)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Location row: icon + name + "•" + distance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "📍",
                        fontSize = 13.sp,
                    )
                    Text(
                        text = animal.location.city,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A5565),
                    )
                    Text(
                        text = "•",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A5565),
                    )
                    Text(
                        text = "+${animal.location.radiusKm}km",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A5565),
                    )
                }

                // Species/breed row: Species + "•" + Breed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = animal.species.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF101828),
                    )
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF101828),
                    )
                    Text(
                        text = animal.breed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF101828),
                    )
                }
            }

            // RIGHT: Status badge and date (vertically stacked)
            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Status badge: pill-shaped
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = Color(animal.status.badgeColor.toColorInt()),
                ) {
                    Text(
                        text = animal.status.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )
                }

                // Date (wraps to content width)
                Text(
                    text = animal.lastSeenDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF6A7282),
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
            status = AnimalStatus.MISSING,
            lastSeenDate = "12/11/2025",
            description = "Energetic dog wearing a red collar.",
            email = "owner@example.com",
            phone = "+48 111 222 333",
        )
}

@Preview(name = "Animal card - Missing status")
@Composable
private fun AnimalCardPreview() {
    MaterialTheme {
        Surface {
            AnimalCard(animal = AnimalCardPreviewData.animal)
        }
    }
}

@Preview(name = "Animal card - Found status")
@Composable
private fun AnimalCardFoundPreview() {
    MaterialTheme {
        Surface {
            AnimalCard(
                animal =
                    AnimalCardPreviewData.animal.copy(
                        status = AnimalStatus.FOUND,
                        name = "Milo",
                    ),
            )
        }
    }
}
