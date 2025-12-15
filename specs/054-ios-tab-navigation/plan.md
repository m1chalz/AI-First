# Implementation Plan: iOS Tab Navigation

**Branch**: `054-ios-tab-navigation` | **Date**: 2025-12-15 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/054-ios-tab-navigation/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement tab-based navigation for iOS application following MVVM-C architecture. Creates UITabBarController with 5 tabs (Home, Lost Pet, Found Pet, Contact Us, Account), where each tab maintains independent navigation stack via UIKit coordinators. Lost Pet tab uses existing AnnouncementListCoordinator; other tabs display placeholder "Coming soon" screens until features are implemented. Tab state not persisted across app restarts.

## Technical Context

**Language/Version**: Swift 5.0, iOS 18.0+  
**Primary Dependencies**: UIKit (coordinators, UITabBarController), SwiftUI (views via UIHostingController)  
**Storage**: N/A (no persistence required for tab navigation)  
**Testing**: XCTest (unit tests for coordinators and tab management logic), target 80% coverage  
**Target Platform**: iOS 18.0+ (iPhone & iPad)  
**Project Type**: Mobile iOS - coordinator-based navigation  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: Each tab must maintain independent back stack (separate UINavigationController per tab). Tab bar hidden on detail screens (`hidesBottomBarWhenPushed`). Always launch to Home tab on app restart.  
**Scale/Scope**: 5 tabs, 2 new coordinators (TabCoordinator, PlaceholderCoordinator), 1 updated coordinator (AppCoordinator)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature affecting `/iosApp` module. Android, Web, and Backend checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (Android implementation in separate feature spec)
  - iOS: Full stack implementation in `/iosApp` - coordinators, views, view models
  - Web: N/A (Web implementation in separate feature spec)
  - Backend: N/A (no backend changes required)
  - NO shared compiled code between platforms
  - Violation justification: N/A - fully compliant

- [ ] **Android MVI Architecture**: N/A - Android implementation not in scope for this feature

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - TabCoordinator manages UITabBarController and creates child coordinators
  - Root coordinators (PlaceholderCoordinator, AnnouncementListCoordinator) create own UINavigationController in init
  - Sub-coordinators (PetDetailsCoordinator) receive navigationController from parent (existing pattern preserved)
  - Each tab has dedicated UINavigationController for independent back stack (created by root coordinator)
  - PlaceholderCoordinator manages placeholder screens with PlaceholderViewModel (ObservableObject with title/message)
  - AppCoordinator updated to start TabCoordinator instead of direct AnnouncementListCoordinator
  - Coordinators create UIHostingController instances for SwiftUI views
  - ViewModels conform to ObservableObject with @Published properties
  - Navigation logic isolated in coordinators (not in views)
  - Violation justification: N/A - fully compliant with MVVM-C

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: No new repositories or domain logic required for navigation infrastructure
  - Existing repositories accessed via ServiceContainer (DI)
  - Violation justification: N/A - navigation infrastructure only, no domain logic

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (Android implementation in separate feature spec)
  - iOS: Manual DI via ServiceContainer already in place - no new dependencies for tab navigation
  - Web: N/A (Web implementation in separate feature spec)
  - Backend: N/A (no backend changes required)
  - Violation justification: N/A - uses existing DI setup

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (Android implementation in separate feature spec)
  - iOS: Unit tests for TabCoordinator (tab creation, child coordinator management) in `/iosApp/iosAppTests/Coordinators/`
  - Web: N/A (Web implementation in separate feature spec)
  - Backend: N/A (no backend changes required)
  - Coverage target: 80% line + branch coverage for TabCoordinator
  - Violation justification: N/A - tests planned

- [ ] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A (Web implementation in separate feature spec)
  - Mobile: Deferred to future iteration - E2E tests will be added after all tab features implemented
  - E2E test complexity: Requires all 5 tabs to be fully implemented (only Lost Pet currently exists)
  - Placeholder screens not valuable to E2E test (static "Coming soon" message)
  - Violation justification: E2E tests deferred - tab navigation infrastructure only, placeholder content not testable in meaningful way. Will add E2E tests when Home, Found Pet, Contact Us, Account features implemented.

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) for coordinator `start()` methods with `@MainActor`
  - No async operations in navigation logic (synchronous tab switching)
  - Violation justification: N/A - fully compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` on tab bar items (e.g., `tabs.home`, `tabs.lostPet`, `tabs.foundPet`, `tabs.contactUs`, `tabs.account`)
  - Placeholder screens include identifiers for future testing
  - Violation justification: N/A - identifiers planned

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`) for TabCoordinator, PlaceholderCoordinator public methods
  - Documentation concise and high-level (WHAT/WHY, not HOW)
  - Skip documentation for self-explanatory coordinator methods
  - Violation justification: N/A - documentation planned

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests follow Given-When-Then pattern with descriptive camelCase_with_underscores names
  - Example: `func testTabCoordinator_whenStarted_shouldCreate5Tabs() async { ... }`
  - Comments mark test phases in complex tests
  - Violation justification: N/A - convention followed

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - `/server` not affected
- [ ] **Backend Code Quality**: N/A - `/server` not affected
- [ ] **Backend Dependency Management**: N/A - `/server` not affected

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: N/A - `/webApp` not affected (Web implementation in separate feature spec)
- [ ] **Web Code Quality**: N/A - `/webApp` not affected
- [ ] **Web Dependency Management**: N/A - `/webApp` not affected
- [ ] **Web Business Logic Extraction**: N/A - `/webApp` not affected
- [ ] **Web TDD Workflow**: N/A - `/webApp` not affected
- [ ] **Web Testing Strategy**: N/A - `/webApp` not affected
- [ ] **Backend Directory Structure**: N/A - `/server` not affected
- [ ] **Backend TDD Workflow**: N/A - `/server` not affected
- [ ] **Backend Testing Strategy**: N/A - `/server` not affected

