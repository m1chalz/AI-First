# E2E Testing Infrastructure Setup

## Overview

Set up complete End-to-End (E2E) testing infrastructure for PetSpot project to achieve compliance with Constitution Principle VI (End-to-End Testing). This includes Playwright for web testing and Appium with WebdriverIO for mobile testing, both using TypeScript.

## Context

**Current State:**
- No E2E testing infrastructure exists
- MCP configuration only includes Playwright, missing Appium
- No test directories, configuration files, or test scripts
- Constitution Principle VI mandates E2E tests for all user stories

**Target State:**
- Complete `/e2e-tests` directory structure with web and mobile subdirectories
- Playwright configuration for web testing
- Appium + WebdriverIO configuration for mobile testing (Android/iOS)
- MCP integration for both Playwright and Appium
- Example tests demonstrating Page Object Model and Screen Object Model patterns
- npm scripts for running all E2E tests
- Documentation for E2E test development

## Functional Requirements

### FR-1: E2E Directory Structure
The project MUST have `/e2e-tests` directory with proper subdirectories for web and mobile tests, following constitution-mandated structure.

**Acceptance Criteria:**
- `/e2e-tests/web/specs/` exists for Playwright test specifications
- `/e2e-tests/web/pages/` exists for Page Object Model
- `/e2e-tests/web/steps/` exists for reusable step definitions
- `/e2e-tests/web/fixtures/` exists for test data
- `/e2e-tests/mobile/specs/` exists for Appium test specifications
- `/e2e-tests/mobile/screens/` exists for Screen Object Model
- `/e2e-tests/mobile/steps/` exists for reusable step definitions
- `/e2e-tests/mobile/utils/` exists for shared mobile test utilities

### FR-2: Playwright Configuration
Web E2E tests MUST be configured with Playwright and TypeScript.

**Acceptance Criteria:**
- `playwright.config.ts` exists at repository root
- Configuration includes proper TypeScript support
- Configuration specifies test directory as `/e2e-tests/web/specs/`
- Configuration includes proper browser targets (Chromium, Firefox, WebKit)
- HTML reporter configured for test results
- Retry logic configured for flaky test handling

### FR-3: Appium Configuration
Mobile E2E tests MUST be configured with Appium, WebdriverIO, and TypeScript.

**Acceptance Criteria:**
- `wdio.conf.ts` exists at repository root
- Configuration supports both Android and iOS platforms
- Configuration includes proper TypeScript support
- Configuration specifies test directory as `/e2e-tests/mobile/specs/`
- Appium service configured with proper capabilities
- Spec reporter configured for test results

### FR-4: MCP Integration
Model Context Protocol (MCP) MUST support both Playwright and Appium.

**Acceptance Criteria:**
- `.cursor/mcp.json` includes Playwright MCP server configuration
- `.cursor/mcp.json` includes Appium MCP server configuration
- Both MCP servers are functional and accessible

### FR-5: npm Scripts
Root package.json MUST include scripts for running E2E tests.

**Acceptance Criteria:**
- `test:e2e:web` script runs Playwright tests
- `test:e2e:web:ui` script runs Playwright tests with UI mode
- `test:mobile:android` script runs Appium tests on Android
- `test:mobile:ios` script runs Appium tests on iOS
- All scripts are executable from repository root

### FR-6: Dependencies
All required E2E testing dependencies MUST be installed.

**Acceptance Criteria:**
- `@playwright/test` installed as dev dependency
- `appium` installed as dev dependency
- `webdriverio` installed as dev dependency
- `@wdio/cli` installed as dev dependency
- `@wdio/appium-service` installed as dev dependency
- `@wdio/local-runner` installed as dev dependency
- `@wdio/mocha-framework` installed as dev dependency
- `@wdio/spec-reporter` installed as dev dependency

### FR-7: Example Tests
Example E2E tests MUST be provided demonstrating proper patterns.

**Acceptance Criteria:**
- Example Playwright test exists using Page Object Model
- Example Appium test exists using Screen Object Model
- Both examples follow Given-When-Then structure (Principle XII)
- Both examples use proper test identifiers (`data-testid`, `testTag`)
- Examples include proper TypeScript types

### FR-8: Documentation
E2E testing approach MUST be documented.

**Acceptance Criteria:**
- `/e2e-tests/README.md` exists with setup instructions
- Documentation explains how to run web tests
- Documentation explains how to run mobile tests
- Documentation explains Page Object Model pattern
- Documentation explains Screen Object Model pattern
- Documentation includes troubleshooting section

## Non-Functional Requirements

### NFR-1: TypeScript Compliance
All E2E test code MUST be written in TypeScript per Constitution Principle VI.

### NFR-2: Constitution Compliance
All setup MUST comply with Constitution Principle VI (End-to-End Testing).

### NFR-3: Parallel Execution
E2E tests MUST support parallel execution where possible.

### NFR-4: CI/CD Ready
Configuration MUST be ready for CI/CD integration.

## User Stories

### P1: Basic E2E Infrastructure
**As a** developer  
**I want** a complete E2E testing infrastructure  
**So that** I can write and run E2E tests for web and mobile platforms

**Acceptance Criteria:**
- All directory structure in place
- Configuration files created and functional
- Dependencies installed
- npm scripts working

### P2: Example Tests & Patterns
**As a** developer  
**I want** example tests demonstrating best practices  
**So that** I can follow established patterns when writing new E2E tests

**Acceptance Criteria:**
- Example Playwright test with Page Object Model
- Example Appium test with Screen Object Model
- Both follow Given-When-Then structure
- Both use proper test identifiers

### P3: Documentation & Onboarding
**As a** new team member  
**I want** comprehensive E2E testing documentation  
**So that** I can quickly learn how to write and run E2E tests

**Acceptance Criteria:**
- README.md with setup instructions
- Examples of running tests
- Pattern explanations
- Troubleshooting guide

## Out of Scope

- Actual feature E2E tests (will be added per feature)
- CI/CD pipeline configuration (future work)
- E2E test coverage reporting setup (future work)
- Performance testing infrastructure (separate effort)
- Visual regression testing setup (future work)

## Dependencies

- Git repository with proper branch structure
- Node.js v24 installed
- Android SDK (for mobile testing)
- Xcode (for iOS mobile testing on macOS)

## Success Metrics

- All 8 functional requirements satisfied
- Constitution Principle VI compliance achieved
- Example tests run successfully
- All npm scripts execute without errors
- Documentation complete and clear

