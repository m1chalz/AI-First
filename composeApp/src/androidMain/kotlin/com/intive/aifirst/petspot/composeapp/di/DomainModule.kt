package com.intive.aifirst.petspot.composeapp.di

import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalsUseCase
import org.koin.dsl.module

/**
 * Koin module containing Android domain dependencies.
 *
 * This module defines domain-layer dependencies for the Android platform:
 * - Use cases (business logic)
 * - Domain services
 * - Repository interfaces (implementations provided by DataModule)
 *
 * @see org.koin.dsl.module
 */
val domainModule = module {
    // Use cases
    factory { GetAnimalsUseCase(get()) }
}

