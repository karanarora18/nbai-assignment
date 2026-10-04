package com.nbai.assignment.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * UiAutomator2 only returns elements that are visible on screen, so controls below the fold must be scrolled to first.
 * Uses UiScrollable (a UiAutomator selector on the accessibility id), not coordinates.
 */
final class ScreenScroller {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private ScreenScroller() {
    }

    /** Returns the element with the given accessibility id, scrolling it into view if needed. */
    static WebElement scrollIntoView(AndroidDriver driver, String accessibilityId) {
        return new WebDriverWait(driver, TIMEOUT).until(d -> {
            var visible = d.findElements(AppiumBy.accessibilityId(accessibilityId));
            if (!visible.isEmpty()) {
                return visible.get(0);
            }
            return d.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))"
                            + ".scrollIntoView(new UiSelector().description(\"" + accessibilityId + "\"))"));
        });
    }

    /** Scrolls the field into view, replaces whatever it contains with the value, and waits until it shows it. */
    static void typeInto(AndroidDriver driver, String accessibilityId, String value) {
        WebElement field = scrollIntoView(driver, accessibilityId);
        field.click();
        field.clear();
        field.sendKeys(value);
    }

    /** The field's current text, scrolling it into view first. */
    static String valueOf(AndroidDriver driver, String accessibilityId) {
        String text = scrollIntoView(driver, accessibilityId).getText();
        return text == null ? "" : text;
    }

    /** Closes the on-screen keyboard if it is open, so it cannot cover the next control. */
    static void hideKeyboardIfShown(AndroidDriver driver) {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (WebDriverException e) {
            // Some React Native screens refuse to hide the keyboard; the next scrollIntoView still works.
        }
    }
}
