package com.intive.aifirst.petspot.features.mapPreview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewError
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewUiState
import com.intive.aifirst.petspot.features.mapPreview.ui.components.MapPreviewColors
import com.intive.aifirst.petspot.features.mapPreview.ui.components.MapPreviewLegend
import com.intive.aifirst.petspot.features.mapPreview.ui.components.MapPreviewOverlay
import com.intive.aifirst.petspot.features.mapPreview.ui.extensions.toMarkerIcon
import com.intive.aifirst.petspot.features.mapPreview.ui.preview.MapPreviewUiStateProvider

/** Default zoom level for 10km visibility */
private const val DEFAULT_ZOOM = 12f

/** Map preview height per design spec */
private val MAP_HEIGHT = 320.dp

/**
 * Stateless composable for Map Preview content.
 * Pure UI component - receives state and callbacks, no ViewModel dependency.
 */
@Composable
fun MapPreviewContent(
    state: MapPreviewUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onMapClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .testTag("mapPreview.container"),
    ) {
        // Header section with title and legend (above map per Figma design)
        // Note: No top padding - LazyColumn's verticalArrangement provides 24dp spacing
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            // Section Header - "Map View"
            Text(
                text = "Map View",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                    ),
                color = MapPreviewColors.HeaderText,
                modifier =
                    Modifier
                        .testTag("mapPreview.header"),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Legend row - below header, above map
            MapPreviewLegend(
                modifier = Modifier.testTag("mapPreview.legend"),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Map Container - full width, no border radius per Figma
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(MAP_HEIGHT),
            color = MapPreviewColors.MapBackground,
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.error != null ->
                    ErrorContent(
                        error = state.error,
                        onRetry = onRetry,
                    )

                state.canShowMap ->
                    MapContent(
                        state = state,
                        onMapClick = onMapClick,
                    )

                else -> LoadingContent() // Fallback while permission is being handled
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .testTag("mapPreview.loading"),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MapPreviewColors.Primary,
        )
    }
}

@Composable
private fun ErrorContent(
    error: MapPreviewError,
    onRetry: () -> Unit,
) {
    val (icon, message) =
        when (error) {
            MapPreviewError.LocationNotAvailable -> Icons.Default.LocationOff to "Location unavailable"
            MapPreviewError.NetworkError -> Icons.Default.WifiOff to "Network error"
            MapPreviewError.MapLoadFailed -> Icons.Default.Refresh to "Failed to load map"
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .testTag("mapPreview.error"),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MapPreviewColors.ErrorIcon,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MapPreviewColors.SubtitleText,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier.testTag("mapPreview.retryButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MapPreviewColors.Primary,
                    ),
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun MapContent(
    state: MapPreviewUiState,
    onMapClick: () -> Unit,
) {
    val userLocation = state.userLocation ?: return
    val centerLatLng = LatLng(userLocation.latitude, userLocation.longitude)

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(centerLatLng, DEFAULT_ZOOM)
        }

    val mapProperties =
        remember {
            MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL,
            )
        }

    val mapUiSettings =
        remember {
            MapUiSettings(
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false,
                scrollGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
            )
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clickable { onMapClick() }
                .testTag("mapPreview.map"),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            // Static preview - no map click interaction
            onMapClick = { },
        ) {
            // Animal markers - non-clickable (static preview)
            state.animalsWithLocation.forEach { animal ->
                val animalLat = animal.location.latitude ?: return@forEach
                val animalLng = animal.location.longitude ?: return@forEach
                val position = LatLng(animalLat, animalLng)

                Marker(
                    state = MarkerState(position = position),
                    icon = animal.status.toMarkerIcon(),
                    // Consume click without action - makes marker non-interactive
                    onClick = { true },
                )
            }
        }

        // Overlay hint - centered in map per Figma design
        MapPreviewOverlay(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MapPreviewContentPreview(
    @PreviewParameter(MapPreviewUiStateProvider::class) state: MapPreviewUiState,
) {
    MaterialTheme {
        MapPreviewContent(
            state = state,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}
