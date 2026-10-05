package com.automationframework.selenium.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed test once. BaseTest quits the driver after every attempt,
 * so the retry always gets a fresh browser session.
 */
public class RetryOnce implements IRetryAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryOnce.class);
    private boolean retried;

    @Override
    public boolean retry(ITestResult result) {
        if (retried) {
            return false;
        }
        retried = true;
        LOG.warn("Retrying {} once in a fresh browser after: {}", result.getName(),
                result.getThrowable() == null ? "unknown failure" : result.getThrowable().getMessage());
        return true;
    }
}
