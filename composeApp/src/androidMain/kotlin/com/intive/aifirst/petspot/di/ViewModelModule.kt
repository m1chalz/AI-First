package com.intive.aifirst.petspot.di

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
 * Example usage (future):
 * ```kotlin
 * val viewModelModule = module {
 *     viewModel { PetListViewModel(get()) }
 *     viewModel { PetDetailViewModel(get(), get()) }
 * }
 * ```
 *
 * Usage in Compose:
 * ```kotlin
 * @Composable
 * fun PetListScreen(viewModel: PetListViewModel = koinViewModel()) {
 *     // ViewModel is automatically injected with dependencies
 * }
 * ```
 *
 * @see org.koin.androidx.viewmodel.dsl.viewModel
 * @see org.koin.androidx.compose.koinViewModel
 */
val viewModelModule =
    module {
        // Empty module - will be populated when ViewModels are added
        // Future dependencies: ViewModels for each screen
    }
