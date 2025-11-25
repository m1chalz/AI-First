import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html', 'json'],
      exclude: [
        'node_modules/**',
        'src/__test__/**', // Integration tests
        'src/database/**',
        'src/routes/**',
        'src/*.ts',
        '**/*.d.ts',
        '**/*.config.*',
        '**/coverage/**',
        'knexfile.ts'
      ],
      thresholds: {
        lines: 80,
        branches: 80,
        functions: 80,
        statements: 80,
      },
    },
  },
});