package com.nbai.assignment.mobile.config;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mobile run settings. Each value is read from a system property first (-Dmobile.app.path=...),
 * then from an environment variable (MOBILE_APP_PATH), then falls back to a default where one makes sense.
 */
public final class MobileConfig {

    private MobileConfig() {
    }

    /** Where the tests run: "local" (default). Other providers can be added later. */
    public static String getTarget() {
        return value("mobile.target", "MOBILE_TARGET", "local");
    }

    /** Appium server address. Appium 3 serves on the root path by default. */
    public static String getAppiumUrl() {
        return value("mobile.appium.url", "MOBILE_APPIUM_URL", "http://127.0.0.1:4723");
    }

    public static String getAutomationName() {
        return value("mobile.automation.name", "MOBILE_AUTOMATION_NAME", "UiAutomator2");
    }

    /** Free-text label for the emulator/device; Appium uses the connected device either way. */
    public static String getDeviceName() {
        return value("mobile.device.name", "MOBILE_DEVICE_NAME", "Android Emulator");
    }

    /** Optional Android version to request, e.g. "14". Null means "use whatever is connected". */
    public static String getPlatformVersion() {
        return value("mobile.platform.version", "MOBILE_PLATFORM_VERSION", null);
    }

    /** Path to the Sauce Labs My Demo App APK. Required, and the file must exist. */
    public static String getAppPath() {
        String path = value("mobile.app.path", "MOBILE_APP_PATH", null);
        if (path == null) {
            throw new IllegalStateException(
                    "App path is not set. Use -Dmobile.app.path=/path/to/MyDemoApp.apk or the MOBILE_APP_PATH environment variable");
        }
        if (!Files.isRegularFile(Path.of(path))) {
            throw new IllegalStateException("App file not found: " + path);
        }
        return path;
    }

    private static String value(String property, String envVar, String defaultValue) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        String fromEnv = System.getenv(envVar);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return defaultValue;
    }
}
