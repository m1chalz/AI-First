# Web E2E Testing with Playwright

End-to-end testing guide for PetSpot web application using Playwright and TypeScript.

## Overview

Web E2E tests validate the React TypeScript web application using Playwright with the Page Object Model pattern.

## Quick Start

```bash
# Install dependencies (from repo root)
npm install
npx playwright install

# Run all web tests
npm run test:e2e:web

# Run with interactive UI
npm run test:e2e:web:ui

# Run specific test
npx playwright test e2e-tests/web/specs/example.spec.ts
```

## Directory Structure

```
e2e-tests/web/
├── specs/          # Test specifications (*.spec.ts)
├── pages/          # Page Object Model classes
├── steps/          # Reusable step definitions
├── fixtures/       # Test data and fixtures
└── README.md       # This file
```

## Writing Tests

### 1. Create Page Object

Page Objects encapsulate page structure and interactions:

```typescript
// pages/PetListPage.ts
import { Page, Locator } from '@playwright/test';
import { navigateTo } from '../steps/urlSteps';
import { clickElement } from '../steps/mouseSteps';

export class PetListPage {
  readonly page: Page;
  readonly addButtonLocator: Locator;
  readonly petItemsLocator: Locator;

  constructor(page: Page) {
    this.page = page;
    // Use data-testid with pattern: {screen}.{element}.{action}
    this.addButtonLocator = page.getByTestId('petList.addButton.click');
    this.petItemsLocator = page.getByTestId('petList.items.display');
  }

  async navigate(): Promise<void> {
    await navigateTo(this.page, '/pets');
  }

  async clickAddButton(): Promise<void> {
    await clickElement(this.page, 'petList.addButton.click');
  }

  async getPetCount(): Promise<number> {
    return await this.petItemsLocator.count();
  }
}
```

### 2. Add Step Definitions (Optional)

For reusable actions, add to appropriate step files:

```typescript
// steps/urlSteps.ts - Navigation and page loading
// steps/elementSteps.ts - Element content and state  
// steps/mouseSteps.ts - Click and mouse interactions

// Example - adding to elementSteps.ts:
import { Page } from '@playwright/test';

export async function selectFromDropdown(
  page: Page,
  testId: string,
  value: string
): Promise<void> {
  await page.getByTestId(testId).selectOption(value);
}
```

### 3. Write Test Spec

Tests follow Given-When-Then structure:

```typescript
// specs/pet-list.spec.ts
import { test, expect } from '@playwright/test';
import { PetListPage } from '../pages/PetListPage';

test.describe('Pet List Feature', () => {
  let petListPage: PetListPage;

  test.beforeEach(async ({ page }) => {
    petListPage = new PetListPage(page);
  });

  test('should display pet list', async ({ page }) => {
    // Given - User navigates to pet list
    await petListPage.navigate();

    // When - Page loads
    const count = await petListPage.getPetCount();

    // Then - Pets are displayed
    expect(count).toBeGreaterThanOrEqual(0);
  });

  test('should add new pet', async ({ page }) => {
    // Given - User is on pet list page
    await petListPage.navigate();
    const initialCount = await petListPage.getPetCount();

    // When - User clicks add button
    await petListPage.clickAddButton();
    // ... fill form and submit ...

    // Then - New pet appears in list
    const newCount = await petListPage.getPetCount();
    expect(newCount).toBe(initialCount + 1);
  });
});
```

## Test Identifiers

All interactive elements MUST have `data-testid` attributes:

### Naming Pattern

`{screen}.{element}.{action}`

Examples:
- `petList.addButton.click`
- `petList.item.123` (for list items with ID)
- `petForm.nameInput.text`
- `petForm.submitButton.click`

### Adding to React Components

```tsx
// Good ✅
<button data-testid="petList.addButton.click">
  Add Pet
</button>

<input 
  data-testid="petForm.nameInput.text"
  type="text"
/>

<div data-testid={`petList.item.${pet.id}`}>
  {pet.name}
</div>

// Bad ❌
<button id="add-btn">Add</button>  // Don't use IDs
<button className="btn-primary">Add</button>  // Don't use classes
```

## Page Object Model Pattern

### Structure

```typescript
export class MyPage {
  // 1. Properties
  readonly page: Page;
  readonly locators: Record<string, Locator>;

  // 2. Constructor
  constructor(page: Page) {
    this.page = page;
    this.locators = {
      title: page.getByTestId('my.title.display'),
      button: page.getByTestId('my.button.click'),
    };
  }

  // 3. Navigation
  async navigate(): Promise<void> {
    await this.page.goto('/my-page');
  }

  // 4. Actions
  async clickButton(): Promise<void> {
    await this.locators.button.click();
  }

  // 5. Queries
  async getTitle(): Promise<string> {
    return await this.locators.title.textContent() || '';
  }

  // 6. Waits
  async waitForLoad(): Promise<void> {
    await this.locators.title.waitFor({ state: 'visible' });
  }
}
```

### Best Practices

