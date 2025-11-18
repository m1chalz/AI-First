# Implementation Plan: E2E Testing Infrastructure

## Feature Overview

Set up complete End-to-End testing infrastructure for PetSpot project including Playwright for web and Appium with WebdriverIO for mobile platforms, both using TypeScript.

## Tech Stack & Libraries

### Web E2E Testing
- **Framework**: Playwright (`@playwright/test` v1.40+)
- **Language**: TypeScript 5.x
- **Pattern**: Page Object Model
- **Reporter**: HTML Reporter (built-in)

### Mobile E2E Testing
- **Framework**: WebdriverIO v8.x
- **Automation**: Appium v2.x
- **Language**: TypeScript 5.x
- **Pattern**: Screen Object Model
- **Test Framework**: Mocha (via `@wdio/mocha-framework`)
- **Reporter**: Spec Reporter (`@wdio/spec-reporter`)

### MCP Integration
- **Playwright MCP**: `@playwright/mcp` (already configured)
- **Appium MCP**: `appium-mcp` (to be added)

## Architecture

### Directory Structure

```
/e2e-tests/
├── web/
│   ├── specs/          # Playwright test specifications
│   ├── pages/          # Page Object Model classes
│   ├── steps/          # Reusable step definitions (Given/When/Then actions)
│   ├── fixtures/       # Test data and fixtures
│   └── README.md       # Web testing documentation
├── mobile/
│   ├── specs/          # Appium test specifications
│   ├── screens/        # Screen Object Model classes
│   ├── steps/          # Reusable step definitions (Given/When/Then actions)
│   ├── utils/          # Shared mobile utilities
│   └── README.md       # Mobile testing documentation
└── README.md           # Main E2E documentation
```

### Configuration Files

```
/playwright.config.ts   # Playwright configuration (repo root)
/wdio.conf.ts          # WebdriverIO/Appium configuration (repo root)
/.cursor/mcp.json      # MCP servers configuration (update)
```

## Technical Approach

### Phase 1: Setup & Configuration (Foundational)

**1.1 Directory Structure Creation**
- Create `/e2e-tests` root directory
- Create web subdirectories: `specs/`, `pages/`, `fixtures/`
- Create mobile subdirectories: `specs/`, `screens/`, `utils/`

**1.2 Playwright Configuration**
- Create `playwright.config.ts` at repo root
- Configure TypeScript support
- Set test directory to `/e2e-tests/web/specs/`
- Configure browsers: Chromium, Firefox, WebKit
- Configure HTML reporter
- Set retry policy for flaky tests
- Configure base URL and timeout settings

**1.3 Appium/WebdriverIO Configuration**
- Create `wdio.conf.ts` at repo root
- Configure TypeScript support
- Set spec directory to `/e2e-tests/mobile/specs/`
- Configure Appium service
- Set capabilities for Android and iOS
- Configure Mocha framework
- Configure Spec reporter
- Set timeouts and retry logic

**1.4 MCP Configuration**
- Update `.cursor/mcp.json`
- Add Appium MCP server configuration
- Verify Playwright MCP still configured

**1.5 Dependencies Installation**
- Install Playwright dependencies
- Install Appium and WebdriverIO dependencies
- Update root `package.json` with dev dependencies

**1.6 npm Scripts**
- Add `test:e2e:web` script
- Add `test:e2e:web:ui` script
- Add `test:mobile:android` script
- Add `test:mobile:ios` script

### Phase 2: Example Tests (User Story 2)

**2.1 Web Example - Page Object Model**
- Create example Page Object class (`e2e-tests/web/pages/ExamplePage.ts`)
- Create example test spec (`e2e-tests/web/specs/example.spec.ts`)
- Implement Given-When-Then structure
- Use proper test identifiers (`data-testid`)
- Add TypeScript types

**2.2 Mobile Example - Screen Object Model**
- Create example Screen Object class (`e2e-tests/mobile/screens/ExampleScreen.ts`)
- Create example test spec (`e2e-tests/mobile/specs/example.spec.ts`)
- Implement Given-When-Then structure
- Use proper test identifiers (`testTag`)
- Add TypeScript types

### Phase 3: Documentation (User Story 3)

**3.1 Main Documentation**
- Create `/e2e-tests/README.md`
- Document setup instructions
- Document test execution commands
- Include troubleshooting section

**3.2 Web Testing Documentation**
- Create `/e2e-tests/web/README.md`
- Explain Page Object Model pattern
- Provide examples of writing web tests
- Document best practices

**3.3 Mobile Testing Documentation**
- Create `/e2e-tests/mobile/README.md`
- Explain Screen Object Model pattern
- Provide examples of writing mobile tests
- Document platform-specific considerations

## File Changes

### New Files

