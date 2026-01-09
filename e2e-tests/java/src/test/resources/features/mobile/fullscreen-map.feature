@android
Feature: Fullscreen Interactive Map
  As a user looking for or reporting pets
  I want to view an interactive fullscreen map with pet locations
  So that I can see where missing and found pets are located

  Background:
    Given the app is open and user is on the home screen
    And location permission has been granted

  # =====================================================
  # User Story 1: Open Fullscreen Interactive Map
  # =====================================================

  @US1 @smoke
  Scenario: User opens fullscreen map from preview
    Given user is on landing page with map preview visible
    When user taps the map preview
    Then fullscreen interactive map is displayed
    And back button is visible in top-left corner
    And "Pet Locations" title is displayed
    And legend showing "Missing" and "Found" is visible

  @US1
  Scenario: User returns to landing page via back button
    Given fullscreen map is displayed
    When user taps the back button
    Then user is returned to the landing page
    And map preview is still visible

  @US1
  Scenario: User returns to landing page via system back button
    Given fullscreen map is displayed
    When user presses the system back button
    Then user is returned to the landing page

  # =====================================================
  # User Story 2: Navigate the Fullscreen Map
  # =====================================================

  @US2
  Scenario: User zooms in using pinch gesture
    Given fullscreen map is displayed
    When user performs pinch-to-zoom-in gesture
    Then map zoom level increases
    And map displays more detail

  @US2
  Scenario: User pans the map
    Given fullscreen map is displayed
    When user drags the map to pan
    Then map center position changes
    And new area becomes visible

  # =====================================================
  # User Story 3: View Pet Pins on the Map
  # =====================================================

  @US3
  Scenario: User sees red pins for missing pets
    Given fullscreen map is displayed with pet pins
    And there are missing pets in the visible area
    Then red pins are displayed on the map for missing pets

  @US3
  Scenario: User sees blue pins for found pets
    Given fullscreen map is displayed with pet pins
    And there are found pets in the visible area
    Then blue pins are displayed on the map for found pets

  @US3
  Scenario: Pins update when user pans map
    Given fullscreen map is displayed with pet pins
    When user pans the map to a new area
    And the gesture completes
    Then pins are updated to show pets in the new visible area

  @US3
  Scenario: Loading indicator shown while fetching pins
    Given fullscreen map is displayed
    When map viewport changes
    Then loading indicator is displayed
    And loading indicator disappears when pins are loaded

  @US3
  Scenario: Error state with retry button on failure
    Given fullscreen map is displayed
    And network connection fails
    When pins fail to load
    Then error message is displayed
    And retry button is visible
    When user taps retry button
    Then loading indicator is displayed

  # =====================================================
  # User Story 4: View Pet Details from a Pin
  # =====================================================

  @US4
  Scenario: User views pet details by tapping pin
    Given fullscreen map is displayed with pet pins
    When user taps a pin
    Then pet details popup appears
    And popup shows pet photo or placeholder
    And popup shows pet name
    And popup shows species and breed
    And popup shows last seen date
    And popup shows description
    And popup shows contact information

  @US4
  Scenario: User dismisses popup by tapping outside
    Given pet details popup is displayed
    When user taps outside the popup
    Then popup is dismissed
    And map is fully visible again

  @US4
  Scenario: User dismisses popup by swiping down
    Given pet details popup is displayed
    When user swipes down on the popup
    Then popup is dismissed

  @US4
  Scenario: User dismisses popup by tapping close button
    Given pet details popup is displayed
    When user taps the close button on popup
    Then popup is dismissed

  @US4
  Scenario: User taps different pin to update popup
    Given pet details popup is displayed for "Max"
    When user taps a different pin for "Bella"
    Then popup updates to show "Bella" details
    And popup shows correct contact information for "Bella"
