package com.salesforce.automation.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class LoginPage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(15);

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//input[@id='username']")
    private WebElement usernameField;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement passwordField;

    @FindBy(xpath = "//input[@id='Login']")
    private WebElement loginButton;

    @FindBy(xpath = "//input[@id='rememberUn']")
    private WebElement rememberMeCheckbox;

    @FindBy(xpath = "//*[@id='error' and normalize-space()]" )
    private WebElement loginError;

    public LoginPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        PageFactory.initElements(driver, this);
    }

    public void open(String url) {
        try {
            driver.get(url);
            wait.until(ExpectedConditions.visibilityOf(usernameField));
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to open the Salesforce login page", exception);
        }
    }

    public boolean isLoginFormDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(usernameField)).isDisplayed()
                    && passwordField.isDisplayed()
                    && loginButton.isDisplayed();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to verify the Salesforce login form", exception);
        }
    }

    public void login(String username, String password) {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            usernameField.clear();
            usernameField.sendKeys(username);
            passwordField.clear();
            passwordField.sendKeys(password);
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to submit Salesforce login credentials", exception);
        }
    }

    public String waitForLoginError() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(loginError)).getText().trim();
        } catch (TimeoutException exception) {
            return "";
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to read the Salesforce login error", exception);
        }
    }

    public void setRememberMeSelected(boolean selected) {
        try {
            wait.until(ExpectedConditions.visibilityOf(rememberMeCheckbox));
            if (rememberMeCheckbox.isSelected() != selected) {
                rememberMeCheckbox.click();
            }
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to update the Remember Me option", exception);
        }
    }

    public boolean isRememberMeSelected() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(rememberMeCheckbox)).isSelected();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to verify the Remember Me option", exception);
        }
    }

    public boolean isRememberMeControlDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(rememberMeCheckbox)).isDisplayed();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to verify the Remember Me control", exception);
        }
    }

    public boolean isLoginErrorAbsent() {
        try {
            return driver.findElements(By.xpath("//*[@id='error' and normalize-space()]")).isEmpty();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to verify the Salesforce login result", exception);
        }
    }
}