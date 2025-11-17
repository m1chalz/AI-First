import { AsyncLocalStorage } from 'async_hooks';

/**
 * Request context stored in AsyncLocalStorage.
 */
interface RequestContext {
  requestId: string;
}

/**
 * AsyncLocalStorage instance for propagating request context throughout
 * the async call chain without explicit parameter passing.
 *
 * This enables automatic request ID correlation in all application logs
 * generated during request processing.
 */
export const requestContextStorage = new AsyncLocalStorage<RequestContext>();

/**
 * Retrieves the current request ID from AsyncLocalStorage context.
 *
 * This function can be called from any code within the request lifecycle
 * (route handlers, services, utilities) to access the request ID without
 * explicit parameter passing.
 *
 * @returns The request ID if within request context, undefined otherwise
 * @example
 * // In a service without access to req object
 * const requestId = getRequestId();
 * logger.info({ requestId }, 'processing business logic');
 */
export function getRequestId(): string | undefined {
  return requestContextStorage.getStore()?.requestId;
}

/**
 * Sets the request context in AsyncLocalStorage.
 *
 * This function is typically called by middleware at the start of request
 * processing. Application code rarely needs to call this directly.
 *
 * @param context - The request context to store
 * @example
 * // In middleware
 * setRequestContext({ requestId: 'aBc123XyZ9' });
 */
export function setRequestContext(context: RequestContext): void {
  requestContextStorage.enterWith(context);
}

