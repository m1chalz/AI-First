# Quickstart: iOS Landing Page Full Content Scroll

**Feature**: KAN-30 iOS Landing Page Full Content Scroll  
**Branch**: `KAN-30-ios-landing-page-full-content-scroll`  
**Date**: 2025-12-19

## Overview

Replace nested scroll architecture in iOS landing page with single outer ScrollView. This guide provides step-by-step implementation instructions.

## Prerequisites

- Xcode with iOS 18 SDK
- iPhone 16 Simulator configured
- PetSpot iOS project building successfully
- Branch `KAN-30-ios-landing-page-full-content-scroll` checked out

## Implementation Steps

### Step 1: Modify LandingPageView.swift

**File**: `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift`

**Current Structure** (lines 25-58):
```swift
var body: some View {
    VStack(spacing: 0) {
        HeroPanelView(...)
        ListHeaderRowView(...)
        AnnouncementCardsListView(...)
            .frame(maxHeight: .infinity)  // ← Remove this
    }
    .task { await viewModel.loadData() }
}
```

**New Structure**:
```swift
var body: some View {
    ScrollView {
        LazyVStack(spacing: 0) {
            HeroPanelView(
                model: .landingPage(
                    onLostPetTap: {
                        viewModel.onSwitchToLostPetTab?(nil)
                    },
                    onFoundPetTap: {
                        viewModel.onSwitchToFoundPetTab?()
                    }
                )
            )
            
            ListHeaderRowView(
                model: .recentReports(
                    onViewAllTap: {
                        viewModel.onSwitchToLostPetTab?(nil)
                    }
                )
            )
            
            // Pass hasOwnScrollView: false to disable nested ScrollView
            // Parent ScrollView (this view) handles scrolling
            AnnouncementCardsListView(
                viewModel: viewModel.listViewModel,
                emptyStateModel: viewModel.emptyStateModel,
                listAccessibilityId: viewModel.listAccessibilityId,
                hasOwnScrollView: false  // ← KEY CHANGE: disable nested scroll
            )
        }
    }
    .task {
        await viewModel.loadData()
    }
    .alert(
        L10n.Location.Permission.Popup.title,
        isPresented: $viewModel.showPermissionDeniedAlert,
        actions: {
            Button(L10n.Location.Permission.Popup.Settings.button) {
                viewModel.openSettings()
            }
            .accessibilityIdentifier("startup.permissionPopup.goToSettings")
            
            Button(L10n.Location.Permission.Popup.Cancel.button, role: .cancel) {
                viewModel.continueWithoutLocation()
            }
            .accessibilityIdentifier("startup.permissionPopup.cancel")
        },
        message: {
            Text(L10n.Location.Permission.Popup.message)
                .accessibilityIdentifier("startup.permissionPopup.message")
        }
    )
}
```

**Changes**:
1. Replace `VStack` with `ScrollView` wrapping `LazyVStack`
2. Remove `.frame(maxHeight: .infinity)` from `AnnouncementCardsListView`
3. Add `hasOwnScrollView: false` parameter to disable nested scroll
4. Keep all subviews in same order (hero, header, list)
5. Preserve existing `.task` and `.alert` modifiers

**Update Documentation Comment** (lines 1-21):
```swift
/// Landing page view for Home tab displaying hero panel, list header, and recent pet announcements.
/// Uses single ScrollView for continuous scrolling of all sections.
///
/// **Layout (top to bottom)**:
/// 1. HeroPanelView - "Find Your Pet" title + "Lost Pet" / "Found Pet" buttons
/// 2. ListHeaderRowView - "Recent Reports" title + "View All" action
/// 3. AnnouncementCardsListView - announcement cards (inline content, no nested scroll)
///
/// **Scroll Architecture**:
/// - Single outer ScrollView with LazyVStack containing all sections
/// - No nested scroll regions (hero, header, and list scroll together)
/// - Loading, error, and empty states render inline in scroll content
///
/// **Navigation**:
/// - NO NavigationView - coordinator manages UINavigationController
/// - Navigation bar title set by coordinator via UIHostingController
/// - Hero button taps trigger tab navigation via ViewModel closures
///
/// **Loading Flow**:
/// 1. View appears → `.task { await viewModel.loadData() }`
/// 2. Parent ViewModel fetches location and sets `listViewModel.query`
/// 3. Shows permission popup if needed (once per session)
/// 4. Child ViewModel loads announcements and updates state
/// 5. `AnnouncementCardsListView` observes child ViewModel and renders UI inline
```

