# Selenium Grid Setup Guide

This directory contains Docker Compose configurations for running Selenium Grid with E2E tests.

## 🎯 Quick Start (Recommended)

**Simplest setup** - Grid in Docker + Backend/Frontend on host (localhost):

```bash
cd e2e-tests

# 1. Start backend and frontend on host
cd ../server && npm run dev &          # Backend on localhost:3000
cd ../webApp && npm run start &        # Frontend on localhost:8080

# 2. Start Selenium Grid (auto-detects ARM/x86)
cd ../e2e-tests
./start-selenium-grid.sh

# 3. Run tests
cd java
mvn test -Dtest=WebTestRunner -Dwebdriver.remote=true

# 4. Stop Grid
cd ..
./stop-selenium-grid.sh
```

**How it works:**
- ✅ Backend + Frontend run natively on your Mac (localhost:3000, localhost:8080)
- ✅ Selenium Grid in Docker uses `extra_hosts` to map `localhost` → host machine
- ✅ Browser in Docker opens `http://localhost:8080` and reaches your Mac!
- ✅ **Zero code changes** - uses standard `localhost` URLs everywhere

---

## Alternative: Complete QA Environment (All-in-One Docker)

If you want **everything in Docker** (Backend + Frontend + Selenium Grid):

```bash
cd e2e-tests

# Start everything (Backend, Frontend, Selenium Grid)
docker-compose -f docker-compose.qa-env.yml up -d

# Verify services are running
docker-compose -f docker-compose.qa-env.yml ps

# Run tests
cd java
mvn test -Dtest=WebTestRunner -Dwebdriver.remote=true

# Stop everything
cd ..
docker-compose -f docker-compose.qa-env.yml down
```

**What you get:**
- ✅ Backend API in Docker (localhost:3000)
- ✅ Frontend in Docker with runtime API URL configuration (localhost:8080)
- ✅ Selenium Grid with Chrome + Firefox (localhost:4444)
- ✅ All services connected via internal Docker network
- ✅ Zero code changes needed - uses `Dockerfile.qa` with nginx sub_filter

**Architecture:**
```
┌─────────────────────────────────────────────┐
│  Docker Network (qa-network)                │
│                                             │
│  ┌──────────┐   ┌──────────┐   ┌────────┐ │
│  │ Backend  │◄──│ Frontend │◄──│ Chrome │ │
│  │ :3000    │   │ :8080    │   │        │ │
│  └────┬─────┘   └─────┬────┘   └────────┘ │
│       │               │                     │
└───────┼───────────────┼─────────────────────┘
        │               │
   localhost:3000  localhost:8080
        ▲               ▲
        │               │
    Tests (Host)    Browser Access
```

---

## Option 2: Grid Only (Manual Architecture)

If you prefer to run Backend + Frontend locally and **only Grid in Docker**:

```bash
cd e2e-tests

# Start Grid only (automatically detects ARM vs x86 architecture)
./start-selenium-grid.sh

# Stop Grid
./stop-selenium-grid.sh
```

The script will:
- ✅ Auto-detect your CPU architecture (ARM vs x86)
- ✅ Start the appropriate Selenium Grid configuration
- ✅ Wait for Grid to be ready
- ✅ Display Grid status and access URLs

Then run Backend + Frontend manually:
```bash
# Terminal 1: Backend
cd server && npm run dev

# Terminal 2: Frontend
cd webApp && npm run start
```

---

## How It Works: `extra_hosts` Magic

Docker Compose configurations use `extra_hosts` to map `localhost` inside containers to your host machine:

```yaml
chrome:
  image: seleniarm/node-chromium:latest
  extra_hosts:
    - "localhost:host-gateway"  # Maps localhost → MacBook IP
```

This means:
- Browser in Docker tries to open `http://localhost:8080`
- Docker resolves `localhost` → your MacBook's IP (via `host-gateway`)
- Request reaches frontend running on your Mac! 🎉

**No application code changes needed!**

---

## Architecture-Specific Setup

### ARM (Apple Silicon - M1/M2/M3)

```bash
docker-compose -f docker-compose.selenium-arm.yml up -d

# Verify
curl http://localhost:4444/wd/hub/status
```

### x86/amd64 (Intel/AMD)

```bash
docker-compose -f docker-compose.selenium-x86.yml up -d

# Verify
curl http://localhost:4444/wd/hub/status
```

---

## Grid Access

Once Grid is running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| **Selenium Hub** | `http://localhost:4444` | WebDriver endpoint |
| **Grid Console** | `http://localhost:4444/ui` | Web UI to monitor Grid |
| **Chrome VNC** | `vnc://localhost:5900` | Debug Chrome sessions |
| **Firefox VNC** | `vnc://localhost:5901` | Debug Firefox sessions |
| **Edge VNC** (x86 only) | `vnc://localhost:5902` | Debug Edge sessions |

**Note**: VNC connections don't require a password (`VNC_NO_PASSWORD=1`)

---

## Available Browsers

### ARM (Seleniarm)
- ✅ Chrome (Chromium)
- ✅ Firefox

### x86 (Selenium)
- ✅ Chrome
- ✅ Firefox
- ✅ Edge

---

## Configuration

Both Grid configurations support:

- **Max sessions per node**: 5 (configurable via `SE_NODE_MAX_SESSIONS`)
- **Session timeout**: 300 seconds (5 minutes)
- **Shared memory**: 2GB per browser node
- **Health checks**: Automatic Grid readiness detection

---

## Running Tests Against Grid

### Update WebDriverManager (Java)

Modify `src/test/java/.../utils/WebDriverManager.java` to support remote execution:

```java
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

public class WebDriverManager {
    private static void initializeDriver() {
        String remoteUrl = System.getProperty("webdriver.remote.url", "http://localhost:4444");
        boolean useRemote = Boolean.parseBoolean(System.getProperty("webdriver.remote", "false"));
        
        ChromeOptions options = new ChromeOptions();
        // ... your existing options ...
        
        WebDriver webDriver;
        if (useRemote) {
            webDriver = new RemoteWebDriver(new URL(remoteUrl), options);
            System.out.println("Using Remote WebDriver at: " + remoteUrl);
        } else {
            webDriver = new ChromeDriver(options);
        }
        
        driver.set(webDriver);
    }
}
```

### Run Tests (with Grid Only)

```bash
# Start Grid
./start-selenium-grid.sh

# Run tests against remote Grid
mvn test -Dtest=WebTestRunner -Dwebdriver.remote=true

# Or with custom Grid URL
mvn test -Dtest=WebTestRunner \
  -Dwebdriver.remote=true \
  -Dwebdriver.remote.url=http://selenium-hub:4444

# Stop Grid
./stop-selenium-grid.sh
```

---

## Troubleshooting

### Grid Not Starting

**Check Docker is running:**
```bash
docker info
```

**Check port conflicts:**
```bash
lsof -i :4444  # Should be empty before starting Grid
```

**View logs:**
```bash
# ARM
docker-compose -f docker-compose.selenium-arm.yml logs

# x86
docker-compose -f docker-compose.selenium-x86.yml logs
```

### Tests Can't Connect to Grid

**Verify Grid status:**
```bash
curl http://localhost:4444/wd/hub/status
```

Expected response:
```json
{
  "value": {
    "ready": true,
    "message": "Selenium Grid ready."
  }
}
```

**Check Grid console:**
```bash
open http://localhost:4444/ui
```

### Wrong Architecture

If you get platform errors like "no matching manifest for linux/arm64/v8":

1. Check your architecture:
   ```bash
   uname -m
   # arm64 → use docker-compose.selenium-arm.yml
   # x86_64 → use docker-compose.selenium-x86.yml
   ```

2. Stop any running Grid:
   ```bash
   ./stop-selenium-grid.sh
   ```

3. Start the correct Grid:
   ```bash
   ./start-selenium-grid.sh  # Auto-detects
   ```

### Browser Nodes Not Connecting

**Check event bus connectivity:**
```bash
# ARM
docker logs seleniarm-event-bus

# x86
docker logs selenium-event-bus
```

**Restart Grid:**
```bash
./stop-selenium-grid.sh
./start-selenium-grid.sh
```

---

## Pinning Browser Versions

To use specific browser versions (e.g., for CDP compatibility):

### Edit docker-compose file

```yaml
chrome:
  image: selenium/node-chrome:131.0  # Specific version
  # OR
  image: seleniarm/node-chromium:131.0  # ARM version
```

Available versions: https://hub.docker.com/u/selenium (or `/u/seleniarm` for ARM)

### Rebuild

```bash
docker-compose -f docker-compose.selenium-{arm|x86}.yml pull
docker-compose -f docker-compose.selenium-{arm|x86}.yml up -d
```

---

## Scaling (Parallel Execution)

To run tests in parallel, scale browser nodes:

```bash
# Add 3 Chrome instances (total 4)
docker-compose -f docker-compose.selenium-arm.yml up -d --scale chrome=4

# Check status
curl http://localhost:4444/wd/hub/status | jq '.value.nodes'
```

Each node supports 5 concurrent sessions by default (`SE_NODE_MAX_SESSIONS=5`).

---

## Network Integration

### Connect Grid to Other Services

To allow Selenium Grid to access your backend/web app:

```yaml
networks:
  selenium-grid:
    name: selenium-grid-arm
  petspot-network:  # Your app network
    external: true

services:
  chrome:
    networks:
      - selenium-grid
      - petspot-network  # Now can access backend/frontend
```

### Use Docker Service Names

In tests, reference services by Docker name:

```java
// Instead of localhost
String appUrl = "http://petspot-frontend:8080";
String apiUrl = "http://petspot-backend:3000";
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: E2E Tests

jobs:
  e2e:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Start Selenium Grid
        run: |
          cd e2e-tests
          docker-compose -f docker-compose.selenium-x86.yml up -d
          
      - name: Wait for Grid
        run: |
          timeout 60 bash -c 'until curl -s http://localhost:4444/wd/hub/status; do sleep 2; done'
          
      - name: Run E2E Tests
        run: |
          cd e2e-tests/java
          mvn test -Dtest=WebTestRunner -Dwebdriver.remote=true
          
      - name: Stop Grid
        if: always()
        run: |
          cd e2e-tests
          docker-compose -f docker-compose.selenium-x86.yml down
```

---

## References

- [Selenium Grid Documentation](https://www.selenium.dev/documentation/grid/)
- [Selenium Docker Images](https://github.com/SeleniumHQ/docker-selenium)
- [Seleniarm (ARM Images)](https://github.com/seleniumhq-community/docker-seleniarm)
- [Spec 053: Selenium Docker Infrastructure](../specs/053-selenium-docker/spec.md)

---

## Files in This Setup

```
e2e-tests/
├── docker-compose.selenium-arm.yml    # ARM architecture (Apple Silicon)
├── docker-compose.selenium-x86.yml    # x86/amd64 architecture (Intel/AMD)
├── start-selenium-grid.sh             # Auto-start script (detects architecture)
├── stop-selenium-grid.sh              # Stop all Grid containers
├── SELENIUM-GRID.md                   # This file
└── README.md                          # Main E2E testing documentation
```

