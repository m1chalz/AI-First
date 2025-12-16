@mobile @ios @legacy
Feature: Pet Details Screen (iOS) - LEGACY
  As a user using the PetSpot mobile application
  I want to view detailed information about a pet
  So that I can contact the owner and verify pet identity

  Background:
    Given I have launched the mobile app
    And I am on the pet list screen

  # CORE SCENARIOS (Feature 025) - MVP coverage 10-12 scenarios

  @smoke
  Scenario: Navigate to pet details from list
    When I tap on the first pet in the list
    Then I should navigate to the pet details screen
    And the details view should be displayed

  @smoke
  Scenario: Display loading state
    Given I navigate to pet details for pet "1"
    When data is being fetched from repository
    Then I should see a loading indicator
    And the loading indicator should be centered on screen

  @smoke
  Scenario: Display pet details after loading completes
    Given I navigate to pet details for pet "1"
    When the details finish loading successfully
    Then I should see pet photo
    And I should see pet name, species, breed, and status
    And all fields should contain valid data

  Scenario: Display error state with retry button
    Given I navigate to pet details for invalid pet "non-existent"
    When data fetch fails with error
    Then I should see error message
    And I should see retry button
    And the error message should be user-friendly

  Scenario: Retry button reloads data
    Given I am on pet details screen with error state
    When I tap the retry button
    Then the loading state should be displayed again
    And the system should attempt to fetch data again

  Scenario: Contact owner via phone tap
    Given I am on pet details screen for pet "1"
    And the pet has phone number
    When I tap on the phone number field
    Then iOS dialer should open with the phone number
    And the phone number should match the pet owner's number

  Scenario: Contact owner via email tap
    Given I am on pet details screen for pet "1"
    And the pet has email address
    When I tap on the email address field
    Then iOS mail composer should open
    And the email address should be pre-filled

  Scenario: Display MISSING status badge with red color
    Given I am on pet details screen for a missing pet
    Then I should see status badge with text "MISSING"
    And the badge should have red background color (#FF0000)
    And the badge should be prominently displayed

  Scenario: Display FOUND status badge with blue color
    Given I am on pet details screen for a found pet
    Then I should see status badge with text "FOUND"
    And the badge should have blue background color (#155DFC)
    And the badge should be prominently displayed

  Scenario: Display Remove Report button at bottom
    Given I am on pet details screen for pet "1"
    When I scroll to the bottom of the screen
    Then I should see "Remove Report" button
    And the button should be visible and tappable

  Scenario: Handle Remove Report button tap
    Given I am on pet details screen for pet "1"
    And the Remove Report button is visible
    When I tap the Remove Report button
    Then the action should be logged to console
    And the system should trigger remove report flow

  Scenario: Display fallback when photo not available
    Given I am on pet details screen for pet without photo
    Then I should see photo placeholder
    And the placeholder should display "Image not available" text
    And the placeholder should have appropriate styling

  # Total: 12 scenarios (35-40% coverage of Spec 012 requirements - MVP coverage)
  # Remaining scenarios (error handling variations, edge cases, full field validation)
  # can be added in future iterations to reach 100% coverage









