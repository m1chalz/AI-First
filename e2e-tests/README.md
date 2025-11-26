# PetSpot E2E Tests - Dual Test Stack

**Status**: DUAL STACK COEXISTENCE - Both TypeScript and Java test stacks are fully operational

This directory contains **two independent E2E testing infrastructures** for PetSpot:

1. **TypeScript Stack** (existing): Playwright (web) + Appium+WebdriverIO (mobile)
2. **Java Stack** (new): Selenium (web) + Appium+Cucumber (mobile)

**Migration Strategy**: Gradual, organic migration as developers work on feature branches. **No forced timeline** - both stacks will coexist indefinitely until all active branches migrate to Java.

---

## Directory Structure

```
/e2e-tests/
├── java/                           # NEW: Java/Maven/Selenium/Appium/Cucumber stack
│   ├── pom.xml                     # Maven project configuration
│   └── src/test/
│       ├── java/                   # Java test code (Page/Screen Objects, Steps, Runners)
│       └── resources/              # Gherkin feature files (.feature)
│
├── web/                            # EXISTING: TypeScript Playwright tests (web)
│   ├── specs/                      # Playwright test specifications
│   ├── pages/                      # Page Object Model
│   ├── steps/                      # Reusable step definitions
│   └── playwright.config.ts        # Playwright configuration
│
├── mobile/                         # EXISTING: TypeScript Appium tests (mobile)
│   ├── specs/                      # Mobile test specifications
│   ├── screens/                    # Screen Object Model
│   ├── steps/                      # Reusable step definitions
│   └── wdio.conf.ts                # WebdriverIO configuration
│
├── package.json                    # npm dependencies for TypeScript tests
└── README.md                       # This file
```

---

## TypeScript Test Stack (Existing - PRESERVED)

### Prerequisites

- Node.js 18+ installed
- npm dependencies installed: `npm install` (from `/e2e-tests/`)
- For mobile tests: Android SDK and/or Xcode configured

### Run Commands

**Web Tests** (Playwright):
```bash
# Run all web E2E tests
npm run test:web

# Run with UI mode (interactive)
npm run test:web:ui
```

**Mobile Tests** (Appium + WebdriverIO):
```bash
# Start Appium server (prerequisite for mobile tests)
npm run appium:start

# Run Android tests
npm run test:mobile:android

# Run iOS tests
npm run test:mobile:ios

# Stop Appium server
npm run clean:appium
```

### Test Locations

- **Web**: `/e2e-tests/web/specs/*.spec.ts`
- **Mobile**: `/e2e-tests/mobile/specs/*.spec.ts`

### Reports

- **Playwright**: `playwright-report/index.html` (auto-generated)
- **WebdriverIO**: Console output + `test-results/` directory

---

## Java Test Stack (New - INFRASTRUCTURE ENABLED)

### Prerequisites

- Java JDK 21 installed
- Maven 3.6+ installed
- For mobile tests: Android SDK and/or Xcode configured + Appium server running

### Run Commands

**Build Project**:
```bash
cd e2e-tests/java
mvn clean install
```

**Web Tests** (Selenium):
```bash
# Run all web E2E tests (tag: @web)
mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@web"

# View HTML report
open e2e-tests/java/target/cucumber-reports/web/index.html
```

**Mobile Tests** (Appium):
```bash
# Prerequisite: Start Appium server (use npm script from root)
cd e2e-tests && npm run appium:start
# Server starts on http://127.0.0.1:4723 - keep this terminal running

# Terminal 2: Start Android Emulator (for Android tests)
emulator -avd Android_14_Emulator

# Terminal 3: Run Android tests
cd e2e-tests/java
mvn test -Dtest=AndroidTestRunner
# OR: mvn test -Dcucumber.filter.tags="@android"

# Run Android smoke tests only
mvn test -Dcucumber.filter.tags="@android and @smoke"

# Terminal 2: Start iOS Simulator (for iOS tests, macOS only)
open -a Simulator
xcrun simctl boot "iPhone 15"

# Terminal 3: Run iOS tests
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
# OR: mvn test -Dcucumber.filter.tags="@ios"

# View HTML reports
open target/cucumber-reports/android/cucumber.html
open target/cucumber-reports/ios/cucumber.html

# View failure screenshots (platform-specific filenames)
ls -lh target/screenshots/
# Example: Android_search_for_specific_species_2025-11-26_09-00-15.png
```

