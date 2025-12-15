const MIN_PASSWORD_LENGTH = 8;
const MAX_PASSWORD_LENGTH = 128;
const EMAIL_REGEX = /^(?=.{1,254}$)[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function isValidEmail(email: string): boolean {
  return !!email && EMAIL_REGEX.test(email);
}

export function isValidPassword(password: string): boolean {
  return !!password && password.length >= MIN_PASSWORD_LENGTH && password.length <= MAX_PASSWORD_LENGTH;
}

export function isValidPhone(phone: string): boolean {
  return /\d/.test(phone);
}
