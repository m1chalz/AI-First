package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.data.AnimalRepositoryImpl
import com.intive.aifirst.petspot.domain.repositories.AnimalRepository
import org.koin.dsl.module

/**
 * Koin module containing Android data layer dependencies.
 *
 * This module defines Android-specific data implementations:
 * - Repository implementations (HTTP clients, local databases)
 * - Data sources (API clients, DAOs)
 * - Platform-specific services (Android system services)
 *
 * @see org.koin.dsl.module
 */
val dataModule = module {
    // Repository implementations
    single<AnimalRepository> { AnimalRepositoryImpl() }
}

