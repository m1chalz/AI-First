import { describe, it, expect } from 'vitest';
import { redactPhone, redactEmail } from '../pii-redaction';

describe('redactPhone', () => {
  it.each([
    ['+1-555-123-4567', '***-***-567'],
    ['123', '***-***-123'],
    ['12', '12'],
    ['', ''],
  ])('should redact phone %s to %s', (phone, expected) => {
    // given / when
    const redacted = redactPhone(phone);
    
    // then
    expect(redacted).toBe(expected);
  });
  
  it.each([
    '12',
    ''
  ])('should leave invalid phone untouched (%s)', (phone) => {
    // given / when
    const redacted = redactPhone(phone);
    
    // then
    expect(redacted).toBe(phone);
  });
});

describe('redactEmail', () => {
  it.each([
    ['john@example.com', 'j***@example.com'],
    ['a@example.com', 'a***@example.com']
  ])('should redact email %s to %s', (email, expected) => {
    // given / when
    const redacted = redactEmail(email);
    
    // then
    expect(redacted).toBe(expected);
  });

  it.each([
    'user@test@example.com',
    'invalidemail',
    ''
  ])('should leave invalid email untouched (%s)', (email) => {
    // given / when
    const redacted = redactEmail(email);
    
    // then
    expect(redacted).toBe(email);
  });
});
