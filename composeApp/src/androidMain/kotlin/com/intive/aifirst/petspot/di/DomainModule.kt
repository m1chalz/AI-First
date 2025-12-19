package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalByIdUseCase
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalsUseCase
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetRecentAnimalsUseCase
import com.intive.aifirst.petspot.features.reportmissing.domain.usecases.ExtractPhotoMetadataUseCase
import com.intive.aifirst.petspot.features.reportmissing.domain.usecases.SubmitAnnouncementUseCase
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
val domainModule =
    module {
        // Use cases
        factory { GetAnimalsUseCase(get()) }
        factory { GetAnimalByIdUseCase(get()) }
        factory { GetRecentAnimalsUseCase(get()) }
        factory { ExtractPhotoMetadataUseCase(get()) }
        factory { SubmitAnnouncementUseCase(get()) }
    }
