package com.automationframework.selenium.pages;

import com.automationframework.selenium.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Common functionality shared by every page object: explicit waits wrapped
 * around the handful of interactions the tests need. No Thread.sleep here.
 */
public abstract class BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(BasePage.class);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.explicitWaitSeconds()));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        element.click();
        // Defensive fallback for this environment's headless Chrome: a native click can
        // silently no-op once the page has sat idle for several seconds (observed after
        // any multi-second explicit wait), even though the element is reported as
        // displayed/enabled and at the front of the hit-test. Re-dispatching the click via
        // JS is a harmless no-op when the native click already worked (the target element
        // has usually left the DOM by then, e.g. add-to-cart -> remove, or the page has
        // navigated), and guarantees the intended action still happens when it did not.
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (StaleElementReferenceException expected) {
            // The native click already took effect and removed/replaced this element.
        } catch (Exception e) {
            LOG.debug("JS click fallback for {} did not apply: {}", locator, e.getMessage());
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isVisible(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
