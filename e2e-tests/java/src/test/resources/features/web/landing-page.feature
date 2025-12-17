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

  # User Story 3 - Browse Recently Lost Pets from Landing Page (P2)
  @P2 @US3
  Scenario: Recent pets section displays with heading and View all link
    Then landing page should display the recent pets section
    And recent pets section should display the heading
    And recent pets section should display the View all link

  @P2 @US3
  Scenario: Recent pets section displays up to 5 MISSING pets
    Then recent pets section should display at most 5 pet cards

  @P2 @US3
  Scenario: View all link navigates to lost pets page
    When user clicks on View all link in recent pets section
    Then user should be navigated to the lost pets page

  # User Story 4 - Access Footer Information (P3)
  @P3 @US4
  Scenario: Footer displays branding with logo and tagline
    Then footer should display the logo
    And footer should display the tagline

  @P3 @US4
  Scenario: Footer displays quick links
    Then footer should display "Report Lost Pet" quick link
    And footer should display "Report Found Pet" quick link
    And footer should display "Search Database" quick link

  @P3 @US4
  Scenario: Report Lost Pet link navigates to report missing page
    When user clicks on "Report Lost Pet" quick link in footer
    Then user should be navigated to the report missing page

  @P3 @US4
  Scenario: Placeholder quick links are not functional
    Then "Report Found Pet" quick link should be a placeholder
    And "Search Database" quick link should be a placeholder

  @P3 @US4
  Scenario: Footer displays contact information
    Then footer should display email contact
    And footer should display phone contact
    And footer should display address contact

  @P3 @US4
  Scenario: Footer displays copyright notice
    Then footer should display copyright notice

  @P3 @US4
  Scenario: Footer displays legal links
    Then footer should display "Privacy Policy" legal link
    And footer should display "Terms of Service" legal link
    And footer should display "Cookie Policy" legal link

