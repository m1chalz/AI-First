# Spec 050 - Empty State Test BLOCKED

**Date**: 2025-12-16  
**Blocker**: iOS E2E infrastructure issues

---

## Current Status

- ✅ iOS app has `accessibilityIdentifier = "animalList.emptyState"` 
- ✅ iOS sends location to API (AnnouncementRepository.swift)
- ✅ Test scenario written: `@ios @location` empty state test
- ❌ Cannot run test - infrastructure blocked

---

## Blocking Issues

### 1. Simulator Not Auto-Started
**Symptom**: E2E test does not start iOS Simulator automatically  
**Impact**: User must manually open Simulator before test  
**Expected**: Test should auto-start or check if simulator running

### 2. WebDriverAgent Connection Failure
**Symptom**: After manual simulator start, Appium loops with:
```
[XCUITestDriver@86fa] connect ECONNREFUSED 127.0.0.1:8100
[XCUITestDriver@c216] Proxying [GET /status] to [GET http://127.0.0.1:8100/status] with no body
```

**Root Cause**: WebDriverAgent not running in simulator on port 8100

**Why This Happens**:
- Appium needs WebDriverAgent (WDA) running inside simulator
- WDA is the bridge between Appium and iOS simulator
- Port 8100 is where WDA listens for commands
- If WDA not installed/started → ECONNREFUSED

---

## Required Infrastructure Setup (Not Yet Done)

### WebDriverAgent Setup Checklist

1. **Verify Appium XCUITest Driver**
   ```bash
   appium driver list
   # Should show: xcuitest@<version> [installed]
   ```

2. **Check WDA Location**
   ```bash
   ls ~/.appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent/
   # Should contain WebDriverAgent.xcodeproj
   ```

3. **Build WDA (if needed)**
   ```bash
   cd ~/.appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent/
   xcodebuild -project WebDriverAgent.xcodeproj -scheme WebDriverAgentRunner \
     -destination 'platform=iOS Simulator,name=iPhone 15' test
   ```

4. **Xcode Command Line Tools**
   ```bash
   xcode-select -p
   # Should output: /Applications/Xcode.app/Contents/Developer
   ```

---

## Workaround: Run from External Terminal

**Problem**: Cursor sandbox blocks Appium access to `~/.appium`  
**Solution**: Run ALL commands from iTerm/Terminal.app

```bash
# Terminal 1: Backend (already running - PID 1169)
cd server && npm run dev

# Terminal 2: Appium (already running - PID 4207)  
appium

# Terminal 3: E2E Test (MUST be iTerm/Terminal.app!)
cd e2e-tests/java
mvn test -Dtest=IosTestRunner \
  -DPLATFORM=iOS \
  -Dcucumber.filter.tags="@ios and @location and not @pending" \
  -Dskip.app.build=true \
  -Ddebug.screenshots=true
```

---

## Next Actions

1. **Investigate WDA Setup** (P0)
   - Check if XCUITest driver installed correctly
   - Verify WDA can be built/launched in simulator
   - Document required setup steps

2. **Fix Simulator Auto-Start** (P1)
   - Check AppiumDriverManager.java iOS driver creation
   - Add logic to start simulator if not running
   - Or document manual start requirement

3. **Update E2E README** (P1)
   - Add iOS-specific pre-requisites section
   - Document WDA setup steps
   - Add troubleshooting for ECONNREFUSED 8100

4. **Test Once Unblocked** (P0)
   - Run empty state test scenario
   - Verify location filtering works
   - Update spec 050 with results

---

## Related Files

- `/e2e-tests/java/PROGRESS-2025-12-16.md` - Previous blocker investigation
- `/e2e-tests/java/TODO-iOS-18.1-fix.md` - iOS 18.1 compatibility issues
- `/e2e-tests/java/src/test/resources/features/animal-list.feature:133` - Empty state scenario
- `/specs/050-e2e-animal-list/tasks.md` - Main tasks document

---

## Success Criteria (When Unblocked)

- [ ] WebDriverAgent connects successfully (no ECONNREFUSED)
- [ ] Simulator starts automatically OR requirement documented
- [ ] Empty state test runs and passes
- [ ] Location filtering verified working on iOS
- [ ] Documentation updated with setup steps

