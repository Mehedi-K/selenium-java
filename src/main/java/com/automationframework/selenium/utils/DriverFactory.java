package com.automationframework.selenium.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and tears down WebDriver instances. Thread-safe via a ThreadLocal
 * so tests could run in parallel without stepping on each other's driver.
 *
 * Chrome binaries/drivers are resolved automatically by Selenium Manager
 * (bundled with Selenium 4.6+), so no WebDriverManager dependency is needed.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        if (DRIVER_THREAD_LOCAL.get() == null) {
            DRIVER_THREAD_LOCAL.set(createDriver());
        }
        return DRIVER_THREAD_LOCAL.get();
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.get("browser", "chrome");
        boolean headless = isHeadless();

        LOG.info("Creating {} driver (headless={})", browser, headless);

        if (!"chrome".equalsIgnoreCase(browser)) {
            LOG.warn("Browser '{}' is not explicitly supported, defaulting to Chrome", browser);
        }

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        // NOTE: --disable-gpu is intentionally NOT set. With --headless=new it stops the
        // headless compositor from producing frames once the page has been idle for a
        // few seconds, which silently drops native WebDriver click input (the click
        // command succeeds but no DOM event is ever dispatched). Leaving GPU rendering
        // enabled keeps clicks reliable after explicit waits/timeouts elapse.

        return new ChromeDriver(options);
    }

    private static boolean isHeadless() {
        String headlessProp = System.getProperty("headless");
        if (headlessProp == null || headlessProp.isBlank()) {
            headlessProp = System.getenv("HEADLESS");
        }
        return Boolean.parseBoolean(headlessProp);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            LOG.info("Quitting driver");
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
