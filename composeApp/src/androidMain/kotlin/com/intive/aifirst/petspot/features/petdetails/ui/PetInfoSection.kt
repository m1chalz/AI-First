package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.lib.DateFormatter
import com.intive.aifirst.petspot.lib.MicrochipFormatter

// Design colors from Figma
private val LabelColor = Color(0xFF6A7282)
private val ValueColor = Color(0xFF101828)
private val DividerColor = Color(0xFFE8E8E8)

/**
 * Pet identification information section per Figma design.
 * Shows fields in specific order with two-column grid layout.
 */
@Composable
fun PetInfoSection(
    pet: Animal,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 23.dp, vertical = 16.dp),
    ) {
        // Date of Disappearance (first per Figma)
        InfoItem(
            label = "Date of Disappearance",
            value = DateFormatter.formatPetDate(pet.lastSeenDate),
            testTag = "petDetails.disappearanceDate",
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = DividerColor, thickness = 0.667.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Contact owner (phone) - displayed in full per spec (no masking)
        InfoItem(
            label = "Contact owner",
            value = pet.phone ?: "—",
            testTag = "petDetails.phone",
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Contact owner (email)
        InfoItem(
            label = "Contact owner",
            value = pet.email ?: "—",
            testTag = "petDetails.email",
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = DividerColor, thickness = 0.667.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Two-column: Animal Name / Microchip number
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                InfoItem(
                    label = "Animal Name",
                    value = pet.name,
                    testTag = "petDetails.name",
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                InfoItem(
                    label = "Microchip number",
                    value = MicrochipFormatter.formatMicrochip(pet.microchipNumber),
                    testTag = "petDetails.microchip",
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Two-column: Animal Species / Animal Race (breed)
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                InfoItem(
                    label = "Animal Species",
                    value = pet.species.name.lowercase().replaceFirstChar { it.uppercase() },
                    testTag = "petDetails.species",
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                InfoItem(
                    label = "Animal Race",
                    value = pet.breed.ifBlank { "—" },
                    testTag = "petDetails.breed",
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Two-column: Animal Sex / Animal Approx. Age
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SexInfoItem(
                    gender = pet.gender,
                    testTag = "petDetails.sex",
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                InfoItem(
                    label = "Animal Approx. Age",
                    value = pet.approximateAge ?: "—",
                    testTag = "petDetails.age",
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    testTag: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
) {
    Column(modifier = modifier.testTag(testTag)) {
        Text(
            text = label,
            color = LabelColor,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
        Text(
            text = value,
            color = ValueColor,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SexInfoItem(
    gender: AnimalGender,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.testTag(testTag)) {
        Text(
            text = "Animal Sex",
            color = LabelColor,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector =
                    when (gender) {
                        AnimalGender.MALE -> Icons.Default.Male
                        AnimalGender.FEMALE -> Icons.Default.Female
                        AnimalGender.UNKNOWN -> Icons.Default.QuestionMark
                    },
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = ValueColor,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = gender.name.lowercase(),
                color = ValueColor,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "Pet Info Section", showBackground = true)
@Composable
private fun PetInfoSectionPreview() {
    PetInfoSection(
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
                description = "Friendly dog with red collar",
                email = "owner@example.com",
                phone = "+48 111 222 333",
                microchipNumber = "123456789012345",
                approximateAge = "3 years",
            ),
    )
}
