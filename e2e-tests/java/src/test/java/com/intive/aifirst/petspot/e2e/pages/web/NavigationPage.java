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
    
    private boolean hasActiveClass(WebElement element) {
        try {
            String className = element.getAttribute("class");
            return className != null && className.contains("Active");
        } catch (Exception e) {
            return false;
        }
    }
}

