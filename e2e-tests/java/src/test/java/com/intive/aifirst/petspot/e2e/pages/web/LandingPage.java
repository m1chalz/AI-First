package com.intive.aifirst.petspot.e2e.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LandingPage {

    private final WebDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;

    // Hero Section
    @FindBy(xpath = "//*[@data-testid='landing.heroSection']")
    private WebElement heroSection;

    @FindBy(xpath = "//*[@data-testid='landing.hero.heading']")
    private WebElement heroHeading;

    // Footer
    @FindBy(xpath = "//*[@data-testid='landing.footer']")
    private WebElement footer;

    // Locators
    private final By heroSectionLocator = By.xpath("//*[@data-testid='landing.heroSection']");
    private final By featureCardLocator = By.xpath("//*[contains(@data-testid, 'landing.hero.featureCard.')]");
    private final By footerLocator = By.xpath("//*[@data-testid='landing.footer']");

    public LandingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(heroSectionLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Hero Section Methods
    public boolean isHeroSectionDisplayed() {
        try {
            return heroSection.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isHeroHeadingDisplayed() {
        try {
            return heroHeading.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeroHeadingText() {
        try {
            return heroHeading.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isHeroDescriptionDisplayed() {
        try {
            WebElement description = heroSection.findElement(By.tagName("p"));
            return description != null && description.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getFeatureCardCount() {
        try {
            List<WebElement> cards = driver.findElements(featureCardLocator);
            return cards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<WebElement> getFeatureCards() {
        return driver.findElements(featureCardLocator);
    }

    public WebElement getFeatureCardById(String id) {
        By locator = By.xpath("//*[@data-testid='landing.hero.featureCard." + id + "']");
        try {
            return driver.findElement(locator);
        } catch (Exception e) {
            return null;
        }
    }

    public String getFeatureCardTitle(String id) {
        WebElement card = getFeatureCardById(id);
        if (card == null) return "";
        try {
            WebElement title = card.findElement(By.tagName("h3"));
            return title.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean hasFeatureCardDescription(String id) {
        WebElement card = getFeatureCardById(id);
        if (card == null) return false;
        try {
            WebElement description = card.findElement(By.tagName("p"));
            return description != null && !description.getText().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getFeatureCardIconColor(String id) {
        WebElement card = getFeatureCardById(id);
        if (card == null) return "";
        try {
            WebElement iconContainer = card.findElement(By.xpath(".//*[contains(@class, 'iconContainer')]"));
            String style = iconContainer.getAttribute("style");
            if (style != null && style.contains("background-color")) {
                return style;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getFeatureCardTitles() {
        List<WebElement> cards = getFeatureCards();
        return cards.stream()
            .map(card -> {
                try {
                    return card.findElement(By.tagName("h3")).getText();
                } catch (Exception e) {
                    return "";
                }
            })
            .toList();
    }

    public boolean areFeatureCardsClickable() {
        List<WebElement> cards = getFeatureCards();
        for (WebElement card : cards) {
            String tagName = card.getTagName().toLowerCase();
            if (tagName.equals("a") || tagName.equals("button")) {
                return true;
            }
            String cursor = card.getCssValue("cursor");
            if ("pointer".equals(cursor)) {
                return true;
            }
            String onclick = card.getAttribute("onclick");
            if (onclick != null && !onclick.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Footer Methods
    public boolean isFooterDisplayed() {
        try {
            return footer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFooterBrandingDisplayed() {
        try {
            WebElement logo = driver.findElement(By.xpath("//*[@data-testid='landing.footer.logo']"));
            return logo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFooterQuickLinksDisplayed() {
        try {
            List<WebElement> quickLinks = driver.findElements(
                By.xpath("//*[contains(@data-testid, 'landing.footer.quickLink.')]")
            );
            return !quickLinks.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFooterContactDisplayed() {
        try {
            WebElement email = driver.findElement(By.xpath("//*[@data-testid='landing.footer.contact.email']"));
            return email.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

