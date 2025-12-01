export const ALLOWED_MIME_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'image/bmp',
  'image/tiff',
  'image/heic',
  'image/heif',
];

export const MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024;

export function validateFileMimeType(file: File): boolean {
  return ALLOWED_MIME_TYPES.includes(file.type);
}

export function validateFileSize(file: File): boolean {
  return file.size <= MAX_FILE_SIZE_BYTES;
}

export function getFileValidationError(file: File): string | null {
  if (!validateFileMimeType(file)) {
    return 'Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format';
  }
  if (!validateFileSize(file)) {
    return 'File size exceeds 20MB limit';
  }
  return null;
}

