import { describe, it, expect } from 'vitest';
import { validateFileMimeType, validateFileSize, getFileValidationError, MAX_FILE_SIZE_BYTES } from '../file-validation';

describe('file-validation', () => {
  describe('validateFileMimeType', () => {
    it.each([
      ['photo.jpg', 'image/jpeg'],
      ['photo.png', 'image/png'],
      ['photo.gif', 'image/gif'],
      ['photo.webp', 'image/webp'],
      ['photo.bmp', 'image/bmp'],
      ['photo.tiff', 'image/tiff'],
      ['photo.heic', 'image/heic'],
      ['photo.heif', 'image/heif']
    ])('returns true for %s (%s)', (filename, mimeType) => {
      // given
      const file = new File(['content'], filename, { type: mimeType });

      // when
      const result = validateFileMimeType(file);

      // then
      expect(result).toBe(true);
    });

    it.each([
      ['document.pdf', 'application/pdf'],
      ['document.txt', 'text/plain'],
      ['image.svg', 'image/svg+xml']
    ])('returns false for %s (%s)', (filename, mimeType) => {
      // given
      const file = new File(['content'], filename, { type: mimeType });

      // when
      const result = validateFileMimeType(file);

      // then
      expect(result).toBe(false);
    });
  });

  describe('validateFileSize', () => {
    it.each([
      ['1MB', 1 * 1024 * 1024, true],
      ['10MB', 10 * 1024 * 1024, true],
      ['exactly 20MB', MAX_FILE_SIZE_BYTES, true],
      ['empty', 0, true],
      ['21MB', 21 * 1024 * 1024, false],
      ['30MB', 30 * 1024 * 1024, false]
    ])('returns %s for %s file', (_description, size, expected) => {
      // given
      const file =
        size > 0
          ? new File([new ArrayBuffer(size)], 'photo.jpg', { type: 'image/jpeg' })
          : new File([], 'photo.jpg', { type: 'image/jpeg' });

      // when
      const result = validateFileSize(file);

      // then
      expect(result).toBe(expected);
    });
  });

  describe('getFileValidationError', () => {
    it.each([
      ['null for valid file', new File([new ArrayBuffer(1024)], 'photo.jpg', { type: 'image/jpeg' }), null],
      [
        'format error for invalid MIME type',
        new File(['content'], 'document.pdf', { type: 'application/pdf' }),
        'Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format'
      ],
      [
        'size error for oversized file',
        new File([new ArrayBuffer(21 * 1024 * 1024)], 'photo.jpg', { type: 'image/jpeg' }),
        'File size exceeds 20MB limit'
      ],
      [
        'format error first when both validations fail',
        new File([new ArrayBuffer(21 * 1024 * 1024)], 'document.pdf', { type: 'application/pdf' }),
        'Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format'
      ]
    ])('returns %s', (_description, file, expected) => {
      // when
      const result = getFileValidationError(file);

      // then
      expect(result).toBe(expected);
    });
  });
});
