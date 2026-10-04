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

/** The Product Details screen of the React Native My Demo App. */
public class ProductDetailsPage {

    // Accessibility ids (content-desc) confirmed on the real emulator.
    private static final By PRODUCT_SCREEN = AppiumBy.accessibilityId("product screen");
    private static final By PRODUCT_PRICE = AppiumBy.accessibilityId("product price");
    private static final By COUNTER_MINUS = AppiumBy.accessibilityId("counter minus button");
    private static final By COUNTER_AMOUNT = AppiumBy.accessibilityId("counter amount");
    private static final By COUNTER_PLUS = AppiumBy.accessibilityId("counter plus button");
    private static final By ADD_TO_CART = AppiumBy.accessibilityId("Add To Cart button");
    private static final By CART_BADGE = AppiumBy.accessibilityId("cart badge");

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public ProductDetailsPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /** Waits for the Product Details screen; true if it appeared, false if it did not within the timeout. */
    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_SCREEN));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * True if the given product name is visible as text on the screen.
     * The name has no accessibility id in the app, so it is matched by its visible text.
     */
    public boolean isProductNameDisplayed(String productName) {
        By byText = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + productName + "\")");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(byText));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getProductPrice() {
        return textOf(PRODUCT_PRICE);
    }

    public String getQuantity() {
        return textOf(COUNTER_AMOUNT);
    }

    /** Taps "+" and waits until the quantity shown has changed. */
    public void increaseQuantity() {
        String before = getQuantity();
        wait.until(ExpectedConditions.elementToBeClickable(COUNTER_PLUS)).click();
        wait.until(d -> !before.equals(readText(COUNTER_AMOUNT)));
    }

    /** Taps "-". It does not wait for a change, because at the minimum quantity nothing changes. */
    public void decreaseQuantity() {
        wait.until(ExpectedConditions.elementToBeClickable(COUNTER_MINUS)).click();
    }

    public void clickAddToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_TO_CART)).click();
    }

    /** Taps the cart badge in the header to open the Cart screen. */
    public void openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(CART_BADGE)).click();
    }

    /** Waits for the cart badge to show the given count; true if it did, false if it did not within the timeout. */
    public boolean waitForCartBadge(String expectedCount) {
        try {
            wait.until(d -> expectedCount.equals(readText(CART_BADGE)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String textOf(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return readText(locator);
    }

    /** Text of the element, or "" if it is not present. Falls back to a child TextView when the container has no text. */
    private String readText(By locator) {
        List<WebElement> found = driver.findElements(locator);
        if (found.isEmpty()) {
            return "";
        }
        WebElement element = found.get(0);
        String text = element.getText();
        if (text == null || text.isBlank()) {
            List<WebElement> children = element.findElements(AppiumBy.className("android.widget.TextView"));
            text = children.isEmpty() ? "" : children.get(0).getText();
        }
        return text == null ? "" : text;
    }
}
