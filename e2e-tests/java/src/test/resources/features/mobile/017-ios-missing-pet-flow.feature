# Missing Pet Report Flow - iOS E2E Tests
# This feature tests the complete missing pet report flow on iOS platform
# Uses Appium with UIKit/SwiftUI navigation and accessibility identifiers

@mobile @ios @missing-pet
Feature: Missing Pet Report Flow (iOS)
  As a pet owner
  I want to report my missing pet through a structured multi-step flow
  So that I can provide comprehensive information to help find my pet

  # Common setup for all scenarios
  Background:
    Given I have launched the iOS app
    And I am on the animal list screen

  # ========================================
  # USER STORY 1: Complete Missing Pet Report (Priority: P1)
  # ========================================

  @ios @us1 @smoke
  Scenario: Complete Missing Pet Report Flow - Navigate Through All 5 Screens
    # Given: User is on animal list screen (from Background)
    
    # Step 1: User taps "report missing animal" button
    When I tap the "report missing animal" button on animal list
    Then the "chip number" screen should be displayed
    And the progress indicator should show "1/4"
    
    # Step 2: Navigate to photo screen
    When I tap the "continue" button
    Then the "photo" screen should be displayed
    And the progress indicator should show "2/4"
    
    # Step 3: Navigate to description screen
    When I tap the "continue" button
    Then the "description" screen should be displayed
    And the progress indicator should show "3/4"
    
    # Step 4: Navigate to contact details screen
    When I tap the "continue" button
    Then the "contact details" screen should be displayed
    And the progress indicator should show "4/4"
    
    # Step 5: Navigate to summary screen (no progress indicator)
    When I tap the "continue" button
    Then the "summary" screen should be displayed
    And the progress indicator should not be visible

  @ios @us1
  Scenario: Microchip input formats digits automatically
    When I tap the "report missing animal" button on animal list
    And I type "123456789012345" into the microchip number field
    Then the microchip number field should display "12345-67890-12345"

  @ios @us2
  Scenario: Continue with empty microchip number
    When I tap the "report missing animal" button on animal list
    And I clear the microchip number field
    Then the microchip number field should be empty
    And I tap the "continue" button
    Then the "photo" screen should be displayed
    And the progress indicator should show "2/4"

  @ios @us4
  Scenario: Microchip number persists when returning from photo screen
    When I tap the "report missing animal" button on animal list
    And I type "123456" into the microchip number field
    And I tap the "continue" button
    Then the "photo" screen should be displayed
    When I tap the back button
    Then the "chip number" screen should be displayed
    And the microchip number field should display "12345-6"

  @ios @us1
  Scenario: Progress Indicator Updates Correctly During Forward Navigation
    When I tap the "report missing animal" button on animal list
    And I verify progress indicator displays "1/4" on chip number screen
    And I tap the "continue" button
    And I verify progress indicator displays "2/4" on photo screen
    And I tap the "continue" button
    And I verify progress indicator displays "3/4" on description screen
    And I tap the "continue" button
    And I verify progress indicator displays "4/4" on contact details screen
    Then each screen transition should update progress indicator correctly

  @ios @us1
  Scenario: All Interactive Elements Have Correct Accessibility Identifiers
    When I tap the "report missing animal" button on animal list
    Then the screen should have accessibility identifier "missingPet.microchip.continueButton" for continue button
    When I tap the "continue" button
    Then the screen should have accessibility identifier "photo.continueButton" for continue button
    When I tap the "continue" button
    Then the screen should have accessibility identifier "description.continueButton" for continue button
    When I tap the "continue" button
    Then the screen should have accessibility identifier "contactDetails.continueButton" for continue button
    When I tap the "continue" button
    Then the screen should have accessibility identifier "summary.submitButton" for submit button

  # ========================================
  # USER STORY 2: Navigate Backwards Through Flow (Priority: P2)
  # ========================================

  @ios @us2
  Scenario: Navigate Backwards from Middle Step Back to Previous Screen
    # Setup: Navigate forward to step 4 (contact details)
    Given I tap the "report missing animal" button on animal list
    And I navigate to step "4" by tapping continue 3 times
    
    # Action: Tap back button on step 4
    When I tap the back button
    
    # Verify: Step 3 (description) should display with progress 3/4
    Then the "description" screen should be displayed
    And the progress indicator should show "3/4"

  @ios @us2
  Scenario: Navigate Backwards from Summary to Contact Details
    # Setup: Navigate forward to summary screen
    Given I tap the "report missing animal" button on animal list
    And I navigate to summary screen by tapping continue 4 times
    
    # Action: Tap back button on summary
    When I tap the back button
    
    # Verify: Step 4 (contact details) should display with progress 4/4
    Then the "contact details" screen should be displayed
    And the progress indicator should show "4/4"

  @ios @us2 @smoke
  Scenario: Exit Flow from Step 1 by Tapping Back Button
    # Setup: Open report missing pet flow (step 1)
    Given I tap the "report missing animal" button on animal list
    And the "chip number" screen should be displayed
    And the progress indicator should show "1/4"
    
    # Action: Tap back button on step 1
    When I tap the back button
    
    # Verify: Modal dismisses and animal list screen displays
    Then the "animal list" screen should be displayed
    And the report missing pet flow should be exited

  @ios @us2
  Scenario: Navigate Backwards Multiple Times to Earlier Steps
    # Setup: Navigate to step 3 (description screen)
    Given I tap the "report missing animal" button on animal list
    And I navigate to step "3" by tapping continue 2 times
    
    # Action: Tap back button multiple times
    When I tap the back button
    Then the "photo" screen should be displayed
    And the progress indicator should show "2/4"
    
    When I tap the back button
    Then the "chip number" screen should be displayed
    And the progress indicator should show "1/4"
    
    # Action: Exit from step 1
    When I tap the back button
    Then the "animal list" screen should be displayed

  @ios @us2
  Scenario: Progress Indicator Updates Correctly During Backward Navigation
    # Setup: Navigate forward to step 4
    Given I tap the "report missing animal" button on animal list
    And I navigate to step "4" by tapping continue 3 times
    
    # Action: Navigate backward through all steps
    When I tap the back button
    And I verify progress indicator displays "3/4" on description screen
    And I tap the back button
    And I verify progress indicator displays "2/4" on photo screen
    And I tap the back button
    And I verify progress indicator displays "1/4" on chip number screen
    Then progress indicator updates immediately when navigating backward

  # ========================================
  # EDGE CASES & NEGATIVE TESTS
  # ========================================

  @ios @edge-case
  Scenario: Continue Button Is Visible and Tappable on All Data Collection Screens
    Given I tap the "report missing animal" button on animal list
    Then the "continue" button should be displayed and tappable
    
    When I tap the "continue" button
    Then the "continue" button should be displayed and tappable
    
    When I tap the "continue" button
    Then the "continue" button should be displayed and tappable
    
    When I tap the "continue" button
    Then the "continue" button should be displayed and tappable

  @ios @edge-case
  Scenario: Back Button Is Visible and Tappable on All Screens
    Given I tap the "report missing animal" button on animal list
    Then the back button should be displayed and tappable
    
    When I tap the "continue" button
    Then the back button should be displayed and tappable
    
    When I tap the "continue" button
    Then the back button should be displayed and tappable
    
    When I tap the "continue" button
    Then the back button should be displayed and tappable
    
    When I tap the "continue" button
    Then the back button should be displayed and tappable

