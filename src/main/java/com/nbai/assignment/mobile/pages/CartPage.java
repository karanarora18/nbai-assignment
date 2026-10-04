package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The Cart screen of the React Native My Demo App. */
public class CartPage {

    // Accessibility ids (content-desc) confirmed on the real emulator.
    private static final By CART_SCREEN = AppiumBy.accessibilityId("cart screen");
    private static final By PRODUCT_LABEL = AppiumBy.accessibilityId("product label");
    private static final By PRODUCT_PRICE = AppiumBy.accessibilityId("product price");
    private static final By QUANTITY = AppiumBy.accessibilityId("counter amount");
    private static final By TOTAL_ITEMS = AppiumBy.accessibilityId("total number");
    private static final By TOTAL_PRICE = AppiumBy.accessibilityId("total price");
    private static final By PROCEED_TO_CHECKOUT = AppiumBy.accessibilityId("Proceed To Checkout button");

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final WebDriverWait wait;

    public CartPage(AndroidDriver driver) {
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /** Waits for the Cart screen; true if it appeared, false if it did not within the timeout. */
    public boolean isLoaded() {
        return isVisible(CART_SCREEN);
    }

    public String getProductName() {
        return textOf(PRODUCT_LABEL);
    }

    public String getProductPrice() {
        return textOf(PRODUCT_PRICE);
    }

    public String getQuantity() {
        return textOf(QUANTITY);
    }

    /** The items summary, e.g. "2 items". */
    public String getTotalItems() {
        return textOf(TOTAL_ITEMS);
    }

    public String getTotalPrice() {
        return textOf(TOTAL_PRICE);
    }

    public boolean isCheckoutButtonDisplayed() {
        return isVisible(PROCEED_TO_CHECKOUT);
    }

    public void clickProceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(PROCEED_TO_CHECKOUT)).click();
    }

    /** Waits for the element, then returns its text; falls back to a child TextView when the container has none. */
    private String textOf(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        String text = element.getText();
        if (text == null || text.isBlank()) {
            List<WebElement> children = element.findElements(AppiumBy.className("android.widget.TextView"));
            text = children.isEmpty() ? "" : children.get(0).getText();
        }
        return text;
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
