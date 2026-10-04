package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The product catalogue screen that My Demo App opens on. */
public class ProductsPage {

    // Accessibility id (content-desc) of the Products screen container in the React Native My Demo App.
    private static final By PRODUCTS_SCREEN = AppiumBy.accessibilityId("products screen");
    private static final Duration LOAD_TIMEOUT = Duration.ofSeconds(15);

    private final AndroidDriver driver;

    public ProductsPage(AndroidDriver driver) {
        this.driver = driver;
    }

    /** Waits for the Products screen; true if it appeared, false if it did not within the timeout. */
    public boolean isLoaded() {
        try {
            new WebDriverWait(driver, LOAD_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(PRODUCTS_SCREEN));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