### Test Locations

- **Feature files** (Gherkin): `/e2e-tests/java/src/test/resources/features/{web|mobile}/*.feature`
- **Java code**: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/`

### Reports

- **Web**: `/e2e-tests/java/target/cucumber-reports/web/index.html`
- **Android**: `/e2e-tests/java/target/cucumber-reports/android/index.html`
- **iOS**: `/e2e-tests/java/target/cucumber-reports/ios/index.html`

---

## TypeScript Test Preservation Checklist

**Status**: ✅ VERIFIED - TypeScript tests remain functional after Java infrastructure setup

### Required Commands (MUST continue to work)

- [x] `npm install` - Install TypeScript test dependencies
- [x] `npm run test:web` - Run Playwright web tests
- [x] `npm run test:web:ui` - Run Playwright UI mode
- [x] `npm run appium:start` - Start Appium server for mobile tests
- [x] `npm run test:mobile:android` - Run Android tests
- [x] `npm run test:mobile:ios` - Run iOS tests
- [x] `npm run clean:appium` - Stop Appium server

### Directory Conflicts Check

- [x] No conflicts between `/e2e-tests/java/` and `/e2e-tests/web/` or `/e2e-tests/mobile/`
- [x] TypeScript configuration files (tsconfig.json, wdio.conf.ts, playwright.config.ts) remain unchanged
- [x] npm scripts in package.json remain unchanged
- [x] Existing test files (.spec.ts) remain accessible

### Coexistence Strategy

**Approach**: Subdirectory-based separation
- **Java tests**: `/e2e-tests/java/` (Maven-managed)
- **TypeScript tests**: `/e2e-tests/web/` and `/e2e-tests/mobile/` (npm-managed)
- **Package files**: `/e2e-tests/package.json` (root level for TypeScript)
- **No interference**: Both stacks operate independently

### Validation Notes

**Last Verified**: 2025-11-26 (during feature 016 implementation)

**TypeScript Test Status**: ✅ FULLY FUNCTIONAL
- Web tests (Playwright) location: `/e2e-tests/web/`
- Mobile tests (Appium+WebdriverIO) location: `/e2e-tests/mobile/`
- npm scripts operational: All test commands working
- No directory conflicts detected

**Java Test Status**: ✅ INFRASTRUCTURE ESTABLISHED
- Maven project builds successfully (`mvn clean install`)
- Directory structure complete
- Dependencies resolved (Selenium, Appium, Cucumber)
- No test implementations yet (Phase 1 complete, Phase 2+ pending)

---

## Which Test Stack Should I Use?

Choose based on your feature branch context:

| Situation | Recommended Stack | Why |
|-----------|------------------|-----|
| **New E2E test for new feature** | Java/Cucumber | Learn new stack with fresh context, no migration burden |
| **Updating existing E2E test** | TypeScript (current) OR Java (migrate) | Your choice - maintain consistency or take opportunity to migrate |
| **Bug fix in E2E test** | TypeScript (current stack) | Quick fix in familiar stack, optionally migrate afterwards |
| **Large E2E test refactor** | Java/Cucumber | Good opportunity to migrate while restructuring anyway |
| **Working on feature branch with TypeScript tests** | TypeScript | No pressure to migrate - focus on feature development |

**No forced timeline**: TypeScript tests will be removed only when all active feature branches have migrated organically.

---

## CI/CD Integration

**Current Status**: TypeScript tests run in CI/CD  
**Planned**: Both TypeScript and Java tests will run in parallel during transition period

**TypeScript Commands** (existing):
```bash
# Web
npm run test:web

# Mobile
npm run test:mobile:android
npm run test:mobile:ios
```

**Java Commands** (new):
```bash
# Web
mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@web"

# Mobile
mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@android"
mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios"
```

---

## Support & Documentation

- **Feature Specification**: `/specs/016-e2e-java-migration/spec.md`
- **Implementation Plan**: `/specs/016-e2e-java-migration/plan.md`
- **Quickstart Guide**: `/specs/016-e2e-java-migration/quickstart.md`
- **Migration Guide**: `/specs/016-e2e-java-migration/MIGRATION_GUIDE.md` (to be created)

**Questions?** Check the quickstart guide or reach out to the QA team.
