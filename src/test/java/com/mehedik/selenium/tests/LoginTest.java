package com.mehedik.selenium.tests;

import com.mehedik.selenium.pages.LoginPage;
import com.mehedik.selenium.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Login scenarios: valid login, invalid/locked-out credentials, empty
 * fields, and a data-driven sweep over several username/password combos.
 */
public class LoginTest extends BaseTest {

    @Test(description = "A standard user can log in and lands on the products page")
    public void standardUserCanLogIn() {
        ProductsPage productsPage = loginPage().open().loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(productsPage.isLoaded(), "Expected to land on the Products page after login");
        Assert.assertTrue(productsPage.getCurrentUrl().contains("inventory.html"),
                "Expected URL to contain inventory.html");
    }

    @Test(description = "A locked out user sees an error message and stays on the login page")
    public void lockedOutUserSeesError() {
        LoginPage loginPage = loginPage().open().loginExpectingFailure("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an error message for a locked out user");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Expected error message to mention the account is locked out");
    }

    @Test(description = "An invalid password shows an error and does not log the user in")
    public void invalidPasswordShowsError() {
        LoginPage loginPage = loginPage().open().loginExpectingFailure("standard_user", "wrong_password");

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an error message for invalid credentials");
        Assert.assertTrue(loginPage.getCurrentUrl().endsWith("saucedemo.com/"),
                "Expected to remain on the login page after a failed login");
    }

    @Test(description = "Submitting the login form with empty fields shows a required-field error")
    public void emptyFieldsShowError() {
        LoginPage loginPage = loginPage().open().clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an error message when submitting empty fields");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"),
                "Expected error to mention that username is required");
    }

    @DataProvider(name = "loginCredentials")
    public Object[][] loginCredentials() {
        return new Object[][]{
                {"standard_user", "secret_sauce", true},
                {"locked_out_user", "secret_sauce", false},
                {"problem_user", "secret_sauce", true},
                {"performance_glitch_user", "secret_sauce", true},
                {"invalid_user", "secret_sauce", false},
                {"standard_user", "wrong_password", false},
        };
    }

    @Test(dataProvider = "loginCredentials",
            description = "Data-driven sweep of username/password combinations and their expected outcome")
    public void loginWithMultipleCredentials(String username, String password, boolean expectSuccess) {
        LoginPage loginPage = loginPage().open();

        if (expectSuccess) {
            ProductsPage productsPage = loginPage.loginAs(username, password);
            Assert.assertTrue(productsPage.isLoaded(),
                    "Expected login to succeed for user: " + username);
        } else {
            LoginPage result = loginPage.loginExpectingFailure(username, password);
            Assert.assertTrue(result.isErrorDisplayed(),
                    "Expected login to fail for user: " + username);
        }
    }
}
