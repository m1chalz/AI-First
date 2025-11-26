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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal

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
