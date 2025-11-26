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
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.domain.models.Animal

/**
 * Main content composable for Pet Details screen per Figma design.
 * Layout order: Photo → Info (with contact) → Location → Description
 */
@Composable
fun PetDetailsContent(
    pet: Animal,
    onBackClick: () -> Unit,
    onShowMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .testTag("petDetails.content")
    ) {
        // Photo section with close button, status badge, and reward badge
        PetPhotoSection(
            pet = pet,
            onCloseClick = onBackClick
        )
        
        // Info section (Date, Contact, Name/Microchip, Species/Race, Sex/Age)
        PetInfoSection(pet = pet)
        
        // Location section with "Show on map" button
        PetLocationSection(
            pet = pet,
            onShowMapClick = onShowMapClick
        )
        
        // Description section
        PetDescriptionSection(pet = pet)
        
        // Bottom padding
        Spacer(modifier = Modifier.height(32.dp))
    }
}

