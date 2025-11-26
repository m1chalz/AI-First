import { describe, it, expect } from 'vitest';
import { validateImageFormat, sanitizeFilename } from '../file-validation.ts';

describe('file-validation', () => {
  describe('validateImageFormat', () => {
    it('should detect JPEG format from magic bytes', async () => {
      // Given: buffer with JPEG magic bytes
      const buffer = Buffer.from([0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46]);

      // When: validateImageFormat is called
      const result = await validateImageFormat(buffer);

      // Then: should return image/jpeg
      expect(result).toBe('image/jpeg');
    });

    it('should detect GIF format from magic bytes', async () => {
      // Given: buffer with GIF magic bytes
      const buffer = Buffer.from([0x47, 0x49, 0x46, 0x38, 0x39, 0x61]);

      // When: validateImageFormat is called
      const result = await validateImageFormat(buffer);

      // Then: should return image/gif
      expect(result).toBe('image/gif');
    });

    it('should detect BMP format from magic bytes', async () => {
      // Given: buffer with BMP magic bytes
      const buffer = Buffer.from([0x42, 0x4D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]);

      // When: validateImageFormat is called
      const result = await validateImageFormat(buffer);

      // Then: should return image/bmp
      expect(result).toBe('image/bmp');
    });

    it.each([
      ['random bytes', Buffer.from([0xAB, 0xCD, 0xEF])],
      ['text data', Buffer.from('Hello World')],
      ['empty buffer', Buffer.alloc(0)],
      ['PDF magic bytes', Buffer.from([0x25, 0x50, 0x44, 0x46])],
    ])('should return null for non-image format: %s', async (desc, buffer) => {
      // Given: buffer without image magic bytes
      // When: validateImageFormat is called
      const result = await validateImageFormat(buffer);

      // Then: should return null
      expect(result).toBeNull();
    });

    it('should detect spoofed MIME types using magic bytes', async () => {
      // Given: buffer with JPEG magic bytes but claimed as PNG
      const jpegBuffer = Buffer.from([0xFF, 0xD8, 0xFF, 0xE0]);
      const claimedMime = 'image/png';

      // When: validateImageFormat is called
      const result = await validateImageFormat(jpegBuffer);

      // Then: should detect actual format (JPEG), not the claimed one
      expect(result).toBe('image/jpeg');
      expect(result).not.toBe(claimedMime);
    });
  });

  describe('sanitizeFilename', () => {
    it.each([
      ['normal name', 'photo.jpg', 'photo.jpg'],
      ['name with spaces', 'my photo.jpg', 'my-photo.jpg'],
      ['multiple spaces', 'my  photo.jpg', 'my-photo.jpg'],
      ['mixed case', 'MyPhoto.JPG', 'myphoto.jpg'],
      ['with numbers', 'photo123.jpg', 'photo123.jpg'],
      ['with hyphens', 'my-photo.jpg', 'my-photo.jpg'],
      ['with underscores', 'my_photo.jpg', 'my_photo.jpg'],
    ])('should normalize filename: %s', (_desc, input, expected) => {
      // Given: filename with various formats
      // When: sanitizeFilename is called
      const result = sanitizeFilename(input);

      // Then: should return normalized filename
      expect(result).toBe(expected);
    });

    it.each([
      ['path traversal ../', '../../../etc/passwd.jpg'],
      ['path traversal ..\\', '..\\..\\windows\\system32.jpg'],
      ['absolute path', '/etc/passwd.jpg'],
      ['backslash path', 'C:\\Windows\\System32\\photo.jpg'],
      ['null bytes', 'photo.jpg\0.exe'],
      ['special chars', 'photo<script>.jpg'],
    ])('should remove dangerous patterns: %s', (_desc, input) => {
      // Given: filename with path traversal or dangerous patterns
      // When: sanitizeFilename is called
      const result = sanitizeFilename(input);

      // Then: should not contain path separators or dangerous patterns
      expect(result).not.toContain('..');
      expect(result).not.toContain('/');
      expect(result).not.toContain('\\');
      expect(result).not.toContain('\0');
      expect(result).not.toContain('<');
      expect(result).not.toContain('>');
    });

    it('should handle edge cases', () => {
      // Given: edge case filenames
      // When: sanitizeFilename is called with edge cases
      // Then: should return safe names

      expect(sanitizeFilename('.jpg')).toBe('.jpg');
      expect(sanitizeFilename('file')).toBe('file');
      expect(sanitizeFilename('')).toBe('');
    });
  });
});

