package com.intive.aifirst.petspot.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/**
 * Initializes Koin dependency injection for iOS/Native platform.
 *
 * This function should be called once from Swift code at application startup,
 * typically in the @main app struct's init() method via KoinInitializer.swift.
 *
 * Example Swift usage:
 * ```swift
 * import shared
 *
 * @main
 * struct PetSpotApp: App {
 *     init() {
 *         KoinIosKt.doInitKoin()
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

