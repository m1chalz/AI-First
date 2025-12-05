# Tasks: Selenium Docker Infrastructure

## Task 1: Create Docker Compose Configuration
**Priority**: P1  
**Estimated**: 30 min

### Description
Create `docker-compose.yml` with Selenium Hub and Chrome node using version 131 (compatible with Selenium 4.29.0 devtools-v131).

### Acceptance Criteria
- [ ] `docker-compose.yml` created in `e2e-tests/java/`
- [ ] Selenium Hub starts on port 4444
- [ ] Chrome node version 131.0 connects to Hub
- [ ] VNC port 7900 exposed for debugging
- [ ] `docker-compose up -d` starts all services

### Files to Create/Modify
- `e2e-tests/java/docker-compose.yml` (new)

---

## Task 2: Create Helper Scripts
**Priority**: P1  
**Estimated**: 20 min

### Description
Create shell scripts for easy management of Docker Selenium environment.

### Acceptance Criteria
- [ ] `start-selenium.sh` starts Docker Compose in background
- [ ] `stop-selenium.sh` stops and removes containers
- [ ] `wait-for-selenium.sh` waits until Grid is ready (max 60s)
- [ ] Scripts are executable (`chmod +x`)
- [ ] Scripts work on macOS and Linux

### Files to Create
- `e2e-tests/java/scripts/start-selenium.sh`
- `e2e-tests/java/scripts/stop-selenium.sh`
- `e2e-tests/java/scripts/wait-for-selenium.sh`

---

## Task 3: Update WebDriverManager for Remote Support
**Priority**: P1  
**Estimated**: 45 min

### Description
Modify `WebDriverManager.java` to support both local and remote WebDriver based on system properties.

### Acceptance Criteria
- [ ] Add `webdriver.remote` system property (default: false)
- [ ] Add `webdriver.remote.url` system property (default: http://localhost:4444)
- [ ] When `webdriver.remote=true`, use `RemoteWebDriver` instead of `ChromeDriver`
- [ ] CDP features work with remote driver
- [ ] Existing local tests continue to work unchanged

### System Properties
```
-Dwebdriver.remote=true           # Use remote WebDriver
-Dwebdriver.remote.url=http://...  # Custom Grid URL
```

### Files to Modify
- `e2e-tests/java/src/test/java/.../utils/WebDriverManager.java`

---

## Task 4: Test CDP Geolocation with Docker Selenium
**Priority**: P1  
**Estimated**: 30 min

### Description
Verify that CDP geolocation mocking works correctly when using Docker Selenium with Chrome 131.

### Acceptance Criteria
- [ ] Start Docker Selenium (Chrome 131)
- [ ] Run geolocation test with `-Dwebdriver.remote=true`
- [ ] No CDP version mismatch warnings
- [ ] Geolocation mock works correctly
- [ ] Remove `@pending` tag from location filtering test

### Test Commands
```bash
docker-compose up -d
./scripts/wait-for-selenium.sh
mvn test -Dtest=WebTestRunner -Dcucumber.filter.tags="@animalList" -Dwebdriver.remote=true
docker-compose down
```

---

## Task 5: Update Documentation
**Priority**: P2  
**Estimated**: 20 min

### Description
Update `e2e-tests/README.md` with Docker Selenium usage instructions.

### Acceptance Criteria
- [ ] Document Docker prerequisites
- [ ] Document how to start/stop Selenium Grid
- [ ] Document how to run tests with remote driver
- [ ] Document VNC debugging access
- [ ] Document troubleshooting common issues

### Files to Modify
- `e2e-tests/README.md`

---

## Task 6: CI/CD Integration (Optional)
**Priority**: P3  
**Estimated**: 1 hour

### Description
Update CI/CD pipeline to use Docker Selenium for E2E tests.

### Acceptance Criteria
- [ ] CI pipeline starts Selenium Grid before tests
- [ ] Tests run with `-Dwebdriver.remote=true`
- [ ] Selenium Grid is stopped after tests
- [ ] Pipeline handles failures gracefully

---

## Summary

| Task | Priority | Time | Dependencies |
|------|----------|------|--------------|
| 1. Docker Compose | P1 | 30 min | - |
| 2. Helper Scripts | P1 | 20 min | Task 1 |
| 3. WebDriverManager | P1 | 45 min | - |
| 4. Test CDP | P1 | 30 min | Tasks 1, 2, 3 |
| 5. Documentation | P2 | 20 min | Tasks 1-4 |
| 6. CI/CD | P3 | 1 hour | Tasks 1-5 |

**Total estimated time**: ~3.5 hours



