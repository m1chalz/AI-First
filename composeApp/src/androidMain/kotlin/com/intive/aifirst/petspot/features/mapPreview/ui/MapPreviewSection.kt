package com.intive.aifirst.petspot.features.mapPreview.ui

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.PermissionDenied
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.PermissionGranted
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.Retry
import com.intive.aifirst.petspot.features.mapPreview.presentation.viewmodels.MapPreviewViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful wrapper for Map Preview.
 * Gets ViewModel via Koin, collects state, and dispatches intents.
 *
 * Self-contained component - can be embedded in any screen.
 * Handles location permission using Accompanist Permissions.
 *
 * @param onNavigateToFullMap Callback when user taps to view full interactive map (future)
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPreviewSection(
    modifier: Modifier = Modifier,
    onNavigateToFullMap: () -> Unit = {},
    viewModel: MapPreviewViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Permission state for coarse location
    val locationPermissionState =
        rememberPermissionState(
            permission = Manifest.permission.ACCESS_COARSE_LOCATION,
            onPermissionResult = { isGranted ->
                if (isGranted) {
                    viewModel.dispatchIntent(PermissionGranted)
                } else {
                    viewModel.dispatchIntent(PermissionDenied)
                }
            },
        )

    // Load map when permission is already granted on composition
    // Use PermissionGranted (not LoadMap) to also update permission status in state
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.dispatchIntent(PermissionGranted)
        }
    }

    MapPreviewContent(
        state = state,
        modifier = modifier,
        onRetry = { viewModel.dispatchIntent(Retry) },
        onMapClick = onNavigateToFullMap,
    )
}
