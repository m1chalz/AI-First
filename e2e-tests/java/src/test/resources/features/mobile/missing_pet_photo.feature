# Missing Pet Photo Step - iOS E2E (Scenario Shell)
# This feature will evolve with US1/US2/US3 work on the photo attachment step.

@mobile @ios @missingPetPhoto
Feature: Attach photo for missing pet report (iOS)
  As a reporter filling the missing pet flow
  I want to select a recent photo via the Photos picker
  So that I can confirm the attachment before proceeding

  Background:
    Given I have launched the iOS app
    And I am on the animal list screen

  @us1 @happy_path
  Scenario: Attach a seeded photo and advance to description
    When I tap the "report missing animal" button on animal list
    Then the "chip number" screen should be displayed
    And the progress indicator should show "1/4"

    When I tap the "continue" button
    Then the "photo" screen should be displayed
    And the progress indicator should show "2/4"

    When I browse and select a seeded animal photo
    Then the photo confirmation card should be visible
    And the photo confirmation card should display filename containing "missing"

    When I tap the "continue" button
    Then the "description" screen should be displayed
    And the progress indicator should show "3/4"

  @us1
  Scenario: Photo selection persists when returning to the step
    When I tap the "report missing animal" button on animal list
    And I tap the "continue" button
    And I browse and select a seeded animal photo
    And the photo confirmation card should be visible
    When I tap the "continue" button
    And I tap the back button
    Then the "photo" screen should be displayed
    And the photo confirmation card should be visible

  @us2
  Scenario: Continue shows mandatory toast when no photo is present
    When I tap the "report missing animal" button on animal list
    And I tap the "continue" button
    Then the "photo" screen should be displayed
    When I tap the "continue" button
    Then the mandatory photo toast should be visible
    And the "photo" screen should be displayed

  @us2
  Scenario: Removing the photo replays the toast
    When I tap the "report missing animal" button on animal list
    And I tap the "continue" button
    And I browse and select a seeded animal photo
    And the photo confirmation card should be visible
    When I remove the selected animal photo
    Then the photo confirmation card should be hidden
    When I tap the "continue" button
    Then the mandatory photo toast should be visible

  @us3
  Scenario: Cancelling the picker keeps the helper state
    When I tap the "report missing animal" button on animal list
    And I tap the "continue" button
    And I trigger the debug photo picker cancellation
    When I tap the "continue" button
    Then the mandatory photo toast should be visible

  @us3
  Scenario: Simulated transfer failure clears the confirmation card
    When I tap the "report missing animal" button on animal list
    And I tap the "continue" button
    And I browse and select a seeded animal photo
    And the photo confirmation card should be visible
    When I trigger the debug photo transfer failure
    Then the photo confirmation card should be hidden

