export function formatMicrochip(raw: string): string {
  const cleaned = raw.replace(/\D/g, '');
  if (cleaned.length === 0) {
    return '';
  }
  // If exactly 15 digits, format as XXXXX-XXXXX-XXXXX
  if (cleaned.length === 15) {
    return cleaned.replace(/(\d{5})(\d{5})(\d{5})/, '$1-$2-$3');
  }
  // If more than 15 digits, take first 15 and format
  if (cleaned.length > 15) {
    return cleaned.substring(0, 15).replace(/(\d{5})(\d{5})(\d{5})/, '$1-$2-$3');
  }
  // Otherwise return as-is (for numbers less than 15 digits)
  return raw;
}
