package com.intive.aifirst.petspot.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/**
 * Initializes Koin dependency injection for iOS/Native platform.
 *
 * This function should be called once from Swift code at application startup,
 * typically in the @main app struct's init() method via KoinInitializer.swift.
 *
 * Note: This function is exported to Swift as `doInitKoin()` due to Kotlin/Native
 * naming conventions (functions returning non-Unit values get "do" prefix).
 *
 * Example Swift usage:
 * ```swift
 * import Shared
 *
 * @main
 * struct PetSpotApp: App {
 *     init() {
 *         KoinInitializer().initialize()
 *         // Or directly: _ = KoinIosKt.doInitKoin()
 *     }
 * }
 * ```
 *
 * @return KoinApplication instance for additional configuration if needed
 */
fun initKoin(): KoinApplication {
    return startKoin {
        modules(domainModule)
    }
}

