@web
Feature: Pet List Management (Web)
  As a user browsing the PetSpot web application
  I want to view and search for pet announcements
  So that I can find pets available for adoption

  Background:
    Given I am on the pet list page
    And the page has loaded completely

  @smoke
  Scenario: View pet list on web
    When I view the pet list
    Then I should see at least one pet announcement
    And each pet should display name, species, and image

  Scenario: Search for specific species on web
    When I search for "dog"
    Then I should see only dog announcements
    And the search results count should be displayed

  Scenario Outline: Filter pets by multiple species
    When I search for "<species>"
    Then I should see only "<species>" announcements
    And the announcement count should match the filter
    
    Examples:
      | species |
      | dog     |
      | cat     |
      | bird    |

  @navigation
  Scenario: Navigate to pet details from list
    When I click on the first pet in the list
    Then I should be navigated to the pet details page
    And the pet details should match the list entry

  @negative
  Scenario: Handle empty search results on web
    When I search for "nonexistent-species"
    Then I should see no pet announcements
    And an empty state message should be displayed

