Feature: iOS Landing Page (Home Tab)
  As a user
  I want to see recent pet announcements on the Home tab
  So that I can quickly check for new pet reports without navigating to the Lost Pets tab

  Background:
    Given the iOS app is launched
    And the user is on the Home tab

  # User Story 1: View Recent Pet Announcements

  @smoke @ios
  Scenario: Display 5 most recent announcements
    Given the backend has 10 pet announcements
    When the landing page loads
    Then I should see exactly 5 announcement cards
    And the announcements should be sorted by date with newest first

  @ios
  Scenario: Display all announcements when backend has fewer than 5
    Given the backend has 3 pet announcements
    When the landing page loads
    Then I should see exactly 3 announcement cards

  @ios
  Scenario: Display empty state when no announcements
    Given the backend has 0 pet announcements
    When the landing page loads
    Then I should see the empty state view with accessibility id "landingPage.emptyState"
    And the empty state message should mention no recent announcements

  @ios
  Scenario: Display error message when backend is unavailable
    Given the backend API is unavailable
    When the landing page loads
    Then I should see the error view with accessibility id "landingPage.error"
    And a retry button should be visible

  @ios
  Scenario: Display loading indicator while fetching data
    Given the backend has pet announcements
    When the landing page starts loading
    Then I should see the loading view with accessibility id "landingPage.loading"

  @ios
  Scenario: Display location coordinates when location permissions granted
    Given the user has granted location permissions
    And the backend has pet announcements
    When the landing page loads
    Then announcement cards should display location coordinates

  @ios
  Scenario: Hide location coordinates when location permissions denied
    Given the user has denied location permissions
    And the backend has pet announcements
    When the landing page loads
    Then announcement cards should display location coordinates based on pet location

  # User Story 2: Navigate to Pet Details

  @smoke @ios
  Scenario: Navigate to pet details from landing page
    Given the backend has 5 pet announcements
    And the landing page is loaded
    When I tap on the first announcement card
    Then the app should switch to the Lost Pets tab
    And the pet details screen should be displayed

  @ios
  Scenario: Back navigation returns to Lost Pets tab after viewing details
    Given the backend has pet announcements
    And the landing page is loaded
    And I tap on an announcement card
    And the pet details screen is displayed
    When I navigate back
    Then I should be on the Lost Pets tab
    And the announcement list should be visible

  @ios
  Scenario: Tapping Home tab after viewing details returns to landing page
    Given the backend has pet announcements
    And the landing page is loaded
    And I tap on an announcement card to view details
    When I tap on the Home tab
    Then I should see the landing page with recent announcements

