/**
 * Formats species name to have only first letter capitalized.
 * @param species - Species name (e.g., "CAT", "DOG")
 * @returns Formatted species name (e.g., "Cat", "Dog")
 */
export function formatSpecies(species: string): string {
  if (!species) return species;
  return species.charAt(0).toUpperCase() + species.slice(1).toLowerCase();
}

/**
 * Formats sex/gender to have only first letter capitalized.
 * @param sex - Sex value (e.g., "MALE", "FEMALE", "UNKNOWN")
 * @returns Formatted sex value (e.g., "Male", "Female", "Unknown")
 */
export function formatSex(sex: string): string {
  if (!sex) return sex;
  return sex.charAt(0).toUpperCase() + sex.slice(1).toLowerCase();
}

