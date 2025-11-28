package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.repositories.LocationRepository

/**
 * Use case for getting the device's current location using a two-stage approach.
 *
 * Stage 1: Try to get cached last known location (instant, no network)
 * Stage 2: If cached unavailable, request fresh location with timeout
 *
 * Returns:
 * - Success with LocationCoordinates when location obtained
 * - Success with null when location unavailable (fallback mode)
 * - Failure with SecurityException when permission not granted
 */
class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
    private val defaultTimeoutMs: Long = DEFAULT_TIMEOUT_MS,
) {
    /**
     * Invokes the use case to get current location.
     *
     * @return Result containing LocationCoordinates (or null if unavailable)
     */
    suspend operator fun invoke(): Result<LocationCoordinates?> =
        runCatching {
            // Stage 1: Try cached location first (instant)
            val cachedLocation = locationRepository.getLastKnownLocation()
            if (cachedLocation != null) {
                return@runCatching cachedLocation
            }

            // Stage 2: Request fresh location with timeout
            locationRepository.requestFreshLocation(defaultTimeoutMs)
        }

    companion object {
        /** Default timeout for fresh location request: 10 seconds */
        const val DEFAULT_TIMEOUT_MS = 10_000L
    }
}
