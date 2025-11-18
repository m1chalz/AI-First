# Tasks: E2E Testing Infrastructure Setup

**Feature**: E2E Testing Infrastructure  
**Branch**: `004-e2e-testing-infrastructure`  
**Status**: Ready for Implementation

---

## Summary

| Metric | Value |
|--------|-------|
| **Total Tasks** | 21 |
| **User Stories** | 3 (P1, P2, P3) |
| **Phases** | 4 |
| **Parallel Opportunities** | 12 tasks |
| **MVP Scope** | Phase 2 (US1) - 11 tasks |
| **Estimated Effort** | 3-4 hours |

---

## Task List

### Phase 1: Setup (Prerequisites)

**Goal**: Create branch and feature structure

- [x] T001 Create feature branch `004-e2e-testing-infrastructure`
- [x] T002 Create feature directory at specs/004-e2e-testing-infrastructure/
- [x] T003 Create spec.md with requirements
- [x] T004 Create plan.md with technical approach

**Status**: ✅ Completed

---

### Phase 2: Basic E2E Infrastructure (US1)

**Goal**: Complete E2E testing infrastructure setup per Constitution Principle VI

**Story**: P1 - Basic E2E Infrastructure  
**Acceptance Criteria**:
- All directory structure in place
- Configuration files created and functional
- Dependencies installed
- npm scripts working

#### Directory Structure

- [x] T005 [P] [US1] Create e2e-tests/ root directory
- [x] T006 [P] [US1] Create e2e-tests/web/ subdirectories (specs/, pages/, steps/, fixtures/)
- [x] T007 [P] [US1] Create e2e-tests/mobile/ subdirectories (specs/, screens/, steps/, utils/)

#### Configuration Files

- [ ] T008 [P] [US1] Create playwright.config.ts at repo root with TypeScript support, HTML reporter, test directory configuration
- [ ] T009 [P] [US1] Create wdio.conf.ts at repo root with Appium service, Android/iOS capabilities, Mocha framework
- [ ] T010 [US1] Update .cursor/mcp.json to add Appium MCP server configuration

#### Dependencies

- [ ] T011 [US1] Add E2E dependencies to root package.json devDependencies (@playwright/test, appium, webdriverio, @wdio/cli, @wdio/appium-service, @wdio/local-runner, @wdio/mocha-framework, @wdio/spec-reporter)
- [ ] T012 [US1] Install all dependencies with npm install

#### npm Scripts

- [ ] T013 [P] [US1] Add test:e2e:web script to root package.json (npx playwright test)
- [ ] T014 [P] [US1] Add test:e2e:web:ui script to root package.json (npx playwright test --ui)
- [ ] T015 [P] [US1] Add test:mobile:android script to root package.json (wdio run wdio.conf.ts --spec ./e2e-tests/mobile/specs/**/*.spec.ts)

**Independent Test Criteria for US1**:
- Run `npm run test:e2e:web -- --dry-run` - should validate configuration without running tests
- Run `npm run test:mobile:android -- --dry-run` - should validate configuration without running tests
- Verify all directories exist with `ls -la e2e-tests/`
- Verify MCP configuration with `cat .cursor/mcp.json`

---

### Phase 3: Example Tests & Patterns (US2)

**Goal**: Provide example tests demonstrating Page Object Model and Screen Object Model patterns

**Story**: P2 - Example Tests & Patterns  
**Acceptance Criteria**:
- Example Playwright test with Page Object Model
- Example Appium test with Screen Object Model
- Both follow Given-When-Then structure
- Both use proper test identifiers

**Dependencies**: Must complete Phase 2 (US1) first

#### Web Example (Playwright)

- [ ] T016 [P] [US2] Create e2e-tests/web/pages/ExamplePage.ts with Page Object Model class demonstrating proper TypeScript types and test identifier usage (data-testid)
- [ ] T017 [US2] Create e2e-tests/web/specs/example.spec.ts with Playwright test using Given-When-Then structure and ExamplePage

#### Mobile Example (Appium)

- [ ] T018 [P] [US2] Create e2e-tests/mobile/screens/ExampleScreen.ts with Screen Object Model class demonstrating proper TypeScript types and test identifier usage (testTag)
- [ ] T019 [US2] Create e2e-tests/mobile/specs/example.spec.ts with Appium test using Given-When-Then structure and ExampleScreen

