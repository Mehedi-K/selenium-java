package com.automationframework.selenium.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checkout step one: "your information" form (name / zip).
 */
public class CheckoutStepOnePage extends BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(CheckoutStepOnePage.class);
    private static final long FORM_SETTLE_MILLIS = 500;
    private static final int MAX_REFILL_ATTEMPTS = 3;

    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    private final Map<By, String> typedValues = new LinkedHashMap<>();

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        typeAndRemember(firstNameInput, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        typeAndRemember(lastNameInput, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        typeAndRemember(postalCodeInput, postalCode);
        return this;
    }

    public CheckoutStepTwoPage clickContinue() {
        refillWipedFields();
        click(continueButton);
        return new CheckoutStepTwoPage(driver);
    }

    public CheckoutStepOnePage clickContinueExpectingFailure() {
        refillWipedFields();
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

    private void typeAndRemember(By locator, String text) {
        type(locator, text);
        typedValues.put(locator, text);
    }

    // Keystrokes sent before the React form finishes wiring its handlers are reset to
    // empty on its next render (seen intermittently on CI with Chrome 154), so confirm
    // the values survived a short settle period and retype any that were wiped.
    private void refillWipedFields() {
        if (typedValues.isEmpty()) {
            return;
        }
        for (int attempt = 1; attempt <= MAX_REFILL_ATTEMPTS; attempt++) {
            pause(FORM_SETTLE_MILLIS);
            Map<By, String> wiped = new LinkedHashMap<>();
            typedValues.forEach((locator, expected) -> {
                if (!expected.equals(waitForVisible(locator).getDomProperty("value"))) {
                    wiped.put(locator, expected);
                }
            });
            if (wiped.isEmpty()) {
                return;
            }
            LOG.warn("Checkout form dropped typed values for {} (attempt {}), retyping", wiped.keySet(), attempt);
            wiped.forEach(this::type);
        }
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
