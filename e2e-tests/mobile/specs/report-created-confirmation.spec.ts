import { SummaryScreen } from '../screens/SummaryScreen';

describe('Report Created Confirmation (Summary Screen)', () => {
    const summaryScreen = new SummaryScreen();

    beforeEach(async () => {
        // Setup: Navigate through report flow to reach summary screen
        // TODO: Implement createMissingAnimalReport() helper when available
        // await createMissingAnimalReport();
    });

    // T017 - US1: Confirmation messaging display
    it('should display confirmation title and body paragraphs', async () => {
        // Given - report submission completed
        await summaryScreen.waitForScreenToLoad();

        // When - screen is displayed
        const title = await summaryScreen.getTitleText();
        const bodyParagraph1 = await summaryScreen.getBodyParagraph1Text();
        const bodyParagraph2 = await summaryScreen.getBodyParagraph2Text();

        // Then - confirmation messaging is visible
        expect(title).toBe('Report created');
        expect(bodyParagraph1).toContain('Your report has been created');
        expect(bodyParagraph2).toContain('use the code provided below');
    });

    // T018 - US2: Password display
    it('should display management password', async () => {
        // Given - summary screen with password
        await summaryScreen.waitForScreenToLoad();

        // When - password element is checked
        const passwordDisplayed = await summaryScreen.isPasswordDisplayed();
        const passwordText = await summaryScreen.getPasswordText();

        // Then - password is visible and non-empty
        expect(passwordDisplayed).toBe(true);
        expect(passwordText).not.toBe('');
    });

    // T019 - US2: Clipboard copy with toast
    it('should show toast when password is tapped', async () => {
        // Given - summary screen with password
        await summaryScreen.waitForScreenToLoad();

        // When - user taps password
        await summaryScreen.tapPassword();

        // Wait a moment for toast to appear
        await browser.pause(500);

        // Then - toast appears with confirmation message
        const toastDisplayed = await summaryScreen.isToastDisplayed();
        expect(toastDisplayed).toBe(true);

        if (toastDisplayed) {
            const toastText = await summaryScreen.getToastText();
            expect(toastText).toContain('copied');
        }
    });

    // T042 - US3: Close button dismisses flow
    it('should dismiss flow when Close button is tapped', async () => {
        // Given - summary screen displayed
        await summaryScreen.waitForScreenToLoad();
        const closeButtonDisplayed = await summaryScreen.isCloseButtonDisplayed();
        expect(closeButtonDisplayed).toBe(true);

        // When - user taps Close button
        await summaryScreen.tapCloseButton();

        // Wait for flow dismissal
        await browser.pause(1000);

        // Then - flow is dismissed (summary screen no longer visible)
        // Note: In real test, verify home/dashboard screen is displayed
        // TODO: Add home screen verification when helper is available
    });
});
