/**
 * Koin Domain Module Contract
 * 
 * Location: /shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt
 * Platform: All (Android, iOS, Web)
 * Visibility: Public API (exported to all platforms)
 */

package com.intive.aifirst.petspot.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Domain module containing shared business logic dependencies.
 * Available to all platforms via Koin DI.
 * 
 * This module MUST contain only platform-agnostic dependencies:
 * - Use cases (business operations)
 * - Domain services (validators, formatters)
 * - Domain-level singletons
 * 
 * This module MUST NOT contain:
 * - UI components or ViewModels
 * - Platform-specific implementations
 * - Repository implementations (use interfaces only)
 * 
 * @return Koin module with domain dependencies
 */
val domainModule: Module = module {
    // Example future dependencies (commented for reference):
    
    // Use cases - singleton scope (stateless business logic)
    // single { GetPetsUseCase(repository = get()) }
    // single { SavePetUseCase(repository = get()) }
    // single { DeletePetUseCase(repository = get()) }
    
    // Domain services - factory scope (lightweight utilities)
    // factory { PetValidator() }
    // factory { DateFormatter() }
    
    // Domain-level singletons
    // single { PetDomainLogger() }
}

/**
 * Contract guarantees:
 * 
 * 1. Module is safe to import on all platforms (no platform-specific dependencies)
 * 2. All dependencies are defined with explicit types
 * 3. Constructor injection is used (parameter injection via get())
 * 4. Module can be loaded multiple times (idempotent)
 * 5. All dependencies are resolvable at app startup (no lazy failures)
 * 
 * Error cases:
 * - Missing dependency: Koin throws NoBeanDefFoundException at startup
 * - Circular dependency: Koin throws DefinitionOverrideException at startup
 * - Type mismatch: Compilation error (type safety via Kotlin)
 */

/**
 * Testing contract:
 * 
 * Unit tests can override this module with test implementations:
 * 
 * ```kotlin
 * class MyUseCaseTest : KoinTest {
 *     @Before
 *     fun setup() {
 *         startKoin {
 *             modules(module {
 *                 single<PetRepository> { FakePetRepository() }
 *                 single { GetPetsUseCase(get()) }
 *             })
 *         }
 *     }
 * }
 * ```
 */

/**
 * Platform consumption:
 * 
 * Android:
 * ```kotlin
 * class PetSpotApplication : Application() {
 *     override fun onCreate() {
 *         startKoin {
 *             modules(domainModule, androidDataModule)
 *         }
 *     }
 * }
 * ```
 * 
 * iOS:
 * ```swift
 * func initKoin() {
 *     KoinKt.doInitKoin()
 * }
 * 
 * class ViewModel {
 *     let useCase: GetPetsUseCase = KoinKt.get()
 * }
 * ```
 * 
 * Web:
 * ```typescript
 * import { startKoin, domainModule } from 'shared';
 * 
 * startKoin({ modules: [domainModule] });
 * 
 * const useCase = get<GetPetsUseCase>();
 * ```
 */

