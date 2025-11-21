package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.domain.usecases.GetAnimalsUseCase
import org.koin.dsl.module

/**
 * Koin module containing shared domain dependencies.
 *
 * This module defines domain-layer dependencies that are available across all platforms:
 * - Use cases (business logic)
 * - Domain services
 * - Repository interfaces (implementations provided by platform-specific modules)
 *
 * @see org.koin.dsl.module
 */
val domainModule =
    module {
        // Use cases
        factory { GetAnimalsUseCase(get()) }
    }
