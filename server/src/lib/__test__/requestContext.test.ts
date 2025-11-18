import { describe, it, expect, beforeEach } from 'vitest';
import {
  requestContextStorage,
  getRequestId,
  setRequestContext,
} from '../requestContext';

describe('requestContext', () => {
  beforeEach(() => {
    requestContextStorage.disable();
  });

  describe('setRequestContext and getRequestId', () => {
    it('should store and retrieve request ID', () => {
      // Given
      const requestId = 'aBc123XyZ9';

      // When
      setRequestContext({ requestId });

      // Then
      expect(getRequestId()).toBe(requestId);
    });

    it('should return undefined when no context is set', () => {
      // When
      const result = getRequestId();

      // Then
      expect(result).toBeUndefined();
    });

    it('should maintain separate contexts in async operations', async () => {
      // Given
      const requestId1 = 'request001';
      const requestId2 = 'request002';

      // When
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

      // Then
      const [result1, result2] = await Promise.all([promise1, promise2]);
      expect(result1).toBe(requestId1);
      expect(result2).toBe(requestId2);
    });

    it('should propagate context through async call chain', async () => {
      // Given
      const requestId = 'testId123';

      async function nestedFunction(): Promise<string | undefined> {
        return getRequestId();
      }

      async function middleFunction(): Promise<string | undefined> {
        return await nestedFunction();
      }

      // When
      const result = await requestContextStorage.run({ requestId }, async () => {
        return await middleFunction();
      });

      // Then
      expect(result).toBe(requestId);
    });
  });
});

