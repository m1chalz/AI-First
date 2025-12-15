import { describe, it, expect } from 'vitest';
import { validateImageFormat } from '../file-validation.ts';

describe('file-validation', () => {
  describe('validateImageFormat', () => {
    it('should detect JPEG format from magic bytes', async () => {
      // Given
      const buffer = Buffer.from([0xff, 0xd8, 0xff, 0xe0, 0x00, 0x10, 0x4a, 0x46, 0x49, 0x46]);

      // When
      const result = await validateImageFormat(buffer);

      // Then
      expect(result).toBe('image/jpeg');
    });

    it('should detect GIF format from magic bytes', async () => {
      // Given
      const buffer = Buffer.from([0x47, 0x49, 0x46, 0x38, 0x39, 0x61]);

      // When
      const result = await validateImageFormat(buffer);

      // Then
      expect(result).toBe('image/gif');
    });

    it('should detect BMP format from magic bytes', async () => {
      // Given
      const buffer = Buffer.from([0x42, 0x4d, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]);

      // When
      const result = await validateImageFormat(buffer);

      // Then
      expect(result).toBe('image/bmp');
    });

    it.each([
      ['random bytes', Buffer.from([0xab, 0xcd, 0xef])],
      ['text data', Buffer.from('Hello World')],
      ['empty buffer', Buffer.alloc(0)],
      ['PDF magic bytes', Buffer.from([0x25, 0x50, 0x44, 0x46])]
    ])('should return null for non-image format: %s', async (desc, buffer) => {
      // Given / When
      const result = await validateImageFormat(buffer);

      // Then
      expect(result).toBeNull();
    });

    it('should detect spoofed MIME types using magic bytes', async () => {
      // Given
      const jpegBuffer = Buffer.from([0xff, 0xd8, 0xff, 0xe0]);
      const claimedMime = 'image/png';

      // When
      const result = await validateImageFormat(jpegBuffer);

      // Then
      expect(result).toBe('image/jpeg');
      expect(result).not.toBe(claimedMime);
    });
  });
});
