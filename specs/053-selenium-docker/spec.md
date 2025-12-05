# Feature Specification: Selenium Docker Infrastructure for E2E Tests

**Feature Branch**: `053-selenium-docker`  
**Created**: 2025-12-04  
**Status**: Draft  
**Input**: Problem z uruchamianiem testów E2E na lokalnych przeglądarkach - wersja Chrome 142 jest zbyt nowa dla dostępnych wersji Selenium DevTools (CDP). Docker z Selenium Grid rozwiązuje ten problem przez kontrolowaną wersję przeglądarki.

## Problem Statement

### Current Issues
1. **Chrome Version Mismatch**: Chrome 142 is too new - Selenium 4.29.0 only has devtools up to v133
2. **CDP Geolocation Mocking**: Tests requiring geolocation mocking fail with "no-op CDP implementation" error
3. **Environment Inconsistency**: Different developers may have different Chrome versions, causing flaky tests
4. **CI/CD Reliability**: Tests may fail in CI due to browser version updates

### Solution
Use Selenium Grid with Docker containers that have fixed, known Chrome versions matching available Selenium DevTools.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Run E2E Tests on Docker Selenium (Priority: P1)

**As a** developer  
**I can** run E2E tests against Selenium Grid in Docker  
**So that** I have a consistent browser environment with working CDP support

**Why this priority**: This is the core functionality - without a working Selenium Grid, tests requiring CDP (like geolocation mocking) cannot run reliably.

**Acceptance Scenarios**:

1. **Given** Docker is installed and running, **When** I execute `docker-compose up -d` in the e2e-tests directory, **Then** Selenium Grid Hub and Chrome node containers start successfully
2. **Given** Selenium Grid is running, **When** I run `mvn test -Dwebdriver.remote=true`, **Then** tests execute against the remote Chrome browser in Docker
3. **Given** tests run against Docker Selenium, **When** a test uses CDP geolocation mocking, **Then** the mock works correctly (no version mismatch errors)

---

### User Story 2 - Easy Setup and Teardown (Priority: P2)

**As a** developer  
**I can** quickly start and stop the Selenium Docker environment  
**So that** I don't waste resources when not testing

**Acceptance Scenarios**:

1. **Given** I want to run E2E tests, **When** I run the start script, **Then** Selenium Grid is ready within 30 seconds
2. **Given** I finished testing, **When** I run the stop script, **Then** all containers are stopped and resources are freed
3. **Given** Selenium Grid is running, **When** I access http://localhost:4444, **Then** I see the Selenium Grid dashboard

---

### User Story 3 - CI/CD Integration (Priority: P3)

**As a** CI/CD pipeline  
**I can** run E2E tests with Docker Selenium  
**So that** tests are reliable and don't fail due to browser version issues

**Acceptance Scenarios**:

