package com.automationframework.selenium.tests;

import com.automationframework.selenium.pages.CartPage;
import com.automationframework.selenium.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Cart behaviour: adding/removing items, the header badge count, and that
 * the cart page contents match what was added on the products page.
 */
public class CartTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        productsPage = loginPage().open().loginAs("standard_user", "secret_sauce");
    }

    @Test(description = "Adding a product to the cart updates the header badge count")
    public void addingProductUpdatesCartBadge() {
        Assert.assertFalse(productsPage.isCartBadgeVisible(), "Cart badge should not be visible when cart is empty");

        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);

        Assert.assertEquals(productsPage.getCartBadgeCount(), 1, "Expected cart badge to show 1 item");
    }

    @Test(description = "Adding multiple products increments the cart badge for each one")
    public void addingMultipleProductsIncrementsBadge() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        productsPage.addProductToCart(ProductsPage.Product.BIKE_LIGHT.slug);
        productsPage.addProductToCart(ProductsPage.Product.BOLT_TSHIRT.slug);

        Assert.assertEquals(productsPage.getCartBadgeCount(), 3, "Expected cart badge to show 3 items");
    }

    @Test(description = "Removing a product from the products page decrements the cart badge")
    public void removingProductDecrementsBadge() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        productsPage.addProductToCart(ProductsPage.Product.BIKE_LIGHT.slug);
        Assert.assertEquals(productsPage.getCartBadgeCount(), 2);

        productsPage.removeProductFromCart(ProductsPage.Product.BACKPACK.slug);

        Assert.assertEquals(productsPage.getCartBadgeCount(), 1, "Expected cart badge to decrease after removal");
    }

    @Test(description = "Items added on the products page are reflected on the cart page")
    public void cartContentsMatchAddedProducts() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        productsPage.addProductToCart(ProductsPage.Product.FLEECE_JACKET.slug);

        CartPage cartPage = productsPage.goToCart();
        List<String> cartItemNames = cartPage.getCartItemNames();

        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Expected 2 items in the cart");
        Assert.assertTrue(cartItemNames.contains("Sauce Labs Backpack"), "Expected backpack in the cart");
        Assert.assertTrue(cartItemNames.contains("Sauce Labs Fleece Jacket"), "Expected fleece jacket in the cart");
    }

    @Test(description = "Removing an item from the cart page removes it from the cart contents")
    public void removingProductFromCartPage() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        productsPage.addProductToCart(ProductsPage.Product.ONESIE.slug);

        CartPage cartPage = productsPage.goToCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 2);

        cartPage.removeProduct(ProductsPage.Product.ONESIE.slug);

        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Expected 1 item left in the cart");
        Assert.assertTrue(cartPage.getCartItemNames().contains("Sauce Labs Backpack"));
    }
}
