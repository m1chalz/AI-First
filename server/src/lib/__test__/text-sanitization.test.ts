import { describe, it, expect } from 'vitest';
import sanitizeText from '../text-sanitization';

describe('sanitizeText', () => {
  it.each([
    ['<script>alert("xss")</script>Hello', 'Hello'],
    ['Plain text with special chars: @#$%', 'Plain text with special chars: @#$%'],
    ['', ''],
    ['<div><p>Hello <strong>World</strong></p></div>', 'Hello World'],
    ['<img src="x" onerror="alert(1)">Test', 'Test'],
  ])('should sanitize %s to %s', (input, expected) => {
    // when
    const result = sanitizeText(input);
    
    // then
    expect(result).toBe(expected);
  });
});
