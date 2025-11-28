package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.BuildConfig
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.data.AnimalRepositoryImpl
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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
val dataModule =
    module {
        // Ktor HttpClient with OkHttp engine
        single {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                }
                if (BuildConfig.DEBUG) {
                    install(Logging) {
                        level = LogLevel.BODY
                    }
                }
            }
        }

        // API Clients
        single { AnnouncementApiClient(get(), BuildConfig.API_BASE_URL) }

        // Repository implementations
        single<AnimalRepository> { AnimalRepositoryImpl(get()) }
    }
