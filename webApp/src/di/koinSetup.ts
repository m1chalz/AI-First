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
 * Note: Due to JavaScript/TypeScript limitations, specific getter functions should be
 * exported from the shared module for each dependency type.
 *
 * Example pattern (to be implemented in shared module when needed):
 * ```kotlin
 * // In shared/src/jsMain/.../KoinJs.kt
 * @JsExport
 * fun getGetPetsUseCase(): GetPetsUseCase = 
 *     org.koin.core.context.GlobalContext.get().get()
 * ```
 *
 * Then use in TypeScript:
 * ```typescript
 * import { getGetPetsUseCase } from 'shared';
 *
 * export function usePets() {
 *   const getPetsUseCase = getGetPetsUseCase();
 *   // use it
 * }
 * ```
 *
 * @template T The type of dependency to retrieve
 * @returns The requested dependency instance
 * @throws Error Not implemented - use specific getter functions from shared module
 */
export function getKoin<T>(): T {
  throw new Error(
    'getKoin() not available - use specific getter functions exported from shared module (e.g., getGetPetsUseCase())'
  );
}