**Independent Test Criteria for US2**:
- Run `npm run test:e2e:web` - example test should pass
- Run `npm run test:mobile:android` with emulator - example test should pass (or fail gracefully with clear error if no app)
- Verify Given-When-Then structure in test files
- Verify proper TypeScript compilation with no errors

---

### Phase 4: Documentation & Onboarding (US3)

**Goal**: Comprehensive documentation for E2E testing

**Story**: P3 - Documentation & Onboarding  
**Acceptance Criteria**:
- README.md with setup instructions
- Examples of running tests
- Pattern explanations
- Troubleshooting guide

**Dependencies**: Must complete Phase 3 (US2) first

#### Documentation Files

- [ ] T020 [P] [US3] Create e2e-tests/README.md with main E2E documentation, setup instructions, test execution commands, troubleshooting section
- [ ] T021 [P] [US3] Create e2e-tests/web/README.md explaining Page Object Model pattern, web test examples, best practices

**Independent Test Criteria for US3**:
- Documentation references all created files correctly
- All commands in documentation are executable
- Documentation covers both web and mobile testing
- Troubleshooting section addresses common setup issues

---

## Dependencies Graph

```
Phase 1 (Setup) [COMPLETED]
    ↓
Phase 2 (US1: Infrastructure) [BLOCKING]
    ↓
Phase 3 (US2: Examples)
    ↓
Phase 4 (US3: Documentation)
```

**Story Dependencies**:
- **US1** (Infrastructure): No dependencies - can start immediately
- **US2** (Examples): Depends on US1 (needs configuration and directories)
- **US3** (Documentation): Depends on US2 (should reference working examples)

---

## Parallel Execution Opportunities

### Phase 2 (US1) - 7 Parallel Tasks:
Can run in parallel (different files, no dependencies):
- T005, T006, T007 (directory creation)
- T008, T009 (configuration file creation)
- T013, T014, T015 (npm scripts - same file but different sections)

### Phase 3 (US2) - 2 Parallel Tasks:
Can run in parallel:
- T016, T018 (web and mobile Page/Screen objects - different files)

### Phase 4 (US3) - 2 Parallel Tasks:
Can run in parallel:
- T020, T021 (documentation files - different files)

**Total Parallel Tasks**: 12 out of 21 tasks (57%)

---

## Implementation Strategy

### Approach
**Infrastructure-first**: Set up all configuration and structure before adding examples and documentation

### MVP Scope
**Phase 2 (US1)** constitutes the MVP:
- Complete directory structure
- Working configuration files
- Installed dependencies
- Executable npm scripts

This provides the foundation for all future E2E test development.

### Incremental Delivery

1. **Phase 1**: ✅ Setup complete
2. **Phase 2**: Infrastructure (US1) - **DELIVERABLE: Can write E2E tests**
3. **Phase 3**: Examples (US2) - **DELIVERABLE: Team can follow patterns**
4. **Phase 4**: Documentation (US3) - **DELIVERABLE: Self-service onboarding**

Each phase delivers independent value.

---

## Constitution Compliance

### ✅ Principle VI: End-to-End Testing (NON-NEGOTIABLE)

**Web Platform** (`/e2e-tests/web/`):
- [x] Framework: Playwright with TypeScript (T008)
- [x] Config: `playwright.config.ts` at repo root (T008)
- [x] Test location: `/e2e-tests/web/specs/` (T006)
- [x] Run command: `npx playwright test` (T013)

**Mobile Platforms** (`/e2e-tests/mobile/`):
- [x] Framework: Appium with TypeScript + WebdriverIO (T009)
- [x] Config: `wdio.conf.ts` (T009)
- [x] Test location: `/e2e-tests/mobile/specs/` (T007)
- [x] Run commands: `npm run test:mobile:android` / `ios` (T015)

**Requirements**:
- [x] Tests written in TypeScript (all tasks)
- [x] Page Object Model pattern REQUIRED (T016)
- [x] Screen Object Model pattern REQUIRED (T018)
- [x] Tests executable in CI/CD pipeline (T013, T015)

### ✅ Principle XII: Given-When-Then Test Convention

