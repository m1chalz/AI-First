/**
 * Koin iOS Initialization Contract
 * 
 * Location: /iosApp/iosApp/DI/KoinInitializer.swift
 * Platform: iOS only
 * Visibility: Internal to iOS module
 */

import shared

/**
 * KoinInitializer provides a Swift-friendly interface for initializing Koin DI from Kotlin/Native.
 * 
 * This class MUST:
 * - Initialize Koin before any SwiftUI views are created
 * - Call Koin initialization from shared module (Kotlin/Native)
 * - Provide access to Koin container for dependency resolution
 * 
 * This class MUST NOT:
 * - Define dependencies directly (use shared Koin modules)
 * - Initialize Koin multiple times (singleton pattern)
 */
class KoinInitializer {
    
    /**
     * Initializes Koin DI container with shared modules.
     * Call this function in @main App init() before rendering any views.
     * 
     * - Throws: Never throws (initialization errors crash app with clear message)
     * - Note: This function is idempotent (safe to call multiple times)
     * 
     * Example:
     * ```swift
     * @main
     * struct PetSpotApp: App {
     *     init() {
     *         KoinInitializer().initialize()
     *     }
     * }
     * ```
     */
    func initialize() {
        // Call Kotlin/Native Koin initialization from shared module
        KoinKt.doInitKoin()
    }
}

/**
 * Contract guarantees:
 * 
 * 1. Koin is initialized before any SwiftUI views render
 * 2. Shared domain module is loaded and accessible
 * 3. Dependencies can be resolved via KoinKt.get()
 * 4. Initialization errors crash app at startup (fail-fast)
 * 5. Function is safe to call multiple times (idempotent)
 * 
 * Error cases:
 * - Koin already started: Warning logged, no error thrown
 * - Missing shared module: Crash with clear error message
 * - Kotlin/Native interop failure: Compilation error
 */

/**
 * Dependency resolution from Swift:
 * 
 * In ViewModels:
 * ```swift
 * @MainActor
 * class PetListViewModel: ObservableObject {
 *     private let getPetsUseCase: GetPetsUseCase
 *     
 *     init(getPetsUseCase: GetPetsUseCase? = nil) {
 *         // Inject from Koin or use provided instance (for testing)
 *         self.getPetsUseCase = getPetsUseCase ?? KoinKt.get()
 *     }
 *     
 *     func loadPets() async {
 *         let result = await getPetsUseCase.invoke()
 *         // Handle result
 *     }
 * }
 * ```
 * 
 * In SwiftUI Views:
 * ```swift
 * struct PetListView: View {
 *     @StateObject private var viewModel = PetListViewModel()
 *     
 *     var body: some View {
 *         // View implementation
 *     }
 * }
 * ```
 */

/**
 * Testing contract:
 * 
 * Unit tests for ViewModels:
 * 
 * ```swift
 * class PetListViewModelTests: XCTestCase {
 *     func testLoadPets_whenRepositorySucceeds_shouldUpdateState() async {
 *         // Given - inject fake use case
 *         let fakeUseCase = FakeGetPetsUseCase()
 *         let viewModel = PetListViewModel(getPetsUseCase: fakeUseCase)
 *         
 *         // When
 *         await viewModel.loadPets()
 *         
 *         // Then
 *         XCTAssertEqual(viewModel.pets.count, 2)
 *     }
 * }
 * ```
 * 
 * Note: Tests provide dependencies explicitly (no Koin needed in tests)
 */

/**
 * Initialization order:
 * 
 * 1. @main App entry point
 * 2. init() → KoinInitializer().initialize()
 * 3. Kotlin/Native KoinKt.doInitKoin()
 * 4. Shared domainModule loaded
 * 5. SwiftUI views render
 * 6. ViewModels resolve dependencies via KoinKt.get()
 */

/**
 * Alternative patterns (for future consideration):
 * 
 * 1. Native Swift DI for iOS ViewModels:
 * ```swift
 * class DependencyContainer {
 *     static let shared = DependencyContainer()
 *     
 *     lazy var getPetsUseCase: GetPetsUseCase = {
 *         KoinKt.get()
 *     }()
 * }
 * ```
 * 
 * 2. Property wrappers for injection:
 * ```swift
 * @propertyWrapper
 * struct Inject<T> {
 *     var wrappedValue: T {
 *         KoinKt.get()
 *     }
 * }
 * 
 * class ViewModel {
 *     @Inject var getPetsUseCase: GetPetsUseCase
 * }
 * ```
 */

/**
 * Best practices:
 * 
 * 1. Initialize Koin in @main init(), not in view init()
 *    ✅ @main struct App { init() { KoinInitializer().initialize() } }
 *    ❌ struct View { init() { KoinInitializer().initialize() } }
 * 
 * 2. Inject dependencies in ViewModel init, not in methods
 *    ✅ init() { self.useCase = KoinKt.get() }
 *    ❌ func loadPets() { let useCase = KoinKt.get() }
 * 
 * 3. Provide default parameter for testing
 *    ✅ init(useCase: GetPetsUseCase? = nil) { self.useCase = useCase ?? KoinKt.get() }
 *    ❌ init() { self.useCase = KoinKt.get() } // Hard to test
 * 
 * 4. Use @MainActor for ViewModels
 *    ✅ @MainActor class ViewModel: ObservableObject { }
 *    ❌ class ViewModel: ObservableObject { } // Race conditions possible
 */

