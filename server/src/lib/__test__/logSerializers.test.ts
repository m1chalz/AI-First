import { describe, it, expect } from 'vitest';
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

    it('should return null/undefined as-is', () => {
      // Given: Null or undefined body

      // When: Truncate null
      const nullResult = truncateBody(null);

      // Then: Should return null
      expect(nullResult).toBeNull();

      // When: Truncate undefined
      const undefinedResult = truncateBody(undefined);

      // Then: Should return undefined
      expect(undefinedResult).toBeUndefined();
    });

    it('should handle edge case at exactly 10240 bytes', () => {
      // Given: A body of exactly 10240 characters
      const exactBody = 'x'.repeat(10240);

      // When: Truncate the body
      const result = truncateBody(exactBody);

      // Then: Should NOT be truncated (exactly at limit)
      expect(result).toBe(exactBody);
    });

    it('should handle edge case at 10241 bytes', () => {
      // Given: A body of 10241 characters (just over limit)
      const overLimitBody = 'x'.repeat(10241);

      // When: Truncate the body
      const result = truncateBody(overLimitBody);

      // Then: Should be truncated
      expect(result).toHaveProperty('truncated', true);
      expect(result).toHaveProperty('originalSize', 10241);
    });
  });

  describe('isBinaryContent', () => {
    it('should detect image MIME types as binary', () => {
      // Given: Various image MIME types

      // When/Then: Should detect all image types
      expect(isBinaryContent('image/jpeg')).toBe(true);
      expect(isBinaryContent('image/png')).toBe(true);
      expect(isBinaryContent('image/gif')).toBe(true);
      expect(isBinaryContent('image/webp')).toBe(true);
      expect(isBinaryContent('image/svg+xml')).toBe(true);
    });

    it('should detect video MIME types as binary', () => {
      // Given: Various video MIME types

      // When/Then: Should detect all video types
      expect(isBinaryContent('video/mp4')).toBe(true);
      expect(isBinaryContent('video/mpeg')).toBe(true);
      expect(isBinaryContent('video/webm')).toBe(true);
    });

    it('should detect audio MIME types as binary', () => {
      // Given: Various audio MIME types

      // When/Then: Should detect all audio types
      expect(isBinaryContent('audio/mpeg')).toBe(true);
      expect(isBinaryContent('audio/wav')).toBe(true);
      expect(isBinaryContent('audio/ogg')).toBe(true);
    });

    it('should detect PDF as binary', () => {
      // Given: PDF MIME type

      // When/Then: Should detect PDF as binary
      expect(isBinaryContent('application/pdf')).toBe(true);
    });

    it('should detect compressed files as binary', () => {
      // Given: Compressed file MIME types

      // When/Then: Should detect compressed files as binary
      expect(isBinaryContent('application/zip')).toBe(true);
      expect(isBinaryContent('application/gzip')).toBe(true);
      expect(isBinaryContent('application/octet-stream')).toBe(true);
    });

    it('should NOT detect text/JSON/XML as binary', () => {
      // Given: Text-based MIME types

      // When/Then: Should NOT detect text as binary
      expect(isBinaryContent('application/json')).toBe(false);
      expect(isBinaryContent('text/plain')).toBe(false);
      expect(isBinaryContent('text/html')).toBe(false);
      expect(isBinaryContent('application/xml')).toBe(false);
      expect(isBinaryContent('text/csv')).toBe(false);
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

