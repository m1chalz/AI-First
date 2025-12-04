import { WebdriverIOElement } from '@wdio/types';

export class SummaryScreen {
    // Locators (iOS accessibility identifiers)
    private get title(): Promise<WebdriverIOElement> {
        return $('~summary.title');
    }

    private get bodyParagraph1(): Promise<WebdriverIOElement> {
        return $('~summary.bodyParagraph1');
    }

    private get bodyParagraph2(): Promise<WebdriverIOElement> {
        return $('~summary.bodyParagraph2');
    }

    private get password(): Promise<WebdriverIOElement> {
        return $('~summary.password');
    }

    private get toast(): Promise<WebdriverIOElement> {
        return $('~summary.toast');
    }

    private get closeButton(): Promise<WebdriverIOElement> {
        return $('~summary.closeButton');
    }

    // Actions
    async waitForScreenToLoad(): Promise<void> {
        await (await this.title).waitForDisplayed({ timeout: 5000 });
    }

    async getTitleText(): Promise<string> {
        return await (await this.title).getText();
    }

    async getBodyParagraph1Text(): Promise<string> {
        return await (await this.bodyParagraph1).getText();
    }

    async getBodyParagraph2Text(): Promise<string> {
        return await (await this.bodyParagraph2).getText();
    }

    async getPasswordText(): Promise<string> {
        return await (await this.password).getText();
    }

    async tapPassword(): Promise<void> {
        await (await this.password).click();
    }

    async tapCloseButton(): Promise<void> {
        await (await this.closeButton).click();
    }

    async isPasswordDisplayed(): Promise<boolean> {
        return await (await this.password).isDisplayed();
    }

    async isToastDisplayed(): Promise<boolean> {
        try {
            return await (await this.toast).isDisplayed();
        } catch {
            return false;
        }
    }

    async isCloseButtonDisplayed(): Promise<boolean> {
        return await (await this.closeButton).isDisplayed();
    }

    async getToastText(): Promise<string> {
        return await (await this.toast).getText();
    }
}
