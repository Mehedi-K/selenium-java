package com.automationframework.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Checkout step three: order confirmation.
 */
public class CheckoutCompletePage extends BasePage {

    private final By completeHeader = By.className("complete-header");
    private final By completeText = By.className("complete-text");
    private final By backToProductsButton = By.id("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public String getCompleteHeader() {
        return getText(completeHeader);
    }

    public String getCompleteText() {
        return getText(completeText);
    }

    public boolean isOrderComplete() {
        return "Thank you for your order!".equals(getCompleteHeader());
    }

    public ProductsPage clickBackToProducts() {
        click(backToProductsButton);
        return new ProductsPage(driver);
    }
}
