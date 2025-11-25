import { scrypt, randomBytes, randomInt, timingSafeEqual } from 'crypto';
import { promisify } from 'util';

const scryptAsync = promisify(scrypt);
const SALT_LENGTH = 16;
const KEY_LENGTH = 64;

export function generateManagementPassword(): string {
  const password = randomInt(100000, 1000000);
  return password.toString();
}

export async function hashPassword(plainPassword: string): Promise<string> {
  const salt = randomBytes(SALT_LENGTH);
  const derivedKey = await scryptAsync(plainPassword, salt, KEY_LENGTH) as Buffer;
  return salt.toString('hex') + ':' + derivedKey.toString('hex');
}

export async function verifyPassword(
  plainPassword: string,
  hash: string
): Promise<boolean> {
  const [saltHex, keyHex] = hash.split(':');
  const salt = Buffer.from(saltHex, 'hex');
  const originalKey = Buffer.from(keyHex, 'hex');
  const derivedKey = await scryptAsync(plainPassword, salt, KEY_LENGTH) as Buffer;
  return timingSafeEqual(originalKey, derivedKey);
}
