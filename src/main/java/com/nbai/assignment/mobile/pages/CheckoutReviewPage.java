package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The order review step of checkout. */
public class CheckoutReviewPage {

    private static final String SCREEN = "checkout review order screen";
    private static final String PRODUCT_LABEL = "product label";
    private static final String DELIVERY_ADDRESS = "checkout delivery address";
    private static final String PAYMENT_INFO = "checkout payment info";
    private static final String BILLING_ADDRESS = "checkout billing address";
    private static final String DELIVERY_DETAILS = "checkout delivery details";
    private static final String TOTAL_NUMBER = "total number";
    private static final String TOTAL_PRICE = "total price";
    private static final String PLACE_ORDER = "Place Order button";

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public CheckoutReviewPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(SCREEN)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getProductName() {
        return textOf(PRODUCT_LABEL);
    }

    public boolean areDeliveryAddressPaymentAndBillingDisplayed() {
        return isDisplayed(DELIVERY_ADDRESS) && isDisplayed(PAYMENT_INFO) && isDisplayed(BILLING_ADDRESS)
                && isDisplayed(DELIVERY_DETAILS);
    }

    /** The items summary, e.g. "2 items". */
    public String getTotalItems() {
        return textOf(TOTAL_NUMBER);
    }

    public String getTotalPrice() {
        return textOf(TOTAL_PRICE);
    }

    public boolean isPlaceOrderButtonDisplayed() {
        return isDisplayed(PLACE_ORDER);
    }

    public void clickPlaceOrder() {
        ScreenScroller.scrollIntoView(driver, PLACE_ORDER).click();
    }

    private boolean isDisplayed(String accessibilityId) {
        try {
            return ScreenScroller.scrollIntoView(driver, accessibilityId).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String textOf(String accessibilityId) {
        WebElement element = ScreenScroller.scrollIntoView(driver, accessibilityId);
        String text = element.getText();
        if (text == null || text.isBlank()) {
            List<WebElement> children = element.findElements(AppiumBy.className("android.widget.TextView"));
            text = children.isEmpty() ? "" : children.get(0).getText();
        }
        return text;
    }
}
