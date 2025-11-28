package com.intive.aifirst.petspot.data.repositories

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.repositories.LocationRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Android implementation of LocationRepository using LocationManager.
 *
 * Uses native Android LocationManager (no Google Play Services dependency).
 * Implements two-stage location fetching:
 * 1. Cached last known location (instant)
 * 2. Fresh location request with timeout
 *
 * @param locationManager Android LocationManager instance (injected for testability)
 */
class LocationRepositoryImpl(
    private val locationManager: LocationManager,
) : LocationRepository {
    /**
     * Gets cached last known location from GPS or Network provider.
     * Tries GPS first (most accurate), falls back to Network.
     *
     * @return Cached location coordinates, or null if no cached location
     * @throws SecurityException if location permission not granted
     */
    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): LocationCoordinates? {
        // Try GPS provider first (most accurate)
        var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // Fallback to Network provider if GPS unavailable
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return location?.toLocationCoordinates()
    }

    /**
     * Requests a fresh location update with timeout.
     * Uses requestSingleUpdate with LocationListener callback.
     *
     * @param timeoutMs Maximum time to wait for location (default 10 seconds)
     * @return Fresh location coordinates, or null if timeout/failure
     * @throws SecurityException if location permission not granted
     */
    @SuppressLint("MissingPermission")
    override suspend fun requestFreshLocation(timeoutMs: Long): LocationCoordinates? =
        withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { continuation ->
                val listener =
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            locationManager.removeUpdates(this)
                            if (continuation.isActive) {
                                continuation.resume(location.toLocationCoordinates())
                            }
                        }

                        @Deprecated("Deprecated in API level 29")
                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: android.os.Bundle?,
                        ) {
                            // Required for older API levels, no-op for modern devices
                        }

                        override fun onProviderEnabled(provider: String) {
                            // Provider enabled, wait for location
                        }

                        override fun onProviderDisabled(provider: String) {
                            // Provider disabled, location request will timeout
                        }
                    }

                // Determine best available provider
                val provider =
                    when {
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                            LocationManager.GPS_PROVIDER

                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                            LocationManager.NETWORK_PROVIDER

                        else -> {
                            // No provider available
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                            return@suspendCancellableCoroutine
                        }
                    }

                // Request single location update
                locationManager.requestSingleUpdate(
                    provider,
                    listener,
                    Looper.getMainLooper(),
                )

                // Clean up on cancellation
                continuation.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }
            }
        }

    /**
     * Extension function to convert Android Location to domain LocationCoordinates.
     */
    private fun Location.toLocationCoordinates(): LocationCoordinates =
        LocationCoordinates(
            latitude = latitude,
            longitude = longitude,
        )
}
