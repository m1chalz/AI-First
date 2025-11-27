export function formatMicrochip(raw: string): string {
  const cleaned = raw.replace(/\D/g, '');
  if (cleaned.length !== 15) {
    return raw; // Return original if invalid format
  }
  return cleaned.replace(/(\d{5})(\d{5})(\d{5})/, '$1-$2-$3');
}
