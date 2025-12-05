# Report Missing Pet E2E Tests
# Spec: 052-e2e-report-missing
# Platforms: Web, iOS, Android
#
# TODO: Implement unified report missing pet flow tests
# See specs/052-e2e-report-missing/spec.md for requirements

@reportMissing
Feature: Report Missing Pet
  As a user
  I want to report a missing pet
  So that others can help me find my lost animal

  Background:
    Given the application is running

  # ========================================
  # Test 1: Start report flow from animal list
  # ========================================

  @web @ios @android @smoke @pending
  Scenario: User starts report missing pet flow
    # Action - Navigate to list and tap report button
    When I navigate to the pet list page
    And I tap the "Report a Missing Animal" button
    
    # Verification - Report flow started (chip number screen)
    Then I should see the chip number screen

  # ========================================
  # Additional scenarios to be added per spec 052
  # ========================================

