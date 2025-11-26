# Feature: Pet List Management (Web)
# This feature file tests the web interface for viewing pet announcements.
# NOTE: Current React implementation has limited functionality - only list view is implemented.
# Search/filter and navigation features are not yet available in the UI.

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
    When I view the pet list
    Then I should see at least one pet announcement
    And the list should contain animal cards
  
  @smoke
  Scenario: Verify UI elements are present
    Then the add button should be visible
    And the add button should have text "Report a Missing Animal"
