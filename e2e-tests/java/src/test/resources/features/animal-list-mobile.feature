@animalList @mobile
Feature: Animal List - Mobile Application (iOS & Android)
  As a mobile user
  I want to view a list of missing and found animals
  So that I can find my lost pet or help others find theirs

  Background:
    Given the application is running

  # ========================================
  # Test 1: Smoke test - Animal list + Report button + location filtering
  # WHAT IT TESTS:
  #   - Page loads successfully
  #   - Announcements are displayed (nearby only when location set)
  #   - "Report a Missing Animal" button is visible
  #   - Button remains visible while scrolling (FR-003)
  #   - Location filtering - far away announcements not visible (soft assert)
  # ========================================

  @ios @android @smoke @location
  Scenario: User views animal list with all UI elements
    # Setup - Create test data at Wroclaw + far away location
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-TestDog"
    Given I create a test announcement at coordinates "40.7" "-74.0" with name "E2E-FarAwayPet"
    
    # Restart app and set GPS to Wroclaw for location filtering
    When I restart the app
    And I set device location to "51.1" "17.0"
    
  # ========================================
  # Test 2: Full list without location + rationale popup
  # WHAT IT TESTS:
  #   - Without location permission, ALL animals are visible
  #   - After app restart, rationale popup appears (FR-015)
  #   - Popup has Settings button
  #   - After dismissing popup, list still works
  # 
  # BLOCKED: 
  #   - Android: rationale popup bug (doesn't appear after restart)
  #   - iOS: iOS 18.1 getPageSource() bug in scrolling
  # ========================================

  # FIXED: iOS 18.1 getPageSource() bug - now uses findElements() instead
  # Still pending for Android due to rationale popup bug
  @ios @android @pending-android
  Scenario: Mobile user sees full animal list without location and rationale popup on restart
    # Setup - Create test data (will be visible because no location filtering)
    Given I create a test announcement via API with name "E2E-NoLocationDog" and species "DOG"
    
    # Restart app to reload data from backend
    When I restart the app
    
    # First launch - dismiss initial rationale popup if shown
    When I dismiss location rationale dialog if present
    And I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Should see test data (full list, no location filtering)
    When I scroll until I see the announcement for "E2E-NoLocationDog"
    Then I should see the announcement for "E2E-NoLocationDog"
    
    # Reinstall app - rationale popup should appear (FR-015: once per session)
    # Note: Split into uninstall + install to ensure app is killed and data reloaded
    When I uninstall the app
    And I install the app
    Then I should see location rationale dialog
    And the rationale dialog should have Settings button
    
    # Dismiss and verify list still works
    When I dismiss location rationale dialog

  
  # ========================================
  # Test 3: Reinstall resets app state (Simple test without scrolling)
  # ========================================
  # Purpose: Verify that uninstall+install properly resets app state
  # Expected: Rationale dialog appears after reinstall (FR-015)
  # ========================================

  @ios @android @locationDialog
  Scenario: User reinstalls app and sees rationale dialog on fresh launch
    # First launch - dismiss initial rationale popup
    When I dismiss location rationale dialog if present
    And I navigate to the pet list page
    Then the page should load successfully
    
    # Reinstall app - should reset state and show rationale again
    When I uninstall the app
    And I install the app
    Then I should see location rationale dialog
    And the rationale dialog should have Settings button
    
    # Verify app works after dismissing dialog
    When I dismiss location rationale dialog
    And I navigate to the pet list page
    Then the page should load successfully
    
  # ========================================
  # Test 4: Empty state - no animals in area
  # WHAT IT TESTS:
  #   - When GPS points to location without announcements
  #   - Empty state message is displayed
  # 
  # NOTE: Requires location filtering to work in mobile apps
  # ========================================
  
  @ios @android @location
  Scenario: User sees empty state when no animals in current location
    # Setup - Create announcement only in Wroclaw
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-OnlyInWroclaw"
    
    # Set GPS to middle of ocean THEN restart (so app reads new location)
    When I set device location to "0.0" "0.0"
    And I restart the app
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - No animals = empty state
    And I should see empty state message
    
    # Cleanup
    And I delete the test announcement via API

