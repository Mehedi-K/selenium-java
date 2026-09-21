package com.mehedik.selenium.pages;

import com.mehedik.selenium.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * https://www.saucedemo.com/ - the login page.
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigReader.baseUrl());
        waitForVisible(usernameInput);
        return this;
    }

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public ProductsPage clickLogin() {
        click(loginButton);
        return new ProductsPage(driver);
    }

    /** Attempts login but expects to stay on the login page (e.g. locked out / invalid credentials). */
    public LoginPage clickLoginExpectingFailure() {
        click(loginButton);
        return this;
    }

    public ProductsPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginExpectingFailure();
    }

    public boolean isErrorDisplayed() {
        return isVisible(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