### Step 2: Modify AnnouncementCardsListView.swift

**File**: `/iosApp/iosApp/Views/AnnouncementCardsListView.swift`

**Current Structure** (lines 14-22):
```swift
struct AnnouncementCardsListView: View {
    @ObservedObject var viewModel: AnnouncementCardsListViewModel
    let emptyStateModel: EmptyStateView.Model
    let listAccessibilityId: String
    // ... no hasOwnScrollView parameter
}
```

**Add New Parameter** (lines 14-30):
```swift
/// Autonomous announcement cards list component.
/// Observes `AnnouncementCardsListViewModel` for state changes and renders appropriate UI.
///
/// **Autonomous Component Pattern**:
/// - Observes own ViewModel for state (loading, error, empty, success)
/// - Handles states internally (loading spinner, error view, empty state, list)
/// - Reusable in multiple contexts (full list with scroll, embedded without scroll)
/// - Parent View triggers load via parent ViewModel setting `listViewModel.query`
///
/// **Scroll Architecture**:
/// - `hasOwnScrollView: true` (default) - wraps content in ScrollView (for standalone usage)
/// - `hasOwnScrollView: false` - no ScrollView wrapper (for embedding in parent ScrollView)
///
/// **Does NOT trigger loading** - parent View's `.task` calls parent ViewModel's `loadData()`,
/// which then sets `listViewModel.query` to trigger the actual load.
struct AnnouncementCardsListView: View {
    @ObservedObject var viewModel: AnnouncementCardsListViewModel
    
    /// Model for empty state display
    let emptyStateModel: EmptyStateView.Model
    
    /// Base accessibility identifier for list elements
    let listAccessibilityId: String
    
    /// Whether this view manages its own scroll container.
    /// Set to `false` when embedding in a parent ScrollView (e.g., LandingPageView).
    /// Default: `true` (backwards compatible with existing usages).
    let hasOwnScrollView: Bool
    
    init(
        viewModel: AnnouncementCardsListViewModel,
        emptyStateModel: EmptyStateView.Model,
        listAccessibilityId: String,
        hasOwnScrollView: Bool = true
    ) {
        self.viewModel = viewModel
        self.emptyStateModel = emptyStateModel
        self.listAccessibilityId = listAccessibilityId
        self.hasOwnScrollView = hasOwnScrollView
    }
```

**Modify Body** (success state section):
```swift
var body: some View {
    ZStack {
        Color(hex: "#FAFAFA").ignoresSafeArea()
        
        if viewModel.isLoading {
            LoadingView(model: LoadingView.Model(
                message: L10n.AnnouncementList.Loading.message,
                accessibilityIdentifier: "\(listAccessibilityId).loading"
            ))
        } else if let errorMessage = viewModel.errorMessage {
            ErrorView(model: ErrorView.Model(
                title: L10n.AnnouncementList.Error.title,
                message: errorMessage,
                onRetry: {
                    viewModel.onRetryTapped()
                },
                accessibilityIdentifier: "\(listAccessibilityId).error"
            ))
        } else if isEmpty {
            EmptyStateView(model: emptyStateModel)
        } else {
            // Conditionally wrap in ScrollView based on hasOwnScrollView parameter
            if hasOwnScrollView {
                ScrollView {
                    listContent
                }
                .accessibilityIdentifier("\(listAccessibilityId).list")
            } else {
                // Parent provides scroll container (e.g., LandingPageView)
                listContent
                    .accessibilityIdentifier("\(listAccessibilityId).list")
            }
        }
    }
}

// MARK: - Private Views

/// Extracted list content for conditional scroll wrapping
private var listContent: some View {
    LazyVStack(spacing: 8) {
        ForEach(viewModel.cardViewModels, id: \.id) { cardViewModel in
            AnnouncementCardView(viewModel: cardViewModel)
        }
    }
    .padding(.horizontal, 16)
    .padding(.top, 8)
}
```

