/**
 * TypeScript contracts for NavigationBar component
 * Feature: 057-web-navigation
 * 
 * This file defines the component interfaces, props, and configuration types
 * for the web navigation bar feature.
 */

import { ComponentType } from 'react';

/**
 * Configuration for a single navigation item
 */
export interface NavigationItemConfig {
  /**
   * Unique identifier for the navigation item
   * @example "home", "lostPet", "foundPet"
   */
  id: string;

  /**
   * Display label shown to users
   * @example "Home", "Lost Pet", "Contact Us"
   */
  label: string;

  /**
   * Icon component from react-icons (already installed v5.5.0)
   * Must accept className and size props for styling
   */
  icon: ComponentType<{ className?: string; size?: number }>;

  /**
   * Destination URL path (absolute path starting with /)
   * @example "/", "/lost-pets", "/account"
   */
  path: string;

  /**
   * Test identifier for E2E and component tests
   * Format: "navigation.{id}.link"
   * @example "navigation.home.link"
   */
  testId: string;
}

/**
 * Props for the NavigationBar component
 */
export interface NavigationBarProps {
  /**
   * Array of navigation items to render
   * Order in array determines display order (left to right)
   */
  items: NavigationItemConfig[];

  /**
   * Path to PetSpot logo image
   * Can be relative path or absolute URL
   * @example "/logo.svg", "https://example.com/logo.png"
   */
  logoPath: string;

  /**
   * Alt text for logo image (for accessibility)
   * @example "PetSpot"
   */
  logoAlt: string;

  /**
   * Optional CSS class name for custom styling
   */
  className?: string;
}

/**
 * Props for individual NavigationItem component
 * (Internal component, not exposed in public API)
 */
export interface NavigationItemProps {
  /**
   * Navigation item configuration
   */
  item: NavigationItemConfig;

  /**
   * Optional CSS class name for custom styling
   */
  className?: string;
}

/**
 * CSS Module class names interface
 * Used for type-safe CSS Module imports
 */
export interface NavigationBarStyles {
  navigationBar: string;
  logo: string;
  logoLink: string;
  navigationItems: string;
  navigationItem: string;
  navigationItemActive: string;
  icon: string;
  label: string;
}

