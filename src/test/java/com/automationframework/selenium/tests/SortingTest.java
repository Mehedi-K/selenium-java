package com.automationframework.selenium.tests;

import com.automationframework.selenium.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Verifies the product sort dropdown correctly reorders the inventory by
 * name (A-Z / Z-A) and price (low-high / high-low).
 */
public class SortingTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        productsPage = loginPage().open().loginAs("standard_user", "secret_sauce");
    }

    @Test(description = "Sorting by name A-Z lists products alphabetically ascending")
    public void sortByNameAToZ() {
        productsPage.sortBy(ProductsPage.SortOption.NAME_A_TO_Z);
        List<String> names = productsPage.getDisplayedProductNames();

        List<String> expected = new ArrayList<>(names);
        Collections.sort(expected);

        Assert.assertEquals(names, expected, "Expected products sorted alphabetically A-Z");
    }

    @Test(description = "Sorting by name Z-A lists products alphabetically descending")
    public void sortByNameZToA() {
        productsPage.sortBy(ProductsPage.SortOption.NAME_Z_TO_A);
        List<String> names = productsPage.getDisplayedProductNames();

        List<String> expected = new ArrayList<>(names);
        expected.sort(Collections.reverseOrder());

        Assert.assertEquals(names, expected, "Expected products sorted alphabetically Z-A");
    }

    @Test(description = "Sorting by price low-high lists products in ascending price order")
    public void sortByPriceLowToHigh() {
        productsPage.sortBy(ProductsPage.SortOption.PRICE_LOW_TO_HIGH);
        List<Double> prices = productsPage.getDisplayedProductPrices();

        List<Double> expected = new ArrayList<>(prices);
        Collections.sort(expected);

        Assert.assertEquals(prices, expected, "Expected products sorted by price ascending");
    }

    @Test(description = "Sorting by price high-low lists products in descending price order")
    public void sortByPriceHighToLow() {
        productsPage.sortBy(ProductsPage.SortOption.PRICE_HIGH_TO_LOW);
        List<Double> prices = productsPage.getDisplayedProductPrices();

        List<Double> expected = new ArrayList<>(prices);
        expected.sort(Collections.reverseOrder());

        Assert.assertEquals(prices, expected, "Expected products sorted by price descending");
    }
}
