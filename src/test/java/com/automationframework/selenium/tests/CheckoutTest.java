package com.automationframework.selenium.tests;

import com.automationframework.selenium.listeners.RetryOnce;
import com.automationframework.selenium.pages.CartPage;
import com.automationframework.selenium.pages.CheckoutCompletePage;
import com.automationframework.selenium.pages.CheckoutStepOnePage;
import com.automationframework.selenium.pages.CheckoutStepTwoPage;
import com.automationframework.selenium.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * End-to-end checkout flow: login, add items, checkout, fill shipping
 * info, verify the order overview totals, finish, and confirm success.
 */
@Test(retryAnalyzer = RetryOnce.class)
public class CheckoutTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        productsPage = loginPage().open().loginAs("standard_user", "secret_sauce");
    }

    @Test(description = "Full checkout flow: add items, fill info, verify totals, finish, confirm success")
    public void completeCheckoutFlowSucceeds() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        productsPage.addProductToCart(ProductsPage.Product.BIKE_LIGHT.slug);
        Assert.assertEquals(productsPage.getCartBadgeCount(), 2);

        CartPage cartPage = productsPage.goToCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Expected 2 items carried into the cart");

        CheckoutStepOnePage stepOne = cartPage.clickCheckout();
        CheckoutStepTwoPage stepTwo = stepOne.fillInfoAndContinue("Mehedi", "Khan", "12345");

        double itemsSum = stepTwo.getItemPricesSum();
        double subtotal = stepTwo.getSubtotal();
        double tax = stepTwo.getTax();
        double total = stepTwo.getTotal();

        Assert.assertEquals(stepTwo.getItemNames().size(), 2, "Expected 2 items in the order overview");
        Assert.assertEquals(subtotal, itemsSum, 0.001, "Expected subtotal to equal the sum of item prices");
        Assert.assertEquals(total, subtotal + tax, 0.01, "Expected total to equal subtotal plus tax");

        CheckoutCompletePage completePage = stepTwo.clickFinish();

        Assert.assertTrue(completePage.isOrderComplete(), "Expected the order confirmation header");
        Assert.assertTrue(
                completePage.getCompleteText().toLowerCase().contains("order"),
                "Expected confirmation text to mention the order");
    }

    @Test(description = "Submitting checkout info with missing fields shows a validation error")
    public void checkoutWithMissingInfoShowsError() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        CartPage cartPage = productsPage.goToCart();
        CheckoutStepOnePage stepOne = cartPage.clickCheckout();

        stepOne.enterFirstName("Mehedi");
        // last name and postal code intentionally left blank
        stepOne.clickContinueExpectingFailure();

        Assert.assertTrue(stepOne.isErrorDisplayed(), "Expected a validation error for missing last name");
        Assert.assertTrue(stepOne.getErrorMessage().contains("Last Name is required"));
    }

    @Test(description = "Cancelling from the checkout info step returns to the cart")
    public void cancellingCheckoutReturnsToCart() {
        productsPage.addProductToCart(ProductsPage.Product.BACKPACK.slug);
        CartPage cartPage = productsPage.goToCart();
        CheckoutStepOnePage stepOne = cartPage.clickCheckout();

        CartPage backToCart = stepOne.clickCancel();

        Assert.assertEquals(backToCart.getCartItemCount(), 1, "Expected the cart to still contain the item");
        Assert.assertTrue(backToCart.getCurrentUrl().contains("cart.html"));
    }

    @DataProvider(name = "productSets")
    public Object[][] productSets() {
        return new Object[][]{
                {new String[]{ProductsPage.Product.BACKPACK.slug}},
                {new String[]{ProductsPage.Product.BACKPACK.slug, ProductsPage.Product.BIKE_LIGHT.slug}},
                {new String[]{
                        ProductsPage.Product.BACKPACK.slug,
                        ProductsPage.Product.BIKE_LIGHT.slug,
                        ProductsPage.Product.FLEECE_JACKET.slug,
                        ProductsPage.Product.ONESIE.slug}},
        };
    }

    @Test(dataProvider = "productSets",
            description = "Checkout totals stay consistent across different product set sizes")
    public void checkoutTotalsAreConsistentAcrossProductSets(String[] productSlugs) {
        for (String slug : productSlugs) {
            productsPage.addProductToCart(slug);
        }
        Assert.assertEquals(productsPage.getCartBadgeCount(), productSlugs.length);

        CartPage cartPage = productsPage.goToCart();
        Assert.assertEquals(cartPage.getCartItemCount(), productSlugs.length);

        CheckoutStepOnePage stepOne = cartPage.clickCheckout();
        CheckoutStepTwoPage stepTwo = stepOne.fillInfoAndContinue("Mehedi", "Khan", "54321");

        Assert.assertEquals(stepTwo.getItemNames().size(), productSlugs.length);
        Assert.assertEquals(stepTwo.getSubtotal(), stepTwo.getItemPricesSum(), 0.001);
        Assert.assertEquals(stepTwo.getTotal(), stepTwo.getSubtotal() + stepTwo.getTax(), 0.01);

        CheckoutCompletePage completePage = stepTwo.clickFinish();
        Assert.assertTrue(completePage.isOrderComplete());
    }
}
