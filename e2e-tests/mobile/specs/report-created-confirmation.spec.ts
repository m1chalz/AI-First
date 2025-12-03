import { SummaryScreen } from '../screens/SummaryScreen';

/**
 * E2E tests for Report Created Confirmation screen (Summary - Step 5)
 * Tests User Stories 1, 2, 3 from spec 044-ios-report-created-screen
 */
describe('Report Created Confirmation (Summary Screen)', () => {
    const summaryScreen = new SummaryScreen();

    beforeEach(async () => {
        // Setup: Navigate through report flow to reach summary screen
        // TODO: Implement helper to create missing animal report and reach summary
        // await createMissingAnimalReport();
    });

    // MARK: - User Story 1: Confirmation Messaging

    it('should display confirmation title and body paragraphs', async () => {
        // Given: Report submission completed
        await summaryScreen.waitForScreenToLoad();

        // When: Screen is displayed
        const title = await summaryScreen.getTitleText();
        const paragraph1 = await summaryScreen.getBodyParagraph1Text();
        const paragraph2 = await summaryScreen.getBodyParagraph2Text();

        // Then: Confirmation messaging is visible
        expect(title).toBe('Report created');
        expect(paragraph1).toContain('Your report has been created');
        expect(paragraph2).toContain('use the code provided below');
    });

    // MARK: - User Story 2: Password Display

    it('should display management password', async () => {
        // Given: Summary screen with password
        await summaryScreen.waitForScreenToLoad();

        // When: Screen is displayed
        const passwordDisplayed = await summaryScreen.isPasswordDisplayed();
        const passwordText = await summaryScreen.getPasswordText();

        // Then: Password is visible and non-empty
        expect(passwordDisplayed).toBe(true);
        expect(passwordText).not.toBe('');
        expect(passwordText.length).toBeGreaterThan(0);
    });

    // MARK: - User Story 2: Clipboard Copy

    it('should show toast when password is tapped', async () => {
        // Given: Summary screen with password
        await summaryScreen.waitForScreenToLoad();

        // When: User taps password
        await summaryScreen.tapPassword();

        // Wait for toast animation
        await driver.pause(500);

        // Then: Toast appears with confirmation message
        const toastDisplayed = await summaryScreen.isToastDisplayed();
        expect(toastDisplayed).toBe(true);

        // Note: Actual clipboard verification requires device-specific APIs
        // This test verifies the UI feedback (toast) appears
    });

    // MARK: - User Story 3: Close Button

    it('should dismiss flow when Close button is tapped', async () => {
        // Given: Summary screen displayed
        await summaryScreen.waitForScreenToLoad();

        // When: User taps Close button
        await summaryScreen.tapCloseButton();

        // Then: Flow is dismissed (verify home screen or dashboard)
        // TODO: Implement helper to verify home/dashboard screen displayed
        // await verifyHomeScreenDisplayed();
        
        // For now, verify summary screen is no longer displayed
        await driver.pause(1000);
        const titleDisplayed = await summaryScreen.title.isDisplayed();
        expect(titleDisplayed).toBe(false);
    });
});

