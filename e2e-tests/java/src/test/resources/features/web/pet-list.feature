# Feature: Pet List Management (Web)
# This feature file tests the web interface for viewing pet announcements.
# NOTE: Current React implementation has limited functionality - only list view is implemented.

@web
Feature: Pet List Management (Web)
  As a user browsing the PetSpot web application
  I want to view pet announcements
  So that I can see lost or found animals in my area
  
  Background:
    Given I am on the pet list page
    And the page has loaded completely
  
  @smoke
  Scenario: View pet list on web
    When I view the web pet list
    Then I should see at least one web pet announcement
    And the list should contain animal cards
  
  @smoke
  Scenario: Verify UI elements are present
    Then the add button should be visible
    And the add button should have text "Report a Missing Animal"
  
  # ========================================
  # Feature 025: New Web Coverage Scenarios
  # ========================================
  
  Scenario: Animal card tap triggers navigation
    When I click on an animal card with ID "1"
    Then the system should trigger navigation to animal details
    And I should see a console log with animal ID "1"
  
  Scenario: Report button tap action
    When I click the "Report a Missing Animal" button
    Then the system should trigger the report missing animal flow
    And I should see a console log confirming the action
  
  Scenario: Animal card details display correctly
    Given I am on the pet list page with loaded animals
    When I view an animal card with ID "1"
    Then the card should display species, breed, status, date, and location
    And the status badge should show "Active" or "Found"
    And the date should be in format "DD/MM/YYYY"