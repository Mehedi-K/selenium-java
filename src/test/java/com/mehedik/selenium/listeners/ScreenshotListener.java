package com.mehedik.selenium.listeners;

import com.mehedik.selenium.utils.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * TestNG listener that captures a screenshot whenever a test fails and
 * saves it under screenshots/ (gitignored, uploaded as a CI artifact).
 */
public class ScreenshotListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotListener.class);
    private static final Path SCREENSHOT_DIR = Paths.get("screenshots");

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
            String fileName = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName()
                    + "_" + timestamp + ".png";
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = SCREENSHOT_DIR.resolve(fileName);
            Files.copy(screenshot.toPath(), destination);
            LOG.error("Test {} failed, screenshot saved to {}", result.getName(), destination);
        } catch (IOException e) {
            LOG.error("Failed to capture screenshot for {}", result.getName(), e);
        }
    }
}
