# Feature Specification: iOS MVVM-C Architecture Setup

**Feature Branch**: `001-ios-mvvmc-architecture`  
**Created**: 2025-11-17  
**Status**: Draft  
**Input**: User description: "Establish iOS project architecture using MVVM-C pattern with UIKit coordinators, SwiftUI views via UIHostingController, and ObservableObject ViewModels. Replace current pure SwiftUI setup with hybrid UIKit/SwiftUI architecture including AppDelegate, SceneDelegate (no storyboards), coordinator protocol, and navigation infrastructure."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Core Navigation Infrastructure (Priority: P1)

As a developer, I need the fundamental navigation structure established so that the app can display screens and handle navigation flows.

**Why this priority**: This is the foundation of the entire architecture. Without navigation infrastructure, no other feature can function. The app must be able to launch, show a splash screen, and navigate to the main content.

**Independent Test**: Can be fully tested by launching the app, observing the splash screen appear with the specified red circle on black background, and verifying the app can transition to the list screen. This delivers a working navigation skeleton that proves the coordinator pattern functions correctly.

**Acceptance Scenarios**:

1. **Given** the app is launched, **When** the system initializes, **Then** AppDelegate and SceneDelegate are properly configured and the window is displayed
2. **Given** SceneDelegate completes initialization, **When** the scene connects, **Then** a transparent green semi-transparent navigation bar is visible across all navigation appearances
3. **Given** the window is configured, **When** the initial view controller is set, **Then** SplashScreenView displays a 100px red circle on black background
4. **Given** AppCoordinator is initialized, **When** start() is called, **Then** ListScreenCoordinator is started and its view becomes the navigation controller's root

---

### User Story 2 - Coordinator Protocol & Hierarchy (Priority: P2)

As a developer, I need a standardized coordinator interface so that all coordinators follow consistent patterns for navigation management.

**Why this priority**: This establishes the contract that all coordinators must follow, enabling predictable navigation patterns and proper memory management. Required before additional coordinators can be added.

**Independent Test**: Can be tested by verifying AppCoordinator and ListScreenCoordinator both conform to CoordinatorInterface, properly manage their weak navigationController references, and can start independently. Delivers a reusable pattern for future coordinators.

**Acceptance Scenarios**:

1. **Given** any coordinator is created, **When** it conforms to CoordinatorInterface, **Then** it provides start(animated: Bool) async method and weak navigationController property
2. **Given** AppCoordinator is initialized with a UINavigationController, **When** navigationController is set, **Then** it's stored as a weak reference to prevent retain cycles
3. **Given** AppCoordinator manages sub-coordinators, **When** a sub-coordinator is accessed, **Then** it's lazily initialized (except for tab bar coordinators which maintain children)
4. **Given** ListScreenCoordinator is started, **When** it presents its view, **Then** it replaces the navigation controller's root view controller with a UIHostingController containing the existing ContentView

---

### User Story 3 - Scene Lifecycle Management (Priority: P3)

As a developer, I need proper scene lifecycle handling so that the app correctly manages app state transitions and window configuration.

**Why this priority**: This ensures proper iOS 13+ scene-based lifecycle management and enables features like multi-window support in the future. While important for production quality, basic navigation works without perfect lifecycle handling.

**Independent Test**: Can be tested by observing app behavior during background/foreground transitions, scene disconnection/reconnection, and verifying no memory leaks or state corruption occurs. Delivers production-ready scene management.

**Acceptance Scenarios**:

1. **Given** a new scene is connecting, **When** scene(_:willConnectTo:options:) is called, **Then** the window, navigation controller, and AppCoordinator are properly initialized
2. **Given** the scene is backgrounded, **When** sceneDidEnterBackground(_:) is called, **Then** the app state is preserved appropriately
3. **Given** the scene is foregrounded, **When** sceneWillEnterForeground(_:) is called, **Then** the app restores any necessary state
4. **Given** the scene disconnects, **When** sceneDidDisconnect(_:) is called, **Then** resources are properly cleaned up

---

### Edge Cases

- What happens when a coordinator is started before its navigationController is set?
- How does the system handle memory management if a parent coordinator is deallocated while sub-coordinators are active?
- What happens if a sub-coordinator attempts to modify navigation state while another coordinator's transition is in progress?
- How does the app handle scene restoration after being terminated by the system?
- What happens if start(animated:) is called multiple times on the same coordinator?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST remove @main attribute from existing SwiftUI App entry point and configure UIKit-based app lifecycle using AppDelegate
- **FR-002**: System MUST create SceneDelegate to manage scene lifecycle and window configuration without using storyboards
- **FR-003**: System MUST define CoordinatorInterface protocol with start(animated: Bool) async method and weak navigationController property (getter and setter)
- **FR-004**: System MUST implement AppCoordinator conforming to CoordinatorInterface and managing sub-coordinators as lazy var properties
- **FR-005**: System MUST initialize AppCoordinator in SceneDelegate with the scene's window and a UINavigationController instance
- **FR-006**: System MUST configure navigation bar appearance in SceneDelegate with transparent, green semi-transparent styling for all appearance proxies
- **FR-007**: System MUST set SplashScreenView (UIHostingController wrapping SwiftUI view) as the initial rootViewController of the navigation controller
- **FR-008**: SplashScreenView MUST display a 100px red circle on a black background
- **FR-009**: AppCoordinator.start() MUST initialize and start ListScreenCoordinator with the AppCoordinator's navigationController
- **FR-010**: ListScreenCoordinator.start() MUST replace the navigationController's rootViewController with UIHostingController wrapping the existing ContentView
- **FR-011**: All coordinators (except tab bar coordinators) MUST store sub-coordinators as lazy var without maintaining a children array
- **FR-012**: All coordinator implementations MUST store navigationController as a weak reference to prevent retain cycles

### Key Entities *(include if feature involves data)*

- **CoordinatorInterface**: Protocol defining navigation coordinator contract with async start method and weak navigation controller reference
- **AppCoordinator**: Root coordinator managing application-level navigation flow and initializing sub-coordinators
- **ListScreenCoordinator**: Sub-coordinator managing the main list screen navigation and view presentation
- **SplashScreenView**: SwiftUI view displaying initial loading screen with red circle on black background
- **ContentView**: Existing SwiftUI view to be displayed as the main list screen content

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: App launches successfully and displays splash screen with 100px red circle on black background within 1 second of launch
- **SC-002**: Navigation bar appears with transparent green semi-transparent styling immediately upon display
- **SC-003**: Transition from splash screen to list screen (ContentView) completes within 2 seconds of coordinator start
- **SC-004**: App supports standard iOS scene lifecycle events (background, foreground, disconnect) without crashes or memory leaks
- **SC-005**: All coordinators properly manage memory with no retain cycles detectable in Instruments
- **SC-006**: Navigation controller hierarchy can be inspected to verify proper UIHostingController wrapping of SwiftUI views
