package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location

// Design colors from Figma
private val LabelColor = Color(0xFF6A7282)
private val ValueColor = Color(0xFF101828)

/**
 * Description section per Figma design.
 */
@Composable
fun PetDescriptionSection(
    pet: Animal,
    modifier: Modifier = Modifier,
) {
    val description = pet.description.ifBlank { "—" }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 23.dp, vertical = 8.dp),
    ) {
        Text(
            text = "Animal Additional Description",
            color = LabelColor,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            color = ValueColor,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            modifier = Modifier.testTag("petDetails.description"),
        )
    }
}

@Preview(name = "Pet Description Section", showBackground = true)
@Composable
private fun PetDescriptionSectionPreview() {
    PetDescriptionSection(
        pet =
            Animal(
                id = "1",
                name = "Luna",
                photoUrl = "",
                location = Location(latitude = 40.7829, longitude = -73.9654),
                species = "Dog",
                breed = "Golden Retriever",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.MISSING,
                lastSeenDate = "18/11/2025",
                description =
                    "Friendly golden retriever with a red collar. " +
                        "Last seen near the fountain. Very energetic.",
                email = "owner@example.com",
                phone = "+48 111 222 333",
            ),
    )
}
