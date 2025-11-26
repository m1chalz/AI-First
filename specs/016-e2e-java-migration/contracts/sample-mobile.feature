@mobile
Feature: Pet List Management (Mobile)
  As a user using the PetSpot mobile application
  I want to view and search for pet announcements
  So that I can find pets available for adoption on my mobile device

  Background:
    Given I have launched the mobile app
    And I am on the pet list screen

  @android @smoke
  Scenario: View pet list on Android
    When I view the pet list
    Then I should see at least one pet announcement
    And each pet should display name, species, and image

  @ios @smoke
  Scenario: View pet list on iOS
    When I view the pet list
    Then I should see at least one pet announcement
    And each pet should display name, species, and image

  @android
  Scenario: Search for specific species on Android
    When I tap on the search input
    And I enter "dog" in the search field
    Then I should see only dog announcements
    And the Android keyboard should be hidden

  @ios
  Scenario: Search for specific species on iOS
    When I tap on the search input
    And I enter "cat" in the search field
    Then I should see only cat announcements
    And the iOS keyboard should be dismissed

  @android @ios
  Scenario Outline: Scroll through pet list on mobile
    When I scroll down the pet list
    Then more pet announcements should load
    And I should see pet announcement at position <position>
    
    Examples:
      | position |
      | 5        |
      | 10       |

  @android @navigation
  Scenario: Navigate to pet details from Android list
    When I tap on the first pet in the list
    Then I should navigate to the pet details screen
    And the pet details should match the list entry

  @ios @navigation
  Scenario: Navigate to pet details from iOS list
    When I tap on the first pet in the list
    Then I should navigate to the pet details screen
    And the pet details should match the list entry

  @android @negative
  Scenario: Handle empty search results on Android
    When I tap on the search input
    And I enter "nonexistent-species" in the search field
    Then I should see no pet announcements
    And an empty state message should be displayed

  @ios @negative
  Scenario: Handle empty search results on iOS
    When I tap on the search input
    And I enter "nonexistent-species" in the search field
    Then I should see no pet announcements
    And an empty state message should be displayed

