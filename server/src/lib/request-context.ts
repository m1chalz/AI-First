import { AsyncLocalStorage } from 'async_hooks';

interface RequestContext {
  requestId: string;
}

/**
 * Propagates request context throughout async call chain without explicit parameter passing.
 * Enables automatic request ID correlation in all application logs.
 */
export const requestContextStorage = new AsyncLocalStorage<RequestContext>();

/**
 * Retrieves current request ID from AsyncLocalStorage.
 * Returns undefined if called outside request context.
 */
export function getRequestId(): string | undefined {
  return requestContextStorage.getStore()?.requestId;
}

/**
 * Sets request context in AsyncLocalStorage.
 * Typically called by middleware; application code rarely needs this.
 */
export function setRequestContext(context: RequestContext): void {
  requestContextStorage.enterWith(context);
}
