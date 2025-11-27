package com.intive.aifirst.petspot.domain.models

/**
 * Device's current geographic location coordinates.
 * Used for location-aware animal listings API queries.
 *
 * @property latitude Latitude coordinate (-90.0 to 90.0)
 * @property longitude Longitude coordinate (-180.0 to 180.0)
 */
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude in -90.0..90.0) {
            "Latitude must be between -90.0 and 90.0, was $latitude"
        }
        require(longitude in -180.0..180.0) {
            "Longitude must be between -180.0 and 180.0, was $longitude"
        }
    }
}
