# Quickstart Guide: iOS MVVM-C Architecture

**Feature**: iOS MVVM-C Architecture Setup  
**Branch**: `001-ios-mvvmc-architecture`  
**Date**: 2025-11-17

## Overview

This guide walks through implementing MVVM-C architecture for the iOS app, replacing the current pure SwiftUI setup with UIKit coordinators + SwiftUI views.

**Expected Outcome**: App launches with UIKit lifecycle, displays splash screen (red circle on black background), then navigates to existing ContentView via coordinator pattern.

**Time Estimate**: 2-3 hours for experienced iOS developer

## Prerequisites

- Xcode 15+
- iOS 18.2+ deployment target
- Familiarity with Swift, UIKit, and SwiftUI
- Understanding of coordinator pattern (see [research.md](./research.md))

## Implementation Steps

### Step 1: Remove SwiftUI App Entry Point

**File**: `iosApp/iosApp/iOSApp.swift`

**Current Code**:
```swift
import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

**Change**: Remove `@main` attribute (keep the struct for now, or delete entirely)

**New Code**:
```swift
import SwiftUI

// @main removed - now using UIKit lifecycle
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

**Why**: `@main` designates the app entry point. Removing it allows us to use UIKit's AppDelegate instead.

---

### Step 2: Create AppDelegate

**New File**: `iosApp/iosApp/AppDelegate.swift`

```swift
import UIKit

/// Application-level lifecycle manager.
/// Handles app launch and global setup.
@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Future: Initialize global services (analytics, crash reporting, etc.)
        return true
    }
    
    // MARK: UISceneSession Lifecycle
    
    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(
            name: "Default Configuration",
            sessionRole: connectingSceneSession.role
        )
    }
    
    func application(
        _ application: UIApplication,
        didDiscardSceneSessions sceneSessions: Set<UISceneSession>
    ) {
        // Called when the user discards a scene session.
        // Release any resources that were specific to the discarded scenes.
    }
}
```

**Add to Xcode Project**:
1. In Xcode, right-click `iosApp` group → New File → Swift File
2. Name it `AppDelegate.swift`
3. Ensure target membership: `iosApp` (checked)

---

### Step 3: Create SceneDelegate

**New File**: `iosApp/iosApp/SceneDelegate.swift`

```swift
import UIKit

/// Scene lifecycle manager.
/// Configures window, navigation controller, and root coordinator.
class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    private var appCoordinator: AppCoordinator?
    
    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        // Configure navigation bar appearance
        configureNavigationBarAppearance()
        
        // Create window
        let window = UIWindow(windowScene: windowScene)
        self.window = window
        
        // Create navigation controller
        let navigationController = UINavigationController()
        
        // Create splash screen as initial root view controller
        let splashView = SplashScreenView()
        let splashHostingController = UIHostingController(rootView: splashView)
        navigationController.setViewControllers([splashHostingController], animated: false)
        
        // Set window root and make visible
        window.rootViewController = navigationController
        window.makeKeyAndVisible()
        
        // Initialize and start app coordinator
        let coordinator = AppCoordinator()
        coordinator.navigationController = navigationController
        self.appCoordinator = coordinator
        
        Task {
            await coordinator.start(animated: true)
        }
    }
    
    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // Release any resources associated with this scene.
    }
    
    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state.
    }
    
    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
    }
    
    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called as the scene transitions from the background to the foreground.
    }
    
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
    }
    
    // MARK: - Navigation Bar Configuration
    
    /// Configures transparent green semi-transparent navigation bar for all appearances.
    private func configureNavigationBarAppearance() {
        let appearance = UINavigationBarAppearance()
        appearance.configureWithTransparentBackground()
        appearance.backgroundColor = UIColor.green.withAlphaComponent(0.5)
        
        // Apply to all appearance states
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
}
```

**Add to Xcode Project**: Same process as AppDelegate.swift

---

### Step 4: Update Info.plist

**File**: `iosApp/iosApp/Info.plist`

**Add Scene Configuration** (if not present):

```xml
<key>UIApplicationSceneManifest</key>
<dict>
    <key>UIApplicationSupportsMultipleScenes</key>
    <false/>
    <key>UISceneConfigurations</key>
    <dict>
        <key>UIWindowSceneSessionRoleApplication</key>
        <array>
            <dict>
                <key>UISceneConfigurationName</key>
                <string>Default Configuration</string>
                <key>UISceneDelegateClassName</key>
                <string>$(PRODUCT_MODULE_NAME).SceneDelegate</string>
            </dict>
        </array>
    </dict>
</dict>
```

