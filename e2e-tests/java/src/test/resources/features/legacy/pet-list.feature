# Mobile E2E Test: Pet List Management (Android + iOS)
# This feature file demonstrates dual-platform mobile testing with Appium
# Tags @android and @ios control which platform runs each scenario
# Both tags can be combined: @android @ios runs on both platforms

@mobile @legacy
Feature: Pet List Management (Mobile) - LEGACY
  As a user using the PetSpot mobile application
  I want to view pet announcements
  So that I can see lost or found animals in my area

  # Background runs before each scenario (app launch and navigation)
  Background:
    Given I have launched the mobile app
    And I am on the pet list screen

  # Android-specific smoke test
  @android @smoke
  Scenario: View pet list on Android
    When I view the pet list
    Then I should see at least one pet announcement
    And each pet should display name, species, and image

  # iOS-specific smoke test  
  @ios @smoke
  Scenario: View pet list on iOS
    When I view the pet list
    Then I should see at least one pet announcement
    And each pet should display name, species, and image

  # Cross-platform scenario (runs on both Android and iOS)
  @android @ios
  Scenario Outline: Scroll through pet list on mobile
    When I scroll down the pet list
    Then more pet announcements should load
    And I should see pet announcement at position <position>
    
    Examples:
      | position |
      | 5        |
      | 10       |

  # Platform-specific navigation tests
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




