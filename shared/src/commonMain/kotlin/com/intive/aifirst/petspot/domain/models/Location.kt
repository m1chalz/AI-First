package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a geographic location with search radius.
 * Domain model for animal location data.
 *
 * @property city City or area name
 * @property radiusKm Search radius in kilometers
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Location(
    val city: String,
    val radiusKm: Int,
)