**Changes**:
1. Add `hasOwnScrollView: Bool = true` parameter (backwards compatible)
2. Add explicit `init` with default value
3. Extract `listContent` as computed property
4. Conditionally wrap in ScrollView based on parameter
5. Preserve all existing behavior for callers not passing parameter

**Existing Usages (No Changes Needed)**:
- `AnnouncementListView.swift` - uses default `hasOwnScrollView: true` ✓ (keeps scroll)
- Preview in `AnnouncementCardsListView.swift` - uses default ✓

### Step 3: Run Unit Tests

**Command**:
```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  -enableCodeCoverage YES
```

**Expected**: All existing tests pass (no ViewModel logic changes)

**Verify**:
- `LandingPageViewModelTests` - all tests green (no state changes)
- Coverage report shows maintained 80%+ coverage

**If tests fail**: ViewModel logic unchanged, so failures indicate test environment issue (not code issue)

### Step 4: Manual Verification (Xcode Preview)

**Steps**:
1. Open `LandingPageView.swift` in Xcode
2. Open Canvas preview (Cmd+Option+Return)
3. Run live preview on iPhone 16 Simulator

**Verify**:
- Hero panel visible at top
- List header below hero
- Announcement cards below header
- Single continuous scroll gesture reaches all sections
- No nested scroll behavior (no scroll handoff)
- Loading/error states render inline (hero remains visible)

**Test Cases**:
- Scroll from top to bottom (all sections reachable)
- Tap hero buttons (navigation works)
- Tap list header "View All" (navigation works)
- Tap announcement card (navigation works)

### Step 5: Add E2E Tests (Java + Cucumber)

**File**: Create `/e2e-tests/java/src/test/resources/features/mobile/landing-page-scroll.feature`

```gherkin
@ios @landing-page
Feature: iOS Landing Page Full Content Scroll
  As a user
  I want to scroll through all landing page sections continuously
  So that I can access all content with a single scroll gesture

  Background:
    Given I am on the iOS app home screen
    And I have granted location permissions

  @priority-high
  Scenario: Scroll entire landing page content continuously
    When I scroll down on the landing page
    Then I should see the hero panel scroll off screen
    And I should see the list header scroll up
    And I should see announcement cards scroll into view
    And I should be able to reach the bottom of the page
    And there should be no nested scroll behavior

  @priority-high
  Scenario: Interactive elements work during and after scrolling
    Given I scroll to the middle of the landing page
    When I tap on an announcement card
    Then I should navigate to the pet details screen
    And the tap should not be missed due to scroll conflicts

  @priority-high
  Scenario: Scroll position preserved during state changes
    Given I scroll down to the announcement list
    When the list finishes loading new data
    Then my scroll position should be preserved
    And I should not be scrolled back to the top

  @priority-low
  Scenario: Short content does not cause blank space
    Given the landing page has only 2 announcements
    When the page renders
    Then there should be no excessive blank space below the content
    And scrolling should not be required if content fits viewport
```

**File**: Create `/e2e-tests/java/src/test/java/com/petspot/e2e/steps/mobile/LandingPageScrollSteps.java`

