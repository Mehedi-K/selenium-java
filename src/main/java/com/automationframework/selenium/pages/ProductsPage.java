package com.automationframework.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The inventory / products listing page shown after a successful login.
 */
public class ProductsPage extends BasePage {

    private final By pageTitle = By.className("title");
    private final By inventoryItems = By.className("inventory_item");
    private final By inventoryItemNames = By.className("inventory_item_name");
    private final By inventoryItemPrices = By.className("inventory_item_price");
    private final By sortDropdown = By.className("product_sort_container");
    private final By cartLink = By.className("shopping_cart_link");
    private final By cartBadge = By.className("shopping_cart_badge");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(pageTitle) && "Products".equals(getText(pageTitle));
    }

    public ProductsPage sortBy(SortOption option) {
        Select select = new Select(waitForVisible(sortDropdown));
        select.selectByValue(option.value);
        return this;
    }

    public List<String> getDisplayedProductNames() {
        return waitForAllVisible(inventoryItemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getDisplayedProductPrices() {
        return waitForAllVisible(inventoryItemPrices).stream()
                .map(el -> Double.parseDouble(el.getText().replace("$", "")))
                .collect(Collectors.toList());
    }

    public ProductsPage addProductToCart(String productSlug) {
        click(By.id("add-to-cart-" + productSlug));
        return this;
    }

    public ProductsPage removeProductFromCart(String productSlug) {
        click(By.id("remove-" + productSlug));
        return this;
    }

    public int getInventoryItemCount() {
        return waitForAllVisible(inventoryItems).size();
    }

    public int getCartBadgeCount() {
        if (!isVisible(cartBadge)) {
            return 0;
        }
        return Integer.parseInt(getText(cartBadge));
    }

    public boolean isCartBadgeVisible() {
        return isVisible(cartBadge);
    }

    public CartPage goToCart() {
        click(cartLink);
        return new CartPage(driver);
    }

    /** Product slugs (the ids saucedemo uses for its add-to-cart / remove buttons). */
    public enum Product {
        BACKPACK("sauce-labs-backpack"),
        BIKE_LIGHT("sauce-labs-bike-light"),
        BOLT_TSHIRT("sauce-labs-bolt-t-shirt"),
        FLEECE_JACKET("sauce-labs-fleece-jacket"),
        ONESIE("sauce-labs-onesie"),
        RED_TSHIRT("test.allthethings()-t-shirt-(red)");

        public final String slug;

        Product(String slug) {
            this.slug = slug;
        }
    }

    public enum SortOption {
        NAME_A_TO_Z("az"),
        NAME_Z_TO_A("za"),
        PRICE_LOW_TO_HIGH("lohi"),
        PRICE_HIGH_TO_LOW("hilo");

        public final String value;

        SortOption(String value) {
            this.value = value;
        }
    }
}
