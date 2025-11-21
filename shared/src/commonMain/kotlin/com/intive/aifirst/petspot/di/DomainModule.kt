package com.intive.aifirst.petspot.di

import org.koin.dsl.module

/**
 * Koin module containing shared domain dependencies.
 *
 * This module defines domain-layer dependencies that are available across all platforms:
 * - Use cases (business logic)
 * - Domain services
 * - Repository interfaces (implementations provided by platform-specific modules)
 *
 * Example usage (future):
 * ```kotlin
 * val domainModule = module {
 *     single { GetPetsUseCase(get()) }
 *     single { SavePetUseCase(get()) }
 * }
 * ```
 *
 * @see org.koin.dsl.module
 */
val domainModule =
    module {
        // Empty module - will be populated when domain logic is added
        // Future dependencies: use cases, domain services, validators
    }