| Path | Purpose | Size Est. |
|------|---------|-----------|
| `/e2e-tests/web/specs/example.spec.ts` | Example Playwright test | ~50 lines |
| `/e2e-tests/web/pages/ExamplePage.ts` | Example Page Object | ~30 lines |
| `/e2e-tests/mobile/specs/example.spec.ts` | Example Appium test | ~60 lines |
| `/e2e-tests/mobile/screens/ExampleScreen.ts` | Example Screen Object | ~40 lines |
| `/playwright.config.ts` | Playwright configuration | ~80 lines |
| `/wdio.conf.ts` | WebdriverIO configuration | ~120 lines |
| `/e2e-tests/README.md` | Main E2E documentation | ~150 lines |
| `/e2e-tests/web/README.md` | Web testing docs | ~80 lines |
| `/e2e-tests/mobile/README.md` | Mobile testing docs | ~100 lines |

### Modified Files

| Path | Changes | Impact |
|------|---------|--------|
| `/.cursor/mcp.json` | Add Appium MCP server | Low - add JSON entry |
| `/package.json` (root) | Add E2E dependencies & scripts | Medium - 8+ dependencies, 4 scripts |

## Dependencies Installation

```json
{
  "devDependencies": {
    "@playwright/test": "^1.40.0",
    "@types/node": "^20.0.0",
    "appium": "^2.0.0",
    "webdriverio": "^8.0.0",
    "@wdio/cli": "^8.0.0",
    "@wdio/local-runner": "^8.0.0",
    "@wdio/mocha-framework": "^8.0.0",
    "@wdio/spec-reporter": "^8.0.0",
    "@wdio/appium-service": "^8.0.0"
  }
}
```

## Testing Strategy

### Validation Tests

After setup, verify:

1. **Playwright Setup**:
   ```bash
   npx playwright test --list
   npm run test:e2e:web -- --reporter=list
   ```

2. **WebdriverIO Setup**:
   ```bash
   npm run test:mobile:android -- --dry-run
   ```

3. **MCP Integration**:
   - Verify Playwright MCP responds to queries
   - Verify Appium MCP responds to queries

### Example Test Execution

1. Run web example test:
   ```bash
   npm run test:e2e:web
   ```

2. Run mobile example test (with emulator/simulator):
   ```bash
   npm run test:mobile:android
   npm run test:mobile:ios
   ```

## Constitution Compliance Check

### Principle VI: End-to-End Testing ✅

- [x] **Web Platform** (`/e2e-tests/web/`)
  - [x] Framework: Playwright with TypeScript
  - [x] Config: `playwright.config.ts` at repo root
  - [x] Test location: `/e2e-tests/web/specs/`
  - [x] Run command: `npx playwright test`

- [x] **Mobile Platforms** (`/e2e-tests/mobile/`)
  - [x] Framework: Appium with TypeScript + WebdriverIO
  - [x] Config: `wdio.conf.ts`
  - [x] Test location: `/e2e-tests/mobile/specs/`
  - [x] Run commands: `npm run test:mobile:android` / `ios`

- [x] **Requirements**
  - [x] Tests written in TypeScript
  - [x] Page Object Model pattern for web
  - [x] Screen Object Model pattern for mobile
  - [x] Tests executable (example tests provided)

### Principle XII: Given-When-Then Test Convention ✅

- [x] Example tests follow Given-When-Then structure
- [x] Test phases clearly separated
- [x] Descriptive test names in TypeScript strings

## Implementation Strategy

**Approach**: Infrastructure-first, then examples, then documentation

**MVP Scope**: Phase 1 (Setup & Configuration) - US1
- All directory structure in place
- Configuration files functional
- Dependencies installed
- npm scripts working

**Incremental Delivery**:
1. **Phase 1**: Complete infrastructure setup (US1)
2. **Phase 2**: Add example tests (US2)
3. **Phase 3**: Complete documentation (US3)

Each phase is independently valuable and testable.

## Rollout Plan

1. **Immediate**: Complete Phase 1 on branch `001-e2e-testing-infrastructure`
2. **Same PR**: Add Phases 2-3 (complete infrastructure)
3. **Merge**: Merge to main once all validation passes
4. **Future**: Add actual feature E2E tests per feature specification

## Risk Mitigation

### Risk 1: Appium Setup Complexity
**Mitigation**: Provide clear documentation and troubleshooting guide for platform-specific setup

### Risk 2: MCP Integration Failures
**Mitigation**: Verify each MCP server independently; provide fallback instructions for manual testing

### Risk 3: Missing Mobile Platform SDKs
**Mitigation**: Document SDK requirements clearly; example tests can be skipped if SDKs unavailable

## Success Criteria

- [ ] All directories created per constitution structure
- [ ] Both configuration files exist and are valid
- [ ] All dependencies installed successfully
- [ ] All npm scripts execute without errors
- [ ] Example tests run successfully (web)
- [ ] MCP servers both functional
- [ ] Documentation complete and clear
- [ ] Constitution Principle VI compliance achieved

