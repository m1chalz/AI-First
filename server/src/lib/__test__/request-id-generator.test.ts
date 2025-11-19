import { describe, it, expect } from 'vitest';
import { generateRequestId } from '../request-id-generator';

describe('requestIdGenerator', () => {
  describe('generateRequestId', () => {
    it('should generate a 10-character ID', () => {
      // When
      const requestId = generateRequestId();

      // Then
      expect(requestId).toHaveLength(10);
    });

    it('should contain only alphanumeric characters (A-Z, a-z, 0-9)', () => {
      // When
      const requestId = generateRequestId();

      // Then
      expect(requestId).toMatch(/^[A-Za-z0-9]{10}$/);
    });
  });
});

