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
  });
});

