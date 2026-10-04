package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The shipping address step of checkout. The test enters every value itself and never relies on defaults. */
public class CheckoutAddressPage {

    private static final String SCREEN = "checkout address screen";
    private static final String FULL_NAME = "Full Name* input field";
    private static final String ADDRESS_LINE_1 = "Address Line 1* input field";
    private static final String ADDRESS_LINE_2 = "Address Line 2 input field";
    private static final String CITY = "City* input field";
    private static final String STATE_REGION = "State/Region input field";
    private static final String ZIP_CODE = "Zip Code* input field";
    private static final String COUNTRY = "Country* input field";
    private static final String TO_PAYMENT = "To Payment button";

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public CheckoutAddressPage(AndroidDriver driver) {
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

    public void enterAddressLine1(String value) {
        ScreenScroller.typeInto(driver, ADDRESS_LINE_1, value);
    }

    public void enterAddressLine2(String value) {
        ScreenScroller.typeInto(driver, ADDRESS_LINE_2, value);
    }

    public void enterCity(String value) {
        ScreenScroller.typeInto(driver, CITY, value);
    }

    public void enterStateRegion(String value) {
        ScreenScroller.typeInto(driver, STATE_REGION, value);
    }

    public void enterZipCode(String value) {
        ScreenScroller.typeInto(driver, ZIP_CODE, value);
    }

    public void enterCountry(String value) {
        ScreenScroller.typeInto(driver, COUNTRY, value);
    }

    public String getFullName() {
        return ScreenScroller.valueOf(driver, FULL_NAME);
    }

    public String getAddressLine1() {
        return ScreenScroller.valueOf(driver, ADDRESS_LINE_1);
    }

    public String getAddressLine2() {
        return ScreenScroller.valueOf(driver, ADDRESS_LINE_2);
    }

    public String getCity() {
        return ScreenScroller.valueOf(driver, CITY);
    }

    public String getStateRegion() {
        return ScreenScroller.valueOf(driver, STATE_REGION);
    }

    public String getZipCode() {
        return ScreenScroller.valueOf(driver, ZIP_CODE);
    }

    public String getCountry() {
        return ScreenScroller.valueOf(driver, COUNTRY);
    }

    public void clickToPayment() {
        ScreenScroller.hideKeyboardIfShown(driver);
        ScreenScroller.scrollIntoView(driver, TO_PAYMENT).click();
    }
}
