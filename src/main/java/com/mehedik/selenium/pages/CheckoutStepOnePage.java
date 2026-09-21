package com.mehedik.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Checkout step one: "your information" form (name / zip).
 */
public class CheckoutStepOnePage extends BasePage {

    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        type(firstNameInput, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        type(lastNameInput, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        type(postalCodeInput, postalCode);
        return this;
    }

    public CheckoutStepTwoPage clickContinue() {
        click(continueButton);
        return new CheckoutStepTwoPage(driver);
    }

    public CheckoutStepOnePage clickContinueExpectingFailure() {
        click(continueButton);
        return this;
    }

    public CheckoutStepTwoPage fillInfoAndContinue(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return clickContinue();
    }

    public CartPage clickCancel() {
        click(cancelButton);
        return new CartPage(driver);
    }

    public boolean isErrorDisplayed() {
        return isVisible(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
