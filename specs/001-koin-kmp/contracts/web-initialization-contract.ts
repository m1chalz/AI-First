/**
 * Koin Web Initialization Contract
 * 
 * Location: /webApp/src/di/koinSetup.ts
 * Platform: Web only
 * Visibility: Internal to web module
 */

import { startKoin, domainModule } from 'shared'; // Kotlin/JS exports

/**
 * Initializes Koin DI container for web platform.
 * Consumes shared domain module from Kotlin/JS.
 * 
 * This function MUST:
 * - Initialize Koin before React app renders
 * - Load shared domain module from Kotlin/JS
 * - Provide access to Koin container for dependency resolution
 * 
 * This function MUST NOT:
 * - Initialize Koin multiple times (singleton pattern)
 * - Be called after React app renders (dependencies won't be available)
 * 
 * @throws Never throws (initialization errors crash app with clear message)
 * 
 * @example
 * ```typescript
 * // index.tsx
 * import { initializeKoin } from './di/koinSetup';
 * 
 * initializeKoin(); // Before ReactDOM.createRoot
 * 
 * const root = ReactDOM.createRoot(document.getElementById('root')!);
 * root.render(<App />);
 * ```
 */
export function initializeKoin(): void {
    startKoin({
        modules: [domainModule] // Shared module from Kotlin/JS
        // Future: Web-specific modules can be added here
    });
}

/**
 * Contract guarantees:
 * 
 * 1. Koin is initialized before React app renders
 * 2. Shared domain module is loaded and accessible
 * 3. Dependencies can be resolved via get<T>() from 'shared'
 * 4. Initialization errors crash app at startup (fail-fast)
 * 5. Function is idempotent (safe to call multiple times)
 * 
 * Error cases:
 * - Koin already started: Warning in console, no error thrown
 * - Missing shared module: Crash with clear error message
 * - Kotlin/JS interop failure: Compilation error
 */

/**
 * Dependency resolution from TypeScript:
 * 
 * In custom hooks:
 * ```typescript
 * import { get } from 'shared'; // Kotlin/JS Koin helper
 * import { GetPetsUseCase } from 'shared'; // Kotlin/JS type export
 * 
 * export function usePets() {
 *     // Inject use case from Koin
 *     const getPetsUseCase = get<GetPetsUseCase>();
 *     
 *     const [pets, setPets] = useState<Pet[]>([]);
 *     const [loading, setLoading] = useState(false);
 *     
 *     const loadPets = async () => {
 *         setLoading(true);
 *         try {
 *             const result = await getPetsUseCase.invoke();
 *             setPets(result);
 *         } finally {
 *             setLoading(false);
 *         }
 *     };
 *     
 *     return { pets, loading, loadPets };
 * }
 * ```
 * 
 * In React components:
 * ```typescript
 * export function PetList() {
 *     const { pets, loading, loadPets } = usePets();
 *     
 *     useEffect(() => {
 *         loadPets();
 *     }, []);
 *     
 *     if (loading) return <div>Loading...</div>;
 *     
 *     return (
 *         <ul>
 *             {pets.map(pet => (
 *                 <li key={pet.id}>{pet.name}</li>
 *             ))}
 *         </ul>
 *     );
 * }
 * ```
 */

/**
 * Testing contract:
 * 
 * Unit tests for hooks:
 * 
 * ```typescript
 * import { renderHook, act } from '@testing-library/react';
 * import { usePets } from './usePets';
 * 
 * // Mock Koin get() function
 * vi.mock('shared', () => ({
 *     get: vi.fn(() => new FakeGetPetsUseCase())
 * }));
 * 
 * describe('usePets', () => {
 *     it('should load pets successfully', async () => {
 *         // Given - mock use case
 *         const { result } = renderHook(() => usePets());
 *         
 *         // When - load pets
 *         await act(async () => {
 *             await result.current.loadPets();
 *         });
 *         
 *         // Then - pets loaded
 *         expect(result.current.pets).toHaveLength(2);
 *         expect(result.current.loading).toBe(false);
 *     });
 * });
 * ```
 */

/**
 * Initialization order:
 * 
 * 1. index.tsx entry point
 * 2. initializeKoin() → startKoin from Kotlin/JS
 * 3. Shared domainModule loaded
 * 4. ReactDOM.createRoot → App renders
 * 5. Components/hooks resolve dependencies via get<T>()
 */

/**
 * Alternative patterns (for future consideration):
 * 
 * 1. React Context for DI:
 * ```typescript
 * const DependencyContext = createContext<DependencyContainer | null>(null);
 * 
 * export function DependencyProvider({ children }) {
 *     const container = useMemo(() => ({
 *         getPetsUseCase: get<GetPetsUseCase>()
 *     }), []);
 *     
 *     return (
 *         <DependencyContext.Provider value={container}>
 *             {children}
 *         </DependencyContext.Provider>
 *     );
 * }
 * 
 * export function useDependency<T>(key: keyof DependencyContainer): T {
 *     const context = useContext(DependencyContext);
 *     return context[key] as T;
 * }
 * ```
 * 
 * 2. Standalone DI container (without Kotlin/JS):
 * ```typescript
 * class DependencyContainer {
 *     private static instance: DependencyContainer;
 *     
 *     private constructor() {
 *         this.getPetsUseCase = new GetPetsUseCase(new PetRepository());
 *     }
 *     
 *     static getInstance() {
 *         if (!this.instance) {
 *             this.instance = new DependencyContainer();
 *         }
 *         return this.instance;
 *     }
 * }
 * ```
 */

/**
 * Best practices:
 * 
 * 1. Initialize Koin in index.tsx, before ReactDOM.createRoot
 *    ✅ initializeKoin(); const root = ReactDOM.createRoot(...);
 *    ❌ const root = ReactDOM.createRoot(...); initializeKoin();
 * 
 * 2. Resolve dependencies in hooks, not in components
 *    ✅ export function usePets() { const useCase = get<GetPetsUseCase>(); }
 *    ❌ export function PetList() { const useCase = get<GetPetsUseCase>(); }
 * 
 * 3. Use TypeScript types from Kotlin/JS exports
 *    ✅ import { GetPetsUseCase, Pet } from 'shared';
 *    ❌ const useCase: any = get('GetPetsUseCase');
 * 
 * 4. Test with mocked get() function, not real Koin
 *    ✅ vi.mock('shared', () => ({ get: vi.fn(() => new FakeUseCase()) }));
 *    ❌ initializeKoin(); // In tests (slow, brittle)
 */

/**
 * Kotlin/JS Export Requirements:
 * 
 * Shared module MUST export:
 * 
 * ```kotlin
 * // /shared/src/jsMain/kotlin/.../di/KoinJs.kt
 * @OptIn(ExperimentalJsExport::class)
 * @JsExport
 * fun startKoin(config: dynamic) {
 *     org.koin.core.context.startKoin {
 *         modules(domainModule)
 *     }
 * }
 * 
 * @OptIn(ExperimentalJsExport::class)
 * @JsExport
 * fun <T> get(): T {
 *     return org.koin.core.context.GlobalContext.get().get()
 * }
 * 
 * @OptIn(ExperimentalJsExport::class)
 * @JsExport
 * val domainModule = com.intive.aifirst.petspot.di.domainModule
 * ```
 */

