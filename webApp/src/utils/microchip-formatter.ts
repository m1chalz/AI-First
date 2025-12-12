export function stripNonDigits(input: string): string {
  return input.replace(/\D/g, '');
}

export function formatMicrochip(digits: string): string {
  const cleaned = digits.slice(0, 15);

  if (cleaned.length <= 5) {
    return cleaned;
  }

  if (cleaned.length <= 10) {
    return `${cleaned.slice(0, 5)}-${cleaned.slice(5)}`;
  }

  return `${cleaned.slice(0, 5)}-${cleaned.slice(5, 10)}-${cleaned.slice(10)}`;
}
