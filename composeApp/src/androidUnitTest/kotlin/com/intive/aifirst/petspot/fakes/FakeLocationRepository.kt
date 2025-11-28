package com.intive.aifirst.petspot.fakes

import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.repositories.LocationRepository

/**
 * Fake repository implementation for unit testing location operations.
 * Implements LocationRepository interface for controlled test scenarios.
 *
 * Allows controlling success/failure/timeout scenarios:
 * - Success: Returns configured location coordinates
 * - No cached location: Returns null for getLastKnownLocation
 * - Timeout: Returns null for requestFreshLocation
 * - Failure: Throws configurable exception (e.g., SecurityException)
 */
class FakeLocationRepository(
    private val cachedLocation: LocationCoordinates? = null,
    private val freshLocation: LocationCoordinates? = null,
    private val shouldFailWithSecurityException: Boolean = false,
    private val shouldFailWithException: Boolean = false,
    private val exception: Throwable = Exception("Fake location error"),
) : LocationRepository {
    var getLastKnownLocationCallCount = 0
        private set

    var requestFreshLocationCallCount = 0
        private set

    var lastRequestedTimeout: Long? = null
        private set

    override suspend fun getLastKnownLocation(): LocationCoordinates? {
        getLastKnownLocationCallCount++

        if (shouldFailWithSecurityException) {
            throw SecurityException("Location permission not granted")
        }

        if (shouldFailWithException) {
            throw exception
        }

        return cachedLocation
    }

    override suspend fun requestFreshLocation(timeoutMs: Long): LocationCoordinates? {
        requestFreshLocationCallCount++
        lastRequestedTimeout = timeoutMs

        if (shouldFailWithSecurityException) {
            throw SecurityException("Location permission not granted")
        }

        if (shouldFailWithException) {
            throw exception
        }

        return freshLocation
    }

    /**
     * Resets call counts for verification in tests.
     */
    fun resetCallCounts() {
        getLastKnownLocationCallCount = 0
        requestFreshLocationCallCount = 0
        lastRequestedTimeout = null
    }

    companion object {
        /** Sample Warsaw coordinates for testing */
        val SAMPLE_WARSAW =
            LocationCoordinates(
                latitude = 52.2297,
                longitude = 21.0122,
            )

        /** Sample Krakow coordinates for testing */
        val SAMPLE_KRAKOW =
            LocationCoordinates(
                latitude = 50.0647,
                longitude = 19.9450,
            )
    }
}
