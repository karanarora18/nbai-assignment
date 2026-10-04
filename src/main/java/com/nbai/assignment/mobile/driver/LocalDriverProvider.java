package com.nbai.assignment.mobile.driver;

import com.nbai.assignment.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Starts an Android session on a local emulator or device through an Appium server that is already running.
 * It does not start Appium or the emulator itself.
 */
final class LocalDriverProvider {

    private LocalDriverProvider() {
    }

    static AndroidDriver create() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setAutomationName(MobileConfig.getAutomationName())
                .setDeviceName(MobileConfig.getDeviceName())
                .setApp(MobileConfig.getAppPath())
                .setNoReset(false); // fresh app data for every session keeps tests independent

        String platformVersion = MobileConfig.getPlatformVersion();
        if (platformVersion != null) {
            options.setPlatformVersion(platformVersion);
        }
        return new AndroidDriver(appiumServerUrl(), options);
    }

    private static URL appiumServerUrl() {
        String url = MobileConfig.getAppiumUrl();
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalStateException("Invalid Appium server URL: " + url, e);
        }
    }
}
