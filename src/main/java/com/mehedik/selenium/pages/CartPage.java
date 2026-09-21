package com.mehedik.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The shopping cart page (/cart.html).
 */
public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By cartItemNames = By.className("inventory_item_name");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getCartItemNames() {
        if (getCartItemCount() == 0) {
            return List.of();
        }
        return waitForAllVisible(cartItemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public CartPage removeProduct(String productSlug) {
        click(By.id("remove-" + productSlug));
        return this;
    }

    public CheckoutStepOnePage clickCheckout() {
        click(checkoutButton);
        return new CheckoutStepOnePage(driver);
    }

    public ProductsPage clickContinueShopping() {
        click(continueShoppingButton);
        return new ProductsPage(driver);
    }
}
