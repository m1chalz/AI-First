package com.intive.aifirst.petspot.di

import com.intive.aifirst.petspot.BuildConfig
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.data.AnimalRepositoryImpl
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import com.intive.aifirst.petspot.features.reportmissing.data.repositories.AnnouncementRepositoryImpl
import com.intive.aifirst.petspot.features.reportmissing.data.repositories.PhotoMetadataRepositoryImpl
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.AnnouncementRepository
import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.PhotoMetadataRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/** Exception for API error responses with status code and body */
class ApiException(val statusCode: Int, val errorBody: String) : Exception("HTTP $statusCode: $errorBody")

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
                            encodeDefaults = true // Include properties with default values in JSON
                            explicitNulls = false // Omit null fields instead of sending "field": null
                        },
                    )
                }
                // Throw exceptions on non-2xx responses
                HttpResponseValidator {
                    validateResponse { response ->
                        val statusCode = response.status.value
                        if (statusCode >= 400) {
                            val errorBody = response.bodyAsText()
                            throw ApiException(statusCode, errorBody)
                        }
                    }
                }
                // OkHttp native logging interceptor (more detailed than Ktor's)
                engine {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            },
                        )
                    }
                }
            }
        }

        // API Clients
        single { AnnouncementApiClient(get(), BuildConfig.API_BASE_URL) }

        // ContentResolver for photo operations (injected, NOT Context)
        single { androidApplication().contentResolver }

        // Repository implementations
        single<AnimalRepository> { AnimalRepositoryImpl(get(), BuildConfig.API_BASE_URL) }
        single<PhotoMetadataRepository> { PhotoMetadataRepositoryImpl(get()) }

        // Announcement repository for 2-step submission (uses AnnouncementApiClient)
        single<AnnouncementRepository> {
            AnnouncementRepositoryImpl(
                apiClient = get(),
                contentResolver = get(),
            )
        }
    }
