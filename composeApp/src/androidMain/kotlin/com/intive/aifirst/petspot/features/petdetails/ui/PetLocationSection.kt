package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.lib.LocationFormatter

// Design colors from Figma
private val LabelColor = Color(0xFF6A7282)
private val ValueColor = Color(0xFF101828)
private val ButtonBlue = Color(0xFF155DFC)

/**
 * Location section per Figma design: lat/lon coordinates and outlined "Show on map" button.
 */
@Composable
fun PetLocationSection(
    pet: Animal,
    onShowMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasCoordinates = pet.location.latitude != null && pet.location.longitude != null
    val coordinatesText = LocationFormatter.formatCoordinates(pet.location)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 23.dp, vertical = 8.dp)
    ) {
        // Label
        Text(
            text = "Place of Disappearance / City",
            color = LabelColor,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Location with icon: "52.2297° N, 21.0122° E" or "—" if no coordinates
        Row(
            modifier = Modifier.testTag("petDetails.location"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(20.dp),
                tint = ValueColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = coordinatesText,
                color = ValueColor,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Show on map button - outlined blue per Figma
        OutlinedButton(
            onClick = onShowMapClick,
            enabled = hasCoordinates,
            modifier = Modifier.testTag("petDetails.showMapButton"),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(2.dp, if (hasCoordinates) ButtonBlue else LabelColor)
        ) {
            Text(
                text = "Show on the map",
                color = if (hasCoordinates) ButtonBlue else LabelColor,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

