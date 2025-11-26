package com.intive.aifirst.petspot.features.petdetails.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsEffect
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsIntent
import com.intive.aifirst.petspot.features.petdetails.presentation.viewmodels.PetDetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen for displaying pet details.
 * Follows MVI architecture with ViewModel managing state and effects.
 *
 * Features:
 * - Pet photo with status and reward badges
 * - Pet identification information (species, breed, sex, age, microchip)
 * - Location with "Show on map" button
 * - Contact information (phone, email)
 * - Additional description
 * - Back navigation
 * - Loading and error states
 */
@Composable
fun PetDetailsScreen(
    animalId: String,
    navController: NavController,
    viewModel: PetDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PetDetailsEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is PetDetailsEffect.ShowMap -> {
                    val lat = effect.location.latitude
                    val lon = effect.location.longitude
                    if (lat != null && lon != null) {
                        val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "No map app available",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
                is PetDetailsEffect.MapNotAvailable -> {
                    Toast.makeText(
                        context,
                        "Location not available",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    // Load pet on screen creation
    LaunchedEffect(animalId) {
        viewModel.dispatchIntent(PetDetailsIntent.LoadPet(animalId))
    }

    // Render UI based on state
    when {
        state.isLoading -> {
            FullScreenLoading()
        }
        state.error != null -> {
            ErrorState(
                error = state.error,
                onRetryClick = { viewModel.dispatchIntent(PetDetailsIntent.RetryLoad) },
            )
        }
        state.pet != null -> {
            PetDetailsContent(
                pet = state.pet!!,
                onBackClick = { viewModel.dispatchIntent(PetDetailsIntent.NavigateBack) },
                onShowMapClick = { viewModel.dispatchIntent(PetDetailsIntent.ShowOnMap) },
            )
        }
    }
}