1. **Given** CI environment with Docker, **When** the pipeline starts Selenium Grid and runs tests, **Then** all tests pass consistently
2. **Given** multiple parallel test runs, **When** tests execute concurrently, **Then** Selenium Grid handles the load without failures

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a `docker-compose.yml` file that starts Selenium Grid Hub and Chrome node
- **FR-002**: Chrome node MUST use a version compatible with Selenium 4.29.0 devtools (Chrome 131-133)
- **FR-003**: WebDriverManager MUST support switching between local and remote WebDriver via system property
- **FR-004**: Remote WebDriver MUST connect to Selenium Grid Hub at configurable URL (default: http://localhost:4444)
- **FR-005**: System MUST provide scripts for starting (`start-selenium.sh`) and stopping (`stop-selenium.sh`) the Docker environment
- **FR-006**: CDP features (geolocation mocking) MUST work correctly when using remote WebDriver
- **FR-007**: System MUST support running tests in headless mode on Docker (default) with option for headed mode for debugging
- **FR-008**: Selenium Grid MUST expose port 4444 for WebDriver connections and port 7900 for VNC access (debugging)

### Non-Functional Requirements

- **NFR-001**: Selenium Grid MUST start within 30 seconds
- **NFR-002**: Tests MUST not be slower than 20% compared to local execution
- **NFR-003**: Docker images MUST be pulled from official Selenium Docker Hub repository

### Technical Specifications

#### Docker Compose Configuration

```yaml
# e2e-tests/java/docker-compose.yml
version: "3.8"
services:
  selenium-hub:
    image: selenium/hub:4.27.0
    container_name: selenium-hub
    ports:
      - "4444:4444"
    environment:
      - SE_NODE_MAX_SESSIONS=4
      - SE_NODE_OVERRIDE_MAX_SESSIONS=true

  chrome:
    image: selenium/node-chrome:131.0
    container_name: selenium-chrome
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
      - SE_NODE_MAX_SESSIONS=4
    ports:
      - "7900:7900"  # VNC for debugging (noVNC)
```

#### WebDriverManager Changes

```java
// In WebDriverManager.java - add remote driver support
private static void initializeDriver() {
    String remoteUrl = System.getProperty("webdriver.remote.url", "http://localhost:4444");
    boolean useRemote = Boolean.parseBoolean(System.getProperty("webdriver.remote", "false"));
    
    ChromeOptions options = new ChromeOptions();
    // ... existing options ...
    
    WebDriver webDriver;
    if (useRemote) {
        webDriver = new RemoteWebDriver(new URL(remoteUrl), options);
        System.out.println("Using Remote WebDriver at: " + remoteUrl);
    } else {
        webDriver = new ChromeDriver(options);
    }
    
    driver.set(webDriver);
}
```

#### Usage Commands

```bash
# Start Selenium Grid
cd e2e-tests/java
docker-compose up -d

# Wait for Grid to be ready
./scripts/wait-for-selenium.sh

# Run tests against Docker Selenium
mvn test -Dtest=WebTestRunner -Dwebdriver.remote=true

# Stop Selenium Grid
docker-compose down
```

### Key Entities

- **Selenium Hub**: Central component that routes test requests to available nodes
- **Chrome Node**: Docker container with Chrome browser and ChromeDriver
- **RemoteWebDriver**: Selenium class for connecting to remote browser instances

### Dependencies

- Docker and Docker Compose installed on development/CI machines
- Network access to Docker Hub for pulling Selenium images
- Port 4444 available for Selenium Hub
- Port 7900 available for VNC debugging (optional)

### Assumptions

- Developers have Docker Desktop or equivalent installed
- CI/CD environment supports Docker-in-Docker or has Docker available
- Selenium official Docker images are stable and maintained

## File Structure

```
e2e-tests/java/
├── docker-compose.yml           # Selenium Grid configuration
├── scripts/
│   ├── start-selenium.sh        # Start Docker Selenium
│   ├── stop-selenium.sh         # Stop Docker Selenium
│   └── wait-for-selenium.sh     # Wait for Grid to be ready
└── src/test/java/.../utils/
    └── WebDriverManager.java    # Updated with remote support
```

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Tests with CDP geolocation mocking pass when run against Docker Selenium
- **SC-002**: Selenium Grid starts within 30 seconds
- **SC-003**: All existing tests pass when run with `-Dwebdriver.remote=true`
- **SC-004**: No Chrome version mismatch warnings in test output when using Docker

## Migration Path

1. **Phase 1**: Add Docker Compose and scripts (no changes to existing tests)
2. **Phase 2**: Update WebDriverManager to support remote driver
3. **Phase 3**: Update CI/CD pipeline to use Docker Selenium
4. **Phase 4**: Remove `@pending` tag from geolocation tests
5. **Phase 5**: Document usage in README

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Docker not available in CI | High | Ensure CI environment supports Docker |
| Network latency to remote browser | Medium | Run Selenium Grid on same machine/network |
| Docker image updates break tests | Low | Pin specific image versions |
| Port conflicts | Low | Make ports configurable |

## Out of Scope

- Firefox or Edge browser support (can be added later)
- Selenium Grid scaling beyond single node (can be added for parallel execution)
- Kubernetes deployment (future enhancement)
- Video recording of test execution (future enhancement)