**Remove Storyboard References** (if present):
- Delete `UISceneStoryboardFile` key (or `Main storyboard file base name`)
- We're using programmatic UI, not storyboards

**Verify**:
- `UIApplicationSceneManifest` exists
- `UISceneDelegateClassName` points to SceneDelegate
- No storyboard references

---

### Step 5: Create CoordinatorInterface Protocol

**New File**: `iosApp/iosApp/Coordinators/CoordinatorInterface.swift`

```swift
import UIKit

/// Protocol defining the contract for all coordinators.
/// Coordinators manage navigation flows and view controller presentation.
protocol CoordinatorInterface: AnyObject {
    /// Navigation controller used for presenting view controllers.
    /// Stored as weak reference to prevent retain cycles.
    var navigationController: UINavigationController? { get set }
    
    /// Starts the coordinator's navigation flow.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async
}

extension CoordinatorInterface {
    /// Convenience method to start coordinator with animation enabled.
    func start() async {
        await start(animated: true)
    }
}
```

**Create Coordinators Folder**:
1. In Xcode, right-click `iosApp` group → New Group
2. Name it `Coordinators`
3. Add `CoordinatorInterface.swift` to this group

---

### Step 6: Create AppCoordinator

**New File**: `iosApp/iosApp/Coordinators/AppCoordinator.swift`

```swift
import UIKit

/// Root application coordinator.
/// Manages app-level navigation and initializes sub-coordinators.
class AppCoordinator: CoordinatorInterface {
    
    weak var navigationController: UINavigationController?
    
    // Sub-coordinators (lazy initialization)
    private lazy var listScreenCoordinator: ListScreenCoordinator = {
        let coordinator = ListScreenCoordinator()
        coordinator.navigationController = self.navigationController
        return coordinator
    }()
    
    /// Starts the app coordinator by launching the list screen flow.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        await listScreenCoordinator.start(animated: animated)
    }
}
```

**Add to Coordinators Group**

---

### Step 7: Create ListScreenCoordinator

**New File**: `iosApp/iosApp/Coordinators/ListScreenCoordinator.swift`

```swift
import UIKit
import SwiftUI

/// Coordinator for the main list screen.
/// Manages presentation of the list view using existing ContentView.
class ListScreenCoordinator: CoordinatorInterface {
    
    weak var navigationController: UINavigationController?
    
    /// Starts the list screen by replacing navigation root with ContentView.
    /// - Parameter animated: Whether to animate the transition
    func start(animated: Bool) async {
        guard let navigationController = navigationController else {
            assertionFailure("NavigationController is nil in ListScreenCoordinator")
            return
        }
        
        // Wrap existing ContentView in UIHostingController
        let contentView = ContentView()
        let hostingController = UIHostingController(rootView: contentView)
        
        // Replace root view controller
        navigationController.setViewControllers([hostingController], animated: animated)
    }
}
```

**Add to Coordinators Group**

---

### Step 8: Create SplashScreenView

**New File**: `iosApp/iosApp/Views/SplashScreenView.swift`

```swift
import SwiftUI

/// Initial splash screen displayed on app launch.
/// Shows a 100px red circle on black background.
struct SplashScreenView: View {
    var body: some View {
        ZStack {
            Color.black
                .ignoresSafeArea()
            
            Circle()
                .fill(Color.red)
                .frame(width: 100, height: 100)
        }
    }
}

#Preview {
    SplashScreenView()
}
```

**Create Views Folder**:
1. In Xcode, right-click `iosApp` group → New Group
2. Name it `Views`
3. Add `SplashScreenView.swift` to this group
4. Move existing `ContentView.swift` to this group

---

### Step 9: Verify Xcode Project Structure

**Final Structure**:
```
iosApp/
├── iosApp/
│   ├── AppDelegate.swift           ✅ NEW
│   ├── SceneDelegate.swift         ✅ NEW
│   ├── Coordinators/
│   │   ├── CoordinatorInterface.swift   ✅ NEW
│   │   ├── AppCoordinator.swift         ✅ NEW
│   │   └── ListScreenCoordinator.swift  ✅ NEW
│   ├── Views/
│   │   ├── SplashScreenView.swift   ✅ NEW
│   │   └── ContentView.swift        ✅ MOVED
│   ├── Assets.xcassets/
│   ├── Info.plist                   ✅ MODIFIED
│   └── iOSApp.swift                 ✅ MODIFIED (removed @main)
```

