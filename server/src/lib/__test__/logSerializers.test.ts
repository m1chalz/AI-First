import { describe, it, expect } from 'vitest';
import { Buffer } from 'buffer';
import { truncateBody, isBinaryContent, serializeBody } from '../logSerializers';

describe('logSerializers', () => {
  describe('truncateBody', () => {
    it('should return body as-is if under 10KB limit', () => {
      // Given: A small body
      const body = { message: 'Hello', data: [1, 2, 3] };

      // When: Truncate the body
      const result = truncateBody(body);

      // Then: Should return original body
      expect(result).toEqual(body);
    });

    it('should truncate body at exactly 10240 bytes', () => {
      // Given: A large body exceeding 10KB
      const largeBody = 'x'.repeat(15000);

      // When: Truncate the body
      const result = truncateBody(largeBody);

      // Then: Should return truncated body with metadata
      expect(result).toHaveProperty('content');
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize', 15000);
      expect(result.content).toHaveLength(10240);
    });

    it('should truncate JSON-stringified objects', () => {
      // Given: A large object that exceeds 10KB when stringified
      const largeObject = { data: 'x'.repeat(15000) };

      // When: Truncate the body
      const result = truncateBody(largeObject);

      // Then: Should return truncated body with metadata
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize');
      expect(result.originalSize).toBeGreaterThan(10240);
    });

    it.each([
      null,
      undefined
    ])('should return %s as-is', (body) => {
      // Given: Null or undefined body

      // When: Truncate the body
      const result = truncateBody(body);

      // Then: Should return original value
      expect(result).toBe(body);
    });

    it.each([
      [10240, false],
      [10241, true],
    ])('should handle edge case at %i bytes', (size, shouldTruncate) => {
      // Given: A body at the truncation boundary
      const body = 'x'.repeat(size);

      // When: Truncate the body
      const result = truncateBody(body);

      // Then: Should truncate only if over limit
      if (shouldTruncate) {
        expect(result).toHaveProperty('truncated', true);
        expect(result).toHaveProperty('originalSize', size);
        expect(result.content).toHaveLength(10240);
      } else {
        expect(result).toBe(body);
      }
    });
  });

  describe('isBinaryContent', () => {
    it.each([
      'image/jpeg',
      'image/png',
      'application/gzip',
      'application/octet-stream',
    ])('should detect %s as binary', (mimeType) => {
      // Given: A binary MIME type

      // When: Check if content is binary
      const result = isBinaryContent(mimeType);

      // Then: Should return true
      expect(result).toBe(true);
    });

    it.each([
      'application/json',
      'text/plain',
      'text/csv',
    ])('should NOT detect %s as binary', (mimeType) => {
      // Given: A text-based MIME type

      // When: Check if content is binary
      const result = isBinaryContent(mimeType);

      // Then: Should return false
      expect(result).toBe(false);
    });

    it('should return false for undefined content type', () => {
      // Given: No content type

      // When/Then: Should return false
      expect(isBinaryContent(undefined)).toBe(false);
    });
  });

  describe('serializeBody', () => {
    it('should omit binary content with metadata', () => {
      // Given: Binary content (image)
      const body = Buffer.from('fake-image-data');
      const contentType = 'image/jpeg';
      const headers = { 'content-length': '245678' };

      // When: Serialize the body
      const result = serializeBody(body, contentType, headers);

      // Then: Should return binary omission metadata
      expect(result).toEqual({
        binaryOmitted: true,
        contentType: 'image/jpeg',
        contentLength: '245678',
      });
    });

    it('should truncate large non-binary content', () => {
      // Given: Large text content
      const body = 'x'.repeat(15000);
      const contentType = 'text/plain';
      const headers = {};

      // When: Serialize the body
      const result = serializeBody(body, contentType, headers);

      // Then: Should return truncated body
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize', 15000);
    });

    it('should return normal content as-is', () => {
      // Given: Normal JSON content
      const body = { message: 'Hello', data: [1, 2, 3] };
      const contentType = 'application/json';
      const headers = {};

      // When: Serialize the body
      const result = serializeBody(body, contentType, headers);

      // Then: Should return original body
      expect(result).toEqual(body);
    });

    it('should handle missing Content-Length header for binary', () => {
      // Given: Binary content without Content-Length
      const body = Buffer.from('data');
      const contentType = 'application/pdf';
      const headers = {};

      // When: Serialize the body
      const result = serializeBody(body, contentType, headers);

      // Then: Should return 'unknown' for content length
      expect(result).toEqual({
        binaryOmitted: true,
        contentType: 'application/pdf',
        contentLength: 'unknown',
      });
    });

    it('should prioritize binary detection over truncation', () => {
      // Given: Large binary content (image over 10KB)
      const body = Buffer.alloc(15000);
      const contentType = 'image/png';
      const headers = { 'content-length': '15000' };

      // When: Serialize the body
      const result = serializeBody(body, contentType, headers);

      // Then: Should omit binary (not truncate)
      expect(result).toHaveProperty('binaryOmitted', true);
      expect(result).not.toHaveProperty('truncated');
    });
  });
});
