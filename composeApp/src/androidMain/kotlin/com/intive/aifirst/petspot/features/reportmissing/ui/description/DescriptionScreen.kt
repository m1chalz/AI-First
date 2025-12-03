package com.intive.aifirst.petspot.features.reportmissing.ui.description

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intive.aifirst.petspot.core.util.openAppSettings
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.AnimalDescriptionViewModel

/**
 * State host composable for Description screen (Step 3/4).
 * Collects state from ViewModel, handles effects, and dispatches intents.
 *
 * Following the same pattern as ChipNumberScreen and PhotoScreen (no Scaffold).
 *
 * @param viewModel AnimalDescriptionViewModel with hybrid pattern
 * @param modifier Modifier for the component
 */
@Composable
fun DescriptionScreen(
    viewModel: AnimalDescriptionViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle one-off effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AnimalDescriptionUiEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is AnimalDescriptionUiEffect.NavigateToContactDetails -> {
                    // Handled by navigation callback in ViewModel
                }
                is AnimalDescriptionUiEffect.NavigateBack -> {
                    // Handled by navigation callback in ViewModel
                }
                is AnimalDescriptionUiEffect.OpenLocationSettings -> {
                    context.openAppSettings()
                }
            }
        }
    }

    // Handle system back button/gesture
    BackHandler {
        viewModel.handleIntent(AnimalDescriptionUserIntent.BackClicked)
    }

    AnimalDescriptionContent(
        state = state,
        modifier = modifier,
        onBackClick = { viewModel.handleIntent(AnimalDescriptionUserIntent.BackClicked) },
        onDateClick = { viewModel.handleIntent(AnimalDescriptionUserIntent.OpenDatePicker) },
        onDateSelected = { date -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateDate(date)) },
        onDatePickerDismiss = { viewModel.handleIntent(AnimalDescriptionUserIntent.DismissDatePicker) },
        onPetNameChanged = { name -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdatePetName(name)) },
        onSpeciesSelected = { species -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies(species)) },
        onRaceChanged = { race -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace(race)) },
        onGenderSelected = { gender -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(gender)) },
        onAgeChanged = { age -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateAge(age)) },
        onRequestGps = { viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition) },
        onLatitudeChanged = { lat -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude(lat)) },
        onLongitudeChanged = { lon -> viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude(lon)) },
        onContinueClick = { viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked) },
    )
}