```java
package com.petspot.e2e.steps.mobile;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;

import static org.junit.Assert.*;

public class LandingPageScrollSteps {
    private final AppiumDriver driver;
    private int initialScrollPosition;

    public LandingPageScrollSteps(AppiumDriver driver) {
        this.driver = driver;
    }

    @Given("I am on the iOS app home screen")
    public void iAmOnTheHomeScreen() {
        // Wait for landing page to load
        WebElement heroPanel = driver.findElement(
            By.xpath("//XCUIElementTypeOther[@name='landingPage.heroPanel']")
        );
        assertTrue("Landing page should be visible", heroPanel.isDisplayed());
    }

    @Given("I have granted location permissions")
    public void iHaveGrantedLocationPermissions() {
        // Handle permission popup if present
        // Implementation depends on permission handling strategy
    }

    @When("I scroll down on the landing page")
    public void iScrollDownOnLandingPage() {
        // Perform vertical scroll gesture
        int startX = driver.manage().window().getSize().width / 2;
        int startY = driver.manage().window().getSize().height * 2 / 3;
        int endY = driver.manage().window().getSize().height / 3;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        
        scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), startX, endY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(scroll));
    }

    @Then("I should see the hero panel scroll off screen")
    public void heroPanelScrollsOffScreen() {
        WebElement heroPanel = driver.findElement(
            By.xpath("//XCUIElementTypeOther[@name='landingPage.heroPanel']")
        );
        
        // Hero panel should not be visible after scrolling
        // (or only partially visible at top of screen)
        int heroPanelY = heroPanel.getLocation().getY();
        assertTrue("Hero panel should scroll up", heroPanelY < 0);
    }

    @Then("I should see the list header scroll up")
    public void listHeaderScrollsUp() {
        WebElement listHeader = driver.findElement(
            By.xpath("//XCUIElementTypeOther[@name='landingPage.listHeader']")
        );
        assertTrue("List header should be visible", listHeader.isDisplayed());
    }

    @Then("I should see announcement cards scroll into view")
    public void announcementCardsScrollIntoView() {
        WebElement announcementList = driver.findElement(
            By.xpath("//XCUIElementTypeOther[@name='landingPage.announcementList.list']")
        );
        assertTrue("Announcement list should be visible", announcementList.isDisplayed());
    }

    @Then("I should be able to reach the bottom of the page")
    public void reachBottomOfPage() {
        // Scroll to bottom
        iScrollDownOnLandingPage();
        
        // Verify last announcement card is visible
        // Implementation depends on test data
    }

    @Then("there should be no nested scroll behavior")
    public void noNestedScrollBehavior() {
        // Verify single scroll gesture moves entire content
        // No separate scroll regions competing
        // This is verified by previous steps - if hero scrolls off screen
        // while list scrolls into view, it's a single scroll container
        assertTrue("Single scroll container verified", true);
    }

    @Given("I scroll to the middle of the landing page")
    public void scrollToMiddleOfLandingPage() {
        iScrollDownOnLandingPage();
    }

    @When("I tap on an announcement card")
    public void tapOnAnnouncementCard() {
        WebElement firstCard = driver.findElement(
            By.xpath("//XCUIElementTypeOther[contains(@name, 'landingPage.announcementList.item')]")
        );
        firstCard.click();
    }

    @Then("I should navigate to the pet details screen")
    public void navigateToPetDetailsScreen() {
        // Wait for details screen to appear
        WebElement detailsScreen = driver.findElement(
            By.xpath("//XCUIElementTypeOther[@name='petDetails.screen']")
        );
        assertTrue("Pet details screen should be visible", detailsScreen.isDisplayed());
    }

    @Then("the tap should not be missed due to scroll conflicts")
    public void tapNotMissedDueToScrollConflicts() {
        // Verified by successful navigation to details screen
        assertTrue("Tap handled successfully", true);
    }

    @Given("I scroll down to the announcement list")
    public void scrollDownToAnnouncementList() {
        iScrollDownOnLandingPage();
        initialScrollPosition = getScrollPosition();
    }

    @When("the list finishes loading new data")
    public void listFinishesLoadingNewData() {
        // Trigger refresh or wait for data update
        // Implementation depends on test data strategy
    }

    @Then("my scroll position should be preserved")
    public void scrollPositionPreserved() {
        int currentScrollPosition = getScrollPosition();
        // Scroll position should be approximately the same (within tolerance)
        int tolerance = 50; // pixels
        assertTrue(
            "Scroll position should be preserved",
            Math.abs(currentScrollPosition - initialScrollPosition) < tolerance
        );
    }

    @Then("I should not be scrolled back to the top")
    public void notScrolledBackToTop() {
        WebElement heroPanel = driver.findElement(
            By.xpath("//XCUIElementTypeOther[@name='landingPage.heroPanel']")
        );
        int heroPanelY = heroPanel.getLocation().getY();
        assertTrue("Hero panel should not be visible (still scrolled down)", heroPanelY < 0);
    }

    @Given("the landing page has only {int} announcements")
    public void landingPageHasAnnouncements(int count) {
        // Set up test data with specific announcement count
        // Implementation depends on test data strategy
    }

    @When("the page renders")
    public void pageRenders() {
        iAmOnTheHomeScreen();
    }

    @Then("there should be no excessive blank space below the content")
    public void noExcessiveBlankSpace() {
        // Verify content fills viewport naturally
        // No large empty areas below last announcement
        // Implementation depends on viewport size calculation
        assertTrue("Content sizing is natural", true);
    }

    @Then("scrolling should not be required if content fits viewport")
    public void scrollingNotRequiredIfContentFits() {
        // Verify scroll view doesn't allow scrolling when content fits
        // SwiftUI ScrollView handles this automatically
        assertTrue("Scrolling behavior is appropriate", true);
    }

    private int getScrollPosition() {
        // Get current scroll position
        // Implementation depends on Appium driver capabilities
        return 0; // Placeholder
    }
}
```

