@web @061-landing-page
Feature: Web Landing Page
  As a PetSpot user
  I want to view the landing page
  So that I can understand the portal features and browse recent lost pets

  Background:
    Given user navigates to the landing page

  # User Story 1 - View Landing Page on Web App Launch (P1, MVP)
  @P1 @US1
  Scenario: Landing page displays all main sections
    Then landing page should display the hero section
    And landing page should display the footer

  @P1 @US1
  Scenario: Hero section displays heading and description
    Then hero section should display the main heading
    And hero section should display the description text

  @P1 @US1
  Scenario: Hero section displays four feature cards
    Then hero section should display 4 feature cards

  @P1 @US1
  Scenario: Footer displays all columns
    Then footer should display the branding column
    And footer should display the quick links column
    And footer should display the contact information column

  # User Story 2 - Understand Portal Features via Feature Cards (P1)
  # Note: Additional scenarios will be added in Phase 4

