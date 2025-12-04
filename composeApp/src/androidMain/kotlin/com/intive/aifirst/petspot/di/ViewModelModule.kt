package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.features.petdetails.presentation.viewmodels.PetDetailsViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.AnimalDescriptionViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ChipNumberViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.OwnerDetailsViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.PhotoViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ReportMissingViewModel
import com.intive.aifirst.petspot.features.reportmissing.ui.FlowStateHolder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module containing Android ViewModel dependencies.
 *
 * This module defines Android ViewModels that manage UI state and coordinate
 * between the presentation layer (Compose UI) and domain layer (use cases).
 *
 * ViewModels are registered with `viewModel { }` scope for automatic lifecycle management
 * and integration with Jetpack Compose via `koinViewModel()` function.
 *
 * Usage in Compose:
 * ```kotlin
 * @Composable
 * fun AnimalListScreen(viewModel: AnimalListViewModel = koinViewModel()) {
 *     // ViewModel is automatically injected with dependencies
 * }
 * ```
 *
 * @see org.koin.androidx.viewmodel.dsl.viewModel
 * @see org.koin.androidx.compose.koinViewModel
 */
val viewModelModule =
    module {
        // ViewModels
        // AnimalListViewModel: GetAnimalsUseCase (required) + location use cases (optional)
        viewModel { AnimalListViewModel(get(), getOrNull(), getOrNull()) }
        viewModel { PetDetailsViewModel(get()) }

        // Report Missing flow
        // Legacy shared ViewModel (used by Photo, Description, ContactDetails, Summary screens)
        viewModel { ReportMissingViewModel() }

        // FlowStateHolder: Holds shared flow state, scoped to NavGraph
        viewModel { FlowStateHolder() }

        // ChipNumberViewModel: Hybrid pattern with FlowState + navigation callbacks
        // Parameters: flowState, onNavigateToPhoto callback, onExitFlow callback
        viewModel { params ->
            ChipNumberViewModel(
                flowState = params.get(),
                onNavigateToPhoto = params.get(),
                onExitFlow = params.get(),
            )
        }

        // PhotoViewModel: Hybrid pattern with use case + FlowState + navigation callbacks
        // Parameters: flowState, onNavigateToDescription, onNavigateBack
        viewModel { params ->
            PhotoViewModel(
                extractPhotoMetadataUseCase = get(),
                flowState = params.get(),
                onNavigateToDescription = params.get(),
                onNavigateBack = params.get(),
            )
        }

        // AnimalDescriptionViewModel: Hybrid pattern with use case + FlowState + navigation callbacks
        // Parameters: flowState, onNavigateToContactDetails, onNavigateBack
        viewModel { params ->
            AnimalDescriptionViewModel(
                flowState = params.get(),
                getCurrentLocationUseCase = get(),
                onNavigateToContactDetails = params.get(),
                onNavigateBack = params.get(),
            )
        }

        // OwnerDetailsViewModel: MVI pattern with FlowState for cross-screen persistence
        // Parameters: flowState
        viewModel { params ->
            OwnerDetailsViewModel(
                flowState = params.get(),
            )
        }
    }
