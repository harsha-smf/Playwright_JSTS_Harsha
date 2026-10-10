package com.salesforce.automation.tests;

import java.net.URI;
import java.time.Duration;

import com.salesforce.automation.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class LoginPageTest {
    private static final String DEFAULT_LOGIN_URL = "https://login.salesforce.com/?locale=in";
    private static final Duration AUTHENTICATION_TIMEOUT = Duration.ofSeconds(30);

    private String loginUrl;
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeTest(alwaysRun = true)
    public void loadConfiguration() {
        loginUrl = System.getProperty(
                "salesforce.login.url",
                System.getenv().getOrDefault("SALESFORCE_LOGIN_URL", DEFAULT_LOGIN_URL));
        try {
            URI uri = URI.create(loginUrl);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
                throw new IllegalArgumentException("A valid HTTPS login URL is required");
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid Salesforce login URL configuration", exception);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1440,1000");
            if (Boolean.parseBoolean(System.getenv().getOrDefault("SELENIUM_HEADLESS", "false"))) {
                options.addArguments("--headless=new");
            }
            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
            loginPage = new LoginPage(driver);
            loginPage.open(loginUrl);
        } catch (WebDriverException exception) {
            closeDriverAfterSetupFailure();
            Assert.fail("Unable to initialize the Salesforce browser session", exception);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        closeDriverAfterSetupFailure();
    }

    @Test
    public void loginPageDisplaysRequiredControls() {
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "The login form should be visible");
        Assert.assertTrue(loginPage.isRememberMeControlDisplayed(), "The Remember Me control should be visible");
    }

    @Test(dataProvider = "invalidCredentials")
    public void invalidCredentialsDisplayAnError(String username, String password) {
        loginPage.login(username, password);

        String errorMessage = loginPage.waitForLoginError();
        Assert.assertFalse(errorMessage.isBlank(), "Invalid credentials should display a login error");
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "The login form should remain available");
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
                { "invalid.user@example.invalid", "Invalid-Password-123!" },
                { "not-a-salesforce-user", "Wrong-Password-456!" }
        };
    }

    @Test
    public void emptyCredentialsDoNotNavigateAwayFromLogin() {
        loginPage.login("", "");

        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "The login page should remain visible");
    }

    @Test
    public void rememberMeCanBeSelectedAndCleared() {
        loginPage.setRememberMeSelected(true);
        Assert.assertTrue(loginPage.isRememberMeSelected(), "Remember Me should be selected");

        loginPage.setRememberMeSelected(false);
        Assert.assertFalse(loginPage.isRememberMeSelected(), "Remember Me should be cleared");
    }

    @Test
    public void validCredentialsNavigateBeyondTheLoginPage() {
        String username = System.getenv("SALESFORCE_USERNAME");
        String password = System.getenv("SALESFORCE_PASSWORD");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new SkipException("Set SALESFORCE_USERNAME and SALESFORCE_PASSWORD to run this test");
        }

        loginPage.login(username, password);
        boolean navigated = new WebDriverWait(driver, AUTHENTICATION_TIMEOUT)
                .until(currentDriver -> !currentDriver.getCurrentUrl().equals(loginUrl));

        Assert.assertTrue(navigated, "Valid credentials should navigate beyond the login URL");
        Assert.assertTrue(loginPage.isLoginErrorAbsent(), "Successful login should not show a login error");
    }

    private void closeDriverAfterSetupFailure() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (WebDriverException exception) {
                throw new IllegalStateException("Unable to close the browser session", exception);
            } finally {
                driver = null;
                loginPage = null;
            }
        }
    }
}