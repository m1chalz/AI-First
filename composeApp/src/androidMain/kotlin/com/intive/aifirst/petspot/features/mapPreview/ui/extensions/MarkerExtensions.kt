package com.intive.aifirst.petspot.features.mapPreview.ui.extensions

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW
import com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus.CLOSED
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus.FOUND
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus.MISSING

/**
 * Converts AnimalStatus to appropriate marker icon for Google Maps.
 * - MISSING: Red marker
 * - FOUND: Blue marker
 * - CLOSED: Yellow marker (cases resolved)
 */
fun AnimalStatus.toMarkerIcon(): BitmapDescriptor =
    when (this) {
        MISSING -> defaultMarker(HUE_RED)
        FOUND -> defaultMarker(HUE_BLUE)
        CLOSED -> defaultMarker(HUE_YELLOW)
    }
