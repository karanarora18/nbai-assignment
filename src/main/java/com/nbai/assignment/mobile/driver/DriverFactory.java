package com.nbai.assignment.mobile.driver;

import com.nbai.assignment.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;

/** Single entry point tests use to get a driver; the execution target decides how it is built. */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static AndroidDriver createDriver() {
        String target = MobileConfig.getTarget();
        if ("local".equalsIgnoreCase(target)) {
            return LocalDriverProvider.create();
        }
        throw new IllegalStateException("Unsupported mobile.target '" + target + "'. Only 'local' is implemented.");
    }
}
