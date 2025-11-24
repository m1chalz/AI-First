package com.intive.aifirst.petspot.composeapp.domain.models

/**
 * Represents a geographic location with search radius.
 * Domain model for animal location data.
 *
 * @property city City or area name
 * @property radiusKm Search radius in kilometers
 */
data class Location(
    val city: String,
    val radiusKm: Int,
)
