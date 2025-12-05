# Mobile Application Files for E2E Testing

This directory contains mobile application binaries (APK/IPA) used by Appium for E2E testing.

## Required Files

### Android
- **File**: `petspot-android.apk`
- **Platform**: Android 14 (API 34)
- **Build Command**: 
  ```bash
  cd /Users/szymon.wagner/projects/INTIVE/AI-First
  ./gradlew :composeApp:assembleDebug
  cp composeApp/build/outputs/apk/debug/composeApp-debug.apk e2e-tests/java/apps/petspot-android.apk
  ```
- **Size**: ~50-100 MB (typical for debug APK with Compose)

### iOS
- **File**: `petspot-ios.app`
- **Platform**: iOS 17.0+ (iPhone 15 Simulator)
- **Build Command**:
  ```bash
  cd /Users/szymon.wagner/projects/INTIVE/AI-First/iosApp
  xcodebuild -scheme iosApp -sdk iphonesimulator -configuration Debug -derivedDataPath build
  cp -r build/Build/Products/Debug-iphonesimulator/iosApp.app ../e2e-tests/java/apps/petspot-ios.app
  ```
- **Size**: ~100-200 MB (typical for iOS app bundle)

## Usage

These files are referenced by `AppiumDriverManager.java`:

```java
// Android
String appPath = System.getProperty("user.dir") + "/apps/petspot-android.apk";

// iOS
String appPath = System.getProperty("user.dir") + "/apps/petspot-ios.app";
```

## Setup Instructions

### First-Time Setup

1. **Build Android APK**:
   ```bash
   ./gradlew :composeApp:assembleDebug
   cp composeApp/build/outputs/apk/debug/composeApp-debug.apk e2e-tests/java/apps/petspot-android.apk
   ```

2. **Build iOS App** (requires macOS with Xcode):
   ```bash
   cd iosApp
   xcodebuild -scheme iosApp -sdk iphonesimulator -configuration Debug \
     -derivedDataPath build
   cp -r build/Build/Products/Debug-iphonesimulator/iosApp.app \
     ../e2e-tests/java/apps/petspot-ios.app
   ```

3. **Verify Files**:
   ```bash
   ls -lh e2e-tests/java/apps/
   # Should show:
   # petspot-android.apk (~50-100 MB)
   # petspot-ios.app/ (directory, ~100-200 MB)
   ```

### Before Running Tests

**Option 1: Use Latest Builds** (Recommended)
```bash
# Update Android APK
./gradlew :composeApp:assembleDebug && \
  cp composeApp/build/outputs/apk/debug/composeApp-debug.apk \
     e2e-tests/java/apps/petspot-android.apk

# Update iOS app
cd iosApp && \
  xcodebuild -scheme iosApp -sdk iphonesimulator -configuration Debug \
    -derivedDataPath build && \
  cp -r build/Build/Products/Debug-iphonesimulator/iosApp.app \
    ../e2e-tests/java/apps/petspot-ios.app
```

**Option 2: Use CI Artifacts**
```bash
# Download from CI/CD pipeline artifacts
# (specific commands depend on CI system)
```

## File Size Warning

⚠️ **These files are large (100-300 MB total) and should NOT be committed to git.**

The `.gitignore` file already excludes this directory:
```gitignore
e2e-tests/java/apps/*.apk
e2e-tests/java/apps/*.app
```

## Troubleshooting

### "App not found" error
```
Error: App file not found: /path/to/apps/petspot-android.apk
```
**Solution**: Build the app using commands above.

### "App installation failed" error
```
Error: Could not install app on simulator/emulator
```
**Solution**: 
1. Verify simulator/emulator is running
2. Rebuild the app with clean build:
   ```bash
   ./gradlew clean :composeApp:assembleDebug
   ```

### iOS app not launching
```
Error: iOS app fails to launch on simulator
```
**Solution**:
1. Verify iOS simulator is running (iPhone 15, iOS 17.0+)
2. Check app is built for simulator (not device):
   ```bash
   file e2e-tests/java/apps/petspot-ios.app/iosApp
   # Should show: Mach-O 64-bit executable arm64
   ```

## Automation

### Pre-Test Hook (Recommended)

Add to Maven `pom.xml` or shell script to auto-copy latest builds:

```xml
<!-- pom.xml: maven-antrun-plugin -->
<plugin>
  <artifactId>maven-antrun-plugin</artifactId>
  <executions>
    <execution>
      <phase>test-compile</phase>
      <goals>
        <goal>run</goal>
      </goals>
      <configuration>
        <tasks>
          <!-- Copy latest Android APK -->
          <copy file="${project.basedir}/../../composeApp/build/outputs/apk/debug/composeApp-debug.apk"
                tofile="${project.basedir}/apps/petspot-android.apk"
                failonerror="false"/>
        </tasks>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Or use shell script:

```bash
#!/bin/bash
# e2e-tests/java/scripts/copy-apps.sh

echo "Copying latest mobile app builds..."

# Android
if [ -f "../../composeApp/build/outputs/apk/debug/composeApp-debug.apk" ]; then
  cp ../../composeApp/build/outputs/apk/debug/composeApp-debug.apk apps/petspot-android.apk
  echo "✅ Android APK copied"
else
  echo "⚠️ Android APK not found - build it first"
fi

# iOS
if [ -d "../../iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app" ]; then
  cp -r ../../iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app apps/petspot-ios.app
  echo "✅ iOS app copied"
else
  echo "⚠️ iOS app not found - build it first"
fi

ls -lh apps/
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Build Android APK for E2E tests
  run: |
    ./gradlew :composeApp:assembleDebug
    mkdir -p e2e-tests/java/apps
    cp composeApp/build/outputs/apk/debug/composeApp-debug.apk \
       e2e-tests/java/apps/petspot-android.apk

- name: Build iOS App for E2E tests
  run: |
    cd iosApp
    xcodebuild -scheme iosApp -sdk iphonesimulator -configuration Debug \
      -derivedDataPath build
    mkdir -p ../e2e-tests/java/apps
    cp -r build/Build/Products/Debug-iphonesimulator/iosApp.app \
      ../e2e-tests/java/apps/petspot-ios.app

- name: Run Appium tests
  run: |
    cd e2e-tests/java
    mvn test -Dcucumber.filter.tags="@mobile"
```

## References

- [Appium Documentation](http://appium.io/docs/en/about-appium/intro/)
- [Spec 016: Java E2E Migration](../../specs/016-e2e-java-migration/spec.md)
- [Spec 025: Java E2E Coverage](../../specs/025-java-e2e-coverage/spec.md)
- [AppiumDriverManager.java](../src/test/java/com/intive/aifirst/petspot/e2e/utils/AppiumDriverManager.java)






