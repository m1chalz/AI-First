/**
 * Maximum body size before truncation (10KB in bytes).
 */
const MAX_BODY_SIZE = 10240;

/**
 * MIME type patterns for binary content detection.
 */
const BINARY_CONTENT_TYPES = [
  /^image\//,
  /^video\//,
  /^audio\//,
  /^application\/pdf/,
  /^application\/octet-stream/,
  /^application\/zip/,
  /^application\/gzip/,
];

/**
 * Truncates a request/response body if it exceeds the maximum size limit.
 *
 * Large payloads are truncated to prevent memory exhaustion and excessive
 * log storage. The truncated body includes metadata about the original size.
 *
 * @param body - The body to potentially truncate (any JSON-serializable type)
 * @returns Original body if under limit, or truncated body object with metadata
 * @example
 * const result = truncateBody({ large: "..." });
 * // If over 10KB: { content: "...", truncated: true, originalSize: 15360 }
 */
export function truncateBody(body: any): any {
  if (!body) {
    return body;
  }

  const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

  if (bodyString.length > MAX_BODY_SIZE) {
    return {
      content: bodyString.substring(0, MAX_BODY_SIZE),
      truncated: true,
      originalSize: bodyString.length,
    };
  }

  return body;
}

/**
 * Checks if the given Content-Type header indicates binary content.
 *
 * Binary content (images, videos, PDFs, etc.) should not be logged as text
 * to prevent log pollution and excessive storage usage.
 *
 * @param contentType - The Content-Type header value
 * @returns true if content is binary, false otherwise
 * @example
 * isBinaryContent('image/jpeg'); // true
 * isBinaryContent('application/json'); // false
 */
export function isBinaryContent(contentType: string | undefined): boolean {
  if (!contentType) {
    return false;
  }
  return BINARY_CONTENT_TYPES.some((pattern) => pattern.test(contentType));
}

/**
 * Serializes a request/response body for logging, handling truncation and
 * binary content omission.
 *
 * This function applies the appropriate transformation based on content type:
 * - Binary content: Omitted with metadata (Content-Type, size)
 * - Large content: Truncated with metadata (original size)
 * - Normal content: Returned as-is
 *
 * @param body - The body to serialize
 * @param contentType - The Content-Type header value
 * @param headers - Request/response headers (for Content-Length)
 * @returns Serialized body (original, truncated, or binary omission metadata)
 * @example
 * serializeBody(buffer, 'image/jpeg', headers);
 * // Returns: { binaryOmitted: true, contentType: 'image/jpeg', contentLength: '245678' }
 */
export function serializeBody(
  body: any,
  contentType: string | undefined,
  headers: Record<string, string | string[] | undefined>
): any {
  if (isBinaryContent(contentType)) {
    return {
      binaryOmitted: true,
      contentType,
      contentLength: headers['content-length'] || 'unknown',
    };
  }

  return truncateBody(body);
}