- [x] Example tests follow Given-When-Then structure (T017, T019)
- [x] Test phases clearly separated with comments (T017, T019)
- [x] Descriptive test names using TypeScript strings (T017, T019)

---

## Validation Checklist

After completing all tasks, verify:

### Infrastructure (US1)
- [ ] Directory structure matches constitution: `ls -R e2e-tests/`
- [ ] Playwright config exists and is valid: `npx playwright test --dry-run`
- [ ] WebdriverIO config exists and is valid: `npm run test:mobile:android -- --dry-run`
- [ ] MCP has both servers: `cat .cursor/mcp.json | grep -E "(Playwright|Appium)"`
- [ ] All dependencies installed: `npm list @playwright/test appium webdriverio`
- [ ] npm scripts work: `npm run test:e2e:web -- --dry-run`

### Examples (US2)
- [ ] Web example test passes: `npm run test:e2e:web`
- [ ] Web Page Object follows pattern: Check ExamplePage.ts structure
- [ ] Mobile example test exists: Check example.spec.ts in mobile/specs/
- [ ] Mobile Screen Object follows pattern: Check ExampleScreen.ts structure
- [ ] All tests use Given-When-Then: Review test file comments

### Documentation (US3)
- [ ] Main README exists: `cat e2e-tests/README.md`
- [ ] Web README exists: `cat e2e-tests/web/README.md`
- [ ] All commands in docs are correct: Test each command
- [ ] Troubleshooting section covers common issues

### Constitution Compliance
- [ ] All Principle VI requirements met
- [ ] All Principle XII requirements met (Given-When-Then)
- [ ] TypeScript used throughout (no JavaScript files)

---

## File Changes Summary

### New Directories (9)
- `/e2e-tests/`
- `/e2e-tests/web/specs/`
- `/e2e-tests/web/pages/`
- `/e2e-tests/web/steps/`
- `/e2e-tests/web/fixtures/`
- `/e2e-tests/mobile/specs/`
- `/e2e-tests/mobile/screens/`
- `/e2e-tests/mobile/steps/`
- `/e2e-tests/mobile/utils/`

### New Files (9)
- `/playwright.config.ts` (~80 lines)
- `/wdio.conf.ts` (~120 lines)
- `/e2e-tests/web/pages/ExamplePage.ts` (~30 lines)
- `/e2e-tests/web/specs/example.spec.ts` (~50 lines)
- `/e2e-tests/mobile/screens/ExampleScreen.ts` (~40 lines)
- `/e2e-tests/mobile/specs/example.spec.ts` (~60 lines)
- `/e2e-tests/README.md` (~150 lines)
- `/e2e-tests/web/README.md` (~80 lines)
- `/e2e-tests/mobile/README.md` (~100 lines)

### Modified Files (2)
- `/.cursor/mcp.json` (add Appium MCP server entry)
- `/package.json` (add 8 devDependencies + 3 scripts)

**Total Lines Added**: ~710 lines  
**Total Files Modified**: 2 files  
**Total New Files**: 9 files

---

## Next Steps

1. **Review tasks**: Ensure all tasks are clear and actionable
2. **Start Phase 2**: Begin with directory structure (T005-T007)
3. **Configure tools**: Complete configuration files (T008-T010)
4. **Install dependencies**: Run npm install (T011-T012)
5. **Add scripts**: Update package.json with test scripts (T013-T015)
6. **Verify setup**: Run validation commands from US1 test criteria
7. **Proceed to Phase 3**: Add example tests once infrastructure verified
8. **Complete Phase 4**: Add documentation after examples are working
9. **Final validation**: Run complete validation checklist
10. **Commit & PR**: Create pull request with all changes

---

## Notes

- **Mobile testing**: Requires Android SDK (for Android) and Xcode (for iOS) to be installed locally
- **Emulators/Simulators**: Example mobile tests require running emulator/simulator
- **MCP Appium**: If `appium-mcp` package doesn't work, document manual Appium testing as fallback
- **CI/CD**: Configuration is CI/CD ready but actual pipeline setup is out of scope for this feature
- **Future work**: Actual feature E2E tests will be added per feature specification in separate branches

---

**Ready to implement**: All tasks defined and ready for execution starting with Phase 2 (T005).

