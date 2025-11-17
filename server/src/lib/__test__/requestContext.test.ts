import { describe, it, expect, beforeEach } from 'vitest';
import {
  requestContextStorage,
  getRequestId,
  setRequestContext,
} from '../requestContext';

describe('requestContext', () => {
  beforeEach(() => {
    // Clear any existing context before each test
    requestContextStorage.disable();
  });

  describe('setRequestContext and getRequestId', () => {
    it('should store and retrieve request ID', () => {
      // Given: A request ID
      const requestId = 'aBc123XyZ9';

      // When: Set the request context
      setRequestContext({ requestId });

      // Then: Should be able to retrieve the request ID
      expect(getRequestId()).toBe(requestId);
    });

    it('should return undefined when no context is set', () => {
      // Given: No context is set

      // When: Try to get request ID
      const result = getRequestId();

      // Then: Should return undefined
      expect(result).toBeUndefined();
    });

    it('should maintain separate contexts in async operations', async () => {
      // Given: Two different request IDs
      const requestId1 = 'request001';
      const requestId2 = 'request002';

      // When: Set contexts in separate async operations
      const promise1 = requestContextStorage.run({ requestId: requestId1 }, () => {
        return new Promise<string>((resolve) => {
          setTimeout(() => {
            resolve(getRequestId() || '');
          }, 10);
        });
      });

      const promise2 = requestContextStorage.run({ requestId: requestId2 }, () => {
        return new Promise<string>((resolve) => {
          setTimeout(() => {
            resolve(getRequestId() || '');
          }, 5);
        });
      });

      // Then: Each context should maintain its own request ID
      const [result1, result2] = await Promise.all([promise1, promise2]);
      expect(result1).toBe(requestId1);
      expect(result2).toBe(requestId2);
    });

    it('should propagate context through async call chain', async () => {
      // Given: A request ID and nested async function
      const requestId = 'testId123';

      async function nestedFunction(): Promise<string | undefined> {
        return getRequestId();
      }

      async function middleFunction(): Promise<string | undefined> {
        return await nestedFunction();
      }

      // When: Call nested functions within context
      const result = await requestContextStorage.run({ requestId }, async () => {
        return await middleFunction();
      });

      // Then: Request ID should be accessible in nested calls
      expect(result).toBe(requestId);
    });
  });
});

