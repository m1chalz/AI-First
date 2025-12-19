export default {
  apiBaseUrl: import.meta.env.PROD ? '' : 'http://localhost:3000',
  map: {
    defaultZoom: 13,
    fallbackLocation: { lat: 51.1079, lng: 17.0385 },
    tileLayerUrl: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }
} as const;
