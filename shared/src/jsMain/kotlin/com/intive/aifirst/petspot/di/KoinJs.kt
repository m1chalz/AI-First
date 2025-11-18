@file:OptIn(ExperimentalJsExport::class)

package com.intive.aifirst.petspot.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Initializes Koin dependency injection for JavaScript/Web platform.
 *
 * Call this function once at application startup before accessing any dependencies.
 * Typically called from webApp/src/di/koinSetup.ts.
 *
 * @param modules Array of Koin modules to register (defaults to domainModule)
 * @return KoinApplication instance for configuration if needed
 */
@JsExport
fun startKoinJs(modules: Array<Module> = arrayOf(domainModule)): KoinApplication {
    return startKoin {
        modules(modules.toList())
    }
}

/**
 * Retrieves a dependency from the Koin container.
 *
 * Note: Cannot use reified generics with @JsExport. TypeScript code should access
 * Koin directly through specific getter functions or use dependency injection
 * at component boundaries.
 *
 * For TypeScript usage, define specific getter functions like:
 * ```kotlin
 * @JsExport
 * fun getGetPetsUseCase(): GetPetsUseCase = org.koin.core.context.GlobalContext.get().get()
 * ```
 */
// Not exported to JS - reified types not supported
inline fun <reified T : Any> getKoin(): T {
    return org.koin.core.context.GlobalContext.get().get()
}

/**
 * Domain module exported for consumption in JavaScript/TypeScript.
 * Re-exported for convenience when initializing Koin from web app.
 */
@JsExport
val domainModuleJs: Module = domainModule

