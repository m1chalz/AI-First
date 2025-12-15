const MAX_BODY_SIZE = 10240; // 10KB - prevents memory exhaustion and excessive log storage

const BINARY_CONTENT_TYPES = [
  /^image\//,
  /^video\//,
  /^audio\//,
  /^application\/pdf/,
  /^application\/octet-stream/,
  /^application\/zip/,
  /^application\/gzip/
];

export interface TruncatedBody {
  content: string;
  truncated: true;
  originalSize: number;
}

export interface BinaryOmittedBody {
  binaryOmitted: true;
  contentType: string | undefined;
  contentLength: string | string[] | undefined;
}

/**
 * Truncates request/response body if it exceeds 10KB.
 * Returns truncated body with metadata (originalSize, truncated flag).
 */
export function truncateBody(body: unknown): unknown | TruncatedBody {
  if (!body) {
    return body;
  }

  const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

  if (bodyString.length > MAX_BODY_SIZE) {
    return {
      content: bodyString.substring(0, MAX_BODY_SIZE),
      truncated: true,
      originalSize: bodyString.length
    };
  }

  return body;
}

/**
 * Checks if Content-Type indicates binary content (images, videos, PDFs, etc.).
 * Binary content should not be logged to prevent log pollution.
 */
export function isBinaryContent(contentType: string | undefined): boolean {
  if (!contentType) {
    return false;
  }
  return BINARY_CONTENT_TYPES.some((pattern) => pattern.test(contentType));
}

/**
 * Serializes body for logging:
 * - Binary content: Omitted with metadata
 * - Large content: Truncated with metadata
 * - Normal content: Returned as-is
 */
export function serializeBody(
  body: unknown,
  contentType: string | undefined,
  headers: Record<string, string | string[] | undefined>
): unknown | BinaryOmittedBody | TruncatedBody {
  if (isBinaryContent(contentType)) {
    return {
      binaryOmitted: true,
      contentType,
      contentLength: headers['content-length'] || 'unknown'
    };
  }

  return truncateBody(body);
}