**Check Target Membership**:
1. Select each new file in Xcode
2. Open File Inspector (right panel)
3. Verify `iosApp` target is checked

---

### Step 10: Build and Run

**Build Command**:
```bash
cd iosApp
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' clean build
```

**Or in Xcode**: `Cmd+B`

**Expected Build Output**: ✅ Build Succeeded

**Run**: `Cmd+R` in Xcode or select iPhone simulator

**Expected Behavior**:
1. App launches with UIKit lifecycle (AppDelegate → SceneDelegate)
2. Splash screen appears: **100px red circle on black background**
3. After ~0.5 seconds (AppCoordinator.start() completes), navigation transitions to ContentView
4. ContentView displays: "Click me!" button and SwiftUI content
5. Navigation bar appears: **transparent with green semi-transparent background**

**Verify in Debug Console**:
- No memory warnings
- No retain cycle warnings (use Instruments if needed)

---

## Testing

### Manual Testing

1. **Launch Test**: App displays splash screen → transitions to ContentView
2. **Lifecycle Test**: Background app (Home button) → Foreground app (tap app icon) → No crashes
3. **Memory Test**: Run Instruments → Leaks → Navigate screens → No leaks detected
4. **Navigation Bar Test**: Verify green semi-transparent bar across all screens

### E2E Test (Future)

**Test File**: `e2e-tests/mobile/specs/001-ios-mvvmc-architecture.spec.ts`

```typescript
describe('iOS MVVM-C Architecture', () => {
  it('should display splash screen then navigate to list', async () => {
    // Launch app
    await driver.execute('mobile: launchApp', { bundleId: 'com.intive.aifirst.petspot.PetSpot' });
    
    // Verify splash screen (will need accessibilityIdentifier)
    const splashScreen = await driver.$('~splash.screen');
    await expect(splashScreen).toBeDisplayed();
    
    // Wait for transition to list screen
    await driver.pause(1000);
    
    // Verify list screen (ContentView)
    const listScreen = await driver.$('~list.screen');
    await expect(listScreen).toBeDisplayed();
  });
});
```

**Note**: Test identifiers (`accessibilityIdentifier`) will be added in future iteration.

---

## Troubleshooting

### Issue: "Multiple commands produce Info.plist"

**Solution**: 
1. Open project.pbxproj in text editor
2. Remove duplicate Info.plist references in "Copy Bundle Resources" build phase
3. Clean build folder: `Cmd+Shift+K`

### Issue: "Unrecognized selector sent to instance" for SceneDelegate

**Solution**: Verify `UISceneDelegateClassName` in Info.plist includes module name:
```xml
<string>$(PRODUCT_MODULE_NAME).SceneDelegate</string>
```

### Issue: Black screen on launch

**Solution**: 
- Check that `window.makeKeyAndVisible()` is called in SceneDelegate
- Verify navigationController has at least one view controller
- Check console for errors

### Issue: Navigation bar not green

**Solution**:
- Ensure `configureNavigationBarAppearance()` is called before creating navigationController
- Verify `UINavigationBar.appearance()` applies to all three appearance states
- Check alpha value: `withAlphaComponent(0.5)` should be visible

### Issue: Retain cycles / memory leaks

**Solution**:
- Verify `weak var navigationController` in all coordinators
- Use `[weak self]` in closures
- Run Instruments → Leaks to identify cycle
- Check that appCoordinator is stored in SceneDelegate (not locally)

---

## Next Steps

After completing this implementation:

1. **Add Test Identifiers**: Add `.accessibilityIdentifier()` to SplashScreenView and ContentView for E2E testing
2. **Integrate ViewModels**: Create ViewModels for screens with business logic (future feature)
3. **Add More Coordinators**: Extend pattern for additional navigation flows (e.g., SettingsCoordinator, ProfileCoordinator)
4. **Integrate Koin DI**: When domain logic is added, use Koin for dependency injection of coordinators and ViewModels
5. **Write Unit Tests**: Test coordinator logic independently (future feature)

---

## References

- [Specification](./spec.md)
- [Research Findings](./research.md)
- [Implementation Plan](./plan.md)
- [Apple Docs: UIWindowSceneDelegate](https://developer.apple.com/documentation/uikit/uiwindowscenedelegate)
- [Apple Docs: UIHostingController](https://developer.apple.com/documentation/swiftui/uihostingcontroller)

---

**Status**: Ready for Implementation ✅

**Questions?** Review research.md or consult iOS team lead.

