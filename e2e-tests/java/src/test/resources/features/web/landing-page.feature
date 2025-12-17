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
  @P1 @US2
  Scenario: Feature cards display in correct order
    Then feature cards should be displayed in the following order:
      | Search Database |
      | Report Missing  |
      | Found a Pet     |
      | Location Based  |

  @P1 @US2
  Scenario Outline: Feature card displays correct content
    Then feature card "<id>" should display title "<title>"
    And feature card "<id>" should display a description

    Examples:
      | id       | title           |
      | search   | Search Database |
      | report   | Report Missing  |
      | found    | Found a Pet     |
      | location | Location Based  |

  @P1 @US2
  Scenario: Feature cards have correct icon colors
    Then feature card "search" should have blue icon color
    And feature card "report" should have red icon color
    And feature card "found" should have green icon color
    And feature card "location" should have purple icon color

  @P1 @US2
  Scenario: Feature cards are not clickable
    Then feature cards should not be clickable

