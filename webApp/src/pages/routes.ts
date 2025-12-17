export const AppRoutes = {
  // Main navigation
  home: '/',
  lostPets: '/lost-pets',
  lostPetDetails: (id: string) => `/lost-pets/${id}`,
  foundPets: '/found-pets',
  contact: '/contact',
  account: '/account',

  // Report missing flow
  reportMissing: {
    base: '/report-missing',
    microchip: '/report-missing/microchip',
    photo: '/report-missing/photo',
    details: '/report-missing/details',
    contact: '/report-missing/contact',
    summary: '/report-missing/summary'
  }
} as const;
