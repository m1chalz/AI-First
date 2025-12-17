/**
 * TypeScript contracts for React Router configuration
 * Feature: 057-web-navigation
 * 
 * This file defines the route structure and configuration types
 * for the web application navigation.
 */

import { ComponentType } from 'react';

/**
 * Route configuration for the application
 */
export interface RouteConfig {
  /**
   * URL path for the route
   * @example "/", "/lost-pets", "/account"
   */
  path: string;

  /**
   * React component to render for this route
   */
  element: ComponentType;

  /**
   * Whether this route requires authentication
   * @default false
   */
  requiresAuth?: boolean;

  /**
   * Page title for browser tab and SEO
   * @example "Home - PetSpot", "Lost Pets - PetSpot"
   */
  title?: string;
}

/**
 * Application route paths (for type-safe route references)
 */
export const ROUTES = {
  HOME: '/',
  LOST_PETS: '/lost-pets',
  FOUND_PETS: '/found-pets',
  CONTACT: '/contact',
  ACCOUNT: '/account',
} as const;

/**
 * Type for route path values
 */
export type RoutePath = typeof ROUTES[keyof typeof ROUTES];

/**
 * Navigation configuration (links routes to navigation items)
 */
export interface NavigationConfig {
  /**
   * Array of route configurations
   */
  routes: RouteConfig[];
}

