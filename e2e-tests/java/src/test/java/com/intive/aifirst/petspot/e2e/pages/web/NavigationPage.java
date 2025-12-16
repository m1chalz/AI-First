package com.intive.aifirst.petspot.e2e.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavigationPage {
    
    private final WebDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    @FindBy(xpath = "//*[@data-testid='navigation.home.link']")
    private WebElement homeLink;
    
    @FindBy(xpath = "//*[@data-testid='navigation.lostPet.link']")
    private WebElement lostPetLink;
    
    @FindBy(xpath = "//*[@data-testid='navigation.foundPet.link']")
    private WebElement foundPetLink;
    
    @FindBy(xpath = "//*[@data-testid='navigation.contact.link']")
    private WebElement contactLink;
    
    @FindBy(xpath = "//*[@data-testid='navigation.account.link']")
    private WebElement accountLink;
    
    @FindBy(xpath = "//*[@data-testid='navigation.logo.link']")
    private WebElement logoLink;
    
    private final By navigationBarLocator = By.xpath("//*[@data-testid='navigation.bar']");
    
    public NavigationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    public void clickHome() {
        homeLink.click();
    }
    
    public void clickLostPet() {
        lostPetLink.click();
    }
    
    public void clickFoundPet() {
        foundPetLink.click();
    }
    
    public void clickContact() {
        contactLink.click();
    }
    
    public void clickAccount() {
        accountLink.click();
    }
    
    public void clickLogo() {
        logoLink.click();
    }
    
    public boolean isNavigationBarDisplayed() {
        try {
            return driver.findElement(navigationBarLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isHomeLinkActive() {
        return hasActiveClass(homeLink);
    }
    
    public boolean isLostPetLinkActive() {
        return hasActiveClass(lostPetLink);
    }
    
    public boolean isFoundPetLinkActive() {
        return hasActiveClass(foundPetLink);
    }
    
    public boolean isContactLinkActive() {
        return hasActiveClass(contactLink);
    }
    
    public boolean isAccountLinkActive() {
        return hasActiveClass(accountLink);
    }
    
    public String getActiveItemId() {
        if (isHomeLinkActive()) return "home";
        if (isLostPetLinkActive()) return "lostPet";
        if (isFoundPetLinkActive()) return "foundPet";
        if (isContactLinkActive()) return "contact";
        if (isAccountLinkActive()) return "account";
        return null;
    }
    
    public boolean waitForNavigationBarVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(navigationBarLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasActiveClass(WebElement element) {
        try {
            String className = element.getAttribute("class");
            return className != null && className.contains("Active");
        } catch (Exception e) {
            return false;
        }
    }

    // User Story 2 - Visual Design Methods
    
    public boolean isNavigationBarHorizontalLayout() {
        try {
            WebElement navBar = driver.findElement(navigationBarLocator);
            String display = navBar.getCssValue("display");
            return "flex".equals(display);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isLogoPositionedLeft() {
        try {
            WebElement navBar = driver.findElement(navigationBarLocator);
            int logoX = logoLink.getLocation().getX();
            int navBarX = navBar.getLocation().getX();
            // Logo should be close to the left edge of the navigation bar
            return logoX <= navBarX + 100;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean areNavigationItemsPositionedRight() {
        try {
            int logoRightEdge = logoLink.getLocation().getX() + logoLink.getSize().getWidth();
            int homeX = homeLink.getLocation().getX();
            // Navigation items should be to the right of the logo
            return homeX > logoRightEdge;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasNavigationItemIcon(WebElement item) {
        try {
            return item.findElement(By.tagName("svg")) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasNavigationItemLabel(WebElement item) {
        try {
            WebElement label = item.findElement(By.tagName("span"));
            return label != null && !label.getText().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean allNavigationItemsHaveIcons() {
        return hasNavigationItemIcon(homeLink) &&
               hasNavigationItemIcon(lostPetLink) &&
               hasNavigationItemIcon(foundPetLink) &&
               hasNavigationItemIcon(contactLink) &&
               hasNavigationItemIcon(accountLink);
    }
    
    public boolean allNavigationItemsHaveLabels() {
        return hasNavigationItemLabel(homeLink) &&
               hasNavigationItemLabel(lostPetLink) &&
               hasNavigationItemLabel(foundPetLink) &&
               hasNavigationItemLabel(contactLink) &&
               hasNavigationItemLabel(accountLink);
    }
    
    public boolean iconsAppearBeforeLabels() {
        try {
            // Check order by comparing Y positions (icons and labels should be at same Y) 
            // and X positions (icon should be before label)
            WebElement icon = homeLink.findElement(By.tagName("svg"));
            WebElement label = homeLink.findElement(By.tagName("span"));
            return icon.getLocation().getX() < label.getLocation().getX();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasActiveItemBlueBackground(WebElement item) {
        try {
            String bgColor = item.getCssValue("background-color");
            // Blue background #EFF6FF converts to RGB(239, 246, 255)
            return bgColor.contains("239") && bgColor.contains("246") && bgColor.contains("255");
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasActiveItemBlueText(WebElement item) {
        try {
            String textColor = item.getCssValue("color");
            // Blue text #155DFC converts to RGB(21, 93, 252)
            return textColor.contains("21") && textColor.contains("93") && textColor.contains("252");
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasInactiveItemTransparentBackground(WebElement item) {
        try {
            String bgColor = item.getCssValue("background-color");
            // Transparent or rgba(0, 0, 0, 0)
            return bgColor.contains("transparent") || bgColor.contains("rgba(0, 0, 0, 0)");
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasInactiveItemGrayText(WebElement item) {
        try {
            String textColor = item.getCssValue("color");
            // Gray text #4A5565 converts to RGB(74, 85, 101)
            return textColor.contains("74") && textColor.contains("85") && textColor.contains("101");
        } catch (Exception e) {
            return false;
        }
    }
    
    public WebElement getNavigationItem(String section) {
        return switch (section) {
            case "Home" -> homeLink;
            case "Lost Pet" -> lostPetLink;
            case "Found Pet" -> foundPetLink;
            case "Contact Us" -> contactLink;
            case "Account" -> accountLink;
            default -> throw new IllegalArgumentException("Unknown section: " + section);
        };
    }
    
    public void hoverOverNavigationItem(String section) {
        WebElement item = getNavigationItem(section);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(item).perform();
    }
    
    public boolean hasHoverFeedback(WebElement item) {
        try {
            String bgColor = item.getCssValue("background-color");
            // Hover background #F3F4F6 converts to RGB(243, 244, 246) - slightly gray
            // Should not be transparent when hovered
            return !bgColor.contains("transparent") && !bgColor.contains("rgba(0, 0, 0, 0)");
        } catch (Exception e) {
            return false;
        }
    }
}

