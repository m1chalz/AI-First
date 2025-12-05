# Pet Details E2E Tests
# Spec: 051-e2e-pet-details
# Platforms: Web, iOS, Android
#
# TODO: Implement unified pet details tests
# See specs/051-e2e-pet-details/spec.md for requirements

@petDetails
Feature: Pet Details
  As a user
  I want to view detailed information about a pet
  So that I can contact the owner and verify pet identity

  Background:
    Given the application is running

  # ========================================
  # Test 1: Navigate from list to details
  # ========================================

  @web @ios @android @smoke @pending
  Scenario: User taps animal card to view details
    # Setup - Create test data via API
    Given I create a test announcement via API with name "E2E-DetailsPet" and species "CAT"
    
    # Action - Navigate to list and tap on announcement
    When I navigate to the pet list page
    And I tap on the announcement for "E2E-DetailsPet"
    
    # Verification - Pet details are displayed
    Then I should see the pet details screen
    And I should see pet name "E2E-DetailsPet"
    And I should see pet species "CAT"
    
    # Cleanup
    And I delete the test announcement via API

  # ========================================
  # Additional scenarios to be added per spec 051
  # ========================================

