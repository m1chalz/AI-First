import { describe, it, expect } from 'vitest';
import { generateRequestId } from '../requestIdGenerator';

describe('requestIdGenerator', () => {
  describe('generateRequestId', () => {
    it('should generate a 10-character ID', () => {
      // Given: No setup needed

      // When: Generate a request ID
      const requestId = generateRequestId();

      // Then: ID should be exactly 10 characters
      expect(requestId).toHaveLength(10);
    });

    it('should contain only alphanumeric characters (A-Z, a-z, 0-9)', () => {
      // Given: No setup needed

      // When: Generate a request ID
      const requestId = generateRequestId();

      // Then: ID should match alphanumeric pattern
      expect(requestId).toMatch(/^[A-Za-z0-9]{10}$/);
    });

    it('should generate unique IDs across 1000 calls', () => {
      // Given: A set to store generated IDs
      const generatedIds = new Set<string>();
      const iterations = 1000;

      // When: Generate 1000 request IDs
      for (let i = 0; i < iterations; i++) {
        const requestId = generateRequestId();
        generatedIds.add(requestId);
      }

      // Then: All IDs should be unique (no collisions)
      expect(generatedIds.size).toBe(iterations);
    });

    it('should have no collisions in concurrent generation', () => {
      // Given: Multiple generators running in parallel
      const iterations = 100;

      // When: Generate IDs concurrently
      const promises = Array.from({ length: iterations }, () =>
        Promise.resolve(generateRequestId())
      );

      // Then: All IDs should be unique
      return Promise.all(promises).then((ids) => {
        const uniqueIds = new Set(ids);
        expect(uniqueIds.size).toBe(iterations);
      });
    });
  });
});

