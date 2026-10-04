package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The order confirmation screen. */
public class CheckoutCompletePage {

    private static final By SCREEN = AppiumBy.accessibilityId("checkout complete screen");
    private static final By CONTINUE_SHOPPING = AppiumBy.accessibilityId("Continue Shopping button");

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public CheckoutCompletePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isLoaded() {
        return isVisible(SCREEN);
    }

    public boolean isCheckoutCompleteTextDisplayed() {
        return isTextDisplayed("Checkout Complete");
    }

    public boolean isThankYouTextDisplayed() {
        return isTextDisplayed("Thank you for your order");
    }

    public boolean isOrderOnItsWayTextDisplayed() {
        return isTextDisplayed("Your new swag is on its way");
    }

    public boolean isContinueShoppingButtonDisplayed() {
        return isVisible(CONTINUE_SHOPPING);
    }

    private boolean isTextDisplayed(String text) {
        return isVisible(AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + text + "\")"));
    }

    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
