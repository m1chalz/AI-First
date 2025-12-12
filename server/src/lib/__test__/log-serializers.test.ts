import { describe, it, expect } from 'vitest';
import { Buffer } from 'buffer';
import { truncateBody, isBinaryContent, serializeBody, TruncatedBody } from '../log-serializers';

describe('logSerializers', () => {
  describe('truncateBody', () => {
    it('should return body as-is if under 10KB limit', () => {
      // Given
      const body = { message: 'Hello', data: [1, 2, 3] };

      // When
      const result = truncateBody(body);

      // Then
      expect(result).toEqual(body);
    });

    it('should truncate body at exactly 10240 bytes', () => {
      // Given
      const largeBody = 'x'.repeat(15000);

      // When
      const result = truncateBody(largeBody) as TruncatedBody;

      // Then
      expect(result).toHaveProperty('content');
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize', 15000);
      expect(result.content).toHaveLength(10240);
    });

    it('should truncate JSON-stringified objects', () => {
      // Given
      const largeObject = { data: 'x'.repeat(15000) };

      // When
      const result = truncateBody(largeObject) as TruncatedBody;

      // Then
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize');
      expect(result.originalSize).toBeGreaterThan(10240);
    });

    it.each([null, undefined])('should return %s as-is', (body) => {
      // When
      const result = truncateBody(body);

      // Then
      expect(result).toBe(body);
    });

    it.each([
      [10240, false],
      [10241, true]
    ])('should handle edge case at %i bytes', (size, shouldTruncate) => {
      // Given
      const body = 'x'.repeat(size);

      // When
      const result = truncateBody(body);

      // Then
      if (shouldTruncate) {
        const truncated = result as TruncatedBody;
        expect(truncated).toHaveProperty('truncated', true);
        expect(truncated).toHaveProperty('originalSize', size);
        expect(truncated.content).toHaveLength(10240);
      } else {
        expect(result).toBe(body);
      }
    });
  });

  describe('isBinaryContent', () => {
    it.each(['image/jpeg', 'image/png', 'application/gzip', 'application/octet-stream'])('should detect %s as binary', (mimeType) => {
      // When
      const result = isBinaryContent(mimeType);

      // Then
      expect(result).toBe(true);
    });

    it.each(['application/json', 'text/plain', 'text/csv'])('should NOT detect %s as binary', (mimeType) => {
      // When
      const result = isBinaryContent(mimeType);

      // Then
      expect(result).toBe(false);
    });

    it('should return false for undefined content type', () => {
      // When/Then
      expect(isBinaryContent(undefined)).toBe(false);
    });
  });

  describe('serializeBody', () => {
    it('should omit binary content with metadata', () => {
      // Given
      const body = Buffer.from('fake-image-data');
      const contentType = 'image/jpeg';
      const headers = { 'content-length': '245678' };

      // When
      const result = serializeBody(body, contentType, headers);

      // Then
      expect(result).toEqual({
        binaryOmitted: true,
        contentType: 'image/jpeg',
        contentLength: '245678'
      });
    });

    it('should truncate large non-binary content', () => {
      // Given
      const body = 'x'.repeat(15000);
      const contentType = 'text/plain';
      const headers = {};

      // When
      const result = serializeBody(body, contentType, headers) as TruncatedBody;

      // Then
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize', 15000);
    });

    it('should return normal content as-is', () => {
      // Given
      const body = { message: 'Hello', data: [1, 2, 3] };
      const contentType = 'application/json';
      const headers = {};

      // When
      const result = serializeBody(body, contentType, headers);

      // Then
      expect(result).toEqual(body);
    });

    it('should handle missing Content-Length header for binary', () => {
      // Given
      const body = Buffer.from('data');
      const contentType = 'application/pdf';
      const headers = {};

      // When
      const result = serializeBody(body, contentType, headers);

      // Then
      expect(result).toEqual({
        binaryOmitted: true,
        contentType: 'application/pdf',
        contentLength: 'unknown'
      });
    });

    it('should prioritize binary detection over truncation', () => {
      // Given
      const body = Buffer.alloc(15000);
      const contentType = 'image/png';
      const headers = { 'content-length': '15000' };

      // When
      const result = serializeBody(body, contentType, headers);

      // Then
      expect(result).toHaveProperty('binaryOmitted', true);
      expect(result).not.toHaveProperty('truncated');
    });
  });
});
