@ios @landing-page @scroll
Feature: iOS Landing Page Full Content Scroll
  As a user
  I want to scroll through all landing page sections continuously
  So that I can access all content with a single scroll gesture

  Background:
    Given the iOS app is launched
    And the user is on the Home tab
    And the user has granted location permissions

  # User Story 1: Full Content Scroll

  @priority-high @smoke
  Scenario: Scroll entire landing page content continuously
    Given the backend has 5 pet announcements
    And the landing page is loaded
    When I scroll down on the landing page
    Then the hero panel should scroll off screen
    And the list header should remain scrollable
    And announcement cards should be visible after scrolling
    And there should be no nested scroll behavior

  @priority-high
  Scenario: All sections scroll together as one
    Given the backend has 5 pet announcements
    And the landing page is loaded
    When I perform a single scroll gesture on the landing page
    Then all sections should move together in the same direction
    And no section should scroll independently

  @priority-medium
  Scenario: Scroll position preserved during state changes
    Given the backend has 5 pet announcements
    And the landing page is loaded
    And I scroll down to the announcement list
    When the list content refreshes
    Then my scroll position should be approximately preserved
    And I should not be scrolled back to the top

  @priority-low
  Scenario: Short content does not cause excessive blank space
    Given the backend has 2 pet announcements
    When the landing page loads
    Then content should fit naturally without excessive blank space
    And the scroll area should match the content size

  # User Story 2: Preserved Interactions After Scroll

  @priority-high @smoke
  Scenario: Tap announcement card after scrolling
    Given the backend has 5 pet announcements
    And the landing page is loaded
    And I scroll to the middle of the landing page
    When I tap on an announcement card
    Then the app should switch to the Lost Pets tab
    And the pet details screen should be displayed
    And the tap should not be missed due to scroll conflicts

  @priority-high
  Scenario: Hero buttons work after scrolling up
    Given the backend has 5 pet announcements
    And the landing page is loaded
    And I scroll down on the landing page
    And I scroll back to the top
    When I tap the Lost Pet hero button
    Then the app should switch to the Lost Pets tab

  @priority-medium
  Scenario: List header action works after scrolling
    Given the backend has 5 pet announcements
    And the landing page is loaded
    And I scroll to show the list header
    When I tap the View All button in the list header
    Then the app should switch to the Lost Pets tab
    And the full announcement list should be visible

  @priority-medium
  Scenario: Rapid scroll then tap interaction
    Given the backend has 5 pet announcements
    And the landing page is loaded
    When I perform rapid scroll down and immediately tap a card
    Then the tap should register correctly
    And navigation should occur to pet details

  # Accessibility & Edge Cases

  @priority-medium @accessibility
  Scenario: Continuous scroll on smaller screen
    Given the backend has 5 pet announcements
    And the device has a smaller screen size
    When the landing page loads
    Then all sections should be reachable via continuous scroll
    And no content should be trapped in inaccessible areas

  @priority-low @accessibility
  Scenario: Continuous scroll with larger text
    Given the backend has 5 pet announcements
    And the device has larger text accessibility setting enabled
    When the landing page loads
    Then all content should still be scrollable continuously
    And text should wrap correctly within scroll content

