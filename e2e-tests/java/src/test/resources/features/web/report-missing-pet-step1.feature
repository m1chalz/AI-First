@web @reportMissingPet
Feature: Report Missing Pet - Step 1 Microchip Number

  Background:
    Given I navigate to "/report-missing/microchip"

  @US1
  Scenario: User enters microchip number with automatic formatting
    When I enter microchip number "123456789012345"
    Then the microchip input should display "12345-67890-12345"
    And the continue button should be enabled
    When I click the continue button on step 1
    Then the current URL should contain "/report-missing/photo"

  @US2
  Scenario: User skips microchip number (optional field)
    Given the continue button should be enabled
    When I click the continue button on step 1
    Then the current URL should contain "/report-missing/photo"

  @US3
  Scenario: User cancels flow via back button
    When I enter microchip number "12345"
    And I click the back button on step 1
    Then the current URL should contain "/pets"

  @US4
  Scenario: User edits microchip after navigating back
    When I enter microchip number "123456789012345"
    And I click the continue button on step 1
    And the current URL should contain "/report-missing/photo"
    And I go back to the previous page
    Then the current URL should contain "/report-missing/microchip"
    And the microchip input should display "12345-67890-12345"

