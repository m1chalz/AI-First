import { describe, it, expect } from 'vitest';
import { formatFileSize } from '../format-file-size';

describe('format-file-size', () => {
  describe('formatFileSize', () => {
    it.each([
      [0, '0 bytes'],
      [500, '500 bytes'],
      [1023, '1023 bytes'],
      [1024, '1.0 KB'],
      [1536, '1.5 KB'],
      [1536.789, '1.5 KB'],
      [1024 * 1024, '1.0 MB'],
      [1.5 * 1024 * 1024, '1.5 MB'],
      [2.5 * 1024 * 1024, '2.5 MB'],
      [2.567 * 1024 * 1024, '2.6 MB'],
      [20 * 1024 * 1024, '20.0 MB']
    ])('formats %d bytes as "%s"', (bytes, expected) => {
      // given / when
      const result = formatFileSize(bytes);

      // then
      expect(result).toBe(expected);
    });
  });
});