## Project Structure

### Documentation (this feature)

```text
specs/054-ios-tab-navigation/
├── plan.md              # This file (/speckit.plan command output)
├── spec.md              # Feature specification (already exists)
└── checklists/
    └── requirements.md  # Specification validation (already exists)
```

### Source Code (iOS implementation)

```text
/iosApp/iosApp/
├── Coordinators/
│   ├── AppCoordinator.swift          # UPDATED: No longer accepts navigationController, creates TabCoordinator
│   ├── CoordinatorInterface.swift    # EXISTING: Protocol (no changes)
│   ├── TabCoordinator.swift          # NEW: Manages UITabBarController, creates 5 child coordinators
│   └── PlaceholderCoordinator.swift  # NEW: Root coordinator creating own UINavigationController
│
├── Features/
│   ├── AnnouncementList/
│   │   └── Coordinators/
│   │       └── AnnouncementListCoordinator.swift  # UPDATED: Modified to create own UINavigationController in init (root coordinator pattern)
│   │
│   └── Placeholder/
│       └── Views/
│           ├── PlaceholderView.swift        # NEW: SwiftUI view for "Coming soon" message
│           └── PlaceholderViewModel.swift   # NEW: ViewModel with title/message (ObservableObject)
│
├── Resources/
│   ├── en.lproj/
│   │   └── Localizable.strings  # UPDATED: Add tab navigation strings + placeholder strings
│   └── pl.lproj/
│       └── Localizable.strings  # UPDATED: Add tab navigation strings + placeholder strings (Polish)
│
├── Generated/
│   └── Strings.swift  # REGENERATED: SwiftGen output with new localization keys
│
└── SceneDelegate.swift  # UPDATED: Simplified - no splash screen, directly sets UITabBarController as window root

/iosApp/iosAppTests/
├── Coordinators/
│   └── TabCoordinatorTests.swift         # NEW: Unit tests for TabCoordinator (80% coverage target)
│
└── Features/
    └── Placeholder/
        └── Views/
            └── PlaceholderViewModelTests.swift  # NEW: Unit tests for PlaceholderViewModel
```

**Structure Decision**: iOS mobile app with coordinator-based navigation. TabCoordinator created in `/iosApp/iosApp/Coordinators/` (root-level navigation infrastructure, not feature-specific). PlaceholderCoordinator also root-level (reusable for any unimplemented feature). Tests in `/iosApp/iosAppTests/Coordinators/` matching source structure.

**Simplified Launch Flow**: No splash screen - app launches directly to tab bar. AppCoordinator.init() → TabCoordinator.init() → creates all root coordinators → all coordinators create UINavigationController in init → complete tab bar structure assembled synchronously. AppCoordinator.start() asynchronously calls coordinator.start() on children to populate views. Result: Full tab bar with 5 tabs + navigation structure visible instantly, content populates in background.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| E2E tests deferred | Only Lost Pet tab implemented; other tabs show placeholder "Coming soon" screens. E2E tests not valuable until tab content features exist. | Creating E2E tests for static placeholders provides no meaningful validation. Will add E2E coverage when Home, Found Pet, Contact Us, Account features implemented. Navigation infrastructure tested via unit tests. |

## Implementation Steps

### Phase 1: Core Navigation Infrastructure

1. **Create PlaceholderViewModel** (`/iosApp/iosApp/Features/Placeholder/Views/PlaceholderViewModel.swift`)
   - ObservableObject with `@Published var title: String`
   - Exposes localized "Coming soon" message
   - Simple, stateless presentation logic

