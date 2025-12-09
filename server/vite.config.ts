import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    fileParallelism: false, // announcement and photo upload tests can't be run in parallel because they share the same database TO BE FIXED LATER
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
        statements: 80
      }
    }
  }
});
