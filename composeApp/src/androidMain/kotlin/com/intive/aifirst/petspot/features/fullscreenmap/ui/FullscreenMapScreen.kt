package com.intive.aifirst.petspot.features.fullscreenmap.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapEffect.NavigateBack
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.Initialize
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnAnimalTapped
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnBackPressed
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnPopupDismissed
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnRetryTapped
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnViewportChanged
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.viewmodels.FullscreenMapViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

/** Default zoom level for fullscreen map */
private const val DEFAULT_ZOOM = 14f

/** Debounce time for camera idle detection (ms) */
private const val CAMERA_DEBOUNCE_MS = 300L

/**
 * Stateful wrapper for Fullscreen Map.
 * Gets ViewModel via Koin, collects state, and dispatches intents.
 *
 * @param navController Navigation controller for back navigation
 */
@OptIn(FlowPreview::class)
@Composable
fun FullscreenMapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FullscreenMapViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Camera position state - use user location if available
    val initialPosition =
        state.userLocation?.let {
            LatLng(it.latitude, it.longitude)
        } ?: LatLng(52.2297, 21.0122) // Warsaw default

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialPosition, DEFAULT_ZOOM)
        }

    // Initialize on first composition
    LaunchedEffect(Unit) {
        viewModel.dispatchIntent(Initialize)
    }

    // Update camera when user location is loaded
    LaunchedEffect(state.userLocation) {
        state.userLocation?.let { location ->
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    DEFAULT_ZOOM,
                )
        }
    }

    // Observe camera idle to load pins for new viewport
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            .filter { !it } // Camera stopped moving
            .debounce(CAMERA_DEBOUNCE_MS)
            .collect {
                val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                if (bounds != null) {
                    viewModel.dispatchIntent(OnViewportChanged(bounds))
                }
            }
    }

    // Collect effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NavigateBack -> navController.popBackStack()
            }
        }
    }

    // Handle system back button
    BackHandler {
        viewModel.dispatchIntent(OnBackPressed)
    }

    FullscreenMapContent(
        state = state,
        cameraPositionState = cameraPositionState,
        modifier = modifier,
        onBackClick = { viewModel.dispatchIntent(OnBackPressed) },
        onAnimalClick = { viewModel.dispatchIntent(OnAnimalTapped(it)) },
        onPopupDismiss = { viewModel.dispatchIntent(OnPopupDismissed) },
        onRetryClick = { viewModel.dispatchIntent(OnRetryTapped) },
    )
}
