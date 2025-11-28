package com.intive.aifirst.petspot.domain.repositories

import com.intive.aifirst.petspot.domain.models.LocationCoordinates

/**
 * Repository interface for location operations.
 * Abstracts Android LocationManager implementation details.
 */
interface LocationRepository {
    /**
     * Gets the cached last known location (instant, no network call).
     * Tries GPS provider first, falls back to Network provider.
     *
     * @return Cached location coordinates, or null if no cached location available
     * @throws SecurityException if location permission not granted
     */
    suspend fun getLastKnownLocation(): LocationCoordinates?

    /**
     * Requests a fresh location update with timeout.
     * Only called if cached location is unavailable or too stale.
     *
     * @param timeoutMs Maximum time to wait for location (default 10 seconds)
     * @return Fresh location coordinates, or null if timeout/failure
     * @throws SecurityException if location permission not granted
     */
    suspend fun requestFreshLocation(timeoutMs: Long = 10_000L): LocationCoordinates?
}
