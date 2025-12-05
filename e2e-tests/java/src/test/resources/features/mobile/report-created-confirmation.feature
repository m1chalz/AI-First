@mobile @android @report-created
Feature: Report Created Confirmation Screen (Android)
  As a user who has successfully submitted a missing pet report
  I want to see confirmation of my report submission
  So that I can save the management code and exit the flow confidently

  Background:
    Given I have completed the missing pet report submission flow
    And I am on the Report Created Confirmation screen

  # ═══════════════════════════════════════════════════════════════════════════
  # User Story 1: Understand Confirmation Outcome (Priority: P1)
  # ═══════════════════════════════════════════════════════════════════════════

  @smoke @us1
  Scenario: User sees confirmation messaging
    Then I should see the title "Report created"
    And I should see the first body paragraph about report creation
    And I should see the second body paragraph about the removal code
    And there should be no back button in the header
    And there should be no loading indicator

  @us1
  Scenario: Confirmation screen displays correct typography
    Then the title should have large heading style
    And the body text should have body style with correct color
    And the layout should respect proper spacing

  # ═══════════════════════════════════════════════════════════════════════════
  # User Story 2: Retrieve and Safeguard Management Password (Priority: P1)
  # ═══════════════════════════════════════════════════════════════════════════

  @smoke @us2
  Scenario: User copies password to clipboard
    Given the management code "5216577" is displayed
    When I tap on the password container
    Then I should see a snackbar with message "Code copied to clipboard"
    And the code should be copied to the device clipboard

  @us2
  Scenario: Password displays in gradient container
    Then I should see the password container with gradient background
    And the password digits should be white and large
    And the gradient should go from purple to pink

  @us2
  Scenario: Empty password displays gracefully
    Given the management code is empty
    Then the gradient container should be visible
    And no password digits should be displayed
    And tapping the container should still work without crashing

  # ═══════════════════════════════════════════════════════════════════════════
  # User Story 3: Exit the Flow Safely (Priority: P2)
  # ═══════════════════════════════════════════════════════════════════════════

  @smoke @us3
  Scenario: User exits flow via Close button
    When I tap the Close button
    Then the flow should be dismissed
    And I should be on the pet list screen
    And no back stack entries from the report flow should remain

  @us3
  Scenario: User exits flow via system back
    When I press the system back button
    Then the flow should be dismissed
    And I should be on the pet list screen
    And no back stack entries from the report flow should remain

  @us3
  Scenario: User exits flow via system back gesture
    When I perform the system back gesture
    Then the flow should be dismissed
    And I should be on the pet list screen
    And no back stack entries from the report flow should remain

  @us3
  Scenario: Close button has correct styling
    Then I should see the Close button
    And the Close button should be full-width
    And the Close button should have blue background color
    And the Close button text should be "Close"

  # ═══════════════════════════════════════════════════════════════════════════
  # Edge Cases
  # ═══════════════════════════════════════════════════════════════════════════

  @edge-case
  Scenario: Screen rotates without losing state
    Given the management code "5216577" is displayed
    When I rotate the device to landscape
    Then the management code should still be "5216577"
    And the UI should adapt without clipping

  @edge-case
  Scenario: Multiple copy actions show snackbar each time
    Given the management code "5216577" is displayed
    When I tap on the password container
    And I wait for the snackbar to disappear
    And I tap on the password container again
    Then I should see a snackbar with message "Code copied to clipboard"

  # Total: 12 scenarios covering all 3 user stories and edge cases

