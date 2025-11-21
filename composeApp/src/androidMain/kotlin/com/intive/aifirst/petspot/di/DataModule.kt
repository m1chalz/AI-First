package com.intive.aifirst.petspot.di

import org.koin.dsl.module

/**
 * Koin module containing Android data layer dependencies.
 *
 * This module defines Android-specific data implementations:
 * - Repository implementations (HTTP clients, local databases)
 * - Data sources (API clients, DAOs)
 * - Platform-specific services (Android system services)
 *
 * Example usage (future):
 * ```kotlin
 * val dataModule = module {
 *     single<PetRepository> { PetRepositoryImpl(get(), get()) }
 *     single { PetApi(get()) }
 *     single { PetDatabase.getInstance(androidContext()) }
 * }
 * ```
 *
 * @see org.koin.dsl.module
 */
val dataModule =
    module {
        // Empty module - will be populated when repository implementations are added
        // Future dependencies: repositories, API clients, databases, data sources
    }
