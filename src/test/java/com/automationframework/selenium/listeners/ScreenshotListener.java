package com.automationframework.selenium.listeners;

import com.automationframework.selenium.utils.DriverFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * TestNG listener that captures a screenshot plus browser diagnostics (console
 * log, loaded resources, form state) whenever a test fails, including attempts
 * that are retried. Saved under screenshots/ (gitignored, uploaded as a CI artifact).
 */
public class ScreenshotListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotListener.class);
    private static final Path SCREENSHOT_DIR = Paths.get("screenshots");
    private static final String PAGE_STATE_SCRIPT = String.join("\n",
            "return JSON.stringify({",
            "  url: location.href, readyState: document.readyState, userAgent: navigator.userAgent,",
            "  activeElement: document.activeElement && (document.activeElement.id || document.activeElement.tagName),",
            "  inputs: [...document.querySelectorAll('input')].map(i => ({id: i.id, value: i.value})),",
            "  errorText: (document.querySelector(\"[data-test='error']\") || {}).textContent || null,",
            "  resources: performance.getEntriesByType('resource').map(r => ({name: r.name,",
            "    status: r.responseStatus, bytes: r.transferSize, ms: Math.round(r.duration)}))",
            "}, null, 1);");

    @Override
    public void onTestFailure(ITestResult result) {
        captureArtifacts(result, "failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result.wasRetried()) {
            captureArtifacts(result, "retried");
        }
    }

    private void captureArtifacts(ITestResult result, String outcome) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }
        String baseName = result.getTestClass().getRealClass().getSimpleName()
                + "_" + result.getMethod().getMethodName()
                + "_" + outcome
                + "_" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = SCREENSHOT_DIR.resolve(baseName + ".png");
            Files.copy(screenshot.toPath(), destination);
            Files.writeString(SCREENSHOT_DIR.resolve(baseName + "_diagnostics.txt"),
                    diagnostics(driver), StandardCharsets.UTF_8);
            LOG.error("Test {} {}, screenshot and diagnostics saved to {}", result.getName(), outcome, destination);
        } catch (IOException | RuntimeException e) {
            LOG.error("Failed to capture failure artifacts for {}", result.getName(), e);
        }
    }

    private String diagnostics(WebDriver driver) {
        StringBuilder out = new StringBuilder("== Page state ==\n");
        try {
            out.append(((JavascriptExecutor) driver).executeScript(PAGE_STATE_SCRIPT)).append('\n');
        } catch (RuntimeException e) {
            out.append("unavailable: ").append(e.getMessage()).append('\n');
        }
        out.append("\n== Browser console ==\n");
        try {
            for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
                out.append(entry.getLevel()).append(' ').append(entry.getMessage()).append('\n');
            }
        } catch (RuntimeException e) {
            out.append("unavailable: ").append(e.getMessage()).append('\n');
        }
        return out.toString();
    }
}
