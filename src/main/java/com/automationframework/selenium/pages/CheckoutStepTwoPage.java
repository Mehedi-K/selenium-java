package com.automationframework.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checkout step two: order overview, with item totals, tax, and grand total.
 */
public class CheckoutStepTwoPage extends BasePage {

    private final By cartItemNames = By.className("inventory_item_name");
    private final By cartItemPrices = By.className("inventory_item_price");
    private final By subtotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.className("summary_total_label");
    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getItemNames() {
        return waitForAllVisible(cartItemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public double getItemPricesSum() {
        return waitForAllVisible(cartItemPrices).stream()
                .mapToDouble(el -> Double.parseDouble(el.getText().replace("$", "")))
                .sum();
    }

    public double getSubtotal() {
        return parseCurrency(getText(subtotalLabel));
    }

    public double getTax() {
        return parseCurrency(getText(taxLabel));
    }

    public double getTotal() {
        return parseCurrency(getText(totalLabel));
    }

    private double parseCurrency(String label) {
        // Labels look like "Item total: $29.99" / "Tax: $2.40" / "Total: $32.39"
        String numeric = label.substring(label.indexOf('$') + 1);
        return Double.parseDouble(numeric);
    }

    public CheckoutCompletePage clickFinish() {
        click(finishButton);
        return new CheckoutCompletePage(driver);
    }

    public CartPage clickCancel() {
        click(cancelButton);
        return new CartPage(driver);
    }
}
