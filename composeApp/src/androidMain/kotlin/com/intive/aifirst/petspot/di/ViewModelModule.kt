package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.features.petdetails.presentation.viewmodels.PetDetailsViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ReportMissingViewModel
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
        viewModel { ReportMissingViewModel() }
    }