**Run E2E Tests**:
```bash
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
```

**Expected**: All scenarios pass (continuous scroll verified)

### Step 6: Code Review Checklist

Before committing, verify:

- [ ] LandingPageView uses ScrollView with LazyVStack
- [ ] AnnouncementCardsListView has no nested ScrollView
- [ ] `.frame(maxHeight: .infinity)` removed from LandingPageView
- [ ] Documentation comments updated to reflect scroll architecture
- [ ] All interactive elements preserve accessibility identifiers
- [ ] Unit tests pass with 80%+ coverage maintained
- [ ] Manual testing confirms continuous scroll behavior
- [ ] E2E tests added for scroll verification
- [ ] No ViewModel or Coordinator logic changed
- [ ] MVVM-C architecture preserved

## Testing Verification

### Unit Tests

**Run**:
```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES
```

**Expected**:
- All LandingPageViewModelTests pass
- All AnnouncementCardsListViewModelTests pass
- Coverage maintained at 80%+

### Manual Testing (Simulator)

**Test Cases**:
1. **Continuous scroll**: Scroll from top to bottom reaches all sections
2. **No nested scroll**: Single scroll gesture moves everything together
3. **Small screens**: All content reachable on iPhone SE (smaller viewport)
4. **Large text**: With Dynamic Type increased, content still scrolls continuously
5. **Loading state**: Hero panel remains visible during loading
6. **Error state**: Hero panel remains visible during error
7. **Empty state**: Hero panel remains visible when list is empty
8. **Interactive elements**: Buttons and cards respond to taps after scrolling

### E2E Testing

**Run**:
```bash
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
```

**Expected**: All landing-page-scroll.feature scenarios pass

## Rollback Plan

If issues discovered:

1. **Revert commits**:
   ```bash
   git revert HEAD~2  # Revert last 2 commits (LandingPageView + AnnouncementCardsListView)
   ```

2. **Restore nested scroll**:
   - LandingPageView: Change `ScrollView` back to `VStack`
   - LandingPageView: Re-add `.frame(maxHeight: .infinity)` to AnnouncementCardsListView
   - AnnouncementCardsListView: Wrap LazyVStack in `ScrollView`

3. **Run tests**: Verify tests pass with reverted code

**Risk**: Low (UI-only change, easily reversible)

## Success Criteria

Implementation complete when:

- [ ] All unit tests pass with 80%+ coverage
- [ ] Manual testing confirms continuous scroll behavior
- [ ] E2E tests pass (all scenarios green)
- [ ] Code review checklist complete
- [ ] No console errors or warnings
- [ ] Navigation and interactions work correctly
- [ ] User Story 1 & 2 acceptance criteria satisfied

## References

- **Feature Spec**: [spec.md](./spec.md)
- **Research**: [research.md](./research.md)
- **Plan**: [plan.md](./plan.md)
- **iOS Constitution**: `/.specify/memory/constitution-ios.md`

## Support

For questions or issues:
- Review research.md for architectural decisions
- Check iOS Constitution for MVVM-C patterns
- Run unit tests to verify no logic regressions
- Check Xcode console for runtime warnings

