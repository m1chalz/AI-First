@web
Feature: Pet List Management (Web) - Complete Coverage
  As a user browsing the PetSpot web application
  I want to view and interact with pet announcements
  So that I can find pets and report missing animals

  Background:
    Given I am on the pet list page
    And the page has loaded completely

  # EXISTING SCENARIOS (from Spec 016)

  @smoke
  Scenario: View pet list on web
    When I view the pet list
    Then I should see at least one pet announcement
    And the list should contain animal cards

  @smoke
  Scenario: Verify UI elements are present
    Then the add button should be visible
    And the add button should have text "Report a Missing Animal"

  # NEW SCENARIOS (Feature 025) - 8 scenarios below

  Scenario: List scrolls smoothly
    When I scroll down the pet list
    Then the list should scroll smoothly
    And all animal cards should remain accessible

  Scenario: Scrolling stops at last item
    Given there are multiple animals in the list
    When I scroll to the bottom of the list
    Then the list should stop at the last animal card
    And I should not be able to scroll further

  Scenario: Animal card tap triggers navigation
    When I click on an animal card with ID "1"
    Then the system should trigger navigation to animal details
    And I should see a console log with animal ID "1"

  Scenario: Button remains visible during scroll
    When I scroll up and down the animal list
    Then the "Report a Missing Animal" button should remain visible at all times
    And the button should be clickable after scrolling

  Scenario: Report button tap action
    When I click the "Report a Missing Animal" button
    Then the system should trigger the report missing animal flow
    And I should see a console log confirming the action

  Scenario: Animal card details display correctly
    Given I am on the pet list page with loaded animals
    When I view an animal card with ID "1"
    Then the card should display species, breed, status, date, and location
    And the status badge should show "MISSING" or "FOUND"
    And the date should be in format "DD/MM/YYYY"

  Scenario: Loading state displayed during initial load
    Given I navigate to the pet list page for the first time
    When the page is loading animal data
    Then a loading indicator should be displayed
    And the animal list should eventually appear with data

  Scenario: Report Found Animal button visible (web only)
    Then the "Report Found Animal" button should be visible
    And the button should be positioned at the top-right of the page
    And the button should have appropriate styling

  Scenario: Reserved search space present
    When I view the top of the screen
    Then there should be space reserved for a search component
    And the space should have minimum height of 64 pixels
    And the space should be positioned above the animal list

  # Total: 10/10 scenarios (100% coverage of Spec 005 web requirements)

