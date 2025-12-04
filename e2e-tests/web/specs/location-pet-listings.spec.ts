import { test, expect, BrowserContext } from '@playwright/test';

test.describe('Location-based Pet Listings', () => {
  
  test.describe('User Story 1: Location-Aware Content for Users with Granted Permission', () => {
    
    test('should fetch and display pets with location filtering when permission is granted', async ({ browser }) => {
      // Given - Create context with geolocation permission granted
      const context = await browser.newContext({
        geolocation: { latitude: 52.229676, longitude: 21.012229 },
        permissions: ['geolocation'],
      });
      const page = await context.newPage();
      
      // When - Navigate to the application
      await page.goto('http://localhost:5173');
      
      // Then - Should show loading state initially
      await expect(page.getByText(/loading animals/i)).toBeVisible();
      
      // Then - Should fetch animals and display them
      await expect(page.getByTestId('animalList.list')).toBeVisible({ timeout: 10000 });
      
      // Then - Should NOT show location banner (permission is granted)
      await expect(page.getByTestId('petList.locationBanner')).not.toBeVisible();
      
      await context.close();
    });
    
    test('should handle geolocation timeout gracefully', async ({ browser }) => {
      // Given - Create context that will timeout
      const context = await browser.newContext({
        permissions: ['geolocation'],
      });
      const page = await context.newPage();
      
      // Mock geolocation to timeout
      await context.grantPermissions(['geolocation']);
      await page.addInitScript(() => {
        // Override geolocation to timeout
        const originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
        navigator.geolocation.getCurrentPosition = function(success, error, options) {
          setTimeout(() => {
            if (error) {
              error({
                code: 3,
                message: 'Timeout',
                PERMISSION_DENIED: 1,
                POSITION_UNAVAILABLE: 2,
                TIMEOUT: 3,
              } as GeolocationPositionError);
            }
          }, 100);
        };
      });
      
      // When - Navigate to the application
      await page.goto('http://localhost:5173');
      
      // Then - Should still display animals (fallback to no location)
      await expect(page.getByTestId('animalList.list')).toBeVisible({ timeout: 10000 });
      
      // Then - Should NOT show location banner (timeout is not permission denial)
      await expect(page.getByTestId('petList.locationBanner')).not.toBeVisible();
      
      await context.close();
    });
    
  });
  
  test.describe('User Story 2: First-Time Location Permission Request', () => {
    
    test('should show loading state while waiting for permission', async ({ page }) => {
      // Given - Fresh page without permissions set
      
      // When - Navigate to the application
      await page.goto('http://localhost:5173');
      
      // Then - Should show loading state
      await expect(page.getByText(/loading animals/i)).toBeVisible();
    });
    
  });
  
  test.describe('User Story 3: Recovery Path for Blocked Permissions', () => {
    
    test('should show LocationBanner when permission is blocked', async ({ browser }) => {
      // Given - Create context with geolocation permission denied
      const context = await browser.newContext();
      const page = await context.newPage();
      
      // Deny geolocation permission
      await context.grantPermissions([], { origin: 'http://localhost:5173' });
      await page.addInitScript(() => {
        // Override geolocation to return permission denied
        const originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
        navigator.geolocation.getCurrentPosition = function(success, error, options) {
          if (error) {
            error({
              code: 1,
              message: 'User denied Geolocation',
              PERMISSION_DENIED: 1,
              POSITION_UNAVAILABLE: 2,
              TIMEOUT: 3,
            } as GeolocationPositionError);
          }
        };
        
        // Mock permissions API to return denied
        const originalQuery = navigator.permissions.query;
        navigator.permissions.query = function(descriptor: any) {
          return Promise.resolve({
            state: 'denied',
            onchange: null,
          } as PermissionStatus);
        };
      });
      
      // When - Navigate to the application
      await page.goto('http://localhost:5173');
      
      // Then - Should show location banner
      await expect(page.getByTestId('petList.locationBanner')).toBeVisible({ timeout: 10000 });
      await expect(page.getByText(/see pets near you/i)).toBeVisible();
      await expect(page.getByText(/browser settings/i)).toBeVisible();
      
      // Then - Should still show animals list (non-blocking)
      await expect(page.getByTestId('animalList.list')).toBeVisible();
      
      await context.close();
    });
    
    test('should close LocationBanner when X button is clicked', async ({ browser }) => {
      // Given - Context with denied permission and banner visible
      const context = await browser.newContext();
      const page = await context.newPage();
      
      await page.addInitScript(() => {
        const originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
        navigator.geolocation.getCurrentPosition = function(success, error, options) {
          if (error) {
            error({
              code: 1,
              message: 'User denied Geolocation',
              PERMISSION_DENIED: 1,
              POSITION_UNAVAILABLE: 2,
              TIMEOUT: 3,
            } as GeolocationPositionError);
          }
        };
        
        const originalQuery = navigator.permissions.query;
        navigator.permissions.query = function(descriptor: any) {
          return Promise.resolve({
            state: 'denied',
            onchange: null,
          } as PermissionStatus);
        };
      });
      
      await page.goto('http://localhost:5173');
      await expect(page.getByTestId('petList.locationBanner')).toBeVisible({ timeout: 10000 });
      
      // When - Click the close button
      await page.getByTestId('petList.locationBanner.close').click();
      
      // Then - Banner should be hidden
      await expect(page.getByTestId('petList.locationBanner')).not.toBeVisible();
      
      // Then - Animals list should still be visible
      await expect(page.getByTestId('animalList.list')).toBeVisible();
      
      await context.close();
    });
    
    test('should show banner again after page reload when permission still denied', async ({ browser }) => {
      // Given - Context with denied permission
      const context = await browser.newContext();
      const page = await context.newPage();
      
      await page.addInitScript(() => {
        const originalGetCurrentPosition = navigator.geolocation.getCurrentPosition;
        navigator.geolocation.getCurrentPosition = function(success, error, options) {
          if (error) {
            error({
              code: 1,
              message: 'User denied Geolocation',
              PERMISSION_DENIED: 1,
              POSITION_UNAVAILABLE: 2,
              TIMEOUT: 3,
            } as GeolocationPositionError);
          }
        };
        
        const originalQuery = navigator.permissions.query;
        navigator.permissions.query = function(descriptor: any) {
          return Promise.resolve({
            state: 'denied',
            onchange: null,
          } as PermissionStatus);
        };
      });
      
      await page.goto('http://localhost:5173');
      await expect(page.getByTestId('petList.locationBanner')).toBeVisible({ timeout: 10000 });
      
      // When - Close banner and reload page
      await page.getByTestId('petList.locationBanner.close').click();
      await expect(page.getByTestId('petList.locationBanner')).not.toBeVisible();
      
      await page.reload();
      
      // Then - Banner should appear again (no persistence)
      await expect(page.getByTestId('petList.locationBanner')).toBeVisible({ timeout: 10000 });
      
      await context.close();
    });
    
  });
  
});

