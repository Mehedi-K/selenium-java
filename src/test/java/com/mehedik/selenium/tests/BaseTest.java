package com.mehedik.selenium.tests;

import com.mehedik.selenium.listeners.ScreenshotListener;
import com.mehedik.selenium.pages.LoginPage;
import com.mehedik.selenium.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Base class for every TestNG test: creates a fresh WebDriver before each
 * test method and tears it down afterwards.
 */
@Listeners(ScreenshotListener.class)
public abstract class BaseTest {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.getDriver();
        LOG.info("Driver started for test");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            LOG.warn("Test {} failed", result.getName());
        }
        DriverFactory.quitDriver();
        LOG.info("Driver quit after test");
    }

    protected LoginPage loginPage() {
        return new LoginPage(driver);
    }
}
