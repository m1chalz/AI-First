import { fileTypeFromBuffer } from 'file-type';

const ALLOWED_IMAGE_TYPES = new Set([
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'image/bmp',
  'image/tiff',
  'image/heic',
  'image/heif',
]);

export async function validateImageFormat(buffer: Buffer): Promise<string | null> {
  try {
    // Detect file type using magic bytes (not trusting Content-Type header)
    const fileType = await fileTypeFromBuffer(buffer);

    if (!fileType || !ALLOWED_IMAGE_TYPES.has(fileType.mime)) {
      return null;
    }

    return fileType.mime;
  } catch {
    // If detection fails, treat as invalid format
    return null;
  }
}

export function sanitizeFilename(filename: string): string {
  return (
    filename
      // Remove null bytes and dangerous characters
      // eslint-disable-next-line no-control-regex
      .replace(/[\x00<>:"|?*]/g, '')
      // Remove path traversal patterns
      .replace(/\.\./g, '')
      // Remove path separators
      .replace(/[\\/]/g, '')
      // Convert to lowercase
      .toLowerCase()
      // Replace multiple spaces with single hyphen
      .replace(/\s+/g, '-')
      // Remove leading/trailing hyphens
      .replace(/^-+|-+$/g, '')
  );
}