2. **Create PlaceholderView** (`/iosApp/iosApp/Features/Placeholder/Views/PlaceholderView.swift`)
   - SwiftUI view observing PlaceholderViewModel
   - Displays icon (SF Symbol clock.fill), title, and "Coming soon" message
   - Test identifiers: `placeholder.comingSoon.icon`, `placeholder.comingSoon.message`

3. **Create PlaceholderCoordinator** (`/iosApp/iosApp/Coordinators/PlaceholderCoordinator.swift`)
   - **Root coordinator pattern**: Creates own `UINavigationController` in `init(title:)`
   - Sets `navigationController` property to own nav controller (CoordinatorInterface conformance)
   - Creates PlaceholderViewModel with title
   - Creates PlaceholderView with ViewModel
   - Wraps in UIHostingController and sets as navigation root
   - Follows MVVM-C pattern (Coordinator → ViewModel → View)

4. **Create TabCoordinator** (`/iosApp/iosApp/Coordinators/TabCoordinator.swift`)
   - **`init()`**: Creates complete UITabBarController structure (fully synchronous, ready immediately)
     - Creates UITabBarController
     - Creates root coordinators for each tab (coordinators create own UINavigationController in init):
       - Home → PlaceholderCoordinator(title: L10n.Tabs.home)
       - Lost Pet → AnnouncementListCoordinator()
       - Found Pet → PlaceholderCoordinator(title: L10n.Tabs.foundPet)
       - Contact Us → PlaceholderCoordinator(title: L10n.Tabs.contactUs)
       - Account → PlaceholderCoordinator(title: L10n.Tabs.account)
     - Retrieves UINavigationController from each coordinator via `coordinator.navigationController!` (available immediately after coordinator init)
     - Configures tab bar items on navigation controllers (SF Symbols icons, localized titles)
     - Sets navigation controllers as `tabBarController.viewControllers`
     - Maintains strong references to child coordinators in `childCoordinators` array
     - Configures tab bar appearance (colors: `#FAFAFA` background, `#808080` inactive, `#FF6B35` active)
     - Exposes UITabBarController via `getTabBarController()` method
   - **`start(animated:)`**: Starts all child coordinators to populate content (asynchronous)
     - Calls `await coordinator.start(animated:)` on each child coordinator
     - Child coordinators populate their navigation controllers with content
   - Test identifiers: `tabs.home`, `tabs.lostPet`, `tabs.foundPet`, `tabs.contactUs`, `tabs.account`

5. **Update AnnouncementListCoordinator** (`/iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift`)
   - **Change to root coordinator pattern**: Update `init()` to create own `UINavigationController` (no longer accepts navigationController parameter)
   - Set `navigationController` property to own nav controller (CoordinatorInterface conformance - TabCoordinator reads this property)
   - Keep all existing logic (start flow, create ViewModels, child coordinators)
   - Sub-coordinators (PetDetailsCoordinator, ReportMissingPetCoordinator) still receive navigationController from this coordinator (existing pattern preserved)

6. **Update AppCoordinator** (`/iosApp/iosApp/Coordinators/AppCoordinator.swift`)
   - Remove `init(navigationController:)` parameter - no longer needed (tab coordinators create own nav controllers)
   - **`init()`**: Creates TabCoordinator (fully synchronous, complete structure ready)
     - `tabCoordinator = TabCoordinator()` creates entire tab bar structure with all navigation controllers
     - Store TabCoordinator reference
     - Maintain as child coordinator
   - **`start(animated:)`**: Starts TabCoordinator to populate content (asynchronous)
     - Calls `await tabCoordinator.start(animated:)`
     - Child coordinators fill their navigation controllers with views
   - Add `getTabBarController()` method to expose TabCoordinator's UITabBarController for SceneDelegate
   - Note: `navigationController` property kept as `nil` for CoordinatorInterface conformance (not used in tab navigation)

7. **Update SceneDelegate** (`/iosApp/iosApp/SceneDelegate.swift`)
   - Remove splash screen setup (no longer needed)
   - Create window
   - Create AppCoordinator with `init()` - creates complete UITabBarController structure synchronously (all 5 tabs with navigation controllers ready)
   - Set `window.rootViewController` to AppCoordinator's UITabBarController (via `getTabBarController()`)
   - Make window visible (`window.makeKeyAndVisible()`)
   - Start AppCoordinator asynchronously in `Task { @MainActor in await coordinator.start() }`
   - Flow: Complete tab bar with 5 tabs visible immediately, coordinators populate content asynchronously
   - Result: No black screen, full tab bar navigation structure ready instantly, views load in background

### Phase 2: Localization