1. ✅ One Page Object per page/component
2. ✅ Use locators with `data-testid`
3. ✅ Return promises with explicit types
4. ✅ Add JSDoc documentation
5. ✅ Use step definitions for common actions
6. ✅ Keep methods focused (single responsibility)
7. ✅ Avoid test assertions in Page Objects
8. ✅ Use TypeScript strict mode

## Configuration

### playwright.config.ts

Key settings for web tests:

```typescript
export default defineConfig({
  testDir: './e2e-tests/web/specs',
  baseURL: 'http://localhost:3000',
  
  use: {
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
    { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
    { name: 'webkit', use: { ...devices['Desktop Safari'] } },
  ],
});
```

## Running Tests

### Basic Commands

```bash
# All tests
npm run test:e2e:web

# Specific file
npx playwright test specs/example.spec.ts

# Specific test by name
npx playwright test -g "should display pet list"

# Specific browser
npx playwright test --project=chromium

# Headed mode (visible browser)
npx playwright test --headed

# Debug mode
npx playwright test --debug
```

### Interactive UI Mode

```bash
npm run test:e2e:web:ui
```

Features:
- Watch mode with auto-run
- Time travel debugging
- Network inspection
- Console logs
- Screenshots/videos

### Reporters

```bash
# HTML report (after test run)
npx playwright show-report

# List reporter (terminal)
npx playwright test --reporter=list

# JSON reporter
npx playwright test --reporter=json
```

## Debugging

### 1. UI Mode (Recommended)

```bash
npx playwright test --ui
```

### 2. Debug Mode

```bash
npx playwright test --debug
```

Opens Playwright Inspector with:
- Step-through debugging
- Element picker
- Console access

### 3. Trace Viewer

```bash
# Enable tracing in config
use: {
  trace: 'on-first-retry',
}

# View trace
npx playwright show-trace trace.zip
```

### 4. Screenshots & Videos

Automatically captured on failure (configured in `playwright.config.ts`):
- Screenshots: `test-results/`
- Videos: `test-results/`

### 5. Console Logs

```typescript
page.on('console', msg => console.log(msg.text()));
```

## Best Practices

### Test Independence

```typescript
// Good ✅
test.beforeEach(async ({ page }) => {
  await page.goto('/pets');
  // Setup fresh state
});

test.afterEach(async ({ page }) => {
  // Cleanup
});

// Bad ❌
// Sharing state between tests
```

### Waiting Strategies

```typescript
// Good ✅
await page.getByTestId('element').waitFor({ state: 'visible' });
await page.waitForLoadState('networkidle');

// Bad ❌
await page.waitForTimeout(5000);  // Arbitrary waits
```

### Error Handling

```typescript
// Good ✅
test('should handle error', async ({ page }) => {
  await page.goto('/pets');
  
  const errorMessage = page.getByTestId('error.message.display');
  await expect(errorMessage).toHaveText('Expected error');
});

// Bad ❌
// Catching and ignoring errors
```

### Assertions

```typescript
// Good ✅
await expect(page.getByTestId('title')).toHaveText('Welcome');
await expect(page.getByTestId('button')).toBeVisible();
await expect(page).toHaveURL(/.*pets/);

// Use soft assertions for multiple checks
await expect.soft(element1).toBeVisible();
await expect.soft(element2).toBeVisible();
```

## Fixtures

Create reusable test data:

```typescript
// fixtures/petData.ts
export const mockPets = [
  { id: '1', name: 'Max', type: 'Dog' },
  { id: '2', name: 'Luna', type: 'Cat' },
];

// Usage in test
import { mockPets } from '../fixtures/petData';

test('should display pets', async ({ page }) => {
  // Use fixture data
  await page.route('**/api/pets', route => {
    route.fulfill({ json: mockPets });
  });
});
```

## Troubleshooting

### Tests Timing Out

```typescript
// Increase timeout per test
test('slow test', async ({ page }) => {
  test.setTimeout(60000);  // 60 seconds
});

// Or globally in config
timeout: 30000,
```

### Element Not Found

1. Check `data-testid` in React component
2. Use Playwright Inspector to verify selector
3. Add explicit wait:
   ```typescript
   await page.getByTestId('element').waitFor();
   ```

### Flaky Tests

1. Avoid arbitrary `waitForTimeout`
2. Use proper wait conditions
3. Enable retries in config:
   ```typescript
   retries: 2,
   ```

### Port Already in Use

```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9
```

## CI/CD Integration

### GitHub Actions

```yaml
- name: Run Playwright tests
  run: npm run test:e2e:web
  
- uses: actions/upload-artifact@v3
  if: always()
  with:
    name: playwright-report
    path: playwright-report/
```

## Resources

- [Playwright Documentation](https://playwright.dev/)
- [Page Object Model Guide](https://playwright.dev/docs/pom)
- [Best Practices](https://playwright.dev/docs/best-practices)
- [Main E2E README](../README.md)

---

**Version**: 1.0.0  
**Last Updated**: 2024-11-18

