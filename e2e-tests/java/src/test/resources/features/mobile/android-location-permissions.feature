# Android Location Permissions E2E Tests
# Feature: Location permissions handling on Android Animal List screen
# Tests all 5 user stories defined in specs/026-android-location-permissions

@android @location-permissions
Feature: Android Location Permissions Handling
  As a user of the PetSpot Android application
  I want location permissions to be handled gracefully
  So that I can see location-aware animal listings when I grant permission
  And continue using the app when I deny permission

  # ============================================
  # User Story 1: Location-Aware Content for Location-Authorized Users (P1) - MVP
  # ============================================
  
  @us1 @mvp @smoke
  Scenario: US1 - Users with granted permissions see location-aware listings
    Given I launch the app on Android
    And location permission is already granted
    When I open the animal list screen
    Then the app should fetch my current location
    And I should see location-aware animal listings

  @us1 @mvp
  Scenario: US1 - Loading indicator shown during location fetch
    Given I launch the app on Android
    And location permission is already granted
    When I open the animal list screen
    Then I should see a loading indicator
    And the app should fetch my current location

  @us1 @negative
  Scenario: US1 - App handles location fetch timeout gracefully
    Given I launch the app on Android
    And location permission is already granted
    # Simulated: Location services disabled or GPS unavailable
    When I open the animal list screen
    Then the app should continue without location
    And I should see location-aware animal listings

  # ============================================
  # User Story 2: First-Time Location Permission Request (P2)
  # ============================================
  
  @us2
  Scenario: US2 - First-time users see system permission dialog
    Given I launch the app on Android
    And location permission has not been requested
    When I open the animal list screen
    Then I should see the system permission dialog

  @us2
  Scenario: US2 - User allows location permission
    Given I launch the app on Android
    And location permission has not been requested
    When I open the animal list screen
    And the system permission dialog appears
    And I tap Allow on the permission dialog
    Then the app should fetch my current location
    And I should see location-aware animal listings

  @us2
  Scenario: US2 - User denies location permission
    Given I launch the app on Android
    And location permission has not been requested
    When I open the animal list screen
    And the system permission dialog appears
    And I tap Deny on the permission dialog
    Then the app should continue without location
    And I should see location-aware animal listings

  # ============================================
  # User Story 3: Recovery Path for Denied Permissions (P3)
  # ============================================
  
  @us3
  Scenario: US3 - Denied users see informational rationale dialog
    Given I launch the app on Android
    And location permission was denied
    When I open the animal list screen
    Then I should see the informational rationale dialog

  @us3
  Scenario: US3 - User navigates to Settings from rationale dialog
    Given I launch the app on Android
    And location permission was denied
    When I open the animal list screen
    And I tap Go to Settings
    Then I should be navigated to device Settings

  @us3
  Scenario: US3 - User cancels rationale dialog
    Given I launch the app on Android
    And location permission was denied
    When I open the animal list screen
    And I tap Cancel on the rationale dialog
    Then the app should continue without location

  @us3
  Scenario: US3 - User grants permission in Settings and returns
    Given I launch the app on Android
    And location permission was denied
    When I open the animal list screen
    And I tap Go to Settings
    And I grant permission in Settings and return to the app
    Then the app should fetch location and update listings

  # ============================================
  # User Story 4: Permission Rationale Before System Dialog (P4)
  # ============================================
  
  @us4
  Scenario: US4 - Educational rationale shown when shouldShowRationale is true
    Given I launch the app on Android
    And shouldShowRequestPermissionRationale returns true
    When I open the animal list screen
    Then I should see the educational rationale dialog

  @us4
  Scenario: US4 - User continues from educational rationale to system dialog
    Given I launch the app on Android
    And shouldShowRequestPermissionRationale returns true
    When I open the animal list screen
    And I tap Continue on the rationale dialog
    Then I should see the system permission dialog

  @us4
  Scenario: US4 - User dismisses educational rationale with Not Now
    Given I launch the app on Android
    And shouldShowRequestPermissionRationale returns true
    When I open the animal list screen
    And I tap Not Now on the rationale dialog
    Then the app should continue without location

  # ============================================
  # User Story 5: Dynamic Permission Change Handling (P5)
  # ============================================
  
  @us5
  Scenario: US5 - App reacts when permission granted while app is open
    Given I launch the app on Android
    And location permission was denied
    When I open the animal list screen
    And I change location permission while the app is open
    Then the app should react to the permission change
    And the app should fetch location and update listings

  @us5
  Scenario: US5 - App reacts when permission revoked while app is open
    Given I launch the app on Android
    And location permission is already granted
    When I open the animal list screen
    And I change location permission while the app is open
    Then the app should react to the permission change
    And the app should continue without location

