package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The Login screen of the React Native My Demo App, plus the menu steps needed to reach it. */
public class LoginPage {

    // Reaching the screen: open the side menu, then choose "Log In".
    private static final By OPEN_MENU = AppiumBy.accessibilityId("open menu");
    private static final By MENU_ITEM_LOG_IN = AppiumBy.accessibilityId("menu item log in");

    // The Login screen itself (accessibility ids / content-desc).
    private static final By LOGIN_SCREEN = AppiumBy.accessibilityId("login screen");
    private static final By USERNAME_INPUT = AppiumBy.accessibilityId("Username input field");
    private static final By PASSWORD_INPUT = AppiumBy.accessibilityId("Password input field");
    private static final By LOGIN_BUTTON = AppiumBy.accessibilityId("Login button");
    private static final By GENERIC_ERROR = AppiumBy.accessibilityId("generic-error-message");

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final WebDriverWait wait;

    public LoginPage(AndroidDriver driver) {
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /** From the Products screen: open the menu and tap "Log In". */
    public void navigateToLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(OPEN_MENU)).click();
        wait.until(ExpectedConditions.elementToBeClickable(MENU_ITEM_LOG_IN)).click();
    }

    /** Waits for the Login screen; true if it appeared, false if it did not within the timeout. */
    public boolean isLoaded() {
        return isVisible(LOGIN_SCREEN);
    }

    public void enterUsername(String username) {
        type(USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
    }

    /** True if the generic error appears within the timeout. */
    public boolean isGenericErrorDisplayed() {
        return isVisible(GENERIC_ERROR);
    }

    /** The text of the generic error shown under the form. */
    public String getGenericErrorMessage() {
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(GENERIC_ERROR));
        String text = error.getText();
        if (text == null || text.isBlank()) {
            // The accessibility id may sit on a container whose text lives in a child TextView.
            text = error.findElement(AppiumBy.className("android.widget.TextView")).getText();
        }
        return text;
    }

    private void type(By field, String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
        input.clear();
        input.sendKeys(value);
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
