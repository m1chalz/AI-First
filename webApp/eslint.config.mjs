import eslint from '@eslint/js';
import { defineConfig } from 'eslint/config';
import reactPlugin from 'eslint-plugin-react';
import tseslint from 'typescript-eslint';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import importPlugin from 'eslint-plugin-import';

export default defineConfig(
  eslint.configs.recommended,
  tseslint.configs.strict,
  tseslint.configs.stylistic,
  reactPlugin.configs.flat.recommended,
  reactPlugin.configs.flat['jsx-runtime'],
  reactHooksPlugin.configs.flat.recommended,
  importPlugin.flatConfigs.recommended,
  importPlugin.flatConfigs.typescript,
  {
    ignores: ['node_modules/', 'dist/', 'build/', 'coverage/', '*.js'],
    settings: {
      react: {
        version: 'detect'
      }
    },
    rules: {
      'arrow-body-style': ['error', 'as-needed'],
      'react-hooks/set-state-in-effect': 'off',
      '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
      'import/order': [
        'error',
        {
          groups: [
            'builtin',
            'external',
            'internal',
            'parent',
            'sibling',
            'index'
          ]
        }
      ]
    }
  }
);
