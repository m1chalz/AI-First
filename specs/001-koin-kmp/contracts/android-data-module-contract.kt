/**
 * Koin Android Data Module Contract
 * 
 * Location: /composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DataModule.kt
 * Platform: Android only
 * Visibility: Internal to Android module
 */

package com.intive.aifirst.petspot.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android data module providing platform-specific repository implementations.
 * 
 * This module MUST contain:
 * - Repository implementations (implementing shared interfaces)
 * - API clients (Retrofit, Ktor)
 * - Database instances (Room, SQLDelight)
 * - Data sources (local, remote)
 * 
 * This module MAY use:
 * - Android Context (via androidContext())
 * - Android-specific libraries
 * 
 * This module MUST NOT contain:
 * - ViewModels (use androidViewModelModule)
 * - UI components
 * 
 * @return Koin module with Android data layer dependencies
 */
val androidDataModule: Module = module {
    // Example future dependencies (commented for reference):
    
    // Repository implementations - singleton scope
    // single<PetRepository> { 
    //     PetRepositoryImpl(
    //         api = get(),
    //         database = get()
    //     ) 
    // }
    
    // API clients - singleton scope
    // single { 
    //     PetApi(
    //         httpClient = get(),
    //         baseUrl = "https://api.petspot.com"
    //     ) 
    // }
    
    // Database instances - singleton scope
    // single { 
    //     PetDatabase.getInstance(
    //         context = androidContext()
    //     ) 
    // }
    
    // Data sources - singleton scope
    // single { PetLocalDataSource(database = get()) }
    // single { PetRemoteDataSource(api = get()) }
}

/**
 * Contract guarantees:
 * 
 * 1. All repository interfaces from shared module have implementations
 * 2. Android Context is available via androidContext()
 * 3. All implementations use constructor injection
 * 4. Singletons are thread-safe (Koin guarantees)
 * 5. Resources are properly managed (DB connections, network clients)
 * 
 * Error cases:
 * - Missing Context: androidContext() throws if not provided in startKoin
 * - Missing shared interface: Compilation error (type safety)
 * - Resource leak: Implementation must close resources in onCleared/lifecycle
 */

/**
 * Initialization contract:
 * 
 * This module MUST be loaded in Application.onCreate():
 * 
 * ```kotlin
 * class PetSpotApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         startKoin {
 *             androidLogger(Level.ERROR)
 *             androidContext(this@PetSpotApplication)
 *             modules(
 *                 domainModule,           // Shared dependencies
 *                 androidDataModule,      // This module
 *                 androidViewModelModule  // ViewModels
 *             )
 *         }
 *     }
 * }
 * ```
 * 
 * AndroidManifest.xml:
 * ```xml
 * <application
 *     android:name=".PetSpotApplication"
 *     ...>
 * </application>
 * ```
 */

/**
 * Testing contract:
 * 
 * Android instrumented tests can override repository implementations:
 * 
 * ```kotlin
 * @Before
 * fun setup() {
 *     loadKoinModules(module {
 *         single<PetRepository>(override = true) { 
 *             FakePetRepository() 
 *         }
 *     })
 * }
 * ```
 */

/**
 * Dependency resolution order:
 * 
 * 1. androidContext() → Provides Android Context
 * 2. Database instances → Requires Context
 * 3. API clients → Standalone
 * 4. Data sources → Require API/Database
 * 5. Repository implementations → Require data sources
 * 6. Use cases (from domainModule) → Require repositories
 * 7. ViewModels (from androidViewModelModule) → Require use cases
 */

