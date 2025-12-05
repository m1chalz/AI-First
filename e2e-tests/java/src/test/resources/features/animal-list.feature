@animalList
Feature: Animal List
  As a user
  I want to view a list of missing and found animals
  So that I can find my lost pet or help others find theirs

  Background:
    Given the application is running

  # ========================================
  # Test 1: Smoke test - Animal list + Report button + location filtering
  # WHAT IT TESTS:
  #   - Page loads successfully
  #   - Announcements are displayed (nearby only on mobile when bug fixed)
  #   - "Report a Missing Animal" button is visible
  #   - Button remains visible while scrolling (FR-003)
  #   - Location filtering - far away announcements not visible (soft assert)
  # ========================================

  @web @ios @android @smoke @location
  Scenario: User views animal list with all UI elements
    # Setup - Create test data at Wroclaw + far away location
    Given I create a test announcement at coordinates "51.1" "17.0" with name "E2E-TestDog"
    Given I create a test announcement at coordinates "40.7" "-74.0" with name "E2E-FarAwayPet"
    
    # Mobile: Restart app and set GPS to Wroclaw for location filtering
    When I restart the app
    And I set device location to "51.1" "17.0"
    
    # Action - Navigate to animal list
    When I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Button is visible before scrolling
    And I should see the "Report a Missing Animal" button
    
    # Verification - Scroll to find nearby announcement
    When I scroll until I see the announcement for "E2E-TestDog"
    Then I should see the announcement for "E2E-TestDog"
    
    # Verification - Button remains visible after scrolling (FR-003 from spec 005)
    And I should see the "Report a Missing Animal" button
    
    # Soft assertion - location filtering (logs warning if fails, doesn't stop test)
    # BUG: Android/iOS apps don't send location to API - this will fail until fixed
    And I should NOT see the announcement for "E2E-FarAwayPet" (soft assert)
    
    # Cleanup
    And I delete all test announcements via API

  # ========================================
  # Test 2: Full list without location + rationale popup (mobile only)
  # WHAT IT TESTS:
  #   - Without location permission, ALL animals are visible
  #   - After app restart, rationale popup appears (FR-015)
  #   - Popup has Settings button
  #   - After dismissing popup, list still works
  # 
  # BLOCKED: 
  #   - Android: rationale popup bug (doesn't appear after restart)
  #   - Web: not applicable (no permission dialogs)
  # ========================================

  @web @ios @android @pending-android @pending-web
  Scenario: User sees full animal list without location and rationale popup on restart
    # Setup - Create test data (will be visible because no location filtering)
    Given I create a test announcement via API with name "E2E-NoLocationDog" and species "DOG"
    
    # First launch - dismiss initial rationale popup if shown
    When I dismiss location rationale dialog if present
    And I navigate to the pet list page
    Then the page should load successfully
    
    # Verification - Should see test data (full list, no location filtering)
    When I scroll until I see the announcement for "E2E-NoLocationDog"
    Then I should see the announcement for "E2E-NoLocationDog"
    
    # Restart app - rationale popup should appear (FR-015: once per session)
    When I restart the app
    Then I should see location rationale dialog
    And the rationale dialog should have Settings button
    
    # Dismiss and verify list still works
    When I dismiss location rationale dialog
    And I navigate to the pet list page
    Then the page should load successfully
    
    # Cleanup
    And I delete the test announcement via API
    
  # ========================================
  # Test 3: Empty state - no animals in area
  # WHAT IT TESTS:
  #   - When GPS points to location without announcements
  #   - Empty state message is displayed
  # 
  # BLOCKED: 
  #   - Android/iOS: apps don't send location to API
  #   - Web: location filtering not implemented (spec 053)
  # ========================================
  
  @web @ios @android @location @pending-android @pending-ios @pending-web
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

