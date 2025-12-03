/**
 * Screen Object Model for Summary screen (Report Created Confirmation)
 * Step 5 of "Report a Missing Animal" flow
 */
export class SummaryScreen {
    // MARK: - Locators (iOS accessibility identifiers)
    
    private get title() {
        return $('~summary.title');
    }

    private get bodyParagraph1() {
        return $('~summary.bodyParagraph1');
    }

    private get bodyParagraph2() {
        return $('~summary.bodyParagraph2');
    }

    private get password() {
        return $('~summary.password');
    }

    private get toast() {
        return $('~summary.toast');
    }

    private get closeButton() {
        return $('~summary.closeButton');
    }

    // MARK: - Actions

    async waitForScreenToLoad(): Promise<void> {
        await this.title.waitForDisplayed({ timeout: 5000 });
    }

    async getTitleText(): Promise<string> {
        return await this.title.getText();
    }

    async getBodyParagraph1Text(): Promise<string> {
        return await this.bodyParagraph1.getText();
    }

    async getBodyParagraph2Text(): Promise<string> {
        return await this.bodyParagraph2.getText();
    }

    async getPasswordText(): Promise<string> {
        return await this.password.getText();
    }

    async tapPassword(): Promise<void> {
        await this.password.click();
    }

    async tapCloseButton(): Promise<void> {
        await this.closeButton.click();
    }

    async isPasswordDisplayed(): Promise<boolean> {
        return await this.password.isDisplayed();
    }

    async isToastDisplayed(): Promise<boolean> {
        return await this.toast.isDisplayed();
    }

    async getToastText(): Promise<string> {
        return await this.toast.getText();
    }

    async isCloseButtonDisplayed(): Promise<boolean> {
        return await this.closeButton.isDisplayed();
    }
}

