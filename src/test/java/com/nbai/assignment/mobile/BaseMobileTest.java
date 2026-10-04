package com.nbai.assignment.mobile;

import com.nbai.assignment.mobile.driver.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Gives every mobile test its own fresh Appium session: created before the test method, closed after it.
 * On failure a screenshot is saved to target/screenshots and attached to the Allure report.
 */
public abstract class BaseMobileTest {

    protected AndroidDriver driver;

    @BeforeMethod
    public void startSession() {
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void endSession(ITestResult result) {
        if (driver == null) {
            return;
        }
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                saveScreenshot(result);
            }
        } finally {
            driver.quit();
            driver = null;
        }
    }

    private void saveScreenshot(ITestResult result) {
        try {
            byte[] png = driver.getScreenshotAs(OutputType.BYTES);
            Path target = Path.of("target", "screenshots",
                    result.getTestClass().getRealClass().getSimpleName() + "." + result.getName() + ".png");
            Files.createDirectories(target.getParent());
            Files.write(target, png);
            Allure.addAttachment("Failure screenshot", "image/png", new ByteArrayInputStream(png), "png");
            System.err.println("Failure screenshot saved to " + target);
        } catch (Exception e) {
            System.err.println("Could not save failure screenshot: " + e.getMessage());
        }
    }
}
