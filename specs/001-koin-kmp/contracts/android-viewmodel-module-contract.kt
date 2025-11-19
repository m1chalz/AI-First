/**
 * Koin Android ViewModel Module Contract
 * 
 * Location: /composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt
 * Platform: Android only
 * Visibility: Internal to Android module
 */

package com.intive.aifirst.petspot.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android ViewModel module.
 * 
 * This module MUST contain:
 * - Android ViewModels (Jetpack Compose / XML views)
 * - Scoped to ViewModel lifecycle
 * 
 * This module MUST use:
 * - viewModel { } scope (not single or factory)
 * 
 * This module MUST NOT contain:
 * - Repository implementations (use androidDataModule)
 * - UI components
 * - Android Context references (ViewModels should be Context-free)
 * 
 * @return Koin module with Android ViewModels
 */
val androidViewModelModule: Module = module {
    // Example future dependencies (commented for reference):
    
    // ViewModels - viewModel scope (lifecycle-aware)
    // viewModel { 
    //     PetListViewModel(
    //         getPetsUseCase = get(),
    //         deletePetUseCase = get()
    //     ) 
    // }
    
    // viewModel { 
    //     PetDetailViewModel(
    //         petId = get(), // Navigation argument (passed from Compose)
    //         getPetByIdUseCase = get(),
    //         savePetUseCase = get()
    //     ) 
    // }
}

/**
 * Contract guarantees:
 * 
 * 1. ViewModels are scoped to their lifecycle (auto-cleared on destroy)
 * 2. ViewModels survive configuration changes (rotation, dark mode)
 * 3. ViewModels have access to use cases via constructor injection
 * 4. ViewModels are created lazily (only when first requested)
 * 5. Multiple requests return the same ViewModel instance (within scope)
 * 
 * Error cases:
 * - Using single { } instead of viewModel { }: ViewModels not cleared → memory leak
 * - Missing use case: NoBeanDefFoundException at ViewModel creation
 * - Circular dependency: DefinitionOverrideException at ViewModel creation
 */

/**
 * Consumption contract (Jetpack Compose):
 * 
 * ```kotlin
 * @Composable
 * fun PetListScreen(
 *     viewModel: PetListViewModel = koinViewModel()
 * ) {
 *     val uiState by viewModel.uiState.collectAsState()
 *     
 *     LaunchedEffect(Unit) {
 *         viewModel.loadPets()
 *     }
 *     
 *     // UI implementation
 * }
 * ```
 * 
 * With navigation parameters:
 * ```kotlin
 * @Composable
 * fun PetDetailScreen(
 *     petId: String,
 *     viewModel: PetDetailViewModel = koinViewModel()
 * ) {
 *     LaunchedEffect(petId) {
 *         viewModel.loadPet(petId)
 *     }
 *     
 *     // UI implementation
 * }
 * ```
 */

/**
 * Testing contract:
 * 
 * ViewModel unit tests:
 * 
 * ```kotlin
 * class PetListViewModelTest : KoinTest {
 *     private lateinit var viewModel: PetListViewModel
 *     
 *     @Before
 *     fun setup() {
 *         startKoin {
 *             modules(module {
 *                 single<GetPetsUseCase> { FakeGetPetsUseCase() }
 *                 viewModel { PetListViewModel(get()) }
 *             })
 *         }
 *         viewModel = get()
 *     }
 *     
 *     @Test
 *     fun `should load pets on init`() = runTest {
 *         // Given - setup complete
 *         
 *         // When
 *         viewModel.loadPets()
 *         
 *         // Then
 *         val state = viewModel.uiState.first()
 *         assertTrue(state is UiState.Success)
 *     }
 * }
 * ```
 */

/**
 * Lifecycle contract:
 * 
 * ViewModel lifecycle phases:
 * 
 * 1. CREATED: ViewModel instantiated by Koin (viewModel { } scope)
 * 2. ACTIVE: ViewModel used by Composable (collecting state flows)
 * 3. CLEARED: Composable leaves composition → ViewModel.onCleared()
 * 4. DESTROYED: ViewModel removed from Koin scope
 * 
 * Configuration changes (rotation):
 * - ViewModel survives configuration change (not recreated)
 * - Composable recreated, but retrieves same ViewModel instance
 * 
 * Navigation:
 * - Navigate away: ViewModel cleared (leaves back stack)
 * - Navigate back: New ViewModel instance created
 */

/**
 * Best practices:
 * 
 * 1. ViewModels SHOULD depend on use cases, not repositories directly
 *    ✅ viewModel { PetListViewModel(getPetsUseCase = get()) }
 *    ❌ viewModel { PetListViewModel(petRepository = get()) }
 * 
 * 2. ViewModels MUST NOT hold Android Context
 *    ❌ viewModel { PetListViewModel(context = androidContext()) }
 *    ✅ Use Application Context via AndroidViewModel if needed
 * 
 * 3. ViewModels SHOULD use StateFlow for UI state
 *    ✅ val uiState: StateFlow<UiState>
 *    ❌ val pets: LiveData<List<Pet>> (LiveData discouraged for new code)
 * 
 * 4. ViewModels SHOULD use viewModelScope for coroutines
 *    ✅ viewModelScope.launch { ... }
 *    ❌ GlobalScope.launch { ... }
 */

