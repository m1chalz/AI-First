import { startKoinJs, domainModuleJs } from 'shared';

/**
 * Initializes Koin dependency injection for the web application.
 *
 * This function must be called once at application startup, before any React components
 * are rendered or dependencies are accessed. Typically called from index.tsx before
 * ReactDOM.createRoot().
 *
 * The function configures Koin with the shared domain module, making all domain
 * dependencies (use cases, business logic) available for injection throughout the app.
 *
 * Example usage:
 * ```typescript
 * import { initializeKoin } from './di/koinSetup';
 * import { createRoot } from 'react-dom/client';
 *
 * // Initialize DI before rendering
 * initializeKoin();
 *
 * // Then render app
 * const root = createRoot(document.getElementById('root')!);
 * root.render(<App />);
 * ```
 *
 * @throws Error if Koin is already initialized (calling this twice)
 */
export function initializeKoin(): void {
  try {
    // Initialize Koin with shared domain module
    startKoinJs([domainModuleJs]);
    
    console.log('[Koin] Dependency injection initialized successfully');
  } catch (error) {
    console.error('[Koin] Failed to initialize dependency injection:', error);
    throw error;
  }
}

/**
 * Retrieves a dependency from the Koin container.
 *
 * Use this function to access dependencies in React components, hooks, or services.
 *
 * Example usage in a custom hook:
 * ```typescript
 * import { getKoin } from './di/koinSetup';
 * import { GetPetsUseCase } from 'shared';
 *
 * export function usePets() {
 *   const getPetsUseCase = getKoin<GetPetsUseCase>();
 *
 *   const loadPets = async () => {
 *     const result = await getPetsUseCase.invoke();
 *     // handle result
 *   };
 *
 *   return { loadPets };
 * }
 * ```
 *
 * @template T The type of dependency to retrieve
 * @returns The requested dependency instance
 * @throws Error if dependency is not registered in Koin
 */
export function getKoin<T>(): T {
  // This will be implemented when we consume dependencies
  // For now, it's a placeholder that will be replaced with actual Koin.get() call
  throw new Error('getKoin not yet implemented - add dependencies first');
}

