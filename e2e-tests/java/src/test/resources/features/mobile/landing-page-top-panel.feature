Feature: iOS Landing Page Top Panel (Hero Section + List Header)
  As a user
  I want to see a top panel with quick actions on the Home tab
  So that I can quickly navigate to report lost/found pets or view all recent reports

  Background:
    Given the iOS app is launched
    And the user is on the Home tab

  # User Story 1: See top panel on Home

  @smoke @ios
  Scenario: Display hero panel with title and action buttons
    Given the backend has pet announcements
    When the landing page loads
    Then I should see the hero panel title "Find Your Pet"
    And I should see the "Lost Pet" button with alert icon
    And I should see the "Found Pet" button with checkmark icon

  @ios
  Scenario: Display list header row with title and View All action
    Given the backend has pet announcements
    When the landing page loads
    Then I should see the list header title "Recent Reports"
    And I should see the "View All" action link

  @ios
  Scenario: Verify top panel renders above the pet list
    Given the backend has 5 pet announcements
    When the landing page loads
    Then I should see the hero panel at the top of the screen
    And I should see the list header row below the hero panel
    And I should see announcement cards below the list header

  @ios
  Scenario: Existing list behavior unchanged after top panel added
    Given the backend has pet announcements
    And the landing page is loaded
    When I tap on an announcement card
    Then the app should switch to the Lost Pets tab
    And the pet details screen should be displayed

  # User Story 2: Use top panel actions to reach core flows

  @smoke @ios
  Scenario: Tap Lost Pet button to navigate to Lost Pet tab
    Given the landing page is loaded
    When I tap the "Lost Pet" hero button
    Then the app should switch to the Lost Pets tab
    And the Lost Pet tab content should be visible

  @ios
  Scenario: Tap Found Pet button to navigate to Found Pet tab
    Given the landing page is loaded
    When I tap the "Found Pet" hero button
    Then the app should switch to the Found Pet tab
    And the Found Pet tab content should be visible

  @ios
  Scenario: Tap View All to navigate to full announcements list
    Given the landing page is loaded
    When I tap the "View All" link
    Then the app should switch to the Lost Pets tab
    And the full announcements list should be visible

  @ios
  Scenario: Return to Home tab after navigating via hero button
    Given the landing page is loaded
    And I tap the "Lost Pet" hero button
    When I tap on the Home tab
    Then I should see the landing page with recent announcements
    And the hero panel should still be visible

  @ios
  Scenario: Hero panel accessibility identifiers are correct
    Given the landing page is loaded
    Then the element with accessibility id "home.hero.title" should be visible
    And the element with accessibility id "home.hero.lostPetButton" should be visible
    And the element with accessibility id "home.hero.foundPetButton" should be visible

  @ios
  Scenario: List header accessibility identifiers are correct
    Given the landing page is loaded
    Then the element with accessibility id "home.recentReports.title" should be visible
    And the element with accessibility id "home.recentReports.viewAll" should be visible

