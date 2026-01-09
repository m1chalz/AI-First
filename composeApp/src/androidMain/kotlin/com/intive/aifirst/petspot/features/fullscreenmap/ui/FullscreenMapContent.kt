package com.intive.aifirst.petspot.features.fullscreenmap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapError
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapUiState
import com.intive.aifirst.petspot.features.mapPreview.ui.components.MapPreviewLegend
import com.intive.aifirst.petspot.features.mapPreview.ui.extensions.toMarkerIcon
import com.intive.aifirst.petspot.ui.components.ErrorState
import com.intive.aifirst.petspot.ui.components.FullScreenLoading
import com.intive.aifirst.petspot.ui.components.ImagePlaceholder
import kotlinx.coroutines.launch

/**
 * Stateless composable for Fullscreen Map content.
 * Pure UI component - receives state and callbacks, no ViewModel dependency.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenMapContent(
    state: FullscreenMapUiState,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAnimalClick: (String) -> Unit = {},
    onPopupDismiss: () -> Unit = {},
    onRetryClick: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Smooth dismiss: animate hide first, then update state
    val dismissSheet: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onPopupDismiss()
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .testTag("fullscreenMap.container"),
    ) {
        // Top app bar
        TopAppBar(
            modifier = Modifier.testTag("fullscreenMap.header"),
            title = {
                Text(
                    text = "Pet Locations",
                    modifier = Modifier.testTag("fullscreenMap.title"),
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("fullscreenMap.backButton"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        )

        // Legend row
        MapPreviewLegend(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("fullscreenMap.legend"),
        )

        // Map container - fills remaining space
        Box(modifier = Modifier.weight(1f)) {
            MapContent(
                state = state,
                cameraPositionState = cameraPositionState,
                onAnimalClick = onAnimalClick,
            )

            // Loading overlay
            if (state.isLoadingAnimals) {
                FullScreenLoading(testTag = "fullscreenMap.loading")
            }

            // Error state
            if (state.hasError) {
                ErrorState(
                    error = state.error?.toUserMessage(),
                    onRetryClick = onRetryClick,
                    modifier = Modifier.align(Alignment.Center),
                    testTagPrefix = "fullscreenMap",
                    fillMaxSize = false,
                )
            }
        }
    }

    // Pet details bottom sheet
    if (state.selectedAnimal != null) {
        ModalBottomSheet(
            onDismissRequest = dismissSheet,
            sheetState = sheetState,
        ) {
            AnimalDetailsBottomSheetContent(
                animal = state.selectedAnimal,
                onDismiss = dismissSheet,
                modifier = Modifier.testTag("fullscreenMap.petPopup"),
            )
        }
    }
}

@Composable
private fun MapContent(
    state: FullscreenMapUiState,
    cameraPositionState: CameraPositionState,
    onAnimalClick: (String) -> Unit,
) {
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
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = true,
                compassEnabled = true,
                mapToolbarEnabled = false,
            )
        }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings,
    ) {
        // Animal markers
        state.animalsWithLocation.forEach { animal ->
            val animalLat = animal.location.latitude ?: return@forEach
            val animalLng = animal.location.longitude ?: return@forEach
            val position = LatLng(animalLat, animalLng)

            Marker(
                state = MarkerState(position = position),
                icon = animal.status.toMarkerIcon(),
                title = animal.name,
                onClick = {
                    onAnimalClick(animal.id)
                    true // Consume click
                },
            )
        }
    }
}

@Composable
private fun AnimalDetailsBottomSheetContent(
    animal: Animal,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Photo section: full width, no padding, with overlaid close button (left) and status badge (right)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
        ) {
            SubcomposeAsyncImage(
                model = animal.photoUrl,
                contentDescription = "Photo of ${animal.name}",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .testTag("fullscreenMap.petPopup.photo"),
                contentScale = ContentScale.Crop,
                loading = { ImagePlaceholder(fontSize = 14.sp) },
                error = { ImagePlaceholder(fontSize = 14.sp) },
                success = { SubcomposeAsyncImageContent() },
            )

            // Status badge (top-left)
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 16.dp),
            ) {
                StatusBadgePill(status = animal.status)
            }

            // Close button (top-right) - same style as PetDetailsCloseButton
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .border(0.667.dp, Color(0xFFE5E9EC), CircleShape)
                        .clickable(role = Role.Button, onClick = onDismiss)
                        .testTag("fullscreenMap.petPopup.close"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF101828),
                )
            }
        }

        // Content below image with padding
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Pet name
            Text(
                text = animal.name,
                style = MaterialTheme.typography.titleLarge,
            )

            // Species/breed
            Text(
                text = "${animal.species} • ${animal.breed}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )

            // Location with icon (show coordinates if available)
            if (animal.location.latitude != null && animal.location.longitude != null) {
                InfoRowWithIcon(
                    icon = Icons.Default.LocationOn,
                    iconTint = Color(0xFFE53935),
                    text = "%.4f, %.4f".format(animal.location.latitude, animal.location.longitude),
                )
            }

            // Date with icon
            InfoRowWithIcon(
                icon = Icons.Default.DateRange,
                iconTint = Color(0xFF1976D2),
                text = animal.lastSeenDate,
            )

            // Email with icon
            animal.email?.let { email ->
                InfoRowWithIcon(
                    icon = Icons.Default.Email,
                    iconTint = Color(0xFF1976D2),
                    text = email,
                )
            }

            // Phone with icon
            animal.phone?.let { phone ->
                InfoRowWithIcon(
                    icon = Icons.Default.Phone,
                    iconTint = Color(0xFF43A047),
                    text = phone,
                )
            }

            // Description
            Text(
                text = animal.description.ifBlank { "No additional description provided." },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun InfoRowWithIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = iconTint,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/**
 * Status badge pill matching the style from PetDetailsScreen.
 * Uses the badgeColor from AnimalStatus for consistency.
 */
@Composable
private fun StatusBadgePill(
    status: AnimalStatus,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = Color(status.badgeColor.toColorInt())

    Text(
        text = status.displayName,
        modifier =
            modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(50),
                )
                .padding(horizontal = 12.dp, vertical = 2.dp),
        color = Color.White,
        fontSize = 16.sp,
    )
}

/**
 * Maps error types to user-facing messages.
 * This is the UI layer's responsibility for localization.
 */
private fun FullscreenMapError.toUserMessage(): String =
    when (this) {
        FullscreenMapError.PermissionDenied -> "Location permission required"
        FullscreenMapError.NetworkError -> "Network error. Please check your connection."
        FullscreenMapError.Unknown -> "Failed to load pets. Please try again."
    }

@Preview(showBackground = true)
@Composable
private fun FullscreenMapContentPreview(
    @PreviewParameter(FullscreenMapStateProvider::class) state: FullscreenMapUiState,
) {
    MaterialTheme {
        FullscreenMapContent(
            state = state,
            cameraPositionState = rememberCameraPositionState(),
        )
    }
}