8. **Add Localization Strings**
   - Update `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`:
     - `tabs.home` = "Home"
     - `tabs.lostPet` = "Lost Pet"
     - `tabs.foundPet` = "Found Pet"
     - `tabs.contactUs` = "Contact Us"
     - `tabs.account` = "Account"
     - `placeholder.title` = "Coming soon"
     - `placeholder.message` = "This feature is under development"
   - Update `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (Polish equivalents)
   - Run SwiftGen: `cd iosApp && swiftgen` to regenerate `Generated/Strings.swift`

### Phase 3: Detail Screen Tab Bar Hiding

9. **Update PetDetailsCoordinator** (`/iosApp/iosApp/Features/PetDetails/Coordinators/PetDetailsCoordinator.swift`)
   - Add `hostingController.hidesBottomBarWhenPushed = true` before push
   - Ensures tab bar hidden on detail screens (better UX, more screen space)

### Phase 4: Testing

10. **Create PlaceholderViewModelTests** (`/iosApp/iosAppTests/Features/Placeholder/Views/PlaceholderViewModelTests.swift`)
   - Test: ViewModel initializes with correct title
   - Test: Localized strings exposed correctly
   - Target: 80% line + branch coverage

11. **Create TabCoordinatorTests** (`/iosApp/iosAppTests/Coordinators/TabCoordinatorTests.swift`)
   - Test: Tab coordinator creates 5 child coordinators
   - Test: Each child coordinator creates own UINavigationController
   - Test: Tab bar controller has 5 view controllers (navigation controllers)
   - Test: Child coordinators started successfully
   - Test: Tab bar items have correct titles and icons
   - Test: Accessibility identifiers set correctly
   - Target: 80% line + branch coverage

12. **Manual Testing**
   - Verify app launches directly to tab bar (no splash screen)
   - Verify all 5 tabs appear in tab bar
   - Verify tapping each tab navigates to correct screen
   - Verify Lost Pet tab shows announcement list
   - Verify other tabs show "Coming soon" placeholder
   - Verify tab bar visible on root screens
   - Verify tab bar hidden on detail screens (Pet Details)
   - Verify switching tabs preserves navigation state (back stack)
   - Verify app restart returns to Home tab (first tab selected)

### Phase 5: Documentation

13. **Update Documentation**
   - Add SwiftDoc comments to TabCoordinator public methods
   - Add SwiftDoc comments to PlaceholderCoordinator (root coordinator pattern with own nav controller)
   - Add SwiftDoc comments to PlaceholderViewModel (title purpose)
   - Document root coordinator vs sub-coordinator distinction:
     - Root coordinators (PlaceholderCoordinator, AnnouncementListCoordinator): Create own UINavigationController
     - Sub-coordinators (PetDetailsCoordinator): Receive navigationController from parent
   - Document tab bar appearance configuration
   - Document navigation stack isolation (independent per tab via separate nav controllers)
   - Document PlaceholderView reusability for future unimplemented features

## Dependencies

### Existing Features
- **AnnouncementListCoordinator**: Updated to root coordinator pattern (creates own UINavigationController)
- **PetDetailsCoordinator**: Updated to hide tab bar on detail screens
- **ServiceContainer**: Provides repositories and services to coordinators (no changes)
- **SwiftGen**: Regenerate localization strings after adding tab navigation keys
- **SplashScreenView**: No longer used in tab navigation flow (removed from SceneDelegate)

### Blocked Features
- **Home tab content** (Landing Page - feature 049-landing-page): Requires this tab navigation infrastructure
- **Found Pet tab content**: Future feature (not yet specified)
- **Contact Us tab content**: Future feature (not yet specified)
- **Account tab content**: Future feature (authentication & account management)

## Risk Assessment

### Low Risk
- UITabBarController is standard UIKit component (well-tested by Apple)
- Coordinator pattern already established in codebase
- SwiftUI views wrapped in UIHostingController (proven approach)

### Medium Risk
- **Independent navigation stacks**: Each tab's UINavigationController must not interfere with others. Mitigation: Standard UIKit pattern, well-documented by Apple.
- **Tab bar visibility**: Must be hidden on detail screens (`hidesBottomBarWhenPushed`). Mitigation: Explicitly set flag on all pushed view controllers.

### Deferred Decisions
- **Tab state persistence**: Currently no persistence across app restarts (always launch to Home tab). Future: May add UserDefaults persistence if user feedback indicates need.
- **Deep linking**: Mobile deep linking deferred to future iteration (web will support URL routing first).

## Success Metrics

- [x] App launches directly to tab bar (no splash screen)
- [x] All 5 tabs visible and interactive
- [x] Tapping each tab navigates to correct destination
- [x] Lost Pet tab shows existing announcement list
- [x] Other tabs show placeholder "Coming soon" screens
- [x] Tab bar visible on root screens, hidden on detail screens
- [x] Each tab maintains independent navigation stack
- [x] App restart returns to Home tab (first tab selected, no persistence)
- [x] AppCoordinator no longer depends on external UINavigationController
- [x] 80% unit test coverage for TabCoordinator
- [x] Build succeeds without errors
- [x] No linter violations introduced
