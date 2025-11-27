package com.intive.aifirst.petspot.di

import android.content.Context
import android.location.LocationManager
import com.intive.aifirst.petspot.data.AndroidPermissionChecker
import com.intive.aifirst.petspot.data.repositories.LocationRepositoryImpl
import com.intive.aifirst.petspot.domain.repositories.LocationRepository
import com.intive.aifirst.petspot.domain.usecases.CheckLocationPermissionUseCase
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.domain.usecases.PermissionChecker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for location-related dependencies.
 * Provides LocationRepository, PermissionChecker, and location-related use cases.
 */
val locationModule =
    module {
        // Android system services
        single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

        // Permission checker
        single<PermissionChecker> { AndroidPermissionChecker(androidContext()) }

        // Repository: LocationManager-based implementation
        single<LocationRepository> { LocationRepositoryImpl(get()) }

        // Use Cases
        factory { GetCurrentLocationUseCase(get()) }
        factory { CheckLocationPermissionUseCase(get()) }
    }
