package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The payment step of checkout. The app pre-fills the fields with default values. */
public class CheckoutPaymentPage {

    private static final String SCREEN = "checkout payment screen";
    private static final String FULL_NAME = "Full Name* input field";
    private static final String CARD_NUMBER = "Card Number* input field";
    private static final String EXPIRATION_DATE = "Expiration Date* input field";
    private static final String SECURITY_CODE = "Security Code* input field";
    private static final String BILLING_CHECKBOX = "checkbox for My billing address is the same as my shipping address.";
    private static final String REVIEW_ORDER = "Review Order button";

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public CheckoutPaymentPage(AndroidDriver driver) {
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

    public void enterFullName(String value) {
        ScreenScroller.typeInto(driver, FULL_NAME, value);
    }

    public void enterCardNumber(String value) {
        ScreenScroller.typeInto(driver, CARD_NUMBER, value);
    }

    public void enterExpirationDate(String value) {
        ScreenScroller.typeInto(driver, EXPIRATION_DATE, value);
    }

    public void enterSecurityCode(String value) {
        ScreenScroller.typeInto(driver, SECURITY_CODE, value);
    }

    public String getFullName() {
        return ScreenScroller.valueOf(driver, FULL_NAME);
    }

    public String getCardNumber() {
        return ScreenScroller.valueOf(driver, CARD_NUMBER);
    }

    public String getExpirationDate() {
        return ScreenScroller.valueOf(driver, EXPIRATION_DATE);
    }

    /** Length of the displayed security code; the field may mask its characters, so the test compares length only. */
    public int getSecurityCodeLength() {
        return ScreenScroller.valueOf(driver, SECURITY_CODE).length();
    }

    /**
     * Makes sure "billing address is the same as shipping" is selected, without ever toggling it blindly.
     * The state is read from the "checked" / "selected" attributes. Only an explicit "false" triggers one click;
     * if the control does not expose its state, it is left as the app set it (the app's default is selected),
     * because clicking an unknown state could just as easily switch it off.
     */
    public void ensureBillingSameAsShipping() {
        ScreenScroller.scrollIntoView(driver, BILLING_CHECKBOX);
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId(BILLING_CHECKBOX)));
        if (isExplicitlyUnselected(checkbox)) {
            checkbox.click();
        }
    }

    private static boolean isExplicitlyUnselected(WebElement checkbox) {
        for (String attribute : new String[] {"checked", "selected"}) {
            String value = checkbox.getAttribute(attribute);
            if ("true".equals(value)) {
                return false;
            }
            if ("false".equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void clickReviewOrder() {
        ScreenScroller.hideKeyboardIfShown(driver);
        ScreenScroller.scrollIntoView(driver, REVIEW_ORDER).click();
    }
}
